package project.controllers.menu;

import javafx.stage.Stage;
import project.controllers.game.GameController;
import project.models.menu.MenuModel;
import project.views.menu.MenuView;

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

	public void startGame() throws Exception{
		switch(model.getGameMode()) {
			case Normal -> {
				view.setVisible(false);
				GameController.getInstance().startNormal(model.getNbWord());
				GameController.getInstance().getView().start(new Stage());
			}
			case Competitive -> {
				view.setVisible(false);
				GameController.getInstance().startCompetitive(model.getNbWord(),model.getLives());
				GameController.getInstance().getView().start(new Stage());
			}
			case Host -> {
				view.setVisible(false);
				// TODO: Start host game
			}
			case Join -> {
				view.setVisible(false);
				// TODO: Start join game
			}
		}

	}

	public void startServer() {
		// TODO: Start server
	}

	public void stopServer() {
		// TODO: Stop server
	}

	public void joinServer() {
		// TODO: Join server
	}
}
