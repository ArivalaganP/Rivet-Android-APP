package com.rivet.app.observer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.rivet.app.core.ContentManager;

public class GpsLocationReceiver extends BroadcastReceiver implements LocationListener{        
    @Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public void onReceive(Context context, Intent intent) {
		if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

			if (ContentManager.getContentManager() != null) {
				if (ContentManager.getContentManager().getPlaylistManager() != null) {
					if (ContentManager.getContentManager().getPlaylistManager()
							.getKeyWordBuilder() != null) {

						ContentManager.getContentManager().getPlaylistManager()
								.getKeyWordBuilder().start(true);
					}
				}
			}
		}
	}

    @Override
    public void onLocationChanged(Location arg0) {
    }
}
