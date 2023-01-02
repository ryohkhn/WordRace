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
	private final static String fileName = "src/main/resources/words_alpha.txt";
	private final static RandomWord instance = new RandomWord();
	private final Random random;
	private final List<String> words;

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

	public static RandomWord getInstance() {
		return instance;
	}

	public String generateWord() {
		return words.get(random.nextInt(words.size()));
	}

	public Stream<String> generateWords(int count) {
		return Stream.generate(this::generateWord)
					 .limit(count);
	}


}
