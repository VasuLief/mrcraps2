package de.thm.mrcraps.controllers;


import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask;
import de.thm.mrcraps.Common;
import de.thm.mrcraps.gamemap.GameMap;
import de.thm.mrcraps.gamemap.fieldcontent.Arrow;
import de.thm.mrcraps.gamemap.fieldcontent.Fire;
import de.thm.mrcraps.views.MultiplayerActivity;

public class GameClientThread extends AsyncTask<String, Void, Vector<String[]>> {

	public final int ARROW_SLEEPTIME = 1500;
	private Activity ma;
	private GameMap map;
	private Arrow[] arrows;
	private long arrowExpTime;
	private long fireExpTime;

	public GameClientThread(Activity s, GameMap m, Arrow[] arrows, long arrowExpTime, long fireExpTime) {

		ma = s;
		map = m;
		this.arrows = arrows;
		this.arrowExpTime = arrowExpTime;
		this.fireExpTime = fireExpTime;

	}

	@Override
	protected Vector<String[]> doInBackground(String... params) {

		for (int arrowCounter = 0; arrowCounter < arrows.length; arrowCounter++) {
			map.spawnArrow(arrows[arrowCounter]);
		}

		publishProgress();

		for (int f = 0; f < 6; f++) {

			try {
				Thread.sleep(arrowExpTime / 6);
			} catch (InterruptedException e) {
			}

			for (int a = 0; a < arrows.length; a++) {
				arrows[a].toggleVisibility();
			}

			// updaten
			publishProgress();

		}

		if (((MultiplayerActivity) ma).state == Common.STATE_LIVE) {

			// Feuer Thread starten
			long sysTime = System.currentTimeMillis();
			for (int i = 0; i < arrows.length; i++) {
				int startpoint = arrows[i].getStartPoint();
				int direction = arrows[i].getDirection();
				map.spawnFire(new Fire(startpoint, direction, fireExpTime + sysTime));
			}

			// updaten
			publishProgress();

			try {
				Thread.sleep(fireExpTime);
			} catch (InterruptedException e) {
			}

			// updaten
			publishProgress();

		}

		return null;
	}

	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		((MultiplayerActivity) ma).onProgressUpdate(values);
	}

	protected void onPostExecute(Vector<String[]> Result) {

	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

}
