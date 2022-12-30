package project.models.game;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import project.controllers.game.NetworkController;
import project.models.Model;
import project.views.game.GameView;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GameModel extends Model {
	private final WordList words;
	private final Stats stats;
	private final int nbWords;
	private final PlayerModel player;
	private final BiConsumer<GameModel, Word> wordValidation;
	private String inputWord;
	private GameView gameView;

	private GameModel(
			int initNbWords,
			int maximumNbWords,
			PlayerModel player,
			Supplier<Word> wordGenerator,
			BiConsumer<GameModel, Word> wordValidation,
			Function<GameModel, Runnable> timerRunnable
	) {
		this.nbWords = maximumNbWords;
		this.player = player;
		this.wordValidation = wordValidation;
		this.words = new WordList(initNbWords, wordGenerator);
		this.stats = new Stats();
		this.inputWord = "";

		if(timerRunnable != null) {
			timerRunnable.apply(this).run();
		}
	}

	/**
	 * Get an iterator over the words
	 *
	 * @return the iterator
	 */
	public Iterator<Word> getWordsIterator() {
		return words.iterator();
	}

	public int getNbWords() {
		return nbWords;
	}

	public WordList getWords() {
		return words;
	}

	public PlayerModel getPlayer() {
		return player;
	}

	public Stats getStats() {
		return stats;
	}

	public boolean isCurrentWordFinished() {
		return inputWord.equals(words.getCurrentWord().content());
	}

	/**
	 * Common tests modes and specific word validation between mods
	 */
	public void validateCurrentWord() {
		if(isCurrentWordFinished()){
			player.incrementCorrectWord();
		}
		wordValidation.accept(this, words.getCurrentWord());
		words.pop();
		words.resetCurrentLetter();
		resetInputWord();
	}

	public String getInputWord() {
		return inputWord;
	}

	public void addLetterToInputWord(char c) {
		inputWord += c;
	}

	public boolean removeLetterFromInputWord() {
		if(inputWord.length() > 0) {
			inputWord = inputWord.substring(0, inputWord.length() - 1);
			return true;
		}
		return false;
	}

	public void resetInputWord() {
		inputWord = "";
	}

	private void timerExec() {
		// If the list is full, we pop and check if word was well written
		if(nbWords == words.getSize()) {
			if(isCurrentWordFinished()) {
				if(player.getNbCorrectWordsLevel() == 100) {
					player.incrementLevel();
					player.resetCorrectWordsLevel();
				}
				player.incrementCorrectWord();
				player.addScore(getWords()
						.getCurrentWord()
						.content()
						.length());
			}
			words.pop();
			words.resetCurrentLetter();
			gameView.resetInputText();
			resetInputWord();
		}
		words.push();
		gameView.updateWords();
		gameView.update();
	}

	public void setGameView(GameView view){
		this.gameView=view;
	}

	public static final class Builder {
		private int initNbWords, initNbLives, maximumNbWords;
		private Supplier<Word> wordGenerator;
		private BiConsumer<GameModel, Word> wordValidator;
		private Function<GameModel, Runnable> timer;

		public Builder() {
			initNbWords = initNbLives = maximumNbWords = 0;
			wordGenerator = null;
			wordValidator = null;
			timer = null;
		}

		public static GameModel soloNormal(int initNbWords) {
			return new Builder()
					.setInitNbWords(initNbWords)
					.setMaximumNbWords(initNbWords)
					.setWordGenerator(() -> Word.generateWord(1, 0, 0))
					.setWordValidator((game,word) -> game.getWords().push())
					.build();
		}

		public static GameModel soloCompetitive(int maximumNbWords, int lives) {
			return new Builder()
					.setInitNbWords(1)
					.setMaximumNbWords(maximumNbWords)
					.setInitNbLives(lives)
					.setWordGenerator(() -> Word.generateWord(0.8, 0, 0.2))
					.setWordValidator((game, word) -> {
						if(game.isCurrentWordFinished()){
							if(word.isBonus()) game.getPlayer().incrementLife();
							if(game.player.getNbCorrectWordsLevel()==100){
								game.player.incrementLevel();
								game.player.resetCorrectWordsLevel();
							}
							game.player.addScore(word.length());
						}
						if(game.words.getSize()<=game.getNbWords()/2){
							game.words.push();
						}
					})
					.setTimer(GameModel::timerExec)
					.build();
		}

		public static GameModel multiplayer(int nbWords, int lives) {
			return new Builder()
					.setInitNbWords(nbWords)
					.setInitNbLives(lives)
					.setWordGenerator(Word::generateWord)
					.setWordValidator((game, word) -> {
						if(word.isBonus()) game.getPlayer().incrementLife();
						else if(word.isMalus()) {
							try {
								NetworkController.getInstance()
												 .getModel()
												 .send(word);
							} catch(IOException e) {
								throw new RuntimeException(e);
							}
						}
					})
					.build();
		}

		public Builder setInitNbWords(int initNbWords) {
			this.initNbWords = initNbWords;
			return this;
		}

		public Builder setMaximumNbWords(int maximumNbWords) {
			this.maximumNbWords = maximumNbWords;
			return this;
		}

		public Builder setInitNbLives(int initNbLives) {
			this.initNbLives = initNbLives;
			return this;
		}

		public Builder setWordGenerator(Supplier<Word> wordGenerator) {
			this.wordGenerator = wordGenerator;
			return this;
		}

		public Builder setWordValidator(BiConsumer<GameModel, Word> wordValidator) {
			this.wordValidator = wordValidator;
			return this;
		}

		public Builder setTimer(Consumer<GameModel> timer) {
			this.timer = game -> () -> {
				long delay = (long) (3 * (Math.pow(0.9, game.player.getLevel())));
				Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(delay),
						event -> timer.accept(game)));
				timeline.setCycleCount(Animation.INDEFINITE);
				timeline.play();
			};
			return this;
		}

		public GameModel build() {
			var playerModel=initNbLives!=0?
					PlayerModel.withLivesAndLevel(initNbLives)
					:PlayerModel.withoutLivesAndLevel();
			return new GameModel(
					initNbWords,
					maximumNbWords,
					playerModel,
					wordGenerator,
					wordValidator,
					timer
			);
		}
	}
}
