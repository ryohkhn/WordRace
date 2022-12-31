package project.models.game.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Client {
	private final Socket socket;
	private final Map<Type, Queue<Response>> responses;
	private final Map<Type, Handler> handlers;
	private final ObjectOutputStream output;
	private final ObjectInputStream input;
	private final Thread listening;

	public Client(InetAddress address, int port) throws IOException {
		this.handlers = new ConcurrentHashMap<>();
		this.handlers.put(Type.PlayerModel, Handler.playerModelRequest());

		this.socket = new Socket(address, port);
		if(this.socket.isClosed()) throw new IOException("Socket is closed");
		this.responses = new ConcurrentHashMap<>();
		for(Type type: Type.values())
			responses.put(type, new ConcurrentLinkedQueue<>());
		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		this.listening = new Thread(this::listen);
	}

	public void start() {
		if(!listening.isAlive()) listening.start();
	}

	public void stop() throws IOException, InterruptedException {
		socket.close();
		listening.interrupt();
		listening.join();
	}

	private void send(Object obj) throws IOException {
		output.writeObject(obj);
	}

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
					handleRequest(request);

			} catch(IOException | ClassCastException |
					ClassNotFoundException ignored) {}
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

	public Response nextResponse(Type type) {
		return responses.get(type).poll();
	}

	public InetAddress getServerAddress() {
		return socket.getInetAddress();
	}

	public int getServerPort() {
		return socket.getPort();
	}
}
