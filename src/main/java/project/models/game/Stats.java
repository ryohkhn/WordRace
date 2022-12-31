package project.models.game;

import project.models.Model;

/**
 * Represents the statistics of the game, such as the number of useful characters
 * typed, the elapsed time, etc.
 */
public class Stats extends Model {
	private final long startTime;
	private long endTime = -1;
	private double numberOfPressedKeys;
	private double usefulCharacters;

	public Stats() {
		this.startTime = System.currentTimeMillis();
	}

	/**
	 * Return the elapsed time in milliseconds
	 *
	 * @return the elapsed time
	 */
	public int getElapsedTime() {
		if(endTime > 0) return (int) (endTime - startTime);
		return (int) (System.currentTimeMillis() - startTime);
	}

	/**
	 * Return the elapsed time in minutes
	 *
	 * @return the elapsed time
	 */
	public final double getElapsedTimeInMinutes() {
		return getElapsedTime() / 60000.;
	}

	/**
	 * Return the words per minute divided by 5
	 *
	 * @return the words per minute
	 */
	public final double getMPM() {
		return usefulCharacters / getElapsedTimeInMinutes() / 5;
	}

	/**
	 * Return the percentage of useful characters typed compared to the total number of pressed
	 * keys (useful or not)
	 *
	 * @return the percentage of useful characters
	 */
	public final double getAccuracy() {
		System.out.println(usefulCharacters);
		System.out.println(numberOfPressedKeys);
		return  (usefulCharacters / numberOfPressedKeys) * 100;
	}

	/**
	 * Return the number of pressed keys
	 *
	 * @return the number of pressed keys
	 */
	public double getNumberOfPressedKeys() {
		return numberOfPressedKeys;
	}

	/**
	 * Return the number of useful characters typed
	 *
	 * @return the number of useful characters
	 */
	public double getUsefulCharacters() {
		return usefulCharacters;
	}

	/**
	 * Increment the number of pressed keys
	 */
	public final void incrementNumberOfPressedKeys() {
		if(endTime < 0) {
			numberOfPressedKeys++;
			notifyViewers();
		}
	}

	/**
	 * Increment the number of useful characters
	 */
	public final void incrementUsefulCharacters() {
		if(endTime < 0) {
			usefulCharacters++;
			notifyViewers();
		}
	}

	/**
	 * Set the end time
	 */
	public final void end() {
		endTime = System.currentTimeMillis();
		notifyViewers();
	}

	public long getStartTime() {
		return startTime;
	}
}
