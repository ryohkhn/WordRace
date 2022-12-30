package project.controllers.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import project.models.game.GameModel;
import project.models.menu.MenuModel;
import project.views.game.GameView;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public final class GameController implements EventHandler<KeyEvent> {
	private static final GameController instance = new GameController();
	private MenuModel.GameMode gameMode;
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

	public void startNormal(int nbWords) {
		this.gameMode = MenuModel.GameMode.Normal;
		this.model = GameModel.Builder.soloNormal(nbWords);
		this.view = new GameView(model);
		model.setGameView(view);
		this.model.addViewer(view);
	}

	public void startCompetitive(int nbWords, int lives) {
		this.gameMode = MenuModel.GameMode.Competitive;
		this.model = GameModel.Builder.soloCompetitive(nbWords, lives);
		this.view = new GameView(model);
		model.setGameView(view);
		this.model.addViewer(view);
	}

	public void reset() {
		this.model = null;
	}

	public boolean isRunning() {
		return model != null;
	}

	private void handleBackSpace() {
		if(model.removeLetterFromInputWord()) {
			model.getWords().previousLetter();
			view.update();
		}
	}

	/**
	 * Handle space input
	 */
	private void handleSpace() {
		model.validateCurrentWord();
		view.resetInputText();
		view.updateWords();
		view.update();
	}

	private void handle(char c) {
		model.addLetterToInputWord(c);
		int inputWord = model.getInputWord().length();
		int currentWord = model.getWords()
							   .getCurrentWord()
							   .content()
							   .length();
		if(inputWord > currentWord
				|| c != model.getWords().getCurrentLetter()) {
			model.getPlayer().decrementLife();
			if(!model.getPlayer().isAlive()){
				// TODO SHOW STATS
				return;
			}
		}
		else{
			model.getStats().incrementUsefulCharacters();
		}
		model.getWords().nextLetter();
		view.update();
	}


	@Override public void handle(KeyEvent event) {
		if(event.getEventType() == KEY_PRESSED) {
			model.getStats().incrementNumberOfPressedKeys();
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
