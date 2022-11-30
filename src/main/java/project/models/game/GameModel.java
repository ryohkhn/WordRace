package project.models.game;

import project.models.Model;

import java.util.Iterator;

public class GameModel extends Model {
	private final RandomWordList words;
	private final Stats stats;
	private int lives, score, level;

	public GameModel(
			int lives,
			int score,
			int level,
			int nbWords
	) {
		this.lives = lives;
		this.score = score;
		this.level = level;
		this.stats = new Stats();
		this.words = new RandomWordList(nbWords);
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

	public Iterator<String> getWords() {
		return words.iterator();
	}

	public String getCurrentWord() {
		return words.getCurrentWord();
	}
}
