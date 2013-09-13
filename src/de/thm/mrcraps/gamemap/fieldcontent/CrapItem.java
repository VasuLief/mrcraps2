package de.thm.mrcraps.gamemap.fieldcontent;

import android.os.Parcel;
import android.os.Parcelable;

public class CrapItem extends FieldContent implements Parcelable {

	@SuppressWarnings("unused")
	private static final long SerialVersionUID = 1;

	// Wie lange ist dieses Item sichtbar, nachdem es genutzt wurde?
	private long durationTime = 4000;

	public CrapItem(long expTime, int posx, int posy) {
		super(expTime, posx, posy);
		this.setContent(InterfaceFieldContent.ITEM_CRAP);
	}

	public CrapItem(Parcel in) {
		super(in.readLong(), in.readInt(), in.readInt());
		this.setContent(in.readInt());
	}

	@Override
	public void useItem() {
		super.useItem();
		isBlocking = true;
	}

	public long getEffectTime() {
		return durationTime;
	}

	public static final Parcelable.Creator<CrapItem> CREATOR = new Parcelable.Creator<CrapItem>() {
		public CrapItem createFromParcel(Parcel in) {
			return new CrapItem(in);
		}

		public CrapItem[] newArray(int size) {
			return new CrapItem[size];
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
		dest.writeInt(InterfaceFieldContent.ITEM_CRAP);
	}

}
