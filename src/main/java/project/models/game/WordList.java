package project.models.game;

import project.models.Model;

import java.util.Iterator;
import java.util.Vector;

/**
 * A list of words chosen randomly from a library of words
 */
public class WordList extends Model {
	private final Vector<String> words;
	private int currentWord;
	private int currentLetter;

	/**
	 * Create a new word list with initial number of words
	 *
	 * @param numberOfWords the number of words to generate
	 */
	public WordList(int numberOfWords) {
		words = new Vector<>(numberOfWords);
		while(numberOfWords-- > 0) push();
	}

	/**
	 * Create a new word list with 15 words
	 */
	public WordList() {
		this(15);
	}

	/**
	 * Add a new random word at the end of the list
	 */
	public final void push() {
		words.add(RandomWord.getInstance()
							.nextWord());
		notifyViewers();
	}

	/**
	 * Remove the first word of the list
	 */
	public final void pop() {
		words.remove(0);
		notifyViewers();
	}

	/**
	 * Get the current word of the list
	 *
	 * @return the current word
	 */
	public final String getCurrentWord() {
		return words.get(currentWord);
	}

	/**
	 * Get the previous word of the list
	 *
	 * @return the previous word
	 */
	public final String getPreviousWord()
	throws ArrayIndexOutOfBoundsException {
		return words.get(currentWord - 1);
	}

	/**
	 * Get the current letter of the current word
	 *
	 * @return the current letter
	 */
	public final char getCurrentLetter() {
		return getCurrentWord().charAt(currentLetter);
	}

	/**
	 * Move the cursor of the current word to the next word, if the current word
	 * is the last word nothing happens
	 *
	 * @return true if there is a next word, false otherwise
	 */
	public final boolean nextWord() {
		if(currentWord + 1 < words.size()) {
			currentWord++;
			currentLetter = 0;
			notifyViewers();
			return true;
		}
		return false;
	}

	public final boolean nextLetter() {
		if(currentLetter + 1 < getCurrentWord().length()) {
			currentLetter++;
			notifyViewers();
			return true;
		}
		return false;
	}

	public final boolean previousWord() {
		if(currentWord - 1 >= 0) {
			currentWord--;
			currentLetter = 0;
			notifyViewers();
			return true;
		}
		return false;
	}

	public final boolean previousLetter() {
		if(currentLetter - 1 >= 0) {
			currentLetter--;
			notifyViewers();
			return true;
		}
		return false;
	}

	public final Iterator<String> iterator() {
		return words.iterator();
	}
}
