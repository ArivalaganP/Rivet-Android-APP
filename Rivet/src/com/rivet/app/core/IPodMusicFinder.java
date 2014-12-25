package com.rivet.app.core;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Story;

public class IPodMusicFinder {
	
	private Context context;
	private ArrayList<Story> ipodList = new ArrayList<Story>();
	private StoryDBAdapter storyDbAdapter;
	String categoryName ;
	private String TAG = "iPodMusicFinder" ;

	public IPodMusicFinder(Context context , StoryDBAdapter storyDbAdapter) {
		this.context = context ;
		this.storyDbAdapter = storyDbAdapter ;
		categoryName = RConstants.MUSIC_FROM_DEVICE;
		
	}
	
	
	
	public void start(){
		
		 String[] projection = {
			        MediaStore.Audio.Media._ID,
			        MediaStore.Audio.Media.ARTIST,
			        MediaStore.Audio.Media.TITLE,
			        MediaStore.Audio.Media.DATA,
			        MediaStore.Audio.Media.DISPLAY_NAME,
			        MediaStore.Audio.Media.DURATION,
			        MediaStore.MediaColumns.DATE_ADDED
			};

		 ContentResolver contentResolver = this.context.getContentResolver();
		 Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			        projection,
			        null,
			        null,
			        null);

	
	if(cursor != null){
	
	if (cursor.moveToFirst()){
		   do{
			   
			   Story story = new Story();
			   
			  story.setTrackID((cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))));
		      story.setAnchorName(( cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))));
		      story.setTitle((cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))));
		      story.setUrl((cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))));
		      story.setProducedBy(( cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))));
		      story.setSource(( cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))));
		      story.setAudioLength((cursor.getFloat(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
		      story.setUploadTimestamp(cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)));
		      story.setExpirationTimestamp(1);
		      story.setCreationTimestamp(System.currentTimeMillis());
		      story.setCategoryName(categoryName);
		      story.setPrimaryCategory(RConstants.IPOD_MUSIC_CATEGORY);
		      story.setIsLead(false);
		      if(story.getUrl()!=null){
		      if(story.getUrl().endsWith(".mp3") && (story.getUrl().contains("Music") || story.getUrl().contains("music"))){
		    	  // add in database 
		    	  ipodList.add(story);
		    	  storyDbAdapter.addStoryInfo(story);
		    	  if(RConstants.BUILD_DEBUBG){
								Log.i(TAG,
										RConstants.SONG_TRACK_ID + ":"
												+ story.getTrackID() + ","
												+ RConstants.SONG_ANCHOR_NAME + ":"
												+ story.getAnchorName() + ","
												+ RConstants.SONG_TITLE_NAME + ":"
												+ story.getTitle() + ","
												+ RConstants.SONG_URL + ":"
												+ story.getUrl() + ","
												+ RConstants.SONG_LENGTH + ":"
												+ story.getAudioLength() + ","
												+ RConstants.SONG_CATEGORY_NAME + ":"
												+ story.getCategoryName());
			      }
			 
		      	}
		      }
		         
		     
		   }while(cursor.moveToNext());
		}
		cursor.close();
		
	}
		
	}	

}
