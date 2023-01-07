package project.models.game;

import project.models.Model;

import java.io.Serializable;

public sealed abstract class PlayerModel extends Model implements Serializable, Cloneable {
	/**
	 * The player's name.
	 *
	 * @see #getName()
	 */
	private final String name;

	/**
	 * Score of the player
	 *
	 * @see #getScore()
	 */
	private int score;
	/**
	 * Number of correct words written by th player
	 *
	 * @see #getNbCorrectWords()
	 */
	private int nbCorrectWords;

	/**
	 * Initial private constructor of PlayerModel
	 *
	 * @param name The player's name
	 */
	private PlayerModel(String name) {
		this.name = name;
		this.score = 0;
		this.nbCorrectWords = 0;
	}

	/**
	 * A fabric of the PlayerModel without lives and level
	 *
	 * @param name The player's name
	 * @return the player model
	 */
	public static PlayerModel withoutLivesAndLevel(String name) {
		return new WithoutLivesAndLevel(name);
	}

	/**
	 * A fabric of the PlayerModel with lives and level
	 *
	 * @param name The player's name
	 * @return the player model
	 */
	public static PlayerModel withLivesAndLevel(String name, int lives) {
		return new WithLivesAndLevel(name, lives);
	}

	/**
	 * Get the player's name
	 *
	 * @return the player's name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Initial value of the lives for the interface
	 *
	 * @return -1 by default
	 */
	public int getLives() {
		return -1;
	}

	/**
	 * Get the score of the player
	 *
	 * @return the score
	 */
	public final int getScore() {
		return score;
	}

	/**
	 * Initial value of the levels for the interface
	 *
	 * @return the levels
	 */
	public int getLevel() {
		return -1;
	}

	/**
	 * Get the number of correct words written by the player
	 *
	 * @return the number of well written words
	 */
	public int getNbCorrectWords() {
		return nbCorrectWords;
	}

	/**
	 * Get if the player is still alive
	 *
	 * @return the boolean
	 */
	public abstract boolean isAlive();

	/**
	 * Add score to the player score
	 *
	 * @param score the score to add
	 */
	public void addScore(int score) {
		this.score += score;
		notifyViewers();
	}

	/**
	 * Decrement player life
	 */
	public abstract void decrementLife();

	/**
	 * Increment player life
	 */
	public abstract void incrementLife();

	/**
	 * Increment a number of player lives
	 *
	 * @param number the number of lives to add
	 */
	public abstract void incrementLives(int number);

	/**
	 * Increment the counter of well written words
	 */
	public void incrementCorrectWord() {
		this.nbCorrectWords++;
		notifyViewers();
	}

	@Override public PlayerModel clone() {
		try {
			return (PlayerModel) super.clone();
		} catch(CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	private static final class WithoutLivesAndLevel extends PlayerModel {

		public WithoutLivesAndLevel(String name) {
			super(name);
		}

		@Override public boolean isAlive() {
			return true;
		}

		@Override public void decrementLife() {
			// Do nothing
		}

		@Override public void incrementLife() {
			// Do nothing
		}

		@Override public void incrementLives(int number) {
			// Do nothing
		}
	}

	private static final class WithLivesAndLevel extends PlayerModel {
		private int lives;

		public WithLivesAndLevel(String name, int lives) {
			super(name);
			this.lives = lives;
		}

		@Override public int getLives() {
			return lives;
		}

		@Override public int getLevel() {
			return getNbCorrectWords() / 100;
		}

		@Override public boolean isAlive() {
			return lives > 0;
		}

		@Override public void decrementLife() {
			lives--;
			notifyViewers();
		}

		@Override public void incrementLife() {
			lives++;
			notifyViewers();
		}

		@Override public void incrementLives(int number) {
			lives += number;
			notifyViewers();
		}
	}
}
