package de.thm.mrcraps.views;

import de.thm.mrcraps.R;
import de.thm.mrcraps.controllers.PreferencesManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Das Hauptmenü
 * 
 * @author Vasu
 * 
 */
public class MainMenuActivity extends Activity {

	private Button btn_singleplayer;
	private Button btn_multiplayer;
	private Button btn_settingss;
	private Button btn_highscore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_mainmenu);

		btn_singleplayer = (Button) findViewById(R.id.buttonSingleplayer);
		btn_singleplayer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this.getBaseContext(), SingleplayerActivity.class);
				startActivity(intent);
			}
		});

		btn_multiplayer = (Button) findViewById(R.id.buttonMultiplayer);
		btn_multiplayer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this.getBaseContext(), SearchServices.class);
				startActivity(intent);
			}
		});

		btn_settingss = (Button) findViewById(R.id.buttonSettings);
		btn_settingss.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this.getBaseContext(), SettingsActivity.class);
				startActivity(intent);
			}
		});

		btn_highscore = (Button) findViewById(R.id.buttonHighscore);
		btn_highscore.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(MainMenuActivity.this.getBaseContext(), HighscoreActivity.class);
				startActivity(intent);
			}
		});

		// Setzt den passenden Begrüßungstitel beim Laden dieser Activity
		setTitle();

	}

	@Override
	public void onResume() {
		super.onResume();
		// Wenn der Username geändert wurde, muss das auch registriert und
		// geändert werden
		setTitle();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.menu_settings:
			startActivity(new Intent(this, SettingsActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setTitle() {
		PreferencesManager pm = new PreferencesManager(this);

		if (pm.getPreferenceString("sp_userName") == null || pm.getPreferenceString("sp_userName").trim().equals("")) {
			this.setTitle("Willkommen Spieler 1!");
		} else {
			this.setTitle("Willkommen" + pm.getPreferenceString("sp_userName") + "!");
		}

	}

}
