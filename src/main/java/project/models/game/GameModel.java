package project.models.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import project.controllers.game.GameController;
import project.controllers.game.NetworkController;
import project.models.Model;

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
	 * If the current word is finished, it will execute the wordValidation
	 * function and the following actions:
	 * - Increment the number of correct words
	 * - Increment the score of the player
	 * - Reset the input word and current letter
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

	public String getInputWord() {
		return inputWord;
	}

	public void addLetterToInputWord(char c) {
		inputWord += c;
	}

	public boolean removeLetterFromInputWord() {
		if(inputWord.length() > 0) {
			inputWord = inputWord.substring(0, inputWord.length() - 1);
			notifyViewers();
			return true;
		}
		return false;
	}

	public void resetInputWord() {
		inputWord = "";
		GameController.getInstance().getView().resetInputText();
	}

	private void timerCompetitiveMode() {
		words.push();
	}

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

		public static GameModel soloNormal(int initNbWords) {
			return new Builder()
					.setInitNbWords(initNbWords)
					.setMaximumNbWords(initNbWords)
					.setWordGenerator(() -> Word.generateWord(1, 0, 0))
					.setWordValidator((game, word) -> game.getWords().push())
					.build();
		}

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

		public static GameModel multiplayer(int nbWords) {
			return new Builder()
					.setInitNbWords(nbWords)
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
					.setTimer(game -> {
						Word word = NetworkController.getInstance()
													 .getModel()
													 .tryReceiveWord();
						if(word != null)
							game.getWords().push(word);
					}, 2)
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
