package com.ubhave.sensocial.client.tracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ubhave.sensormanager.ESException;

public class LocationTrackerService extends Service {

	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			new TrackLocation(getApplicationContext()).startTracking();
		} catch (ESException e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}

}