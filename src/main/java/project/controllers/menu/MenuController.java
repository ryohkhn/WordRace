package project.controllers.menu;

import javafx.event.Event;
import javafx.event.EventHandler;
import project.models.menu.MenuModel;
import project.views.menu.MenuView;

public class MenuController implements EventHandler<Event> {
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

	@Override public void handle(Event event) {
	}

	public MenuView getView() {
		return view;
	}

	public MenuModel getModel() {
		return model;
	}
}
