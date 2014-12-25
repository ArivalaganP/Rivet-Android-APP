package com.rivet.app.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.rivet.app.common.RConstants;
import com.rivet.app.core.CategoryBuilder;
import com.rivet.app.core.ContentManager;

public class CategoryBuilderObserver implements PropertyChangeListener {

	private ContentManager ctManger;

	public CategoryBuilderObserver(CategoryBuilder categorybuilder,
			ContentManager contentManager) {
		
		this.ctManger = contentManager;
		categorybuilder.addChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		  if(event.getPropertyName().equalsIgnoreCase(RConstants.FINISH_CATEGORIES_BUILDING)){
			  
			  //rule building complete
			  
			  if(this.ctManger.isRuleBuildingComplete() && !this.ctManger.isStoryBuidingStarted()){
			  this.ctManger.startBuildingStories();
			  this.ctManger.setStoryBuidingStarted(true);
			  }
			  this.ctManger.setCategorybuildingComplete(true);
		
		  }else if (event.getPropertyName().equalsIgnoreCase(RConstants.HHR_ERROR_SERVER_IS_DOWN_CATEGORY_BUILDER)){
			  
			  //Rule download not not possible try again.
			  this.ctManger.alertUserAndReset(RConstants.HHR_ERROR_SERVER_IS_DOWN_CATEGORY_BUILDER);
			 
		  }
	  }

	}


