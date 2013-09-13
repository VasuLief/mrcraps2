package de.thm.mrcraps.gamemap;

import de.thm.mrcraps.gamemap.fieldcontent.FieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.Fire;
import de.thm.mrcraps.gamemap.fieldcontent.InterfaceFieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.Player;
import android.util.Log;

/**
 * Diese Klasse beschreibt ein einzelnes Feld innerhalb der Map.
 * 
 */
public class GameField {

	// Meine Position
	private int myPosX;
	private int myPosY;

	// Ist etwas auf mir drauf?
	private boolean isOccupied;

	// Bin ich durch ein Item blockiert?
	private boolean isBlocked;

	// Welches Item ist hier?
	private FieldContent content;

	private Player player;

	public GameField(int x, int y) {
		myPosX = x;
		myPosY = y;
		isOccupied = false;
		isBlocked = false;
	}

	public int getX() {
		return myPosX;
	}

	public int getY() {
		return myPosY;
	}

	public void placeItem(FieldContent newitem) {
		if (content != null) {
			if (!content.isNotExpired()) {
				this.expireContent();
				content = newitem;
				isOccupied = true;
			} else if (content.getContent() != InterfaceFieldContent.FIRE) {
				content = newitem;
				isOccupied = true;
			}
		} else {
			content = newitem;
			isOccupied = true;
		}
		if (content.isUsed()) {
			if (content.getContent() == InterfaceFieldContent.ITEM_CRAP)
				isBlocked = true;
		}
	}

	public void useItem() {
		if (hasContent()) {
			content.useItem();
			isOccupied = true;
			if (content.getContent() == InterfaceFieldContent.ITEM_CRAP)
				isBlocked = true;
		}
	}

	public FieldContent getItem() {
		if (hasContent())
			return content;
		return null;
	}

	public boolean hasContent() {
		if (content != null) {
			if (content.isNotExpired()) {
				return true;
			} else {
				this.expireContent();
			}
		}
		return false;
	}

	public void expireContent() {
		content = null;
		if (!hasPlayer())
			isBlocked = false;
	}

	public int getContent() {
		if (this.hasContent()) {
			if (!content.isNotExpired()) {
				isOccupied = false;
				isBlocked = false;
				expireContent();
			} else {
				return content.getContent();
			}
		}
		return InterfaceFieldContent.FIELD_EMPTY;
	}

	public boolean hasPlayer() {
		if (player == null)
			return false;
		return true;
	}

	public int getPlayer() {
		if (this.hasPlayer()) {
			if (!player.isDead()) {
				return player.getNumber();
			}
			if (player.isDead())
				return InterfaceFieldContent.Player_DEAD;
		}
		return InterfaceFieldContent.FIELD_EMPTY;
	}

	public void killPlayer() {
		if (player != null) {
			player.kill();
			Log.e("killed", "Player #" + player.getNumber());
		}
	}

	public void setPlayer(Player p) {
		player = p;
		if (p != null) {
			isOccupied = true;
			isBlocked = true;
		} else {
			isBlocked = false;
		}
	}

	public void setOnFlame(Fire fire) {
		content = fire;
		isOccupied = true;
	}

	public void occupy(boolean occ) {
		if (!this.hasContent()) {
			isOccupied = occ;
		}
	}

	public boolean isOccupied() {
		if (this.hasContent()) {
			if (!content.isNotExpired()) {
				isOccupied = false;
				expireContent();
			}
		}
		return isOccupied;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

}
