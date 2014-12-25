package com.rivet.app.core;

import com.rivet.app.abstracts.RuleBase;

/**
 * Created by brian on 14/6/14.
 */
public class PodcastRule extends RuleBase{

    int storyLimit;
    int maxLength;
    String categoryName;
    int storyCount;
    
    public PodcastRule(){
    	//empty constructor
    }
    
    public PodcastRule(int mStoryLimit, int mMaxLength, String mCategoryName, int mStoryCount){
    	this.storyLimit=mStoryLimit;
    	this.maxLength=mMaxLength;
    	this.categoryName=mCategoryName;
    	this.storyCount=mStoryCount;
    }

    public int getStoryLimit() {
		return storyLimit;
	}

	public void setStoryLimit(int storyLimit) {
		this.storyLimit = storyLimit;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getStoryCount() {
		return storyCount;
	}

	public void setStoryCount(int storyCount) {
		this.storyCount = storyCount;
	}

	public boolean isEqual ( PodcastRule pcrule){
        boolean val=false;

         if((this.maxLength == pcrule.maxLength) &&
                 (this.storyLimit == pcrule.storyLimit) &&
                 (this.storyCount ==pcrule.storyCount) &&
                 this.categoryName.contentEquals(pcrule.getCategoryName())) {  val = true;  return val; }

        return val;

    }
    public void  incrementStoryCount (){ this.storyCount++;}


}
