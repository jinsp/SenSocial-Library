package com.ubhave.sensocial.mqtt;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.ubhave.sensocial.filters.ConfigurationHandler;
import com.ubhave.sensocial.filters.DownloadFilter;
import com.ubhave.sensocial.filters.FilterSettings;
import com.ubhave.sensocial.filters.ServerStreamRegistrar;
import com.ubhave.sensocial.sensormanager.AllPullSensors;
import com.ubhave.sensocial.sensormanager.OneOffSensing;
import com.ubhave.sensormanager.ESException;

public class NotificationParser {
	private Context context;
	private SharedPreferences sp;
	protected NotificationParser(Context context){
		this.context=context;
		sp=context.getSharedPreferences("SNnMB", 0);
	}
	
	protected void takeAction(String message){
		Log.i("SNnMB", "MQTT message: "+message);
		if(message.startsWith(MQTTNotifitions.start_stream.getMessage())){
			String streamId=message.substring(13);  //<<message --> start_stream:streamId>>
			ServerStreamRegistrar.addStreamId(streamId);
			String url=sp.getString("url", null)+streamId+".xml";
			String destination="/mnt/sdcard/"+streamId+".xml";
			new DownloadFilter(context, url, destination).execute();
		}
		else if(message.startsWith(MQTTNotifitions.stop_stream.getMessage())){
			String streamId=message.substring(12);
			ServerStreamRegistrar.removeStreamId(streamId);
			FilterSettings.removeConfiguration(streamId);
			ConfigurationHandler.run(context);
		}
		else if(message.startsWith(MQTTNotifitions.pause_stream.getMessage())){
			String streamId=message.substring(13);
			FilterSettings.stopConfiguration(streamId);
			ConfigurationHandler.run(context);
		}
		else if(message.startsWith(MQTTNotifitions.unpause_stream.getMessage())){
			String streamId=message.substring(15);
			FilterSettings.startConfiguration(streamId);
			ConfigurationHandler.run(context);
		}
		else if(message.startsWith(MQTTNotifitions.facebook_update.getMessage())){
			AllPullSensors aps=new AllPullSensors(context);
			SharedPreferences sp=context.getSharedPreferences("SNnMB", 0);
			ArrayList<Integer> sensorIds=new ArrayList<Integer>();
			for(String s:sp.getStringSet("OSNSensorSet", null))
				sensorIds.add(aps.getSensorIdByName(s));
			try {
				new OneOffSensing(context, sensorIds, message).execute();
			} catch (ESException e) {
				Log.e("SNnMB","Error at Notification parser: "+e.toString());
			}
		}
		else if(message.startsWith(MQTTNotifitions.nearby_bluetooths.getMessage())){
			
		}
	}
}