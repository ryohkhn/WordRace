package project.models.game.network;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import project.controllers.MenuController;
import project.controllers.NetworkController;
import project.models.game.words.Word;
import project.models.menu.MenuModel;

import java.io.IOException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NetworkTest {
	private static final NetworkController network =
			NetworkController.getInstance();
	private static final NetworkModel model = network.getModel();

	@BeforeAll public static void setup() {
		try {
			network.host(3333);
		} catch(Throwable ignored) {}
	}

	@AfterAll public static void teardown()
	throws IOException, InterruptedException {
		network.stop();
	}

	@Test public void host() {
		if(!network.isRunning())
			throw new AssertionError("Network is not running");
	}

	@Test public void send() throws IOException {
		network
				.getModel()
				.send(Word.normal("test"));
	}

	@Test public void tryReceiveWord() {
		if(network
				.getModel()
				.tryReceiveWord() != null)
			throw new AssertionError("Received word");
	}

	@Test public void getPlayersList() throws IOException {
		if(network
				.getModel()
				.getPlayersList()
				.size() != 1)
			throw new AssertionError("Received players list");
	}

	@Test public void getServerAddress() {
		if(network
				.getModel()
				.getInetAddress() == null)
			throw new AssertionError("Received server address");
	}

	@Test public void getConfiguration()
	throws IOException, InterruptedException {
		MenuModel config = network
				.getModel()
				.getConfiguration();

		if(!MenuController.getInstance().getModel().equals(config))
			throw new AssertionError("Received configuration");
	}

	@Test public void getServerPort() {
		if(network
				.getModel()
				.getPort() != 3333)
			throw new AssertionError("Received server port");
	}

	@Test public void getNumberOfPlayers() {
		if(network
				.getModel()
				.getNumberOfPlayers() != 1)
			throw new AssertionError("Received number of players");
	}
}