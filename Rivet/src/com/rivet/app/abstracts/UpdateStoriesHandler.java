package com.rivet.app.abstracts;

import android.util.Log;

import com.rivet.app.common.RConstants;
import com.rivet.app.core.StoryManager;

public class UpdateStoriesHandler implements Runnable {
	
	private StoryManager storyManager;
	private String TAG = "StackOverFlowHandler" ;

	public UpdateStoriesHandler(StoryManager storyManager) {
		// TODO Auto-generated constructor stub
		this.storyManager = storyManager ;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(RConstants.BUILD_DEBUBG)
		{
			Log.i(TAG, RConstants.STACK_OVER_FLOW_FINDER_THREAD);
		}
		storyManager.updateStories();
	}

}
