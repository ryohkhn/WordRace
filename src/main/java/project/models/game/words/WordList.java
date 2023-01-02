package project.models.game.words;

import project.models.Model;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A list of words chosen randomly from a library of words
 */
public class WordList extends Model {
	private final Supplier<Word> supplier;
	private final Queue<Word> words;
	private int currentLetter;

	/**
	 * Create a new word list with initial number of words
	 *
	 * @param numberOfWords the number of words to generate
	 */
	public WordList(int numberOfWords, Supplier<Word> wordGenerator) {
		words = Stream.generate(wordGenerator).limit(numberOfWords).collect(Collectors.toCollection(ConcurrentLinkedQueue::new));
		this.supplier = wordGenerator;
	}

	public final void push(Word word) {
		words.add(word);
		notifyViewers();
	}

	/**
	 * Add a new random word at the end of the list
	 */
	public final void push() {
		push(supplier.get());
	}

	/**
	 * Remove the first word of the list
	 */
	public final void pop() {
		words.poll();
		notifyViewers();
	}

	/**
	 * Get the current word of the list
	 *
	 * @return the current word
	 */
	public final Word getCurrentWord() {
		return words.peek();
	}

	/**
	 * Get the current letter of the current word
	 *
	 * @return the current letter
	 */
	public final char getCurrentLetter() {
		return getCurrentWord().content().charAt(currentLetter);
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

	public final Iterator<Word> iterator() {
		return words.iterator();
	}

	/**
	 * Set the current letter to 0
	 */
	public void resetCurrentLetter(){
		currentLetter=0;
	}

	public int getSize(){
		return words.size();
	}

	@Override public String toString() {
		return words.parallelStream()
				.map(Word::content)
				.reduce((s, s2) -> s + " " + s2)
				.orElse("");
	}
}