package project.controllers.menu;

import javafx.stage.Stage;
import project.controllers.game.GameController;
import project.controllers.game.NetworkController;
import project.models.menu.MenuModel;
import project.views.menu.MenuView;

import java.io.IOException;
import java.net.InetAddress;

public class MenuController {
	private static final MenuController instance = new MenuController();
	private final MenuView view;
	private final MenuModel model;

	private MenuController() {
		model = new MenuModel();
		view = new MenuView(model);
	}

	public static MenuController getInstance() {
		return instance;
	}

	public MenuView getView() {
		return view;
	}

	public MenuModel getModel() {
		return model;
	}

	public void startGame() throws Exception {
		switch(model.getGameMode()) {
			case Normal -> {
            	NetworkController.getInstance().stop();
				view.setVisible(false);
				GameController.getInstance().startNormal(model.getNbWord());
				GameController.getInstance().getView().start(new Stage());
			}
			case Competitive -> {
				NetworkController.getInstance().stop();
				view.setVisible(false);
				GameController.getInstance().startCompetitive(model.getNbWord(),model.getLives());
				GameController.getInstance().getView().start(new Stage());
			}
			case Host, Join -> {
				if(!NetworkController.getInstance().isRunning())
					throw new IllegalStateException(
							"NetworkController is not running");

				int nbPlayers = NetworkController.getInstance()
												 .getNumberOfPlayers();
				if(nbPlayers < 2) throw new IllegalStateException("Not enough players");

				throw new UnsupportedOperationException("Not implemented");
			}
		}
	}

	public void startServer()
	throws IOException, InterruptedException, NumberFormatException {
		int port = Integer.parseInt(model.getPort());
		NetworkController.getInstance().host(port);
	}

	public void stopServer() {
		try {
			NetworkController.getInstance().stop();
		} catch(Exception ignored) {}
	}

	public void joinServer()
	throws IOException, InterruptedException, NumberFormatException {
		int port = Integer.parseInt(model.getPort());
		InetAddress host = InetAddress.getByName(model.getHost());
		NetworkController.getInstance().join(host, port);
	}
}
