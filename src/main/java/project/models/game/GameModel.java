package project.models.game;

import project.models.Model;

import java.util.Iterator;

public class GameModel extends Model {
	private final WordList words;
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
		this.words = new WordList(nbWords);
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

	public char getCurrentLetter() {
		return words.getCurrentLetter();
	}

	public int getElapsedTime() {
		return stats.getElapsedTime();
	}

	public int getElapsedTimeInMinutes() {
		return stats.getElapsedTimeInMinutes();
	}

	public int getMPM() {
		return stats.getMPM();
	}

	public double getAccuracy() {
		return stats.getAccuracy();
	}

	public int getNumberOfPressedKeys() {
		return stats.getNumberOfPressedKeys();
	}

	public int getUsefulCharacters() {
		return stats.getUsefulCharacters();
	}
}
