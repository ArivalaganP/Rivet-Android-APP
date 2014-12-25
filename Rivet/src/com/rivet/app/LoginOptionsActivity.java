package com.rivet.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.flurry.android.FlurryAgent;
import com.gigya.json.JSONException;
import com.gigya.json.JSONObject;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.rivet.app.common.ActivitiesUserApp;
import com.rivet.app.common.ConnectionDetector;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.FacebookUser;
import com.rivet.app.social.PasswordDialogToMergeAccount;
import com.rivet.app.social.PostUserAction;

public class LoginOptionsActivity extends BaseActivity implements
		View.OnClickListener, MySpinServerSDK.ConnectionStateListener {

	Context context = this;
	FbResponseListener fbResponseListener;
	private String TAG = "LoginOptionsActivity";
	Button backBttn;
	RelativeLayout fbLoginRl, signInRl, signUpRl;
	ImageView backIv, skipIv;
	TextView skipTv;
	ProgressDialog progressDialog;
	private Typeface metaProTypeFace;
	private TextView tvStayAhead;
	private TextView tvLoginReason;
	private TextView tvLoginFb;
	private TextView tvLogin;
	private TextView tvSignUp;
	private TextView headerTitleTV;
	private boolean isFirstTime;
	
	
	
	
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_options);
		isFirstTime = ((BaseActivity) context).prefStore.getBooleanData(
				RConstants.RUNNING_FIRST_TIME, true);
		initGigya();
		metaProTypeFace = Typeface.createFromAsset(getAssets(),
				"MetaPro_Book.otf");
		fbLoginRl = (RelativeLayout) findViewById(R.id.rl_fb_login);
		tvStayAhead = (TextView) findViewById(R.id.tv_stay_ahead);
		headerTitleTV = (TextView) findViewById(R.id.header_titleTV);
		tvLoginReason = (TextView) findViewById(R.id.tv_login_reason);
		tvLoginFb = (TextView) findViewById(R.id.tv_login_fb);
		tvSignUp = (TextView) findViewById(R.id.tv_sign_up);
		tvLogin = (TextView) findViewById(R.id.tv_login);
		fbLoginRl.setOnClickListener(this);
		signInRl = (RelativeLayout) findViewById(R.id.rl_login);
		signInRl.setOnClickListener(this);
		signUpRl = (RelativeLayout) findViewById(R.id.rl_sign_up);
		signUpRl.setOnClickListener(this);
		backBttn = (Button) findViewById(R.id.bt_back);
		backBttn.setOnClickListener(this);
		backIv = (ImageView) findViewById(R.id.iv_back);
		backIv.setOnClickListener(this);
		skipIv = (ImageView) findViewById(R.id.iv_skip);
		skipIv.setOnClickListener(this);
		skipTv = (TextView) findViewById(R.id.tv_skip);
		skipTv.setOnClickListener(this);
		
		// registration with bosch
		try {
			MySpinServerSDK.sharedInstance().registerApplication(getApplication());
		} catch (MySpinException e) {
			Log.i(TAG, RConstants.REGISTRATION_BOSCH_FAILED);
		}

		setAllTypeface();
		
		

	}

	private void setAllTypeface() {
		tvStayAhead.setTypeface(metaProTypeFace);
		tvLoginReason.setTypeface(metaProTypeFace);
		tvLoginFb.setTypeface(metaProTypeFace);
		tvLogin.setTypeface(metaProTypeFace);
		tvSignUp.setTypeface(metaProTypeFace);
		skipTv.setTypeface(metaProTypeFace);
		headerTitleTV.setTypeface(metaProTypeFace);
		backBttn.setTypeface(metaProTypeFace);
	}

	@Override
	protected void onResume() {
		super.onResume();

		backBttn.setVisibility(View.VISIBLE);
		backIv.setVisibility(View.VISIBLE);

		// show items while first time
		if (prefStore.getBooleanData(RConstants.RUNNING_FIRST_TIME, true)) {

			skipTv.setVisibility(View.VISIBLE);
			skipIv.setVisibility(View.VISIBLE);
		}

		if (RConstants.LOGIN_DONE_FINISH_OPTION_ACTIVITY) {
			RConstants.LOGIN_DONE_FINISH_OPTION_ACTIVITY = false;
			this.finish();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_skip:
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
				
					FlurryAgent.logEvent(RConstants.FLURRY_ACTION_SKIP_REGISTRATION);
					 
					String userActivity = ActivitiesUserApp.getAppropriateAction(RConstants.ActivitySkipRegistration);

					String actionLogsToPostServer = "[{\n" + '"' + RConstants.ACTION_TYPE
							+ '"' + " : " + '"' + userActivity + '"' + ","
							+ "\n" + '"' + RConstants.ACTIVITY_TIMESTAMP + '"' + " : "
							+ '"' + (System.currentTimeMillis() / 1000) + '"'
							+ "," + "\n" + '"' + RConstants.USER_ID + '"' + " : " + '"'
							+ uid + '"' + "," + "\n" + '"' + RConstants.DATA_LOGS + '"'
							+ " : " + '"' + "{" + "\\n    " + "\\" + '"'
							+ RConstants.APP_VERSION + "\\" + '"' + " : " + "\\" + '"'
							+ versionName + "\\" + '"' + "," + "\\n    " + "\\"
							+ '"' + RConstants.PLATFORM + "\\" + '"' + " : " + "\\"
							+ '"' + RConstants.OS_NAME + "\\" + '"' + "," + "\\n    "
							+ "\\" + '"' + RConstants.GIGYA_UID + "\\" + '"' + " : "
							+ "\\" + '"' + uid + "\\" + '"' + "," + "\\n    "
							+ "\\" + "\\n" + "}" + '"' + "}]";
					  
					Log.i("actionLogsToPostServer", actionLogsToPostServer);
					PostUserAction postUserAction = new PostUserAction(actionLogsToPostServer);
					postUserAction.startLogging();
						
				}
			}).start();

			if (isFirstTime) {
				Intent customizeIntent = new Intent(LoginOptionsActivity.this,OneTimeSelectCategoryActivity.class);
				startActivity(customizeIntent);

			} else {
				finish();
			}

			break;
		case R.id.bt_back:
			finish();
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.rl_fb_login:
			
			if(new ConnectionDetector(LoginOptionsActivity.this).isConnectingToInternet()){
				FbLoginTask fbLoginTask = new FbLoginTask();
				fbLoginTask.execute();
			}else{
				showToast("No Internet Connection");
			}
		

			break;
		case R.id.rl_login:
			Intent signInScreen = new Intent(LoginOptionsActivity.this,SignInActivity.class);
			startActivity(signInScreen);

			break;
		case R.id.rl_sign_up:
			Intent signUpIntent = new Intent(LoginOptionsActivity.this,
					SignUpActivity.class);
			startActivity(signUpIntent);

			break;
		}
	}

	public void initGigya() {
		GSAPI.getInstance().initialize(this, RConstants.GIGYA_API_KEY);
	}

	public class FbLoginTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			 progressDialog = ProgressDialog.show(context,
	                 context.getString(R.string.app_name), RConstants.LOGGING_IN_WAIT, true, true);
	         progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			GSObject gsParams = new GSObject();
			gsParams.put(RConstants.PROVIDER, "facebook");
			gsParams.put(RConstants.FB_LOGIN_BEHAVIOR , "SSO_WITH_FALLBACK");
			fbResponseListener = new FbResponseListener();
			try {
				GSAPI.getInstance().login(gsParams, fbResponseListener, context);
			} catch (Exception e) {
				e.printStackTrace();
				destroyProgressDialog();
				showToast("Error in facebook login");
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}

	public class FbResponseListener implements GSResponseListener {

		@Override
		public void onGSResponse(String s, GSResponse gsResponse, Object o) {
			if (gsResponse.getErrorCode() == 0) {
				
				FlurryAgent.logEvent(RConstants.FLURRY_ACTION_LOGIN_THROUGH_FB);
				
				String responseText = gsResponse.getResponseText();
				if (RConstants.BUILD_DEBUBG) {
					Log.i(TAG, responseText);
				}
				try {
					JSONObject fbJsonObj = new JSONObject(responseText);

					FacebookUser fbUser = new FacebookUser();
					fbUser.setFirstName(fbJsonObj.getString("firstName"));
					fbUser.setGender(fbJsonObj.getString("gender"));
					fbUser.setGender(fbJsonObj.getString("lastName"));
					fbUser.setLoggedIn(fbJsonObj.getBoolean("isLoggedIn"));
					fbUser.setLoginProviderUID(fbJsonObj.getLong("loginProviderUID"));
					try {
						
						fbUser.setEmail(fbJsonObj.getString("email"));
						((BaseActivity)context).prefStore.setStringData(RConstants.FB_EMIAL_ID, fbUser.getEmail());
						
					} catch (Exception exception) {
						exception.printStackTrace();
						Log.i("exception", exception.getMessage());
						
						// to handle the exception is no such key coming like "email"
						((BaseActivity)context).prefStore.setStringData(RConstants.FB_EMIAL_ID, fbUser.getFirstName());
					}
					String UID = fbJsonObj.getString("UID") ;
					fbUser.setUID(UID);

					((BaseActivity)context).prefStore.setStringData(RConstants.FB_LOGIN_PROVIDER_UID, String.valueOf(fbUser.getLoginProviderUID()));
					((BaseActivity)context).prefStore.setBooleanData(RConstants.FB_USER_IS_LOGIN, fbUser.isLoggedIn());
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_UID, UID);
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_PHOTO_URL, fbUser.getPhotoURL());
					
					destroyProgressDialog();

					if (isFirstTime) {
						Intent customizeIntent = new Intent(LoginOptionsActivity.this,OneTimeSelectCategoryActivity.class);
						startActivity(customizeIntent);

					} else {
						finish();
						
					}

				} catch (JSONException e) {
					e.printStackTrace();
					if (RConstants.BUILD_DEBUBG) {
						Log.e(TAG, e.toString());
						destroyProgressDialog();
						showToast("Error in Log in");
						
					}
				}
				
			}else if(gsResponse.getErrorCode() == RConstants.ACCOUNT_EXISTS){
				
				GSObject responseText = gsResponse.getData();
				try {
				
					String regToken = responseText.getString("regToken");
					String existingLoginID = responseText.getString("existingLoginID");
					
					// merge existing account with facebook account for gigya
					PasswordDialogToMergeAccount passwordDialogToMergeAccount = new PasswordDialogToMergeAccount(context, regToken, existingLoginID);
					passwordDialogToMergeAccount.showDialogBox();
					
				
				} catch (GSKeyNotFoundException e) {
					e.printStackTrace();
				}
				
			} else {
				destroyProgressDialog();
				showToast("Error :" + gsResponse.getErrorMessage());
				Log.i("Error :", gsResponse.getErrorMessage());
				
				// post logs on flurry and server while error on gigiya 
				postRareLogsToServerAndFlurry(RConstants.ActivityErrorLoginGigiya);
				if (isFirstTime) {
					Intent customizeIntent = new Intent(LoginOptionsActivity.this,OneTimeSelectCategoryActivity.class);
					startActivity(customizeIntent);

				} else {
					finish();
					
				}
				
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		new LoginModel(LoginOptionsActivity.this).destroyProgressDialog();
		super.onDestroy();
	}
	
	
	  public void destroyProgressDialog(){
	    	
	    	if(progressDialog != null && progressDialog.isShowing()){
				progressDialog.dismiss();
			}
	    	
	    }

	@Override
	public void onConnectionStateChanged(boolean arg0) {
		// TODO Auto-generated method stub
		RConstants.BOSCH_CONNECTED_IN_LOGIN_ACTIVITIES = true ;
		this.finish();
	}
	


}