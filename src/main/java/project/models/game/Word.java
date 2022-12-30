package project.models.game;

import java.io.Serializable;
import java.util.Random;
import java.util.stream.Stream;

public sealed abstract class Word implements Serializable {
	private final static Random random = new Random();
	private final String content;

	private Word(String content) {
		if(content == null)
			throw new IllegalArgumentException("content cannot be null");
		this.content = content;
	}


	/**
	 * Create a new normal word
	 *
	 * @param content the content of the word
	 * @return a new normal word
	 * @throws IllegalArgumentException if content is null
	 */
	public static Normal normal(String content) {
		return new Normal(content);
	}

	/**
	 * Create a new bonus word
	 *
	 * @param content the content of the word
	 * @return a new bonus word
	 * @throws IllegalArgumentException if content is null
	 */
	public static Bonus bonus(String content) {
		return new Bonus(content);
	}

	/**
	 * Create a new malus word
	 *
	 * @param content the content of the word
	 * @return a new malus word
	 * @throws IllegalArgumentException if content is null
	 */
	public static Malus malus(String content) {
		return new Malus(content);
	}

	/**
	 * Generate a new word with a random type (normal, bonus or malus) and a random content
	 *
	 * @param normal the probability of generating a normal word
	 * @param malus  the probability of generating a malus word
	 * @param bonus  the probability of generating a bonus word
	 * @return a new word
	 * @throws IllegalArgumentException if normal + malus + bonus is not equal to 1
	 * @throws IllegalArgumentException if normal, malus or bonus is not between 0 and 1
	 * @see RandomWord
	 */
	public static Word generateWord(double normal, double malus, double bonus) {
		if(normal + malus + bonus != 1.0)
			throw new IllegalArgumentException(
					"The sum of the probabilities must be equal to 1"
			);
		if(normal < 0 || normal > 1 || malus < 0 || malus > 1 ||
				bonus < 0 || bonus > 1)
			throw new IllegalArgumentException(
					"The probabilities must be between 0 and 1"
			);

		double random = Word.random.nextDouble();
		if(random <= normal)
			return normal(RandomWord.getInstance().generateWord());
		else if(random <= normal + malus)
			return malus(RandomWord.getInstance().generateWord());
		else
			return bonus(RandomWord.getInstance().generateWord());
	}

	/**
	 * Generate a new word with a random type (normal, bonus or malus) and a random content
	 * with the default probabilities (normal = 0.8, malus = 0.1, bonus = 0.1)
	 *
	 * @return a new word
	 */
	public static Word generateWord() {
		return generateWord(0.8, 0.1, 0.1);
	}

	/**
	 * Generate a stream of words with the specified probability for each type
	 *
	 * @param numberOfWords the number of words to generate
	 * @param normal        the probability of generating a normal word
	 * @param malus         the probability of generating a malus word
	 * @param bonus         the probability of generating a bonus word
	 * @return a stream of words
	 * @throws IllegalArgumentException if numberOfWords is negative or null
	 */
	public static Stream<Word> stream(
			int numberOfWords,
			double normal,
			double malus,
			double bonus
	) {
		if(numberOfWords <= 0)
			throw new IllegalArgumentException("numberOfWords must be positive");
		return Stream.generate(() -> generateWord(normal, malus, bonus))
					 .limit(numberOfWords);
	}

	/**
	 * Generate a stream of words with the default probability for each type
	 * (normal = 0.8, malus = 0.1, bonus = 0.1).
	 *
	 * @param numberOfWords the number of words to generate
	 * @return a stream of words
	 * @throws IllegalArgumentException if numberOfWords is negative or null
	 */
	public static Stream<Word> stream(int numberOfWords) {
		return stream(numberOfWords, 0.8, 0.1, 0.1);
	}

	/**
	 * Returns whether the word is a bonus word.
	 *
	 * @return true if the word is a bonus word, false otherwise
	 */
	public boolean isBonus() {
		return false;
	}

	/**
	 * Returns whether the word is a malus word.
	 *
	 * @return true if the word is a malus word, false otherwise
	 */
	public boolean isMalus() {
		return false;
	}

	/**
	 * Returns the content of the word.
	 *
	 * @return the content of the word
	 */
	public final String content() {
		return content;
	}

	/**
	 * Returns the length of the word.
	 *
	 * @return the length of the word
	 */
	public final int length() {
		return content().length();
	}

	@Override public final String toString() {
		return content();
	}

	private static final class Normal extends Word {
		private Normal(String content) {
			super(content);
		}
	}

	private static final class Bonus extends Word {
		private Bonus(String content) {
			super(content);
		}

		@Override public boolean isBonus() {
			return true;
		}
	}

	private static final class Malus extends Word {
		private Malus(String content) {
			super(content);
		}

		@Override public boolean isMalus() {
			return true;
		}
	}
}
