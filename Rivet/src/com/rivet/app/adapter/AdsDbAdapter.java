package com.rivet.app.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.rivet.app.abstracts.RDBAdapterBase;
import com.rivet.app.core.AdRule;

public class AdsDbAdapter extends RDBAdapterBase {

	public static final String Ads_Id = "adsId";
	public static final String NUMBER_OF_STORIES_PLAYED = "noOfStoriesPlayed";
	public static final String LENGTH_OF_STORIES_PLAYED_IN_MINUTES = "lengthOfStoriesPlayedInMintues";
	public static final String  DESIRED_MINIMUM_ADS_COUNT  = "desiredMinimumAdsCount" ;
	

	private static final String DATABASE_ADS_TABLE = "adsDbAdapter";
	private static AdsDbAdapter adsDbAdapter;

	/************************************** Table categoryInfo ************************************************/
	private static final String DATABASE_ADS_CREATE = "create table if not exists adsDbAdapter(adsId integer "
			+ "primary key autoincrement, noOfStoriesPlayed integer , lengthOfStoriesPlayedInMintues integer ,desiredMinimumAdsCount integer);";

	/****************************************************************************
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context with in which to work
	 *****************************************************************************/
	private AdsDbAdapter(Context ctx) {
		super(ctx , true);

		createTable(DATABASE_ADS_CREATE , true);

	}

	public static AdsDbAdapter getAdsDbAdapter(Context ctx) {

		synchronized (CategoryDBAdapter.class) {

			if (adsDbAdapter == null) {
				adsDbAdapter = new AdsDbAdapter(ctx);
			}
		}
		return adsDbAdapter;

	}

	public void addAdsInDB(AdRule adRule) {

		ContentValues contentValues = new ContentValues();

		contentValues.put(NUMBER_OF_STORIES_PLAYED, adRule.getNoOfStoriesPlayed());
		contentValues.put(LENGTH_OF_STORIES_PLAYED_IN_MINUTES, adRule.getLengthOfStoriesPlayedInMintues());
		contentValues.put(DESIRED_MINIMUM_ADS_COUNT , adRule.getDesiredMinimumAdsCount());
	
		
		mDbForWrite.insert(DATABASE_ADS_TABLE, null, contentValues);

	}
	
	
	
	public int getAdsCount(){
		String query = "select * from " + DATABASE_ADS_TABLE ;
		Cursor cursor = mDbForWrite.rawQuery(query, null);
		
		return  cursor.getCount();
	}
	
	public AdRule getAdsRule(){
		String query = "select * from " + DATABASE_ADS_TABLE ;
		AdRule adRule = new AdRule();
		Cursor cursor = mDbForWrite.rawQuery(query, null);
		if (cursor.moveToFirst()){
			   do{
				   adRule.setNoOfStoriesPlayed(cursor.getInt(cursor.getColumnIndex(NUMBER_OF_STORIES_PLAYED)));
				   adRule.setLengthOfStoriesPlayedInMintues(cursor.getInt(cursor.getColumnIndex(LENGTH_OF_STORIES_PLAYED_IN_MINUTES)));
				   adRule.setDesiredMinimumAdsCount(cursor.getInt(cursor.getColumnIndex(DESIRED_MINIMUM_ADS_COUNT)));
			 
			   }while(cursor.moveToNext());
			}
			cursor.close();
		return adRule;	
	}

}
