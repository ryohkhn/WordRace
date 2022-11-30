package project.models.game;

import project.models.Model;

public class Stats extends Model {
	private final long startTime;
	private long endTime = -1;
	private int numberOfPressedKeys;
	private int usefulCharacters;

	public Stats() {
		this.startTime = System.currentTimeMillis();
	}

	public int getElapsedTime() {
		if(endTime > 0) return (int) (endTime - startTime);
		return (int) (System.currentTimeMillis() - startTime);
	}

	public final int getElapsedTimeInMinutes() {
		return getElapsedTime() / 60000;
	}

	public final int getMPM() {
		return usefulCharacters / getElapsedTimeInMinutes() / 5;
	}

	public final double getAccuracy() {
		return (double) usefulCharacters / numberOfPressedKeys * 100;
	}

	public final void incrementNumberOfPressedKeys() {
		if(endTime < 0) {
			numberOfPressedKeys++;
			notifyViewers();
		}
	}

	public final void incrementUsefulCharacters() {
		if(endTime < 0) {
			usefulCharacters++;
			numberOfPressedKeys++;
			notifyViewers();
		}
	}

	public final void end() {
		endTime = System.currentTimeMillis();
		notifyViewers();
	}
}
