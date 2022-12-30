package project.models.game.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Server {
	private final ServerSocket socket;
	private final Map<InetAddress, Queue<Request>> requests;
	private final Map<InetAddress, Queue<Response>> responses;
	private final Queue<ClientHandler> clients;
	private final Thread listening;
	private final Thread responding;
	private Response cachedPlayersListResponse;

	public Server(int port) throws IOException {
		this.socket = new ServerSocket(port);
		this.requests = new ConcurrentHashMap<>();
		this.responses = new ConcurrentHashMap<>();
		this.clients = new ConcurrentLinkedQueue<>();
		this.listening = new Thread(this::listener);
		this.responding = new Thread(this::responder);
	}

	public void start() {
		clients.clear();
		requests.clear();
		responses.clear();
		if(!listening.isAlive()) listening.start();
		if(!responding.isAlive()) responding.start();
	}

	public void stop() throws InterruptedException, IOException {
		for(ClientHandler client: clients)
			client.stop();
		socket.close();
		listening.interrupt();
		responding.interrupt();
		listening.join();
		responding.join();
		clients.clear();
		requests.clear();
		responses.clear();
	}

	public InetAddress getAddress() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getLocalPort();
	}

	private void listener() {
		while(!Thread.interrupted()) {
			try {
				var socket = this.socket.accept();
				if(requests.containsKey(socket.getInetAddress())) {
					socket.close();
					continue;
				}

				requests.put(
						socket.getInetAddress(),
						new ConcurrentLinkedQueue<>()
				);
				responses.put(
						socket.getInetAddress(),
						new ConcurrentLinkedQueue<>()
				);

				var client = new ClientHandler(socket);
				clients.add(client);

				// notify all other clients that a new client has joined
				requests.values()
						.parallelStream()
						.forEach(q -> q.add(Request.playersList()));
			} catch(IOException ignored) {}
			Thread.onSpinWait();
		}
	}

	private void updateCachedPlayersListResponse() {
		clients.parallelStream().forEach(c -> c.send(Request.playerModel()));
		var players = responses.values()
							   .parallelStream()
							   .map(q -> {
								   Response response = null;
								   while(response == null ||
										   response.getType() !=
												   Type.PlayerModel) {
									   if(response != null) q.add(response);
									   response = q.poll();
									   Thread.onSpinWait();
								   }
								   return ((Response.PlayerModelResponse) response).getPlayer();
							   })
							   .filter(Objects::nonNull)
							   .toList();
		cachedPlayersListResponse = Response.playersList(players);
	}

	private Response requestToResponse(Request request) {
		return switch(request.getType()) {
			case Word ->
					Response.word(((Request.WordRequest) request).getWord());
			case PlayersList -> {
				if(cachedPlayersListResponse == null)
					updateCachedPlayersListResponse();
				else if(cachedPlayersListResponse.getCreated() + 10000 <
						System.currentTimeMillis()) // 10 seconds
					updateCachedPlayersListResponse();
				yield cachedPlayersListResponse;
			}
			default -> null;
		};
	}

	private void handleRequest(Map.Entry<InetAddress, Queue<Request>> entry) {
		Request request;
		while((request = entry.getValue().poll()) != null) {
			var response = requestToResponse(request);
			if(response == null) continue;
			var stream = clients.parallelStream();

			if(request.getType() == Type.Word)
				stream = stream.filter(c -> c.isNotAddress(entry.getKey()));
			else if(request.getType() == Type.PlayersList)
				stream = stream.filter(c -> c.isAddress(entry.getKey()));

			stream.forEach(c -> c.send(response));
		}
	}

	private void responder() {
		while(!Thread.interrupted()) {
			requests.entrySet()
					.parallelStream()
					.forEach(this::handleRequest);
			Thread.onSpinWait();
		}
	}

	private class ClientHandler {
		private final Socket socket;
		private final ObjectInputStream input;
		private final ObjectOutputStream output;
		private final Thread thread;

		private ClientHandler(Socket socket) throws IOException {
			this.socket = socket;
			this.input = new ObjectInputStream(socket.getInputStream());
			this.output = new ObjectOutputStream(socket.getOutputStream());
			this.thread = new Thread(this::listener);
			this.thread.start();
		}

		private void stop() throws InterruptedException, IOException {
			input.close();
			output.close();
			socket.close();
			thread.interrupt();
			thread.join();
		}

		public void listener() {
			while(!Thread.interrupted() && socket.isConnected()) {
				try {
					Object obj = input.readObject();
					if(obj instanceof Request request)
						requests.get(socket.getInetAddress()).add(request);
					else if(obj instanceof Response response)
						responses.get(socket.getInetAddress()).add(response);
				} catch(IOException | ClassNotFoundException ignored) {}
				Thread.onSpinWait();
			}
		}

		private void send(Response response) {
			try {
				output.writeObject(response);
			} catch(IOException ignored) {}
		}

		private void send(Request request) {
			try {
				output.writeObject(request);
			} catch(IOException ignored) {}
		}

		public boolean isAddress(InetAddress address) {
			return socket.getInetAddress().equals(address);
		}

		public boolean isNotAddress(InetAddress address) {
			return !isAddress(address);
		}
	}
}
