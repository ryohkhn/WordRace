package project.models.game;

import project.models.Model;

import java.io.Serial;
import java.io.Serializable;

public sealed abstract class PlayerModel extends Model implements Serializable {
	private int score;
	private int nbCorrectWords;
	private int nbCorrectWordsLevel;

	private PlayerModel() {
		this.score = 0;
		this.nbCorrectWords = 0;
	}

	public static PlayerModel withoutLivesAndLevel() {
		return new WithoutLivesAndLevel();
	}

	public static PlayerModel withLivesAndLevel(int lives) {
		return new WithLivesAndLevel(lives, 0);
	}

	public int getLives() {
		return -1;
	}

	public int getScore() {
		return score;
	}

	public int getLevel() {
		return -1;
	}

	public int getNbCorrectWords() {
		return nbCorrectWords;
	}

	public int getNbCorrectWordsLevel() {
		return nbCorrectWordsLevel;
	}

	public abstract boolean isAlive();

	public void addScore(int score) {
		this.score += score;
		notifyViewers();
	}

	public abstract void incrementLevel();

	public abstract void decrementLife();

	public abstract void incrementLife();

	public void incrementCorrectWord() {
		this.nbCorrectWords++;
		this.nbCorrectWordsLevel++;
		notifyViewers();
	}

	public void resetCorrectWordsLevel() {
		this.nbCorrectWordsLevel = 0;
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
			return level;
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
