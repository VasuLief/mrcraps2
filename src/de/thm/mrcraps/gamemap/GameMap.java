package de.thm.mrcraps.gamemap;

import java.util.Vector;

import de.thm.mrcraps.gamemap.fieldcontent.Arrow;
import de.thm.mrcraps.gamemap.fieldcontent.FieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.Fire;
import de.thm.mrcraps.gamemap.fieldcontent.GlueItem;
import de.thm.mrcraps.gamemap.fieldcontent.InterfaceFieldContent;
import de.thm.mrcraps.gamemap.fieldcontent.Player;

import android.util.Log;

/**
 * Diese Klasse beschreibt die komplette Map und führt ein Array aus Feldern.
 * 
 */
public class GameMap {

	public static final int MODE_SINGLEPLAYER = 0;
	public static final int MODE_MULTIPLAYER = 1;
	private int runningMode;

	// Das Array mit den Feldern
	private GameField[][] map;
	private BorderField[][] border;

	// Der Vector mit den Players
	private Vector<Player> players;
	private int playerSelf;

	// Die größe der Map (Settings)
	private int mapSize;

	public GameMap(int source, int size) {

		runningMode = source;
		mapSize = size;

		// Spieler erstellen
		players = new Vector<Player>();

		// Karte erstellen
		map = new GameField[mapSize][mapSize];
		for (int x = 0; x < mapSize; x++) {
			for (int y = 0; y < mapSize; y++) {
				map[x][y] = new GameField(x, y);
			}
		}
		border = new BorderField[2][mapSize + 1]; // GameField[0][] Border
													// x-Achse; GameField[1][]
													// Border y-Achse
		for (int counter = 0; counter < mapSize + 1; counter++) {
			border[BorderField.DIRECTION_HOR][counter] = new BorderField(counter);
			border[BorderField.DIRECTION_VERT][counter] = new BorderField(counter);
		}
	}

	public void addPlayer(Player p, int x, int y) {
		players.add(p);
		map[x][y].setPlayer(p);
		Log.e("koordinaten", x + " " + y);
	}

	public Vector<Player> getPlayer() {
		return players;
	}

	public boolean playersAlive() {
		for (int i = 0; i < players.size(); i++) {
			if (players.elementAt(i).isDead() != true)
				return true;
		}
		return false;
	}

	public void setPlayerSelf(int id) {
		playerSelf = id;
	}

	public void spawnFire(Fire fire) {
		if (fire.getDirection() == Fire.DIRECTION_HOR) {
			for (int x = 0; x < mapSize; x++) {
				if (map[x][fire.getStartPoint()].hasContent())
					map[x][fire.getStartPoint()].expireContent();
				if (map[x][fire.getStartPoint()].hasPlayer()) {
					if (map[x][fire.getStartPoint()].getPlayer() == playerSelf)
						map[x][fire.getStartPoint()].killPlayer();
				}
				map[x][fire.getStartPoint()].setOnFlame(fire);
			}
		}
		if (fire.getDirection() == Fire.DIRECTION_VERT) {
			for (int y = 0; y < mapSize; y++) {
				if (map[fire.getStartPoint()][y].hasContent())
					map[fire.getStartPoint()][y].expireContent();
				if (map[fire.getStartPoint()][y].hasPlayer()) {
					if (map[fire.getStartPoint()][y].getPlayer() == playerSelf)
						map[fire.getStartPoint()][y].killPlayer();
				}
				map[fire.getStartPoint()][y].setOnFlame(fire);
			}
		}
	}

	public void spawnArrow(Arrow arrow) {
		if (arrow.getDirection() == Arrow.DIRECTION_HOR) {
			border[0][arrow.getStartPoint() + 1].placeArrow(arrow);
		}
		if (arrow.getDirection() == Arrow.DIRECTION_VERT) {
			border[1][arrow.getStartPoint() + 1].placeArrow(arrow);
		}
	}

	public void spawnItem(FieldContent item) {
		map[item.getPosX()][item.getPosY()].placeItem(item);
		map[item.getPosX()][item.getPosY()].occupy(true);
	}

	public boolean useItem(Player player) {
		if (runningMode == GameMap.MODE_SINGLEPLAYER)
			return false;
		if (player.hasItem()) {
			Log.e("player:" + player.getNumber(), "place Item on:" + player.getX() + "," + player.getY());
			map[player.getX()][player.getY()].placeItem(player.getItem());
			map[player.getX()][player.getY()].useItem();
			return true;
		}
		return false;
	}

	public boolean movePlayerToPosition(Player p, int x, int y) {
		if (!map[p.getX()][p.getY()].hasContent())
			map[p.getX()][p.getY()].occupy(false);
		map[p.getX()][p.getY()].setPlayer(null);
		if (map[x][y].getContent() == InterfaceFieldContent.ITEM_BANANA) {
			Log.e("field", "has banana");
			map[x][y].expireContent();
		}

		if (map[x][y].getContent() != InterfaceFieldContent.ITEM_CRAP) {
			map[x][y].setPlayer(p);
		}
		p.setX(x);
		p.setY(y);
		map[p.getX()][p.getY()].occupy(true);
		map[x][y].setPlayer(p);
		return true;
	}

	public boolean movePlayer(Player p, int dir) {
		if (map[p.getX()][p.getY()].getContent() == InterfaceFieldContent.ITEM_GLUE) {
			GlueItem gi = (GlueItem) map[p.getX()][p.getY()].getItem();
			if (gi.isActivated())
				return false;
			else
				gi.activate();
		}

		int newx = p.getX();
		int newy = p.getY();
		if (dir == 1)
			newx--;
		if (dir == 2)
			newx++;
		if (dir == 3)
			newy--;
		if (dir == 4)
			newy++;

		if (newx < 0 || newx > mapSize - 1 || newy < 0 || newy > mapSize - 1)
			return false;

		if (map[newx][newy].isBlocked() == true)
			return false;

		if (map[newx][newy].getContent() == InterfaceFieldContent.ITEM_CRAP) {
			if (map[newx][newy].getItem().isBlocking())
				return false;
		}

		if (!map[p.getX()][p.getY()].hasContent())
			map[p.getX()][p.getY()].occupy(false);
		map[p.getX()][p.getY()].setPlayer(null);

		if (dir == 1 && (p.getX() > 0)) { // Spieler darf nicht auf den Rahmen
											// bewegen!
			p.moveLeft();
		}
		if (dir == 2 && (p.getX() < mapSize)) { // Spieler darf nicht auf den
												// Rahmen bewegen!
			p.moveRight();
		}
		if (dir == 3 && (p.getY() > 0)) { // Spieler darf nicht auf den Rahmen
											// bewegen!
			p.moveUp();
		}
		if (dir == 4 && (p.getY() < mapSize)) {// Spieler darf nicht auf den
												// Rahmen bewegen!
			p.moveDown();
		}
		if (map[p.getX()][p.getY()].hasContent()) {
			if (map[p.getX()][p.getY()].getItem().getContent() == InterfaceFieldContent.FIRE) {
				if (p.getNumber() == playerSelf) {
					Log.e("killed", "myself");
					map[p.getX()][p.getY()].killPlayer();
					p.kill();
				}
			}
			if (map[p.getX()][p.getY()].getItem().getContent() == InterfaceFieldContent.ITEM_GLUE) {
				((GlueItem) map[p.getX()][p.getY()].getItem()).glueIt();
			}
			if (map[p.getX()][p.getY()].getItem().getContent() == InterfaceFieldContent.ITEM_BANANA) {
				map[p.getX()][p.getY()].expireContent();
				map[p.getX()][p.getY()].occupy(true);
				map[p.getX()][p.getY()].setPlayer(p);
				moveBanana(p, dir);
			}
			if (map[p.getX()][p.getY()].hasContent()) {
				if (p.hasItem() != true && map[p.getX()][p.getY()].getItem().isUsed() != true) {
					Log.e("grabbing item", "" + map[p.getX()][p.getY()].getItem().getContent());
					p.grabItem(map[p.getX()][p.getY()].getItem());
					map[p.getX()][p.getY()].expireContent();
				}
			}
		}
		map[p.getX()][p.getY()].occupy(true);
		map[p.getX()][p.getY()].setPlayer(p);

		return true;
	}

	public void moveBanana(Player p, int dir) {
		if (dir != -1) {
			boolean walkable = false;
			walkable = movePlayer(p, dir);
			while (walkable) {
				walkable = movePlayer(p, dir);
			}
		}
	}

	public int getMapSize() {
		return mapSize;
	}

	public GameField getGameField(int x, int y) {
		return map[x][y];
	}

	public BorderField getBorder(int Direction, int y) {
		return border[Direction][y];
	}
}
