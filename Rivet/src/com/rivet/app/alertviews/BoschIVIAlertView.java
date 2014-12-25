package com.rivet.app.alertviews;


import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.rivet.app.BaseActivity;
import com.rivet.app.R;

public class BoschIVIAlertView {
	
	
	public void showAlertDialog(Context context , int whatToDisplay) {
		
		final Dialog dialog = new Dialog(context);
		if (whatToDisplay == 1) {
		
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_view_bosch_internet_connection);
			Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		
		}else if(whatToDisplay == 2){
			
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_view_bosch_no_stories_play);
			Button dialogButton = (Button) dialog.findViewById(R.id.okNoMoreStoriesBtn);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
		
		}else if(whatToDisplay == 3){
			
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.alert_view_bosch_internet_connection_slow);
			Button dialogButton = (Button) dialog.findViewById(R.id.okNoMoreStoriesBtn);
			// if button is clicked, close the custom dialog
			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			
		}
		
		try {
			MySpinServerSDK.sharedInstance().registerDialog(dialog);
		} catch (MySpinException e) {
			
			e.printStackTrace();
		}
		dialog.show();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((BaseActivity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		int actualHeight = (height * 60) / 100 ;
		Window window = dialog.getWindow();
		window.setLayout((width - 30), actualHeight);

	}

}
