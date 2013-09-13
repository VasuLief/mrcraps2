package de.thm.mrcraps.views;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.Vector;

import de.thm.mrcraps.Common;
import de.thm.mrcraps.controllers.DatabaseHandler;
import de.thm.mrcraps.controllers.DelayedThread;
import de.thm.mrcraps.controllers.GameThread;
import de.thm.mrcraps.controllers.ItemThread;
import de.thm.mrcraps.controllers.MoveListener;
import de.thm.mrcraps.controllers.PreferencesManager;
import de.thm.mrcraps.controllers.SchwierigkeitsManager;
import de.thm.mrcraps.gamemap.GameMap;
import de.thm.mrcraps.gamemap.fieldcontent.InterfaceFieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.Player;
import de.thm.mrcraps.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager.LayoutParams;

/**
 * Diese Activity erstellt ein Singleplayer Spiel
 * 
 * @author Vasu
 * 
 */
public class SingleplayerActivity extends Activity {

	// Einstellungen
	private SharedPreferences settings;
	private int mapSize;

	// Stuff
	private DebugThread DT;
	private ItemThread IT;
	private boolean ITT;
	private GameThread GT;
	private boolean GTT;

	// Wichtige Variablen
	private MapBackgroundView mapPlane;
	private MapVordergrundView gamePlane;
	private MapThirdView pointPlane;
	private MapNotifView notifPlane;
	private GameMap map;
	private Player playerSelf;
	private Vector<Player> players;
	private SchwierigkeitsManager schwierigManag;

	// Steuerungsvariablen
	private MessageHandler msgHandler;
	private MoveListener movementListener;
	private SensorManager mSensorManager;
	private Sensor gyroSensor;

	// irgendwas
	private long cancelTime;
	private boolean retButtonWasClicked = false;

	// live
	public int state = Common.STATE_WAITING;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Laden der Settings
		settings = new PreferencesManager(this).getSharedPreferencesObject();
		mapSize = Integer.valueOf(settings.getString("sp_mapsize", "7").split("x")[0]);

		int xpos, ypos;
		xpos = (int) Math.ceil(mapSize / 2);
		ypos = (int) Math.ceil(mapSize / 2);
		// Laden des Players und der GameMap
		playerSelf = new Player(xpos, ypos, InterfaceFieldContent.PLAYER_0, "Bernd");
		players = new Vector<Player>();
		players.add(playerSelf);
		map = new GameMap(GameMap.MODE_SINGLEPLAYER, mapSize);
		map.addPlayer(playerSelf, xpos, ypos);
		map.setPlayerSelf(playerSelf.getNumber());

		// Erstellen der View
		setContentView(R.layout.layout_game);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Die Planes
		mapPlane = (MapBackgroundView) findViewById(R.id.mapBackground);
		mapPlane.setGameMap(map);

		gamePlane = (MapVordergrundView) findViewById(R.id.mapVordergrund);
		gamePlane.setGameMap(map);
		gamePlane.setPlayer(playerSelf);
		gamePlane.bringToFront();

		pointPlane = (MapThirdView) findViewById(R.id.mapThrid);
		pointPlane.setPlayer(playerSelf);
		pointPlane.bringToFront();

		notifPlane = (MapNotifView) findViewById(R.id.mapNotif);
		notifPlane.bringToFront();

		schwierigManag = SchwierigkeitsManager.getInstance();
		schwierigManag.reset();

		// für die Steuerung per Accelerometer
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		msgHandler = new MessageHandler();
		movementListener = new MoveListener(msgHandler, this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(movementListener, gyroSensor, SensorManager.SENSOR_DELAY_GAME);

		gamePlane.invalidate();
		mapPlane.invalidate();
		pointPlane.invalidate();
		notifPlane.invalidate();

		StartThread ST = new StartThread();
		if (Build.VERSION.SDK_INT >= 11) {
			ST.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} else {
			ST.execute();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(movementListener);

		if (IT != null) {
			IT.cancel(true);
		}
		if (GT != null) {
			GT.cancel(true);
		}
		if (DT != null) {
			DT.cancel(true);
		}
		GT = null;
		IT = null;
		// AT = null;
		// FT = null;

		state = Common.STATE_DEAD;
	}

	public void startGame() {

		Log.e("SA", "start");

		state = Common.STATE_LIVE;

		boolean[] alloweditems = new boolean[3];
		alloweditems[0] = settings.getBoolean("sp_item_Banane", true);
		alloweditems[1] = settings.getBoolean("sp_item_Kleber", true);
		alloweditems[2] = settings.getBoolean("sp_item_Crap", true);
		if (DT == null)
			DT = new DebugThread();

		if (IT == null)
			IT = new ItemThread(this, Common.GAME_SINGLEPLAYER, map, alloweditems);
		if (GT == null)
			GT = new GameThread(this, Common.GAME_SINGLEPLAYER, map, playerSelf);
		if (Build.VERSION.SDK_INT >= 11) {
			if (GTT != true) {
				GT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				GTT = true;
			}
			if (ITT != true) {
				IT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				ITT = true;
			}
		} else {
			if (GTT != true) {
				GT.execute();
				GTT = true;
			}
			if (ITT != true) {
				IT.execute();
				ITT = true;
			}
		}

		gamePlane.invalidate();
		mapPlane.invalidate();
		pointPlane.invalidate();
		notifPlane.invalidate();

	}

	@SuppressLint("SimpleDateFormat")
	public void endGame() {

		state = Common.STATE_SCORE;

		if (IT != null || ITT == true) {
			IT.cancel(true);
		}
		if (GT != null || GTT == true) {
			GT.cancel(true);
		}
		notifPlane.gotKilled();
		notifPlane.endGame();
		notifPlane.invalidate();

		// Daten fŸür die Highscore
		PreferencesManager pm = new PreferencesManager(this);
		String name = "";
		if (pm.getPreferenceString("sp_userName") == null || pm.getPreferenceString("sp_userName").trim().equals("")) {
			name = "Spieler 1";
		} else {
			name = pm.getPreferenceString("sp_userName");
		}
		int points = playerSelf.getCurrentPoints();
		String user = name;
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
		String time = sdf.format(new Date(System.currentTimeMillis()));
		sdf = new SimpleDateFormat("HH:mm");
		time = time + " um " + sdf.format(new Date(System.currentTimeMillis())) + " Uhr";

		DatabaseHandler db = new DatabaseHandler(this);
		db.addNewHighscore(points, user, time);
		Log.i("DB", "Wrote Score (" + points + ") in DB!");

	}

	public boolean playersAlive() {
		if (!players.isEmpty()) {
			for (int i = 0; i < players.size(); i++) {
				if (players.elementAt(i).isDead() != true)
					return true;
			}
		}

		endGame();

		return false;
	}

	public void invalidate() {
		gamePlane.invalidate();
		pointPlane.invalidate();
	}

	public void startDelayedThread(long t) {
		DelayedThread DT = new DelayedThread(this, t);
		if (Build.VERSION.SDK_INT >= 11)
			DT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			DT.execute();
	}

	public void onProgressUpdate(Void... values) {
		invalidate();
		playersAlive();
	}

	public boolean onTouchEvent(MotionEvent event) {
		return movementListener.onTouchEvent(event);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			if (state >= Common.STATE_DEAD)
				this.finish();

			long now = System.currentTimeMillis();
			if ((now - cancelTime) < 1000)
				this.finish();

			cancelTime = now;
			return true;
		}

		// ---this event has not been handled---
		return false;
	}

	@SuppressLint("HandlerLeak")
	private class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if ((msg.what == MoveListener.FLING_MOVEMENT || msg.what == MoveListener.GYRO_MOVEMENT) && state == Common.STATE_LIVE) {
				if (msg.arg1 == 0) {
					if (msg.arg2 == 1) {
						if (playerSelf.getX() > 0) {
							map.movePlayer(playerSelf, 1);
						}
					} else {
						if (playerSelf.getX() < map.getMapSize() - 1) {
							map.movePlayer(playerSelf, 2);
						}
					}
				}
				if (msg.arg2 == 0) {
					if (msg.arg1 == 1) {
						if (playerSelf.getY() > 0) {
							map.movePlayer(playerSelf, 3);
						}
					} else {
						if (playerSelf.getY() < map.getMapSize() - 1) {
							map.movePlayer(playerSelf, 4);
						}
					}
				}

				gamePlane.invalidate();
				pointPlane.invalidate();
			}

			if (msg.what == MoveListener.SINGLE_TAP && state != Common.STATE_WARMUP && state != Common.STATE_LIVE) {
				if (notifPlane.clickButton(msg.arg1, msg.arg2) == 1) {
					SingleplayerActivity.this.finish();
				}
				if (notifPlane.clickButton(msg.arg1, msg.arg2) == 2 && !retButtonWasClicked) {

					retButtonWasClicked = true;
					Intent intent = new Intent(SingleplayerActivity.this.getBaseContext(), SingleplayerActivity.class);
					startActivity(intent);
					SingleplayerActivity.this.finish();
				}

				if (retButtonWasClicked) {
					Log.e("SP", "Der ret-Buttton wurde schon einmal gedrückt!");
				}
			}

			if (msg.what == MoveListener.DOUBLE_TAP && state == Common.STATE_LIVE) {
				map.useItem(playerSelf);

				gamePlane.invalidate();
				pointPlane.invalidate();
			}

		}

	}

	/**
	 * CountDown von 3 bis GO
	 * 
	 * @author Vasu
	 * 
	 */
	public class StartThread extends AsyncTask<String, Void, Vector<String[]>> {

		private int countdown = 5;

		public StartThread() {
			state = Common.STATE_WARMUP;
		}

		protected Vector<String[]> doInBackground(String... params) {

			while (countdown > 0) {

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
				}

				publishProgress();

				countdown--;

			}

			return null;
		}

		protected void onProgressUpdate(Void... values) {
			notifPlane.countDown();
		}

		protected void onPostExecute(Vector<String[]> Result) {
			startGame();
		}

	} // StartThread

	/**
	 * Macht ab und zu mal ein refresh
	 * 
	 * @author Vasu
	 * 
	 */
	public class DebugThread extends AsyncTask<String, Void, Vector<String[]>> {

		Random rand = new Random(System.currentTimeMillis());

		public DebugThread() {

		}

		protected Vector<String[]> doInBackground(String... params) {
			Log.v("DT", "ya");
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
			}

			return null;
		}

		protected void onPostExecute(Vector<String[]> Result) {

			invalidate();
			playersAlive();

			if (state == Common.STATE_LIVE) {

				DebugThread DT = new DebugThread();
				if (Build.VERSION.SDK_INT >= 11)
					DT.executeOnExecutor(THREAD_POOL_EXECUTOR);
				else
					DT.execute();

			}

		}

	} // DebugThread

}
