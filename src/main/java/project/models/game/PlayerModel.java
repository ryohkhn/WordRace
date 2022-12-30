package project.models.game;

import project.models.Model;

public final class PlayerModel extends Model {
	private int lives;
	private int score;
	private int level;
	private int nbCorrectWords;
	private int nbCorrectWordsLevel;

	public PlayerModel(int lives) {
		// TODO temporaire le temps d'adapter le model à plusieurs jeux
		/*
		if(lives < 1)
			throw new IllegalArgumentException(
					"The number of lives must be greater than 0");

		 */
		this.lives = lives;
		this.score = 0;
		this.level = 0;
		this.nbCorrectWords = 0;
	}

	public int getLives() {
		return lives;
	}

	public int getScore() {
		return score;
	}

	public int getLevel() {
		return level;
	}

	public int getNbCorrectWords() {
		return nbCorrectWords;
	}

	public int getNbCorrectWordsLevel(){
		return nbCorrectWordsLevel;
	}

	public boolean isAlive() {
		return lives > 0;
	}

	public void addScore(int score) {
		this.score += score;
		notifyViewers();
	}

	public void incrementLevel() {
		this.level++;
		notifyViewers();
	}

	public void decrementLife() {
		this.lives--;
		notifyViewers();
	}

	public void incrementLife() {
		this.lives++;
		notifyViewers();
	}

	public void incrementCorrectWord() {
		this.nbCorrectWords++;
		this.nbCorrectWordsLevel++;
		notifyViewers();
	}

	public void resetCorrectWordsLevel(){
		this.nbCorrectWordsLevel=0;
	}

	@Override public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		PlayerModel that = (PlayerModel) o;

		return lives == that.lives && score == that.score &&
				level == that.level &&
				nbCorrectWords == that.nbCorrectWords;
	}
}
