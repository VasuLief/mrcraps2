package de.thm.mrcraps;

/**
 * Konstanten
 * 
 * @author Vasu
 * 
 */
public interface Common {
	// Zeit, die der Splash-Screen angezeigt wird
	static final int SPLASH_DISPLAY_LENGTH = 1100;

	// Unsere aktuelle OS-Version
	static final int API_VERSION = android.os.Build.VERSION.SDK_INT;

	// Datenbank für die Highscores
	static final String DATABASE_FILE = "Database.sql";

	// Konstanten and Stuff
	public final static int STATE_WAITING = 0;
	public final static int STATE_WARMUP = 1;
	public final static int STATE_LIVE = 2;
	public final static int STATE_DEAD = 3;
	public final static int STATE_SCORE = 4;

	public final static int GAME_SINGLEPLAYER = 1;
	public final static int GAME_MULTIPLAYER = 2;
	public final static int GAME_HOST = 1;
	public final static int GAME_CLIENT = 2;
	
	public final static int WUFF = 2;

}

// Test 