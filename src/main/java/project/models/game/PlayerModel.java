package project.models.game;

public final class PlayerModel {
	private int lives;
	private int score;
	private int level;
	private int nbCorrectWords;

	public PlayerModel(int lives) {
		if(lives < 1)
			throw new IllegalArgumentException("The number of lives must be greater than 0");
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

	public boolean isAlive() {
		return lives > 0;
	}

	public void addScore(int score) {
		this.score += score;
	}

	public void incrementLevel() {
		this.level++;
	}

	public void decrementLife() {
		this.lives--;
	}

	public void incrementLife() {
		this.lives++;
	}

	public void incrementCorrectWord() {
		this.nbCorrectWords++;
	}
}
