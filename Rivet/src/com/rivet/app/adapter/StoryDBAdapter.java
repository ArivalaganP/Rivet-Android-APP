package com.rivet.app.adapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.rivet.app.abstracts.RDBAdapterBase;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.pojo.Story;
import com.rivet.app.observer.UpdateStroiesObserver;

public class StoryDBAdapter extends RDBAdapterBase {
	
	private List<PropertyChangeListener> sDBListner = new ArrayList<PropertyChangeListener>();

	private static final String DATABASE_STORY_INFO_CREATE = "create table if not exists storyInfo(_id integer "
			+ " primary key autoincrement, trackID integer, title text, expirationTimestamp long,"
			+ " additionalDiscription text, producedBy text, additional_category_r text,"
			+ " offset integer, stream_url text, titleImageUrl text, url text , titleActionUrl text,"
			+ " storyTypeID integer, source text, primaryCategory integer, keyWordsString text, displayCategory integer,"
			+ " uploadTimestamp long, anchorName text, externalID integer, isLead boolean ,isPlayed int, audioLength double,"
			+ " bannerActionUrl text, bannerImageUrl text, adsDuration text , impressionTrackerUrl text ,creationTimestamp long,"
			+ " categoriesString text,coverageID integer,storyLike integer , adsSystemName text , adsImageURL text,"
			+ " companionClickThroughURl text , likeCount text);";
	

	public static final String _ID = "_id";
	public static final String STORY_TRACK_ID = "trackID";
	public static final String STORY_TITLE = "title";
	public static final String STORY_ADDITIONAL_DISCRIPTION = "additionalDiscription";
	public static final String STORY_ANCHOR_NAME = "anchorName";
	public static final String STORY_AUDIO_LENGTH = "audioLength";
	public static final String STORY_IS_LIKE = "storyLike" ;
	public static final String STORY_BANNER_ACTION_URL = "bannerActionUrl";
	public static final String STORY_BANNER_IMAGE_URL = "bannerImageUrl";
	public static final String STORY_CATEGORIES_STRING = "categoriesString";
	public static final String STORY_COVRAGE_ID = "coverageID";
	public static final String STORY_CREATION_TIMESTAMP = "creationTimestamp";
	public static final String STORY_DISPLAY_CATEGORY = "displayCategory";
	public static final String STORY_EXPIRATION_TIMESTAMP = "expirationTimestamp";
	public static final String STORY_EXTERNAL_ID = "externalID";
	public static final String STORY_IS_LEAD = "isLead";
	public static final String STORY_KEYWORDS_STRING = "keyWordsString";
	public static final String STORY_PRIMARY_CATEGORY = "primaryCategory";
	public static final String STORY_PRODUCED_BY = "producedBy";
	public static final String STORY_SOURCE = "source";
	public static final String STORY_STORY_TYPE_ID = "storyTypeID";
	public static final String STORY_TITLE_ACTION_URL = "titleActionUrl";
	public static final String STORY_TITLE_IMAGE_URL = "titleImageUrl";
	public static final String STORY_URL = "url";
	public static final String STORY_ADDITIONAL_CATEGORY_R = "additional_category_r";
	public static final String STORY_STREAM_URL = "stream_url";
	public static final String STORY_OFFSET = "offset";
	public static final String STORY_IS_PLAYED="isPlayed";
	public static final String STORY_UPLOAD_TIMESTAMP="uploadTimestamp";
	
	// for ads
	public static final String STORY_ADS_IMAGE_URL = "adsImageURL";
    public static final String STORY_COMPANION_CLICK_THROUGH_URL = "companionClickThroughURl" ;
    public static final String STORY_ADS_SYSTEM_NAME = "adsSystemName" ;
    public static final String STORY_ADS_DURATION = "adsDuration" ;
    public static final String STORY_IMPRESSION_TRACKER_URL = "impressionTrackerUrl" ;

	private static final String DATABASE_STORY_INFO_TABLE = "storyInfo";
	private static StoryDBAdapter mStoryDBAdapter = null;
	Context ctx=null;

	private CategoryDBAdapter catDBAdapterForRead;
	
	private StoryDBAdapter(Context ctx) {
		super(ctx , true);
		createTable(DATABASE_STORY_INFO_CREATE , true);
		this.ctx = ctx;
		catDBAdapterForRead = CategoryDBAdapter.getCategoryDBAdapterForRead(this.ctx);
	}

	public static StoryDBAdapter getStoryDBAdapter(Context ctx) {
	
		synchronized (StoryDBAdapter.class) {

			if (mStoryDBAdapter == null) {
				mStoryDBAdapter = new StoryDBAdapter(ctx);

			}
		}

		return mStoryDBAdapter;
	}

	private int storyExistOrNot(Story story) {
		
		String query = "select "+STORY_TRACK_ID+" from " + DATABASE_STORY_INFO_TABLE
				+ " where trackID = " + story.getTrackID();
		Cursor cursor = this.mDbForWrite.rawQuery(query, null);
		int id = 0;

		if (cursor.moveToFirst()) {
			do {
				id = cursor.getInt(cursor.getColumnIndex(STORY_TRACK_ID));
				// do what ever you want here
			} while (cursor.moveToNext());
		}
		cursor.close();

		return id;
	}

	public void removeStoryFromDatabase(Story story) {

		String query = "delete from " + DATABASE_STORY_INFO_TABLE
				+ " where trackID = " + story.getTrackID();
		if (storyExistOrNot(story) > 0)
			mDbForWrite.rawQuery(query, null);

	}
	
	public void setStoryLiked(int trackId , boolean flag){
		int storyLiked = 0 ;
		if(flag){
		 storyLiked = 1 ;
		}
		else{
			storyLiked = 0 ;
		}
		String query = "UPDATE " + DATABASE_STORY_INFO_TABLE + " SET " + STORY_IS_LIKE + " = " + storyLiked + " WHERE " + STORY_TRACK_ID + " = " + trackId ;
		
		mDbForWrite.execSQL(query);
	}

	public void setStoryPlayed(int trackID){
		int storyPlayed=1;
		String query = "UPDATE  " + DATABASE_STORY_INFO_TABLE + " SET "+STORY_IS_PLAYED+"="+storyPlayed+" WHERE "+STORY_TRACK_ID+"="+trackID;
		mDbForWrite.execSQL(query);
		String selquery = "select * from "+ DATABASE_STORY_INFO_TABLE+ " where "+STORY_TRACK_ID+"="+trackID;
		Cursor cursor = mDbForWrite.rawQuery(selquery, null);
		if (cursor.moveToFirst()) {
			do {
				int isLead = cursor.getInt(cursor.getColumnIndex(STORY_IS_LEAD));
				int isPlayed = cursor.getInt(cursor.getColumnIndex(STORY_IS_PLAYED));
				Log.i("storyDBAdapter", "trackid"+trackID+" storyislead"+isLead+"isStoryPlayed"+isPlayed);
				
			} while (cursor.moveToNext());
		}
		cursor.close();
		
	}
	
	public void addStoryInfo(Story story) {

		
		if (storyExistOrNot(story) > 0)
			return;

		ContentValues values = new ContentValues();
		int storyPlayed=0;
		String keywordStringsWithCommas = ListToCommaSeperatedString(story
				.getKeyWordsString());
		String additionalCategoriesWithCommas = ListToCommaSeperatedString(story
				.getAdditional_category_r());

		values.put(STORY_TRACK_ID, story.getTrackID());
		values.put(STORY_TITLE, story.getTitle());
		values.put(STORY_ADDITIONAL_DISCRIPTION,
				story.getAdditionalDescription());
		values.put(STORY_ANCHOR_NAME, story.getAnchorName());
		values.put(STORY_AUDIO_LENGTH, story.getAudioLength());
		values.put(STORY_BANNER_ACTION_URL, story.getBannerActionUrl());
		values.put(STORY_BANNER_IMAGE_URL, story.getBannerImageUrl());
		values.put(STORY_CATEGORIES_STRING, story.getCategoriesString());
		values.put(STORY_COVRAGE_ID, story.getCoverageID());
		values.put(STORY_CREATION_TIMESTAMP, story.getCreationTimestamp());
		values.put(STORY_DISPLAY_CATEGORY, story.getDisplayCategory());
		values.put(STORY_EXPIRATION_TIMESTAMP, story.getExpirationTimestamp());
		values.put(STORY_EXTERNAL_ID, story.getExternalID());
		values.put(STORY_IS_LEAD, story.getIsLead());
		values.put(STORY_IS_LIKE , story.getIsLike());
		values.put(STORY_KEYWORDS_STRING, keywordStringsWithCommas);
		values.put(STORY_PRIMARY_CATEGORY, story.getPrimaryCategory());
		values.put(STORY_PRODUCED_BY, story.getProducedBy());
		values.put(STORY_STORY_TYPE_ID, story.getStoryTypeID());
		values.put(STORY_SOURCE, story.getSource());
		values.put(STORY_TITLE_ACTION_URL, story.getTitleActionUrl());
		values.put(STORY_TITLE_IMAGE_URL, story.getTitleImageUrl());
		values.put(STORY_ADDITIONAL_CATEGORY_R, additionalCategoriesWithCommas);
		values.put(STORY_STREAM_URL, story.getStream_url());
		values.put(STORY_OFFSET, story.getOffset());
		values.put(STORY_IS_PLAYED, storyPlayed);
		values.put(STORY_UPLOAD_TIMESTAMP, story.getUploadTimestamp());
		values.put(STORY_URL,story.getUrl());

		
		
		mDbForWrite.insert(DATABASE_STORY_INFO_TABLE, null, values);
		
		
		// story inserted after stackOver flow
		
		if(ContentManager.getContentManager(ctx).getFlagNoStoryToPlay()){
			RConstants.storyInserted ++ ;
		}
		
		if(RConstants.storyInserted > 2 && (ContentManager.getContentManager(ctx).getFlagNoStoryToPlay())){
			notifyListeners();
			RConstants.storyInserted = 0 ;
		}
		
	

	}

	public ArrayList<Story> getAllStories(String keywords) {
	boolean	shouldAddInPlayList = true ;
		String query = "select * from " + DATABASE_STORY_INFO_TABLE;
		ArrayList<Story> listOfStories = new ArrayList<Story>();

		if (listOfStories != null) {
			listOfStories.clear();
		}

		Cursor cursor = mDbForWrite.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {

				Story story = new Story();

				story.setTrackID(cursor.getInt(cursor
						.getColumnIndex(STORY_TRACK_ID)));
				story.setTitle(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE)));
				story.setIsLike(cursor.getInt(cursor.getColumnIndex(STORY_IS_LIKE)));
				story.setAdditionalDescription(cursor.getString(cursor
						.getColumnIndex(STORY_ADDITIONAL_DISCRIPTION)));
				story.setAnchorName(cursor.getString(cursor
						.getColumnIndex(STORY_ANCHOR_NAME)));
				story.setAudioLength(cursor.getInt(cursor
						.getColumnIndex(STORY_AUDIO_LENGTH)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_ACTION_URL)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_IMAGE_URL)));
				story.setCategoriesString(cursor.getString(cursor
						.getColumnIndex(STORY_CATEGORIES_STRING)));
				story.setCoverageID(cursor.getInt(cursor
						.getColumnIndex(STORY_COVRAGE_ID)));
				
				story.setCreationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_CREATION_TIMESTAMP)));
				story.setUploadTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_UPLOAD_TIMESTAMP)));
				story.setExpirationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_EXPIRATION_TIMESTAMP)));
				story.setSource(cursor.getString(cursor.getColumnIndex(STORY_SOURCE)));
				story.setDisplayCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_DISPLAY_CATEGORY)));
				
				story.setExternalID(cursor.getInt(cursor
						.getColumnIndex(STORY_EXTERNAL_ID)));
				int isLead = cursor
						.getInt(cursor.getColumnIndex(STORY_IS_LEAD));
				story.setIsLead((isLead == 1) ? true : false);

				// change stringWithCommas to ArrayList
				String str = cursor.getString(cursor
						.getColumnIndex(STORY_KEYWORDS_STRING));

				ArrayList<String> listKeywords = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setKeyWordsString(listKeywords);
				
				if(keywords != null){
					keywords = keywords.toLowerCase(Locale.getDefault());
					
					if(listKeywords.size() > 0){
					for(String locationKeyword: listKeywords) {
						if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
							if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains(keywords)){
								shouldAddInPlayList = true ;
								break ;
							}else{
								shouldAddInPlayList = false ; 
							}
						}else{
							shouldAddInPlayList = true ;
						}
					}
					}else{
						shouldAddInPlayList = true ;
					}
					
					}else{
						
						if(listKeywords.size() > 0){
							for(String locationKeyword: listKeywords) {
								if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
									shouldAddInPlayList = false ;
									break ;
								}else{
									shouldAddInPlayList = true ;
								}
							}
							}
						
					}

				story.setPrimaryCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_PRIMARY_CATEGORY)));
				story.setProducedBy(cursor.getString(cursor
						.getColumnIndex(STORY_PRODUCED_BY)));
				story.setStoryTypeID(cursor.getInt(cursor
						.getColumnIndex(STORY_STORY_TYPE_ID)));
				story.setTitleActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_ACTION_URL)));
				story.setTitleImageUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_IMAGE_URL)));
				story.setAdsImageURL(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_IMAGE_URL)));
				story.setAdsSystemName(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_SYSTEM_NAME)));
				story.setCompanionClickThroughUrl(cursor.getString(cursor
						.getColumnIndex(STORY_COMPANION_CLICK_THROUGH_URL)));
				story.setAdsDuration(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_DURATION)));
				story.setImpressionTrackerUrl(cursor.getString(cursor
						.getColumnIndex(STORY_IMPRESSION_TRACKER_URL)));
				
				
				// change stringWithCommas to ArrayList
			
				ArrayList<String> additional_category_r = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setAdditional_category_r(additional_category_r);

				story.setStream_url(cursor.getString(cursor
						.getColumnIndex(STORY_STREAM_URL)));
				story.setOffset(cursor.getInt(cursor
						.getColumnIndex(STORY_OFFSET)));

				if(shouldAddInPlayList){
				listOfStories.add(story);
				}

			} while (cursor.moveToNext());
		}
		cursor.close();
		return listOfStories;
	}
	
	public Cursor getAllMusic (){
		return mDbForWrite.rawQuery("select * from storyInfo where " + STORY_PRIMARY_CATEGORY + " = 777", null);
	}


	public ArrayList<Story> getStoriesByCategoriesId(ArrayList<Integer> allcatListID , ArrayList<Integer> playedTrackIDList
			,int count , long currentTimeStamp , String keywords) {
		boolean shouldAddInPlayList = true ;
		int storyPlayed=0;
		ArrayList<Story> listStories = new ArrayList<Story>();

		String whereClause = " where ( " + STORY_PRIMARY_CATEGORY + " = "
				+ allcatListID.get(0);
		
		for (int idx = 1; idx < allcatListID.size(); idx++) {
			whereClause += " OR " + STORY_PRIMARY_CATEGORY + " = " +allcatListID.get(idx);
	
		}
		whereClause+= " ) AND "+ STORY_IS_PLAYED + " = " + storyPlayed;
		
		if(playedTrackIDList.size()>0){
			for(int idx=0;idx<playedTrackIDList.size();idx++){
				whereClause +=  " AND "+STORY_TRACK_ID+" <> "+playedTrackIDList.get(idx);
			}
		}
		if(allcatListID.size()>0){
			if(allcatListID.get(0) != RConstants.IPOD_MUSIC_CATEGORY)
		whereClause+= " AND " + STORY_EXPIRATION_TIMESTAMP + " > " + currentTimeStamp ;
			
		}
		whereClause+=  " ORDER BY "+STORY_UPLOAD_TIMESTAMP+" DESC";
		
		//add limit to get sotries in count if count 0 then get 1
		if(count>0){
			whereClause+= " limit 0,"+count;
		}
		    
	

		String query = "select * from " + DATABASE_STORY_INFO_TABLE+whereClause;

		Cursor cursor = mDbForWrite.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				Story story = new Story();

				story.setTrackID(cursor.getInt(cursor
						.getColumnIndex(STORY_TRACK_ID)));
				story.setTitle(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE)));
				story.setAdditionalDescription(cursor.getString(cursor
						.getColumnIndex(STORY_ADDITIONAL_DISCRIPTION)));
				story.setAnchorName(cursor.getString(cursor
						.getColumnIndex(STORY_ANCHOR_NAME)));
				story.setAudioLength(cursor.getInt(cursor
						.getColumnIndex(STORY_AUDIO_LENGTH)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_ACTION_URL)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_IMAGE_URL)));
				story.setCategoriesString(cursor.getString(cursor
						.getColumnIndex(STORY_CATEGORIES_STRING)));
				story.setCoverageID(cursor.getInt(cursor
						.getColumnIndex(STORY_COVRAGE_ID)));
				story.setIsLike(cursor.getInt(cursor.getColumnIndex(STORY_IS_LIKE)));
				story.setSource(cursor.getString(cursor.getColumnIndex(STORY_SOURCE)));
				story.setCreationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_CREATION_TIMESTAMP)));
				story.setUploadTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_UPLOAD_TIMESTAMP)));
				story.setExpirationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_EXPIRATION_TIMESTAMP)));
				
				story.setDisplayCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_DISPLAY_CATEGORY)));
				
				story.setExternalID(cursor.getInt(cursor
						.getColumnIndex(STORY_EXTERNAL_ID)));
				int isLead = cursor
						.getInt(cursor.getColumnIndex(STORY_IS_LEAD));
				story.setIsLead((isLead == 1) ? true : false);

				// change stringWithCommas to ArrayList
				String str = cursor.getString(cursor
						.getColumnIndex(STORY_KEYWORDS_STRING));

				ArrayList<String> listKeywords = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setKeyWordsString(listKeywords);
				
				
				if(keywords != null){
				keywords = keywords.toLowerCase(Locale.getDefault());
				
				if(listKeywords.size() > 0){
				for(String locationKeyword: listKeywords) {
					if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
						if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains(keywords)){
							shouldAddInPlayList = true ;
							break ;
						}else{
							shouldAddInPlayList = false ; 
						}
					}else{
						shouldAddInPlayList = true ;
					}
				}
				}else{
					shouldAddInPlayList = true ;
				}
				
				}else{
					
					if(listKeywords.size() > 0){
						for(String locationKeyword: listKeywords) {
							if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
								shouldAddInPlayList = false ;
								break ;
							}else{
								shouldAddInPlayList = true ;
							}
						}
						}
					
				}
				
				story.setPrimaryCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_PRIMARY_CATEGORY)));
				story.setProducedBy(cursor.getString(cursor
						.getColumnIndex(STORY_PRODUCED_BY)));
				story.setStoryTypeID(cursor.getInt(cursor
						.getColumnIndex(STORY_STORY_TYPE_ID)));
				story.setTitleActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_ACTION_URL)));
				story.setTitleImageUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_IMAGE_URL)));
				story.setAdsImageURL(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_IMAGE_URL)));
				story.setAdsSystemName(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_SYSTEM_NAME)));
				story.setCompanionClickThroughUrl(cursor.getString(cursor
						.getColumnIndex(STORY_COMPANION_CLICK_THROUGH_URL)));
				story.setAdsDuration(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_DURATION)));
				story.setImpressionTrackerUrl(cursor.getString(cursor
						.getColumnIndex(STORY_IMPRESSION_TRACKER_URL)));

				// change stringWithCommas to ArrayList
			
				ArrayList<String> additional_category_r = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setAdditional_category_r(additional_category_r);

				story.setStream_url(cursor.getString(cursor
						.getColumnIndex(STORY_STREAM_URL)));
				
				story.setUrl(cursor.getString(cursor.getColumnIndex(STORY_URL)));
				
				story.setOffset(cursor.getInt(cursor
						.getColumnIndex(STORY_OFFSET)));
				if (story.getPrimaryCategory() == RConstants.ADS_CATEGORY) {
					
					story.setCategoryName(RConstants.ADVERTISEMENT_TITLE);
					story.setParentCategoryID(RConstants.ADS_CATEGORY);
					
				}else{

					story.setCategoryName(catDBAdapterForRead
							.getCategoryNameByCategoryId(catDBAdapterForRead
									.getParentCategoryID(story
											.getPrimaryCategory())));
					story.setParentCategoryID(catDBAdapterForRead
							.getParentCategoryID(story.getPrimaryCategory()));
				
				}
				
				if(shouldAddInPlayList){
				listStories.add(story);
				}
				
			} while (cursor.moveToNext());
		}

		return listStories;
	}
	
	
public ArrayList<Story> getAdsByCategoriesId(ArrayList<Integer> allcatListID , ArrayList<Integer> playedTrackIDList,int count , long currentTimeStamp) {
		
		int storyPlayed=0;
		ArrayList<Story> listStories = new ArrayList<Story>();
		String whereClause = " where ( " + STORY_PRIMARY_CATEGORY + " = "
				+ allcatListID.get(0);
	
		whereClause+= " ) AND "+ STORY_IS_PLAYED + " = " + storyPlayed;
	
		whereClause+=  " ORDER BY "+STORY_UPLOAD_TIMESTAMP+" DESC";
	
		String query = "select * from " + DATABASE_STORY_INFO_TABLE + whereClause ;

		Cursor cursor = mDbForWrite.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				Story story = new Story();

				story.setTrackID(cursor.getInt(cursor
						.getColumnIndex(STORY_TRACK_ID)));
				story.setTitle(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE)));
				story.setAdditionalDescription(cursor.getString(cursor
						.getColumnIndex(STORY_ADDITIONAL_DISCRIPTION)));
				story.setAnchorName(cursor.getString(cursor
						.getColumnIndex(STORY_ANCHOR_NAME)));
				story.setAudioLength(cursor.getInt(cursor
						.getColumnIndex(STORY_AUDIO_LENGTH)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_ACTION_URL)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_IMAGE_URL)));
				story.setCategoriesString(cursor.getString(cursor
						.getColumnIndex(STORY_CATEGORIES_STRING)));
				story.setCoverageID(cursor.getInt(cursor
						.getColumnIndex(STORY_COVRAGE_ID)));
				story.setIsLike(cursor.getInt(cursor.getColumnIndex(STORY_IS_LIKE)));
				story.setSource(cursor.getString(cursor.getColumnIndex(STORY_SOURCE)));
				story.setCreationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_CREATION_TIMESTAMP)));
				story.setUploadTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_UPLOAD_TIMESTAMP)));
				story.setExpirationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_EXPIRATION_TIMESTAMP)));
				
				story.setDisplayCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_DISPLAY_CATEGORY)));
				
				story.setExternalID(cursor.getInt(cursor
						.getColumnIndex(STORY_EXTERNAL_ID)));
				int isLead = cursor
						.getInt(cursor.getColumnIndex(STORY_IS_LEAD));
				story.setIsLead((isLead == 1) ? true : false);

				story.setPrimaryCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_PRIMARY_CATEGORY)));
				story.setProducedBy(cursor.getString(cursor
						.getColumnIndex(STORY_PRODUCED_BY)));
				story.setStoryTypeID(cursor.getInt(cursor
						.getColumnIndex(STORY_STORY_TYPE_ID)));
				story.setTitleActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_ACTION_URL)));
				story.setTitleImageUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_IMAGE_URL)));
				story.setAdsImageURL(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_IMAGE_URL)));
				story.setAdsSystemName(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_SYSTEM_NAME)));
				story.setCompanionClickThroughUrl(cursor.getString(cursor
						.getColumnIndex(STORY_COMPANION_CLICK_THROUGH_URL)));
				story.setAdsDuration(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_DURATION)));
				story.setImpressionTrackerUrl(cursor.getString(cursor
						.getColumnIndex(STORY_IMPRESSION_TRACKER_URL)));

				// change stringWithCommas to ArrayList
//			
//				ArrayList<String> additional_category_r = new ArrayList<String>(
//						Arrays.asList(str.split("\\s*,\\s*")));
//				story.setAdditional_category_r(additional_category_r);

				story.setStream_url(cursor.getString(cursor
						.getColumnIndex(STORY_STREAM_URL)));
				
				story.setUrl(cursor.getString(cursor.getColumnIndex(STORY_URL)));
				
				story.setOffset(cursor.getInt(cursor
						.getColumnIndex(STORY_OFFSET)));
				if (story.getPrimaryCategory() == RConstants.ADS_CATEGORY) {
					
					story.setCategoryName(RConstants.ADVERTISEMENT_TITLE);
					story.setParentCategoryID(RConstants.ADS_CATEGORY);
					
				}else{

					story.setCategoryName(catDBAdapterForRead
							.getCategoryNameByCategoryId(catDBAdapterForRead
									.getParentCategoryID(story
											.getPrimaryCategory())));
					story.setParentCategoryID(catDBAdapterForRead
							.getParentCategoryID(story.getPrimaryCategory()));
				
				}
				listStories.add(story);
			} while (cursor.moveToNext());
		}

		return listStories;
	}

	


	public ArrayList<Story> getNonLeadStoriesOrLeadStoriesFromDb(ArrayList<Integer> allcatListID, int isLead
			, int count,  ArrayList<Integer> playedTrackIDList , long currentTimeStamp , String keywords) {
		boolean shouldAddInPlayList = true ;
		int storytype=1;
		ArrayList<Story> listOFStories = new ArrayList<Story>();
		int storyPlayed=0;
		String whereClause = " WHERE ( " + STORY_PRIMARY_CATEGORY + " = "+ allcatListID.get(0) ;
		
		for (int idx = 1; idx < allcatListID.size(); idx++) {
			whereClause += " OR " + STORY_PRIMARY_CATEGORY + " = " +allcatListID.get(idx);
	
		}
		whereClause += "  ) AND "+ STORY_IS_LEAD +" = "+isLead;
		whereClause += " AND  "+STORY_STORY_TYPE_ID+ "="+storytype;
		whereClause+=  " AND "+ STORY_IS_PLAYED + "="+storyPlayed;
		whereClause+=  " AND "+ STORY_EXPIRATION_TIMESTAMP + " > " + currentTimeStamp ;
		
		if(playedTrackIDList.size()>0){
			for(int idx=0;idx<playedTrackIDList.size();idx++){
				
				whereClause+=  " AND "+STORY_TRACK_ID+" <>"+playedTrackIDList.get(idx);
			}
		}
		
		whereClause+=  " ORDER BY "+STORY_UPLOAD_TIMESTAMP+" DESC";		
		
		//add limit to get sotries in count if count 0 then get 1
		if(count>0){
			whereClause+= " limit 0,"+count;
		}
		
		String query = "select * from " + DATABASE_STORY_INFO_TABLE+ whereClause;

		Cursor cursor = mDbForWrite.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				Story story = new Story();

				story.setTrackID(cursor.getInt(cursor
						.getColumnIndex(STORY_TRACK_ID)));
				story.setTitle(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE)));

		    
				story.setAdditionalDescription(cursor.getString(cursor
						.getColumnIndex(STORY_ADDITIONAL_DISCRIPTION)));
				story.setAnchorName(cursor.getString(cursor
						.getColumnIndex(STORY_ANCHOR_NAME)));
				story.setAudioLength(cursor.getInt(cursor
						.getColumnIndex(STORY_AUDIO_LENGTH)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_ACTION_URL)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_IMAGE_URL)));
				story.setCategoriesString(cursor.getString(cursor
						.getColumnIndex(STORY_CATEGORIES_STRING)));
				story.setCoverageID(cursor.getInt(cursor
						.getColumnIndex(STORY_COVRAGE_ID)));
				
				story.setCreationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_CREATION_TIMESTAMP)));
				story.setUploadTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_UPLOAD_TIMESTAMP)));
				story.setExpirationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_EXPIRATION_TIMESTAMP)));
				story.setIsLike(cursor.getInt(cursor.getColumnIndex(STORY_IS_LIKE)));
				story.setSource(cursor.getString(cursor.getColumnIndex(STORY_SOURCE)));
				story.setDisplayCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_DISPLAY_CATEGORY)));
				
				story.setExternalID(cursor.getInt(cursor
						.getColumnIndex(STORY_EXTERNAL_ID)));
				int isLeading = cursor.getInt(cursor
						.getColumnIndex(STORY_IS_LEAD));
				story.setIsLead((isLeading == 1) ? true : false);

				// change stringWithCommas to ArrayList
				String str = cursor.getString(cursor
						.getColumnIndex(STORY_KEYWORDS_STRING));

				ArrayList<String> listKeywords = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setKeyWordsString(listKeywords);
				
				if(keywords != null){
					keywords = keywords.toLowerCase(Locale.getDefault());
					
					if(listKeywords.size() > 0){
					for(String locationKeyword: listKeywords) {
						if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
							if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains(keywords)){
								shouldAddInPlayList = true ;
								break ;
							}else{
								shouldAddInPlayList = false ; 
							}
						}else{
							shouldAddInPlayList = true ;
						}
					}
					}else{
						shouldAddInPlayList = true ;
					}
					
					}else{
						
						if(listKeywords.size() > 0){
							for(String locationKeyword: listKeywords) {
								if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
									shouldAddInPlayList = false ;
									break ;
								}else{
									shouldAddInPlayList = true ;
								}
							}
							}
						
					}
			

				story.setPrimaryCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_PRIMARY_CATEGORY)));
				story.setProducedBy(cursor.getString(cursor
						.getColumnIndex(STORY_PRODUCED_BY)));
				story.setStoryTypeID(cursor.getInt(cursor
						.getColumnIndex(STORY_STORY_TYPE_ID)));
				story.setTitleActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_ACTION_URL)));
				story.setTitleImageUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_IMAGE_URL)));
				story.setAdsImageURL(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_IMAGE_URL)));
				story.setAdsSystemName(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_SYSTEM_NAME)));
				story.setCompanionClickThroughUrl(cursor.getString(cursor
						.getColumnIndex(STORY_COMPANION_CLICK_THROUGH_URL)));
				story.setAdsDuration(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_DURATION)));
				story.setImpressionTrackerUrl(cursor.getString(cursor
						.getColumnIndex(STORY_IMPRESSION_TRACKER_URL)));

				// change stringWithCommas to ArrayList
				
				ArrayList<String> additional_category_r = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setAdditional_category_r(additional_category_r);

				story.setStream_url(cursor.getString(cursor
						.getColumnIndex(STORY_STREAM_URL)));
				story.setOffset(cursor.getInt(cursor
						.getColumnIndex(STORY_OFFSET)));
				
				if (story.getPrimaryCategory() == RConstants.ADS_CATEGORY) {
					
					story.setCategoryName(RConstants.ADVERTISEMENT_TITLE);
					story.setParentCategoryID(RConstants.ADS_CATEGORY);
					
				}else{

					story.setCategoryName(catDBAdapterForRead
							.getCategoryNameByCategoryId(catDBAdapterForRead
									.getParentCategoryID(story
											.getPrimaryCategory())));
					story.setParentCategoryID(catDBAdapterForRead
							.getParentCategoryID(story.getPrimaryCategory()));
				
				}
				// add only filtered stories
				if(shouldAddInPlayList){
				listOFStories.add(story);
				}
			} while (cursor.moveToNext());
		}
		cursor.close();
		return listOFStories;
	}
	
	
	public ArrayList<Story> getPodcastStoriesForCategoriesFromDb(ArrayList<Integer> allcatListID, int count,
			                                     int storyType,ArrayList<Integer> playedTrackIDList , long currentTimeStamp , String keywords) {
		ArrayList<Story> listOFStories = new ArrayList<Story>();
		boolean shouldAddInPlayList = true ;
		int storyPlayed=0;
		String whereClause = " WHERE  (" + STORY_PRIMARY_CATEGORY + " = "+ allcatListID.get(0) ;
		
		for (int idx = 1; idx < allcatListID.size(); idx++) {
			whereClause += " OR " + STORY_PRIMARY_CATEGORY + " = " +allcatListID.get(idx);
	
		}
		whereClause +=" ) AND  "+STORY_STORY_TYPE_ID+ "="+storyType;
		whereClause+= " AND "+ STORY_IS_PLAYED + "="+storyPlayed ;
		whereClause+= " AND " + STORY_EXPIRATION_TIMESTAMP + " > " + currentTimeStamp ;
		if(playedTrackIDList.size()>0){
			for(int idx=0;idx<playedTrackIDList.size();idx++){
				whereClause+=  " AND "+STORY_TRACK_ID+" <>"+playedTrackIDList.get(idx);
			}
		}
		whereClause+= " ORDER BY "+STORY_UPLOAD_TIMESTAMP+" DESC";
		
		//add limit to get sotries in count if count 0 then get 1
		if(count>0){
			whereClause+= " limit 0,"+count;
		}
		
		
		String query = "select * from " + DATABASE_STORY_INFO_TABLE+ whereClause;

		Cursor cursor = mDbForWrite.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				Story story = new Story();

				story.setTrackID(cursor.getInt(cursor
						.getColumnIndex(STORY_TRACK_ID)));
				story.setTitle(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE)));


				story.setAdditionalDescription(cursor.getString(cursor
						.getColumnIndex(STORY_ADDITIONAL_DISCRIPTION)));
				story.setAnchorName(cursor.getString(cursor
						.getColumnIndex(STORY_ANCHOR_NAME)));
				story.setAudioLength(cursor.getInt(cursor
						.getColumnIndex(STORY_AUDIO_LENGTH)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_ACTION_URL)));
				story.setBannerActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_BANNER_IMAGE_URL)));
				story.setCategoriesString(cursor.getString(cursor
						.getColumnIndex(STORY_CATEGORIES_STRING)));
				story.setCoverageID(cursor.getInt(cursor
						.getColumnIndex(STORY_COVRAGE_ID)));
				story.setIsLike(cursor.getInt(cursor.getColumnIndex(STORY_IS_LIKE)));
				story.setDisplayCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_DISPLAY_CATEGORY)));
				
				story.setCreationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_CREATION_TIMESTAMP)));
				story.setUploadTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_UPLOAD_TIMESTAMP)));
				story.setExpirationTimestamp(cursor.getLong(cursor
						.getColumnIndex(STORY_EXPIRATION_TIMESTAMP)));
				
				story.setExternalID(cursor.getInt(cursor
						.getColumnIndex(STORY_EXTERNAL_ID)));
				int isLeading = cursor.getInt(cursor
						.getColumnIndex(STORY_IS_LEAD));
				story.setIsLead((isLeading == 1) ? true : false);

				// change stringWithCommas to ArrayList
				String str = cursor.getString(cursor
						.getColumnIndex(STORY_KEYWORDS_STRING));

				ArrayList<String> listKeywords = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setKeyWordsString(listKeywords);
				
				if(keywords != null){
					keywords = keywords.toLowerCase(Locale.getDefault());
					
					if(listKeywords.size() > 0){
					for(String locationKeyword: listKeywords) {
						if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
							if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains(keywords)){
								shouldAddInPlayList = true ;
								break ;
							}else{
								shouldAddInPlayList = false ; 
							}
						}else{
							shouldAddInPlayList = true ;
						}
					}
					}else{
						shouldAddInPlayList = true ;
					}
					
					}else{
						
						if(listKeywords.size() > 0){
							for(String locationKeyword: listKeywords) {
								if(locationKeyword.trim().toLowerCase(Locale.getDefault()).contains("zn_")){
									shouldAddInPlayList = false ;
									break ;
								}else{
									shouldAddInPlayList = true ;
								}
							}
							}
						
					}

				story.setPrimaryCategory(cursor.getInt(cursor
						.getColumnIndex(STORY_PRIMARY_CATEGORY)));
				story.setProducedBy(cursor.getString(cursor
						.getColumnIndex(STORY_PRODUCED_BY)));
				story.setStoryTypeID(cursor.getInt(cursor
						.getColumnIndex(STORY_STORY_TYPE_ID)));
				story.setTitleActionUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_ACTION_URL)));
				story.setTitleImageUrl(cursor.getString(cursor
						.getColumnIndex(STORY_TITLE_IMAGE_URL)));
				story.setAdsImageURL(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_IMAGE_URL)));
				story.setAdsSystemName(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_SYSTEM_NAME)));
				story.setCompanionClickThroughUrl(cursor.getString(cursor
						.getColumnIndex(STORY_COMPANION_CLICK_THROUGH_URL)));
				story.setAdsDuration(cursor.getString(cursor
						.getColumnIndex(STORY_ADS_DURATION)));
				story.setImpressionTrackerUrl(cursor.getString(cursor
						.getColumnIndex(STORY_IMPRESSION_TRACKER_URL)));

				// change stringWithCommas to ArrayList
		
				ArrayList<String> additional_category_r = new ArrayList<String>(
						Arrays.asList(str.split("\\s*,\\s*")));
				story.setAdditional_category_r(additional_category_r);

				story.setStream_url(cursor.getString(cursor
						.getColumnIndex(STORY_STREAM_URL)));
				story.setOffset(cursor.getInt(cursor
						.getColumnIndex(STORY_OFFSET)));
				
				if (story.getPrimaryCategory() == RConstants.ADS_CATEGORY) {
					
					story.setCategoryName(RConstants.ADVERTISEMENT_TITLE);
					story.setParentCategoryID(RConstants.ADS_CATEGORY);
					
				}else{

					story.setCategoryName(catDBAdapterForRead
							.getCategoryNameByCategoryId(catDBAdapterForRead
									.getParentCategoryID(story
											.getPrimaryCategory())));
					story.setParentCategoryID(catDBAdapterForRead
							.getParentCategoryID(story.getPrimaryCategory()));
				
				}
				
				if(shouldAddInPlayList){
				listOFStories.add(story);
				}
				
			} while (cursor.moveToNext());
		}

		cursor.close();
		return listOFStories;
	}

	public void addChangeListener(UpdateStroiesObserver updateStoriesObserver) {
		
		sDBListner.add(updateStoriesObserver);
	}
	
	public void notifyListeners(){
		for(PropertyChangeListener observer : sDBListner){
			observer.propertyChange(new PropertyChangeEvent(this, RConstants.NEW_STORIES_ADDED, null, null));
		}
	}

	// method is created to adds the ads in database
	
	public void addAds(Story adsStory) {
		int storyPlayed=0;
		ContentValues adsContentValues = new ContentValues();
		adsContentValues.put(STORY_TITLE, adsStory.getTitle());
		adsContentValues.put(STORY_URL, adsStory.getUrl());
		adsContentValues.put(STORY_PRIMARY_CATEGORY, adsStory.getPrimaryCategory());
		adsContentValues.put(STORY_IS_PLAYED, storyPlayed);
		adsContentValues.put(STORY_ADS_IMAGE_URL, adsStory.getAdsImageURL());
		adsContentValues.put(STORY_COMPANION_CLICK_THROUGH_URL, adsStory.getCompanionClickThroughUrl());
		adsContentValues.put(STORY_ADS_SYSTEM_NAME, adsStory.getAdsSystemName());
		adsContentValues.put(STORY_ADS_DURATION, adsStory.getAdsDuration());
		adsContentValues.put(STORY_IMPRESSION_TRACKER_URL, adsStory.getImpressionTrackerUrl());
		adsContentValues.put(STORY_EXPIRATION_TIMESTAMP, adsStory.getExpirationTimestamp());
		adsContentValues.put(STORY_UPLOAD_TIMESTAMP, adsStory.getUploadTimestamp());
		adsContentValues.put(STORY_TRACK_ID, adsStory.getTrackID());
		
		mDbForWrite.insert(DATABASE_STORY_INFO_TABLE, null, adsContentValues);
		
	}
	
	public void updateAds(Story adsStory){
		
		int storyPlayed=0;
		
		String updateAdsQuery = "UPDATE " + DATABASE_STORY_INFO_TABLE + " SET " 
		+ STORY_TITLE + " = " + "'" + adsStory.getTitle() + "'" + " , " 
		+ STORY_URL + " = " + "'" + adsStory.getUrl() + "'" + " , "
		+ STORY_PRIMARY_CATEGORY + " = " + adsStory.getPrimaryCategory() + " , " 
		+ STORY_IS_PLAYED + " = " + storyPlayed  + " , "
		+ STORY_ADS_IMAGE_URL + " = " + "'" + adsStory.getAdsImageURL() + "'" + " , " 
		+ STORY_COMPANION_CLICK_THROUGH_URL + " = " + "'" +adsStory.getCompanionClickThroughUrl() + "'" + " , " 
		+ STORY_ADS_SYSTEM_NAME + " = " + "'" +adsStory.getAdsSystemName() + "'" + " , " 
		+ STORY_ADS_DURATION + " = " + "'" +adsStory.getAdsDuration() + "'" + " , " 
		+ STORY_IMPRESSION_TRACKER_URL + " = " + "'" + adsStory.getImpressionTrackerUrl() + "'" + " , " 
		+ STORY_EXPIRATION_TIMESTAMP + " = " + adsStory.getExpirationTimestamp() + " , " 
		+ STORY_UPLOAD_TIMESTAMP + " = " + adsStory.getUploadTimestamp() 
		+ " WHERE " + STORY_TRACK_ID + " = " + adsStory.getTrackID() ;
		
		mDbForWrite.execSQL(updateAdsQuery);
		
	}
	

}
