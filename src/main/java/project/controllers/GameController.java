package project.controllers;

import project.models.game.GameModel;
import project.views.View;

public final class GameController {
	private static GameController instance = new GameController();
	private final View view;
	private GameModel model;

	private GameController() {
		this.view = null; // TODO: Create the view class
		this.model = null;
	}

	public static GameController getInstance() {
		return instance;
	}

	public View getView() {
		return view;
	}

	public GameModel getModel() {
		return model;
	}

	public void start(int lives, int nbWords) {
		this.model = new GameModel(lives, 0, 0, nbWords);
		this.model.addViewer(view);
	}

	public void reset() {
		this.model = null;
	}

	public boolean isRunning() {
		return model != null;
	}
}
