package de.thm.mrcraps.controllers;

import de.thm.mrcraps.views.SingleplayerActivity;
import android.os.AsyncTask;

/**
 * Dieser Thread malt die Map neu nachdem das Feuer erloschen sein sollte, aber
 * bevor neue Pfeile gemalt werden
 * 
 * @author Vasu
 * 
 */
public class DelayedThread extends AsyncTask<int[], Void, int[][]> {

	private SingleplayerActivity sa;

	long time;

	public DelayedThread(SingleplayerActivity c, long l) {

		this.sa = c;
		time = l;
	}

	protected int[][] doInBackground(int[]... params) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
		return null;
	}

	protected void onPostExecute(int[][] result) {

		sa.invalidate();
		sa.playersAlive();

	}

}