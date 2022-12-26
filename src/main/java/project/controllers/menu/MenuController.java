package project.controllers.menu;

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

	public void startGame() {
		switch(model.getGameMode()) {
			case Solo -> {
				// TODO: Start solo game
			}
			case Host -> {
				// TODO: Start host game
			}
			case Join -> {
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
}
