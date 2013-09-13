package de.thm.mrcraps.controllers;

public class SchwierigkeitsManager {

	private static SchwierigkeitsManager instance;
	private int min_schwierigkeitsgrad;
	private int max_schwierigkeitsgrad;
	private int counter;

	private SchwierigkeitsManager() {
		min_schwierigkeitsgrad = 1;
		max_schwierigkeitsgrad = 1;
		counter = 0;
	}

	public static SchwierigkeitsManager getInstance() {
		if (instance == null)
			instance = new SchwierigkeitsManager();
		return instance;
	}

	public void reset() {
		min_schwierigkeitsgrad = 1;
		max_schwierigkeitsgrad = 1;
		counter = 0;
	}

	public int getMinSchwierigAndIncrease() {
		if (min_schwierigkeitsgrad < max_schwierigkeitsgrad)
			min_schwierigkeitsgrad++;
		else
			min_schwierigkeitsgrad = Math.round((max_schwierigkeitsgrad / 5) * (max_schwierigkeitsgrad / 5)) + 1;
		return min_schwierigkeitsgrad;
	}

	public int getMinSchwierig() {
		return min_schwierigkeitsgrad;
	}

	public int getMaxSchwierig() {
		return max_schwierigkeitsgrad;
	}

	public void calSchwierigkeit() {
		setSchwierigkeit((int) Math.round(Math.sqrt(counter)));
	}

	public void setSchwierigkeit(int newSchwierigkeit) {
		max_schwierigkeitsgrad = newSchwierigkeit;
	}

	public int getCounter() {
		return counter;
	}

	public void increaseCounter() {
		counter++;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
}
