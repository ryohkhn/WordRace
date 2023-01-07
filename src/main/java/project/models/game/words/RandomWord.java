package project.models.game.words;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * A random word generator
 */
public final class RandomWord {
	/**
	 * File location of the words used
	 */
	private final static String fileName = "src/main/resources/words_dictionnary.txt";
	/**
	 * Final static instance of RandomWord
	 *
	 * @see #getInstance()
	 */
	private final static RandomWord instance = new RandomWord();
	/**
	 * Random object to generate an index getter for the words
	 */
	private final Random random;
	/**
	 * The list of string of the generator
	 */
	private final List<String> words;

	/**
	 * Constructor of RandomWord
	 */
	private RandomWord() {
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(reader);
			this.words = Collections.unmodifiableList(
					bufferedReader.lines()
								  .collect(
										  ArrayList::new,
										  ArrayList::add,
										  ArrayList::addAll
								  )
			);
			this.random = new Random();
		} catch(FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the instance of RandomWord
	 * @return the instance
	 */
	public static RandomWord getInstance() {
		return instance;
	}

	/**
	 * Generate a random word with game mode probabilities
	 * @return the word generated
	 */
	public String generateWord() {
		return words.get(random.nextInt(words.size()));
	}

	/**
	 * Generate a stream of string of count elements
	 * @param count the number of words
	 * @return the stream of string
	 */
	public Stream<String> generateWords(int count) {
		return Stream.generate(this::generateWord)
					 .limit(count);
	}


}
