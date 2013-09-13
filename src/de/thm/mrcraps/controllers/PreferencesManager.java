package de.thm.mrcraps.controllers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesManager {

	Activity caller = null;

	public PreferencesManager(Activity caller) {
		this.caller = caller;
	}

	public String getPreferenceString(String key) {
		// Prefer ences auslesen
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(caller);
		StringBuilder builder = new StringBuilder();
		builder.append("\n" + sharedPrefs.getString(key, ""));
		return builder.toString();
	}

	public String getPreferenceBoolean(String key) {
		// Preferences auslesen
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(caller);
		StringBuilder builder = new StringBuilder();
		builder.append("\n" + sharedPrefs.getBoolean(key, false));
		return builder.toString();
	}

	public void setPreferenceString(String key, String value) {
		// Preferences ändern
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(caller);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public SharedPreferences getSharedPreferencesObject() {
		return PreferenceManager.getDefaultSharedPreferences(caller);
	}

}
