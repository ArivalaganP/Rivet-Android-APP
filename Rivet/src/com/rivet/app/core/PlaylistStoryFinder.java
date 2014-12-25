package com.rivet.app.core;

import java.util.Date;

import android.content.Context;

import com.rivet.app.BaseActivity;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.common.RConstants;

public class PlaylistStoryFinder extends FinderBase {

	private CategoryDBAdapter databaseForRead;
	Context ctx = null;

	public PlaylistStoryFinder(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ctx = context;
		databaseForRead = CategoryDBAdapter.getCategoryDBAdapterForRead(context);
		// its called from content manager init from splash.so we can not
		// get categories here its not yet build
	}

	public void start() {

		this.categoryArray = databaseForRead.getAllCategories();
		
		BaseActivity bactivity = (BaseActivity) this.ctx;
		Long startTimeseconds = bactivity.prefStore.getLongData(
				RConstants.LAST_REFRESHED_TIME, Long.valueOf(0));

		if (startTimeseconds == 0) {

			this.startTimestamp = null;
		} else {

			this.startTimestamp = new Date(startTimeseconds);
		}

		this.endTimestamp = new Date();

		// passed value 1 for get checked items and 0 for unchecked items
		this.requiredCategories = this.categoryArray;

		if (RConstants.IS_DEMO) {
			this.includeExpired = true;
		} else {
			this.includeExpired = false;
		}

		// tell the story build to start building.
		super.start();
		// this.lastRefreshTime = this.endTimestamp;
		bactivity.prefStore.setLongData(RConstants.LAST_REFRESHED_TIME,
				this.endTimestamp.getTime());
	}

	public void findStory() {

		// TODO: we need to write this later on to find particular story when
		// user search it
	}

}
