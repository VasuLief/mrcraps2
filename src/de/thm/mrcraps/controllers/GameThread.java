package de.thm.mrcraps.controllers;


import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import de.thm.mrcraps.Common;
import de.thm.mrcraps.gamemap.GameMap;
import de.thm.mrcraps.gamemap.fieldcontent.Arrow;
import de.thm.mrcraps.gamemap.fieldcontent.Fire;
import de.thm.mrcraps.gamemap.fieldcontent.Player;
import de.thm.mrcraps.views.MultiplayerActivity;
import de.thm.mrcraps.views.SingleplayerActivity;

public class GameThread extends AsyncTask<String, Void, Vector<String[]>> {

	private int mode;

	public final int ARROW_SLEEPTIME = 1500;
	private final int ARROW_EXPTIME = 1800;

	private final int MAX_FIRE_EXPTIME = 600;
	private final int MIN_FIRE_EXPTIME = 400;

	private SchwierigkeitsManager schwierigManag;
	Random rand = new Random(System.currentTimeMillis());

	private Activity sa;
	private GameMap map;

	private int curMinSchwierigkeit;
	private int curMaxSchwierigkeit;
	private int max_firecount;
	private int fireHorCount;
	private int fireVertCount;
	private int counter;
	long expTime;
	long fireExpTime;
	long sysTime;

	private Player playerself = null;
	private ConnectionManager cM;

	public GameThread(Activity s, int md, GameMap m, Player p) {
		schwierigManag = SchwierigkeitsManager.getInstance();
		mode = md;
		sa = s;
		map = m;
		playerself = p;
		cM = ConnectionManager.create(s.getApplicationContext());
		max_firecount = map.getMapSize() - 1;
	}

	protected Vector<String[]> doInBackground(String... params) {

		// Am Anfang warten
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
		}

		// Solange Spieler noch am Leben sind
		while (true) {
			// publishProgress();
			curMinSchwierigkeit = schwierigManag.getMinSchwierigAndIncrease();
			curMaxSchwierigkeit = schwierigManag.getMaxSchwierig();
			counter = schwierigManag.getCounter();
			Log.e("Schwierigkeit", "Min:" + curMinSchwierigkeit + " Max:" + curMaxSchwierigkeit);
			fireHorCount = rand.nextInt(max_firecount - curMinSchwierigkeit % max_firecount) + curMinSchwierigkeit % max_firecount;
			fireVertCount = rand.nextInt(max_firecount - curMinSchwierigkeit % max_firecount) + curMinSchwierigkeit % max_firecount;
			if (fireHorCount > (counter + 1))
				fireHorCount = counter % max_firecount + 1;
			if (fireVertCount > (counter + 1))
				fireVertCount = counter % max_firecount + 1;

			int[] startpoints = new int[fireHorCount + fireVertCount];
			int[] directions = new int[fireHorCount + fireVertCount];
			expTime = ARROW_EXPTIME - Math.round(Math.sqrt(curMaxSchwierigkeit - curMinSchwierigkeit / 2)) * 25 - ((2 * max_firecount - (fireHorCount + fireVertCount)) % 13) * 50;
			sysTime = System.currentTimeMillis();
			Arrow[] arrows = new Arrow[fireHorCount + fireVertCount];
			for (int i = 0; i < fireHorCount; i++) {
				int startpoint = rand.nextInt(map.getMapSize());
				startpoints[i] = startpoint;
				directions[i] = Arrow.DIRECTION_HOR;
				Arrow a = new Arrow(startpoint, Arrow.DIRECTION_HOR, expTime + sysTime);
				arrows[i] = a;
			}
			for (int i = fireHorCount; i < fireHorCount + fireVertCount; i++) {
				int startpoint = rand.nextInt(map.getMapSize());
				startpoints[i] = startpoint;
				directions[i] = Arrow.DIRECTION_VERT;
				Arrow a = new Arrow(startpoint, Arrow.DIRECTION_VERT, expTime + sysTime);
				arrows[i] = a;
			}
			Log.e("Schwierigkeit:", "fireCount: " + (fireHorCount + fireVertCount) + " Time: " + expTime);
			fireExpTime = (rand.nextInt(MAX_FIRE_EXPTIME - MIN_FIRE_EXPTIME) + MIN_FIRE_EXPTIME) / curMaxSchwierigkeit;

			if (sa instanceof MultiplayerActivity) {
				Bundle dataBundle = new Bundle();
				dataBundle.putBoolean(CrapsMultiplayerInterface.GAME_THREAD, true);
				dataBundle.putParcelableArray(CrapsMultiplayerInterface.ARROW, arrows);
				dataBundle.putLong(CrapsMultiplayerInterface.ARROW_EXPTIME, expTime);
				dataBundle.putLong(CrapsMultiplayerInterface.FIRE_EXPTIME, fireExpTime);
				cM.sendBundleAsMessage3(dataBundle, -1, CrapsMultiplayerInterface.GAME_THREAD);
			}

			for (int arrowCounter = 0; arrowCounter < arrows.length; arrowCounter++) {
				map.spawnArrow(arrows[arrowCounter]);
			}

			for (int f = 0; f < 6; f++) {

				// updaten
				publishProgress();
				try {
					Thread.sleep(expTime / 6);
				} catch (InterruptedException e) {
				}

				for (int a = 0; a < arrows.length; a++) {
					arrows[a].toggleVisibility();
				}

			}

			// updaten
			publishProgress();

			// Feuer Thread starten
			sysTime = System.currentTimeMillis();
			for (int i = 0; i < (fireHorCount + fireVertCount); i++) {
				int startpoint = startpoints[i];
				int direction = directions[i];
				map.spawnFire(new Fire(startpoint, direction, fireExpTime + sysTime));
			}

			// updaten
			publishProgress();

			// Punkte berechnen
			if (mode == Common.GAME_SINGLEPLAYER && playerself != null) {
				int currentPoints = playerself.getCurrentPoints();
				currentPoints = currentPoints + curMaxSchwierigkeit;
				playerself.setCurrentPoints(currentPoints);
			}
			schwierigManag.increaseCounter();
			schwierigManag.calSchwierigkeit();

			try {
				Thread.sleep(fireExpTime);
			} catch (InterruptedException e) {
			}

			// updaten
			publishProgress();

			// warten bis man neue Arrows malen kann

			try {
				Thread.sleep(ARROW_SLEEPTIME / Math.round(Math.sqrt(curMaxSchwierigkeit)));
			} catch (InterruptedException e) {
			}

			// Und jetzt das ganze wiederholen
			if (mode == Common.GAME_SINGLEPLAYER) {
				if (((SingleplayerActivity) sa).state != Common.STATE_LIVE) {
					schwierigManag.reset();
					break;
				}

			}
			if (mode == Common.GAME_MULTIPLAYER) {
				if (((MultiplayerActivity) sa).state != Common.STATE_LIVE) {
					schwierigManag.reset();
					break;
				}
			}
		}

		return null;

	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		if (mode == Common.GAME_SINGLEPLAYER) {
			((SingleplayerActivity) sa).onProgressUpdate(values);
		}
		if (mode == Common.GAME_MULTIPLAYER) {
			((MultiplayerActivity) sa).onProgressUpdate(values);
		}
	}

	protected void onPostExecute(Vector<String[]> Result) {
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

} // GameThread