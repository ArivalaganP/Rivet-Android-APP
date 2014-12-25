package com.rivet.app.common;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.rivet.app.abstracts.IGPSActivity;

public class GPS {

    private IGPSActivity main;

    // Helper for GPS-Position
    private LocationListener mlocListener;
    private LocationManager mlocManager;
    public boolean isProviderDisabled=false;
    private boolean isRunning;
 // flag for GPS status
    private  boolean isGPSEnabled = false;
 
    // flag for network status
    boolean isNetworkEnabled = false;
 
    // flag for GPS status
    boolean canGetLocation = false;
 
    
    
    public boolean isProviderDisabled(){
    	boolean value = true ;
    	
    	  isGPSEnabled = mlocManager
                  .isProviderEnabled(LocationManager.GPS_PROVIDER);

           isNetworkEnabled = mlocManager
                  .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
           
           if(isGPSEnabled || isNetworkEnabled){
          	 value = false ;
           }else{
          	 value = true ;
           }
           
           return value ;
           
    }
 
    public GPS(Context ctx ) {
    
        this.main = (IGPSActivity)ctx;
    	

        // GPS Position
        mlocManager = (LocationManager) (ctx).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
       
        isGPSEnabled = mlocManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = mlocManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        	GPS.this.isProviderDisabled=true;
        	
        }else{
        	// mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        	 this.canGetLocation = true;
             // First get location from Network Provider
             if (isNetworkEnabled) {
            	 mlocManager.requestLocationUpdates(
                         LocationManager.NETWORK_PROVIDER,
                         0,
                         0, mlocListener);
                 
                 
             }
             // if GPS Enabled get lat/long using GPS Services
             if (isGPSEnabled) {
                
            	 mlocManager.requestLocationUpdates(
                             LocationManager.GPS_PROVIDER,
                             0,
                             0, mlocListener);
                    
                    
                 }
             }
        
        // GPS Position END
        this.isRunning = true;
    }
    
    public void startGPS() {
    	
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        this.isRunning = true;
	}

    public void stopGPS() {
        if(isRunning) {
            mlocManager.removeUpdates(mlocListener);
            this.isRunning = false;
        }
    }

    public void resumeGPS() {
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        this.isRunning = true;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location loc) {
        	
            GPS.this.main.locationChanged(loc.getLongitude(), loc.getLatitude());
        	
        }

        @Override
        public void onProviderDisabled(String provider) {
            //GPS.this.main.displayGPSSettingsDialog();
        	GPS.this.isProviderDisabled=true;
        }

        @Override
        public void onProviderEnabled(String provider) {
        	GPS.this.isProviderDisabled=false;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        	
        	
        }

    }

}

