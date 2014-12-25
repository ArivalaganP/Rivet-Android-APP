package com.rivet.app.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.RuleBuilder;


public class RuleBuilderObserver implements PropertyChangeListener {
   ContentManager ctManger;
   
   
  public RuleBuilderObserver(RuleBuilder rulebuilder,ContentManager ctx) {
	this.ctManger = ctx;  
	
    rulebuilder.addChangeListener(this);
  }


  @Override
  public void propertyChange(PropertyChangeEvent event) {
	  
	  if(event.getPropertyName().equalsIgnoreCase(RConstants.FINISH_RULE_BUILDING)){
		  //rule building complete
		 
		  if(this.ctManger.isCategorybuildingComplete() && !this.ctManger.isStoryBuidingStarted()){
		  this.ctManger.startBuildingStories();
		  this.ctManger.setStoryBuidingStarted(true);
		  }
		  this.ctManger.setRuleBuildingComplete(true);
	  
	  }else if (event.getPropertyName().equalsIgnoreCase(RConstants.HHR_ERROR_SERVER_IS_DOWN_RULE_BUILDER)){
		  
		  //Rule download not not possible try again.
		  this.ctManger.alertUserAndReset(RConstants.HHR_ERROR_SERVER_IS_DOWN_RULE_BUILDER);
		 
	  }
  }
} 
  