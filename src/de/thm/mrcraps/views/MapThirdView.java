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
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

/**
 * Die View auf der gemalt wird
 * 
 * @author Vasu
 * 
 */
public class MapThirdView extends View {

	private Context context;

	// Mal-Stuff
	private Canvas canvas;
	private Paint paint;
	private Paint textPaint;
	private Paint pRed;
	private Paint pGreen;

	// Bitmaps
	private Bitmap player_1;
	private Bitmap player_2;
	private Bitmap player_dead;
	private Bitmap giftItem;
	private Bitmap crapItem;
	private Bitmap bananaItem;
	private Bitmap glueItem;

	//
	private Typeface font;
	private Player player;

	private Vector<Player> playerList;

	private int conType = Common.GAME_SINGLEPLAYER;

	public MapThirdView(Context context, AttributeSet attrs) {
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
		textPaint.setTextSize(12);

		pRed = new Paint();
		pRed.setColor(Color.RED);

		pGreen = new Paint();
		pGreen.setColor(Color.GREEN);

		if (context instanceof MultiplayerActivity) {
			playerList = ((MultiplayerActivity) context).getPlayer();
			conType = Common.GAME_MULTIPLAYER;
		}

	}

	// Hier findet das malen statt.
	@Override
	public void draw(Canvas c) {
		super.onDraw(c);
		canvas = c;

		if (conType == Common.GAME_MULTIPLAYER && playerList == null) {
			playerList = ((MultiplayerActivity) context).getPlayer();
		}

		if (conType == Common.GAME_MULTIPLAYER) {
			if (player_1 == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				player_1 = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_playerself), (int) iconSize);
			}
			if (player_2 == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				player_2 = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_player), (int) iconSize);
			}
			if (player_dead == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				player_dead = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_skull), (int) iconSize);
			}

			if (giftItem == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				giftItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_gift), (int) iconSize);
			}
			if (crapItem == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				crapItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_crap), (int) iconSize);
			}
			if (bananaItem == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				bananaItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_banana), (int) iconSize);
			}
			if (glueItem == null) {
				float height = (canvas.getWidth() - canvas.getHeight()) / 2 - 10;
				float iconSize = height / 3;
				glueItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_glue), (int) iconSize);
			}
		}

		// Nur wenn man im Multiplayer ist
		if (conType == Common.GAME_MULTIPLAYER) {

			for (int i = 0; i < playerList.size(); i++) {

				if (i == InterfaceFieldContent.PLAYER_0) {
					canvas.drawRect(0, 0, 10, canvas.getHeight() / 2, getProperColor(i));
					canvas.drawBitmap(getProperIcon(i), 20, 20, paint);
					canvas.drawText(playerList.elementAt(i).getName(), getProperIcon(i).getWidth() + 30, 50, textPaint);
					if (getProperItem(i) != null)
						canvas.drawBitmap(getProperItem(i), 20, getProperIcon(i).getHeight() + 20, paint);
				}

				if (i == InterfaceFieldContent.PLAYER_1) {
					canvas.drawRect(canvas.getWidth() - 10, 0, canvas.getWidth(), canvas.getHeight() / 2, getProperColor(i));
					canvas.drawBitmap(getProperIcon(i), canvas.getWidth() / 2 + canvas.getHeight() / 2 + 10, 20, paint);
					canvas.drawText(playerList.elementAt(i).getName(), canvas.getWidth() / 2 + canvas.getHeight() / 2 + getProperIcon(i).getWidth() + 30, 50, textPaint);
					if (getProperItem(i) != null)
						canvas.drawBitmap(getProperItem(i), canvas.getWidth() / 2 + canvas.getHeight() / 2 + 20, getProperIcon(i).getHeight() + 20, paint);
				}

				if (i == InterfaceFieldContent.PLAYER_2) {
					canvas.drawRect(0, canvas.getHeight() / 2, 10, canvas.getHeight(), getProperColor(i));
					canvas.drawBitmap(getProperIcon(i), 20, canvas.getHeight() / 2 + 20, paint);
					canvas.drawText(playerList.elementAt(i).getName(), getProperIcon(i).getWidth() + 30, canvas.getHeight() / 2 + 50, textPaint);
					if (getProperItem(i) != null)
						canvas.drawBitmap(getProperItem(i), 20, getProperIcon(i).getHeight() + canvas.getHeight() / 2 + 20, paint);
				}

				if (i == InterfaceFieldContent.PLAYER_3) {
					canvas.drawRect(canvas.getWidth() - 10, 0, canvas.getWidth(), canvas.getHeight(), getProperColor(i));
					canvas.drawBitmap(getProperIcon(i), canvas.getWidth() / 2 + canvas.getHeight() / 2 + 10, canvas.getHeight() / 2 + 20, paint);
					canvas.drawText(playerList.elementAt(i).getName(), canvas.getWidth() / 2 + canvas.getHeight() / 2 + getProperIcon(i).getWidth() + 30, 50, textPaint);
					if (getProperItem(i) != null)
						canvas.drawBitmap(getProperItem(i), canvas.getWidth() / 2 + canvas.getHeight() / 2 + 20, getProperIcon(i).getHeight() + canvas.getHeight() / 2 + 20, paint);
				}

			}
		}

		// Nur wenn man im Singleplayer ist
		if (conType == Common.GAME_SINGLEPLAYER) {

			canvas.drawText("Aktueller Punktestand: ", 20, 40, textPaint);

			if (player != null) {
				int points = player.getCurrentPoints();

				if (points == 1) {
					canvas.drawText(points + " Punkt", 20, 70, textPaint);
				} else {
					canvas.drawText(points + " Punkte", 20, 70, textPaint);
				}

				canvas.drawText("Aktuelle Moves: " + player.getMoveCount(), 20, 100, textPaint);
			}
		}

	}

	public Paint getProperColor(int id) {
		if (playerList.elementAt(id).isDead())
			return pRed;
		else
			return pGreen;
	}

	public Bitmap getProperIcon(int id) {
		if (playerList.elementAt(id).isDead())
			return player_dead;
		if (id == player.getNumber())
			return player_1;
		else
			return player_2;
	}

	public Bitmap getProperItem(int id) {
		if (playerList.elementAt(id).isDead())
			return null;
		if (!playerList.elementAt(id).hasItem())
			return null;
		if (id == player.getNumber()) {
			if (playerList.elementAt(id).getItem().getContent() == InterfaceFieldContent.ITEM_BANANA)
				return bananaItem;
			if (playerList.elementAt(id).getItem().getContent() == InterfaceFieldContent.ITEM_CRAP)
				return crapItem;
			if (playerList.elementAt(id).getItem().getContent() == InterfaceFieldContent.ITEM_GLUE)
				return glueItem;
		} else {
			return giftItem;
		}
		return null;
	}

	public void setPlayer(Player player) {
		this.player = player;
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
