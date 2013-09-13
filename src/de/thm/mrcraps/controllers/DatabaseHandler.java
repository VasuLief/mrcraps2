package de.thm.mrcraps.controllers;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "higscore.db";
	private static final int DATABASE_VERSION = 1;

	private static final String HIGHSCORE_TABLE = "highscore";

	private static final String HIGHSCORE_ID = "_id";
	private static final String HIGHSCORE_POINTS = "points";
	private static final String HIGHSCORE_USER = "user";
	private static final String HIGHSCORE_TIME = "time";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String createDB = "CREATE TABLE " + HIGHSCORE_TABLE + " (" + HIGHSCORE_ID + " INTEGER PRIMARY KEY, " + HIGHSCORE_POINTS + " INTEGER, " + HIGHSCORE_USER + " TEXT, " + HIGHSCORE_TIME + " TEXT)";
		db.execSQL(createDB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + HIGHSCORE_TABLE);
		onCreate(db);

	}

	public void addNewHighscore(int points, String user, String time) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "INSERT INTO " + HIGHSCORE_TABLE + " (" + HIGHSCORE_POINTS + "," + HIGHSCORE_USER + "," + HIGHSCORE_TIME + ") VALUES (" + points + ",'" + user.trim() + "','" + time + "')";
		// Log.i("DB", query);
		db.execSQL(query);
	}

	public ArrayList<String> getAllData() {
		ArrayList<String> scores = new ArrayList<String>();
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + HIGHSCORE_TABLE + " ORDER BY " + HIGHSCORE_POINTS + " DESC";
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				// Log.i("DB", "read: "+Integer.valueOf(cursor.getString(1))
				// +" - "+ cursor.getString(2)+" - "+cursor.getString(3));
				String line = "";

				if (Integer.valueOf(cursor.getString(1)) == 1) {
					line = cursor.getString(1) + " Punkt wurde von " + cursor.getString(2) + " am " + cursor.getString(3) + " erspielt.";
				} else {
					line = cursor.getString(1) + " Punkte wurden von " + cursor.getString(2) + " am " + cursor.getString(3) + " erspielt.";
				}
				scores.add(line);
			} while (cursor.moveToNext());
		}

		return scores;

	}

}
