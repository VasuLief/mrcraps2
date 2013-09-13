package de.thm.mrcraps.views;


import java.util.Random;
import java.util.Vector;

import de.thm.mrcraps.Common;
import de.thm.mrcraps.controllers.ConnectionManager;
import de.thm.mrcraps.controllers.CrapsMultiplayerInterface;
import de.thm.mrcraps.controllers.GameClientThread;
import de.thm.mrcraps.controllers.GameThread;
import de.thm.mrcraps.controllers.ItemThread;
import de.thm.mrcraps.controllers.MOWSchnittstellenInterface;
import de.thm.mrcraps.controllers.MoveListener;
import de.thm.mrcraps.controllers.PreferencesManager;
import de.thm.mrcraps.controllers.SchwierigkeitsManager;
import de.thm.mrcraps.gamemap.GameMap;
import de.thm.mrcraps.gamemap.fieldcontent.Arrow;
import de.thm.mrcraps.gamemap.fieldcontent.BananaItem;
import de.thm.mrcraps.gamemap.fieldcontent.CrapItem;
import de.thm.mrcraps.gamemap.fieldcontent.FieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.GlueItem;
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
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
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
public class MultiplayerActivity extends Activity {

	// Einstellungen
	private SharedPreferences settings;

	// MP Stuff
	private MultiplayerActivity mA;
	private ConnectionManager conMan;
	private Messenger receiver = new Messenger(new Receiver());

	// Stuff
	private DebugThread DT;
	private ItemThread IT;
	private GameThread GT;

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

	// MP related
	public static int plyr;
	public static String names;
	public static int myid;
	public int mapSize;

	// live
	public int state = Common.STATE_WAITING;
	public int conType = Common.GAME_HOST;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Erstellen der View
		setContentView(R.layout.layout_game);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Connection Stuff
		conMan = ConnectionManager.create(this);
		conMan.registerReceiver(receiver);

		plyr = this.getIntent().getExtras().getInt("player");
		names = this.getIntent().getExtras().getString("names");
		myid = this.getIntent().getExtras().getInt("myid");
		mapSize = this.getIntent().getExtras().getInt("mapSize");

		if (myid == 0) {
			conType = Common.GAME_HOST;
		} else {
			conType = Common.GAME_CLIENT;
		}

		if (conType == Common.GAME_HOST) {
			schwierigManag = SchwierigkeitsManager.getInstance();
			schwierigManag.reset();
			settings = new PreferencesManager(this).getSharedPreferencesObject();
		}

		// Laden des Players und der GameMap
		players = new Vector<Player>();
		String[] theNames = names.split(";");
		map = new GameMap(GameMap.MODE_MULTIPLAYER, mapSize);
		int xPos = (int) Math.ceil(mapSize / 2) - (int) Math.ceil(plyr / 2);
		int yPos = (int) Math.ceil(mapSize / 2);
		for (int i = 0; i < plyr; i++) {
			Player pl;
			if(theNames.length>=i){
			pl = new Player(xPos, yPos, i, theNames[i]);
			}else{
			pl = new Player(xPos, yPos, i, "Player #"+i);
			}
			if (myid == i) {
				playerSelf = pl;
			}
			players.add(pl);
			map.addPlayer(pl, xPos++, yPos);

		}
		map.setPlayerSelf(playerSelf.getNumber());
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

		// für die Steuerung per Accelerometer
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		msgHandler = new MessageHandler();
		movementListener = new MoveListener(msgHandler, this);
		mA = this;

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

		state = Common.STATE_WAITING;
	}

	public Vector<Player> getPlayer() {
		return players;
	}

	public void startGame() {

		Log.e("SA", "start");

		state = Common.STATE_LIVE;

		if (DT == null)
			DT = new DebugThread();
		// DT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		if (conType == Common.GAME_HOST) {

			boolean[] alloweditems = new boolean[3];
			alloweditems[0] = settings.getBoolean("mp_item_Banane", true);
			alloweditems[1] = settings.getBoolean("mp_item_Kleber", true);
			alloweditems[2] = settings.getBoolean("mp_item_Crap", true);

			if (IT == null && conType != Common.GAME_CLIENT)
				IT = new ItemThread(this, Common.GAME_MULTIPLAYER, map, alloweditems);
			if (GT == null && conType != Common.GAME_CLIENT)
				GT = new GameThread(this, Common.GAME_MULTIPLAYER, map, playerSelf);
			if (Build.VERSION.SDK_INT >= 11 && conType != Common.GAME_CLIENT) {
				GT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				IT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				GT.execute();
				IT.execute();
			}

		}

		mapPlane.invalidate();
		gamePlane.invalidate();
		pointPlane.invalidate();
		notifPlane.invalidate();

	}

	public void winGame() {
		notifPlane.winGame();
		endGame();
	}

	public void endGame() {

		state = Common.STATE_SCORE;
		notifPlane.endGame();
		notifPlane.invalidate();
		if (IT != null && conType != Common.GAME_CLIENT) {
			IT.cancel(true);
		}
		if (GT != null && conType != Common.GAME_CLIENT) {
			GT.cancel(true);
		}

	}

	public boolean playersAlive() {
		if (!players.isEmpty()) {
			for (int i = 0; i < players.size(); i++) {
				if (players.elementAt(i).isDead() != true) {
					if (players.size() != 1) {
						if (playerSelf.getNumber() != i)
							return true;
					} else {
						return true;
					}

				}

			}
		}
		if (!playerSelf.isDead() && players.size() != 1) {
			winGame();
		}
		endGame();
		return false;
	}

	public void invalidate() {
		gamePlane.invalidate();
		pointPlane.invalidate();
	}

	public void onProgressUpdate(Void... values) {
		if (playerSelf.isDead()) {
			notifPlane.gotKilled();
			playersAlive();
			Bundle b = new Bundle();
			b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
			b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
			b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
			b.putInt(CrapsMultiplayerInterface.PLAYER_DOES, CrapsMultiplayerInterface.PLAYER_DIE);
			conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
			state = Common.STATE_DEAD;
		}
		playersAlive();
		invalidate();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			if (state >= Common.STATE_DEAD)
				this.finish();

			long now = System.currentTimeMillis();
			if ((now - cancelTime) < 1000) {

				// Ich sage jedem, das ich gestorben bin
				Bundle b = new Bundle();
				b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
				b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
				b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
				b.putInt(CrapsMultiplayerInterface.PLAYER_DOES, CrapsMultiplayerInterface.PLAYER_DIE);
				conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);

				if (conType == Common.GAME_HOST) {
					// Dann, wenn ich der Host bin, ist das Spiel vorbei
					endGame();
				}

				// dann sag ich bescheid, das ich aussteige
				// Wenn ich Host bin, wird es bei jedem das Spiel beenden
				Message msg = Message.obtain(null, MOWSchnittstellenInterface.MESSAGE_5_CLOSE);
				conMan.sendMessage(msg);

				this.finish();
			}

			cancelTime = now;
			return true;
		}

		// ---this event has not been handled---
		return false;
	}

	public void finish(){
		Intent resultIntent = new Intent();
		resultIntent.putExtra("closing", "destroy ConMan");
		setResult(Activity.RESULT_OK, resultIntent);
		super.finish();
	}
	public boolean onTouchEvent(MotionEvent event) {
		return movementListener.onTouchEvent(event);
	}

	@SuppressLint("HandlerLeak")
	private class Receiver extends Handler {
		@Override
		public void handleMessage(Message msg) {

			Log.e("Multi", "Da ist etwas reingekommen! ->" + String.valueOf(msg.what));

			Bundle bundle = msg.getData();
			bundle.setClassLoader(getClassLoader());
			String msgCode = bundle.getString(MOWSchnittstellenInterface.GET_TYPE);

			switch (msg.what) {
			case (MOWSchnittstellenInterface.MESSAGE_2_CONNECTION): //
				// Sollte nicht mehr passieren in dieser Phase
				break;

			case (MOWSchnittstellenInterface.MESSAGE_4_GET): //
				byte[] data = bundle.getByteArray(MOWSchnittstellenInterface.GET_DATA);
				Log.v("MP", "Nachricht 4 Größe: " + data.length);
				Parcel parcel = Parcel.obtain();
				parcel.unmarshall(data, 0, data.length);
				parcel.setDataPosition(0);
				Bundle resultBundle = Bundle.CREATOR.createFromParcel(parcel);
				resultBundle.setClassLoader(getClassLoader());
				// wir trennen die Nachrichten hier
				if (msgCode != null) {
					if (msgCode.equals(CrapsMultiplayerInterface.PLAYER_DOES)) {
						Player p = players.elementAt(resultBundle.getInt(CrapsMultiplayerInterface.PLAYER_NUMBER));
						int x = resultBundle.getInt(CrapsMultiplayerInterface.POS_X);
						int y = resultBundle.getInt(CrapsMultiplayerInterface.POS_Y);
						map.movePlayerToPosition(p, x, y);
						if (resultBundle.getInt(CrapsMultiplayerInterface.PLAYER_DOES) == CrapsMultiplayerInterface.PLAYER_GET_ITEM) {
							int type = resultBundle.getInt(CrapsMultiplayerInterface.ITEM_TYPE);
							if (type == InterfaceFieldContent.ITEM_BANANA) {
								BananaItem item = new BananaItem(-1, x, y);
								p.grabItem(item);
							}
							if (type == InterfaceFieldContent.ITEM_CRAP) {
								CrapItem item = new CrapItem(-1, x, y);
								p.grabItem(item);
							}
							if (type == InterfaceFieldContent.ITEM_GLUE) {
								GlueItem item = new GlueItem(-1, x, y);
								p.grabItem(item);
							}
							if (map.getGameField(x, y).hasContent())
								map.getGameField(x, y).expireContent();
						}
						if (resultBundle.getInt(CrapsMultiplayerInterface.PLAYER_DOES) == CrapsMultiplayerInterface.PLAYER_USE_ITEM) {
							int type = resultBundle.getInt(CrapsMultiplayerInterface.ITEM_TYPE);
							if (type == InterfaceFieldContent.ITEM_BANANA) {
								BananaItem item = new BananaItem(-1, x, y);
								p.grabItem(item);
							}
							if (type == InterfaceFieldContent.ITEM_CRAP) {
								CrapItem item = new CrapItem(-1, x, y);
								p.grabItem(item);
							}
							if (type == InterfaceFieldContent.ITEM_GLUE) {
								GlueItem item = new GlueItem(-1, x, y);
								p.grabItem(item);
							}
							map.spawnItem(p.getItem());
							//map.getGameField(x, y).placeItem(p.getItem());
							//map.getGameField(x, y).getItem().useItem();
							p.useItem();
						}
						if (resultBundle.getInt(CrapsMultiplayerInterface.PLAYER_DOES) == CrapsMultiplayerInterface.PLAYER_DIE) {
							Log.e("player", "dead");
							if (map.getGameField(x, y).hasPlayer())
								map.getGameField(x, y).killPlayer();
							p.kill();
							playersAlive();
						}
					}

					if (msgCode.equals(CrapsMultiplayerInterface.ITEMS_THREAD)) {
						long duration = resultBundle.getLong(CrapsMultiplayerInterface.ITEMS_EXPTIME);
						long expTime = duration + System.currentTimeMillis();
						Log.e("itemsdur_msg", "" + duration);
						if (resultBundle.getBoolean(CrapsMultiplayerInterface.ITEMS_BANANA_BOOL)) {
							Parcelable[] bananaItems = resultBundle.getParcelableArray(CrapsMultiplayerInterface.ITEMS_BANANA);
							BananaItem[] resultArray = null;
							if (bananaItems != null) {
								resultArray = new BananaItem[bananaItems.length];
								for (int i = 0; i < bananaItems.length; ++i) {
									resultArray[i] = (BananaItem) bananaItems[i];
									resultArray[i].setExpTime(expTime);
								}
							}
							for (int bananaCount = 0; bananaCount < resultArray.length; bananaCount++) {
								map.spawnItem(resultArray[bananaCount]);
							}
						}
						if (resultBundle.getBoolean(CrapsMultiplayerInterface.ITEMS_GLUE_BOOL)) {
							Parcelable[] glueItems = resultBundle.getParcelableArray(CrapsMultiplayerInterface.ITEMS_GLUE);

							GlueItem[] resultArray = null;
							if (glueItems != null) {
								resultArray = new GlueItem[glueItems.length];
								for (int i = 0; i < glueItems.length; ++i) {
									resultArray[i] = (GlueItem) glueItems[i];
									resultArray[i].setExpTime(expTime);
								}
							}

							for (int glueCount = 0; glueCount < resultArray.length; glueCount++) {
								map.spawnItem(resultArray[glueCount]);
							}
						}
						if (resultBundle.getBoolean(CrapsMultiplayerInterface.ITEMS_CRAP_BOOL)) {
							Parcelable[] crapItems = resultBundle.getParcelableArray(CrapsMultiplayerInterface.ITEMS_CRAP);

							CrapItem[] resultArray = null;
							if (crapItems != null) {
								resultArray = new CrapItem[crapItems.length];
								for (int i = 0; i < crapItems.length; ++i) {
									resultArray[i] = (CrapItem) crapItems[i];
									resultArray[i].setExpTime(expTime);
								}
							}

							for (int crapCount = 0; crapCount < resultArray.length; crapCount++) {
								map.spawnItem(resultArray[crapCount]);
							}
						}
						invalidate();
					}

					if (msgCode.equals(CrapsMultiplayerInterface.GAME_THREAD)) {

						long arrowExpTime = (long) resultBundle.getLong(CrapsMultiplayerInterface.ARROW_EXPTIME);
						long fireExpTime = (long) resultBundle.getLong(CrapsMultiplayerInterface.FIRE_EXPTIME);
						Parcelable[] parcelableArray = resultBundle.getParcelableArray(CrapsMultiplayerInterface.ARROW);
						Arrow[] resultArray = null;
						if (parcelableArray != null) {
							resultArray = new Arrow[parcelableArray.length];
							long sysTime = System.currentTimeMillis();
							for (int i = 0; i < parcelableArray.length; ++i) {
								resultArray[i] = (Arrow) parcelableArray[i];
								resultArray[i].setExpTime(sysTime + arrowExpTime);
							}
						}

						GameClientThread gThread = new GameClientThread(mA, map, resultArray, arrowExpTime, fireExpTime);
						if (Build.VERSION.SDK_INT >= 11) {
							gThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						} else {
							gThread.execute();
						}

					}
				}
				break;

			case (MOWSchnittstellenInterface.MESSAGE_6_CLOSED): //

				int id = bundle.getInt(MOWSchnittstellenInterface.CLOSED_BY);
				Log.e("MP", "Spieler " + String.valueOf(id) + " ist gerade ausgestiegen");

				// Der Host hat das Spiel beendet();
				if (id == 0) {
					endGame();
				}

				break;
			}

			MultiplayerActivity.this.invalidate();

		}
	}

	@SuppressLint("HandlerLeak")
	private class MessageHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			boolean hasItem = playerSelf.hasItem();
			if ((msg.what == MoveListener.FLING_MOVEMENT || msg.what == MoveListener.GYRO_MOVEMENT) && state == Common.STATE_LIVE) {
				if (msg.arg1 == 0) {
					if (msg.arg2 == 1) {
						if (playerSelf.getX() > 0) { // Links
							if (map.getGameField(playerSelf.getX() - 1, playerSelf.getY()).getContent() == InterfaceFieldContent.ITEM_BANANA) {
								Log.e("is", "banana");
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX() - 1);
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}
							if (map.movePlayer(playerSelf, 1)) {
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}

						}
					} else {
						if (playerSelf.getX() < map.getMapSize() - 1) {// Rechts
							if (map.getGameField(playerSelf.getX() + 1, playerSelf.getY()).getContent() == InterfaceFieldContent.ITEM_BANANA) {
								Log.e("is", "banana");
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX() + 1);
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}
							if (map.movePlayer(playerSelf, 2)) {
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}

						}
					}
				}
				if (msg.arg2 == 0) {
					if (msg.arg1 == 1) {
						if (playerSelf.getY() > 0) {// Up
							if (map.getGameField(playerSelf.getX(), playerSelf.getY() - 1).getContent() == InterfaceFieldContent.ITEM_BANANA) {
								Log.e("is", "banana");
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY() - 1);
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}
							if (map.movePlayer(playerSelf, 3)) {
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}

						}
					} else {
						if (playerSelf.getY() < map.getMapSize() - 1) {// Down
							if (map.getGameField(playerSelf.getX(), playerSelf.getY() + 1).getContent() == InterfaceFieldContent.ITEM_BANANA) {
								Log.e("is", "banana");
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY() + 1);
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}
							if (map.movePlayer(playerSelf, 4)) {
								Bundle b = new Bundle();
								b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
								b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
								b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
								conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
							}

						}
					}
				}
			}

			Log.e("playerSelf", "Gotitem:" + playerSelf.hasItem());
			if (playerSelf.hasItem() != hasItem) {
				Bundle b = new Bundle();
				b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
				b.putInt(CrapsMultiplayerInterface.PLAYER_DOES, CrapsMultiplayerInterface.PLAYER_GET_ITEM);
				Log.e("playerPos", "X:" + playerSelf.getX() + "Y:" + playerSelf.getY());
				b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
				b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
				b.putInt(CrapsMultiplayerInterface.ITEM_TYPE, playerSelf.getItem().getContent());
				conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
				Log.e("playerself", "sending i got");
			}
			gamePlane.invalidate();
			pointPlane.invalidate();

			if (msg.what == MoveListener.SINGLE_TAP && state != Common.STATE_WARMUP && state != Common.STATE_LIVE) {
				if (notifPlane.clickButton(msg.arg1, msg.arg2) == 1) {
					MultiplayerActivity.this.finish();
				}
				if (notifPlane.clickButton(msg.arg1, msg.arg2) == 2) {
					Intent intent = new Intent(MultiplayerActivity.this.getBaseContext(), MultiplayerActivity.class);
					startActivity(intent);
					MultiplayerActivity.this.finish();
				}
			}

			if (msg.what == MoveListener.DOUBLE_TAP && state == Common.STATE_LIVE) {
				if (playerSelf.hasItem()) {
					FieldContent item = playerSelf.getItem();
					item.useItem();
					map.useItem(playerSelf);
					playerSelf.useItem();
					if (item.getContent() == InterfaceFieldContent.ITEM_GLUE)
						((GlueItem) item).deActivate();
					Log.e("usingitem", "" + item.getContent());
					Bundle b = new Bundle();
					b.putInt(CrapsMultiplayerInterface.PLAYER_NUMBER, playerSelf.getNumber());
					b.putInt(CrapsMultiplayerInterface.POS_X, playerSelf.getX());
					b.putInt(CrapsMultiplayerInterface.POS_Y, playerSelf.getY());
					b.putInt(CrapsMultiplayerInterface.ITEM_TYPE, item.getContent());
					b.putInt(CrapsMultiplayerInterface.PLAYER_DOES, CrapsMultiplayerInterface.PLAYER_USE_ITEM);
					conMan.sendBundleAsMessage3(b, -1, CrapsMultiplayerInterface.PLAYER_DOES);
				}

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
				} catch (InterruptedException e) {
				}

				if(state == Common.STATE_WARMUP){
					publishProgress();
				}

				countdown--;

			}

			return null;
		}

		protected void onProgressUpdate(Void... values) {
			notifPlane.countDown();
		}

		protected void onPostExecute(Vector<String[]> Result) {
			if(state == Common.STATE_WARMUP){
				startGame();
			}
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
			Log.e("DT", "ya");
			try {
				Thread.sleep(50);
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