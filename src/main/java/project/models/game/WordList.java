package project.models.game;

import project.models.Model;

import java.util.Iterator;
import java.util.Vector;

/**
 * A list of words chosen randomly from a library of words
 */
public class WordList extends Model {
	private final Vector<String> words;
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
		return words.get(0);
	}

	/**
	 * Get how many letters are well written
	 * @return index
	 */
	public final int getNumberOfValidLetters(){
		return currentLetter;
	}

	/**
	 * Get the current letter of the current word
	 *
	 * @return the current letter
	 */
	public final char getCurrentLetter() {
		return words.get(0).charAt(currentLetter);
	}

	//TODO Enlever le return boolean ?

	/**
	 * Goes to the next letter of the word
	 * @return true if it wasn't the last letter
	 */
	public final boolean nextLetter() {
		if(currentLetter + 1 < getCurrentWord().length()) {
			currentLetter++;
			notifyViewers();
			return true;
		}
		return false;
	}

	/**
	 * Goes to the previous letter of the word
	 * @return true if it wasn't the first letter
	 */
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

	/**
	 * Set the current letter to 0
	 */
	public void resetCurrentLetter(){
		currentLetter=0;
	}
}
