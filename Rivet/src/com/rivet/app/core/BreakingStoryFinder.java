package com.rivet.app.core;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;

import com.gigya.json.JSONException;
import com.gigya.json.JSONObject;
import com.rivet.app.BaseActivity;
import com.rivet.app.BaseActivity.LocationPoints;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.common.GPS;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.core.pojo.MapPolygon;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

public class BreakingStoryFinder extends FinderBase {

	private Context ctx;
	private CategoryDBAdapter database;
	private ArrayList<Category> breakingNewscatList = new ArrayList<Category>();
	private MapPolygon mapPloygon;
	String mainURL = null ;
	GPS gps;

	private String trafficFilterKeyword = null ;
	private String localNewsFilterKeyword = null ;
	
	
	public BreakingStoryFinder(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		this.ctx = context;
		database = CategoryDBAdapter.getCategoryDBAdapterForWrite(context);

	}

	public void start() {
		Thread polygonThread=null;
		PolygonResponseHandler polygonResponseHanlder=null;
		
		mapPloygon = new MapPolygon();
		
		 gps = ((BaseActivity)this.ctx).getGPS();
		
		 if(! gps.isProviderDisabled()) {
			  LocationPoints location	= ((BaseActivity)ctx).getLocationPoints();
			  mapPloygon.setLongs(location.getLongs());
			  mapPloygon.setLatts(location.getLatts());
			   //mainURL = RConstants.BaseUrl +	"mappolygons?latitude=41.771312&longitude=-87.583008";
				
			   mainURL = RConstants.BaseUrl + RConstants.MAPPOLYGON + "?latitude=" + mapPloygon.getLatts() + "&longitude=" + mapPloygon.getLongs();
			 
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
			 
		 }
		 
			if((trafficFilterKeyword != null && localNewsFilterKeyword != null ))
			{	
				this.filterByKeywords.add(trafficFilterKeyword);
				this.filterByKeywords.add(localNewsFilterKeyword);
				
			}else{
				this.filterByKeywords.add("_zn_unusedKeyword");
			}
		 
		
		breakingNewscatList = database.getCategoryBreakingNews();
		BaseActivity bactivity = (BaseActivity) this.ctx;
		Long startTimeseconds = bactivity.prefStore.getLongData(
				RConstants.LAST_REFRESHED_TIME, Long.valueOf(0));

		if (startTimeseconds == 0) {

			this.startTimestamp = null;
		} else {

			this.startTimestamp = new Date(startTimeseconds);
		}

		this.endTimestamp = new Date();

		this.requiredCategories = this.breakingNewscatList;

		if (RConstants.IS_DEMO) {
			this.includeExpired = true;
		} else {
			this.includeExpired = false;
		}

	
		// tell the story build to start building.
		super.start();
		// this.lastRefreshTime = this.endTimestamp;
		bactivity.prefStore.setLongData(RConstants.LAST_REFRESHED_TIME,
				this.endTimestamp.getTime());
		
		
		 // post logs when location update
        
        ((BaseActivity)ctx).postRareLogsToServerAndFlurry(RConstants.ActivityLocationUpdate);
	}
	
	
	private class PolygonResponseHandler implements HttpResponseListener , HttpExceptionListener {

		


		@Override
		public void handleResponse(String response) {
			// TODO Auto-generated method stub
			//{"trafficPolygon":null,"localNewsPolygon":null}
			try {
				JSONObject responseJson = new JSONObject(response);
				
				String trafficPolygon = responseJson.getString("trafficPolygon");
				if( trafficPolygon.equalsIgnoreCase("null")){
					trafficFilterKeyword=null;
				}else{
					JSONObject trafficPolygonObj = responseJson.getJSONObject("trafficPolygon");
					trafficFilterKeyword = trafficPolygonObj.getString("keyword");
					
				}
				String localNewsPolygon = responseJson.getString("localNewsPolygon");
			
				if(localNewsPolygon.equalsIgnoreCase("null")){
					localNewsFilterKeyword=null;
				}else{
					JSONObject localNewsPolygonObj= responseJson.getJSONObject("localNewsPolygon");
					localNewsFilterKeyword = localNewsPolygonObj.getString("keyword");
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
