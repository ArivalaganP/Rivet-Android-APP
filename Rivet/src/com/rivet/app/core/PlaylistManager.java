package com.rivet.app.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.os.ResultReceiver;
import android.util.Log;

import com.rivet.app.BaseActivity;
import com.rivet.app.abstracts.ManagerBase;
import com.rivet.app.abstracts.RuleBase;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.core.pojo.RewindAudio;
import com.rivet.app.core.pojo.Story;
import com.rivet.app.services.AudioService;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

public class PlaylistManager implements ManagerBase {

	Story currentStory;
	String TAG = "PlaylistManager";
	AudioService audioService=null;

	ArrayList<Story> playlistArray;
	ArrayList<String>adlistArray;
	private StoryDBAdapter sotryDbAdapter;
	private CategoryDBAdapter categoryDBAdapterForRead;
	private Context context;
	public String downloadUrl = "";
	private boolean isPlaying = false;
	ArrayList<String> audioQueue=null;
	RuleBase currentRule = null;
	private boolean isStreamReadyToPlay=false;
	BaseActivity baseAcvity ;
	KeywordBuilder keywordBuilder=null;
	
	// queue for rewind
	private ArrayList<RewindAudio> rewindListArray = null;

	private GetUrlByTrackIdHandler getUrlByTrackIdHandler;

	private ResultReceiver resultReciever;
	private String categoryName;
	private int rewindIndx =0;
	private int rewindCount =0 ;
	private boolean isInRewindQueue = false ;
	private int  lastTrackPlayedID = 0;
	private int storyPlayedCountForIPodeMusic=0;
	private int storyPlayedCountForAds=0;
	private int noStoryFound=0;
	private int noPodCastFound=0;
	private boolean recentlyLastRewindStoryPlayed = false ;

	public KeywordBuilder getKeyWordBuilder(){
	return this.keywordBuilder;
	}
	
	public int getNoPodCastFound() {
		return noPodCastFound;
	}
	public void setNoPodCastFound(int noPodCastFound) {
		this.noPodCastFound = noPodCastFound;
	}
	public int isNoStoryFound() {
		return noStoryFound;
	}
	public void setNoStoryFound(int noStoryFound) {
		this.noStoryFound = noStoryFound;
	}
	public int getStoryPlayedCountForIPodeStory() {
		return storyPlayedCountForIPodeMusic;
	}
	public void setStoryPlayedCountForIPodeStory(int storyPlayedCountForIpodeMusic) {
		this.storyPlayedCountForIPodeMusic = storyPlayedCountForIpodeMusic;
	}
	
	public int getStoryPlayedCountForAdvertisement() {
		return storyPlayedCountForAds;
	}
	public void setStoryPlayedCountForAdvertisement(int storyPlayedCountForAds) {
		this.storyPlayedCountForAds = storyPlayedCountForAds;
	}
	
	public int getLastTrackPlayedID() {
		return lastTrackPlayedID;
	}
	public void setLastTrackPlayedID(int lastTrackPlayedID) {
		this.lastTrackPlayedID = lastTrackPlayedID;
	}
	
	
	
	public String getCategoryName() {
	
			String catName = categoryDBAdapterForRead.getCategoryNameByCategoryId(getCategoryID());
		return catName;
		
	}
	
	public Story getNextStory(){
		
		Story nextStory = null;
		
		if(playlistArray.size() >= 1){
			nextStory = playlistArray.get(0);
		}
		return nextStory;
		
	}
	
	public ArrayList<Story> getWholeQueueOfStories(){
		
		return playlistArray;
	}
	
	public int getCategoryID() {
			
		int catID = this.currentStory.getPrimaryCategory();		 
		
		return catID;

		
	}
	
	public void setCategoryName(String categoryName) {
		
		this.categoryName = categoryName;
	}
	
	public void setRewindIndex(int count){
		rewindIndx = rewindListArray.size() - (count+1) ;
	}
	
	public int getRewindIndex(){
		return rewindIndx ;
	}
	

	public void resetRewindCount(){
		rewindCount = 0 ;
	}
	
     public int	getRewindCount(){
       return rewindCount ;
    }
     public void increaseRewindCount(){
    	 rewindCount ++ ;
     }
     
     public void decreaseRewindCount(){
    	 rewindCount -- ;
     }

	public int getPlaylistCount() {
		return playlistArray.size();
	}

	public Story getCurrentStory() {
		return this.currentStory;
	}

	public void setCurrentStory(Story currentStory) {
	
		this.currentStory = currentStory;
	}

	public ResultReceiver getResultReciever() {
		return resultReciever;
	}

	public boolean isStreamReadyToPlay() {
		return isStreamReadyToPlay;
	}

	public void setStreamReadyToPlay(boolean isStreamReadyToPlay) {
		this.isStreamReadyToPlay = isStreamReadyToPlay;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public void setResultReciever(ResultReceiver resultReciever) {
		this.resultReciever = resultReciever;
	}

	public PlaylistManager(Context context, RuleBase currentRule) {
		this.context = context;
		this.sotryDbAdapter = StoryDBAdapter.getStoryDBAdapter(context);
		this.categoryDBAdapterForRead = CategoryDBAdapter.getCategoryDBAdapterForRead(context);
		this.playlistArray = new ArrayList<Story>();
		this.audioQueue = new ArrayList<String>();
		this.rewindListArray = new ArrayList<RewindAudio>();
		//this.currentSessionAudioList = new ArrayList<RewindAudio>();
		this.currentRule = currentRule;
		baseAcvity = (BaseActivity) context;
		keywordBuilder = new KeywordBuilder(context);
	}

	public void addStoryToQueue(String storyURL) {		
		this.audioQueue.add(storyURL);
	}
	
	
	
	public void removeStoryFromQueue(int index) {
		this.audioQueue.remove(index);
	}
	
	public ArrayList<RewindAudio> getRewindAudioQueue(){
		
		return rewindListArray;
	}

	public int sizeOfQueue() {
		return this.audioQueue.size();
	}

	public void clearQueue() {
		this.audioQueue.clear();
	}

	public synchronized void start() {
		
		// start the player with story 0 index
		startWithOffset(0);	
		//Do not download if its iPod Music Story
		if(this.currentStory != null){
		if((this.currentStory.getPrimaryCategory()!=RConstants.IPOD_MUSIC_CATEGORY) 
				              && (this.currentStory.getPrimaryCategory()!=RConstants.ADS_CATEGORY) ){
			
			getStoryStreamURL(false);
			
		}else{
			audioQueue.add(this.currentStory.getUrl());
			setStreamReadyToPlay(true);
		
		}
		
		startServiceMethod();
		// marked story played in database
		MarkStoryPlayedTask markStoryPlayTask = new MarkStoryPlayedTask(playlistArray.get(0).getTrackID());
		Thread markStoryPlayThread = new Thread(markStoryPlayTask);
		markStoryPlayThread.start();
		
		//set the last track id and remove the story from playlist.
		setLastTrackPlayedID(playlistArray.get(0).getTrackID());
		setStoryPlayedCountForIPodeStory(storyPlayedCountForIPodeMusic + 1);
		setStoryPlayedCountForAdvertisement(storyPlayedCountForAds + 1);
		
		playlistArray.remove(0);
		
		}
		}
		

	public void rewindTrack(float rewindSeek) {
		
		if (rewindListArray.size() <= 1 && rewindSeek < 30000) {
			// if this is first story then start story from 0 position
			Intent svc = new Intent(context, AudioService.class);
			svc.putExtra("receiver", resultReciever);
			svc.putExtra("progress", RConstants.START_FROM_ZERO_POSITION);
			svc.setAction(RConstants.seekAudio);
			context.startService(svc);

		} else if (rewindSeek >= 0) {

			Intent svc = new Intent(context, AudioService.class);
			svc.putExtra("receiver", resultReciever);
			svc.putExtra("progress", (int) rewindSeek);
			svc.setAction(RConstants.seekAudio);
			context.startService(svc);
			recentlyLastRewindStoryPlayed = true;

		} else {
			
			if(rewindListArray.size() > 1){
			
				isInRewindQueue = true ;
				
				if(this.rewindIndx < rewindListArray.size()){
					
					if(this.rewindIndx >= 0){
					// do not increase count if rewind repeats on last rewind queue story 	
						increaseRewindCount();
						setRewindIndex(this.rewindCount);
					
					}
				
					
					if(RConstants.BUILD_DEBUBG){
						Log.i(TAG, "rewind index : " + this.rewindIndx + " , rewind count : " + this.rewindCount);
					}
					
					if(this.rewindIndx<0){ 
									
						Intent svc = new Intent(context, AudioService.class);
						svc.putExtra("receiver", resultReciever);
						svc.putExtra("progress", RConstants.START_FROM_ZERO_POSITION);
						svc.setAction(RConstants.seekAudio);
						context.startService(svc);
						
					}else{
					
					Story rewindStory = rewindListArray.get(this.rewindIndx).getStory();
					setCurrentStory(rewindStory);
					
					setCategoryName(rewindListArray.get(this.rewindIndx).getCategoryName());
					
					//get stream then start the audio service
					if(audioQueue.size()>0){	  
						
						 removeStoryFromQueue(0);
					  }
					
					getStoryStreamURL(false);
					
					StartAudioService startAudioService = new StartAudioService(true, (int)rewindSeek,false);
					Thread startServiceThread = new Thread(startAudioService);
					startServiceThread.start();
					// update playlist manager 
					}
			
				}
			
			}else{
				//that means user hit rewind on first story <30 second we need to enable the rewind button.
				Intent svc = new Intent(context, AudioService.class);
				svc.putExtra("receiver", resultReciever);
				svc.putExtra("progress", (int)0);
				svc.setAction(RConstants.seekAudio);
				context.startService(svc);
				
			}
		}

	}
	
	

	public synchronized void next() {
		
		// check is in rewind queue 
		// (session list size - 1 ) because new audio is not been added yet in rewind list 
		
		if(isInRewindQueue ){
			
			// don't play last rewind story again
			if(recentlyLastRewindStoryPlayed){
				decreaseRewindCount();
				setRewindIndex(this.rewindCount);
				recentlyLastRewindStoryPlayed  = false ;
				
			}
		
			decreaseRewindCount();
			if( this.rewindCount<=0 ){
				
				setRewindIndex(0);
				
				isInRewindQueue=false;
				
			}
			setRewindIndex(this.rewindCount);
			
			// this code is to handle the exception ....
			if(this.rewindIndx == rewindListArray.size()){
				
				 isInRewindQueue = false ;
				    resetRewindCount();
				    if(audioQueue.size()>0){
				    	clearQueue();
					  }

					this.start();
					
					// of index out of bound otherwise enable only else block
			}else{
			
			Story rewindStory = rewindListArray.get(this.rewindIndx).getStory();
			setCurrentStory(rewindStory);
			setCategoryName(rewindListArray.get(this.rewindIndx).getCategoryName());
			
			//get stream then start the audio service
			if(audioQueue.size()>0){	  
				
				 removeStoryFromQueue(0);
			  }
			getStoryStreamURL(false);
		
			
			StartAudioService startAudioService = new StartAudioService(true, (int)0,false);
			Thread startServiceThread = new Thread(startAudioService);
			startServiceThread.start();
			
			}
			
		}else {
		
	    isInRewindQueue = false ;
	    resetRewindCount();
		
	    if(audioQueue.size()>0){	  
			 removeStoryFromQueue(0);
		  }

		this.start();
		
		}	 
		
		baseAcvity.postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityNextTrack);
			
	}

	public void startWithOffset(int offset) {
		
		synchronized (PlaylistManager.class) {
			if (playlistArray.size() > 0) {

				this.currentStory = playlistArray.get(offset);

			}else{
				this.currentStory = null;
			}
		}
	}

	
	public boolean BuildPlaylistByCurrentRule(RuleBase currentRule) {
		this.currentRule = currentRule;
		
		if (currentRule instanceof PlaylistRule) {
			
			return doBuildStoryListforPlayListRule();
			
		}else if (currentRule instanceof PodcastRule ){
			
			return doBuildStoryListforPodcastRule();
			
		}else if (currentRule instanceof IPodRule ){

			doBuildStoryListforIPodMusicRule();
		}else if( currentRule instanceof AdRule){
			
			doBuildStoryListForAdsRule();
		}
		
		return true;
	}

	
	public boolean doBuildBreakNewsStories(){
		boolean retvalue = true;
		
		if(keywordBuilder.getLocalNewsFilterKeyword() == null && 
			keywordBuilder.getTrafficFilterKeyword() == null      )
		{
			retvalue =false;
		}else
		{
			int count=0; //setting count to 0 will get all the stories.
			ArrayList<Integer> catIdList = new ArrayList<Integer>();
			
			// get id's of breaking , weather and traffic
			ArrayList<Category> listOfBWTCategory = categoryDBAdapterForRead.getCategoryBreakingNews();
			for(int indx = 0 ; indx < listOfBWTCategory.size() ; indx++){
				catIdList.add(Integer.valueOf(listOfBWTCategory.get(indx).getCategoryId()));
			}
			
			//we are not to select the track which are already in playlist array 
			ArrayList<Integer> playedTrackIDList = new ArrayList<Integer>();
			for(int idx = 0 ; idx < playlistArray.size();idx++){
				
					playedTrackIDList.add(playlistArray.get(idx).getTrackID());
				
			}
			
			ArrayList<Story> tplaylistArray = sotryDbAdapter.getStoriesByCategoriesId(catIdList,playedTrackIDList,count ,
					getCurrentTimeInGMTFormat() , keywordBuilder.getLocalNewsFilterKeyword());
	
			if (tplaylistArray.size() > 0) {
				for (int idx = 0; idx < tplaylistArray.size(); idx++) {
				
					playlistArray.add(tplaylistArray.get(idx));
					setNoStoryFound(this.noStoryFound+1);
				}
				this.currentStory = playlistArray.get(0);
	
			}
			// we need atlest two stories to start playing
			if (playlistArray.size() < RConstants.MINIMUM_STORIES_TO_PLAY) {
				retvalue=false;
			} 
		}
		
	return retvalue;
	
}
	
	public boolean doBuildStoryListForAdsRule(){
	
		// reset counting for advertising first 
		setStoryPlayedCountForAdvertisement(0);
	
		boolean retvalue = true;
		int count=1;
		
		ArrayList<Integer> catIdList = new ArrayList<Integer>();
		catIdList.add(Integer.valueOf(RConstants.ADS_CATEGORY));
		
		//we are not to select the track which are already in playlist array 
		ArrayList<Integer> playedTrackIDList = new ArrayList<Integer>();
		for(int idx = 0 ; idx < playlistArray.size();idx++){
			
				playedTrackIDList.add(playlistArray.get(idx).getTrackID());
			
		}
		ArrayList<Story> tplaylistArray = sotryDbAdapter.getAdsByCategoriesId(catIdList,playedTrackIDList,count , 0);

		if (tplaylistArray.size() > 0) {
			{
				// add advertisement at 0th index , eight stories have been played yet.
				playlistArray.add(0, tplaylistArray.get(0));
			}
			
			this.currentStory = playlistArray.get(0);

		}
		// we need atlest two stories to start playing
		if (playlistArray.size() < RConstants.MINIMUM_STORIES_TO_PLAY) {
			retvalue=false;
		} 
		
		return retvalue;
		
	
		
	}
	
	public boolean doBuildStoryListforIPodMusicRule(){
		
		// reseting counting for ipod music first
		setStoryPlayedCountForIPodeStory(0);
		
		boolean retvalue = true;
		int count=1;
		
		ArrayList<Integer> catIdList = new ArrayList<Integer>();
		catIdList.add(Integer.valueOf(RConstants.IPOD_MUSIC_CATEGORY));
		
		//we are not to select the track which are already in playlist array 
		ArrayList<Integer> playedTrackIDList = new ArrayList<Integer>();
		for(int idx = 0 ; idx < playlistArray.size();idx++){
			
				playedTrackIDList.add(playlistArray.get(idx).getTrackID());
			
		}
		ArrayList<Story> tplaylistArray = sotryDbAdapter.getStoriesByCategoriesId(catIdList,playedTrackIDList,count , 0 
				, keywordBuilder.getLocalNewsFilterKeyword());

		if (tplaylistArray.size() > 0) {
		//	for (int idx = 0; idx < tplaylistArray.size(); idx++) {
				playlistArray.add(0 , tplaylistArray.get(0));
		//	}
			this.currentStory = playlistArray.get(0);

		}
		// we need atlest two stories to start playing
		if (playlistArray.size() < RConstants.MINIMUM_STORIES_TO_PLAY) {
			retvalue=false;
		} 
		
		return retvalue;
		
	}
	
	public  boolean  doBuildStoryListforPlayListRule(){
								
 					PlaylistRule playlistRule = (PlaylistRule) this.currentRule;

					this.categoryName = playlistRule.getCategoryName().trim();
					// get categories and subcategories for the main category first
					int parentId = categoryDBAdapterForRead.getCategoryIDByName(this.categoryName);
					//check if this category is selected by user
					Category tcategory = new Category();
					tcategory.setCategoryId(parentId);
					tcategory.setName(this.categoryName);
					
					//skip the mycategory check for welcome we have to play welcome any ways.
					if(!this.categoryName.equalsIgnoreCase("welcome")){
						
						//get the breaking news
						doBuildBreakNewsStories();
						
						int catID = categoryDBAdapterForRead.categoryExistInMyCategoryById(tcategory);
						if(catID==0){
							//user has not selected this catgory to play return and get new rule.
							return false;
						}
					}
					
					ArrayList<Category> tCategoryList = categoryDBAdapterForRead.getSubcategoriesByParentId(parentId);
					ArrayList<Integer> allcatListID = new ArrayList<Integer>();

				
					// add the parent id first then all its child
					allcatListID.add(parentId);

					for (int i = 0; i < tCategoryList.size(); i++) {
						Category subcatgory = tCategoryList.get(i);
						allcatListID.add(subcatgory.getCategoryId());
					}
					
					
					// pass 1 to get all lead stories and add them at bottom in playlist
					// if leadstorycount >0
					
					//we are not to select the track which are already in playlist array 
					ArrayList<Integer> playedTrackIDList = new ArrayList<Integer>();
					for(int idx = 0 ; idx < playlistArray.size();idx++){
						
							playedTrackIDList.add(playlistArray.get(idx).getTrackID());
						
					}
					
					
					if (playlistRule.getLeadStoriesCount() > 0) {
				
						ArrayList<Story> leadStoriesList = sotryDbAdapter.
								getNonLeadStoriesOrLeadStoriesFromDb(allcatListID, 1,playlistRule.getLeadStoriesCount()
										,playedTrackIDList , getCurrentTimeInGMTFormat()
										, keywordBuilder.getLocalNewsFilterKeyword());

						for (int indx = 0; indx < leadStoriesList.size(); indx++) {
							
							playlistArray.add(leadStoriesList.get(indx));
							
							setNoStoryFound(this.noStoryFound+1);
						}
					}
					
					
					// pass 0 to get non lead stories and add first to playlist array
					
					//we are not to select the track which are already in playlist array 
					playedTrackIDList.clear();
					for(int idx = 0 ; idx < playlistArray.size();idx++){
						
							playedTrackIDList.add(playlistArray.get(idx).getTrackID());
						
					}

					
					// only if nonlead story count > 0
					if (playlistRule.getNonLeadStoriesCount() > 0) {
						ArrayList<Story> nonLeadStoriesList = sotryDbAdapter.
								getNonLeadStoriesOrLeadStoriesFromDb(allcatListID, 0,playlistRule.getNonLeadStoriesCount()
										,playedTrackIDList,getCurrentTimeInGMTFormat() , keywordBuilder.getLocalNewsFilterKeyword());

						for (int index = 0; index < nonLeadStoriesList.size(); index++) {
						
							playlistArray.add(nonLeadStoriesList.get(index));
							
							setNoStoryFound(this.noStoryFound+1);
						}
					}

					
					// check if we have any story for this rule
					// make current story
					if (playlistArray.size() > 0) {

						// assign the current story
						this.currentStory = playlistArray.get(0);

					}

					// Do we have 2 stories atlest to play
					if (playlistArray.size() < RConstants.MINIMUM_STORIES_TO_PLAY) {
						return false;
					}	
				
					return true;
	}
	

	public  boolean  doBuildStoryListforPodcastRule(){
		
		PodcastRule podCastRule = (PodcastRule) this.currentRule;
		
		this.categoryName = podCastRule.getCategoryName().trim();
		
		// get categories and subcategories for the main category first
		int parentId = categoryDBAdapterForRead.getCategoryIDByName(this.categoryName);
		
		//check if this category is selected by user
		Category tcategory = new Category();
		tcategory.setCategoryId(parentId);
		tcategory.setName(this.categoryName);
		
		int catID = categoryDBAdapterForRead.categoryExistInMyCategoryById(tcategory);
		if(catID==0){
			//user has not selected this catgory to play return and get new rule.
			return false;
		}
		
		ArrayList<Category> tCategoryList = categoryDBAdapterForRead.getSubcategoriesByParentId(parentId);
		ArrayList<Integer> allcatListID = new ArrayList<Integer>();

		// add the parent id first then all its child
		allcatListID.add(parentId);

	
		for (int i = 0; i < tCategoryList.size(); i++) {
			Category subcatgory = tCategoryList.get(i);
			allcatListID.add(subcatgory.getCategoryId());
		}
		
		//the story type for podcast is 2
		int storyType=2;
		
		//we are not to select the track which are already in playlist array 
		ArrayList<Integer> playedTrackIDList = new ArrayList<Integer>();
		for(int idx = 0 ; idx < playlistArray.size();idx++){
			
				playedTrackIDList.add(playlistArray.get(idx).getTrackID());
			
		}
		
		if(podCastRule.getStoryCount()>0){
			ArrayList<Story> tStoriesList  = sotryDbAdapter.getPodcastStoriesForCategoriesFromDb(allcatListID,podCastRule.getStoryCount()
					                                                  ,storyType,playedTrackIDList , getCurrentTimeInGMTFormat() , 
					                                                     keywordBuilder.getLocalNewsFilterKeyword());
			
			for (int indx = 0; indx < tStoriesList.size(); indx++) {
				
				// check the story's expiration time first and then add in queue if valid one
				playlistArray.add(tStoriesList.get(indx));
				setNoPodCastFound(this.noPodCastFound+1);
			}
		}
		if (playlistArray.size() > 0) {

			// assign the current story
			this.currentStory = playlistArray.get(0);

		}
		
		// Do we have 2 stories atlest to play
		if (playlistArray.size() < RConstants.MINIMUM_STORIES_TO_PLAY) {
			return false;
		}	
		
		return true;
	}
	
	public void stopAudioService(){
		Intent svc = new Intent(this.context, AudioService.class);
		context.stopService(svc);
	}

	
	private void startServiceMethod() {
		
		Thread startAudioThread = new Thread ( new StartAudioService(false, 0,false));
		startAudioThread.start();
		
	}
	
	
	class StartAudioService implements Runnable {

		boolean rewind = false;
		int seekpos = 0;
		boolean isOrientationChanged=false;
		
		public StartAudioService(boolean rewind , int seekPos , boolean isOrientationChanged){
			
			this.rewind = rewind;
			this.seekpos = seekPos;
			this.isOrientationChanged = isOrientationChanged;
			
		}
		@Override
		public void run() {
			
			for(;;){
				
				   if(isStreamReadyToPlay()){
					
					   
					//local music not to be added to the rewind queue
					Intent svc = new Intent(context, AudioService.class);
					svc.putExtra("receiver", resultReciever);
					svc.putExtra("downloadUrl", audioQueue.get(0));
					if(!rewind)
					{
						if(currentStory.getPrimaryCategory()!=RConstants.IPOD_MUSIC_CATEGORY 
								  && currentStory.getPrimaryCategory() != RConstants.ADS_CATEGORY){
							RewindAudio rewindAudio = new RewindAudio();
							rewindAudio.setCategoryName(getCategoryName());
							rewindAudio.setStory(currentStory);
							rewindAudio.setStreamUrl(downloadUrl);
							// add audio to current session  queue
							rewindListArray.add(rewindAudio);
						}
						svc.setAction(RConstants.startNewAudio);
					}else{
						
						//run track for rewind
						svc.putExtra("progress",(int) this.seekpos);
						
						if(isOrientationChanged){
							svc.putExtra("orientationChanged", true);
							//setting seekPos to 0 make sure we dont' seek to player
							svc.putExtra("progress",(int) 0);
						}
						svc.putExtra("actualQueueFlag", false);
						
						svc.setAction(RConstants.seekAudioByRewindQueue);
					}
					//Tell Audio the UI music server is starting playing
					isPlaying=true;
					if(RConstants.BUILD_DEBUBG && audioQueue.size() > 0){
						Log.i(TAG,"RIVET_LOG : Starting Audio service for stream: "+audioQueue.get(0));
					}
					
					context.startService(svc);
					break;
				}else{
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}//ends for(;;)
		}
		
	}
	
	
	public void getStoryStreamURL(boolean nextstory) {
		
		setStreamReadyToPlay(false);
		String url = "";
		getUrlByTrackIdHandler = new GetUrlByTrackIdHandler();

		// if flag is true gets storyURL for current story else gets for next
		// story
		if(nextstory){
			url =  RConstants.BaseUrl + "downloadurl/"
					+ this.playlistArray.get(0).getTrackID();
					
		}else{
		url = RConstants.BaseUrl + "downloadurl/"
					+ this.currentStory.getTrackID();
		}
		Thread getUrlThread = new Thread(new HttpRequest(url,
				HttpMethodType.GET, getUrlByTrackIdHandler,
				getUrlByTrackIdHandler, null, null , false , null));
		getUrlThread.start();
		if(RConstants.BUILD_DEBUBG){
			Log.i(TAG,"RIVET_LOG : "+url);
		}
		try {
			getUrlThread.join();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}

		// add story to audio queue
		addStoryToQueue(downloadUrl);
		
		//notify the content Home UI so it can stop the loader and call start on service.
		if(RConstants.BUILD_DEBUBG){Log.i(TAG, "RIVET_LOG story added to queue set stream t readystate");}
		setStreamReadyToPlay(true);

	}

	
	
	public class GetUrlByTrackIdHandler implements HttpResponseListener,
			HttpExceptionListener {

		@Override
		public void handleResponse(String response) {
			
			try {
				JSONObject downloadUrlObject = new JSONObject(response);
				downloadUrl = downloadUrlObject.getString("downloadURL");
				if(RConstants.BUILD_DEBUBG){Log.i(TAG,"RIVET_LOG :  "+downloadUrl );}
				
			} catch (JSONException e) {
				
				e.printStackTrace();
			}
			
		}

		@Override
		public void handleException(String exception) {
			
			Log.i(TAG,"RIVET_LOG : "+exception.toString());
			
		}

	}
	
	
	public void play() {
		
		if (isPlaying) {
			isPlaying = false;
			Intent svc = new Intent(context, AudioService.class);
			svc.putExtra("receiver", resultReciever);
			svc.setAction(RConstants.pauseAudio);
			context.startService(svc);
			
			baseAcvity.postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityPlaybackPaused);

		} else {

			isPlaying = true;
			Intent svc = new Intent(context, AudioService.class);
			svc.putExtra("receiver", resultReciever);
			svc.setAction(RConstants.playAudio);
			context.startService(svc);
			
			baseAcvity.postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityPlaybackPlay);

		}
	}
	
	
	/**
	 * 
	 * @param expirationTimeStamp
	 * @return true is the story is not expired yet according to time stamp coming from server.
	 */
	@SuppressLint("SimpleDateFormat")
	private long getCurrentTimeInGMTFormat() {
		
		long currentTimeGMTMillis = 0 ;

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'H:m:s'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		String currentTimeGMT = dateFormat.format(System.currentTimeMillis());
		try {
			Date currentDate = dateFormat.parse(currentTimeGMT);
			currentTimeGMTMillis = currentDate.getTime();
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
      return currentTimeGMTMillis;
	}
	
	private class MarkStoryPlayedTask implements Runnable{

		
		private int trackId;

		public MarkStoryPlayedTask(int trackId) {
			
			this.trackId = trackId ;
		}
		
		@Override
		public void run() {
			
			for( ; ; ){
				try {
					sotryDbAdapter.setStoryPlayed(trackId);
					
					break ;
					
				} catch (SQLiteDatabaseLockedException sqliteLockedException) {
					
					sqliteLockedException.printStackTrace();
					if(RConstants.BUILD_DEBUBG){
						Log.i(TAG, "StoryDB is locked ");
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
			
						e.printStackTrace();
					}
				}
				
			
			}
		}
		
	}

}
