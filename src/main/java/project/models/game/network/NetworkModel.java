package project.models.game.network;

import project.models.Model;
import project.models.game.PlayerModel;
import project.models.game.words.Word;
import project.models.menu.MenuModel;
import project.views.View;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public sealed abstract class NetworkModel extends Model {
	public static NetworkModel host(int port) throws IOException {
		return new HostModel(port);
	}

	public static NetworkModel join(InetAddress address, int port)
	throws IOException {
		return new ClientModel(address, port);
	}

	public abstract void start();

	public abstract void stop() throws IOException, InterruptedException;

	public abstract void send(Word word) throws IOException;

	public abstract Word tryReceiveWord();

	public abstract List<PlayerModel> getPlayersList()
	throws IOException;

	public abstract InetAddress getServerAddress();

	public abstract MenuModel getConfiguration()
	throws IOException, InterruptedException;

	public abstract int getServerPort();

	public final int getNumberOfPlayers() {
		try {
			return getPlayersList().size();
		} catch(IOException e) {
			return -1;
		}
	}

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

		public Word tryReceiveWord() {
			var response = (Response.WordResponse) client.tryReceive(Type.Word);
			return response != null ? response.getWord() : null;
		}

		@Override public List<PlayerModel> getPlayersList()
		throws IOException {
			client.send(Request.playersList());
			try {
				Response response = client.receive(Type.PlayersList);
				if(response == null) throw new IOException("Timeout");
				return ((Response.PlayersListResponse) response).getPlayers();
			} catch(InterruptedException e) {
				throw new IOException(e);
			}
		}

		@Override public InetAddress getServerAddress() {
			return client.getServerAddress();
		}

		@Override public int getServerPort() {
			return client.getServerPort();
		}

		@Override public void gameStarted() {
			throw new UnsupportedOperationException();
		}

		@Override public MenuModel getConfiguration()
		throws IOException, InterruptedException {
			client.send(Request.configuration());
			Response r = client.receive(Type.Configuration);
			if(r == null) throw new IOException("Server did not respond");
			return ((Response.ConfigurationResponse) r).getConfiguration();
		}

		@Override public void update() {
			notifyViewers();
		}

		@Override public void setVisible(boolean visible) {}
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

		@Override public List<PlayerModel> getPlayersList()
		throws IOException {
			return client.getPlayersList();
		}

		@Override public InetAddress getServerAddress() {
			return client.getServerAddress();
		}

		@Override public int getServerPort() {
			return client.getServerPort();
		}

		@Override public void gameStarted() {
			server.sendAll(
					Request.gameStart(),
					c -> c.isNotAddress(client.getServerAddress())
			);
		}

		@Override public MenuModel getConfiguration()
		throws IOException, InterruptedException {
			return client.getConfiguration();
		}

		@Override public void update() {
			notifyViewers();
		}

		@Override public void setVisible(boolean visible) {}
	}
}
