package project.models.game.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class Client {
	private final Socket socket;
	private final Queue<Response> responses;
	private final ObjectOutputStream output;
	private final ObjectInputStream input;
	private final Thread listening;

	public Client(InetAddress address, int port) throws IOException {
		this.socket = new Socket(address, port);
		if(this.socket.isClosed())
			throw new IOException("Socket is closed");
		this.responses = new ConcurrentLinkedQueue<>();
		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		this.listening = new Thread(this::listen);
	}

	public void start() {
		listening.start();
	}

	public void stop() throws IOException, InterruptedException {
		socket.close();
		listening.interrupt();
		listening.join();
	}

	public void send(Request request) throws IOException {
		output.writeObject(request);
	}

	private void listen() {
		while(!Thread.interrupted()) {
			try {
				Response response = (Response) input.readObject();
				responses.add(response);
			} catch(IOException | ClassCastException |
					ClassNotFoundException ignored) {}
			Thread.onSpinWait();
		}
	}

	public boolean hasResponsePending() {
		return !responses.isEmpty();
	}

	public Response nextResponse() {
		return responses.poll();
	}
}
