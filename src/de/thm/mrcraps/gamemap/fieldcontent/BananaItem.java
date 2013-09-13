package de.thm.mrcraps.gamemap.fieldcontent;

import android.os.Parcel;
import android.os.Parcelable;

public class BananaItem extends FieldContent implements Parcelable {

	@SuppressWarnings("unused")
	private static final long SerialVersionUID = 1;

	// Wie lange ist dieses Item sichtbar, nachdem es genutzt wurde?
	private long durationTime = 4000;

	public BananaItem(long expTime, int posx, int posy) {
		super(expTime, posx, posy);
		this.setContent(InterfaceFieldContent.ITEM_BANANA);
	}

	public BananaItem(Parcel in) {
		super(in.readLong(), in.readInt(), in.readInt());
		this.setContent(in.readInt());
	}

	public long getEffectTime() {
		return durationTime;
	}

	public static final Parcelable.Creator<BananaItem> CREATOR = new Parcelable.Creator<BananaItem>() {
		public BananaItem createFromParcel(Parcel in) {
			return new BananaItem(in);
		}

		public BananaItem[] newArray(int size) {
			return new BananaItem[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.getExpTime());
		dest.writeInt(this.getPosX());
		dest.writeInt(this.getPosY());
		dest.writeInt(InterfaceFieldContent.ITEM_BANANA);
	}

}
