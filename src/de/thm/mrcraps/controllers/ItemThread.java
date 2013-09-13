package de.thm.mrcraps.controllers;


import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import de.thm.mrcraps.Common;
import de.thm.mrcraps.gamemap.GameMap;
import de.thm.mrcraps.gamemap.fieldcontent.BananaItem;
import de.thm.mrcraps.gamemap.fieldcontent.CrapItem;
import de.thm.mrcraps.gamemap.fieldcontent.GlueItem;
import de.thm.mrcraps.views.MultiplayerActivity;
import de.thm.mrcraps.views.SingleplayerActivity;

/**
 * Der ItemThread spawnt die Items
 * 
 * @author Vasu
 * 
 */
public class ItemThread extends AsyncTask<String, Void, Vector<String[]>> {

	private int mode;

	private final int MAX_ITEM_SLEEPTIME = 5000;
	private final int MIN_ITEM_SLEEPTIME = 2000;
	private final int MAX_ITEM_EXPTIME = 12000;
	private final int MIN_ITEM_EXPTIME = 7000;
	private SchwierigkeitsManager schwierigManag;
	Random rand = new Random(System.currentTimeMillis());
	private boolean[] allowedItems;
	private Activity sa;
	private GameMap map;
	private final int MIN_ITEMCOUNT = 1;
	private ConnectionManager conMan;

	public ItemThread(Activity c, int md, GameMap m, boolean[] allowedItems) {

		schwierigManag = SchwierigkeitsManager.getInstance();
		this.sa = c;
		this.mode = md;
		this.map = m;
		this.allowedItems = allowedItems;
		this.conMan = ConnectionManager.create(c.getApplicationContext());

	}

	protected Vector<String[]> doInBackground(String... params) {
		// Solange spieler noch am Leben sind

		Log.e("IT", "gestartet");

		while (true) {

			try {
				Thread.sleep(rand.nextInt(MAX_ITEM_SLEEPTIME - MIN_ITEM_SLEEPTIME) + MIN_ITEM_SLEEPTIME);
			} catch (InterruptedException e) {
			}

			Random rand = new Random();
			int mapSize = map.getMapSize();
			int x = rand.nextInt(mapSize);
			int y = rand.nextInt(mapSize);
			while (map.getGameField(x, y).hasPlayer() || map.getGameField(x, y).hasContent()) {
				x = rand.nextInt(mapSize);
				y = rand.nextInt(mapSize);
			}
			long duration = rand.nextInt(MAX_ITEM_EXPTIME - MIN_ITEM_EXPTIME) + MIN_ITEM_EXPTIME;
			long expTime = duration + System.currentTimeMillis();
			int itemCount = rand.nextInt(schwierigManag.getMaxSchwierig()) + MIN_ITEMCOUNT;
			if (schwierigManag.getMaxSchwierig() > map.getMapSize()) {
				itemCount = map.getMapSize();
			}
			;
			Vector<BananaItem> bananaItems = new Vector<BananaItem>();
			Vector<CrapItem> crapItems = new Vector<CrapItem>();
			Vector<GlueItem> glueItems = new Vector<GlueItem>();

			for (int count = 0; count < itemCount; count++) {

				int randomItem = rand.nextInt(3);
				while (!allowedItems[randomItem]) {
					randomItem = rand.nextInt(3);
				}
				if (randomItem == 0) {
					BananaItem item = new BananaItem(expTime, x, y);
					if (map.getPlayer().size() == 1) {
						item.useItem();
					}
					bananaItems.add(item);
					map.spawnItem(item);
				}
				if (randomItem == 1) {
					GlueItem item = new GlueItem(expTime, x, y);
					if (map.getPlayer().size() == 1) {
						item.useItem();
					}
					glueItems.add(item);
					map.spawnItem(item);
				}
				if (randomItem == 2) {
					CrapItem item = new CrapItem(expTime, x, y);
					if (map.getPlayer().size() == 1) {
						item.useItem();
					}
					crapItems.add(item);
					map.spawnItem(item);
				}

				publishProgress();

			}// for

			if (mode == Common.GAME_MULTIPLAYER) {
				Bundle dataBundle = new Bundle();
				if (!crapItems.isEmpty()) {
					dataBundle.putParcelableArray(CrapsMultiplayerInterface.ITEMS_CRAP, (CrapItem[]) crapItems.toArray(new CrapItem[crapItems.size()]));
					dataBundle.putBoolean(CrapsMultiplayerInterface.ITEMS_CRAP_BOOL, true);
				}
				if (!bananaItems.isEmpty()) {
					dataBundle.putParcelableArray(CrapsMultiplayerInterface.ITEMS_BANANA, (BananaItem[]) bananaItems.toArray(new BananaItem[bananaItems.size()]));
					dataBundle.putBoolean(CrapsMultiplayerInterface.ITEMS_BANANA_BOOL, true);
				}
				if (!glueItems.isEmpty()) {
					dataBundle.putParcelableArray(CrapsMultiplayerInterface.ITEMS_GLUE, (GlueItem[]) glueItems.toArray(new GlueItem[glueItems.size()]));
					dataBundle.putBoolean(CrapsMultiplayerInterface.ITEMS_GLUE_BOOL, true);
				}
				dataBundle.putLong(CrapsMultiplayerInterface.ITEMS_EXPTIME, duration);
				conMan.sendBundleAsMessage3(dataBundle, -1, CrapsMultiplayerInterface.ITEMS_THREAD);
			}

			if (mode == Common.GAME_SINGLEPLAYER) {
				if (((SingleplayerActivity) sa).state != Common.STATE_LIVE) {
					break;
				}

			}
			if (mode == Common.GAME_MULTIPLAYER) {
				if (((MultiplayerActivity) sa).state != Common.STATE_LIVE) {
					break;
				}
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		super.onProgressUpdate(values);
		if (mode == Common.GAME_SINGLEPLAYER)
			((SingleplayerActivity) sa).onProgressUpdate(values);
		if (mode == Common.GAME_MULTIPLAYER)
			((MultiplayerActivity) sa).onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Vector<String[]> Result) {

	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

} // ItemThread