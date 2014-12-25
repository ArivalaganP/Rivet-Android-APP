package com.rivet.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mobileapptracker.BuildConfig;
import com.rivet.app.abstracts.IGPSActivity;
import com.rivet.app.common.ActivitiesUserApp;
import com.rivet.app.common.GPS;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.pojo.Story;
import com.rivet.app.social.PostUserAction;
import com.rivet.app.store.PrefStore;

public class BaseActivity extends Activity   implements IGPSActivity{
	
	public PrefStore prefStore;
	public boolean runTime;
	public Long lastRefreshedTime;
	public String uuid;
	public static String TAG="BaseActivity";
	public GPS gps=null;
	public LocationPoints locationPoints=null;
	public String uid;
	public String versionName;
	
	
	@Override
	protected void onStart() {
		super.onStart();	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 

		prefStore = new PrefStore(this);
		runTime = prefStore.getBooleanData(RConstants.RUNNING_FIRST_TIME, true);
		RConstants.BUILD_DEBUBG = BuildConfig.DEBUG;
		lastRefreshedTime = prefStore.getLongData(RConstants.LAST_REFRESHED_TIME, Long.valueOf(0));

		// get device id to send in action logs
		uuid = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		// use uuid in place of android device id if device id is null
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
		}

		locationPoints = new LocationPoints();
		// flurry registered
		FlurryAgent.setContinueSessionMillis(15000);
		FlurryAgent.onStartSession(this, RConstants.FLURRY_API_KEY);

		gps = new GPS(this);
		uid = prefStore.getStringData(RConstants.UID , "");
		if (uid.isEmpty()) {
			uid = uuid;
		}

		versionName = null;
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	 public GPS getGPS(){
		 return gps;
	 }
	 
	 public LocationPoints getLocationPoints(){
		 return locationPoints;
	 }
	/**
	 * 
	 * @param tag : pass a specific tag to recognize the error
	 * @param errorMsg : pass error message to show on log cat
	 */
	public void logI(String tag, String errorMsg) {
		Log.i(tag, errorMsg);
	}

	public void logv(String tag, String msg) {
		Log.v(tag, msg);
	}
	
	public void showToast(final String message){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
			
				Toast.makeText(getApplicationContext(), message , Toast.LENGTH_LONG).show();
			}
		});
		
	}
	
	
	public void postRareLogsToServerAndFlurry(int userAction){
		
	  String userActivity = ActivitiesUserApp.getAppropriateAction(userAction);

	    
		String actionLogsToPostServer = "[{\n" + '"' + RConstants.ACTION_TYPE + '"'
				+ " : " + '"' + userActivity + '"' + "," + "\n" + '"'
				+ RConstants.ACTIVITY_TIMESTAMP + '"' + " : " + '"'
				+ (System.currentTimeMillis() / 1000) + '"' + "," + "\n" + '"'
				+ RConstants.USER_ID + '"' + " : " + '"' + uuid + '"' + "," + "\n" + '"'
				+ RConstants.DATA_LOGS + '"' + " : " + '"' + "{" + "\\n    " + "\\" + '"'
				+ RConstants.APP_VERSION + "\\" + '"' + " : " + "\\" + '"' + versionName
				+ "\\" + '"' + "," + "\\n    " + "\\" + '"' + RConstants.PLATFORM+ "\\"
				+ '"' + " : " + "\\" + '"' + RConstants.OS_NAME + "\\" + '"' + ","
				+ "\\n    " + "\\" + '"' + RConstants.GIGYA_UID + "\\" + '"' + " : "
				+ "\\" + '"' + uid + "\\" + '"' + "," + "\\n    " + "\\" + '"'
				+ RConstants.LATITIDE + "\\" + '"' + " : " + locationPoints.getLatts()
				+ "," + "\\n    " + "\\" + '"' + RConstants.LONGTITUDE + "\\" + '"'
				+ " : " + locationPoints.getLongs() + "\\n" + "}" + '"' + "}]";
	  
	  	PostUserAction postUserAction = new PostUserAction(actionLogsToPostServer);
		postUserAction.startLogging();
		
		
		Map<String, String> logsParams = new HashMap<String, String>();
		String logKey = userActivity;
	  
	  	logsParams.put(RConstants.ACTION_TYPE, userActivity);
	  	logsParams.put(RConstants.ACTIVITY_TIMESTAMP, String.valueOf((System.currentTimeMillis()/1000)));  
	  	logsParams.put(RConstants.USER_ID , uid);
	  	logsParams.put(RConstants.APP_VERSION , versionName);
	  	logsParams.put(RConstants.PLATFORM, RConstants.OS_NAME);
	  	logsParams.put(RConstants.LATITIDE, String.valueOf(locationPoints.getLatts()));
	  	logsParams.put(RConstants.LONGTITUDE, String.valueOf(locationPoints.getLongs()));
	  	
		PostLogsToFlurryTask postLogsToFlurryTask = new PostLogsToFlurryTask(logsParams,logKey);
		Thread postLogsToFlurryThread = new Thread(postLogsToFlurryTask);
		postLogsToFlurryThread.start();	   

	}
	
	
	
	public void postLogsToServerAndInDatabaseForFlurry(int userAction){
		
		
		Story currentStory=null;
		
		    String userActivity =	ActivitiesUserApp.getAppropriateAction(userAction);
		    
		    if(userAction == RConstants.ActivityNewStoryStarted && (!ContentManager.getContentManager(BaseActivity.this).getFlagNoStoryToPlay())){
		    	
		    	currentStory = ContentManager.getContentManager(BaseActivity.this).getPlaylistManager().getNextStory();
		    	
		    }else{ 
		    	currentStory = ContentManager.getContentManager(BaseActivity.this).getPlaylistManager().getCurrentStory();
		    
		    }
		    if(currentStory!=null){
			    
		    	String currentTrackId = String.valueOf(currentStory.getTrackID());
			    String currentPositionOfAudioTrack = String.valueOf(RConstants.currentPosOfAudioPlayer);
			    String currentTrackTitle =currentStory.getTitle();
			    
			    
			    // post logs to flurry
			    Map<String, String> logsParams = new HashMap<String, String>();
			    logsParams.put(RConstants.CURRENT_TRACK_ID, currentTrackId);
				logsParams.put(RConstants.CURRENT_TRACK_POSITIONS_SECONDS, currentPositionOfAudioTrack);  
				logsParams.put(RConstants.CURRENT_TRACK_TITLE, currentTrackTitle);
				String logKey = userActivity;
			    PostLogsToFlurryTask postLogsToFlurryTask = new PostLogsToFlurryTask(logsParams,logKey);
			    Thread postLogsToFlurryThread = new Thread(postLogsToFlurryTask);
			    postLogsToFlurryThread.start();	   
		    
		
		    
			String actionLogsToPostServer = "[{\n" + '"' + RConstants.ACTION_TYPE + '"'
					+ " : " + '"' + userActivity + '"' + "," + "\n" + '"'
					+ RConstants.ACTIVITY_TIMESTAMP + '"' + " : " + '"'
					+ (System.currentTimeMillis() / 1000) + '"' + "," + "\n"
					+ '"' + RConstants.USER_ID + '"' + " : " + '"' + uuid + '"' + ","
					+ "\n" + '"' + RConstants.DATA_LOGS + '"' + " : " + '"' + "{" + "\\n    "
					+ "\\" + '"' + RConstants.APP_VERSION + "\\" + '"' + " : " + "\\"
					+ '"' + versionName + "\\" + '"' + "," + "\\n    " + "\\"
					+ '"' + RConstants.PLATFORM + "\\" + '"' + " : " + "\\" + '"'
					+ RConstants.OS_NAME + "\\" + '"' + "," + "\\n    " + "\\" + '"'
					+ RConstants.GIGYA_UID + "\\" + '"' + " : " + "\\" + '"' + uid + "\\"
					+ '"' + "," + "\\n    " + "\\" + '"' + RConstants.CURRENT_TRACK_ID
					+ "\\" + '"' + " : " + currentStory.getTrackID() + ","
					+ "\\n    " + "\\" + '"' + RConstants.CURRENT_TRACK_TITLE + "\\" + '"'
					+ " : " + "\\" + '"' + currentStory.getTitle() + "\\" + '"'
					+ "," + "\\n    " + "\\" + '"'
					+ RConstants.CURRENT_TRACK_POSITIONS_SECONDS + "\\" + '"' + " : "
					+ RConstants.currentPosOfAudioPlayer + "\\n" + "}" + '"' +"}]"; 
		    
		
			PostUserAction postUserAction = new PostUserAction(actionLogsToPostServer);
			postUserAction.startLogging();
			
		    }
		
	    
	}
	
	
	
	public  Bitmap getBitmapFromURL(String src) {
	     try {
	         URL url = new URL(src);
	         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	         connection.setDoInput(true);
	         connection.connect();
	         InputStream input = connection.getInputStream();
	         Bitmap myBitmap = BitmapFactory.decodeStream(input);
	         return myBitmap;
	     } catch (IOException e) {
	         e.printStackTrace();
	         return null;
	     }
	 } 
	
	
	private class PostLogsToFlurryTask implements Runnable{
		
		private Map<String, String> logParams;
		private String logKey;
		public PostLogsToFlurryTask(Map<String, String> logParams,String logkey){
			this.logParams = logParams;
			this.logKey = logkey;
		}

		@Override
		public void run() {
			  
				FlurryAgent.logEvent(logKey , logParams);
		}
		
	}
	
	
	   public void locationChanged(double longitude, double latitude) {
	        Log.d(TAG, "Main-Longitude: " + longitude);
	        Log.d(TAG, "Main-Latitude: " + latitude);
	        locationPoints.setLatts(latitude);
	        locationPoints.setLongs(longitude);
	   }


	    @Override
	    public void displayGPSSettingsDialog() {
	    	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
	    }

	    
	    public class LocationPoints{
	    	
		     double Latts;
		     double longs;
		     public double getLatts() {
				return Latts;
			}
			public void setLatts(double latts) {
				Latts = latts;
			}
			public double getLongs() {
				return longs;
			}
			public void setLongs(double longs) {
				this.longs = longs;
			}
			
	  
	    }

		@Override
		protected void onDestroy() {
		
			super.onDestroy();
			FlurryAgent.onEndSession(this);
			
			
		
		}
		
		public void failedToLoadAds(){
			
			   String userActivity = ActivitiesUserApp.getAppropriateAction(RConstants.ActivityFailedToLoadAds);
			
					String actionLogsToPostServer = "[{\n" + '"' + RConstants.ACTION_TYPE + '"'
			+ " : " + '"' + userActivity + '"' + "," + "\n" + '"'
			+ RConstants.ACTIVITY_TIMESTAMP + '"' + " : " + '"'
			+ (System.currentTimeMillis() / 1000) + '"' + "," + "\n" + '"'
			+ RConstants.USER_ID + '"' + " : " + '"' + uid + '"' + "," + "\n" + '"'
			+ RConstants.DATA_LOGS + '"' + " : " + '"' + "{" + "\\n    " 
			+ "\\" + '"' + RConstants.APP_VERSION + "\\" + '"' + " : " + "\\" + '"' + versionName+ "\\" + '"' + "," + "\\n    "
			+ "\\" + '"' + RConstants.PLATFORM + "\\" + '"' + " : " + "\\" + '"' + RConstants.OS_NAME + "\\" + '"' + "," + "\\n    " 
			+ "\\" + '"' + RConstants.GIGYA_UID + "\\" + '"' + " : " + "\\" + '"' + uid + "\\" + '"' + "\\n" + "}" + '"' + "}]";
				  
					  PostUserAction postUserAction = new PostUserAction(actionLogsToPostServer);
					  postUserAction.startLogging();
		}

	    
	    

}
