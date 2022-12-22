package project.controllers.game;

import project.models.game.GameModel;
import project.views.game.GameView;

public final class GameController {
	private static final GameController instance = new GameController();
	private final GameView view;
	private GameModel model;

	private GameController() {
		this.view = null; // TODO: Create the view class
		this.model = null;
	}

	public static GameController getInstance() {
		return instance;
	}

	public GameView getView() {
		return view;
	}

	public GameModel getModel() {
		return model;
	}

	public void start(int lives, int nbWords) {
		this.model = new GameModel(lives, 0, nbWords);
		this.model.addViewer(view);
	}

	public void reset() {
		this.model = null;
	}

	public boolean isRunning() {
		return model != null;
	}
}
