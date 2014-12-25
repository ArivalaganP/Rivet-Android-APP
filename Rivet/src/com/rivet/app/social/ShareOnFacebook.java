package com.rivet.app.social;

import java.util.ArrayList;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.gigya.json.JSONException;
import com.gigya.json.JSONObject;
import com.gigya.socialize.GSArray;
import com.gigya.socialize.GSObject;
import com.gigya.socialize.GSResponse;
import com.gigya.socialize.GSResponseListener;
import com.gigya.socialize.android.GSAPI;
import com.rivet.app.BaseActivity;
import com.rivet.app.HomeActivity;
import com.rivet.app.R;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.FacebookUser;

public class ShareOnFacebook {
	
	private Context context;
	private String userUID;
	private static String status;
	private ProgressDialog progressDialog;
	private Dialog dialog;
	private Button btnUpdateStatus;
	private TextView lblUpdate;
	private Button cancelBT;
	private String TAG = "ShareOnFacebook" ;
	private String title;
	private String href;
	private EditText userMessageET;
	private String postCategory = "public" ;
	private ImageView iconStory;
	private Spinner publicSP;
	private ArrayList<String> privacyList = new ArrayList<String>();
	private FacebookLoginTask fbLoginTask;
	
	
	
	@SuppressWarnings("static-access")
	public ShareOnFacebook(Context context , String title , String href , String status , String userUID) {
	this.context = context ;
	this.userUID = userUID ;
	this.status = status ;
	this.title = title ;
	this.href = href ;
	}
	
	private void postUserStatus(String userUID){
		
					this.userUID = userUID ;
					PostUserTask postUserTask=new PostUserTask();
					postUserTask.execute(userUID, status);
		
	}
	
	 public FacebookLoginTask getFbLoginTask(){
	 if(fbLoginTask == null)
	 fbLoginTask = new FacebookLoginTask();
	 return fbLoginTask;
	 }
	

     private class PostUserTask extends AsyncTask<String, Void, Void>{
    	 
	private PostStatusResponseHandler postStatusResponseHandler;

	@Override
     protected void onPreExecute() {
         super.onPreExecute();
         progressDialog = ProgressDialog.show(context,
                 context.getString(R.string.app_name), RConstants.POSTING_STATUS, true, true);
         progressDialog.show();
     }

     @Override
     protected Void doInBackground(String... params) {
         postStatusResponseHandler=new PostStatusResponseHandler();
       
         
     	GSObject userAction = new GSObject();
     	userAction.put(RConstants.UID, params[0]);
     	userAction.put(RConstants.TITLE, title);
     
     	userAction.put(RConstants.LINE_BACK, href);
     	userAction.put(RConstants.USER_MSG, userMessageET.getText().toString());
     	
     	// next line code show post public or private 
     	userAction.put(RConstants.PRIVACY, postCategory);

     	GSObject image = new GSObject();
     	image.put(RConstants.SRC, RConstants.FB_SHARE_IMAGE_URL);

     	image.put(RConstants.TYPE, "image");

     	GSArray mediaItems = new GSArray();
     	mediaItems.add(image);

     	userAction.put(RConstants.MEDIA_ITEMS ,mediaItems);
     	GSObject shareParams = new GSObject();
     	shareParams.put(RConstants.USER_ACTION, userAction);
     	
     	// flurry post logs
     	
     	((BaseActivity)context).postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityShareOnFB);
         
         GSAPI.getInstance().sendRequest(RConstants.PUBLISH_STATUS, shareParams, true,
        		 postStatusResponseHandler, null);
         return null;
     }

     }
     
     
     public void createFacebookDialog(Bitmap fbShareBitmap){
    	 
    	 privacyList.add(RConstants.PUBLIC);
    	 privacyList.add(RConstants.FRIENDS);
    	 privacyList.add(RConstants.PRIVATE);
    	 
		 dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_facebook);
		dialog.setTitle("Facebook");
		iconStory = (ImageView) dialog.findViewById(R.id.iconStory);
		btnUpdateStatus = (Button) dialog.findViewById(R.id.btnUpdateFacebookStatus);
		publicSP = (Spinner) dialog.findViewById(R.id.publicSP); 
		lblUpdate = (TextView) dialog.findViewById(R.id.lblFacebookUpdate);
		cancelBT = (Button) dialog.findViewById(R.id.facebookCancelBT);
		userMessageET = (EditText) dialog.findViewById(R.id.userMessageET);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, privacyList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		publicSP.setAdapter(dataAdapter);
		
		publicSP.setOnItemSelectedListener(new OnItemSelectedListener() {

		

		

			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int postion, long arg3) {
			postCategory =	(String) adapterView.getItemAtPosition(postion);	
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
		});
		
		if( fbShareBitmap!= null){
		setFbBitmapInImageView(fbShareBitmap);
		}
		String trimmedTitle ;
		if (title.length() > 30){
			trimmedTitle  = title.substring(0, 27) + "..." ;
		}else {
			trimmedTitle = title ;
		}
		lblUpdate.setText(trimmedTitle);
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
				
				
				postUserStatus(userUID);
				
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
     
     public void setFbBitmapInImageView(Bitmap bitmap){
    	 iconStory.setImageBitmap(bitmap);
     }
     
     private class PostStatusResponseHandler implements GSResponseListener{

        

		@Override
         public void onGSResponse(String s, GSResponse gsResponse, Object o) {
         	 if(RConstants.BUILD_DEBUBG){
        		 Log.i(TAG, gsResponse.getResponseText());
        	 }
        	 if(gsResponse.getErrorCode() == 0){
     			((BaseActivity)context).runOnUiThread(new Runnable() {
     				@Override
     				public void run() {
     					
     					Toast.makeText(context, RConstants.STATUS_POSTED, Toast.LENGTH_LONG).show();
     					
     				}
     			});
     			
     			dismissFacebookDialog();
				if(progressDialog != null)
				progressDialog.dismiss();
     		}
           
         }

		protected void dismissFacebookDialog() {
		if(dialog != null){
			dialog.dismiss();
		}
			
		}
     }
     
	public class FacebookLoginTask extends AsyncTask<Void, Void, Void> {

		private FacebookResponseListener fbResponseListener;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = ProgressDialog.show(context,
					context.getString(R.string.app_name),
					"Preparing to Post , please wait...", true, true);
			progressDialog.show();
			
		}

		@Override
		protected Void doInBackground(Void... params) {

			// do facebook login
			GSObject gsParams = new GSObject();
			gsParams.put("provider", "facebook");
			gsParams.put("facebookLoginBehavior", "SSO_WITH_FALLBACK");
			fbResponseListener = new FacebookResponseListener();
			try {
				GSAPI.getInstance()
						.login(gsParams, fbResponseListener, context);
			} catch (Exception e) {
				e.printStackTrace();

				e.printStackTrace();
				if(progressDialog != null){
					progressDialog.dismiss();
				}
				
				((BaseActivity)context).showToast("Error in facebook login");
			
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
		}
	}
     
 	
 	
 	public class FacebookResponseListener implements GSResponseListener {
 		

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
					fbUser.setLoginProviderUID(fbJsonObj
							.getLong("loginProviderUID"));
					try {
						
						fbUser.setEmail(fbJsonObj.getString("email"));
						((BaseActivity)context).prefStore.setStringData(RConstants.FB_EMIAL_ID, fbUser.getEmail());
						
					} catch (Exception exception) {
						exception.printStackTrace();
						// to handle the exception is no such key coming like "email"
						((BaseActivity)context).prefStore.setStringData(RConstants.FB_EMIAL_ID, fbUser.getFirstName());
					}
					String UID = fbJsonObj.getString("UID") ;
					fbUser.setUID(UID);

					
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_LOGIN_PROVIDER_UID, String.valueOf(fbUser.getLoginProviderUID()));
					((BaseActivity)context).prefStore.setBooleanData(RConstants.FB_USER_IS_LOGIN, fbUser.isLoggedIn());
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_UID, UID);
					((BaseActivity)context).prefStore.setStringData(RConstants.FB_PHOTO_URL, fbUser.getPhotoURL());
					
					
					((BaseActivity)context).prefStore.setBooleanData(RConstants.IS_LOGIN_WITH_GMAIL, false);	
					((HomeActivity)context).showLoginOrLogout();
					
					if(progressDialog!=null){
						progressDialog.dismiss();
					}
					
					createFacebookDialog(RConstants.sharingFBBitmap);
					// TODO : work here for post status

				} catch (JSONException e) {
					e.printStackTrace();
					if (RConstants.BUILD_DEBUBG) {
						Log.e(TAG, e.toString());
					}
					
					if(progressDialog != null){
						progressDialog.dismiss();
					}
					
					((BaseActivity)context).showToast("Error in Log in");
					
				}
			} else {
				Toast.makeText(context, "Error : " +
						 gsResponse.getErrorMessage() + " " ,
						Toast.LENGTH_LONG).show();
				
				if(progressDialog != null){
					progressDialog.dismiss();
				}
				
			}
		}
	}
 	
	
 	
}
