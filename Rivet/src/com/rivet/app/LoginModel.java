package com.rivet.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.gigya.json.JSONArray;
import com.gigya.json.JSONException;
import com.gigya.json.JSONObject;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.UserInfo;

/**
 * Created by brian on 17/6/14.
 */
public class LoginModel {

    Context context;
    LoginResponseHandler loginResponseHandler;
    InitRegistrationResponseHandler initRegistrationHandler;
    RegistrationListener registrationListener;
    ProgressDialog progressDialog;
    String TAG = "loginModelTag" ;
    String regToken="";
    String registrationEmail="";
    String registrationPassword="";
    boolean initRegistrationSuccess=false;
    boolean loginSuccess=false;
    boolean signUpSuccess=false;
	private String userName;
	public boolean logoutSuccess = false ;
	public boolean resetPassword;

    /**
     *
     * @param ctx: Activity context, so that the operations remain in activity's context
     *
     * It also calls to initGigya() method, because we need to get instance of GSAPI class for every
     * operation that we have to perform.
     */
    public LoginModel(Context ctx){
        this.context=ctx;
        initGigya();
    }

    /**
     * method for initialising GSAPI class
     */
    public void initGigya(){
        GSAPI.getInstance().initialize(context, RConstants.GIGYA_API_KEY);
    }

    /**
     *
     * @param loginId: take login Id from Sign In activity
     * @param password: take password for above login id from activity
     *
     * this method starts the login task, which logs a user in
     */
    public void login(String loginId, String password){
    	userName = loginId ;
    	
        LoginTask loginTask=new LoginTask();
        loginTask.execute(loginId, password);
    }
    
    /**
    *
    * @param loginId: take login Id from Forgot password activity
    * this method resets the password for an emailId 
    */
    public void resetPassword(String mailId) {
	ResetpasswordTask resetTask = new ResetpasswordTask();
	resetTask.execute(mailId);
	}
    
    
    /**
    *
    * @param loginId: take login Id from Home activity
    *
    * this method starts the log out task, which logs a user out
    */
    public void logout(){
    	LogoutTask logoutTask = new LogoutTask();    	
    	((BaseActivity)context).prefStore.getStringData(RConstants.GMAIL_USER_NAME, null);
    	logoutTask.execute(userName);
    }

    /**
     *
     * @param email: takes email of user from activity with which user is trying to register
     * @param password: takes password from activity
     *
     * this method starts task for registering a new user
     */
    public void register(String email, String password){
        this.registrationEmail=email;
        this.registrationPassword=password;
        InitRegistrationTask registrationTask=new InitRegistrationTask();
        registrationTask.execute();
    }
    
    public class LogoutTask extends AsyncTask<String, Void, Void>{
    	
    	private LogoutResponseHandler logoutResponseHandler;

		protected void  onPreExecute() {
			super.onPreExecute(); progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), RConstants.LOGGING_OUT_WAIT, true, true);
            progressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {	 
			logoutResponseHandler=new LogoutResponseHandler();
            GSObject logoutParams=new GSObject();
            logoutParams.put("loginID", params[0]);

            GSAPI.getInstance().sendRequest(RConstants.ACCOUNTS_LOGOUT, logoutParams, true,
            		logoutResponseHandler, null);
       
			return null;
		}
    	
    }
    
    private class ResetpasswordTask extends AsyncTask<String, Void, Void>{

    	private ResetResponseHandler resetResponseHandler;

		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), RConstants.RESETING_PASSWORD_WAIT, true, true);
            progressDialog.show();
        }
    	
		@Override
		protected Void doInBackground(String... params) {
			  resetResponseHandler=new ResetResponseHandler();
	            GSObject resetParams =new GSObject();
	            resetParams.put("loginID", params[0]);
	            GSAPI.getInstance().sendRequest(RConstants.ACCOUNTS_RESET_PASSWORD, resetParams , true,
	                    resetResponseHandler, null);
	            return null;
		}
    	
    }

    public class LoginTask extends AsyncTask<String, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
  
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), RConstants.LOGGING_IN_WAIT, true, true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            loginResponseHandler=new LoginResponseHandler();
            GSObject loginParams=new GSObject();
            loginParams.put(RConstants.LOGIN_ID, params[0]);
            loginParams.put(RConstants.PASSWORD, params[1]);
            GSAPI.getInstance().sendRequest(RConstants.ACCOUNTS_LOGIN, loginParams, true,
                    loginResponseHandler, null);
            return null;
        }

   
    }
    
    public class LogoutResponseHandler implements GSResponseListener{

	

		@Override
		public void onGSResponse(String string, final GSResponse gsResponse, Object object) {
			Log.i(TAG, gsResponse.toString());
		if(gsResponse.getErrorCode() == 0){

						
						((BaseActivity)context).prefStore.setStringData(RConstants.GMAIL_USER_NAME, null);
						((BaseActivity)context).prefStore.setBooleanData(RConstants.IS_LOGIN_WITH_GMAIL, false);
						
						Toast.makeText(context,
								RConstants.LOGOUT_SUCCESSFULLY,
								Toast.LENGTH_LONG).show();
						((HomeActivity) context).runOnUiThread(new Runnable() {

							@Override
							public void run() {

								((HomeActivity) context).LogoutButtonGone();

							}
						});
				
		
			
						destroyProgressDialog();
	  
	    	
		}else{
			((HomeActivity)context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					destroyProgressDialog();
					Toast.makeText(context, gsResponse.getErrorMessage() , Toast.LENGTH_LONG).show();
					
				}
			});
		}
			
		}
    	
    }
    
    private class ResetResponseHandler implements GSResponseListener{

		

		@Override
		public void onGSResponse(String string, final GSResponse gsResponse, Object object) {
		if(gsResponse.getErrorCode() == 0){
			resetPassword = true ;
			onPostOfReset();
		}else {
			((BaseActivity)context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {

					destroyProgressDialog();
					Toast.makeText(context, gsResponse.getErrorMessage() , Toast.LENGTH_LONG).show();
					
				}
			});
		}
			
		}

	
    	
    }

    public class LoginResponseHandler implements GSResponseListener{

        @Override
        public void onGSResponse(String s, final GSResponse gsResponse, Object o) {
        	if(RConstants.BUILD_DEBUBG){
        		Log.i(TAG, gsResponse.getResponseText());
        	}
            if(gsResponse.getErrorCode()==0){
                loginSuccess=true;
                onPostOfLogin();
            }else{
            	((BaseActivity)context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {

						destroyProgressDialog();
						
					Toast.makeText(context, gsResponse.getErrorMessage() , Toast.LENGTH_LONG).show();
						
					}
				});
            }
        }
    }
    
    public void destroyProgressDialog(){
    	
    	if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
    	
    }
    
	private void onPostOfReset() {
		destroyProgressDialog();
		if(resetPassword){
		
			((ForgotPasswordActivity) context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					((ForgotPasswordActivity) context).makeTextViewsVisiable();
				}
			});
		}else{
			Toast.makeText(context, RConstants.ERROR_RESETING_PASSWORD, Toast.LENGTH_LONG).show();
		}
		
	}

    public void onPostOfLogin(){
       
		// post log to flurry
		FlurryAgent.logEvent(RConstants.FLURRY_LOGIN_ACTION_EMAIL);				
				((BaseActivity)context).prefStore.setBooleanData(RConstants.FB_USER_IS_LOGIN, false);
				((BaseActivity)context).prefStore.setStringData(RConstants.GMAIL_USER_NAME, userName);
				((BaseActivity)context).prefStore.setBooleanData(RConstants.IS_LOGIN_WITH_GMAIL, loginSuccess);
				
				UserInfo infoUser = new UserInfo();
				infoUser.setIsLogin(loginSuccess);
				infoUser.setUsername(userName);				
				((BaseActivity)context).prefStore.setStringData(RConstants.GMAIL_USER_NAME, userName);
				((BaseActivity)context).prefStore.setBooleanData(RConstants.IS_LOGIN_WITH_GMAIL, loginSuccess);
		
				// enable back button when login done after registration
				if(context instanceof SignUpActivity){
					((SignUpActivity)context).enableBackButtonToPress();
				}
				 
		 // checking weather app running first time and login sucessful do this else go to Home screen
        boolean isFirstTime =  ((BaseActivity)context).prefStore.getBooleanData(RConstants.RUNNING_FIRST_TIME, true);
        if(loginSuccess && isFirstTime) {
            //Toast.makeText(context, "Login was successful!", Toast.LENGTH_SHORT).show();
            Intent catIntent = new Intent(context, OneTimeSelectCategoryActivity.class);
            ((Activity) context).startActivity(catIntent);
        }else if(loginSuccess){

        	RConstants.LOGIN_DONE_FINISH_OPTION_ACTIVITY = true ;
        	if(context instanceof SignInActivity){
        	((SignInActivity)context).finish();
        	}else {
        	((SignUpActivity)context).finish();
        	}
        	
        }
        
        destroyProgressDialog();
        
    }


    public class InitRegistrationTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), RConstants.REGISTERING_WAIT, true, true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            initRegistrationHandler=new InitRegistrationResponseHandler();
            GSAPI.getInstance().sendRequest(RConstants.ACCOUNTS_INIT_REGISTRATION,
                    null, initRegistrationHandler, null);
            return null;
        }
    }

    public class InitRegistrationResponseHandler implements GSResponseListener{

        @Override
        public void onGSResponse(String s, GSResponse gsResponse, Object o) {
        	if(RConstants.BUILD_DEBUBG){
        		Log.i(TAG, gsResponse.getResponseText());
        	}
            try {
            	if(gsResponse.getErrorCode() == 0){
                regToken=gsResponse.getData().getString(RConstants.REG_TOKEN);
                initRegistrationSuccess=true;
                Log.d(RConstants.REG_TOKEN, regToken);
            	}
            } catch (GSKeyNotFoundException e) {
                e.printStackTrace();
            }
            onPostOfInitRegistration();
        }
    }

    /*
    This onPost is written outside Async class, because somehow, thread is not joining,
    and if we keep the onPost in Async class, it just directly calls onPost, before
    waiting for response.
     */
    public void onPostOfInitRegistration(){
    	destroyProgressDialog();
        if(initRegistrationSuccess){
            RegistrationTask registrationTask=new RegistrationTask();
            registrationTask.execute(regToken);
        }
    }

    public class RegistrationTask extends AsyncTask<String, Void, Void>{
    	
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), RConstants.REGISTERING_WAIT, true, true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            registrationListener=new RegistrationListener();
            GSObject gsObject=new GSObject();
            gsObject.put(RConstants.EMAIL, registrationEmail);
            gsObject.put(RConstants.FINALIZE_REGISTRATION, "true");
            gsObject.put(RConstants.TARGET_ENV, "mobile");
            gsObject.put(RConstants.PASSWORD, registrationPassword);
            gsObject.put(RConstants.REG_TOKEN, regToken);
            GSAPI.getInstance().sendRequest(RConstants.ACCOUNTS_REGISTRATION,
                    gsObject, registrationListener, null);
            return null;
        }
    }

    public class RegistrationListener implements GSResponseListener{

        @Override
        public void onGSResponse(String s, final GSResponse gsResponse, Object o) {
        	
            if(gsResponse.getErrorCode()==0){
                signUpSuccess=true;
            onPostOfRegistration();
            
            }else{
            	((SignUpActivity)context).runOnUiThread(new Runnable() {
					String errMsg = "" ;
					@Override
					public void run() {
						try {
							JSONObject errorJson = new JSONObject(gsResponse.getResponseText());
							JSONArray errorArray =	errorJson.getJSONArray("validationErrors");
							for(int errIndx = 0 ; errIndx < errorArray.length() ; errIndx++){
								JSONObject errMsgJson = (JSONObject)errorArray.get(errIndx);
								errMsg = errMsgJson.getString("message");
							}
						} catch (JSONException e) {
							
							e.printStackTrace();
						}
						
						destroyProgressDialog();
						
						String errorString = gsResponse.getErrorMessage() ;
						if(!errMsg.isEmpty()){
							errorString =errorString.concat("("+errMsg+")");
						}
						
						Toast.makeText(context, errorString+"" , Toast.LENGTH_LONG).show();
						
					}
				});
            }
        }
    }

    /*
    This onPost is written outside Async class, because somehow, thread is not joining,
    and if we keep the onPost in Async class, it just directly calls onPost, before
    waiting for response.
     */
    public void onPostOfRegistration(){
    	destroyProgressDialog();
        if(signUpSuccess) {
            Toast.makeText(context, RConstants.REGISTERATION_SUCESSFULL, Toast.LENGTH_SHORT).show();
            login(registrationEmail, registrationPassword);
        }
    }

	public void fbLogout(String fbUserUID) {
		LogoutFBTask logoutFBTask = new LogoutFBTask();
		logoutFBTask.execute(fbUserUID);
		
	}
	
	private class LogoutFBTask extends AsyncTask<String , Void, Void>{
		
		private LogoutFbResponseHandler logoutFBResponseHandler;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			 progressDialog = ProgressDialog.show(context,
	                    context.getString(R.string.app_name), RConstants.LOGGING_OUT_WAIT, true, true);
	            progressDialog.show();
	            
		}

		@Override
		protected Void doInBackground(String... params) {
			
			  logoutFBResponseHandler =new LogoutFbResponseHandler();
	            GSObject logoutparams=new GSObject();
	            logoutparams.put("UID", params[0]);
	            
	            GSAPI.getInstance().sendRequest(RConstants.ACCOUNTS_FB_LOGOUT , logoutparams, true,
	            		logoutFBResponseHandler, null);
	            
				return null;
			
		}
		
	}
	
	private class LogoutFbResponseHandler implements GSResponseListener{

		@Override
		public void onGSResponse(String arg0,final GSResponse gsresponse, Object arg2) {
		
			
			if(gsresponse.getErrorCode() == 0){
				
					for (;;) {

						try {
							
							((BaseActivity)context).prefStore.setBooleanData(RConstants.FB_USER_IS_LOGIN, false);
							
							
							Toast.makeText(context, "You have successfully logged out",
									Toast.LENGTH_LONG).show();
							((HomeActivity) context).runOnUiThread(new Runnable() {

								@Override
								public void run() {
									((HomeActivity) context).LogoutButtonGone();
								}
							});
						} catch (Exception e) {
							
							e.printStackTrace();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
								
								e1.printStackTrace();
							}
						}

						break;
					}
				
				
					destroyProgressDialog();
				
			}else{
				
				destroyProgressDialog();
				
				((BaseActivity)context).runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						
						Toast.makeText(context, gsresponse.getErrorMessage() , Toast.LENGTH_LONG).show();
					}
				});
				
			
			}
			
		}
		
	}
	
}
