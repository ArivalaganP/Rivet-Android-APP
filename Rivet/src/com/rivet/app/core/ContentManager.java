package com.rivet.app.core;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.rivet.app.BaseActivity;
import com.rivet.app.abstracts.ManagerBase;
import com.rivet.app.abstracts.RefreshHandler;
import com.rivet.app.abstracts.RuleBase;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.observer.AdsBuilderObserver;
import com.rivet.app.observer.CategoryBuilderObserver;
import com.rivet.app.observer.RuleBuilderObserver;
import com.rivet.app.observer.StroiesLoadToDBObserver;
import com.rivet.app.observer.UpdateStroiesObserver;

public class ContentManager implements ManagerBase {

	private static ContentManager contentManager;
	boolean isFirstTime = true;
	Context ctx;
	CategoryBuilder categoryBuilder;
	CategoryManager categoryManager;
	StoryBuilder storyBuilder;
	StoryManager storyManager;
	RuleBuilder ruleBuilder;
	RuleManager ruleManager;
	PlaylistManager playlistManager;
	boolean isReadyToPlay = false;
	boolean categorybuildingComplete = false;
	boolean ruleBuildingComplete = false;
	boolean storyBuidingStarted = false;
	boolean updatePlayListComplete=false;
	boolean storyLoadTODBComplete=false;
	int retryCount = 0;
	CategoryDBAdapter categoyDBAdapter=null;
	int isNetworkError=0;
	boolean noStoriesToPlay=false;
	int storySwitchCount=0;
	Long lastRefreshedTime;
	RuleBuilderObserver ruleBuilderObserver = null;
	StroiesLoadToDBObserver stroiesLoadToDBObserver=null;
	CategoryBuilderObserver categoryRuleBuiderObserver = null;
	UpdateStroiesObserver updateStoriesObserver = null ;
	
	public boolean isStoryLoadTODBComplete() {
		return storyLoadTODBComplete;
	}

	public void setStoryLoadTODBComplete(boolean storyLoadTODBComplete) {
		this.storyLoadTODBComplete = storyLoadTODBComplete;
	}

	AdsBuilderObserver adsBuilderObserver = null ;
	
	private String TAG="ContentManager";
	private AdsBuilder adsBuilder;
	 
	public int getIsNetworkError() {
		return isNetworkError;
	}

	public void setIsNetworkError(int isNetworkError) {
		this.isNetworkError = isNetworkError;
	}

	public static ContentManager getContentManager(Context context) {

		if (contentManager == null)
			contentManager = new ContentManager(context);

		return contentManager;
	}
	
	public static ContentManager getContentManager() {
		
		return contentManager;
	}
	public void resetContentManager(){
		
		 isReadyToPlay = false;
		 categorybuildingComplete = false;
		 ruleBuildingComplete = false;
		 storyBuidingStarted = false;
		 updatePlayListComplete=false;
		 contentManager = null ;
		
	}
	
	
	private ContentManager(Context ctx) {
		this.ctx = ctx;
		this.categoryBuilder = new CategoryBuilder(this.ctx);
		this.categoryManager = new CategoryManager();

		this.storyBuilder = new StoryBuilder(this.ctx);
		this.storyManager = new StoryManager(this.ctx);

		this.ruleBuilder = new RuleBuilder(this.ctx);
		this.adsBuilder = new AdsBuilder(this.ctx);
		this.categoryRuleBuiderObserver = new CategoryBuilderObserver(categoryBuilder, this);
		this.ruleBuilderObserver = new RuleBuilderObserver(ruleBuilder, this);
		this.updateStoriesObserver = new UpdateStroiesObserver(StoryDBAdapter.getStoryDBAdapter(this.ctx), this);
		this.adsBuilderObserver = new AdsBuilderObserver(adsBuilder, this);
		
		this.ruleManager = new RuleManager(this.ctx);

		this.playlistManager = new PlaylistManager(ctx,ruleManager.getCurrentRule());
		
		categoyDBAdapter =   CategoryDBAdapter.getCategoryDBAdapterForRead(ctx);
		
		// start timer to get breaking news after each 10 minutes
		BreakingNewsAndUpdateStoriesTimer timerTask = new BreakingNewsAndUpdateStoriesTimer();
		Timer timer = new Timer();
		timer.schedule(timerTask, RConstants.UPDATE_STORIES_TIME_INTERVAL , RConstants.UPDATE_STORIES_TIME_INTERVAL);
		
		isFirstTime = true;

	}

	public boolean isCategorybuildingComplete() {
		return categorybuildingComplete;
	}

	public void setCategorybuildingComplete(boolean categorybuildingComplete) {
		this.categorybuildingComplete = categorybuildingComplete;
	}

	public boolean isRuleBuildingComplete() {
		return ruleBuildingComplete;
	}

	public void setRuleBuildingComplete(boolean ruleBuildingComplete) {
		this.ruleBuildingComplete = ruleBuildingComplete;
	}

	public boolean isStoryBuidingStarted() {
		return storyBuidingStarted;
	}

	public void setStoryBuidingStarted(boolean storyBuidingStarted) {
		this.storyBuidingStarted = storyBuidingStarted;
	}

	public boolean isUpdatePlayListComplete() {
		return updatePlayListComplete;
	}

	public void setUpdatePlayListComplete(boolean updatePlayListComplete) {
		this.updatePlayListComplete = updatePlayListComplete;
	}

	public void initiate() {

		// after successfully saving region we need to start the category build
		categoryBuilder.start();

		// handle verify error if throws by rule builder
		// TODO : change here in upcoming build if verify error comes on flurry in 1.35
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				for(;;){
				try {
					if(ruleBuilder == null){
						ruleBuilder = new RuleBuilder(ctx);
					}
					ruleBuilder.start();
					break;
				} catch (VerifyError verifyError) {
					
					verifyError.printStackTrace();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						
						e.printStackTrace();
						
					}
				}
				}
				
			}
		}).start();
	
		playlistManager.keywordBuilder.start(false);
		

	}

	public void alertUserAndReset(String errorCode) {

		if (errorCode.equalsIgnoreCase(RConstants.HHR_ERROR_SERVER_IS_DOWN_RULE_BUILDER)) {

			if (retryCount <= RConstants.RETRY_COUNT) {
				// we have to give alret to user also
				ruleBuilder.start();
				showToast("Server might be down. Retrying ...");
				retryCount++;
				
			} else {

				showToast("Sorry there is a problem connecting to Rivet, Please quit app and run again.");
				
			}

		}else if(errorCode.equalsIgnoreCase(RConstants.HHR_ERROR_SERVER_IS_DOWN_CATEGORY_BUILDER)){
			if (retryCount <= RConstants.RETRY_COUNT) {
				// we have to give alret to user also
				categoryBuilder.start();
				showToast("Sorry there is a problem connecting to Rivet. Retrying ...");
				retryCount++;
				
			} else {

				showToast("Sorry there is a problem connecting to Rivet, Please quit app and run again.");
				
			}
			
		}

	}

	public PlaylistManager getPlaylistManager() {

		return this.playlistManager;
	}
	
	// TODO : call this method when NoStoriesToPlay goes true 
	public void startBuildingStories() {

		this.isReadyToPlay = false;
		
		StoryBuilderThread builderThread = new StoryBuilderThread();
		builderThread.start();

		try {
			builderThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// get the rules from db and put in rulesManager
		//for first tiem we need to wait till mycategories table is build by user.
		
		//for next launch storybuild might take time and observer send notificate late and home load first and 
		//so it runs updatePlaylistforFirstime already so it will reset rules 
		if((categoyDBAdapter.getMyCategoriesCount()) > 1 && (!updatePlayListComplete) && (RConstants.STORY_UPLOAD_TO_DB)){
			 //this is for first time flow when user can stop at welcome , or one time category builder so this might skip here.
			 setUpdatePlayListComplete(true);
			 ruleManager.resetRules();
			 updatePlayListForCurrentRule();
			 //set the updatePlylsit Flag so home when load does not build it again.
		
			 
		 }

	}

	public void updatePlyListforFirstTime(){
		//this is called one time from addcategory build one time activity after we have the mycategories build
		setUpdatePlayListComplete(true);
		ruleManager.resetRules();
		updatePlayListForCurrentRule();
		
		
	}
	
	public void moveToNextRuleAndUpdateStoriesForCurrentRule() {
		
		isReadyToPlay = false;
		
		if(ruleManager == null){
			 this.ruleManager = new RuleManager(this.ctx);
		}
		RuleBase ruleBase = ruleManager.getNextRule();
		//reset the podcast rule
		
		if (ruleBase == null) 
		{
			ruleManager.setPodcastRuleEnable(false);
			
			if( storySwitchCount >= 3 && playlistManager.getNoPodCastFound() < 1 && playlistManager.isNoStoryFound()<=1){
				//we need to avoid the stack over flow. 
				//set flag noStories to play 
				setFlagNoStoryTOPlay(true);
				storySwitchCount=0;
				ruleManager.setResetRulesCount(0);
				playlistManager.setNoStoryFound(0);
				playlistManager.setNoPodCastFound(0);
				
			}
			
			if(!noStoriesToPlay)
			{
				// we have exhausted the rules so we need to call reset rules
				if(ruleManager.getResetRulesCount() >=2 && playlistManager.isNoStoryFound()<=1){
					//it means we hav iterrated through all the rules and no story found
					//so we need to switch to podcast rule
					ruleManager.setPodcastRuleEnable(true);
					storySwitchCount++;
					ruleManager.setResetRulesCount(0);
						
				}
				
				playlistManager.setNoStoryFound(0);
				playlistManager.setNoPodCastFound(0);
				ruleManager.resetRules();
				ruleBase = ruleManager.getCurrentRule();
				
			}
		}//rule base null
		
		
		if(!noStoriesToPlay)
		{
			//check if no stories to play flag is on then we are not going to update playlist now
			ruleManager.setCurrentRule(ruleBase);
			updatePlayListForCurrentRule();
		}
				
	}

	public void updatePlayListForCurrentRule() {

		if (!playlistManager.BuildPlaylistByCurrentRule(ruleManager.getCurrentRule())) {

			// story building has failed so we do have story for current rule
			moveToNextRuleAndUpdateStoriesForCurrentRule();

		}
		if( playlistManager.getPlaylistCount()>0){
			isReadyToPlay = true;
			setFlagNoStoryTOPlay(false);
		}

	}
	
	public void playNewStory(){
	
		// TODO : validate here for stackOver flow
		
		if(RConstants.BUILD_DEBUBG && this.playlistManager.getCurrentStory()!=null){
			Log.i(TAG, "RIVET_LOG : story Title: "+ this.playlistManager.getCurrentStory().getTitle()+"TrackID: "+ this.playlistManager.getCurrentStory().getTrackID());
		}
		
		
		if ( ruleManager.isStoryCountMatchiPodRule(playlistManager.getStoryPlayedCountForIPodeStory())){
			playlistManager.doBuildStoryListforIPodMusicRule();
		}
		
		if(ruleManager.isStoryCountMatchAdvertisementRule(playlistManager.getStoryPlayedCountForAdvertisement())){
			playlistManager.doBuildStoryListForAdsRule();
			
			// download next ads to play for next time 
			if(adsBuilder == null){
				adsBuilder = new AdsBuilder(this.ctx);
			}
			adsBuilder.start();
		}
		
		
		this.playlistManager.next();
		
		
	}
	
	public void playFirstStory()
	{
			//we have already checked the playlist is >=2 so we should come here 
			if(this.playlistManager.getPlaylistCount()>0){
				this.playlistManager.start();
			}else{
				updatePlayListForCurrentRule();
			}
		
		startTimerNotToShowAdsBanner();
	
	}
	
	private void startTimerNotToShowAdsBanner() {
		
		// start timer to not show add banner if its not advertisement story
		DontShowAdBannerTimer dontShowAddBannerTimer = new DontShowAdBannerTimer();
		Timer adtimer = new Timer();
		adtimer.schedule(dontShowAddBannerTimer, 1000, 1000);
		
	}

	public void rewindStoryAndPlay(int seekpos){
		
		this.playlistManager.rewindTrack(seekpos);
	}

	public boolean hasDownloadedCategories() {

		if (categoryManager.getCategoryArray().size() > 0) {
			return true;

		}
		return false;
	}

	public boolean isReadyToPlay() {
		return isReadyToPlay;
	}

	public void setReadyToPlay(boolean isReadyToPlay) {
		this.isReadyToPlay = isReadyToPlay;
	}

	public class StoryBuilderThread extends Thread {
		public void run() {

			storyManager.startBuilding();
		}

	}
	
	
	// this flag will handle stackOverflowException when no story to play in Database
	
	public void setFlagNoStoryTOPlay(boolean flag){
		noStoriesToPlay = flag ;
	}
	
	public boolean getFlagNoStoryToPlay(){
		
		return noStoriesToPlay ;
	}
	
	public void showToast ( final String message){
		
		((BaseActivity)ctx).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(ctx, message, duration);
				toast.show();
				
			}
		});
	}
	
	class DontShowAdBannerTimer extends TimerTask {

		@Override
		public void run() {
			
			if(getPlaylistManager().getCurrentStory() != null){
			if(getPlaylistManager().getCurrentStory().getPrimaryCategory() != RConstants.ADS_CATEGORY){
			Intent dontShowAddIntent = new Intent();
			dontShowAddIntent.setAction(RConstants.DONT_SHOW_AD_BANNER);	
			ctx.sendBroadcast(dontShowAddIntent);
				}
			}
		}
		
	}

	class BreakingNewsAndUpdateStoriesTimer extends TimerTask {

		@Override
		public void run() {
			
			RefreshHandler handler = new RefreshHandler(storyManager);
			Thread UpdateStoriesThread = new Thread(handler);
			UpdateStoriesThread.start();

		
				}
			
			
		}
	}


