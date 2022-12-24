package project.controllers.game;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import project.models.game.GameModel;
import project.views.game.GameView;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public final class GameController implements EventHandler<KeyEvent> {
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
		this.model = new GameModel(lives, nbWords);
		this.view = new GameView(model);
		this.model.addViewer(view);
	}

	public void reset() {
		this.model = null;
	}

	public boolean isRunning() {
		return model != null;
	}

	private void handleBackSpace() {
		model.getStats().incrementNumberOfPressedKeys();
		if(model.removeLetterFromInputWord()) {
			model.getWords().previousLetter();
			view.update();
		}
	}

	private void handleSpace() {
		model.getStats().incrementNumberOfPressedKeys();
		if(model.isCurrentWordFinished()) {
			model.getPlayer().incrementLevel();
			model.getPlayer().incrementCorrectWord();
			model.getPlayer().addScore(model.getWords().getCurrentWord().length());

			model.getWords().pop();
			model.getWords().push();
			model.getWords().resetCurrentLetter();
			model.resetInputWord();

			view.updateWords();
			view.update();
		} else handle(' ');
	}

	private void handle(char c) {
		model.getStats().incrementNumberOfPressedKeys();
		model.addLetterToInputWord(c);
		if(model.getInputWord().length() > model.getWords().getCurrentWord().length() ||
				c != model.getWords().getCurrentLetter())
			model.getPlayer().decrementLife();
		else
			model.getStats().incrementUsefulCharacters();
		model.getWords().nextLetter();
		view.update();
	}


	@Override public void handle(KeyEvent event) {
		if(event.getEventType() == KEY_PRESSED) {
			switch(event.getCode()) {
				case BACK_SPACE, DELETE -> handleBackSpace();
				case SPACE, ENTER, TAB -> handleSpace();
				default -> {
					if(event.getText().length() == 1)
						handle(event.getText().charAt(0));
				}
			}
		}
	}
}
