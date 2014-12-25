package com.rivet.app.core;

import com.rivet.app.abstracts.RuleBase;

/**
 * Created by brian on 12/6/14.
 */

public class IPodRule extends RuleBase{

    int numberOfStoriesPlayed;
    int lengthOfStoriesPlayedInMinutes;
    int desiredMinimumLengthOfSongsInMinutes;

    public IPodRule(){
    
    	 this.numberOfStoriesPlayed = 0;
         this.lengthOfStoriesPlayedInMinutes = 0;
         this.desiredMinimumLengthOfSongsInMinutes = 0;
    }
    
    public IPodRule( int numberOfStoriesPlayed , int lengthOfStoriesPlayedInMinutes , int desiredMinimumLengthOfSongsInMinutes){

        this.numberOfStoriesPlayed = numberOfStoriesPlayed;
        this.lengthOfStoriesPlayedInMinutes = lengthOfStoriesPlayedInMinutes;
        this.desiredMinimumLengthOfSongsInMinutes = desiredMinimumLengthOfSongsInMinutes;
    }

	public int getNumberOfStoriesPlayed() {
		return numberOfStoriesPlayed;
	}

	public void setNumberOfStoriesPlayed(int numberOfStoriesPlayed) {
		this.numberOfStoriesPlayed = numberOfStoriesPlayed;
	}

	public int getLengthOfStoriesPlayedInMinutes() {
		return lengthOfStoriesPlayedInMinutes;
	}

	public void setLengthOfStoriesPlayedInMinutes(int lengthOfStoriesPlayedInMinutes) {
		this.lengthOfStoriesPlayedInMinutes = lengthOfStoriesPlayedInMinutes;
	}

	public int getDesiredMinimumLengthOfSongsInMinutes() {
		return desiredMinimumLengthOfSongsInMinutes;
	}

	public void setDesiredMinimumLengthOfSongsInMinutes(
			int desiredMinimumLengthOfSongsInMinutes) {
		this.desiredMinimumLengthOfSongsInMinutes = desiredMinimumLengthOfSongsInMinutes;
	}

    
    
}