package project.models.game.network;

import project.models.Model;
import project.models.game.PlayerModel;
import project.models.game.Word;

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

	public abstract Word tryReceive();

	public abstract List<PlayerModel> getPlayersList()
	throws IOException;

	public abstract InetAddress getServerAddress();

	public abstract int getServerPort();

	private static final class ClientModel extends NetworkModel {
		private final Client client;

		public ClientModel(InetAddress address, int port) throws IOException {
			this.client = new Client(address, port);
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

		public Word tryReceive() {
			var response = (Response.WordResponse) client.nextResponse(Type.Word);
			return response != null ? response.getWord() : null;
		}

		@Override public List<PlayerModel> getPlayersList()
		throws IOException {
			client.send(Request.playersList());
			Response.PlayersListResponse response;
			do {
				response = (Response.PlayersListResponse) client.nextResponse(Type.PlayersList);
			} while(response == null);
			return response.getPlayers();
		}

		@Override public InetAddress getServerAddress() {
			return client.getServerAddress();
		}

		@Override public int getServerPort() {
			return client.getServerPort();
		}
	}

	private static final class HostModel extends NetworkModel {
		private final Server server;
		private final ClientModel client;

		private HostModel(int port) throws IOException {
			this.server = new Server(port);
			this.client = new ClientModel(InetAddress.getLocalHost(), port);
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

		@Override public Word tryReceive() {
			return client.tryReceive();
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
	}
}
