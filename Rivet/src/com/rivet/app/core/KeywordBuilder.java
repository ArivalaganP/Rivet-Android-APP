package com.rivet.app.core;


import android.content.Context;
import android.util.Log;

import com.gigya.json.JSONException;
import com.gigya.json.JSONObject;
import com.rivet.app.BaseActivity;
import com.rivet.app.BaseActivity.LocationPoints;
import com.rivet.app.common.GPS;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.MapPolygon;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

public class KeywordBuilder {
	
	public static final String TAG = "KeywordBuilder";
	Context ctx;
	private MapPolygon mapPloygon;
	String mainURL = null ;
	GPS gps;

	private String trafficFilterKeyword = null ;
	private String localNewsFilterKeyword = null ;
	Thread polygonThread=null;
	PolygonResponseHandler polygonResponseHanlder=null;
	
	public KeywordBuilder(Context ctx){
		this.ctx = ctx;
		
	}

	public String getTrafficFilterKeyword() {
		return trafficFilterKeyword;
	}

	public void setTrafficFilterKeyword(String trafficFilterKeyword) {
		this.trafficFilterKeyword = trafficFilterKeyword;
	}

	public String getLocalNewsFilterKeyword() {
		return localNewsFilterKeyword;
	}

	public void setLocalNewsFilterKeyword(String localNewsFilterKeyword) {
		this.localNewsFilterKeyword = localNewsFilterKeyword;
	}

	public void start(boolean isStartFromReciever) {
		
		mapPloygon = new MapPolygon();
		
		 gps = ((BaseActivity)this.ctx).getGPS();	 
		
		 if(! gps.isProviderDisabled()) {
			 
			 if(isStartFromReciever){
			 gps.startGPS();
			 
			 try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			 
			 }
			 
			  LocationPoints location	= ((BaseActivity)ctx).getLocationPoints();
			  mapPloygon.setLongs(location.getLongs());
			  mapPloygon.setLatts(location.getLatts());
				
			  mainURL = RConstants.BaseUrl + RConstants.MAPPOLYGON + "?latitude="
					+ mapPloygon.getLatts() + "&longitude="
					+ mapPloygon.getLongs();
			 
				polygonResponseHanlder = new PolygonResponseHandler();
				polygonThread =	new Thread(new HttpRequest(mainURL , HttpMethodType.GET,
						polygonResponseHanlder,polygonResponseHanlder, null, null , false , null));
				
				polygonThread.start();
				
				try {
					polygonThread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		 }else{
			 // if provider not enabled set traffic and news filter keywords to null
				setTrafficFilterKeyword(null);
				setLocalNewsFilterKeyword(null);
		 }
	 
		
	}//start ends
		
		private class PolygonResponseHandler implements HttpResponseListener , HttpExceptionListener {

			@Override
			public void handleResponse(String response) {
			
				try {
					JSONObject responseJson = new JSONObject(response);
					
					String trafficPolygon = responseJson.getString(RConstants.TRAFFIC_POLYGON);
					if( trafficPolygon.equalsIgnoreCase("null")){
						setTrafficFilterKeyword(null);
					}else{
						JSONObject trafficPolygonObj = responseJson.getJSONObject(RConstants.TRAFFIC_POLYGON);
						setTrafficFilterKeyword(trafficPolygonObj.getString(RConstants.KEYWORD));
						
					}
					String localNewsPolygon = responseJson.getString(RConstants.LOCAL_NEWS_POLYGON);
				
					if(localNewsPolygon.equalsIgnoreCase("null")){
						setLocalNewsFilterKeyword(null);
					}else{
						JSONObject localNewsPolygonObj= responseJson.getJSONObject(RConstants.LOCAL_NEWS_POLYGON);
				
						setLocalNewsFilterKeyword(localNewsPolygonObj.getString(RConstants.KEYWORD));
					}
					
					
					if(RConstants.BUILD_DEBUBG){
						Log.i(TAG, response);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}

			@Override
			public void handleException(String exception) {
				// TODO Auto-generated method stub
				if(RConstants.BUILD_DEBUBG){
					Log.i(TAG, exception.toString());
				}
			}
			
		}

}
