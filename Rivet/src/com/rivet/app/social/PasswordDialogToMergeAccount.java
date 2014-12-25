package com.rivet.app.social;

import java.io.InvalidClassException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.gigya.socialize.GSKeyNotFoundException;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.rivet.app.BaseActivity;
import com.rivet.app.LoginOptionsActivity;
import com.rivet.app.OneTimeSelectCategoryActivity;
import com.rivet.app.R;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.FacebookUser;

public class PasswordDialogToMergeAccount {
	
	private Context context;
	private Dialog dialog;
	private String regToken;
	private String existingID;
	PostStatusResponseHandler postStatusResponseHandler = null ;
	private String TAG= "LoginOptionsActivity";

	public PasswordDialogToMergeAccount(Context context , String regToken , String existingID){
		
		
		this.context = context ;
		this.regToken = regToken ;
		this.existingID = existingID ;
		
	}
	
 public void showDialogBox(){
	 
	 dialog = new Dialog(context);
	 dialog.setTitle("Rivet");
	 dialog.setContentView(R.layout.merge_gigya_account_password);
	 Button doneButton = (Button) dialog.findViewById(R.id.done_btn);
	 Button cancelButton = (Button) dialog.findViewById(R.id.cancel_btn);
	 
	 cancelButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			((LoginOptionsActivity)context).destroyProgressDialog();
			dialog.cancel();
		}
	});
	 
	 final EditText existingPasswordET = (EditText) dialog.findViewById(R.id.existing_password_et);
	 
	 doneButton.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String existingPassword = existingPasswordET.getText().toString();
			
			if(!existingPassword.isEmpty()){
				
				postStatusResponseHandler = new PostStatusResponseHandler();
				GSObject shareParams = new GSObject(); 
				shareParams.put("regToken", regToken);
				shareParams.put("loginID", existingID);
				shareParams.put("password", existingPassword);
				
				GSAPI.getInstance().sendRequest(RConstants.MERGE_ACCOUNT, shareParams, true,
		        		 postStatusResponseHandler, null);
				dialog.cancel();
			}else{
				((BaseActivity)context).showToast("Please Enter Password");
			}
		}
	});
	 
		dialog.show();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((LoginOptionsActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		int actualHeight = (height * 60) / 100 ;
		Window window = dialog.getWindow();
		window.setLayout((width - 30), actualHeight);
		
	}
 
	private class PostStatusResponseHandler implements GSResponseListener{

	

		@Override
		public void onGSResponse(String arg0, GSResponse gsResponse, Object arg2) {
			// TODO Auto-generated method stub
			if(RConstants.BUILD_DEBUBG){
				Log.v(TAG, gsResponse.getResponseText()+"");
			}

			if(gsResponse.getErrorCode() == 0){
			
			FlurryAgent.logEvent(RConstants.FLURRY_ACTION_LOGIN_THROUGH_FB);
			
			GSObject responseGSObject = gsResponse.getData();
			
			try {

				FacebookUser fbUser = new FacebookUser();
				fbUser.setFirstName(responseGSObject.getString("firstName"));
				fbUser.setGender(responseGSObject.getString("gender"));
				fbUser.setGender(responseGSObject.getString("lastName"));
				fbUser.setLoggedIn(responseGSObject.getBool("isLoggedIn"));
				fbUser.setLoginProviderUID(responseGSObject.getLong("loginProviderUID"));
				
				
				try {
					
					fbUser.setEmail(responseGSObject.getString("email"));
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_EMIAL_ID, fbUser.getEmail());
					
				} catch (Exception exception) {
					exception.printStackTrace();
					// to handle the exception is no such key coming like "email"
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_EMIAL_ID, fbUser.getFirstName());
				}
				String UID = responseGSObject.getString("UID") ;
				fbUser.setUID(UID);

				((BaseActivity)context).prefStore.setStringData(RConstants.FB_LOGIN_PROVIDER_UID, String.valueOf(fbUser.getLoginProviderUID()));
				((BaseActivity)context).prefStore.setBooleanData(RConstants.FB_USER_IS_LOGIN, fbUser.isLoggedIn());
				((BaseActivity)context).prefStore.setStringData(RConstants.FB_UID, UID);
				((BaseActivity)context).prefStore.setStringData(RConstants.FB_PHOTO_URL, fbUser.getPhotoURL());
				
				if(context instanceof LoginOptionsActivity){
				((LoginOptionsActivity)context).destroyProgressDialog();
				}
				
				
				
			((BaseActivity)context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean isFirstTime = ((BaseActivity) context).prefStore.getBooleanData(
							RConstants.RUNNING_FIRST_TIME, true);
					if (isFirstTime) {
						Intent customizeIntent = new Intent(
								context,
								OneTimeSelectCategoryActivity.class);
						context.startActivity(customizeIntent);

					} else {
						((LoginOptionsActivity)context).finish();
						
					}
				}
			});
				

			} catch (GSKeyNotFoundException | InvalidClassException | NullPointerException e) {
				e.printStackTrace();
				if (RConstants.BUILD_DEBUBG) {
					Log.e(TAG, e.toString());
					
					if(context instanceof LoginOptionsActivity){
					((LoginOptionsActivity)context).destroyProgressDialog();
					}
					
					((BaseActivity)context).showToast("Error in Log in, please try again");
					
				}
			}
		
		}else {
			
			((LoginOptionsActivity)context).destroyProgressDialog();
			
			((BaseActivity)context).showToast(gsResponse.getErrorMessage()+"");
			
		
		}
	}
	}

}
