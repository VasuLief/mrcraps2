package de.thm.mrcraps.views;

import java.util.ArrayList;
import java.util.List;

import de.thm.mrcraps.R;
import de.thm.mrcraps.controllers.ConnectionManager;
import de.thm.mrcraps.controllers.MOWSchnittstellenInterface;
import de.thm.mrcraps.controllers.PreferencesManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.Toast;

public class SearchServices extends Activity {

	// Einstellungen
	private SharedPreferences settings;

	private boolean boundToService = false;
	private static boolean readyToStart = false;

	private List<PackageInfo> availableServices;
	private int chosenServiceIndex;

	private ConnectionManager conMan;
	private Messenger receiver = new Messenger(new Receiver());

	// views
	private Button buttonStart;

	private MainListener mainListener;

	public static int plyr;
	public static String names;
	public static int myid;
	private int mapSize;
	private static String infraActivity = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_searchservices);
		getWindow().addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
		Bundle extras = getIntent().getExtras();
		
		buttonStart = (Button) findViewById(R.id.buttonStart);
		buttonStart.setEnabled(false);

		mainListener = new MainListener();
		buttonStart.setOnClickListener(mainListener);

		conMan = ConnectionManager.create(this);
		conMan.registerReceiver(receiver);

		settings = new PreferencesManager(this).getSharedPreferencesObject();
		mapSize = Integer.valueOf(settings.getString("mp_mapsize", "7").split("x")[0]);

		chosenServiceIndex = -1;
		availableServices = new ArrayList<PackageInfo>();
		List<PackageInfo> services = getPackageManager().getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo pi : services) {
			if (pi.packageName.startsWith(ConnectionManager.SERVICE_PACKAGE)) {
				availableServices.add(pi);
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!boundToService) {
			new ClassChooserDialog().show(getFragmentManager(), "Search Services");
		} else {
			Message msg = Message.obtain(null, MOWSchnittstellenInterface.MESSAGE_I_REGISTER);
			conMan.sendMessage(msg);
		}
	}

	/**
	 * Lässt den Spieler zur der Spiel-Activity wechseln
	 * 
	 * @param size
	 */
	public void switchToGame(int size) {
		Intent i = new Intent(SearchServices.this, MultiplayerActivity.class);
		i.putExtra("player", plyr);
		i.putExtra("names", names);
		i.putExtra("myid", myid);
		i.putExtra("mapSize", size);
		SearchServices.this.startActivityForResult(i, 0);
	}

	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {     
	  super.onActivityResult(requestCode, resultCode, data); 
	  switch(requestCode) { 
	    case (0) : { 
	      if (resultCode == Activity.RESULT_OK) { 
	    	  if(data.hasExtra("closing")){
	    		gameOver();  
	    	  }
	      } 
	      break; 
	    } 
	  } 
	}
	/**
	 * Nur beim Zurück-Klicken wird die Verbindung unterbrochen
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			boundToService = false;
			if(conMan.isBound())conMan.doUnbind();
			conMan.destroy();
			this.finish();
			return true;
		}

		// ---this event has not been handled---
		return false;
	}

	/**
	 * Wenn diese Methode aufgerufen wird, wird alles in dieser Klasse
	 * geschlossen
	 */
	public void gameOver() {

		if(conMan.isBound())conMan.doUnbind();
		conMan.destroy();
		this.finish();

	}

	@SuppressLint("HandlerLeak")
	private class Receiver extends Handler {
		@Override
		public void handleMessage(Message msg) {

			Log.e("Multi", "Da ist etwas reingekommen! ->" + msg.getData().toString());

			Bundle bundle = msg.getData();
			// bundle.setClassLoader(getClassLoader());

			switch (msg.what) {
			case (MOWSchnittstellenInterface.MESSAGE_2_CONNECTION): //

				boolean bla = false;
				if (infraActivity == null && bundle.getString(MOWSchnittstellenInterface.CONNECTION_ACTIVITY) != null) {
					bla = true;
				}
				plyr = bundle.getInt(MOWSchnittstellenInterface.CONNECTION_PLAYERCOUNT);
				names = bundle.getString(MOWSchnittstellenInterface.CONNECTION_PLAYERNAMES);
				myid = bundle.getInt(MOWSchnittstellenInterface.CONNECTION_OWN_ID);
				infraActivity = bundle.getString(MOWSchnittstellenInterface.CONNECTION_ACTIVITY);
				if (plyr > 0 && myid == 0) {
					readyToStart = true;
					buttonStart.setEnabled(true);
				}else{
					buttonStart.setEnabled(false);
				}

				Log.e("SS", "Anzahl Spieler: " + String.valueOf(plyr));
				Log.e("SS", "Die Namen: " + names);
				Log.e("SS", "Meine ID: " + String.valueOf(myid));
				Log.e("SS", "Activity: " + infraActivity);

				if (bla) {
					Intent intent = new Intent();
					PackageInfo chosenService = availableServices.get(chosenServiceIndex);
					intent.setClassName(chosenService.packageName, infraActivity);
					SearchServices.this.startActivity(intent);
				}

				break;

			case (MOWSchnittstellenInterface.MESSAGE_4_GET): //

				byte[] data = bundle.getByteArray(MOWSchnittstellenInterface.GET_DATA);
				Log.v("MP", "Nachricht 4 Größe: " + data.length);
				Parcel parcel = Parcel.obtain();
				parcel.unmarshall(data, 0, data.length);
				parcel.setDataPosition(0);

				// Bundle
				// resultBundle=(Bundle)parcel.readValue(Bundle.class.getClassLoader());
				Bundle resultBundle = Bundle.CREATOR.createFromParcel(parcel);

				if (resultBundle.containsKey("start")) {
					Log.e("SS", "spiel durch anderen gestartet");
					switchToGame(resultBundle.getInt("mapSize"));
				}

				break;

			case (MOWSchnittstellenInterface.MESSAGE_6_CLOSED): //

				int id = bundle.getInt(MOWSchnittstellenInterface.CLOSED_BY);

				Log.e("SS", String.valueOf(id));
				break;

			}

		}
	}

	/**
	 * Listener für die Buttons dieser Klasse
	 * 
	 * @author Vasu
	 * 
	 */
	private class MainListener implements View.OnClickListener {

		public void onClick(View v) {

			if (v.getId() == R.id.buttonStart) {

				Log.e("SS", "start gedrückt");
				if (!boundToService) {

					Toast.makeText(SearchServices.this, "not bound to a service", Toast.LENGTH_SHORT).show();
					return;
				}

				if (readyToStart) {

					

					Message msg = Message.obtain(null, MOWSchnittstellenInterface.MESSAGE_I_REGISTER);
					if (!conMan.sendMessage(msg)) {
						if(conMan.isBound())conMan.doUnbind();
						conMan.destroy();
						conMan = ConnectionManager.create(SearchServices.this);
						new ClassChooserDialog().show(getFragmentManager(), "Search Services");
						chosenServiceIndex = 0;
						conMan.doBind(availableServices, chosenServiceIndex);
					}

					Bundle b = new Bundle();
					b.putString("start", "yes");
					b.putInt("mapSize", mapSize);
					conMan.sendBundleAsMessage3(b, -1, "start");
					switchToGame(mapSize);

				}

			}
		}

	}

	/**
	 * Der Dialog, der einen die Infra-App wählen lässt.
	 * 
	 * @author Vasu
	 * 
	 */
	@SuppressLint("ValidFragment")
	private class ClassChooserDialog extends DialogFragment implements DialogInterface.OnClickListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("choose your service");
			CharSequence[] texts = new CharSequence[availableServices.size()];
			for (int i = 0; i < availableServices.size(); i++) {
				PackageInfo service = availableServices.get(i);
				texts[i] = service.applicationInfo.loadLabel(getPackageManager()) + " (" + service.packageName + ")";
			}
			builder.setItems(texts, this);
			return builder.create();
		}

		public void onClick(DialogInterface dialog, int which) {
			chosenServiceIndex = which;
			conMan.doBind(availableServices, chosenServiceIndex);
			boundToService = true;
			infraActivity = null;
		}

	}
}