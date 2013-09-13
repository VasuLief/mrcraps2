package de.thm.mrcraps.views;

import de.thm.mrcraps.R;
import de.thm.mrcraps.gamemap.BorderField;
import de.thm.mrcraps.gamemap.GameField;
import de.thm.mrcraps.gamemap.GameMap;
import de.thm.mrcraps.gamemap.fieldcontent.Arrow;
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
import android.view.View;

/**
 * Die View auf der gemalt wird
 * 
 * @author Vasu
 * 
 */
public class MapVordergrundView extends View {

	// Die View kennt die Map und bekommt daher ihre Informationen
	private GameMap gMap;
	private int mapSize;
	private int mapPadding = 0; // breite des randes in Pixel

	// Mal-Stuff
	private Canvas canvas;
	private Paint paint;
	private Paint textPaint;
	private Paint textPaintSmall;
	private Context context;

	private Bitmap giftItem;
	private Bitmap crapItem;
	private Bitmap bananaItem;
	private Bitmap glueItem;
	private Bitmap player;
	private Bitmap playerself;
	private Bitmap fire;
	private Bitmap death;
	private Bitmap arrowRightRed;
	private Bitmap arrowLeftRed;
	private Bitmap arrowUpRed;
	private Bitmap arrowDownRed;
	private Bitmap arrowRightYellow;
	private Bitmap arrowLeftYellow;
	private Bitmap arrowUpYellow;
	private Bitmap arrowDownYellow;
	private Bitmap arrowRightGreen;
	private Bitmap arrowLeftGreen;
	private Bitmap arrowUpGreen;
	private Bitmap arrowDownGreen;

	private Typeface font;
	private Player playerObj;

	// Buttons
	Rect retButton;
	Rect rematchButton;

	public MapVordergrundView(Context context, AttributeSet attrs) {
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

	}

	public void setGameMap(GameMap map) {
		gMap = map;
	}

	public void setPlayer(Player player) {
		playerObj = player;
	}

	// Hier findet das malen statt.
	@Override
	public void draw(Canvas c) {
		super.onDraw(c);
		canvas = c;
		// long time = System.currentTimeMillis();

		if (c != null && gMap != null) {
			mapSize = gMap.getMapSize();
			int fieldWidth = c.getHeight() / (mapSize + 1) - mapPadding;
			int fieldHeight = c.getHeight() / (mapSize + 1) - mapPadding;
			int offsetL = (c.getWidth() - c.getHeight()) / 2;

			if (retButton == null) {
				retButton = new Rect(canvas.getWidth() / 7, (int) (canvas.getHeight() / 1.6f), (int) (canvas.getWidth() / 2.3f), (int) (canvas.getHeight() / 1.2f));
			}
			if (rematchButton == null) {
				rematchButton = new Rect((int) (canvas.getWidth() / 1.8f), (int) (canvas.getHeight() / 1.6f), (int) (canvas.getWidth() / 1.2f), (int) (canvas.getHeight() / 1.2f));
			}
			if (giftItem == null) {
				giftItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_gift), fieldWidth);
			}
			if (crapItem == null) {
				crapItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_crap), fieldWidth);
			}
			if (bananaItem == null) {
				bananaItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_banana), fieldWidth);
			}
			if (glueItem == null) {
				glueItem = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_glue), fieldWidth);
			}
			if (player == null) {
				player = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_player), fieldWidth);
			}
			if (playerself == null) {
				playerself = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_playerself), fieldWidth);
			}
			if (fire == null) {
				fire = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_fire), fieldWidth);
			}
			if (death == null) {
				death = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_skull), fieldWidth);
			}
			if (arrowUpRed == null) {
				arrowUpRed = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_red), fieldWidth, 0), -90);
			}
			if (arrowLeftRed == null) {
				arrowLeftRed = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_red), fieldWidth, 0), -180);
			}
			if (arrowDownRed == null) {
				arrowDownRed = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_red), fieldWidth, 0), -270);
			}
			if (arrowRightRed == null) {
				arrowRightRed = this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_red), fieldWidth, 0);
			}
			if (arrowUpYellow == null) {
				arrowUpYellow = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_yellow), fieldWidth, 0), -90);
			}
			if (arrowLeftYellow == null) {
				arrowLeftYellow = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_yellow), fieldWidth, 0), -180);
			}
			if (arrowDownYellow == null) {
				arrowDownYellow = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_yellow), fieldWidth, 0), -270);
			}
			if (arrowRightYellow == null) {
				arrowRightYellow = this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_yellow), fieldWidth, 0);
			}
			if (arrowUpGreen == null) {
				arrowUpGreen = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_green), fieldWidth, 0), -90);
			}
			if (arrowLeftGreen == null) {
				arrowLeftGreen = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_green), fieldWidth, 0), -180);
			}
			if (arrowDownGreen == null) {
				arrowDownGreen = this.getRotatedIcon(this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_green), fieldWidth, 0), -270);
			}
			if (arrowRightGreen == null) {
				arrowRightGreen = this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow_right_green), fieldWidth, 0);
			}

			// Hauptspieldfeld
			for (int x = 1; x < mapSize + 1; x++) {
				for (int y = 1; y < mapSize + 1; y++) {
					GameField feld = gMap.getGameField(x - 1, y - 1);
					if (feld.isOccupied()) {
						if(feld.hasContent()){
						if (feld.getContent() == InterfaceFieldContent.ITEM_GIFT) {
							canvas.drawBitmap(giftItem, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						}
						if (feld.getContent() == InterfaceFieldContent.ITEM_CRAP) {
							canvas.drawBitmap(crapItem, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						}
						if (feld.getContent() == InterfaceFieldContent.ITEM_BANANA) {
							canvas.drawBitmap(bananaItem, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						}
						if (feld.getContent() == InterfaceFieldContent.ITEM_GLUE) {
							canvas.drawBitmap(glueItem, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						}
						if (feld.getContent() == InterfaceFieldContent.FIRE) {
							canvas.drawBitmap(fire, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						}
						}

						if (feld.getPlayer() == InterfaceFieldContent.PLAYER_0) {

							if (playerObj.getNumber() == feld.getPlayer()) {
								canvas.drawBitmap(playerself, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							} else {
								canvas.drawBitmap(player, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							}
						}
						if (feld.getPlayer() == InterfaceFieldContent.PLAYER_1) {
							if (playerObj.getNumber() == feld.getPlayer()) {
								canvas.drawBitmap(playerself, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							} else {
								canvas.drawBitmap(player, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							}
						}
						if (feld.getPlayer() == InterfaceFieldContent.PLAYER_2) {
							if (playerObj.getNumber() == feld.getPlayer()) {
								canvas.drawBitmap(playerself, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							} else {
								canvas.drawBitmap(player, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							}
						}
						if (feld.getPlayer() == InterfaceFieldContent.PLAYER_3) {
							if (playerObj.getNumber() == feld.getPlayer()) {
								canvas.drawBitmap(playerself, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							} else {
								canvas.drawBitmap(player, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							}
						}
						if (feld.getPlayer() == InterfaceFieldContent.PLAYER_4) {
							if (playerObj.getNumber() == feld.getPlayer()) {
								canvas.drawBitmap(playerself, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							} else {
								canvas.drawBitmap(player, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
							}
						}

						if (feld.getPlayer() == InterfaceFieldContent.Player_DEAD) {
							canvas.drawBitmap(death, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						}

					}
				}

			}

			// Ränder
			for (int x = 1; x < mapSize + 1; x++) {
				{
					BorderField border = gMap.getBorder(Arrow.DIRECTION_HOR, x);
					if (border.isArrowed()) {
						Bitmap iconUp = null, iconDown = null;
						if (border.getColor() == Arrow.STATE_RED) {
							iconUp = arrowUpRed;
							iconDown = arrowDownRed;
						}
						if (border.getColor() == Arrow.STATE_YELLOW) {
							iconUp = arrowUpYellow;
							iconDown = arrowDownYellow;
						}
						if (border.getColor() == Arrow.STATE_GREEN) {
							iconUp = arrowUpGreen;
							iconDown = arrowDownGreen;
						}
						if (iconDown == null) {
							iconDown = arrowDownRed;
						}
						if (iconUp == null) {
							iconUp = arrowUpRed;
						}
						canvas.drawBitmap(iconDown, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, 0, paint);
						canvas.drawBitmap(iconUp, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * (mapSize + 1) + (mapPadding * (mapSize + 1)) - fieldWidth / 2, paint);
					}
				}
			}
			for (int y = 1; y < mapSize + 1; y++) {
				{
					BorderField border = gMap.getBorder(Arrow.DIRECTION_VERT, y);
					if (border.isArrowed()) {
						Bitmap iconLeft = null, iconRight = null;
						if (border.getColor() == Arrow.STATE_RED) {
							iconLeft = arrowLeftRed;
							iconRight = arrowRightRed;
						}
						if (border.getColor() == Arrow.STATE_YELLOW) {
							iconLeft = arrowLeftYellow;
							iconRight = arrowRightYellow;
						}
						if (border.getColor() == Arrow.STATE_GREEN) {
							iconLeft = arrowLeftGreen;
							iconRight = arrowRightGreen;
						}
						if (iconLeft == null) {
							iconLeft = arrowLeftRed;
						}
						if (iconRight == null) {
							iconRight = arrowRightRed;
						}
						canvas.drawBitmap(iconRight, offsetL, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
						canvas.drawBitmap(iconLeft, fieldWidth * (mapSize + 1) + (mapPadding * (mapSize + 1)) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
					}
				}
			}
		}

		// long rTime = System.currentTimeMillis() - time;
		// Log.i("MV", "renderzeit " + rTime);

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

	public Bitmap getResizedBorderIcon(Bitmap icon, int newSize, int dir) {
		int width = icon.getWidth();
		int height = icon.getHeight();
		float scaleWidth = ((float) newSize) / width;
		float scaleHeight = ((float) newSize) / height;
		if (dir == 0)
			scaleWidth = scaleWidth / 2;
		if (dir == 1)
			scaleHeight = scaleHeight / 2;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(icon, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	public Bitmap getRotatedIcon(Bitmap icon, int rotation) {
		Matrix matrix = new Matrix();
		matrix.postRotate(rotation); // anti-clockwise by 90 degrees
		// create a new bitmap from the original using the matrix to transform
		// the result
		return Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, true);
	}

}
