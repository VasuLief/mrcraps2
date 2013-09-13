package de.thm.mrcraps.views;

import de.thm.mrcraps.R;
import de.thm.mrcraps.gamemap.GameMap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Die View auf der der Untergrund gemalt wird.
 * 
 * @author Vasu
 * 
 */
public class MapBackgroundView extends View {

	private int mapSize;
	private int mapPadding = 0; // breite des randes in Pixel
	private Context context;
	private Canvas canvas;
	private Paint paint;
	private Bitmap emptyField;
	private Bitmap borderFieldX;
	private Bitmap borderFieldY;
	// Die View kennt die Map und bekommt daher ihre Informationen
	private GameMap gMap;

	public MapBackgroundView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		canvas = new Canvas();
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
	}

	public void reDraw() {
		this.invalidate();
		draw(canvas);
	}

	public void setGameMap(GameMap map) {
		gMap = map;
	}

	@Override
	public void draw(Canvas c) {
		super.onDraw(canvas);
		int saveCount = canvas.getSaveCount();
		long time = System.currentTimeMillis();
		canvas.save();
		if (c != null && gMap != null) {
			mapSize = gMap.getMapSize();
			int fieldWidth = c.getHeight() / (mapSize + 1) - mapPadding;
			int fieldHeight = c.getHeight() / (mapSize + 1) - mapPadding;
			int offsetL = (c.getWidth() - c.getHeight()) / 2;

			if (emptyField == null) {
				emptyField = this.getResizedIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_floor), fieldWidth);
			}
			if (borderFieldX == null) {
				borderFieldX = this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_border), fieldWidth, 1);
			}
			if (borderFieldY == null) {
				borderFieldY = this.getResizedBorderIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_border), fieldWidth, 0);
			}

			for (int x = 1; x < mapSize + 1; x++) {
				for (int y = 1; y < mapSize + 1; y++) {
					c.drawBitmap(emptyField, (fieldWidth * x) + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
				}
			}
			// Draw Border
			for (int x = 1; x < mapSize + 1; x++) {
				c.drawBitmap(borderFieldX, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, 0, paint);
				c.drawBitmap(borderFieldX, fieldWidth * x + (mapPadding * x) + offsetL - fieldWidth / 2, fieldHeight * (mapSize + 1) + (mapPadding * (mapSize + 1)) - fieldWidth / 2, paint);
			}
			for (int y = 1; y < mapSize + 1; y++) {
				c.drawBitmap(borderFieldY, offsetL, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
				c.drawBitmap(borderFieldY, fieldWidth * (mapSize + 1) + (mapPadding * (mapSize + 1)) + offsetL - fieldWidth / 2, fieldHeight * y + (mapPadding * y) - fieldWidth / 2, paint);
			}

		}
		canvas.restoreToCount(saveCount);

		long rTime = System.currentTimeMillis() - time;
		Log.i("BGVIEW", "Render Time: " + String.valueOf(rTime) + "ms");

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

}
