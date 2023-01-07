package project.models.game.network;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import project.controllers.MenuController;
import project.controllers.NetworkController;
import project.models.game.words.Word;
import project.models.menu.MenuModel;

import java.io.IOException;

public class NetworkTest {
	@BeforeAll public static void setup()
	throws IOException, InterruptedException {
		NetworkController.getInstance().host(3333);
	}

	@AfterAll public static void teardown()
	throws IOException, InterruptedException {
		NetworkController.getInstance().stop();
	}

	@Test public void host() {
		if(!NetworkController.getInstance().isRunning())
			throw new AssertionError("Network is not running");
	}

	@Test public void send() throws IOException {
		NetworkController.getInstance()
						 .getModel()
						 .send(Word.normal("test"));
	}

	@Test public void tryReceiveWord() {
		if(NetworkController.getInstance()
							.getModel()
							.tryReceiveWord() != null)
			throw new AssertionError("Received word");
	}

	@Test public void getPlayersList() throws IOException {
		if(NetworkController.getInstance()
							.getModel()
							.getPlayersList()
							.size() != 1)
			throw new AssertionError("Received players list");
	}

	@Test public void getServerAddress() {
		if(NetworkController.getInstance()
							.getModel()
							.getInetAddress() == null)
			throw new AssertionError("Received server address");
	}

	@Test public void getConfiguration()
	throws IOException, InterruptedException {
		MenuModel config = NetworkController.getInstance()
											.getModel()
											.getConfiguration();

		if(!MenuController.getInstance().getModel().equals(config))
			throw new AssertionError("Received configuration");
	}

	@Test public void getServerPort() {
		if(NetworkController.getInstance()
							.getModel()
							.getPort() != 3333)
			throw new AssertionError("Received server port");
	}

	@Test public void getNumberOfPlayers() {
		if(NetworkController.getInstance()
							.getModel()
							.getNumberOfPlayers() != 1)
			throw new AssertionError("Received number of players");
	}
}