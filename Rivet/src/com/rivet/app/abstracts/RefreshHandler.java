package com.rivet.app.abstracts;

import java.util.ArrayList;

import android.util.Log;

import com.rivet.app.common.RConstants;
import com.rivet.app.core.StoryManager;

/**
 * Created by brian on 12/6/14.
 */

public class RefreshHandler implements Runnable {
	
	private StoryManager storyManager;
	ArrayList<String> keywordList=null;
	
	private static final String TAG="RefreshHanlder";

	public RefreshHandler(StoryManager storyManager) {
		// TODO Auto-generated constructor stub
		this.storyManager = storyManager ;
		keywordList = new ArrayList<String>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(RConstants.BUILD_DEBUBG)
		{
			Log.i(TAG, RConstants.START_BREAKING_NEWS_FINDER);
		}
	
		storyManager.UpdateBreakingNews();
		storyManager.updateStories();
	}

	
	
	
}
