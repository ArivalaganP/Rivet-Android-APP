package com.rivet.app.social;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rivet.app.BaseActivity;
import com.rivet.app.HomeActivity;
import com.rivet.app.R;
import com.rivet.app.common.ConnectionDetector;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.AlertDialogManager;
import com.rivet.app.core.ContentManager;

public class ShareOnTwitter {

	

	// Internet Connection detector
	private ConnectionDetector cd;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();
	private Button btnUpdateStatus;
	private String status;
	private TextView lblUpdate;
	private Button cancelBT;
	private Context context;
	private String TAG = "ShareOnTwitter" ;
	private Dialog dialog;
	
	public ShareOnTwitter(Context context) {
		this.context = context ;
	}
	
	public void startShareOnTwitter(){
		// post log while share with twitter
		JSONObject jsonObjectLog = new JSONObject();
		try {
			
			jsonObjectLog.put("currentTrackTitle", (ContentManager.getContentManager(context).getPlaylistManager().getCurrentStory().getTitle()));
			jsonObjectLog.put("currentTrackPositionInSeconds", String.valueOf(RConstants.currentPosOfAudioPlayer));
			jsonObjectLog.put("currentTrackId", String.valueOf(ContentManager.getContentManager(context).getPlaylistManager().getCurrentStory().getTrackID()));
			
		} catch (JSONException exception) {
			
			exception.printStackTrace();
		}		
				// connection detector code
				cd = new ConnectionDetector(context);

				if (!cd.isConnectingToInternet()) {
					alert.showAlertDialog(context ,
							RConstants.INTERNET_CONNECTION_ERROR,
							RConstants.WROKING_INTERNET_CONNECTION, false);
					return;
				}

				// Check if twitter keys are set
				if (RConstants.TWITTER_CONSUMER_KEY.trim().length() == 0
						|| RConstants.TWITTER_CONSUMER_SECRET.trim().length() == 0) {
					alert.showAlertDialog(context , "Twitter oAuth tokens",
							"Please set your twitter oauth tokens first!", false);
					return;
				}
				
				 // getting the status to share via twitter
				
				getContentOfStatus();
				

				loginToTwitter();
	}


     public void getContentOfStatus() {
    	 
    	
    	 
		 String storyTitle = (ContentManager.getContentManager(context).getPlaylistManager().getCurrentStory().getTitle()) ;
		 String storyURL = RConstants.SHARE_STORY_URL + (ContentManager.getContentManager(context).getPlaylistManager().
				                                                                                 getCurrentStory().getTrackID());
		 
//		  status = storyTitle + "\n" + "<a href=\"" + storyURL + "\">" ;	
		  status=storyTitle+"\n"+storyURL;
		  
		  
	}

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(RConstants.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(RConstants.TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();

			TwitterFactory factory = new TwitterFactory(configuration);
			RConstants.twitter = factory.getInstance();

			Thread thread = new Thread(new Runnable() {
			

				@Override
				public void run() {
					try {

						RConstants.requestToken = RConstants.twitter
								.getOAuthRequestToken(RConstants.TWITTER_CALLBACK_URL);
						((BaseActivity)context).runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(RConstants.requestToken.getAuthenticationURL())));
							}
						});
				

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		} else {
			// if already logged in then visible the contents 
			
			createTwitterDialog();
			makeStatusVisible();
		}
	}

	/**
	 * Function to update status
	 * */
	class updateTwitterStatus extends AsyncTask<String, String, String> {


		private ProgressDialog progressDialog;
	

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
					 progressDialog = ProgressDialog.show(context,
	                    context.getResources().getString(R.string.app_name), "Updating status, please wait...", true, true);
					 progressDialog.setIndeterminate(false);
					 progressDialog.setCancelable(false);
	            progressDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			if(RConstants.BUILD_DEBUBG){
			Log.i(TAG,   args[0]);
			}
			String status = args[0];
			try {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(RConstants.TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(RConstants.TWITTER_CONSUMER_SECRET);

				// Access Token
				String access_token =((BaseActivity)context).prefStore.getStringData(RConstants.PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = ((BaseActivity)context).prefStore.getStringData(RConstants.PREF_KEY_OAUTH_SECRET, "");

				AccessToken accessToken = new AccessToken(access_token,access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build())
						.getInstance(accessToken);

				// Update status
				twitter4j.Status response = twitter.updateStatus(status);
				
				// send log to flurry
				
			((BaseActivity)context).postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityShareOnTwitter);
				
          if(RConstants.BUILD_DEBUBG){
				Log.i(TAG , "> " + response.getText());
                  }
			} catch (TwitterException e) {
				// Error in updating status
				if(RConstants.BUILD_DEBUBG){
				Log.e(TAG, e.getMessage());
				}
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(String file_url) {
			if(progressDialog != null)
				progressDialog.dismiss();
			Toast.makeText(context , "Status posted successfully", Toast.LENGTH_SHORT).show();
			dismissTwitterDialog();
		}

	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	public boolean isTwitterLoggedInAlready() {
	boolean login = ((BaseActivity)context).prefStore.getBooleanData(RConstants.PREF_KEY_TWITTER_LOGIN, false);
		return login ;
	}

	

	public void makeStatusVisible() {
		lblUpdate.setText(status);	
		
	}
	
	public void createTwitterDialog(){
		 dialog = new Dialog(context);
		dialog.setContentView(R.layout.activity_twitter);
		dialog.setTitle("Twitter");
		btnUpdateStatus = (Button) dialog.findViewById(R.id.btnUpdateStatus);
		lblUpdate = (TextView) dialog.findViewById(R.id.lblUpdate);
		cancelBT = (Button) dialog.findViewById(R.id.cancelBT);
		
		
		/**
		 * Button click event to Update Status, will call updateTwitterStatus()
		 * function
		 * */
		
		cancelBT.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v){
				dialog.dismiss();
			}
		});
		
		 btnUpdateStatus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 new updateTwitterStatus().execute(status);
			}
		});

		dialog.show();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((HomeActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		int actualHeight = (height * 60) / 100 ;
		Window window = dialog.getWindow();
		window.setLayout((width - 30), actualHeight);
	  }
	
	
	
	public void dismissTwitterDialog(){
		if(dialog != null)
		dialog.dismiss();
	}
	
	}

