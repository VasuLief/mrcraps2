package de.thm.mrcraps.views;

import de.thm.mrcraps.Common;
import de.thm.mrcraps.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_splash);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Überprüfe, ob der Benutzer einen Namen hat, wenn nicht, zeige
		// Settings
		new Handler().postDelayed(new Runnable() {
			public void run() {

				SplashActivity.this.startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
				SplashActivity.this.finish();

			}
		}, Common.SPLASH_DISPLAY_LENGTH);
	}

}
