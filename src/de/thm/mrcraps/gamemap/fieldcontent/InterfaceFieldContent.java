package de.thm.mrcraps.gamemap.fieldcontent;

public interface InterfaceFieldContent {

	// Feld leer
	public final static int FIELD_EMPTY = 99;

	// Auf dem Feld ist ein Spieler
	public final static int PLAYER_0 = 0;
	public final static int PLAYER_1 = 1;
	public final static int PLAYER_2 = 2;
	public final static int PLAYER_3 = 3;
	public final static int PLAYER_4 = 4;
	public final static int PLAYER_5 = 5;
	public final static int PLAYER_6 = 6;
	public final static int PLAYER_7 = 7;
	public final static int PLAYER_8 = 8;

	public final static int Player_DEAD = 10;

	// Fire und Arrow
	public final static int FIRE = 11;
	public final static int ARROW_UP = 12;
	public final static int ARROW_DOWN = 13;
	public final static int ARROW_RIGHT = 14;
	public final static int ARROW_LEFT = 15;

	// Auf dem Feld ist ein Item
	public final static int ITEM_GIFT = 20;
	public final static int ITEM_CRAP = 21;
	public final static int ITEM_BANANA = 22;
	public final static int ITEM_GLUE = 23;
	public final static int ITEM_PORTAL = 24;
	public final static int ITEM_TRAP = 25;
}
