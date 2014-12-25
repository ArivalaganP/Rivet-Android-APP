package com.rivet.app.common;

import android.graphics.Bitmap;
import twitter4j.Twitter;
import twitter4j.auth.RequestToken;

/**
 * Created by brian on 16/6/14.
 */

public class RConstants {

	public static final String QUESTION_KEY = "question_key";
	public static final String ANSWER_KEY = "answer_key";

	public static final String RIVET_PREFERENCES = "rivet_prefernces";
	public static final String RUNNING_FIRST_TIME = "running_first_time";
	
	
	public static final String FINISH_STORY_LOADING_ACTION = "finish_story_loading_action";
	public static final String KEY_STORY_NAME = "key_category_name";
	public static final String FINISH_STORY_LOADING = "finish_story_loading";
	public static final String PRODUCER_NAME = "anchor_name";
	public static final String CAREGORY_NAME_KEY = "category_name_key";
	public static final String FINISH_RULE_BUILDING = "RuleBuildingComplete";
	public static final String FINISH_CATEGORIES_BUILDING = "CategoryBuildingComplete";
	public static final String FINISH_STORY_LOAD_TO_DB = "FinishStoriesLoadtoDB";
	public static final String NEW_STORIES_ADDED = "AddedNewStories" ;
	public static final int MINIMUM_STORIES_TO_PLAY=2;
	
	public static final int UPDATE_UI_ACTION = 71000;
	public static final int UPDATE_UI_NEXT_ENABLE_ACTION = 71001;
	public static final int START_NEW_STORY = 71002;
	public static final int SHOW_PAUSE_BUTTON = 71003;
	public static final int SHOW_PLAY_BUTTON = 71004;
	public static final int UPDATE_UI_REWIND_ACTION = 71005;
	public static int UPDATE_UI_UNLOCK_ACTION=71007;
	public static final int UPDATE_UI_ON_ORIENTATION_CHANGED_ACTION = 71008;
	public static final int UPDATE_UI_ACTION_DISABLE_INPUT = 71009 ;
	public static final int UPDATE_UI_ACTION_ENABLE_INPUT = 71010;
	public static final int UPDATE_UI_WITH_NO_STORY_ACTION = 71010 ;
	public static final int BLUETOOTH_META_CHANGE = 71011 ;
	public static final int SEND_NOTIFICATION_WHEN_INTERNET_CONNECTION_LOW = 71012;
	public static final int SEND_NOTIFICATION_WHEN_STRONG_INTERNET_CONNECTION = 71013;

	
	public static final int TOP_NEWS_CATEGORY = 28;
	public static final int BREAKING_NEWS_CATEGORY = 1;
	public static final int ENTERTAINMENT_CATEGORY = 18;
	public static final int BUSINESS_CATEGORY = 26;
	public static final int LIFE_STYLE_CATEGORY = 34;
	public static final int TECHNOLOGY_CATEGORY = 33;
	public static final int SPORTS_CATEGORY = 8;
	public static final int POLITICS_CATEGORY = 43;
	public static final int SCIENCE_CATEGORY = 32;
	public static final int CRIME_AND_COURT = 2;
	public static Bitmap sharingFBBitmap;
	public static final int IPOD_MUSIC_CATEGORY = 777;
	public static final int ADS_CATEGORY = 888;
	public static final int REWIND_DURATION_PLAYER = 30000;
	public static boolean countLayoutChanged = false;
	public static final int WELCOME_PARENT_CATEGORY=42;
	public static final int WELCOME_CHILD_CATEGORY=46;
	public static final int PLAYER_CONTROL_ALPHA=51;
	public static final int PLAYER_CONTROL_ALPHA_DISABLE=153;
	
	// flurry api key to track the user behavior in app
	
	public static final String FLURRY_API_KEY = "2MYMPTY3K2P2KV3VSNC8" ; 
	public static final String CRITTERCISM_SDK_KEY = "53e8b94d1787844d84000005" ;
	
	
	public static boolean BUILD_DEBUBG = false;

	public static String BaseUrl = "http://services.production.h2radio.com/hhrbes/content/";
	public static String GIGYA_API_KEY = "3_ldvMvcsAy7Xz-yqE5U3j3ksiGRs8rmfLt7x0DGoftOFQLlHUyW48fhnY3-qhny5X";
	public static String playAudio = "play";
	public static String pauseAudio = "pause";
	public static String seekAudio = "seekAudio";
	public static String seekAudioByRewindQueue = "seekAudioByRewindQueue";
	public static String startNewAudio = "startNew";
	public static int threadCount = 0;
	public static String ADVERTISEMENT_TITLE = "Advertisement";
	public static String IPOD_TITLE = "ipod";
	public static String SPLASH_TITLE = "Splash";
	public static String POADCAST_TITLE = "Podcast";
	public static boolean IS_DEMO = false;
	public static String LAST_REFRESHED_TIME = "lastRefreshedTime";
	public static String MainPlayListUrl = "mainplaylist";
	public static boolean STORY_UPLOAD_TO_DB= false;
	public static String HHR_ERROR_SERVER_IS_DOWN_STORY_BUILDER_FAIL = "SERVER_DOWN_STORY_BUILDER_FAIL";
	public static final String HHR_ERROR_SERVER_IS_DOWN_RULE_BUILDER = "SERVER_DOWN_RULE_BUILDER";
	public static final String HHR_ERROR_SERVER_IS_DOWN_CATEGORY_BUILDER = "SERVER_DOWN_CATEOGRY_BUILDER";

			
			
	/************************ ERROR CODES ***********************************************/


	public static int RETRY_COUNT = 2;

	/**
	 * @param resultIsProgress
	 *            : used as result code, for sending the current position of
	 *            audio, being played in service.
	 */
	public static int resultIsProgress = 100;

	/**
	 * @param resultIsDuration
	 *            : used as result code, for sending the total duration of
	 *            audio, being played in service.
	 */
	public static int resultIsDuration = 200;

	/**
	 * @param resultIsPlay
	 *            : used as result code, to tell activity that audio has started
	 *            playing.
	 */
	public static int resultIsPlay = 300;

	/**
	 * @param resultIsPause
	 *            : use as result code, to tell activity that audio has been
	 *            paused.
	 */
	public static int resultIsPause = 400;

	/**
	 * @param resultIsCompletion
	 *            : used as result code, to tell activity that audio has
	 *            finished.
	 */
	public static int resultIsCompletion = 500;

	/**
	 * @param currentPosition
	 *            : used to transfer current position of audio from service to
	 *            activity
	 */
	
	
	public static String currentPosition = "currentPosition";

	/**
	 * @param audioDuration
	 *            : used to transfer total duration of audio from service to
	 *            activity
	 */
	public static String audioDuration = "audioDuration";
	
	
	
	// mobile app tracking key
	
	public static final String CONVERSION_KEY = "c3fc85c103890526590eae69e5013960";
	public static final String ADVERTIZER_ID = "16730";

	// api methods of gigya sdk
	public static String ACCOUNTS_INIT_REGISTRATION = "accounts.initRegistration";
	public static String ACCOUNTS_REGISTRATION = "accounts.register";
	public static String ACCOUNTS_LOGIN = "accounts.login";
	public static String ACCOUNTS_LOGOUT = "accounts.logout";
	public static String ACCOUNTS_RESET_PASSWORD = "accounts.resetPassword" ;
	public static final String ACCOUNTS_FB_LOGOUT = "socialize.logout";
	public static String UPDATE_STATUS = "socialize.setStatus" ;
	public static final String PUBLISH_STATUS = "socialize.publishUserAction ";
	public static final String MERGE_ACCOUNT = "accounts.linkAccounts" ;
	public static String QUESTION_INDX = "question_indx" ;
	public static int currentPosOfAudioPlayer = 0 ;
	
// twitter integration keys
	
	static String PREFERENCE_NAME = "twitter_oauth";
	public static boolean LOGIN_DONE_FINISH_OPTION_ACTIVITY = false ;
	public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	public static final String TWITTER_CALLBACK_URL = "com.rivet.app://callback";
	
	
	// bluetooth interface constants 
	
	public static final String AVRCP_PLAYSTATE_CHANGED = "com.android.music.playstatechanged";
	public static final String AVRCP_META_CHANGED = "com.android.music.metachanged";
	public static final String PLAYSTATE_CHANGED = "com.rivet.app.playlistChanged";
	public static final String META_CHANGED = "com.rivet.app.metaChanged";

	// twitter keys 
	public static String TWITTER_CONSUMER_KEY = "dCkfIBrfuwuNVRf4aS9gIK4pX";
	public static String TWITTER_CONSUMER_SECRET = "foiHHHWrEYkxUREMRbwKjE4W0j3Lmyqq3IkAATrIgVXriNyU9N";
	
	
	// Twitter oauth urls
	
	static final String URL_TWITTER_AUTH = "auth_url";
	public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	public  static RequestToken requestToken;
	// twitter share variables 
	public static Twitter twitter;
		
	
	// user actions log constants
	
		 public static final int ActivityPlaybackPlay = 1000;
		 public static final int ActivityPlaybackPaused = 1001 ;
		 public static final int ActivityPlaybackRewind = 1003 ;
		 public static final int ActivityNextTrack = 1004 ;
		 public static final int ActivityNewTrackStarted = 1005 ;
		 public static final int ActivityFavouriteAdd = 1006 ;
		 public static final int ActivityLikeStory = 1007 ;
		 public static final int ActivityShareOnFB = 1008 ;
		 public static final int ActivityShareOnTwitter = 1009 ;
		  
		 public static final int  ActivityShareOnEmail = 2000 ;
		 public static final int  ActivitySearch = 2001 ;
		 public static final int  ActivityHomeTab = 2002  ;
		 public static final int  ActivityCustomizeTab = 2003 ;
		 public static final int  ActivitySelectedCategories = 2004 ;
		 public static final int  ActivityLocationUpdate = 2005 ;
		 public static final int  ActivityMusicStory = 2006 ;
		 public static final int  ActivityFailureLoadStories = 2007 ;
		 public static final int  ActivityLoadBreakingNews = 2008 ;
		 public static final int ActivityModeSimple = 2009 ;
		 public static final int ActivitySimpleModeExit = 2010;
		 public static final int ActivitySkipRegistration = 2011;
		 public static final int ActivityLoginThroughFB = 2012;
	     public static final int ActivityLoginWithEmail = 2013;
		 public static final int ActivityStoryEnded = 2014;
		 public static final int ActivityStoryPlayBackResumed = 2015;
		 public static final int ActivityStoryPlayBackPaused = 2016;
		 public static final int ActivityNewStoryStarted = 2017;
		 public static final int ActivitySplashFinished = 2018;
		 public static final int ActivityKillApplication = 2019;
		public static final int ActivityAppInBackground = 2020;
		public static final int ActivityAppInForeground = 2021;
		public static final int ActivityAboutApp = 2022;
		public static final int ActivityUserLogout = 2023;
		public static final int ActivityExternalAudioInterruption = 2024;
		public static final int ActivityErrorLoginGigiya = 2025;
		public static final int AcvtivityFailureToLoadStories = 2026;
		public static final int ActivityCategoryDisable = 2027;
		public static final int ActivityCategoryEnable = 2028;
		public static final int ActivityFailedToLoadAds = 2029 ;
		
	 public static final long UPDATE_STORIES_TIME_INTERVAL = ( 1000 * 60 * 10 );
	 
	public static final int START_FROM_ZERO_POSITION = 0;
	public static final String MAPPOLYGON = "mappolygons";
	public static final String TRAFFIC_KEYWORD = "trafficKeyword";
	public static final String LOCAL_NEWS_KEYWORD = "localNewsKeyword";
	
	// account login details keys because DB is confliting
	public static final String GMAIL_USER_NAME = "gmail_user_name";
	public static final String IS_LOGIN_WITH_GMAIL = "is_login_with_gmail";
	public static final String FB_LOGIN_PROVIDER_UID = "fb_login_provider_uid";
	public static final String FB_EMIAL_ID = "fb_email_id";
	public static final String FB_USER_IS_LOGIN = "fb_user_is_login";
	public static final String FB_UID = "fb_uid";
	public static final String FB_PHOTO_URL = "fb_photo_url";
	public static final String LAST_RUN_TIME = "last_run_time";
	
	
	public static final String LATEST_AD_TRACK_ID = "latestAdTrackID";
	
	public static final int MAXIMUM_BUFFERING_COUNT = 29;
	
	
	public static final CharSequence ENTER_EMAIL_ID = "Please enter email id";
	public static final String FB_SHARE_IMAGE_URL = "http://www.rivetnewsradio.com/images/rivet-share-apr2014.jpg" ;
	public static final String NO_STORY_TO_SHARE = "No story to share yet" ;
	public static final String NO_INTERNET_CONNECTION = "No Internet connection";
	public static final String SLOW_INTERNET_CONNECTION = "Internet Connection is slow";
	public static final String RIVET_RADIO = "Rivet Radio";
	public static final String NO_STORY_TO_PLAY = "No new stories to play for selected categories.";
	public static final String RIVET_WITH = "Rivet radio with ";
	public static final CharSequence WELCOME_RIVET = "Welcome";
	public static final String ARTIST = "artist";
	public static final String TRACK = "track";
	public static final String PLAYING = "playing";
	public static final String DURATION = "duration";
	public static final String POSITION = "position";
	public static final CharSequence LOADING_STORIES = "Loading...";
	public static final CharSequence UNLIKE_STORY = "Unlike";
	public static final CharSequence LIKE_STORY = "Like";
	public static final CharSequence SHARE_STORY_FACEBOOK = "Share on Facebook";
	public static final CharSequence SHARE_STORY_TWITTER = "Share on Twitter";
	public static final CharSequence SHARE_STORY_EMAIL = "Share via Email";
	public static final String SHARE_STORY_URL = "http://www.rivetnewsradio.com/share/";
	public static final String LOGIN_FIRST_FB = "Please Login first into facebook";
	public static final String RIVET_PACKAGE_NAME = "com.rivet.app";
	public static final String LOGIN_ACTIVITY = "we are in activity after login";
	public static final String TWITTER_AUTH_TOKEN = "Twitter OAuth Token";
	public static final String NO_STORY_TO_SHARE_TWITTER = "No Story to share on Twitter";
	public static final CharSequence LOGGING_OUT_WAIT = "Logging out, please wait...";
	public static final CharSequence RESETING_PASSWORD_WAIT = "Reseting password, please wait...";
	public static final CharSequence LOGGING_IN_WAIT = "Logging in, please wait...";
	public static final String LOGIN_ID = "loginID";
	public static final String PASSWORD = "password";
	public static final CharSequence LOGOUT_SUCCESSFULLY = "You have successfully logged out";
	public static final CharSequence ERROR_RESETING_PASSWORD = "Error in Reset Password";
	public static final String FLURRY_LOGIN_ACTION_EMAIL = "LoginWithEmail";
	public static final CharSequence REGISTERING_WAIT = "Registering, please wait...";
	public static final String REG_TOKEN = "regToken";
	public static final String FINALIZE_REGISTRATION = "finalizeRegistration";
	public static final String TARGET_ENV = "targetEnv";
	public static final String USER_ACTION = "userAction";
	public static final String EMAIL = "email";
	public static final CharSequence REGISTERATION_SUCESSFULL = "Registration was successful!";
	public static final String REGISTRATION_BOSCH_FAILED = "register with Bosch Failed";
	public static final String FLURRY_ACTION_SKIP_REGISTRATION = "SkipRegistration";
	public static final String PROVIDER = "provider";
	public static final String FB_LOGIN_BEHAVIOR = "facebookLoginBehavior";
	public static final String FLURRY_ACTION_LOGIN_THROUGH_FB = "LoginThroughFB";
	public static final String MUSIC_FROM_DEVICE = "Music from the device";
	public static final String CATEGORIES_SELECTED = "CategoriesSelected";
	public static final String ENABLED_CATEGORY = "enabled";
	
	// categories names
	public static final String BREAKING_NEWS = "Breaking News";
	public static final String TRAFFIC_NEWS = "Traffic";
	public static final String WEATHER_NEWS = "Weather";
	
	// story time constants
	public static final String MONTH_AGO = " month ago by ";
	public static final String MONTHS_AGO = " months ago by ";
	public static final String DAY_AGO = " day ago by ";
	public static final String DAYS_AGO = " days ago by ";
	public static final String HOURS_AGO = " hours ago by ";
	public static final String HOUR_AGO = " hour ago by ";
	public static final String SECONDS_AGO = " seconds ago by ";
	public static final String MINUTES_AGO = " minutes ago by ";
	
	
	// user logs constants
	public static final String APP_VERSION = "appVersion";
	public static final String PLATFORM = "platform";
	public static final String OS_NAME = "android";
	public static final String GIGYA_UID = "gigyaUID";
	public static final String LATITIDE = "latitude";
	public static final String LONGTITUDE = "longitude";
	public static final String DISABLED_CATEGORY = "disabled";
	public static final String LOG_CATEGORIES = "LogCategories";
	public static final String ACTIVITY_TIMESTAMP = "activityTimestamp";
	public static final String USER_ID = "userID";
	public static final String DATA_LOGS = "data";
	public static final String ACTION_TYPE = "actionType";
	public static final String CURRENT_TRACK_ID = "currentTrackId";
	public static final String CURRENT_TRACK_TITLE = "currentTrackTitle";
	public static final String CURRENT_TRACK_POSITIONS_SECONDS = "currentTrackPositionInSeconds";
	public static final String TERMS_OF_USE_URL = "http://www.rivetnewsradio.com/docs/terms-of-use";
	public static final CharSequence LOADING_PAGE_WAIT = "Loading page, please wait...";
	public static final String START_BREAKING_NEWS_FINDER = "RIVET_LOG: Starting Breaking news finder Thread";
	public static final String STACK_OVER_FLOW_FINDER_THREAD = "RIVET_LOG: Starting stackoverFlow  finder Thread";
	public static final String ACTIVITY_LOG = "activitylog";
	public static final String LOG_CONTENT = "logContent";
	public static final String RESPONSE_CODE = "Response Code is";
	public static final String SONG_TRACK_ID = "songTrackID";
	public static final String SONG_ANCHOR_NAME = "songAnchorName";
	public static final String SONG_TITLE_NAME = "songTitleName";
	public static final String SONG_URL = "songURL";
	public static final String SONG_LENGTH = "songLength";
	public static final String SONG_CATEGORY_NAME = "songCategoryName";
	public static final String TRAFFIC_POLYGON = "trafficPolygon";
	public static final String KEYWORD = "keyword";
	public static final String LOCAL_NEWS_POLYGON = "localNewsPolygon";
	public static final String WRONG_RULE_DETECT = "Wrong rule string detected";
	public static final String ENABLE_INTERNET_CONNECTION = "Please enable internet connection";
	public static final String ERROR_IN_PLAYING_FILE =  "error in playing file ";
	public static final String CHECK_OUT_RIVET = "Check this out on Rivet Radio: ";
	public static final String FEEDBACK_RECIVER = "feedback@rivetnewsradio.com";
	public static final CharSequence POSTING_STATUS = "posting status , please wait...";
	public static final CharSequence STATUS_POSTED = "status posted successfully";
	public static final String UID = "UID";
	public static final String TITLE = "title";
	public static final String LINE_BACK = "linkBack";
	public static final String USER_MSG = "userMessage";
	public static final String PRIVACY = "privacy";
	public static final String SRC = "src";
	public static final String TYPE = "type";
	public static final String PUBLIC = "public";
	public static final String FRIENDS = "friends";
	public static final String PRIVATE = "private";
	public static final String MEDIA_ITEMS = "mediaItems";
	public static final String INTERNET_CONNECTION_ERROR = "Internet Connection Error";
	public static final String WROKING_INTERNET_CONNECTION = "Please connect to working Internet connection";
	public static final String ACCEPT = "Accept";
	public static final String CONTENT_TYPE = "Content-Type";
	
	

	

	// Application should restart after the specific delay
	public static int DELAY_IN_HOURS = 0 ;
	public static int storyInserted = 0 ;
	
	// flag for bosch flow if connected with bosch in login activities
	public static boolean BOSCH_CONNECTED_IN_LOGIN_ACTIVITIES = false ;
	public static boolean isControlEnable = false ;
	public static boolean shouldUpdateTimer = true ;
	
	
	// vast ads integration constants
	
	

	public final static String VAST_ADTAGURI_TAG = "VASTAdTagURI";

	public final static String VAST_START_TAG = "VAST";

	public final static String VAST_AD_TAG = "Ad";

	public final static String VAST_INLINE_TAG = "InLine";

	public final static String VAST_WRAPPER_TAG = "Wrapper";

	public final static String VAST_IMPRESSION_TAG = "Impression";

	public final static String VAST_CREATIVES_TAG = "Creatives";

	public final static String VAST_CREATIVE_TAG = "Creative";

	public final static String VAST_LINEAR_TAG = "Linear";

	public final static String VAST_DURATION_TAG = "Duration";

	public final static String VAST_TRACKINGEVENTS_TAG = "TrackingEvents";

	public final static String VAST_TRACKING_TAG = "Tracking";

	public final static String VAST_MEDIAFILES_TAG = "MediaFiles";

	public final static String VAST_MEDIAFILE_TAG = "MediaFile";

	public final static String VAST_VIDEOCLICKS_TAG = "VideoClicks";

	public final static String VAST_CLICKTHROUGH_TAG = "ClickThrough";

	public final static String VAST_CLICKTRACKING_TAG = "ClickTracking";
	
	public static final String VAST_ADS_SYSTEM_TAG = "AdSystem";
	
	public static final String VAST_ADS_TITLE_TAG = "AdTitle";
	
	public static final String VAST_COMPAIN_CLICK_THROUGH = "CompanionClickThrough" ;
	
	public static final String VAST_COMPAIN_ADS = "CompanionAds" ;
	
	public static final String VAST_COMPAIN = "Companion";
	
	public static final String VAST_STATIC_RESOURCE = "StaticResource";
	
	public static final String CLICKABLE_AD_URL = "clickableAdsURL";
	
	
	// broadcast actions intent
	public static final String DONT_SHOW_AD_BANNER = "dontShowAddBanner";
	public static final String SERVICE_STATE = "android.intent.action.SERVICE_STATE";
	public static final String OUTGOING_CALLS = "android.intent.action.NEW_OUTGOING_CALL";
	public static final String PHONE_STATE = "android.intent.action.PHONE_STATE";
	
	
	// service class constants
	public static final String ACTUAL_QUEUE_FLAG = "actualQueueFlag";
	public static final String DOWNLOAD_URL = "downloadUrl";
	public static final String PROGRESS = "progress";

	
	// current database version
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "RivetDB";
	public static final long SHOW_ERROR_TIME = 60000;
	public static final String WELCOME = "Welcome";
	public static final String ADVERTISEMENT_STRING = "AdvertisementXMLString";
	public static final long DELAY_TO_GET_ADS = 1000 * 60 * 2;
	public static final int ACCOUNT_EXISTS = 403043;
	
	

	
	

	
	public static boolean leftMenuLoaded = false ;
	public static boolean rightMenuLoaded=false;


}