package project.models.game;

import project.models.Model;

import java.io.Serializable;

public sealed abstract class PlayerModel extends Model implements Serializable {
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
	 */
	private PlayerModel() {
		this.score = 0;
		this.nbCorrectWords = 0;
	}

	/**
	 * A fabric of the PlayerModel without lives and level
	 * @return the player model
	 */
	public static PlayerModel withoutLivesAndLevel() {
		return new WithoutLivesAndLevel();
	}

	/**
	 * A fabric of the PlayerModel with lives and level
	 * @return the player model
	 */
	public static PlayerModel withLivesAndLevel(int lives) {
		return new WithLivesAndLevel(lives, 0);
	}

	/**
	 * Initial value of the lives for the interface
	 * @return -1
	 */
	public int getLives() {
		return -1;
	}

	/**
	 * Get the score of the player
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Initial value of the levels for the interface
	 * @return the levels
	 */
	public int getLevel() {
		return -1;
	}

	/**
	 * Get the number of correct words written by the player
	 * @return the number of well written words
	 */
	public int getNbCorrectWords() {
		return nbCorrectWords;
	}

	/**
	 * Get if the player is still alive
	 * @return the boolean
	 */
	public abstract boolean isAlive();

	/**
	 * Add score to the player score
	 * @param score the score to add
	 */
	public void addScore(int score) {
		this.score += score;
		notifyViewers();
	}

	/**
	 * Increment player level
	 */
	public abstract void incrementLevel();

	/**
	 * Decrement player life
	 */
	public abstract void decrementLife();

	/**
	 * Increment player life
	 */
	public abstract void incrementLife();

	/**
	 * Increment the counter of well written words
	 */
	public void incrementCorrectWord() {
		this.nbCorrectWords++;
		notifyViewers();
	}

	private static final class WithoutLivesAndLevel extends PlayerModel {

		public WithoutLivesAndLevel() {
			super();
		}

		@Override public boolean isAlive() {
			return true;
		}

		@Override public void incrementLevel() {
			// Do nothing
		}

		@Override public void decrementLife() {
			// Do nothing
		}

		@Override public void incrementLife() {
			// Do nothing
		}
	}

	private static final class WithLivesAndLevel extends PlayerModel {
		private int lives;
		private int level;

		public WithLivesAndLevel(int lives, int level) {
			super();
			this.lives = lives;
			this.level = level;
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

		@Override public void incrementLevel() {
			level++;
			notifyViewers();
		}

		@Override public void decrementLife() {
			lives--;
			notifyViewers();
		}

		@Override public void incrementLife() {
			lives++;
			notifyViewers();
		}
	}
}
