package project.models.game;

import project.models.Model;

import java.util.Iterator;

public class GameModel extends Model {
	private final WordList words;
	private final Stats stats;
	private int lives, score, level;
	private int count;

	public GameModel(
			int lives,
			int score,
			int level,
			int nbWords
	) {
		this.lives = lives;
		this.score = score;
		this.level = level;
		this.count = 0;
		this.stats = new Stats();
		this.words = new WordList(nbWords);
	}

	/**
	 * Get the number of lives the player has left
	 *
	 * @return the number of lives
	 */
	public int getLives() {
		return lives;
	}

	/**
	 * Get the current score of the player
	 *
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Get the current level of the player
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Get an iterator over the words
	 *
	 * @return the iterator
	 */
	public Iterator<String> getWords() {
		return words.iterator();
	}

	/**
	 * Get the current word
	 *
	 * @return the current word
	 */
	public String getCurrentWord() {
		return words.getCurrentWord();
	}

	/**
	 * Get the current letter of the current word
	 *
	 * @return the current letter
	 */
	public char getCurrentLetter() {
		return words.getCurrentLetter();
	}

	/**
	 * Get the elapsed time from the start of the game in milliseconds
	 *
	 * @return the elapsed time in milliseconds
	 */
	public int getElapsedTime() {
		return stats.getElapsedTime();
	}

	/**
	 * Get the elapsed time like from the start of the game in minutes
	 *
	 * @return the elapsed time in minutes
	 * @see #getElapsedTime()
	 */
	public int getElapsedTimeInMinutes() {
		return stats.getElapsedTimeInMinutes();
	}

	/**
	 * Get the ratio between the useful characters typed and the elapsed time
	 * in minutes divided by 5
	 *
	 * @return the ratio
	 */
	public int getMPM() {
		return stats.getMPM();
	}

	/**
	 * Get the percentage of useful characters typed compared to the total
	 *
	 * @return the percentage of useful characters
	 */
	public double getAccuracy() {
		return stats.getAccuracy();
	}

	/**
	 * Get the number of pressed keys
	 *
	 * @return the number of pressed keys
	 */
	public int getNumberOfPressedKeys() {
		return stats.getNumberOfPressedKeys();
	}

	/**
	 * Get the number of useful characters typed
	 *
	 * @return the number of useful characters
	 */
	public int getUsefulCharacters() {
		return stats.getUsefulCharacters();
	}

	/**
	 * Handle a character typed by the player.
	 * If the character is the current letter of the current word go to the
	 * next letter.
	 * If a word is completed, increment the score by the length of the word
	 * and go to the first letter of the next word.
	 *
	 * @param c the character
	 */
	public void handleInput(char c) {
		if(c == getCurrentLetter()) {
			stats.incrementUsefulCharacters();
			if(!words.nextLetter()) {
				score += words.getPreviousWord()
							  .length();
				words.pop();
				words.push();
			}
		} else {
			stats.incrementNumberOfPressedKeys();
			lives--;
		}
		notifyViewers();
	}
}
