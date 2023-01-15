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
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A server that can host a game
 */
public final class Server {
	private final ServerSocket socket;
	private final Map<InetAddress, Queue<Request>> requests;
	private final Map<InetAddress, Queue<Response>> responses;
	private final Queue<ClientHandler> clients;
	private final Map<Type, Handler> handlers;
	/**
	 * A thread who listen for new clients
	 */
	private final Thread listening;
	/**
	 * A thread who responds to the requests
	 */
	private final Thread responding;

	public Server(int port) throws IOException {
		this.handlers = new ConcurrentHashMap<>();
		this.handlers.put(Type.Word, Handler.wordRequest());
		this.handlers.put(Type.PlayersList, Handler.playersListRequest(this));
		this.handlers.put(Type.PlayerModel, Handler.playerModelRequest());
		this.handlers.put(Type.Configuration, Handler.configurationRequest());

		this.socket = new ServerSocket(port);
		this.requests = new ConcurrentHashMap<>();
		this.responses = new ConcurrentHashMap<>();
		this.clients = new ConcurrentLinkedQueue<>();
		this.listening = new Thread(this::listener);
		this.responding = new Thread(this::responder);
	}

	/**
	 * Start listening for clients and responding to requests
	 */
	public void start() {
		clients.clear();
		requests.clear();
		responses.clear();
		listening.start();
		responding.start();
	}

	/**
	 * Stop listening for clients and responding to requests
	 *
	 * @throws InterruptedException if the thread is interrupted
	 * @throws IOException          if an I/O error occurs
	 */
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

	/**
	 * Get the address of the server
	 *
	 * @return the address of the server
	 */
	public InetAddress getAddress() {
		return socket.getInetAddress();
	}

	/**
	 * Get the port of the server
	 *
	 * @return the port of the server
	 */
	public int getPort() {
		return socket.getLocalPort();
	}

	/**
	 * Send a request to all clients who are not filtered out by the predicate
	 *
	 * @param request the request to send
	 * @param filter  the predicate to filter out clients
	 */
	public void sendAll(Request request, Predicate<ClientHandler> filter) {
		clients.parallelStream()
			   .filter(filter)
			   .forEach(c -> c.send(request));
	}

	/**
	 * Send a request to all clients
	 *
	 * @param request the request to send
	 */
	public void sendAll(Request request) {
		sendAll(request, c -> true);
	}

	/**
	 * Wait at most 1 second for a response from all clients who are not
	 * filtered out by the predicate
	 *
	 * @param type   the type of the response
	 * @param filter the predicate to filter out clients
	 * @return a stream of responses
	 */
	public Stream<Response> receiveAll(
			Type type,
			Predicate<Queue<Response>> filter
	) {
		return responses.values()
						.parallelStream()
						.filter(filter)
						.map(q -> {
							long end = System.currentTimeMillis() + 1000;
							Response res = null;
							while((res == null || res.getType() != type) &&
									System.currentTimeMillis() < end) {
								if(res != null) q.add(res);
								res = q.poll();
								Thread.onSpinWait();
							}
							return res;
						}).filter(Objects::nonNull);
	}

	/**
	 * Wait at most 1 second for a response from all clients
	 *
	 * @param type the type of the response
	 * @return a stream of responses
	 */
	public Stream<Response> receiveAll(Type type) {
		return receiveAll(type, q -> true);
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
			} catch(IOException ignored) {}
			Thread.onSpinWait();
		}
	}

	/**
	 * Respond to all requests of the client in the queue
	 *
	 * @param entry the entry of the client
	 */
	private void handleRequestQueue(Map.Entry<InetAddress, Queue<Request>> entry) {
		Request request;
		while((request = entry.getValue().poll()) != null) {
			var completable = handlers.getOrDefault(
					request.getType(),
					Handler.empty()
			).handle(request);
			completable.thenAcceptAsync(
					response -> {
						if(response == null) return;
						if(response.getType() == Type.Word)
							clients.parallelStream()
								   .filter(c -> c.isNotAddress(entry.getKey()))
								   .forEach(c -> c.send(response));
						else
							clients.parallelStream()
								   .filter(c -> c.isAddress(entry.getKey()))
								   .forEach(c -> c.send(response));
					}
			);
		}
	}

	private void responder() {
		while(!Thread.interrupted()) {
			requests.entrySet()
					.parallelStream()
					.filter(e -> !e.getValue().isEmpty())
					.forEach(this::handleRequestQueue);
			Thread.onSpinWait();
		}
	}

	/**
	 * A handler for a client connection, which listens for requests and
	 * responses
	 */
	public class ClientHandler {
		private final Socket socket;
		private final ObjectInputStream input;
		private final ObjectOutputStream output;
		/**
		 * The thread that listens for requests and responses
		 */
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
						requests.get(socket.getInetAddress())
								.add(request);
					else if(obj instanceof Response response)
						responses.get(socket.getInetAddress())
								 .add(response);
				} catch(IOException | ClassNotFoundException ignored) {}
				Thread.onSpinWait();
			}
		}

		/**
		 * Send a request to the client
		 *
		 * @param response the request to send
		 */
		private void send(Response response) {
			try {
				output.writeObject(response);
			} catch(IOException ignored) {}
		}

		/**
		 * Send a request to the client
		 *
		 * @param request the request to send
		 */
		private void send(Request request) {
			try {
				output.writeObject(request);
				output.flush();
			} catch(IOException ignored) {}
		}

		/**
		 * Return if the address is equal to the address of this client
		 *
		 * @param address the address to compare
		 * @return if the address is equal to the address of this client
		 */
		public boolean isAddress(InetAddress address) {
			return socket.getInetAddress().equals(address);
		}

		/**
		 * Return if the address is not equal to the address of this client
		 *
		 * @param address the address to compare
		 * @return if the address is not equal to the address of this client
		 */
		public boolean isNotAddress(InetAddress address) {
			return !isAddress(address);
		}
	}
}
