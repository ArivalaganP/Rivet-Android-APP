package com.rivet.app.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;

public class UpdateStroiesObserver implements PropertyChangeListener {
	
	private StoryDBAdapter storyDbAdapter;
	private ContentManager ctManager;

	public UpdateStroiesObserver(StoryDBAdapter storyDbAdapter , ContentManager ctManager) {
		this.storyDbAdapter = storyDbAdapter ;
		this.ctManager = ctManager ;
		
		this.storyDbAdapter.addChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		
		if(event.getPropertyName().equalsIgnoreCase(RConstants.NEW_STORIES_ADDED)){
			
			ctManager.setFlagNoStoryTOPlay(false);

		ctManager.playNewStory();
		}
		
	}

}
