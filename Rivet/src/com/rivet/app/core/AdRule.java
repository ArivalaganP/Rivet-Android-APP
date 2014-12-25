package com.rivet.app.core;

import com.rivet.app.abstracts.RuleBase;

/**
 * Created by Brian on 12/6/14.
 */
public  class AdRule extends RuleBase {

    public int noOfStoriesPlayed;
    public int lengthOfStoriesPlayedInMintues;
    public int desiredMinimumAdsCount;
    
    
    public AdRule(){
        
    	this.noOfStoriesPlayed = 0;
        this.lengthOfStoriesPlayedInMintues = 0;
        this.desiredMinimumAdsCount = 0;
        
   }

     public boolean isEqual(AdRule adrule) { return  true;}
     

    public int getNoOfStoriesPlayed() {
		return noOfStoriesPlayed;
	}
	public void setNoOfStoriesPlayed(int noOfStoriesPlayed) {
		this.noOfStoriesPlayed = noOfStoriesPlayed;
	}
	public int getLengthOfStoriesPlayedInMintues() {
		return lengthOfStoriesPlayedInMintues;
	}
	public void setLengthOfStoriesPlayedInMintues(int lengthOfStoriesPlayedInMintues) {
		this.lengthOfStoriesPlayedInMintues = lengthOfStoriesPlayedInMintues;
	}
	public int getDesiredMinimumAdsCount() {
		return desiredMinimumAdsCount;
	}
	public void setDesiredMinimumAdsCount(int desiredMinimumAdsCount) {
		this.desiredMinimumAdsCount = desiredMinimumAdsCount;
	}

}
