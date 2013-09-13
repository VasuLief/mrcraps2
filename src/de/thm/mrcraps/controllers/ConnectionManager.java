package de.thm.mrcraps.controllers;

import java.util.List;

import de.thm.mrcraps.Common;
import de.thm.mrcraps.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/**
 * Hält die Verbindung zu unserer Schnittstelle
 * 
 * @author Vasu
 * 
 */
public class ConnectionManager implements MOWSchnittstellenInterface {

	private static ConnectionManager instance;

	public final static String SERVICE_PACKAGE = "de.thm.mps";

	public static final int UNREGISTER_CLIENT = 1;
	public static final int REGISTER_CLIENT = 1;
	public static final int RECEIVED = 11;
	public static final int TO_SENT = 1;

	public static final String DATA = "data";
	public static final String RECEIVER = "receiver";
	public static final String SENDER = "sender";
	public static final int PARCEL_DATA = 3474;

	private Context context;

	// Connection Stuff
	private final Messenger messengerActivity = new Messenger(new Receiver());
	private Messenger messengerService;
	private ServiceConnection serviceConnection = new MyServiceConnection();
	private boolean boundToService = false;

	// Variablen
	private int myID;

	// Messenger zum weiterleiten von eingehenden Nachrichten
	private static Messenger m;

	/**
	 * Konstruktor mit Context.
	 * 
	 * @param context
	 */
	private ConnectionManager(Context context) {
		this.context = context;
		this.messengerService = null;
	}

	/**
	 * Singleton Zeugs und so
	 * 
	 * @param context
	 * @return
	 */
	public static ConnectionManager create(Context context) {
		if (instance == null)
			instance = new ConnectionManager(context);

		return instance;
	}

	public int isHost() {
		if (myID == 0)
			return Common.GAME_HOST;
		return Common.GAME_CLIENT;
	}

	public int getMyID() {
		return myID;
	}

	public boolean isBound() {
		return boundToService;
	}

	/**
	 * Diese Methode versicht die Nachticht msg an den Service. Die Nachticht
	 * muss vorher erstellt werden.
	 * 
	 * @param msg
	 */
	public boolean sendMessage(Message msg) {

		try {
			msg.replyTo = messengerActivity;
			if (messengerService != null) {
				messengerService.send(msg);
				boundToService = true;
				return true;
			}

			else {
				boundToService = false;
				return false;
			}
			// Log.i("CM", "theoretisch sollte was verschickt worden sein");
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * Nachricht III what: public final int MESSAGE_3_SEND=3; Bundle der
	 * Nachricht III enthält folgende Keys: public final static String
	 * SEND_ADDRESS="message3.address.id"; // Gibt String zurück public final
	 * static String SEND_TYPE="message3.datatype"; //Gibt String zurück public
	 * final static String SEND_DATA="message3.data"; //Gibt Rohdaten zurück
	 */
	public void sendBundleAsMessage3(Bundle dataBundle, int who, String type) {

		Parcel parcelData = Parcel.obtain();
		parcelData.writeBundle(dataBundle);
		byte[] bytes = parcelData.marshall();

		Bundle messageBundle = new Bundle();
		messageBundle.putInt(MOWSchnittstellenInterface.SEND_ADDRESS, who);
		messageBundle.putString(MOWSchnittstellenInterface.SEND_TYPE, type);
		messageBundle.putByteArray(MOWSchnittstellenInterface.SEND_DATA, bytes);
		Message m = Message.obtain(null, MOWSchnittstellenInterface.MESSAGE_3_SEND);
		m.setData(messageBundle);
		sendMessage(m);

	}

	/**
	 * Hier wird verbunden
	 * 
	 * @param availableServices
	 * @param chosenServiceIndex
	 */
	public void doBind(List<PackageInfo> availableServices, int chosenServiceIndex) {
		Intent i = new Intent();
		PackageInfo chosenService = availableServices.get(chosenServiceIndex);

		i.setClassName(chosenService.packageName, chosenService.packageName + "." + chosenService.applicationInfo.loadLabel(context.getPackageManager()));
		Log.d("CM", chosenService.packageName + "." + chosenService.applicationInfo.loadLabel(context.getPackageManager()));
		context.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
		boundToService = true;
		Log.i("CM", "Verbindungsaufbau zum Service gestartet!");
	}

	/**
	 * Hier wird getrennt
	 */
	public void doUnbind() {
		if (boundToService) {

			context.unbindService(serviceConnection);
			boundToService = false;

			Log.e("CM", "Service ist nicht mehr an Spiele-App gebunden!");
		}
	}

	/**
	 * Diese Klasse wird beim Verbinden und Beenden aktiv
	 * 
	 * @author Vasu
	 * 
	 */
	private class MyServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName name, IBinder service) {
			messengerService = new Messenger(service);

			// make myself public to the service
			Message msg = Message.obtain(null, MOWSchnittstellenInterface.MESSAGE_I_REGISTER);
			msg.replyTo = messengerActivity;
			try {
				messengerService.send(msg);
			} catch (RemoteException e) {
				Log.e("CM", e.getClass().getName() + ": " + e.getMessage());
			}
			boundToService = true;

			Log.e("Multi", "Service ist nun verbunden!");
		}

		public void onServiceDisconnected(ComponentName name) {
			messengerService = null;
			boundToService = false;

			Log.e("Multi", "Service-Verbindung wurde getrennt!");
		}
	}

	/**
	 * Hier können sich Receiver zum empfangen der Nachrichten eintragen
	 * 
	 * @param receiver2
	 */
	public void registerReceiver(Messenger receiver2) {
		m = receiver2;
	}

	/**
	 * Hier kommen alle Nachtichten der Infra-App an. Sie werden zur Zeit an die
	 * SearchServices-Activity weitergeleitet, am Ende sollten sie an die zur
	 * Zeit zuständige Activity weitergeleitet werden (MultiplayerActivity z.
	 * B.)
	 * 
	 * @author Vasu
	 * 
	 */
	private static class Receiver extends Handler {
		@Override
		public void handleMessage(Message msg) {

			if (m != null) {
				try {
					m.send(Message.obtain(msg));
				} catch (RemoteException e) {
					// something went wrong
					Log.e("CM", "Fehler beim zustellen der Nachricht");
				}
			}

		}
	}

	public void destroy() {

		instance = null;

	}

}
