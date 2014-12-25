package com.rivet.app.common;

public class ActivitiesUserApp {
	
	public static String getAppropriateAction(int userAction) {
		String action  = "" ;
		
		if(userAction == RConstants.ActivityPlaybackPlay){
			
			action = "PlayCommand" ;
			return action ;
		}else if(userAction == RConstants.ActivityFailedToLoadAds){
			
			action = "InvalidAdXML" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityCategoryDisable){
			
			action = "CategoryDisabled" ;
			return action ;
		}else if(userAction == RConstants.ActivityCategoryEnable){
			
			action = "CategoryEnabled";
			return action ;
		}else if(userAction == RConstants.ActivityAboutApp){
			action = "SettingsAbout" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityErrorLoginGigiya){
			action = "GigyaLoggingError";
			return action ;
			
		}else if(userAction == RConstants.AcvtivityFailureToLoadStories){
			
			action = "FailureToLoadStories" ;
			return action ;
		}else if(userAction == RConstants.ActivityUserLogout){
			action = "UserLogout" ;
			return action ;
		}else if(userAction == RConstants.ActivityExternalAudioInterruption){
			action = "ExternalAudioInterruption" ;
			return action ;
		}else if(userAction == RConstants.ActivitySplashFinished){
			action = "SplashFinished" ;
			return action ;
		}else if(userAction == RConstants.ActivityPlaybackPaused){
			action = "PauseCommand" ;
			return action ;
		}else if(userAction == RConstants.ActivityKillApplication){
			action = "UserKillsTheApp" ;
		}else if(userAction == RConstants.ActivityLikeStory){
		
			action = "ThumbsUp" ;
			return action ;
		}else if(userAction == RConstants.ActivityPlaybackRewind){
			action = "RewindCommand" ;
			return action ;
		}else if(userAction == RConstants.ActivityNextTrack){
			action = "NextTrack" ;
			return action ;
		}else if(userAction == RConstants.ActivityNewTrackStarted){
			action = "ActivityNewTrackStarted" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityFavouriteAdd){
			action = "ActivityFavouriteAdd" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityShareOnFB){
			action = "FacebookShareSelected" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityShareOnTwitter){
			action = "TwitterShareSelected" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityAppInBackground){
			action = "EnterBackground" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityAppInForeground){
			action = "EnterForeground" ;
		}else if(userAction == RConstants.ActivityShareOnEmail){
			action = "EmailShareSelected" ;
			return action ;
			
		}else if(userAction == RConstants.ActivitySearch){
			action = "SearchCommand" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityHomeTab){
			action = "HomeCommand" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityCustomizeTab){
			action = "Customize" ;
			return action ;
			
		}else if(userAction == RConstants.ActivitySelectedCategories){
			action = "ActivitySelectedCategories" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityLocationUpdate){
			action = "LocationUpdate" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityMusicStory){
			action = "ActivityMusicStory" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityFailureLoadStories){
			action = "ActivityFailureLoadStories" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityLoadBreakingNews){
			action = "ActivityLoadBreakingNews" ;
			return action ;
			
		}else if(userAction == RConstants.ActivityModeSimple){
			action = "SimpleMode";
			return action ;
			
		}else if(userAction == RConstants.ActivitySimpleModeExit){
			action = "SimpleModeExit";
			return action ;
			
		}else if(userAction == RConstants.ActivitySkipRegistration){
			action = "SkipRegistration";
			return action ;
			
		}else if(userAction == RConstants.ActivityLoginThroughFB){
			action = "LoginThroughFB";
			return action ;
			
		}else if(userAction == RConstants.ActivityLoginWithEmail){
			action = "LoginWithEmail";
			return action ;
			
		}else if(userAction == RConstants.ActivityStoryEnded){
			
			action = "StoryEnded" ;
			return action ;
		}else if(userAction == RConstants.ActivityStoryPlayBackResumed){
			
			action = "StoryPlaybackResumed" ;
			return action ;
		}else if(userAction == RConstants.ActivityStoryPlayBackPaused){
			
			action = "StoryPlaybackPaused" ;
			return action ;
		}else if(userAction == RConstants.ActivityNewStoryStarted){
			
			action = "NewStoryStarted" ;
			return action ;
		}
		
		 return action ;
		
	}

}
