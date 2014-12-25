package com.rivet.app.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.StoryBuilder;

public class StroiesLoadToDBObserver implements PropertyChangeListener {
	
	private ContentManager ctManager;

	public StroiesLoadToDBObserver(StoryBuilder storyBuilder , ContentManager ctManager) {
		this.ctManager = ctManager ;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		if(event.getPropertyName().equalsIgnoreCase(RConstants.FINISH_STORY_LOAD_TO_DB)){
			
			ctManager.setStoryLoadTODBComplete(true);
		}
		
	}

}
