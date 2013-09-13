package de.thm.mrcraps.gamemap.fieldcontent;

import android.os.Parcel;
import android.os.Parcelable;

public class Arrow extends FieldContent implements Parcelable {

	@SuppressWarnings("unused")
	private static final long SerialVersionUID = 1;

	// In welche Richtung zeigt der Arrow?
	public final static int DIRECTION_VERT = 0;
	public final static int DIRECTION_HOR = 1;
	private int dir;
	private int startpoint;

	// Welche Farbe hat der Arrow?
	public final static int STATE_GREEN = 0;
	public final static int STATE_YELLOW = 1;
	public final static int STATE_RED = 2;
	private int color = STATE_GREEN;

	private boolean isVisible = true;

	public Arrow(int start, int direction, long longExp) {
		super(longExp);

		startpoint = start;
		dir = direction;
		if (dir == DIRECTION_VERT) {
			this.setContent(InterfaceFieldContent.ARROW_DOWN);
		}
		if (dir == DIRECTION_HOR) {
			this.setContent(InterfaceFieldContent.ARROW_RIGHT);
		}

		isUsed = true;

	}

	public Arrow(Parcel in) {
		super(in.readLong());
		dir = in.readInt();
		startpoint = in.readInt();
		color = in.readInt();
		if (dir == DIRECTION_VERT) {
			this.setContent(InterfaceFieldContent.ARROW_DOWN);
		}
		if (dir == DIRECTION_HOR) {
			this.setContent(InterfaceFieldContent.ARROW_RIGHT);
		}

		isUsed = true;
	}

	public int getDirection() {
		return dir;
	}

	public void setExpTime(long time) {
		super.setExpTime(time);
	}

	public int getStartPoint() {
		return startpoint;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void toggleVisibility() {
		isVisible = isVisible ? false : true;
		if (isVisible) {
			nextColor();
		}
	}

	public void nextColor() {
		color++;
	}

	public int getColor() {
		return color;
	}

	public static final Parcelable.Creator<Arrow> CREATOR = new Parcelable.Creator<Arrow>() {
		public Arrow createFromParcel(Parcel in) {
			return new Arrow(in);
		}

		public Arrow[] newArray(int size) {
			return new Arrow[size];
		}
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getExpTime());
		dest.writeInt(dir);
		dest.writeInt(startpoint);
		dest.writeInt(color);

	}
}
