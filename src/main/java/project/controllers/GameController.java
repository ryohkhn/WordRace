package project.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;
import project.models.game.GameModel;
import project.models.game.PlayerModel;
import project.models.menu.MenuModel;
import project.views.game.GameView;
import project.views.game.StatsView;

import java.io.IOException;

import static javafx.scene.input.KeyEvent.KEY_PRESSED;

public final class GameController implements EventHandler<KeyEvent> {
	/**
	 * GameController static instance
	 *
	 * @see #getInstance()
	 */
	private static final GameController instance = new GameController();
	private final Timeline gameStatusCheck;
	/**
	 * Game mode enum chosen by the player in the menu
	 */
	private MenuModel.GameMode gameMode;
	/**
	 * GameView object reference
	 *
	 * @see #getView()
	 */
	private GameView view;
	/**
	 * GameModel object reference
	 *
	 * @see #getModel()
	 */
	private GameModel model;

	private GameController() {
		gameStatusCheck = new Timeline(
				new KeyFrame(
						Duration.millis(500),
						event -> verifyGameEnd()
				)
		);
		gameStatusCheck.setCycleCount(Timeline.INDEFINITE);
	}

	/**
	 * Return the instance of the Game controller
	 *
	 * @return controller object
	 */
	public static GameController getInstance() {
		return instance;
	}

	/**
	 * Get the GameView object
	 *
	 * @return GameView object
	 */
	public GameView getView() {
		return view;
	}

	/**
	 * Get the GameModel object
	 *
	 * @return GameModel object
	 */
	public GameModel getModel() {
		return model;
	}

	/**
	 * Starts a normal game
	 *
	 * @param nbWords number of words to validate to end the game
	 */
	public void startNormal(String name, int nbWords) {
		this.gameMode = MenuModel.GameMode.Normal;
		this.model = GameModel.Builder.soloNormal(name, nbWords);
		this.view = new GameView(model);
		this.model.addViewer(this::updateView);
		gameStatusCheck.play();
	}

	/**
	 * Starts a competitive game
	 *
	 * @param nbWords the number of words shown
	 * @param lives   the initial lives value
	 */
	public void startCompetitive(String name, int nbWords, int lives) {
		this.gameMode = MenuModel.GameMode.Competitive;
		this.model = GameModel.Builder.soloCompetitive(name, nbWords, lives);
		this.view = new GameView(model);
		this.model.addViewer(this::updateView);
		gameStatusCheck.play();
	}

	public void startMultiplayer(
			String name,
			int nbWords,
			int nbLives,
			MenuModel.GameMode mode
	) {
		this.gameMode = mode;
		this.model = GameModel.Builder.multiplayer(name, nbWords, nbLives);
		this.view = new GameView(model);
		this.model.addViewer(this::updateView);

		if(mode == MenuModel.GameMode.Host)
			NetworkController.getInstance().gameStarted();
		gameStatusCheck.play();
	}

	/**
	 * Checks whether the game is running
	 *
	 * @return the state
	 */
	public boolean isRunning() {
		return model != null;
	}

	public void updateView() {
		if(view != null) {
			view.update();
			view.updateWords();
		}
	}

	/**
	 * Verify the game end depending on the mode
	 */
	private void verifyGameEnd() {
		if(model == null)
			return;
		switch(gameMode) {
			case Normal -> {
				if(model.getPlayer().getNbCorrectWords() ==
						model.getWords().getSize())
					showStats();
			}
			case Competitive -> {
				if(!model.getPlayer().isAlive())
					showStats();
			}
			case Host, Join -> {
				if(!model.getPlayer().isAlive()) showStats();
				try {
					// Check if there is less than 2 players alive
					long players = NetworkController.getInstance()
													.getModel()
													.getPlayersList()
													.orElseThrow()
													.parallelStream()
													.filter(PlayerModel::isAlive)
													.count();
					if(players < 2) showStats();
				} catch(IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * Show the stats screen and ends the current game
	 */
	private void showStats() {
		gameStatusCheck.stop();
		model.end();
		view.setVisible(false);
		StatsView statsView = new StatsView(
				new Stage(),
				model.getStats()
		);
		model.removeViewer(this.view);
	}

	/**
	 * Handle a backspace input
	 */
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

	/**
	 * Handle a letter the player pressed
	 *
	 * @param c the letter
	 */
	private void handle(char c) {
		if(model.getWords().getCurrentWord() == null)
			return;
		model.addLetterToInputWord(c);
		int inputWord = model.getInputWord().length();
		int currentWord = model.getWords()
							   .getCurrentWord()
							   .content()
							   .length();
		// decrement life on error
		if(inputWord > currentWord
				|| c != model.getWords().getCurrentLetter()) {
			model.getPlayer().decrementLife();
		} else {
			model.getStats().incrementUsefulCharacters();
		}
		model.getWords().nextLetter();
		view.update();
	}

	/**
	 * Handle a KeyEvent input from the player
	 *
	 * @param event event input
	 */
	@Override public void handle(KeyEvent event) {
		if(event.getEventType() == KEY_PRESSED) {
			switch(event.getCode()) {
				case BACK_SPACE, DELETE -> handleBackSpace();
				case SPACE, ENTER, TAB -> handleSpace();
				default -> {
					if(event.getText().length() == 1) {
						model.getStats().incrementNumberOfPressedKeys();
						handle(event.getText().charAt(0));
					}
				}
			}
		}
	}

	public PlayerModel getPlayer() {
		return model.getPlayer();
	}
}
