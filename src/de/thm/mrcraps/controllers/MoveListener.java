package de.thm.mrcraps.controllers;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.View.OnTouchListener;

/**
 * Diese Klasse reagiert auf die Movementbefehle des Users
 * 
 * @author Benedikt
 * 
 */
public class MoveListener extends SimpleOnGestureListener implements SensorEventListener, OnTouchListener {

	public static final int FLING_MOVEMENT = 0;
	public static final int GYRO_MOVEMENT = 1;
	public static final int DOUBLE_TAP = 2;
	public static final int SINGLE_TAP = 3;
	private Handler handler;
	private GestureDetector swipeDetector;
	private boolean leftBegonnen = false;
	private boolean rightBegonnen = false;
	private boolean downBegonnen = false;
	private boolean upBegonnen = false;
	private long mFirstGyroChangeTime = 0;
	private long mLastGyroChangeTime;
	private long gyroWait = System.currentTimeMillis();
	private long flingWait = System.currentTimeMillis();

	private final int WAITTIME = 150;
	private final int GYRO_WAITTIME = 250;
	private final double SCHWELLENWERT = 2.5;
	private final int MAX_ZEITWERT = 1000;
	private final int FLING_SENSIVITY = 60;

	public MoveListener(Handler handler, Activity view) {
		this.handler = handler;
		swipeDetector = new GestureDetector(view.getBaseContext(), this);
	}

	public boolean onTouch(View view, MotionEvent event) {
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		return swipeDetector.onTouchEvent(event);
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (System.currentTimeMillis() > flingWait) {
			if ((e1.getX() - e2.getX()) > FLING_SENSIVITY) {
				handler.sendMessage(Message.obtain(handler, MoveListener.FLING_MOVEMENT, 0, 1));
			} else if ((e2.getX() - e1.getX()) > FLING_SENSIVITY) {
				handler.sendMessage(Message.obtain(handler, MoveListener.FLING_MOVEMENT, 0, -1));
			} else if ((e1.getY() - e2.getY()) > FLING_SENSIVITY) {
				handler.sendMessage(Message.obtain(handler, MoveListener.FLING_MOVEMENT, 1, 0));
			} else if ((e2.getY() - e1.getY()) > FLING_SENSIVITY) {
				handler.sendMessage(Message.obtain(handler, MoveListener.FLING_MOVEMENT, -1, 0));
			}
			flingWait = System.currentTimeMillis() + WAITTIME;
		}
		return super.onFling(e1, e2, velocityX, velocityY);
	};

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		handler.sendMessage(Message.obtain(handler, MoveListener.SINGLE_TAP, (int) e.getX(), (int) e.getY()));
		return super.onSingleTapConfirmed(e);
	}

	public boolean onDoubleTap(MotionEvent e) {
		handler.sendMessage(Message.obtain(handler, MoveListener.DOUBLE_TAP, 0, 0));
		return super.onDoubleTap(e);
	}

	@Override
	public void onSensorChanged(SensorEvent se) {
		float x = 0;
		float y = 0;
		if (System.currentTimeMillis() > gyroWait) {
			x = se.values[0];
			y = -se.values[1];
			long now = System.currentTimeMillis();
			if (mFirstGyroChangeTime == 0) {
				mFirstGyroChangeTime = now;
				mLastGyroChangeTime = now;
			}
			if (mLastGyroChangeTime - mFirstGyroChangeTime < MAX_ZEITWERT) {
				if (!leftBegonnen && !rightBegonnen && !upBegonnen && !downBegonnen) {
					if (x > SCHWELLENWERT) {
						Log.e("X:", "RIGHT_START" + x);
						rightBegonnen = true;
						mLastGyroChangeTime = now;
					} else if (x < -SCHWELLENWERT) {
						Log.e("X:", "LEFT_START" + x);
						leftBegonnen = true;
						mLastGyroChangeTime = now;
					} else if (y < -SCHWELLENWERT) {
						Log.e("Y:", "UP_START" + y);
						upBegonnen = true;
						mLastGyroChangeTime = now;
					} else if (y > SCHWELLENWERT) {
						Log.e("Y:", "DOWN_START" + y);
						downBegonnen = true;
						mLastGyroChangeTime = now;
					}
				}

				if (leftBegonnen) {
					if (x > 0) {
						Log.e("X:", "LEFT_END" + x);
						leftBegonnen = false;
						handler.sendMessage(Message.obtain(handler, MoveListener.GYRO_MOVEMENT, 0, 1));
						mFirstGyroChangeTime = 0;
						gyroWait = System.currentTimeMillis() + GYRO_WAITTIME;
					}
				}
				if (rightBegonnen) {
					if (x < 0) {
						Log.e("X:", "RIGHT_END" + x);
						rightBegonnen = false;
						handler.sendMessage(Message.obtain(handler, MoveListener.GYRO_MOVEMENT, 0, -1));
						mFirstGyroChangeTime = 0;
						gyroWait = System.currentTimeMillis() + GYRO_WAITTIME;
					}
				}
				if (upBegonnen) {
					if (y > 0) {
						Log.e("Y:", "UP_END" + y);
						upBegonnen = false;
						handler.sendMessage(Message.obtain(handler, MoveListener.GYRO_MOVEMENT, 1, 0));
						mFirstGyroChangeTime = 0;
						gyroWait = System.currentTimeMillis() + GYRO_WAITTIME;
					}
				}
				if (downBegonnen) {
					if (y < 0) {
						Log.e("Y:", "DOWN_END" + y);
						downBegonnen = false;
						handler.sendMessage(Message.obtain(handler, MoveListener.GYRO_MOVEMENT, -1, 0));
						mFirstGyroChangeTime = 0;
						gyroWait = System.currentTimeMillis() + GYRO_WAITTIME;
					}
				}
			} else {
				leftBegonnen = false;
				rightBegonnen = false;
				downBegonnen = false;
				upBegonnen = false;
				mFirstGyroChangeTime = 0;
				gyroWait = System.currentTimeMillis();
			}
		}
	}
}
