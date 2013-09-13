package de.thm.mrcraps.gamemap.fieldcontent;

public class BorderField {
	public final static int DIRECTION_VERT = 0;
	public final static int DIRECTION_HOR = 1;
	private int position;
	private Arrow arrow;

	public BorderField(int pos) {
		position = pos;
		arrow = null;
	}

	public void placeArrow(Arrow arrow) {
		this.arrow = arrow;
	}

	public boolean isArrowed() {
		if (arrow == null)
			return false;
		if (arrow.isNotExpired() && arrow.isVisible())
			return true;
		return false;
	}

	public int getPosition() {
		return position;
	}

	public int getColor() {
		if (isArrowed()) {
			return arrow.getColor();
		}
		return 0;
	}
}
