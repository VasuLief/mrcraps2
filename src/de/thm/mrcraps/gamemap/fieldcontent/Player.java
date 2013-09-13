package de.thm.mrcraps.gamemap.fieldcontent;

import android.util.Log;

/**
 * Diese Klasse beschreibt den Spieler
 * 
 * @author Vasu
 * 
 */
public class Player {

	// Welches Item hat der Player zur Zeit in der Hand?
	private FieldContent currentItem;

	// Wo befindet sich der Player?
	private int posX;
	private int posY;

	private int number;
	private String name;
	private boolean isDead;

	private int currentPoints = 0;
	private int crapCount = 0;
	private int moveCount = 0;

	public Player(int startPosX, int startPosY, int player_number, String pName) {

		posX = startPosX;
		posY = startPosY;
		number = player_number;
		name = pName;
		isDead = false;

	}

	public void moveLeft() {
		if (!isDead)
			posX--;
		moveCount++;
	}

	public void moveRight() {
		if (!isDead)
			posX++;
		moveCount++;
	}

	public void moveDown() {
		if (!isDead)
			posY++;
		moveCount++;
	}

	public void moveUp() {
		if (!isDead)
			posY--;
		moveCount++;
	}

	public int getX() {
		return posX;
	}

	public int getY() {
		return posY;
	}

	public void setX(int posX) {
		this.posX = posX;
	}

	public void setY(int posY) {
		this.posY = posY;
	}

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

	public void grabItem(FieldContent item) {
		currentItem = item;
		currentItem.useItem();
		Log.e("Player grab Item:", "Nr: " + getNumber() + "Itemtype:" + currentItem.getContent() + "");
	}

	public boolean hasItem() {
		if (currentItem != null)
			return true;
		return false;
	}

	public FieldContent getItem() {
		return currentItem;
	}

	public void useItem() {
		if (hasItem()) {
			currentItem.useItem();
			currentItem = null;
			crapCount++;
		}
	}

	public boolean isDead() {
		return isDead;
	}

	public void kill() {
		isDead = true;
	}

	public int getCurrentPoints() {
		return currentPoints;
	}

	public void setCurrentPoints(int currentPoints) {
		if (!isDead())
			this.currentPoints = currentPoints;
	}

	public int getCrapCount() {
		return crapCount;
	}

	public void setCrapCount(int crapCount) {
		this.crapCount = crapCount;
	}

	public int getMoveCount() {
		return moveCount;
	}

	public void setMoveCount(int moveCount) {
		this.moveCount = moveCount;
	}
}
