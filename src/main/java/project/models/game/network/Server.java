package project.models.game.network;

import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Server {
	private final ServerSocket socket;
	private final Queue<Pair<InetAddress, Request>> requests;
	private final Queue<ClientHandler> clients;
	private final Thread listening;
	private final Thread responding;

	public Server(int port) throws IOException {
		this.socket = new ServerSocket(port);
		this.requests = new ConcurrentLinkedQueue<>();
		this.clients = new ConcurrentLinkedQueue<>();
		this.listening = new Thread(this::listen);
		this.responding = new Thread(this::respond);
	}

	public void start() {
		clients.clear();
		requests.clear();
		listening.start();
		responding.start();
	}

	public void stop() throws InterruptedException, IOException {
		socket.close();
		listening.interrupt();
		responding.interrupt();
		listening.join();
		responding.join();
		for(ClientHandler client: clients)
			client.stop();
	}

	public InetAddress getAddress() {
		return socket.getInetAddress();
	}

	public int getPort() {
		return socket.getLocalPort();
	}

	private void listen() {
		while(!Thread.interrupted()) {
			try {
				var socket = this.socket.accept();
				if(clients.stream()
						  .anyMatch(
								  client -> client.socket.getInetAddress()
														 .equals(socket.getInetAddress())
						  )) {
					socket.close();
					continue;
				}

				var client = new ClientHandler(socket);
				var players = requestToResponse(Request.playersList());
				clients.parallelStream().forEach(c -> c.send(players));
				clients.add(client);
			} catch(IOException ignored) {}
			Thread.onSpinWait();
		}
	}

	private Response requestToResponse(Request request) {
		return switch(request.getType()) {
			case Word ->
					Response.word(((Request.WordRequest) request).getWord());
			case PlayersList -> {
				yield Response.playersList(List.of());
			}
			default -> throw new IllegalStateException(
					"Unexpected value: " + request.getType());
		};
	}

	private void respond() {
		while(!Thread.interrupted()) {
			Pair<InetAddress, Request> pair = requests.poll();
			if(pair == null) continue;
			var response = requestToResponse(pair.getValue());

			var stream = clients.parallelStream();
			if(response.getType() == Type.Word)
				stream = stream.filter(client -> !client.socket.getInetAddress()
															   .equals(pair.getKey()));
			else
				stream = stream.filter(client -> client.socket.getInetAddress()
															  .equals(pair.getKey()));
			stream
					.peek(client -> System.out.println(
							"\t\t" + client.socket.getInetAddress() + " " +
									pair.getKey()))
					.forEach(client -> client.send(response));
			System.out.println();
			Thread.onSpinWait();
		}
	}

	private class ClientHandler implements Runnable {
		private final Socket socket;
		private final ObjectInputStream input;
		private final ObjectOutputStream output;
		private final Thread thread;

		private ClientHandler(Socket socket) throws IOException {
			this.socket = socket;
			this.input = new ObjectInputStream(socket.getInputStream());
			this.output = new ObjectOutputStream(socket.getOutputStream());
			this.thread = new Thread(this);
			this.thread.start();
		}

		private void stop() throws InterruptedException, IOException {
			thread.interrupt();
			thread.join();
			input.close();
			output.close();
			socket.close();
		}

		@Override
		public void run() {
			while(!Thread.interrupted() && socket.isConnected()) {
				try {
					var request = (Request) input.readObject();
					var pair = new Pair<>(socket.getInetAddress(), request);
					requests.add(pair);
				} catch(IOException | ClassNotFoundException ignored) {}
				Thread.onSpinWait();
			}
		}

		private void send(Response response) {
			try {
				output.writeObject(response);
			} catch(IOException ignored) {}
		}
	}
}
