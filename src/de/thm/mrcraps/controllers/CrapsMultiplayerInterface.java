package de.thm.mrcraps.controllers;

public interface CrapsMultiplayerInterface {

	// Host to Clients:
	public static final String MAP_SIZE = "map.size";
	public static final String PLAYER_COUNT = "player.count";
	// content int
	public static final String GO_STATE = "state.go";// returns 3, 2, 1 and then
														// 0 (for go) -1 for
														// GAME OVER (==ALL
														// DEAD)

	// Clients to Host bzw Host to all Clients

	// Key
	public static final String PLAYER_DOES = "player.does";
	public static final int MOVE_PLAYER_UP = 0;
	public static final int MOVE_PLAYER_DOWN = 1;
	public static final int MOVE_PLAYER_LEFT = 2;
	public static final int MOVE_PLAYER_RIGHT = 3;
	public static final int PLAYER_USE_ITEM = 4;
	public static final int PLAYER_GET_ITEM = 5;
	public static final int PLAYER_DIE = 6;
	public static final String ITEM_TYPE = "item.type";
	public static final String POS_X = "pos.x";
	public static final String POS_Y = "pos.y";
	// Content
	public static final String PLAYER_NUMBER = "player.number";

	// Host to Client
	// type
	public static final String ITEMS_THREAD = "items";
	public static final String GAME_THREAD = "game";
	// anderes
	public static final String ITEMS_BANANA_BOOL = "items.banana.bool";
	public static final String ITEMS_GLUE_BOOL = "items.glue.bool";
	public static final String ITEMS_CRAP_BOOL = "items.crap.bool";
	public static final String ITEMS_BANANA = "items.banana";
	public static final String ITEMS_GLUE = "items.glue";
	public static final String ITEMS_CRAP = "items.crap";
	public static final String ITEMS_EXPTIME = "items.time";
	public static final String ARROW_AND_FIRE_SPAWNS = "fire.please.help.me";
	public static final String ARROW = "arrow";
	public static final String ARROW_EXPTIME = "arrow.expTime";
	public static final String FIRE_EXPTIME = "fire.expTime";
	public static final String ARROW_WAITTIME = "arrow.waittime";

}