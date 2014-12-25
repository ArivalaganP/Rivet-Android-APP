package com.rivet.app;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.flurry.android.FlurryAgent;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.alertviews.BoschIVIAlertView;
import com.rivet.app.common.ConnectionDetector;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.pojo.Category;

public class SplashActivity extends BaseActivity  implements MySpinServerSDK.ConnectionStateListener{
	
	private static final String TAG = "SplashActivity";
	ConnectionDetector connectionDetector = new ConnectionDetector(SplashActivity.this);
	Context ctx = SplashActivity.this;
	private CategoryDBAdapter database;
	private TextView errorMsgTV;
	private int showMeOnlyOnce = 1 ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    
		setContentView(R.layout.activity_splash);
		
		
		/*try {
	        PackageInfo info = getPackageManager().getPackageInfo("com.rivet.app",  PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            System.out.println("KeyHash:"+Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            Log.i("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }*/
		
		
	    
		// take the last run time from 
		if(runTime){
			prefStore.setLongData(RConstants.LAST_RUN_TIME , System.currentTimeMillis());	
		}
		
		errorMsgTV = (TextView) findViewById(R.id.errorMsgTV);
		database = CategoryDBAdapter.getCategoryDBAdapterForWrite(this);
		try {
			MySpinServerSDK.sharedInstance().registerApplication(getApplication());
		} catch (MySpinException e) {
			Log.i(TAG, "register with Bosch Failed");
		}
		
		MovetoNextScreenAsyncTask movetoNextScreenAsyncTask = new MovetoNextScreenAsyncTask();
		movetoNextScreenAsyncTask.execute(null,null,null);
		if(!RConstants.BUILD_DEBUBG){
			Log.i(TAG,"RIVET_LOG Build Type: Production");
		}else{
			Log.i(TAG,"RIVET_LOG Build Type: Debug");
		}
		
		
		
	}

	private class InitiateContentManagerThread extends Thread {
		@Override
		public void run() {
			super.run();
			
			if(connectionDetector.isConnectingToInternet()){
				ContentManager cntManager = ContentManager.getContentManager(SplashActivity.this);
				cntManager.initiate();
			}else{
				
				System.out.println("not connected with Internet");
			}
		}

	}
	
	@Override
	protected void onStart() {
		
		super.onStart();

		// When this activity gets started register for mySPIN connection events
		// in order to
		// adapt views for the according connection state.
		try {
			MySpinServerSDK.sharedInstance().registerConnectionStateListener(this);
		} catch (MySpinException e) {
			e.printStackTrace();
		}

	}
	private class MovetoNextScreenAsyncTask extends AsyncTask<Void, Void, Long>{

		@Override
		protected Long doInBackground(Void... params) {
			try {
				Thread.sleep(3000);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Long result) {
			
			Thread doTAskIfInternetConnection =  new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					for ( ; ; ){
						if (connectionDetector.isConnectingToInternet()) {
							
							runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									
									errorMsgTV.setVisibility(View.GONE);
								}
							});
							
							callFullProcedureInLoopToCheckInternetConnection();
							
							break ;
							
						} else {
							int increment = 0 ;
							if(MySpinServerSDK.sharedInstance().isConnected() && increment == 0){
								increment ++ ;
								FlurryAgent.logEvent("ConnectedWithBoschAndNoInternetConnected" , null);
							}
							
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									if(showMeOnlyOnce == 1){ 
									
									if(!MySpinServerSDK.sharedInstance().isConnected()){
									errorMsgTV.setVisibility(View.VISIBLE);
									} else { new BoschIVIAlertView().showAlertDialog(SplashActivity.this , 1); }}
									
									showMeOnlyOnce ++ ;
								}
							});
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
							
								e.printStackTrace();
							}

							

						}
					}
					
				}
			});
			doTAskIfInternetConnection.start();
				
		
	     }
		

		private void callFullProcedureInLoopToCheckInternetConnection() {
			if (MySpinServerSDK.sharedInstance().isConnected() && runTime) {
				// launch this thread so that category can be built to save
				// selected categories in Mycategory table
				// initiate contentManager for bosch and first time launch

				InitiateContentManagerThread InitiateContentManagerForBosch = new InitiateContentManagerThread();
				InitiateContentManagerForBosch.start();

				CheckCategoryBuildingTask checkCategoryBuildingTask = new CheckCategoryBuildingTask();
				Thread checkCategoryBuildingThread = new Thread(checkCategoryBuildingTask);
				checkCategoryBuildingThread.start();

				try {
					checkCategoryBuildingThread.join();
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}

				// start HomeActivity here
				new Thread(new Runnable() {
					@Override
					public void run() {
						((BaseActivity) ctx).postRareLogsToServerAndFlurry(RConstants.ActivitySplashFinished);
					}
				}).start();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						
						Intent intentHomeActivity = new Intent(SplashActivity.this,HomeActivity.class);
						startActivity(intentHomeActivity);
					}
				});
			

			} else {

				if (!runTime) {
					InitiateContentManagerThread initiateContentManagerThread = new InitiateContentManagerThread();
					initiateContentManagerThread.start();
				}

				// send logs to flurry and server
				new Thread(new Runnable() {
					@Override
					public void run() {
						((BaseActivity) ctx).postRareLogsToServerAndFlurry(RConstants.ActivitySplashFinished);
					}
				}).start();

			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
				
					if (runTime) {
						Intent welcomeIntent = new Intent(SplashActivity.this,WelcomeActivity.class);
						startActivity(welcomeIntent);
						finish();

					} else {
						// this make sure that playlistupdatecomplete
						// contentManager.setStoryLoadTODBComplete(true);
				
						// TODO : find a better solution to overcome this , 
						//STORY_UPLOAD_TO_DB should not enable from here , because it gives old stories to user
						
							
						if(checkIfAppRunningAfterTwoHours()){
							// false is set to build fresh stories after 2 hours
							RConstants.STORY_UPLOAD_TO_DB = false;
						}else{
							RConstants.STORY_UPLOAD_TO_DB = true ;
						}
						// set the last run time again
						prefStore.setLongData(RConstants.LAST_RUN_TIME, System.currentTimeMillis());
						
						Intent homeIntent = new Intent(SplashActivity.this,HomeActivity.class);
						startActivity(homeIntent);
						finish();
					}
				}
			});	
				
			}
		
			
		}

		
		
	}
	
	
	public boolean checkIfAppRunningAfterTwoHours(){
	boolean	shouldStartWithFreshStories = false ;
		
		long timeAgo = getTimeToBuildFreshStories();
		
		int daysAgo = (int) (timeAgo / (1000 * 60 * 60 * 24));
		int hours = (int) ((timeAgo - (1000 * 60 * 60 * 24 * daysAgo)) / (1000 * 60 * 60));
		
		if(daysAgo > 1 || hours > 2){
			shouldStartWithFreshStories = true ;
		}
		
		return shouldStartWithFreshStories;
		
	}
	
	
	@SuppressLint("SimpleDateFormat")
	private long getTimeToBuildFreshStories() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		long currentTime = System.currentTimeMillis();
		String startTimeGMT = dateFormat.format(prefStore.getLongData(RConstants.LAST_RUN_TIME, currentTime));
		String EndTimeGMT = dateFormat.format(currentTime);

		long timeAgo = 0;
		try {

			Date startDate = dateFormat.parse(startTimeGMT);
			Date currentDate = dateFormat.parse(EndTimeGMT);
			timeAgo = currentDate.getTime() - startDate.getTime();

		} catch (ParseException e) {
			
			e.printStackTrace();
		}

		return timeAgo;
	}
	
    
	@Override
	public void onConnectionStateChanged(boolean arg0) {
		
		if (MySpinServerSDK.sharedInstance().isConnected()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.activity_splash);
			
			// show alert view when not connected with Internet
			if(!connectionDetector.isConnectingToInternet()){
				new BoschIVIAlertView().showAlertDialog(SplashActivity.this , 1);
			}
		
			
		} else {
			
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			logI(TAG, "Bosch not connected");
		}
		
	}
	
	
	private void addCategoryInDatabase(String catID) {

		Category category = new Category();
		String categoryName  = null ;
		if(catID.equals(String.valueOf(RConstants.IPOD_MUSIC_CATEGORY))){
			categoryName = "Music from the device" ;
		}else{
		 categoryName = database.getCategoryNameByCategoryId(Integer.parseInt(catID));
		}
		category.setName(categoryName);
		category.setCategoryId(Integer.parseInt(catID));
		category.setIsChecked(true);
		database.addMyCategory(category);
		

	}
	
	private class CheckCategoryBuildingTask implements Runnable{

		@Override
		public void run() {
			

					for( ; ; ){
						
						if(ContentManager.getContentManager(SplashActivity.this).isCategorybuildingComplete()){
							
							// bosch implementation when application launch first time
							addCategoryInDatabase(String.valueOf(RConstants.TOP_NEWS_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.TECHNOLOGY_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.POLITICS_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.SCIENCE_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.BUSINESS_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.LIFE_STYLE_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.SPORTS_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.CRIME_AND_COURT));
							addCategoryInDatabase(String.valueOf(RConstants.IPOD_MUSIC_CATEGORY));
							addCategoryInDatabase(String.valueOf(RConstants.ENTERTAINMENT_CATEGORY));
							
							break ;
							
						}else{
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								
								e.printStackTrace();
							}
						}
					}
				}
			}

}
