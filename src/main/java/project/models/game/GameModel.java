package project.models.game;

import project.models.Model;

import java.util.Iterator;

public class GameModel extends Model {
	private final WordList words;
	private final Stats stats;
	private PlayerModel player;
	private final int nbWords;
	private String inputWord;

	// On normal mode, the words list is set to the number of words we display
	public GameModel(int nbWords) {
		this.inputWord = "";
		this.nbWords=nbWords;
		this.stats = new Stats();
		this.words = new WordList(nbWords);
		this.player = new PlayerModel(0);
	}

	// On competitive mode, the words list is generated to 1
	public GameModel(int lives, int nbWords) {
		this.inputWord = "";
		this.nbWords=nbWords;
		this.stats = new Stats();
		this.words = new WordList(1);
		this.player = new PlayerModel(lives);
	}

	/**
	 * Get an iterator over the words
	 *
	 * @return the iterator
	 */
	public Iterator<Word> getWordsIterator() {
		return words.iterator();
	}

	public int getNbWords(){
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
