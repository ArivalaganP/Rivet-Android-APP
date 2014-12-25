


package com.rivet.app.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import com.rivet.app.BaseActivity;
import com.rivet.app.BaseActivity.LocationPoints;
import com.rivet.app.common.RConstants;
import com.rivet.app.observer.AdsBuilderObserver;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

public class AdsBuilder  {
	
	private Context context;
	private String TAG = "AdsBuilder" ;
	ArrayList<PropertyChangeListener> arrProperyListnrsList = new ArrayList<PropertyChangeListener>();
	private String AdsBuildComplete = "AdsBuildComplete" ;

	public AdsBuilder(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context ;
	}
	
	public void start(){
		
		 LocationPoints location	= ((BaseActivity)context).getLocationPoints();
		 
		 if(location != null){
		
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID); 
		
		String mainUrl = "http://cmod210.live.streamtheworld.com/ondemand/ars?type=midroll&fmt=vast&stid=76973&lsid=" + 
				android_id + "&at=audio&cntnr=MP3&acodec=MP3&lat=" + location.getLatts() + "&long=" +location.getLongs() ;

		
		AdsResponseListener adsResponseListener = new AdsResponseListener();
		Thread getAdsThread = new Thread(new HttpRequest(mainUrl,
				HttpMethodType.GET, adsResponseListener,
				adsResponseListener, null, null , false , null));
		getAdsThread.start();
		
		}else{
			((BaseActivity)context).failedToLoadAds();
			
			if(RConstants.BUILD_DEBUBG){
				Log.e(TAG, "failed to Load ads");
				}
			
		}
		
		
		
	}
	
	public class AdsResponseListener implements HttpResponseListener,
			HttpExceptionListener {

		@Override
		public void handleResponse(String response) {
			if (response != null) {
				
			String adsHtmlFile =	decodeAds(response);
				// now its time to parse vast ads xml file
				VASTAdsXmlParser vastAdsXmlParser =  new VASTAdsXmlParser(context , adsHtmlFile);
				vastAdsXmlParser.start();
				notifyListeners(AdsBuildComplete);
			}
		}

		@Override
		public void handleException(String exception) {
			// TODO Auto-generated method stub
			if (RConstants.BUILD_DEBUBG) {
				((BaseActivity) context).logI(TAG, exception);
			}
		}

	}

	public void addChangeListener(AdsBuilderObserver adsBuilderObserver) {
		// TODO Auto-generated method stub
		arrProperyListnrsList.add(adsBuilderObserver);
	}
	
	public void notifyListeners(String eventName){
		
	for(PropertyChangeListener listner : arrProperyListnrsList){
		listner.propertyChange(new PropertyChangeEvent(this, eventName , null, null));
		}
	
	}
	
	private String decodeAds(String url)
	{
	   return url.replace( "&" , "&amp;");
	}
	

}
