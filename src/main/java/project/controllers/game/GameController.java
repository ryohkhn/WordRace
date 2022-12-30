package project.controllers.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
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
		this.model.addViewer(view);
	}

	public void startCompetitive(int nbWords, int lives) {
		this.gameMode = MenuModel.GameMode.Competitive;
		this.model = GameModel.Builder.soloCompetitive(nbWords, lives);
		this.view = new GameView(model);
		this.model.addViewer(view);
		launchTimer();
	}

	public void reset() {
		this.model = null;
	}

	public boolean isRunning() {
		return model != null;
	}

	private void launchTimer() {
		long delay = (long) (3 * (Math.pow(0.9, model.getPlayer().getLevel())));
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delay),
													  event -> pushTimer()));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();
	}

	public void pushTimer() {
		// If the list is full, we pop and check if word was well written
		if(model.getNbWords() == model.getWords().getSize()) {
			if(model.isCurrentWordFinished()) {
				if(model.getPlayer().getNbCorrectWordsLevel() == 100) {
					model.getPlayer().incrementLevel();
					model.getPlayer().resetCorrectWordsLevel();
				}
				model.getPlayer().incrementCorrectWord();
				model.getPlayer().addScore(model.getWords()
												.getCurrentWord()
												.content()
												.length());
			}
			model.getWords().pop();
			view.resetInputText();
			model.resetInputWord();
		}
		model.getWords().push();
		model.getWords().resetCurrentLetter();
		view.updateWords();
		view.update();
	}

	private void handleBackSpace() {
		if(model.removeLetterFromInputWord()) {
			model.getWords().previousLetter();
			view.update();
		}
	}

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
