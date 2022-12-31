package project.models.game;

import project.models.Model;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Represents the statistics of the game, such as the number of useful characters
 * typed, the elapsed time, etc.
 */
public class Stats extends Model {
	private final long startTime;
	private long endTime = -1;
	private double numberOfPressedKeys;
	private double usefulCharacters;
	private double lastCorrectCharacterTime;
	private final List<Double> durations=new ArrayList<>();

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
		double result = usefulCharacters / getElapsedTimeInMinutes() / 5;
		return roundTwoDecimals(result);
	}

	/**
	 * Return the percentage of useful characters typed compared to the total number of pressed
	 * keys (useful or not)
	 *
	 * @return the percentage of useful characters
	 */
	public final double getAccuracy() {
		double result = (usefulCharacters / numberOfPressedKeys) * 100;
		return roundTwoDecimals(result);
	}

	/**
	 * Get standard deviation of the duration of 2 consecutive useful characters, rounded to 2 decimals
	 * @return the deviation
	 */
	public final double getRegularity(){
		List<Double> deviations=new ArrayList<>();
		List<Double> square_deviations;
		// Calculate the average of the list of durations, where each duration is the time between 2 useful characters
		double average=durations.stream().mapToDouble(Double::doubleValue).average().orElse(0);
		// Calculate the deviation between each duration and the average
		durations.forEach(duration -> deviations.add(duration-average));
		// Calculate the power of each deviation
		square_deviations=deviations.stream().map(duration -> Math.pow(duration,2)).toList();
		DoubleSummaryStatistics square_deviations_stats=square_deviations.stream().mapToDouble(Double::doubleValue).summaryStatistics();
		double regularity=Math.sqrt(square_deviations_stats.getSum()/durations.size());
		return roundTwoDecimals(regularity);
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
			if(lastCorrectCharacterTime==0){
				lastCorrectCharacterTime=System.nanoTime();
			}
			else{
				long tmp=System.nanoTime();
				durations.add((tmp-lastCorrectCharacterTime)/1_000_000_000);
				lastCorrectCharacterTime=System.nanoTime();
			}
			notifyViewers();
		}
	}

	double roundTwoDecimals(double value){
		value = value * 100;
        long tmp = Math.round(value);
        return (double) tmp / 100;
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
