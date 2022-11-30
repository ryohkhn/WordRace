package project.models.game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomWord {
	private final static String fileName = "src/main/resources/words_alpha.txt";
	private static RandomWord instance = null;
	private final Random random = new Random();
	private final List<String> words;

	private RandomWord() throws FileNotFoundException {
		FileReader reader = new FileReader(fileName);
		BufferedReader bufferedReader = new BufferedReader(reader);
		words = bufferedReader.lines().collect(
				ArrayList::new,
				ArrayList::add,
				ArrayList::addAll
		);
	}

	public static RandomWord getInstance() {
		if(instance == null) {
			try {
				instance = new RandomWord();
			} catch(FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}

	public String nextWord() {
		return words.get(random.nextInt(words.size()));
	}
}
