package com.rivet.app.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;

import com.rivet.app.BaseActivity;
import com.rivet.app.abstracts.BuilderBase;
import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.common.ConnectionDetector;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.core.pojo.Story;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

/**
 * Created by brian on 12/6/14.
 */
public class StoryBuilder extends BuilderBase {

	public static String TAG = "StoryBuilder";
	Date startTimestamp;
	Date endTimestamp;
	ArrayList<Category> requiredCategories;
	ArrayList<Integer> filterByCategories;
	List<String> filterByKeywords = new ArrayList<String>();
	List<Object> filterByCoverage;
	int storyTypes;
	boolean includeExpired;
	private Context context;
	
	public int getStoryTypes() {
		return storyTypes;
	}

	public void setStoryTypes(int storyTypes) {
		this.storyTypes = storyTypes;
	}

	public StoryBuilder(Context context) {

		this.context = context;
		filterByCategories = new ArrayList<Integer>();
		filterByCoverage = new ArrayList<Object>();
		setDatasource(StoryDBAdapter.getStoryDBAdapter(context));

	}

	@Override
	public void start() {
	
		String mainPlayListUrl = RConstants.BaseUrl
				+ RConstants.MainPlayListUrl + buildParams();

		MainPlayListResponseHandler mainPlayListResponseHandler = new MainPlayListResponseHandler();
		
		if((new ConnectionDetector(context)).isConnectingToInternet()){

		Thread getStoriesThread = new Thread(new HttpRequest(mainPlayListUrl,
				HttpMethodType.GET, mainPlayListResponseHandler,
				mainPlayListResponseHandler, null, null , false , null));
		
		if(RConstants.BUILD_DEBUBG){
			Log.i(TAG, mainPlayListUrl); 
		}	
		getStoriesThread.start();

		try {
			getStoriesThread.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		}else{
			((BaseActivity)context).runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					((BaseActivity)context).showToast(RConstants.ENABLE_INTERNET_CONNECTION);
				}
			});
			
		}
	}

	public class MainPlayListResponseHandler implements HttpResponseListener,
			HttpExceptionListener {

		@Override
		public void handleResponse(String response) {
	
			for( ; ; ){
				try {
					JSONArray storiesArray = new JSONArray(response);
					for (int i = 0; i < storiesArray.length(); i++) {
						JSONObject story = storiesArray.getJSONObject(i);
						Story storyInfo = new Story();
						ArrayList<String> additionalCategory = new ArrayList<String>();
						ArrayList<String> keywordsArray = new ArrayList<String>();
						boolean additionalCategories = false;
						boolean keywords = false;
						storyInfo.setTrackID(story.getInt("trackID"));
						storyInfo.setPrimaryCategory(story
								.getInt("primaryCategory"));
						storyInfo.setTitle(story.getString("title"));
						storyInfo.setSource(story.getString("source"));
						storyInfo.setAnchorName(story.getString("anchorName"));
						storyInfo.setProducedBy(story.getString("producedBy"));
						storyInfo.setAudioLength(story.getInt("audioLength"));
						storyInfo.setIsLead(story.getBoolean("isLead"));
						storyInfo.setCreationTimestamp(story.getLong("creationTimestamp"));
						storyInfo.setUploadTimestamp(story.getLong("uploadTimestamp"));
						storyInfo.setCoverageID(story.getInt("coverageID"));
						storyInfo.setStoryTypeID(story.getInt("storyTypeID"));
					
						
						//additional categories
						JSONArray additionalCategoryArray = story.getJSONArray("additionalCategories");

						for (int j = 0; j < additionalCategoryArray.length(); j++) {
							additionalCategory.add(additionalCategoryArray
									.getString(j));
							additionalCategories = true;
						}
						if (additionalCategories){
							storyInfo.setAdditional_category_r(additionalCategory);
						}
						
						//keyword handling
						JSONArray keywordsArrayJSON = story.getJSONArray("keyWords");
						for (int k = 0; k < keywordsArrayJSON.length(); k++) {
							keywordsArray.add(keywordsArrayJSON.getString(k));
							keywords = true;
						}
						if (keywords){
							storyInfo.setKeyWordsString(keywordsArray);
						}
						storyInfo.setExpirationTimestamp(story.getLong("expirationTimestamp"));
						
						storyInfo.setUrl("");
						
						// add story to database
						((StoryDBAdapter) getDatasource()).addStoryInfo(storyInfo);
						
						if(RConstants.BUILD_DEBUBG){
							String msg = storyInfo.getTitle() + " id : " + storyInfo.getPrimaryCategory() + " track id : " + storyInfo.getTrackID() ;
		                	Log.i(TAG, msg);
						}	
					} // inner for loop ends here 
					
					
					// get out of here from outer loop
						break ;
					
				} catch (JSONException e) {
					
					// post logs when failure to load stories
					e.printStackTrace();
				} catch (SQLiteDatabaseLockedException DBLockedException) {
					Log.i(TAG, "StoryBuilder DB locked");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			} // outer for loop ends here 
			
			if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Stories for categories."+requiredCategories.toArray().toString()+" are uploaded to db");
			}
			
			if(requiredCategories.size()>3){
				
				RConstants.STORY_UPLOAD_TO_DB = true;
				
			}
			
		}

		

		@Override
		public void handleException(String exception) {
			// TODO Auto-generated method stub
			if(RConstants.BUILD_DEBUBG){
			Log.e(TAG, exception + "");
			}
			// post log from here that story building has been failed
			((BaseActivity)context).postRareLogsToServerAndFlurry(RConstants.AcvtivityFailureToLoadStories);
		}

	}

	public String buildParams() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'H:m:s'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String params = new String("?endTimestamp=");

		params += dateFormat.format(this.endTimestamp.getTime());
		
		// add categories, if there are any
		if (this.requiredCategories.size() > 0) {

			String categoryIds = "";
			for (int i = 0; i < requiredCategories.size(); i++) {
				categoryIds += String.valueOf(requiredCategories.get(i).getCategoryId() + ",");
			}
			params += "&requiredCategories=" + categoryIds;
		}
		if (this.includeExpired) {
			params += "&includeExpired=YES";
		} else {
			params += "&includeExpired=NO";
		}
		// add categories filter, if there any
		if (this.filterByCategories.size() > 0) {
			String filterCategoryIds = "";
			for (int i = 0; i < filterByCategories.size(); i++) {
				filterCategoryIds += String.valueOf(filterByCategories.get(i))
						+ ",";
			}
			params += "&filterByCategories=" + filterCategoryIds;
		}
		
		
		// add keywords filter, if there are any
		if (this.filterByKeywords.size() > 0) {
			String filterKeyword = "";
			for (int i = 0; i < filterByKeywords.size(); i++) {
				filterKeyword += filterByKeywords.get(i) + ",";
			}
			params += "&filterByKeywords=" + filterKeyword;
		}
		// add coverage filters, if there are any
		if (this.filterByCoverage.size() > 0) {
			String coverageId = "";
			for (int i = 0; i < filterByCoverage.size(); i++) {
				coverageId += String.valueOf(filterByCoverage.get(i)) + ",";
			}
			params += "&filterByCoverage=" + coverageId;
		}
		if (storyTypes != 0) {
			params += "&storyTypes=" + String.valueOf(storyTypes);
		}
		return params;
	}
	
}