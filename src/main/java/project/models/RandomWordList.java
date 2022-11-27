package project.models;

import java.util.Iterator;
import java.util.Vector;

public class RandomWordList extends Model {
	private final Vector<String> words;
	private int currentWord;
	private int currentLetter;

	public RandomWordList(int numberOfWords) {
		words = new Vector<>(numberOfWords);
		while(numberOfWords-- > 0) push();
	}

	public RandomWordList() {
		this(15);
	}

	public final void push() {
		words.add(RandomWord.getInstance()
							.nextWord());
		notifyViewers();
	}

	public final void pop() {
		words.remove(0);
		notifyViewers();
	}

	public final String getCurrentWord() {
		return words.get(currentWord);
	}

	public final char getCurrentLetter() {
		return getCurrentWord().charAt(currentLetter);
	}

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
