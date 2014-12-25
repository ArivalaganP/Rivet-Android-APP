package com.rivet.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.rivet.app.abstracts.RDBAdapterBase;
import com.rivet.app.core.IPodRule;
import com.rivet.app.core.PlaylistRule;
import com.rivet.app.core.PodcastRule;

public class RulesDBAdapter extends RDBAdapterBase {

	/************************************** Table playlistrules ************************************************/
	public static final String R_ID = "rid";
	public static final String RULE_CATEGORY_NAME = "categoryName";
	public static final String NON_LEAD_STORIES_COUNT = "nonLeadStoriesCount";
	public static final String REPEATS_COUNT = "repeatsCount";
	public static final String LEAD_STORIES_COUNT = "leadStoriesCount";
	public static final String REGIONS = "regions";
	public static final String RESTARTS_APP_TIME = "restartAppTime" ;

	private static final String TABLE_PLAYLIST_RULES = "playlistrules";

	private static final String DATABASE_PLAYLIST_RULES_CREATE = " create table if not exists playlistrules(rid integer "
			+ "primary key autoincrement, categoryName text , restartAppTime integer  , nonLeadStoriesCount integer, repeatsCount integer, leadStoriesCount integer, regions text);";

	
	/***************************************Table podCastrules ***************************************************/
	//# Podcast,Category, # Podcasts per playlist rule, # Max length of Podcast in minutes
	public static final String PC_ID = "pcid";
	public static final String PC_RULE_CATEGORY_NAME = "categoryName";
	public static final String PC_STORYCOUNT = "storyCount";
	public static final String PC_MAXLENGTH = "maxLength";
	
	private static final String TABLE_PODCAST_RULES = "podcastlistrules";

	private static final String DATABASE_PODCAST_RULES_CREATE = " create table if not exists podcastlistrules (pcid integer "
			+ "primary key autoincrement, categoryName text , storyCount integer, maxLength integer);";


	
	/****************************************Table iPodRule*******************************************************/
	public static final String IPOD_ID = "ipid";
	
	public static final String IPOD_STORYCOUNT = "ipStoryCount";
	public static final String IPOD_STORIES_PLAYED_IN_MINTUES = "ipStoryPlayedInMinutes";
	public static final String IPOD_DESIRED_MUSIC_LENGTH_TO_PLAY_IN_MIN = "ipDesiredMusicLengthToPlay";
	
	private static final String TABLE_IPOD_RULES = "ipodlistrules";

	private static final String DATABASE_IPOD_RULES_CREATE = " create table if not exists ipodlistrules (ipid integer "
			+ "primary key autoincrement, ipStoryCount integer, ipStoryPlayedInMinutes integer , ipDesiredMusicLengthToPlay integer);";


	
	
	private static RulesDBAdapter mRulesDBAdapter = null;

	private RulesDBAdapter(Context ctx) {
		super(ctx , true);
		createTable(DATABASE_PLAYLIST_RULES_CREATE , true );
		createTable(DATABASE_PODCAST_RULES_CREATE , true );
		createTable(DATABASE_IPOD_RULES_CREATE , true );

	}

	public static RulesDBAdapter getRulesDBAdapter(Context ctx) {

		synchronized (RulesDBAdapter.class) {
			if (mRulesDBAdapter == null) {
				mRulesDBAdapter = new RulesDBAdapter(ctx);
			}
		}
		return mRulesDBAdapter;
	}

	// methods for playListRule
	public void addPlayListRule(PlaylistRule rulePlayList) {

		//int id = playListRuleExistOrNot(rulePlayList);
		//if (id > 0)
		//	return;
		//add all the rules categories can repeate in the rule file.
		ContentValues values = new ContentValues();
		values.put(RULE_CATEGORY_NAME, rulePlayList.getCategoryName());
		values.put(NON_LEAD_STORIES_COUNT,rulePlayList.getNonLeadStoriesCount());
		values.put(REPEATS_COUNT, rulePlayList.getRepeatsCount());
		values.put(LEAD_STORIES_COUNT, rulePlayList.getLeadStoriesCount());

		// enter regions with comma seperator
		List<String> regionsList = rulePlayList.getRegions();
		String regions = ListToCommaSeperatedString(regionsList);
		values.put(REGIONS, regions);

		mDbForWrite.insert(TABLE_PLAYLIST_RULES, null, values);
	}
	

	public ArrayList<PlaylistRule> getAllPlayListRules() {
		String query = "select * from " + TABLE_PLAYLIST_RULES+" ORDER BY "+R_ID+" ASC";

		Cursor cursor =  mDbForWrite.rawQuery(query, null);
		
		ArrayList<PlaylistRule> ruleList = new ArrayList<PlaylistRule>();
		if (cursor.moveToFirst()) {
			do {
				PlaylistRule rb= new PlaylistRule();
				rb.setLeadStoriesCount(cursor.getInt(cursor.getColumnIndex(LEAD_STORIES_COUNT)));
				rb.setNonLeadStoriesCount(cursor.getInt(cursor.getColumnIndex(NON_LEAD_STORIES_COUNT)));
				rb.setCategoryName(cursor.getString(cursor.getColumnIndex(RULE_CATEGORY_NAME)));
				rb.setRepeatsCount(cursor.getInt(cursor.getColumnIndex(REPEATS_COUNT)));
				
				ruleList.add(rb);
			} while (cursor.moveToNext());
		}
		
		return ruleList;
	}
	
	public void resetRulesTable(){
		
		String playquery = "delete  from " + TABLE_PLAYLIST_RULES;

		mDbForWrite.execSQL(playquery);
		
		String podquery = "delete  from " + TABLE_PODCAST_RULES;

		mDbForWrite.execSQL(podquery);
		
		
	}
	
	public void addPodcastRule(PodcastRule podCastRule) {
			//add all the podcast rules whihc are in rule file
			ContentValues values = new ContentValues();
			values.put(PC_RULE_CATEGORY_NAME, podCastRule.getCategoryName());
			values.put(PC_STORYCOUNT,podCastRule.getStoryCount());
			values.put(PC_MAXLENGTH, podCastRule.getMaxLength());
			mDbForWrite.insert(TABLE_PODCAST_RULES, null, values);
	}
	@SuppressWarnings("unused")
	private int PodCastRuleExistOrNot(PodcastRule podCastRule) {
		String query = "select * from " + TABLE_PODCAST_RULES + " where "
				+ PC_RULE_CATEGORY_NAME + " = " + "'"
				+ podCastRule.getCategoryName() + "'";
		Cursor cursor = mDbForWrite.rawQuery(query, null);
		int Id = 0;
		if (cursor.moveToFirst()) {
			do {
				Id = cursor.getInt(cursor.getColumnIndex(PC_ID));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return Id;
	}
	
	
	public ArrayList<PodcastRule> getAllPodCastRules() {
		String query = "select * from " + TABLE_PODCAST_RULES+" ORDER BY "+PC_ID+" ASC";

		Cursor cursor =  mDbForWrite.rawQuery(query, null);
		
		ArrayList<PodcastRule> ruleList = new ArrayList<PodcastRule>();
		if (cursor.moveToFirst()) {
			do {
				PodcastRule rb= new PodcastRule();
				rb.setStoryCount(cursor.getInt(cursor.getColumnIndex(PC_STORYCOUNT)));
				rb.setMaxLength(cursor.getInt(cursor.getColumnIndex(PC_MAXLENGTH)));
				rb.setCategoryName(cursor.getString(cursor.getColumnIndex(PC_RULE_CATEGORY_NAME)));
				ruleList.add(rb);
			} while (cursor.moveToNext());
		}
		
		return ruleList;
	}

	
	public void addIPodRule(IPodRule ipodRule){
		//add all the iPOd rules whihc are in rule file
		ContentValues values = new ContentValues();
		values.put(IPOD_STORYCOUNT, ipodRule.getNumberOfStoriesPlayed());
		values.put(IPOD_STORIES_PLAYED_IN_MINTUES,ipodRule.getLengthOfStoriesPlayedInMinutes());
		values.put(IPOD_DESIRED_MUSIC_LENGTH_TO_PLAY_IN_MIN, ipodRule.getDesiredMinimumLengthOfSongsInMinutes());
		mDbForWrite.insert(TABLE_IPOD_RULES, null, values);
		
	}
	
	public IPodRule getIPodRule() {
		String query = "select * from " + TABLE_IPOD_RULES+" ORDER BY "+IPOD_ID+" ASC";

		Cursor cursor =  mDbForWrite.rawQuery(query, null);
		
		IPodRule iPodRule = new IPodRule();
		if (cursor.moveToFirst()) {
			do {
				
				iPodRule.setNumberOfStoriesPlayed(cursor.getInt(cursor.getColumnIndex(IPOD_STORYCOUNT)));
				iPodRule.setLengthOfStoriesPlayedInMinutes(cursor.getInt(cursor.getColumnIndex(IPOD_STORIES_PLAYED_IN_MINTUES)));
				iPodRule.setDesiredMinimumLengthOfSongsInMinutes(cursor.getInt(cursor.getColumnIndex(IPOD_DESIRED_MUSIC_LENGTH_TO_PLAY_IN_MIN)));
				
			} while (cursor.moveToNext());
		}
		
		return iPodRule;
	}
	
	
}
