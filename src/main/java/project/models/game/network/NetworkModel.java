package project.models.game.network;

import project.models.Model;
import project.models.game.PlayerModel;
import project.models.game.words.Word;
import project.models.menu.MenuModel;
import project.views.View;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Optional;

/**
 * Represents a network model for a game.
 * It can be a client or a server.
 */
public sealed abstract class NetworkModel extends Model {
	/**
	 * Create a new network model to host a game.
	 *
	 * @param port the port to host the game on
	 * @return the network model
	 * @throws IOException if an I/O error occurs
	 */
	public static NetworkModel host(int port) throws IOException {
		return new HostModel(port);
	}

	/**
	 * Create a new network model to join a game.
	 *
	 * @param address the address of the host
	 * @param port    the port of the host
	 * @return the network model
	 * @throws IOException if an I/O error occurs
	 */
	public static NetworkModel join(InetAddress address, int port)
	throws IOException {
		return new ClientModel(address, port);
	}

	/**
	 * Start the network model.
	 */
	public abstract void start();

	/**
	 * Stop the network model.
	 *
	 * @throws IOException          if an I/O error occurs
	 * @throws InterruptedException if the thread is interrupted
	 */
	public abstract void stop() throws IOException, InterruptedException;

	/**
	 * Send a word through the network.
	 *
	 * @param word the word to send
	 * @throws IOException if an I/O error occurs
	 */
	public abstract void send(Word word) throws IOException;

	/**
	 * Attempt to receive a word through this network model.
	 *
	 * @return the word received, or null if no word was received
	 */
	public abstract Word tryReceiveWord();

	/**
	 * Get the list of players connected to the same server
	 * through this network model.
	 *
	 * @return the list of players
	 * @throws IOException if an I/O error occurs
	 */
	public abstract Optional<List<PlayerModel>> getPlayersList()
	throws IOException;

	/**
	 * Get the address of the server this network model is connected to.
	 *
	 * @return the address of the server
	 */
	public abstract InetAddress getInetAddress();

	/**
	 * Get the configuration of the game of the server this network model is
	 * connected to.
	 *
	 * @return the configuration of the game
	 * @throws IOException          if an I/O error occurs
	 * @throws InterruptedException if the thread is interrupted
	 */
	public abstract Optional<MenuModel> getConfiguration()
	throws IOException, InterruptedException;

	/**
	 * Get the port of the server this network model is connected to.
	 *
	 * @return the port of the server
	 */
	public abstract int getPort();

	/**
	 * Get the number of players connected to the same server
	 *
	 * @return the number of players
	 */
	public final Optional<Integer> getNumberOfPlayers() {
		try {
			var players = getPlayersList();
			if(players.isPresent())
				return Optional.of(players.get().size());
		} catch(IOException ignored) {}
		return Optional.empty();
	}

	/**
	 * Indicate to all the other clients that the game has started.
	 * Only the host should call this method.
	 *
	 * @throws UnsupportedOperationException if this method is called by a client
	 */
	public abstract void gameStarted() throws UnsupportedOperationException;

	private static final class ClientModel extends NetworkModel implements View {
		private final Client client;

		public ClientModel(InetAddress address, int port) throws IOException {
			this.client = new Client(address, port);
			this.client.start();
			this.client.addViewer(this);
		}

		@Override public void start() {
			client.start();
		}

		@Override public void stop() throws IOException, InterruptedException {
			client.stop();
		}

		@Override public void send(Word word) throws IOException {
			client.send(Request.word(word));
		}

		@Override public Word tryReceiveWord() {
			var response = (Response.WordResponse) client.tryReceive(Type.Word);
			return response != null ? response.getWord() : null;
		}

		@Override public Optional<List<PlayerModel>> getPlayersList()
		throws IOException {
			client.send(Request.playersList());
			try {
				Response r = client.receive(Type.PlayersList);
				if(r instanceof Response.PlayersListResponse response)
					return Optional.of(response.getPlayers());
			} catch(InterruptedException ignored) {}
			return Optional.empty();
		}

		@Override public InetAddress getInetAddress() {
			return client.getInetAddress();
		}

		@Override public int getPort() {
			return client.getPort();
		}

		@Override public void gameStarted() {
			throw new UnsupportedOperationException();
		}

		@Override public Optional<MenuModel> getConfiguration()
		throws IOException, InterruptedException {
			client.send(Request.configuration());
			Response r = client.receive(Type.Configuration);
			if(r instanceof Response.ConfigurationResponse response)
				return Optional.of(response.getConfiguration());
			return Optional.empty();
		}

		@Override public void update() {
			notifyViewers();
		}
	}

	private static final class HostModel extends NetworkModel implements View {
		private final Server server;
		private final ClientModel client;

		private HostModel(int port) throws IOException {
			this.server = new Server(port);
			this.server.start();

			this.client = new ClientModel(server.getAddress(), port);
			this.client.addViewer(this);
		}

		@Override public void start() {
			server.start();
			client.start();
		}

		@Override public void stop() throws IOException, InterruptedException {
			server.stop();
			client.stop();
		}

		@Override public void send(Word word) throws IOException {
			client.send(word);
		}

		@Override public Word tryReceiveWord() {
			return client.tryReceiveWord();
		}

		@Override public Optional<List<PlayerModel>> getPlayersList()
		throws IOException {
			return client.getPlayersList();
		}

		@Override public InetAddress getInetAddress() {
			return client.getInetAddress();
		}

		@Override public int getPort() {
			return client.getPort();
		}

		@Override public void gameStarted() {
			server.sendAll(
					Request.gameStart(),
					c -> c.isNotAddress(client.getInetAddress())
			);
		}

		@Override public Optional<MenuModel> getConfiguration()
		throws IOException, InterruptedException {
			return client.getConfiguration();
		}

		@Override public void update() {
			notifyViewers();
		}
	}
}
