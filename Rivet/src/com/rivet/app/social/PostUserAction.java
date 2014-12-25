package com.rivet.app.social;
import android.util.Log;

import com.rivet.app.common.RConstants;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

public class PostUserAction {

	
	private String path;
	
	private String TAG = "PostUserAction" ;

	private String activityLogs;


	// send user Action and parameters
	public PostUserAction(String activityLogs) {
		
	this.activityLogs = activityLogs;
	this.path = RConstants.BaseUrl + RConstants.ACTIVITY_LOG;
	}
		
    public void	startLogging(){
    	
    	UserActionResponseListener categoryResponseListener = new UserActionResponseListener();
    	Thread getCategoryThread = new Thread(new HttpRequest(path,HttpMethodType.POST, categoryResponseListener,
    			categoryResponseListener, null, null , true , activityLogs));

		 if(RConstants.BUILD_DEBUBG){
		    	
		    	Log.i(TAG, "RIVET_LOG: "+activityLogs);
		 }
		    
    	getCategoryThread.start();
	}
	

	private class UserActionResponseListener implements HttpResponseListener , HttpExceptionListener{
		@Override
		public void handleResponse(String response) {
			if (response != null) {
				processUserActionData(response);
			}
		}

		private void processUserActionData(String response) {
			if(RConstants.BUILD_DEBUBG){
		        Log.i(TAG, response);
			}
			
		}

		@Override
		public void handleException(String exception) {
			Log.i(TAG, exception.toString());
		}
		
	}

}
