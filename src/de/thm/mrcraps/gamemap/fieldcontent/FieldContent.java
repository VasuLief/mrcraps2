package de.thm.mrcraps.gamemap.fieldcontent;

public abstract class FieldContent {

	// Wie sieht dieses Item aus?
	protected int content;

	// Wann wird er wieder verschwinden?
	protected long expirationTime;
	private long durationTime = 4000;
	private int posX;
	private int posY;
	// Ist es in Benutzung?
	protected boolean isUsed;
	protected boolean isBlocking;

	public FieldContent(long expTime, int posx, int posy) {
		expirationTime = expTime;
		posX = posx;
		posY = posy;
		isUsed = false;
		isBlocking = false;
	}

	public FieldContent(long expTime) {
		expirationTime = expTime;
		isUsed = false;
		isBlocking = false;
	}

	public void setContent(int Con) {
		content = Con;
	}

	public void setExpTime(long time) {
		expirationTime = time;
	}

	public int getContent() {
		if (isUsed != true)
			return InterfaceFieldContent.ITEM_GIFT;
		else
			return content;
	}

	public long getExpTime() {
		return expirationTime;
	}

	public boolean isNotExpired() {
		if (expirationTime == -1)
			return true;
		if (expirationTime > System.currentTimeMillis()) {
			return true;
		}
		return false;
	}

	public void useItem() {
		isUsed = true;
		expirationTime = System.currentTimeMillis() + durationTime;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public boolean isBlocking() {
		return isBlocking;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

}
