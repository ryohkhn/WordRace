package project.models.game.network;

import project.models.Model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The client that connects to the server
 */
public final class Client extends Model {
	private final Socket socket;
	private final Map<Type, Queue<Response>> responses;
	private final Queue<Request> requests;
	private final Map<Type, Handler> handlers;
	private final ObjectOutputStream output;
	private final ObjectInputStream input;
	private final Thread listening;
	private final Thread responding;

	public Client(InetAddress address, int port) throws IOException {
		this.handlers = new ConcurrentHashMap<>();
		this.handlers.put(Type.PlayerModel, Handler.playerModelRequest());
		this.handlers.put(Type.GameStart, Handler.gameStartRequest());

		this.responses = new ConcurrentHashMap<>();
		this.requests = new ConcurrentLinkedQueue<>();
		for(Type type: Type.values())
			responses.put(type, new ConcurrentLinkedQueue<>());

		this.socket = new Socket(address, port);
		if(this.socket.isClosed())
			throw new IOException("Socket is closed");
		else if(this.socket.isInputShutdown())
			throw new IOException("Input is shutdown");
		else if(this.socket.isOutputShutdown())
			throw new IOException("Output is shutdown");
		else if(!this.socket.isConnected())
			throw new IOException("Socket is not connected");
		else if(!this.socket.isBound())
			throw new IOException("Socket is not bound");
		else if(this.socket.isClosed())
			throw new IOException("Socket is closed");

		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		this.listening = new Thread(this::listen, "Client listening");
		this.responding = new Thread(this::respond, "Client responding");
	}

	/**
	 * Starts the listening and responding to the server
	 */
	public void start() {
		listening.start();
		responding.start();
	}

	/**
	 * Stops the listening and responding to the server
	 *
	 * @throws IOException          if an I/O error occurs when closing the socket
	 * @throws InterruptedException if the current thread is interrupted
	 */
	public void stop() throws IOException, InterruptedException {
		socket.close();
		listening.interrupt();
		responding.interrupt();
		listening.join();
		responding.join();
	}

	private void send(Object obj) throws IOException {
		output.writeObject(obj);
	}

	/**
	 * Send the request to the server
	 *
	 * @param request the request to send
	 * @throws IOException if an I/O error occurs when sending the request
	 */
	public void send(Request request) throws IOException {
		send((Object) request);
	}

	private void listen() {
		while(!Thread.interrupted()) {
			try {
				Object obj = input.readObject();
				if(obj instanceof Response response)
					responses.get(response.getType())
							 .add(response);
				else if(obj instanceof Request request)
					requests.add(request);
			} catch(IOException | ClassCastException |
					ClassNotFoundException ignored) {}
			Thread.onSpinWait();
		}
	}

	private void respond() {
		while(!Thread.interrupted()) {
			while(!requests.isEmpty()) {
				try {
					handleRequest(requests.poll());
				} catch(IOException ignored) {}
			}
			Thread.onSpinWait();
		}
	}

	private void handleRequest(Request request) throws IOException {
		Response response = handlers.getOrDefault(
				request.getType(),
				Handler.empty()
		).handle(request);
		if(response != null) send(response);
	}

	/**
	 * Try to get a response from the server of the given type, if there is no
	 * response of the given type, then return null
	 *
	 * @param type the type of the response
	 * @return the response of the given type, or null if there is no response
	 */
	public Response tryReceive(Type type) {
		return responses.get(type).poll();
	}

	/**
	 * Wait for a response from the server of the given type and return it
	 * when it is received or null if the timeout is reached before or if
	 * the thread is interrupted
	 *
	 * @param type    the type of the response
	 * @param timeout the maximum time to wait for a response
	 * @return the response of the given type, or null if the timeout is reached
	 * @throws InterruptedException if the current thread is interrupted
	 */
	public Response receive(Type type, long timeout)
	throws InterruptedException {
		long end = System.currentTimeMillis() + timeout;
		while(!Thread.interrupted() &&
				System.currentTimeMillis() < end) {
			Response response = tryReceive(type);
			if(response != null) return response;
			Thread.onSpinWait();
		}
		if(Thread.interrupted()) throw new InterruptedException();
		return null;
	}

	/**
	 * Wait at most 1 second for a response from the server of the given type
	 * and return it when it is received or null if the timeout is reached before
	 * or if the thread is interrupted
	 *
	 * @param type the type of the response
	 * @return the response of the given type, or null if the timeout is reached
	 * @throws InterruptedException if the current thread is interrupted
	 */
	public Response receive(Type type) throws InterruptedException {
		return receive(type, 1000);
	}

	/**
	 * Get the server's address
	 * @return the server's address
	 */
	public InetAddress getInetAddress() {
		return socket.getInetAddress();
	}

	/**
	 * Get the server's port
	 * @return the server's port
	 */
	public int getPort() {
		return socket.getPort();
	}
}
