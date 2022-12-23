package project.controllers.game;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import project.models.game.GameModel;
import project.views.game.GameView;

public final class GameController implements EventHandler<KeyEvent>{
	private static final GameController instance = new GameController();
	private GameView view;
	private GameModel model;

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
		this.model = new GameModel(lives, 0,  nbWords);
		this.view=new GameView(model);
		this.model.addViewer(view);
	}

	public void reset() {
		this.model = null;
	}

	public boolean isRunning() {
		return model != null;
	}

	@Override
	public void handle(KeyEvent event){

	}
}
