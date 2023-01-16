package project.controllers;

import javafx.stage.Stage;
import project.models.menu.MenuModel;
import project.views.menu.MenuView;

import java.io.IOException;
import java.net.InetAddress;

public class MenuController {
	/**
	 * MenuController static instance
	 *
	 * @see #getInstance()
	 */
	private static final MenuController instance = new MenuController();
	/**
	 * MenuView object reference
	 *
	 * @see #getView()
	 */
	private final MenuView view;
	/**
	 * MenuModel object reference
	 *
	 * @see #getModel()
	 */
	private final MenuModel model;

	/**
	 * Menu controller constructor,
	 * create the Menu model and the Menu view
	 */
	private MenuController() {
		model = new MenuModel();
		view = new MenuView(model);
		model.addViewer(view);
	}

	/**
	 * Get the static instance of the Menu controller
	 *
	 * @return the instance
	 */
	public static MenuController getInstance() {
		return instance;
	}

	/**
	 * Menu view getter
	 *
	 * @return menu view
	 */
	public MenuView getView() {
		return view;
	}

	/**
	 * Menu model getter
	 *
	 * @return menu model
	 */
	public MenuModel getModel() {
		return model;
	}

	/**
	 * Start a game depending on the game mode
	 *
	 * @throws Exception error on the menu configuration
	 */
	public void startGame() throws Exception {
		switch(model.getGameMode()) {
			case Normal -> {
				NetworkController.getInstance().stop();
				view.setVisible(false);
				GameController.getInstance().startNormal(
						model.getPlayerName(),
						model.getNbWord()
				);
				GameController.getInstance().getView().start(new Stage());
			}
			case Competitive -> {
				NetworkController.getInstance().stop();
				view.setVisible(false);
				GameController.getInstance().startCompetitive(
						model.getPlayerName(),
						model.getNbWord(),
						model.getLives()
				);
				GameController.getInstance().getView().start(new Stage());
			}
			case Host, Join -> {
				if(!NetworkController.getInstance().isRunning())
					throw new IllegalStateException(
							"NetworkController is not running");

				int nbPlayers = NetworkController.getInstance()
												 .getNumberOfPlayers();
				if(nbPlayers < 2)
					throw new IllegalStateException("Not enough players");

				MenuModel config = model;
				if(model.getGameMode() == MenuModel.GameMode.Join) {
					config = NetworkController.getInstance()
											  .getModel()
											  .getConfiguration()
											  .orElseThrow();
				}

				view.setVisible(false);
				GameController.getInstance()
							  .startMultiplayer(
									  model.getPlayerName(),
									  config.getNbWord(),
									  config.getLives(),
									  model.getGameMode()
							  );
				GameController.getInstance().getView().start(new Stage());
			}
		}
	}

	/**
	 * Starts the server
	 *
	 * @throws IOException           error in port
	 * @throws InterruptedException  error in port
	 * @throws NumberFormatException string to int conversion
	 */
	public void startServer()
	throws IOException, InterruptedException, NumberFormatException {
		int port = Integer.parseInt(model.getPort());
		NetworkController.getInstance().host(port);
	}

	/**
	 * Stops the server
	 */
	public void stopServer() {
		try {
			NetworkController.getInstance().stop();
		} catch(Exception ignored) {}
	}

	/**
	 * Join the server
	 *
	 * @throws IOException           error in host IP
	 * @throws InterruptedException  error in host IP
	 * @throws NumberFormatException string to int conversion
	 */
	public void joinServer()
	throws IOException, InterruptedException, NumberFormatException {
		int port = Integer.parseInt(model.getPort());
		InetAddress host = InetAddress.getByName(model.getHost());
		NetworkController.getInstance().join(host, port);
	}
}
