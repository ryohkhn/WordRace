package project.models.game;

import project.models.Model;

import java.util.Iterator;

public class GameModel extends Model {
	private final WordList words;
	private final Stats stats;
	private final PlayerModel player;
	private String inputWord;

	public GameModel(int lives, int nbWords) {
		this.inputWord = "";
		this.stats = new Stats();
		this.words = new WordList(nbWords);
		this.player = new PlayerModel(lives);
	}

	/**
	 * Get an iterator over the words
	 *
	 * @return the iterator
	 */
	public Iterator<String> getWordsIterator() {
		return words.iterator();
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
		return inputWord.equals(words.getCurrentWord());
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
}
