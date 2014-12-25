package com.rivet.app.core;

import java.util.ArrayList;

import android.content.Context;

import com.rivet.app.abstracts.RuleBase;
import com.rivet.app.adapter.AdsDbAdapter;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.adapter.RulesDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.core.pojo.Story;

/**
 * Created by Brian on 13/6/14.
 */
public class RuleManager  extends  RuleBase{

    int currentRuleIndex;
    RuleBase currentRule;
    int resetRulesCount=0;
    int resetRulesCountForNoStory=-0;
    int currentRuleStoryRepetitionsPassed =0;
    int currentRuleLeadStoryRepetitionsPassed = 0;
    ArrayList<PlaylistRule> playlistRules;
    ArrayList<PodcastRule> podcastRules;
    IPodRule iPodRule;
    AdRule adRule ;

    RulesDBAdapter rulesDBAdapter =null;
    AdsDbAdapter adsDbAdapter = null ;
    CategoryDBAdapter categoryDBAdapterForRead =null;
    boolean podcastRuleEnable=false;
	public RuleManager(Context ctx){
    	
    	rulesDBAdapter = RulesDBAdapter.getRulesDBAdapter(ctx);
    	categoryDBAdapterForRead = CategoryDBAdapter.getCategoryDBAdapterForRead(ctx);
    	adsDbAdapter = AdsDbAdapter.getAdsDbAdapter(ctx);
    }

	 public int getResetRulesCountForNoStory() {
			return resetRulesCountForNoStory;
		}
	
	public void setResetRulesCountForNoStory(int resetRulesCountForNoStory) {
		this.resetRulesCountForNoStory = resetRulesCountForNoStory;
	}
	
    public int getResetRulesCount() {
		return resetRulesCount;
	}

	public boolean isPodcastRuleEnable() {
		return podcastRuleEnable;
	}

	public void setPodcastRuleEnable(boolean podcastRuleEnable) {
		this.podcastRuleEnable = podcastRuleEnable;
	}

	public void setResetRulesCount(int resetRulesCount) {
		this.resetRulesCount = resetRulesCount;
	}


    public int getCurrentRuleIndex() {
        return this.currentRuleIndex;
    }

    public void setCurrentRuleIndex(int currentRuleIndex) {
        this.currentRuleIndex = currentRuleIndex;
    }

    public RuleBase getCurrentRule() {
    	
    	return this.currentRule;
    }

    public void setCurrentRule(RuleBase currentRule) {
        this.currentRule = currentRule;
    }
    
    public boolean isStoryCountMatchiPodRule(int storyCount)
    {
    	boolean value = false;
    	if(storyCount  == iPodRule.getNumberOfStoriesPlayed() && 
      		  (categoryDBAdapterForRead.categoryExistInMyCategoryById(new Category(RConstants.IPOD_MUSIC_CATEGORY,null))!=0)){
    		value = true;
    	}
    	
    	return value;
    	
    }
    
    public boolean isStoryCountMatchAdvertisementRule(int storyCount){
    	boolean value = false ;
    	if(storyCount == adRule.getNoOfStoriesPlayed()){
    		value = true ;
    	}
    	
    	return value ;
    }
    
  
    
    public RuleBase getNextRule(){
    	
    	removeCurrentRule(); 
    	
    	
    	if(podcastRuleEnable){
    		//return the podcast rule
    		if(podcastRules != null){
	    		if(podcastRules.size()>0){
		    		this.currentRule = podcastRules.get(0);
		    		return podcastRules.get(0);
	    		}else{
	    			return null;
	    		}
    		}
    	}
    	
    	//check if we have next rule in playlist 
    	if(playlistRules != null){
    		if(playlistRules.size()>0){
    			this.currentRule = playlistRules.get(0);
    			return playlistRules.get(0);
    		}
    	}
    	
    	
    	//we dont have any rules we need to reset rules
    	return null;
    	
    	
    }
    
    private void removeCurrentRule(){
    	
    	
    		if(podcastRuleEnable){
    			if(podcastRules != null){
	        		if (podcastRules.size()>0){
	            		
	            		podcastRules.remove(0);
	            	}
    			}
        	}else{
        	
        		if(playlistRules != null){
	    	    	if(playlistRules.size() >0 )
	    	    	{
	    	    		playlistRules.remove(0);
	    	    		
	    	    	}
        		}
        	}
    	
    }
    
    public void resetRules(){
    	//TODO: we need to get new stories. 
    	//get the rules again.
    	this.playlistRules = rulesDBAdapter.getAllPlayListRules();
    	this.podcastRules = rulesDBAdapter.getAllPodCastRules();
    	this.iPodRule = rulesDBAdapter.getIPodRule();
    	this.adRule = adsDbAdapter.getAdsRule();
    	
    	if(!podcastRuleEnable){
    		
    		if(playlistRules!= null){
	    		if(playlistRules.size()>0){
	        		this.currentRule = playlistRules.get(0);
	        	}
	    	}
    		
    		
    	}else{
    		
    		if(podcastRuleEnable){
        		if (podcastRules.size()>0){
            		
        			this.currentRule = podcastRules.get(0);
            		
            	}
        	}
	    	
    	}
    	resetRulesCount++;
    	resetRulesCountForNoStory++;
    }
    
   
    public void finishedSuccessfullyDownloadRules ( /*rule dict*/) {

        playlistRules = null;

        if (playlistRules.size() > 0) {

            this.currentRule = playlistRules.get(0);

        }

        this.resetRulesCounter();

    }



    public void setRuleForStory(Story story) {

        if(currentRuleIndex < playlistRules.size()) {
            this.incrementRuleForStory(story);


        }
    }

    public  void resetRulesCounter() {
        if (playlistRules.size() > 0 )
        {
            this.currentRule = playlistRules.get(0);
            currentRuleIndex = 0;
            currentRuleStoryRepetitionsPassed =0;
            currentRuleLeadStoryRepetitionsPassed =0;
           this.resetPodcastCounter();
        }
    }

    public int incrementRuleForStory (Story applicableStory) {

        PlaylistRule selectedRule = (PlaylistRule)this.currentRule;

        int rulesPassed = 0;
        if (applicableStory !=null)
        {
            //since we have only one category per rule if this attempt did not find any applicable story then all consecutive attempts for the same rule category will fail
            //in order to speed things up this rule should be simply skipped
            if (currentRuleLeadStoryRepetitionsPassed < selectedRule.leadStoriesCount)
            {
                currentRuleLeadStoryRepetitionsPassed = selectedRule.leadStoriesCount;
                if (selectedRule.repeatsCount ==0)
                {
                    rulesPassed = this.incrementCurrentRule();
                }
            }
            else
            {
                rulesPassed = this.incrementCurrentRule();
        
            }
        } else {
            if (currentRuleLeadStoryRepetitionsPassed < selectedRule.leadStoriesCount) {
                currentRuleLeadStoryRepetitionsPassed = currentRuleLeadStoryRepetitionsPassed + 1;
                if (selectedRule.repeatsCount == 0) {
                    rulesPassed = this.incrementCurrentRule();
                }
            } else {
                currentRuleStoryRepetitionsPassed = currentRuleStoryRepetitionsPassed + 1;
                int additionalTrackCountFromMoreOftenSetting = 0;
                if (selectedRule.categories.size() > 0) {
              
                }
                if (currentRuleStoryRepetitionsPassed >= (selectedRule.repeatsCount + additionalTrackCountFromMoreOftenSetting)) {
                    //TODO: need to see this addtionalTrackCountFromMoreOftenSetting
                    rulesPassed = incrementCurrentRule();
                  
                }
            }
        }

        return rulesPassed;


    }

    public void resetPodcastCounter ()
    {
        for( int idx =0 ; idx < podcastRules.size() ; idx++){

            podcastRules.get(idx).storyCount = 0;
        }
    }

    public int incrementCurrentRule(){

        currentRuleLeadStoryRepetitionsPassed = 0;
        currentRuleStoryRepetitionsPassed =0;
        if (playlistRules.size()>0)
        {
            currentRuleIndex = (currentRuleIndex +1) ; 
            this.currentRule = playlistRules.get(currentRuleIndex);

            this.resetPodcastCounter();
        }
        else
        {
            this.resetRulesCounter();
        }


        return 1;

    }

}