package com.rivet.app.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;

import com.rivet.app.abstracts.ManagerBase;
import com.rivet.app.core.pojo.Category;

/**
 * Created by brian on 12/6/14.
 */
public class StoryManager implements ManagerBase {

	List<Object> storyQueue;
	List<Object> nextUpQueue;
	Date startTimestamp;
	Date endTimestamp;
	List<Category> requiredCategories;
	List<Category> filterByCategories;
	List<String> filterByKeywords;
	List<String> filterByCoverage;
	List<String> filterStoryTypes;
	List<String> storyTypes;


	PlaylistStoryFinder playlistStoryFinder;
	String newsKeyword;
	String trafficKeyword;
	ArrayList<Integer> historyIDArray;
	Context ctx;
	private BreakingStoryFinder breakingStoryFinder;

	// Story Manager Datasource

	public StoryManager(Context ctx) {
		this.ctx = ctx;
		this.init();

	}

	public Context getContext(){
			return this.ctx;
		

	}
	public List<String> getFilterByKeywords() {
		return filterByKeywords;
	}

	public void setFilterByKeywords(List<String> filterByKeywords) {
		this.filterByKeywords = filterByKeywords;
	}

	private void init() {
		
				breakingStoryFinder = new BreakingStoryFinder(this.ctx);
		
				playlistStoryFinder = new PlaylistStoryFinder(this.ctx );
		
	
	}

	public void findStoriesFromDate(Date startDate, Date endDate,
			List<Category> forCategories, boolean includingExpired,
			List<String> withKeywords, List<String> withKeywordsCategories,
			List<String> storyTypes) {
	};

	public void startBuilding() {

	
		breakingStoryFinder.start();
		playlistStoryFinder.start();

	}

	public void UpdateBreakingNews() {

		breakingStoryFinder.start();

	}
	
	public void updateStories(){
		playlistStoryFinder.start();
	}

	public void clearExpiredStories() {

	}


}
