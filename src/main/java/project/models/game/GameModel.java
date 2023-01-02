package project.models.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import project.controllers.GameController;
import project.controllers.NetworkController;
import project.models.Model;
import project.models.game.words.Word;
import project.models.game.words.WordList;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GameModel extends Model {
	/**
	 * WordList object representing a list of words
	 *
	 * @see #getWords()
	 */
	private final WordList words;
	/**
	 * Stats object, statistics of the current game
	 *
	 * @see #getStats()
	 */
	private final Stats stats;
	/**
	 * Maximum number of words on the list
	 *
	 * @see #getNbWords()
	 */
	private final int nbWords;
	/**
	 * PlayerModel object, everything related to the player
	 *
	 * @see #getPlayer()
	 */
	private final PlayerModel player;
	/**
	 * BiConsumer<GameModel,Word>, specific logic on a word depending on the game mode
	 */
	private final BiConsumer<GameModel, Word> wordValidation;
	/**
	 * Current input word of the player
	 *
	 * @see #getInputWord()
	 */
	private String inputWord;

	/**
	 * Private Game model constructor, used by the builder
	 * @param initNbWords initiation number of words
	 * @param maximumNbWords maximum number of words
	 * @param player the type of player depending on the mode
	 * @param wordGenerator the type of word generator, probabilities variation
	 * @param wordValidation the word validator properties of the mode
	 * @param timerRunnable a timer if needed
	 */
	private GameModel(
			int initNbWords,
			int maximumNbWords,
			PlayerModel player,
			Supplier<Word> wordGenerator,
			BiConsumer<GameModel, Word> wordValidation,
			Function<GameModel, Timeline> timerRunnable
	) {
		this.nbWords = maximumNbWords;
		this.player = player;
		this.wordValidation = wordValidation;
		this.words = new WordList(initNbWords, wordGenerator);
		this.words.addViewer(() -> {
			GameController.getInstance().updateView();
			notifyViewers();
		});
		this.stats = new Stats();
		this.inputWord = "";

		if(timerRunnable != null)
			timerRunnable.apply(this).play();
	}

	/**
	 * Get an iterator over the words
	 *
	 * @return the iterator
	 */
	public Iterator<Word> getWordsIterator() {
		return words.iterator();
	}

	/**
	 * Get the number of maximum words
	 * @return
	 */
	public int getNbWords() {
		return nbWords;
	}

	/**
	 * Get the words list object
	 * @return WordList instance
	 */
	public WordList getWords() {
		return words;
	}

	/**
	 * Get the player model object
	 * @return PlayerModel instance
	 */
	public PlayerModel getPlayer() {
		return player;
	}

	/**
	 * Get the statistics object
	 * @return Stats instance
	 */
	public Stats getStats() {
		return stats;
	}

	/**
	 * Check if the input word is equal to the current word
	 * @return boolean
	 */
	public boolean isCurrentWordFinished() {
		return inputWord.equals(words.getCurrentWord().content());
	}

	/**
<<<<<<< HEAD
	 * If the current word is finished, it will execute the wordValidation
	 * function and the following actions:
	 * - Increment the number of correct words
	 * - Increment the score of the player
	 * - Reset the input word and current letter
=======
	 * Common tests and specific word validation depending on the mode
>>>>>>> 08e1130 (some javaDoc comments)
	 */
	public void validateCurrentWord() {
		if(isCurrentWordFinished()) {
			player.incrementCorrectWord();
			player.addScore(getWords()
									.getCurrentWord()
									.content()
									.length());
			wordValidation.accept(this, words.getCurrentWord());
			words.pop();
			words.resetCurrentLetter();
			resetInputWord();
		}
	}

	/**
	 * Get the input word of the player
	 * @return the word
	 */
	public String getInputWord() {
		return inputWord;
	}

	/**
	 * Add a letter to the input word
	 * @param c the letter
	 */
	public void addLetterToInputWord(char c) {
		inputWord += c;
	}

	/**
	 * Remove a letter from the input word
	 * @return succeed boolean
	 */
	public boolean removeLetterFromInputWord() {
		if(inputWord.length() > 0) {
			inputWord = inputWord.substring(0, inputWord.length() - 1);
			notifyViewers();
			return true;
		}
		return false;
	}

	/**
	 * Reset the input word
	 */
	public void resetInputWord() {
		inputWord = "";
		GameController.getInstance().getView().resetInputText();
	}

	/**
	 * A timer execution function, executes at a certain rate
	 * fixed by the timer in the builder
	 */
	private void timerCompetitiveMode(){
		words.push();
	}

	/**
	 * GameModel builder class
	 */
	public static final class Builder {
		private int initNbWords, initNbLives, maximumNbWords;
		private Supplier<Word> wordGenerator;
		private BiConsumer<GameModel, Word> wordValidator;
		private Function<GameModel, Timeline> timer;

		public Builder() {
			initNbWords = initNbLives = maximumNbWords = 0;
			wordGenerator = null;
			wordValidator = null;
			timer = null;
		}

		/**
		 * Creates a new solo normal instance of
		 * {@link GameModel}
		 * @param initNbWords the number of words to validate
		 * @return the new instance
		 */
		public static GameModel soloNormal(int initNbWords) {
			return new Builder()
					.setInitNbWords(initNbWords)
					.setMaximumNbWords(initNbWords)
					.setWordGenerator(() -> Word.generateWord(1, 0, 0))
					.setWordValidator((game, word) -> game.getWords().push())
					.build();
		}

		/**
		 * Creates a new competitive instance of {@link GameModel}
		 * @param maximumNbWords the maximum number of words in the queue
		 * @param lives the initial number of lives
		 * @return the new instance
		 */
		public static GameModel soloCompetitive(int maximumNbWords, int lives) {
			return new Builder()
					.setInitNbWords(1)
					.setMaximumNbWords(maximumNbWords)
					.setInitNbLives(lives)
					.setWordGenerator(() -> Word.generateWord(0.8, 0, 0.2))
					.setWordValidator((game, word) -> {
						if(word.isBonus())
							game.getPlayer().incrementLife();

						if(game.words.getSize() <= game.getNbWords() / 2)
							game.words.push();

						int level = game.player.getNbCorrectWords() / 100;
						while(game.player.getLevel() < level)
							game.player.incrementLevel();
					})
					.setTimer(
							GameModel::timerCompetitiveMode,
							game -> () -> (long) (3 * (Math.pow(
									0.9,
									game.player.getLevel()
							)))
					)
					.build();
		}

		/**
		 * Creates a new multiplayer instance of {@link GameModel}
		 * @param nbWords the number of words to validate
		 * @return the new instance
		 */
		public static GameModel multiplayer(int nbWords, int initNbLives) {
			return new Builder()
					.setInitNbWords(nbWords)
					.setInitNbLives(initNbLives)
					.setWordGenerator(Word::generateWord)
					.setWordValidator((game, word) -> {
						if(word.isMalus()) {
							try {
								NetworkController.getInstance()
												 .getModel()
												 .send(word);
							} catch(IOException e) {
								throw new RuntimeException(e);
							}
						}
					})
					.setTimer(
							game -> {
								Word word = NetworkController.getInstance()
															 .getModel()
															 .tryReceiveWord();
								while(word != null) {
									game.getWords()
										.push(Word.normal(word.content()));
									word = NetworkController.getInstance()
															.getModel()
															.tryReceiveWord();
								}
							},
							1
					)
					.build();
		}

		/**
		 * initNbWords setter
		 * @param initNbWords value to set
		 * @return Builder object with the value set
		 */
		public Builder setInitNbWords(int initNbWords) {
			this.initNbWords = initNbWords;
			return this;
		}

		/**
		 * maximumNbWords setter
		 * @param maximumNbWords value to set
		 * @return Builder object with the value set
		 */
		public Builder setMaximumNbWords(int maximumNbWords) {
			this.maximumNbWords = maximumNbWords;
			return this;
		}

		/**
		 * initNbLives setter
		 * @param initNbLives value to set
		 * @return Builder object with the value set
		 */
		public Builder setInitNbLives(int initNbLives) {
			this.initNbLives = initNbLives;
			return this;
		}

		/**
		 * wordGenerator setter
		 * @param wordGenerator Supplier to set
		 * @return Builder object with the value set
		 */
		public Builder setWordGenerator(Supplier<Word> wordGenerator) {
			this.wordGenerator = wordGenerator;
			return this;
		}

		/**
		 * wordValidator setter
		 * @param wordValidator Biconsumer to set
		 * @return Builder object with the value set
		 */
		public Builder setWordValidator(BiConsumer<GameModel, Word> wordValidator) {
			this.wordValidator = wordValidator;
			return this;
		}

		public Builder setTimer(
				Consumer<GameModel> timer,
				Function<GameModel, Supplier<Long>> delayGenerator
		) {
			this.timer = game -> {
				Supplier<Long> delay = delayGenerator.apply(game);
				var timeline = new Timeline(
						new KeyFrame(
								Duration.seconds(delay.get()),
								e -> timer.accept(game)
						)
				);
				timeline.setCycleCount(1);
				timeline.setOnFinished(e -> {
					if(GameController.getInstance().isRunning()) {
						timeline.getKeyFrames().clear();
						timeline.getKeyFrames().add(
								new KeyFrame(
										Duration.seconds(delay.get()),
										event -> timer.accept(game)
								)
						);
						timeline.play();
					}
				});
				return timeline;
			};
			return this;
		}

		public Builder setTimer(Consumer<GameModel> timer, long delay) {
			return setTimer(timer, game -> () -> delay);
		}

		/**
		 * Build method to create the instance of GameModel
		 * @return the instance
		 */
		public GameModel build() {
			PlayerModel playerModel = initNbLives != 0 ?
					PlayerModel.withLivesAndLevel(initNbLives)
					: PlayerModel.withoutLivesAndLevel();
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
