package de.thm.mrcraps.gamemap.fieldcontent;

import android.os.Parcel;
import android.os.Parcelable;

public class GlueItem extends FieldContent implements Parcelable {

	@SuppressWarnings("unused")
	private static final long SerialVersionUID = 1;

	// Wie lange ist dieses Item sichtbar, nachdem es genutzt wurde?
	private long durationTime = 5000;
	private long glueTime = 1000;

	private boolean aktiviert = true;

	public GlueItem(long expTime, int posx, int posy) {
		super(expTime, posx, posy);
		this.setContent(InterfaceFieldContent.ITEM_GLUE);
	}

	public GlueItem(Parcel in) {
		super(in.readLong(), in.readInt(), in.readInt());
		this.setContent(in.readInt());
	}

	public void glueIt() {
		expirationTime = System.currentTimeMillis() + glueTime;
	}

	public void activate() {
		aktiviert = true;
		glueIt();
	}

	public void deActivate() {
		aktiviert = false;
	}

	public boolean isActivated() {
		return aktiviert;
	}

	public long getEffectTime() {
		return durationTime;
	}

	public static final Parcelable.Creator<GlueItem> CREATOR = new Parcelable.Creator<GlueItem>() {
		public GlueItem createFromParcel(Parcel in) {
			return new GlueItem(in);
		}

		public GlueItem[] newArray(int size) {
			return new GlueItem[size];
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
		dest.writeInt(this.getPosX());
		dest.writeInt(this.getPosY());
		dest.writeInt(InterfaceFieldContent.ITEM_GLUE);
	}
}
