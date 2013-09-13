package de.thm.mrcraps.gamemap.fieldcontent;

public class Fire extends FieldContent {

	public final static int DIRECTION_VERT = 0;
	public final static int DIRECTION_HOR = 1;

	private int dir;
	private int startpoint;

	public Fire(int start, int direction, long longExp) {
		super(longExp);
		startpoint = start;
		dir = direction;
		this.setContent(InterfaceFieldContent.FIRE);
		isUsed = true;
	}

	public int getDirection() {
		return dir;
	}

	public int getStartPoint() {
		return startpoint;
	}
}
