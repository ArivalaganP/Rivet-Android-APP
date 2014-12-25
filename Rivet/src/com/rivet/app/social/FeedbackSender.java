package com.rivet.app.social;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings.Secure;

import com.rivet.app.BaseActivity;
import com.rivet.app.common.RConstants;

public class FeedbackSender {
	
	private Context context;

	public FeedbackSender(Context context) {
		this.context = context ;
	}
	
	public void sendFeedBack(){
		
		((BaseActivity)context).postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityShareOnEmail);
		
		  /*Intent mailIntent = new Intent();
		  mailIntent.setAction(Intent.ACTION_SEND);
		  mailIntent.setType("text/plain");
		 
		  String signature =  getAppAndPhoneInfo();
		  mailIntent.putExtra(android.content.Intent.EXTRA_TEXT, signature);
		  mailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{RConstants.FEEDBACK_RECIVER});
		  ((BaseActivity)context).startActivity(Intent.createChooser(mailIntent, "Send mail..."));*/
		
		String signature =  getAppAndPhoneInfo();
		String URI="mailto:"+RConstants.FEEDBACK_RECIVER+"?subject=" + "&body=" + signature;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse(URI);
		intent.setData(data);
		((BaseActivity)context).startActivity(Intent.createChooser(intent, "Send mail..."));
		  
		
	}
	
	private String getAppAndPhoneInfo(){
		
		String versionName = null;
		try {
			versionName = context.getPackageManager()
				    .getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		
			e.printStackTrace();
		}
		
		String deviceName = android.os.Build.MODEL;

        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
        
        String deviceAndAppDetails ="\n" + "\n" + "\n" + "--Version " + versionName + "\n" + "--" + deviceName + "\n" +  "--" + androidId ;
		
		return deviceAndAppDetails ;
	}

}
