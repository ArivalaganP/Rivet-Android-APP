package com.rivet.app.core;

import com.rivet.app.abstracts.RuleBase;
import com.rivet.app.core.pojo.Category;

import java.util.List;
import java.util.Set;

/**
 * Created by brian on 12/6/14.
 */
public class PlaylistRule extends RuleBase {

    Set<Category> categories;
    List<Integer> categoryIds;
    String categoryName="";
    int nonLeadStoriesCount;
    int repeatsCount;
    int leadStoriesCount;
    List<String> regions;
    
    public PlaylistRule(){
    	//empty constructor
    }
    
    //constructor to be called from RuleParser
    public PlaylistRule(String categoryName, int nonLeadStoriesCount,
    		int leadStoriesCount, List<String> regions){
    	this.categoryName=categoryName;
    	this.nonLeadStoriesCount=nonLeadStoriesCount;
    	this.leadStoriesCount=leadStoriesCount;
    	this.regions=regions;
    }
    
    
    public void playlistRuleWithCategories(){}
    
	public Set<Category> getCategories() {
		return categories;
	}
	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}
	public List<Integer> getCategoryIds() {
		return categoryIds;
	}
	public void setCategoryIds(List<Integer> categoryIds) {
		this.categoryIds = categoryIds;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getNonLeadStoriesCount() {
		return nonLeadStoriesCount;
	}
	public void setNonLeadStoriesCount(int nonLeadStoriesCount) {
		this.nonLeadStoriesCount = nonLeadStoriesCount;
	}
	public int getRepeatsCount() {
		return repeatsCount;
	}
	public void setRepeatsCount(int repeatsCount) {
		this.repeatsCount = repeatsCount;
	}
	public int getLeadStoriesCount() {
		return leadStoriesCount;
	}
	public void setLeadStoriesCount(int leadStoriesCount) {
		this.leadStoriesCount = leadStoriesCount;
	}
	public List<String> getRegions() {
		return regions;
	}
	public void setRegions(List<String> regions) {
		this.regions = regions;
	}

}




