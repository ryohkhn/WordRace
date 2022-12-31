package project.controllers.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import project.models.game.GameModel;
import project.models.menu.MenuModel;
import project.views.game.GameView;
import project.views.game.StatsView;

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

	private void verifyGameEnd(){
		switch(gameMode){
			case Normal -> {
				if(model.getPlayer().getNbCorrectWords()==model.getWords().getSize()){
					restartGame();
				}
			}
			case Competitive -> {
				if(!model.getPlayer().isAlive()){
					restartGame();
				}
			}
		}
	}

	private void restartGame(){
		model.getStats().end();
		// TODO HIDE FOR NOW, TO CHANGE
		view.setVisible(false);
		StatsView statsView = new StatsView(new Stage(),model,model.getStats());
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
		verifyGameEnd();
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
		}
		else{
			model.getStats().incrementUsefulCharacters();
		}
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
						model.getStats().incrementNumberOfPressedKeys();
						handle(event.getText().charAt(0));
				}
			}
		}
	}
}
