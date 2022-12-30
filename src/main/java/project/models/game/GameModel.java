package project.models.game;

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
	private final Thread timerThread;
	private String inputWord;

	private GameModel(
			int InitNbWords,
			PlayerModel player,
			Supplier<Word> wordGenerator,
			BiConsumer<GameModel, Word> wordValidation,
			Function<GameModel, Runnable> timerRunnable
	) {
		this.nbWords = InitNbWords;
		this.player = player;
		this.wordValidation = wordValidation;
		this.words = new WordList(InitNbWords, wordGenerator);
		this.stats = new Stats();
		this.inputWord = "";

		if(timerRunnable != null) {
			this.timerThread = new Thread(timerRunnable.apply(this));
			this.timerThread.start();
		} else
			this.timerThread = null;
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

	public boolean validateCurrentWord() {
		if(isCurrentWordFinished()) {
			wordValidation.accept(this, words.getCurrentWord());
			player.incrementCorrectWord();
			player.addScore(words.getCurrentWord().length());
			words.pop();
			resetInputWord();
			return true;
		}
		return false;
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

	public static final class Builder {
		private int initNbWords, initNbLives;
		private Supplier<Word> wordGenerator;
		private BiConsumer<GameModel, Word> wordValidator;
		private Function<GameModel, Runnable> timer;

		public Builder() {
			initNbWords = initNbLives = 0;
			wordGenerator = null;
			wordValidator = null;
			timer = null;
		}

		public static GameModel soloNormal(int initNbWords) {
			return new Builder()
					.setInitNbWords(initNbWords)
					.setWordGenerator(() -> Word.generateWord(1, 0, 0))
					.setWordValidator((game, word) -> game.getWords().push())
					.build();
		}

		public static GameModel soloCompetitive(int nbWords, int lives) {
			return new Builder()
					.setInitNbWords(nbWords)
					.setInitNbLives(lives)
					.setWordGenerator(() -> Word.generateWord(0.8, 0, 0.2))
					.setWordValidator((game, word) -> {
						if(word.isBonus()) game.getPlayer().incrementLife();
					})
					.setTimer(game -> game.getWords().push())
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
				long startTime = game.getStats().getStartTime();
				while(!Thread.interrupted()) {
					long n = (System.currentTimeMillis() - startTime) / 1000;
					long wait = (long) (3 * Math.pow(0.9, n) * 1000);
					try {
						Thread.sleep(wait);
					} catch(InterruptedException e) {
						break;
					}
					timer.accept(game);
				}
			};
			return this;
		}

		public GameModel build() {
			return new GameModel(
					initNbWords,
					PlayerModel.withLivesAndLevel(initNbLives),
					wordGenerator,
					wordValidator,
					timer
			);
		}
	}
}
