package de.thm.mrcraps.views;

import java.util.Vector;

import de.thm.mrcraps.Common;
import de.thm.mrcraps.R;
import de.thm.mrcraps.gamemap.fieldcontent.InterfaceFieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.Player;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Die View auf der gemalt wird
 * 
 * @author Vasu
 * 
 */
public class MapNotifView extends View {

	private Context context;

	// Overlay Stuff
	private boolean gameOver = false;
	private boolean gotKilled = false;
	private boolean wonGame = false;
	private boolean warmUp = true;
	private int countDown = 5;

	// Bitmaps
	private Bitmap player_1;
	private Bitmap player_2;
	private Bitmap player_3;
	private Bitmap player_4;

	// Mal-Stuff
	private Canvas canvas;
	private Paint paint;
	private Paint textPaint;
	private Paint textPaintSmall;
	private Typeface font;

	// Buttons
	private Rect retButton;
	private Rect rematchButton;

	private Vector<Player> playerList;

	private int conType = Common.GAME_SINGLEPLAYER;

	/**
	 * Zeigt Notifications, z. B. den CountDown sowie das Ergebniss
	 * 
	 * @param context
	 * @param attrs
	 */
	public MapNotifView(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.context = context;

		font = (Typeface) Typeface.createFromAsset(context.getAssets(), "fonts/Minecraftia.ttf");
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		paint.setARGB(255, 255, 255, 255);

		textPaint = new Paint();
		textPaint.setColor(Color.WHITE);
		textPaint.setTypeface(font);
		textPaint.setTextSize(150);

		textPaintSmall = new Paint();
		textPaintSmall.setColor(Color.WHITE);
		textPaintSmall.setTypeface(font);
		textPaintSmall.setTextSize(40);

		if (context instanceof MultiplayerActivity) {
			playerList = ((MultiplayerActivity) context).getPlayer();
			conType = Common.GAME_MULTIPLAYER;
		}

	}

	/**
	 * Hier findet das malen statt.
	 */
	@Override
	public void draw(Canvas c) {
		super.onDraw(c);
		canvas = c;

		if (retButton == null) {
			retButton = new Rect(canvas.getWidth() / 7, (int) (canvas.getHeight() / 1.6f), (int) (canvas.getWidth() / 2.3f), (int) (canvas.getHeight() / 1.2f));
		}

		if (rematchButton == null) {
			rematchButton = new Rect((int) (canvas.getWidth() / 1.8f), (int) (canvas.getHeight() / 1.6f), (int) (canvas.getWidth() / 1.2f), (int) (canvas.getHeight() / 1.2f));
		}

		if (context instanceof MultiplayerActivity && playerList == null) {
			playerList = ((MultiplayerActivity) context).getPlayer();
		}

		if (context instanceof MultiplayerActivity) {
			if (player_1 == null) {
				float height = canvas.getHeight() / 2 - canvas.getHeight() / 6 - 30;
				float iconSize = height / playerList.size() - playerList.size() * 5;
				player_1 = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_playerself), (int) iconSize);
			}
			if (player_2 == null) {
				float height = canvas.getHeight() / 2 - canvas.getHeight() / 6 - 30;
				float iconSize = height / playerList.size() - playerList.size() * 5;
				player_2 = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_player), (int) iconSize);
			}
			if (player_3 == null) {
				float height = canvas.getHeight() / 2 - canvas.getHeight() / 6 - 30;
				float iconSize = height / playerList.size() - playerList.size() * 5;
				player_3 = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_player), (int) iconSize);
			}
			if (player_4 == null) {
				float height = canvas.getHeight() / 2 - canvas.getHeight() / 6 - 30;
				float iconSize = height / playerList.size() - playerList.size() * 5;
				player_4 = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_player), (int) iconSize);
			}
		}

		if (warmUp) {

			Paint p = new Paint();
			p.setARGB(180, 50, 50, 50);
			float tl = textPaint.measureText(String.valueOf(countDown));
			while (tl > canvas.getWidth()) {
				float newtextsize = textPaint.getTextSize() - 6;
				textPaint.setTextSize(newtextsize);
				tl = textPaint.measureText(String.valueOf(countDown));
			}

			if (context instanceof SingleplayerActivity) {
				canvas.drawRect(0, canvas.getHeight() / 6, canvas.getWidth(), canvas.getHeight() / 2, p);
				canvas.drawText(countDown + "!", canvas.getWidth() / 2 - tl / 2, canvas.getHeight() / 2.4f, textPaint);
			}

			if (context instanceof MultiplayerActivity && playerList != null) {
				canvas.drawRect(0, canvas.getHeight() / 6, canvas.getWidth(), canvas.getHeight() / 2, p);
				canvas.drawText(countDown + "!", canvas.getWidth() / 4 - tl / 2, canvas.getHeight() / 2.4f, textPaint);

				for (int i = 0; i < playerList.size(); i++) {
					Player pl = playerList.elementAt(i);
					Bitmap player = null;
					if (pl.getNumber() == InterfaceFieldContent.PLAYER_0) {
						player = player_1;
					}
					if (pl.getNumber() == InterfaceFieldContent.PLAYER_1) {
						player = player_2;
					}
					if (pl.getNumber() == InterfaceFieldContent.PLAYER_2) {
						player = player_3;
					}
					if (pl.getNumber() == InterfaceFieldContent.PLAYER_3) {
						player = player_4;
					}
					if (player != null) {
						canvas.drawBitmap(player, canvas.getWidth() / 2, canvas.getHeight() / 6 + player.getHeight() * i, paint);
						canvas.drawText(pl.getName(), canvas.getWidth() / 2 + player.getWidth() + 15, canvas.getHeight() / 6 + player.getHeight() * i + player.getHeight() / 2, textPaintSmall);
					}
				}
			}

		}

		if (gotKilled) {
			String text = context.getString(R.string.failed); // der
																// anzuzeigende
																// Text
			if (wonGame)
				text = context.getString(R.string.won);
			Paint p = new Paint();
			p.setARGB(180, 50, 50, 50);
			float tl = textPaint.measureText(text);
			while (tl > canvas.getWidth()) {
				float newtextsize = textPaint.getTextSize() - 6;
				textPaint.setTextSize(newtextsize);
				tl = textPaint.measureText(text);
			}
			canvas.drawRect(0, canvas.getHeight() / 6, canvas.getWidth(), canvas.getHeight() / 2, p);
			canvas.drawText(text, canvas.getWidth() / 2 - tl / 2, canvas.getHeight() / 2.4f, textPaint);

			canvas.drawRect(retButton, p);

			// Die 2 Buttons
			String mainmenu = context.getString(R.string.mainmenue); // der
																		// anzuzeigende
																		// Text
			tl = textPaintSmall.measureText(mainmenu);
			while (tl > retButton.width()) {
				float newtextsize = textPaintSmall.getTextSize() - 6;
				textPaintSmall.setTextSize(newtextsize);
				tl = textPaintSmall.measureText(mainmenu);
			}
			canvas.drawText(mainmenu, retButton.width() / 2 - tl / 2 + retButton.left, retButton.top + retButton.height() / 2, textPaintSmall);

			// Neuer Versuch geht nur im Singleplayer
			if (conType == Common.GAME_SINGLEPLAYER) {

				if (gameOver)
					canvas.drawRect(rematchButton, p);

				String regame = context.getString(R.string.reGame); // der
																	// anzuzeigende
																	// Text
				tl = textPaintSmall.measureText(regame);
				while (tl > retButton.width()) {
					float newtextsize = textPaintSmall.getTextSize() - 6;
					textPaintSmall.setTextSize(newtextsize);
					tl = textPaintSmall.measureText(regame);
				}
				if (gameOver)
					canvas.drawText(regame, rematchButton.width() / 2 - tl / 2 + rematchButton.left, rematchButton.top + rematchButton.height() / 2, textPaintSmall);

			}

		}

	}

	public void gotKilled() {
		gotKilled = true;
		this.invalidate();
		Log.e("got", "killed");
	}

	public void winGame() {
		wonGame = true;
	}

	public void endGame() {
		Log.e("ended", "game");
		gotKilled = true;
		gameOver = true;
		this.invalidate();
	}

	public void countDown() {
		countDown--;
		if (countDown == 0) {
			warmUp = false;
		}
		this.invalidate();
	}

	public int clickButton(int x, int y) {
		if (retButton.contains(x, y))
			return 1;
		if (rematchButton.contains(x, y))
			return 2;
		return 0;
	}

	public Bitmap getResizedIcon(Bitmap icon, int newSize) {
		int width = icon.getWidth();
		int height = icon.getHeight();
		float scaleWidth = ((float) newSize) / width;
		float scaleHeight = ((float) newSize) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(icon, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

}
