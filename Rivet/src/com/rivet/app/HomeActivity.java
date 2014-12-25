package com.rivet.app;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import twitter4j.auth.AccessToken;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bosch.myspin.serversdk.MySpinException;
import com.bosch.myspin.serversdk.MySpinServerSDK;
import com.crittercism.app.Crittercism;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.ads.identifier.AdvertisingIdClient.Info;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.mobileapptracker.MobileAppTracker;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.adapter.CustomizeAdapter;
import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.alertviews.BoschIVIAlertView;
import com.rivet.app.common.ActivitiesUserApp;
import com.rivet.app.common.ConnectionDetector;
import com.rivet.app.common.HHRHelper;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.AdsBuilder;
import com.rivet.app.core.AlertDialogManager;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.IPodMusicFinder;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.core.pojo.Story;
import com.rivet.app.observer.RemoteControlReciever;
import com.rivet.app.social.EmailSender;
import com.rivet.app.social.FeedbackSender;
import com.rivet.app.social.PostUserAction;
import com.rivet.app.social.ShareOnFacebook;
import com.rivet.app.social.ShareOnFacebook.FacebookLoginTask;
import com.rivet.app.social.ShareOnTwitter;

public class HomeActivity extends BaseActivity implements View.OnClickListener,
		MySpinServerSDK.ConnectionStateListener {

	Context context = this;
	String TAG = "HomeActivity";
	ServiceDataReciever resultReciever;
	ImageButton playPauseBttn, rewindBttn;
	ProgressDialog wsProgress;
	private ImageView flyoutIv;
	public MobileAppTracker mobileAppTracker = null;
	int totalDuration = 0;
	boolean RecentlyDidAirplane = false;
	private ImageButton nextIB;
	private TextView titleTV;
	private TextView producerTV;
	private TextView categoryTV;
	private long appStartTime;
	private TextView bufferingTV;
	private ImageButton bookMarkBttn;
	public ImageView loaderIV = null;
	AnimationDrawable frameAnimation;
	private Animation rightMenuShows;
	private Animation rightMenuHides;
	private Animation rightMenuMainShows;
	private Animation rightMenuMainHides;

	private ProgressBar storyProgressBar;
	private TextView durationTV;
	private Typeface metaProNormFont;
	private RelativeLayout info_RL = null;
	private ListView customizeLV;
	private RelativeLayout child2RL;
	private RelativeLayout child1RL;
	private RelativeLayout child3Rl;
	private TextView accountSetting;
	private TextView categoriesTV;
	private TextView loginTV;
	private Typeface metaProTypeFace;
	@SuppressLint("UseSparseArrays")
	HashMap<Integer, Category> mapCategoryList = new HashMap<Integer, Category>();
	private CustomizeAdapter adapter;
	private CategoryDBAdapter categoryDBAdapter = null;
	private RelativeLayout childLoginSectionRL;
	private RelativeLayout childLogoutSectionRL;
	private ImageView dividerIV;
	private TextView emailIDTV;
	private TextView uploadedTimeTV;
	LayoutInflater inflater = null;
	View[] childrenPortrait = null;
	protected static AccessToken accessToken;
	private ShareOnTwitter shareOnTwitter;
	private RelativeLayout mainchildRL;
	private FrameLayout leftchildFL;
	private Animation mainGoes;
	private Animation mainChildRLComes;
	private Animation rightToLeftAnim;
	private Animation leftToRightAnim;
	private RelativeLayout parentRL;
	private StoryDBAdapter storyDbAdapter;
	private UserCallEventDetector callDetectReciever;
	private int checkedItemsNumber;
	private FrameLayout rightChildRl;
	private ImageView logoIv;
	private TextView lowConnectivityTV;
	private ImageView adsIV;
	private RelativeLayout childFeedBackRL;
	private RelativeLayout childTermsOfUseRL;
	private AudioManager mAudioManager;
	private ComponentName mRemoteControlResponder;
	private TextView warningIVI_TV;

	private boolean canPressButtons = false;
	private boolean shouldPlayWhenNewCategoriesSelected = false;
	private boolean shouldStayBannerOnScreen = true;
	private boolean ShouldPlayAfterCallDisconnect = true;
	private boolean flagAnimationLand = false;
	private boolean isLoadingStories = false;
	private boolean isPlayerPlaying = true;
	private boolean isNotONAirplaneMode = true;
	private boolean audioFocusLost = false;

	private ConnectionDetector connectionDetector = new ConnectionDetector(
			HomeActivity.this);

	private Timer adsTimer = null;
	private Timer timeToRestart = null;

	@Override
	protected void onStart() {
		super.onStart();

		// When this activity gets started register for mySPIN connection events
		// in order to
		// adapt views for the according connection state.
		try {
			MySpinServerSDK.sharedInstance().registerConnectionStateListener(this);
		} catch (MySpinException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_home_bosch);
		if (MySpinServerSDK.sharedInstance().isConnected()) {
			// code to lock the screen
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.activity_home_bosch);

		} else {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			setContentView(R.layout.activity_home);
		}

		// initialize receiver to detect phone call events
		callDetectReciever = new UserCallEventDetector();
		registerReceiverforCallStated();

		appStartTime = System.currentTimeMillis();

		// timer starts to restart application after 6 hours
		startAppRestartTimer();

		// thread starts to load image for fb sharing
		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap largeBitmap = ((BaseActivity) context).getBitmapFromURL(RConstants.FB_SHARE_IMAGE_URL);
				if (largeBitmap != null) {
					RConstants.sharingFBBitmap = Bitmap.createScaledBitmap(
							largeBitmap, 100, 80, true);
				}
			}
		}).start();

		// register crittercism sdk
		Crittercism.initialize(getApplicationContext(),RConstants.CRITTERCISM_SDK_KEY);

		// Mobile application tracking
		MobileAppTracker.init(getApplicationContext(),RConstants.ADVERTIZER_ID, RConstants.CONVERSION_KEY);
		mobileAppTracker = MobileAppTracker.getInstance();

		DownLoadTrackThread downLoadTrackThread = new DownLoadTrackThread();
		Thread downLoadTrack = new Thread(downLoadTrackThread);
		downLoadTrack.start();

		// this thread starts to pick songs from galary
		Thread songPickerThread = new Thread(new IPodeThread());
		songPickerThread.start();

		shareOnTwitter = new ShareOnTwitter(HomeActivity.this);
		storyDbAdapter = StoryDBAdapter.getStoryDBAdapter(HomeActivity.this);

		metaProNormFont = Typeface.createFromAsset(getAssets(),"MetaPro_Norm.otf");

		categoryDBAdapter = CategoryDBAdapter.getCategoryDBAdapterForRead(this);

		/****************************** Initialize the content manager ***********************/
		resultReciever = new ServiceDataReciever(null);

		ContentManager.getContentManager(this).getPlaylistManager().setResultReciever(resultReciever);

		if (!MySpinServerSDK.sharedInstance().isConnected()) {
			initAllUI();
		} else {
			initAllUIForBosch();
			resetCheckedItemSize();
			loadCategoriesInList();
		}

		// to handle mode portrait to landscape

		UIUpdateOnStoryLoadTask uIUpdateOnstoryLoadTask = new UIUpdateOnStoryLoadTask();
		uIUpdateOnstoryLoadTask.execute();

		// code to respect other audio source stop when they start
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		OnAudioFocus onAudioFocus = new OnAudioFocus(this);
		mAudioManager.requestAudioFocus(onAudioFocus,AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		// add ads in database after each 5 minutes
		executeAdsThread();

	}// onCreate Ends here

	private void terminateAdsThread() {

		if (adsTimer != null) {
			adsTimer.cancel();
		}

	}

	private void executeAdsThread() {

		AddAdsInDBTimer addAdsInDBTimer = new AddAdsInDBTimer();
		adsTimer = new Timer();
		adsTimer.schedule(addAdsInDBTimer, 100, RConstants.DELAY_TO_GET_ADS);

	}

	public void setupUI(int config) {

		if (config == Configuration.ORIENTATION_LANDSCAPE) {
			flagAnimationLand = true;
			setContentView(R.layout.activity_home_landscape);

		} else {
			flagAnimationLand = false;
			setContentView(R.layout.activity_home);
		}

		initAllUI();
		setAdapterInList();

	}

	private void initAllUI() {
		RConstants.leftMenuLoaded = false;
		RConstants.rightMenuLoaded = false;

		logoIv = (ImageView) findViewById(R.id.logoIV);
		logoIv.setOnClickListener(this);
		rightChildRl = (FrameLayout) findViewById(R.id.rl_right_menu);
		customizeLV = (ListView) findViewById(R.id.customizeLV);
		child2RL = (RelativeLayout) findViewById(R.id.child2RL);
		child1RL = (RelativeLayout) findViewById(R.id.child1RL);
		child3Rl = (RelativeLayout) findViewById(R.id.child3Rl);
		emailIDTV = (TextView) findViewById(R.id.emailIDTV);
		accountSetting = (TextView) findViewById(R.id.accountSetting);
		categoriesTV = (TextView) findViewById(R.id.categoriesTV);
		childLoginSectionRL = (RelativeLayout) findViewById(R.id.childLoginSectionRL);
		childLogoutSectionRL = (RelativeLayout) findViewById(R.id.childLogoutSectionRL);
		dividerIV = (ImageView) findViewById(R.id.dividerIV);
		childLogoutSectionRL.setOnClickListener(this);
		loginTV = (TextView) findViewById(R.id.loginTV);
		childFeedBackRL = (RelativeLayout) findViewById(R.id.childFeedBackRL);
		childTermsOfUseRL = (RelativeLayout) findViewById(R.id.childTermsOfUseRL);
		childFeedBackRL.setOnClickListener(this);
		childTermsOfUseRL.setOnClickListener(this);
		// sliding drawer functionality with animation
		mainchildRL = (RelativeLayout) findViewById(R.id.mainchildRL);
		leftchildFL = (FrameLayout) findViewById(R.id.leftChildFL);

		if (!flagAnimationLand) {
			mainChildRLComes = AnimationUtils.loadAnimation(context,
					R.anim.main_right_to_left);
			mainGoes = AnimationUtils.loadAnimation(context,
					R.anim.main_left_to_right);
		} else {
			mainChildRLComes = AnimationUtils.loadAnimation(context,
					R.anim.main_right_to_left_land);
			mainGoes = AnimationUtils.loadAnimation(context,
					R.anim.main_left_to_right_land);
		}
		mainGoes.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mainchildRL.setX(leftchildFL.getX() + leftchildFL.getWidth());
			}
		});

		rightToLeftAnim = AnimationUtils.loadAnimation(context,
				R.anim.left_menu_right_to_left);
		leftToRightAnim = AnimationUtils.loadAnimation(context,
				R.anim.left_menu_left_to_right);

		rightMenuShows = AnimationUtils.loadAnimation(context,
				R.anim.right_menu_shows);
		rightMenuHides = AnimationUtils.loadAnimation(context,
				R.anim.right_menu_hides);
		rightMenuMainHides = AnimationUtils.loadAnimation(context,
				R.anim.right_menu_main_hides);
		rightMenuMainHides.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				mainchildRL.setX(-(mainchildRL.getWidth() - (getScreenWidth() - rightChildRl
						.getWidth())));
			}
		});
		rightMenuMainShows = AnimationUtils.loadAnimation(context,
				R.anim.right_menu_main_shows);

		metaProTypeFace = Typeface.createFromAsset(getAssets(),
				"MetaPro_Book.otf");
		loginTV.setTypeface(metaProTypeFace);
		categoriesTV.setTypeface(metaProTypeFace);
		accountSetting.setTypeface(metaProTypeFace);

		child1RL.setOnClickListener(this);
		child2RL.setOnClickListener(this);
		child3Rl.setOnClickListener(this);

		customizeLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				Category category = (Category) parent
						.getItemAtPosition(position);
				boolean isChecked = category.getisChecked();

				if (category.getCategoryId() == RConstants.IPOD_MUSIC_CATEGORY) {
					if (isChecked) {
						category.setIsChecked(false);
						PostLogsForCategory postLogsForCategoryTask = new PostLogsForCategory(
								RConstants.ActivityCategoryDisable, category
										.getName());
						Thread postLogsForCategoryThread = new Thread(
								postLogsForCategoryTask);
						postLogsForCategoryThread.start();
					} else {
						category.setIsChecked(true);
						PostLogsForCategory postLogsForCategoryTask = new PostLogsForCategory(
								RConstants.ActivityCategoryEnable, category
										.getName());
						Thread postLogsForCategoryThread = new Thread(
								postLogsForCategoryTask);
						postLogsForCategoryThread.start();
					}
				} else {

					if (isChecked) {

						if (checkedItemsNumber == 1) {
							AlertDialogManager alertDialogManager = new AlertDialogManager();
							alertDialogManager.showAlertDialog(HomeActivity.this, "Can not diasable all categories", 
									"At least one category must be enabled", true);
//							alertDialogManager
//									.showAlertDialog(
//											HomeActivity.this,
//											"Rivet Radio",
//											"Can't deselect all categories , you must select one category",
//											true);
							
							
						} else {
							category.setIsChecked(false);
							// to decrease the size of checked categories
							decreaseCheckedItemSize();
							PostLogsForCategory postLogsForCategoryTask = new PostLogsForCategory(
									RConstants.ActivityCategoryDisable,
									category.getName());
							Thread postLogsForCategoryThread = new Thread(
									postLogsForCategoryTask);
							postLogsForCategoryThread.start();
						}
					} else {
						category.setIsChecked(true);
						// to increase the size of checked categories
						increaseCheckedItemSize();
						PostLogsForCategory postLogsForCategoryTask = new PostLogsForCategory(
								RConstants.ActivityCategoryEnable, category
										.getName());
						Thread postLogsForCategoryThread = new Thread(
								postLogsForCategoryTask);
						postLogsForCategoryThread.start();
					}
				}
				adapter.notifyDataSetChanged();
			}
		});

		resetCheckedItemSize();
		loadCategoriesInList();

		// initialize variables

		storyProgressBar = (ProgressBar) findViewById(R.id.storyProgressBar);
		parentRL = (RelativeLayout) findViewById(R.id.parent);
		titleTV = (TextView) findViewById(R.id.tv_title);
		producerTV = (TextView) findViewById(R.id.tv_producer);
		categoryTV = (TextView) findViewById(R.id.tv_category);
		bufferingTV = (TextView) findViewById(R.id.tv_buffering);
		uploadedTimeTV = (TextView) findViewById(R.id.tv_uploaded);
		playPauseBttn = (ImageButton) findViewById(R.id.ib_play_pause);
		bookMarkBttn = (ImageButton) findViewById(R.id.ib_bookmark);
		loaderIV = (ImageView) findViewById(R.id.animateloader_iv);
		adsIV = (ImageView) findViewById(R.id.adsIV);
		adsIV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Story currentStory = ContentManager
						.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory();
				int categoryID = currentStory.getParentCategoryID();
				if (categoryID == RConstants.ADS_CATEGORY) {

					String clickThroughURL = currentStory
							.getCompanionClickThroughUrl();

					if (clickThroughURL != null) {
						Intent openWebViewIntent = new Intent(
								HomeActivity.this, ShowMeAdsActivity.class);
						openWebViewIntent.putExtra(RConstants.CLICKABLE_AD_URL,
								clickThroughURL);
						startActivity(openWebViewIntent);
					}

				}

			}
		});

		durationTV = (TextView) findViewById(R.id.tv_timer);
		loaderIV.setBackgroundResource(R.drawable.loaderanimation);
		frameAnimation = (AnimationDrawable) loaderIV.getBackground();
		playPauseBttn.setOnClickListener(this);
		rewindBttn = (ImageButton) findViewById(R.id.ib_rewind);
		nextIB = (ImageButton) findViewById(R.id.ib_next);
		info_RL = (RelativeLayout) findViewById(R.id.rl_info);

		if (isPlayerPlaying) {
			playPauseBttn.setImageResource(R.drawable.button_pause);
		} else {
			playPauseBttn.setImageResource(R.drawable.button_play);
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			enlargeTitleBackgroundSpaceOrientation(true);
		} else {
			enlargeTitleBackgroundSpaceOrientation(false);
		}

		titleTV.setTypeface(metaProNormFont);
		nextIB.setOnClickListener(this);

		setContorlsColorForEnableMode();

		// registration for context menu for share button
		registerForContextMenu(bookMarkBttn);

		bookMarkBttn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if ((!RConstants.rightMenuLoaded)
						&& (ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().getCurrentStory() != null)) {

					if (connectionDetector.isConnectingToInternet()) {
						setContorlsColorForEnableMode();
						bookMarkBttn.getBackground().setAlpha(
								RConstants.PLAYER_CONTROL_ALPHA_DISABLE);
						if (ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().getCurrentStory() != null) {
							view.showContextMenu();
						} else {
							showToast(RConstants.NO_STORY_TO_SHARE);
						}
					} else {
						if (!MySpinServerSDK.sharedInstance().isConnected()) {
							AlertDialogManager alertDialogManager = new AlertDialogManager();
							alertDialogManager.showAlertDialog(
									HomeActivity.this, RConstants.RIVET_RADIO,
									RConstants.NO_INTERNET_CONNECTION, true);
						}
					}

				}
			}
		});

		rewindBttn.setOnClickListener(this);

		flyoutIv = (ImageView) findViewById(R.id.iv_menu);
		flyoutIv.setOnClickListener(HomeActivity.this);
		nextIB.setClickable(true);
		playPauseBttn.setClickable(true);
		rewindBttn.setClickable(true);
		bookMarkBttn.setClickable(true);

	}

	private void loadCategoriesInList() {

		Cursor cursor = categoryDBAdapter.getCategories();

		if (cursor.moveToFirst()) {
			do {

				Category category = new Category();

				category.setCategoryId(cursor.getInt(cursor.getColumnIndex(CategoryDBAdapter.CATEGORY_ID)));
				category.setName(cursor.getString(cursor.getColumnIndex(CategoryDBAdapter.CATEGORY_NAME)));
				// check weather is selected or not
				int exist = categoryDBAdapter.categoryExistInMyCategoryById(category);
				if (exist > 0) {
					category.setIsChecked(true);
					if (category.getCategoryId() != RConstants.IPOD_MUSIC_CATEGORY) {
						increaseCheckedItemSize();
					}
				} else {
					category.setIsChecked(false);
				}
				// do'nt show braking news , traffic and weather in category
				// list selection
				String categoryName = category.getName();

				if (categoryName.equals(RConstants.BREAKING_NEWS)|| categoryName.equals(RConstants.TRAFFIC_NEWS)|| categoryName.equals(RConstants.WEATHER_NEWS)) {
					continue;
				} else {
					addCategoriesSerialWise(category);
				}

			} while (cursor.moveToNext());
		}
		cursor.close();

		// code to show hardcoded Music category in list
		Category categoryMusic = new Category();
		categoryMusic.setName("Music from device");
		categoryMusic.setCategoryId(RConstants.IPOD_MUSIC_CATEGORY);
		addCategoriesSerialWise(categoryMusic);

		int exist = categoryDBAdapter.categoryExistInMyCategoryById(categoryMusic);
		if (exist > 0) {
			categoryMusic.setIsChecked(true);
		} else {
			categoryMusic.setIsChecked(false);
		}
		addCategoriesSerialWise(categoryMusic);
	}

	private void initAllUIForBosch() {

		logoIv = (ImageView) findViewById(R.id.logoIV);
		logoIv.setOnClickListener(this);

		lowConnectivityTV = (TextView) findViewById(R.id.tv_lowConnectivity);
		metaProTypeFace = Typeface.createFromAsset(getAssets(),"MetaPro_Book.otf");
		warningIVI_TV = (TextView) findViewById(R.id.warningIVI_TV);
		storyProgressBar = (ProgressBar) findViewById(R.id.storyProgressBar);
		parentRL = (RelativeLayout) findViewById(R.id.parent);
		titleTV = (TextView) findViewById(R.id.tv_title);
		producerTV = (TextView) findViewById(R.id.tv_producer);
		categoryTV = (TextView) findViewById(R.id.tv_category);
		bufferingTV = (TextView) findViewById(R.id.tv_buffering);
		uploadedTimeTV = (TextView) findViewById(R.id.tv_uploaded);
		playPauseBttn = (ImageButton) findViewById(R.id.ib_play_pause);
		bookMarkBttn = (ImageButton) findViewById(R.id.ib_bookmark);
		loaderIV = (ImageView) findViewById(R.id.animateloader_iv);
		durationTV = (TextView) findViewById(R.id.tv_timer);
		loaderIV.setBackgroundResource(R.drawable.loaderanimation);
		frameAnimation = (AnimationDrawable) loaderIV.getBackground();
		playPauseBttn.setOnClickListener(this);
		rewindBttn = (ImageButton) findViewById(R.id.ib_rewind);
		nextIB = (ImageButton) findViewById(R.id.ib_next);
		info_RL = (RelativeLayout) findViewById(R.id.rl_info);

		titleTV.setTypeface(metaProNormFont);
		nextIB.setOnClickListener(this);

		setContorlsColorForEnableMode();
		rewindBttn.setOnClickListener(this);
		nextIB.setClickable(true);
		playPauseBttn.setClickable(true);
		rewindBttn.setClickable(true);
		bookMarkBttn.setClickable(true);

	}// endof InitUIForBosch

	public void setContorlsColorForDisableMode() {
		playPauseBttn.getBackground().setAlpha(
				RConstants.PLAYER_CONTROL_ALPHA_DISABLE);
		rewindBttn.getBackground().setAlpha(
				RConstants.PLAYER_CONTROL_ALPHA_DISABLE);
		nextIB.getBackground()
				.setAlpha(RConstants.PLAYER_CONTROL_ALPHA_DISABLE);
		bookMarkBttn.getBackground().setAlpha(
				RConstants.PLAYER_CONTROL_ALPHA_DISABLE);

	}

	public void setContorlsColorForEnableMode() {

		playPauseBttn.getBackground().setAlpha(RConstants.PLAYER_CONTROL_ALPHA);
		rewindBttn.getBackground().setAlpha(RConstants.PLAYER_CONTROL_ALPHA);
		nextIB.getBackground().setAlpha(RConstants.PLAYER_CONTROL_ALPHA);
		bookMarkBttn.getBackground().setAlpha(RConstants.PLAYER_CONTROL_ALPHA);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		checkedItemsNumber = 0;

		// if we are connected to bosch , do not handle orientation change
		if (!MySpinServerSDK.sharedInstance().isConnected()) {
			if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				setupUI(newConfig.orientation);
				enlargeTitleBackgroundSpaceOrientation(false);

			} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
				setupUI(newConfig.orientation);
				enlargeTitleBackgroundSpaceOrientation(true);
			}

			runOnUiThread(new UpdateUi(
					RConstants.UPDATE_UI_ON_ORIENTATION_CHANGED_ACTION));

		}

	}

	private int getScreenHeight() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		return displaymetrics.heightPixels;
	}

	public void enlargeTitleBackgroundSpaceOrientation(boolean isPortriat) {
		int screenHeight = getScreenHeight();
		int titleBarHeight;
		int actualTitleHeight;
		if (isPortriat) {

			if (getScreenWidth() <= 480) {
				titleBarHeight = (screenHeight * 40) / 100;
			} else {
				titleBarHeight = (screenHeight * 35) / 100;
			}
			actualTitleHeight = (titleBarHeight * 42) / 100;

			titleTV.setGravity(Gravity.TOP);

		} else {

			if (getScreenWidth() <= 480) {
				titleBarHeight = (screenHeight * 38) / 100;
			} else {
				titleBarHeight = (screenHeight * 34) / 100;
			}
			actualTitleHeight = (titleBarHeight * 75) / 100;

		}

		info_RL.getLayoutParams().height = titleBarHeight-10;
		info_RL.requestLayout();

		titleTV.getLayoutParams().height = actualTitleHeight+10;
		titleTV.requestLayout();

	}

	private int getScreenWidth() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	public void addCategoriesSerialWise(Category category) {

		if (category.getCategoryId() == RConstants.TOP_NEWS_CATEGORY) {
			mapCategoryList.put(0, category);
		} else if (category.getCategoryId() == RConstants.POLITICS_CATEGORY) {
			mapCategoryList.put(1, category);
		} else if (category.getCategoryId() == RConstants.BUSINESS_CATEGORY) {
			mapCategoryList.put(2, category);
		} else if (category.getCategoryId() == RConstants.SPORTS_CATEGORY) {
			mapCategoryList.put(3, category);
		} else if (category.getCategoryId() == RConstants.ENTERTAINMENT_CATEGORY) {
			mapCategoryList.put(4, category);
		} else if (category.getCategoryId() == RConstants.TECHNOLOGY_CATEGORY) {
			mapCategoryList.put(5, category);
		} else if (category.getCategoryId() == RConstants.SCIENCE_CATEGORY) {
			mapCategoryList.put(6, category);
		} else if (category.getCategoryId() == RConstants.LIFE_STYLE_CATEGORY) {
			mapCategoryList.put(7, category);
		} else if (category.getCategoryId() == RConstants.CRIME_AND_COURT) {
			mapCategoryList.put(8, category);
		} else if (category.getCategoryId() == RConstants.IPOD_MUSIC_CATEGORY) {
			mapCategoryList.put(9, category);
		}

	}

	public void updateCategories() {

		if (MySpinServerSDK.sharedInstance().isConnected()) {

			// don't get runtimeException here run this code on UI thread
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							warningIVI_TV.setVisibility(View.GONE);
							break;
						} catch (RuntimeException runtimeException) {
							runtimeException.printStackTrace();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			});
		}

		// need to check if nostorytoplay is on then set
		if (ContentManager.getContentManager(HomeActivity.this).getFlagNoStoryToPlay() && !RConstants.leftMenuLoaded) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if (ContentManager.getContentManager(HomeActivity.this).getPlaylistManager().getWholeQueueOfStories().size() == 0) {
						ContentManager.getContentManager(HomeActivity.this).setFlagNoStoryTOPlay(false);
						ContentManager.getContentManager(HomeActivity.this).updatePlayListForCurrentRule();
					}
				}
			}).start();

		}
		// don't execute whole this code if bosch connected
		if (!MySpinServerSDK.sharedInstance().isConnected()) {

			SaveCategoryInDatabaseTask saveCategoryInDatabaseTask = new SaveCategoryInDatabaseTask();
			Thread saveCategoryInDatabaseThread = new Thread(
					saveCategoryInDatabaseTask);
			saveCategoryInDatabaseThread.start();

			try {
				saveCategoryInDatabaseThread.join();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}

			// check for stories already in playlist queue are from selected
			// categories
			for (int storyIndx = 0; storyIndx < ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getWholeQueueOfStories().size(); storyIndx++) {
				int parentCategoryId = ContentManager
						.getContentManager(HomeActivity.this)
						.getPlaylistManager().getWholeQueueOfStories()
						.get(storyIndx).getParentCategoryID();
				String storyCategoryName = ContentManager
						.getContentManager(HomeActivity.this)
						.getPlaylistManager().getWholeQueueOfStories()
						.get(storyIndx).getCategoryName();
				Category categoryExistOrNot = new Category();
				categoryExistOrNot.setCategoryId(parentCategoryId);
				int existOrNot = categoryDBAdapter
						.categoryExistInMyCategoryById(categoryExistOrNot);
				if (storyCategoryName.equals(RConstants.BREAKING_NEWS)
						|| storyCategoryName.equals(RConstants.TRAFFIC_NEWS)
						|| storyCategoryName.equals(RConstants.WEATHER_NEWS)) {
					continue;
				} else {
					if (existOrNot == 0) {
						ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().getWholeQueueOfStories()
								.remove(storyIndx);

					}
				}
			}

			if (!RConstants.leftMenuLoaded
					&& shouldPlayWhenNewCategoriesSelected) {
				// execute this code when new categories selected and no new
				// stories to play
				setFixTextTitle();

				Thread startNextStoryThread = new Thread(new Runnable() {

					@Override
					public void run() {
						// play next audio
						// download the stream and call next.
						if (ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().getPlaylistCount() < RConstants.MINIMUM_STORIES_TO_PLAY) {
							ContentManager
									.getContentManager(HomeActivity.this)
									.moveToNextRuleAndUpdateStoriesForCurrentRule();

						}
						if (!ContentManager
								.getContentManager(HomeActivity.this)
								.getFlagNoStoryToPlay()) {
							ContentManager.getContentManager(HomeActivity.this)
									.playNewStory();
							shouldPlayWhenNewCategoriesSelected = false;

						} else {
							runOnUiThread(new Runnable() {
								public void run() {

									if (!MySpinServerSDK.sharedInstance()
											.isConnected()) {

										if (!RecentlyDidAirplane) {
											AlertDialogManager alertDialogManager = new AlertDialogManager();
											alertDialogManager
													.showAlertDialog(
															HomeActivity.this,
															RConstants.RIVET_RADIO,
															RConstants.NO_STORY_TO_PLAY,
															true);

											shouldPlayWhenNewCategoriesSelected = true;
										}
									} else {

										if (!RecentlyDidAirplane) {
											// set view visible to give warning
											// message on Bosch UI
											shouldPlayWhenNewCategoriesSelected = true;
											new BoschIVIAlertView()
													.showAlertDialog(
															HomeActivity.this,
															2);
										}
									}

									nextIB.getBackground().setAlpha(
											RConstants.PLAYER_CONTROL_ALPHA);
									nextIB.setClickable(true);
									rewindBttn.setClickable(true);
									playPauseBttn.setClickable(true);
								}
							});
						}

					}
				});

				startNextStoryThread.start();
			}

		}

	}

	private void startAppRestartTimer() {
		if (timeToRestart == null) {
			RestartApplicationByTimer restartApp = new RestartApplicationByTimer();
			timeToRestart = new Timer();
			timeToRestart.schedule(restartApp, 100, 1000 * 60 * 15);
		}
	}

	private void cancelAppRestartTimer() {

		if (timeToRestart != null) {
			timeToRestart.cancel();
			timeToRestart = null;
		}

	}

	private void registerBluetoothButtoncontorlReceiverRegister() {
		mRemoteControlResponder = new ComponentName(getPackageName(),
				RemoteControlReciever.class.getName());

		mAudioManager.registerMediaButtonEventReceiver(mRemoteControlResponder);

	}

	private void registerReceiverforCallStated() {

		IntentFilter filterFirst = new IntentFilter();
		filterFirst.addAction(RConstants.SERVICE_STATE);
		filterFirst.addAction(RConstants.PHONE_STATE);
		filterFirst.addAction(RConstants.OUTGOING_CALLS);
		filterFirst.addAction(RConstants.DONT_SHOW_AD_BANNER);

		registerReceiver(callDetectReciever, filterFirst);

	}

	private void setAdapterInList() {

		Map<Integer, Category> maplist = new TreeMap<Integer, Category>(
				mapCategoryList);
		// if user logged in then show user's userName with Log out button
		showLoginOrLogout();
		adapter = new CustomizeAdapter(HomeActivity.this, maplist,
				R.layout.row_customize, metaProTypeFace);
		customizeLV.setAdapter(adapter);

	}

	public void showLoginOrLogout() {

		// int loginEmailCount = userInfoDbAdapter.getUserInfoCount();
		boolean isLoginWithGmail = prefStore.getBooleanData(
				RConstants.IS_LOGIN_WITH_GMAIL, false);
		boolean isLoginWithFB = prefStore.getBooleanData(
				RConstants.FB_USER_IS_LOGIN, false);
		if (isLoginWithGmail) {

			childLogoutSectionRL.setVisibility(View.VISIBLE);
			childLoginSectionRL.setVisibility(View.VISIBLE);
			dividerIV.setVisibility(View.VISIBLE);
			child1RL.setVisibility(View.GONE);
			emailIDTV.setText(prefStore.getStringData(
					RConstants.GMAIL_USER_NAME, null));

		} else if (isLoginWithFB) {

			childLogoutSectionRL.setVisibility(View.VISIBLE);
			childLoginSectionRL.setVisibility(View.VISIBLE);
			dividerIV.setVisibility(View.VISIBLE);
			child1RL.setVisibility(View.GONE);

			emailIDTV.setText(prefStore.getStringData(RConstants.FB_EMIAL_ID,
					null));

		}

	}

	public int selectedTextColor() {

		return R.color.black_background;
	}

	public int unSelectedTextColor() {

		return R.color.unselected_text_color;
	}

	@Override
	public void onClick(View v) {

		if (v != null) {
			switch (v.getId()) {
			case R.id.logoIV:

				if (!RConstants.leftMenuLoaded) {
					if (RConstants.rightMenuLoaded) {
						RConstants.rightMenuLoaded = false;
						rightChildRl.setVisibility(View.GONE);
						rightChildRl.setAnimation(rightMenuHides);
						rightChildRl.animate();
						mainchildRL.setAnimation(rightMenuMainShows);
						mainchildRL.animate();
						rightMenuHides.start();
						rightMenuMainShows.start();
						mainchildRL.setX(0);
						rewindBttn.setClickable(true);
						bookMarkBttn.setClickable(true);
					} else {
						RConstants.rightMenuLoaded = true;
						rewindBttn.setClickable(false);
						bookMarkBttn.setClickable(false);
						rightChildRl.setVisibility(View.VISIBLE);
						rightChildRl.setAnimation(rightMenuShows);
						rightChildRl.animate();
						mainchildRL.setAnimation(rightMenuMainHides);
						mainchildRL.animate();
						rightMenuShows.start();
						rightMenuMainHides.start();

						// post logs on server if user watching about the
						// application
						postRareLogsToServerAndFlurry(RConstants.ActivityAboutApp);
					}
				}
				break;

			case R.id.childTermsOfUseRL:

				Intent browserIntent = new Intent(Intent.ACTION_VIEW,
						Uri.parse(RConstants.TERMS_OF_USE_URL));
				startActivity(browserIntent);

				break;

			case R.id.childFeedBackRL:

				FeedbackSender feedbackSender = new FeedbackSender(
						HomeActivity.this);
				feedbackSender.sendFeedBack();

				break;
			case R.id.iv_menu:

				if (!RConstants.rightMenuLoaded) {
					if (RConstants.leftMenuLoaded) {
						RConstants.leftMenuLoaded = false;
						nextIB.setClickable(true);
						playPauseBttn.setClickable(true);
						leftchildFL.setVisibility(View.GONE);
						leftchildFL.setAnimation(rightToLeftAnim);
						leftchildFL.animate();
						mainchildRL.setAnimation(mainChildRLComes);
						mainchildRL.animate();
						rightToLeftAnim.start();
						mainChildRLComes.start();
						mainchildRL.setX(0);
					} else {
						RConstants.leftMenuLoaded = true;
						nextIB.setClickable(false);
						playPauseBttn.setClickable(false);
						leftchildFL.setVisibility(View.VISIBLE);
						leftchildFL.setAnimation(leftToRightAnim);
						leftchildFL.animate();
						mainchildRL.setAnimation(mainGoes);
						mainchildRL.animate();
						leftToRightAnim.start();
						mainGoes.start();
					}
					new Thread(new Runnable() {

						@Override
						public void run() {
							// update category and send logs to flurry
							postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityCustomizeTab);
							updateCategories();
						}
					}).start();
					showLoginOrLogout();
				}
				break;

			case R.id.ib_play_pause:

				if (canPressButtons
						&& connectionDetector.isConnectingToInternet()) {

					setContorlsColorForDisableMode();

					isNotONAirplaneMode = true;

					if (isPlayerPlaying) {
						isPlayerPlaying = false;
						ShouldPlayAfterCallDisconnect = false;
						audioFocusLost = false;
						setContorlsColorForEnableMode();
						playPauseBttn.setImageResource(R.drawable.button_play);
						new Thread(new Runnable() {

							@Override
							public void run() {
								postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityPlaybackPlay);
							}
						}).start();
						// cancel app Restart Timer
						cancelAppRestartTimer();
					} else {
						isPlayerPlaying = true;
						ShouldPlayAfterCallDisconnect = true;
						setContorlsColorForEnableMode();
						playPauseBttn.setImageResource(R.drawable.button_pause);

						// thread starts to send logs on flurry
						new Thread(new Runnable() {

							@Override
							public void run() {
								postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityPlaybackPaused);
							}
						}).start();

						startAppRestartTimer();
					}

					ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().play();

				}

				break;
			case R.id.ib_rewind:

				if (canPressButtons) {

					isNotONAirplaneMode = true;

					frameAnimation.start();
					rewindBttn.getBackground().setAlpha(
							RConstants.PLAYER_CONTROL_ALPHA_DISABLE);
					nextIB.setClickable(false);
					rewindBttn.setClickable(false);
					playPauseBttn.setClickable(false);
					setFixTextTitle();

					int rewindPositionOfTrack = RConstants.currentPosOfAudioPlayer
							- RConstants.REWIND_DURATION_PLAYER;
					ContentManager.getContentManager(HomeActivity.this)
							.rewindStoryAndPlay(rewindPositionOfTrack);

					new Thread(new Runnable() {

						@Override
						public void run() {
							postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityPlaybackRewind);

						}
					}).start();

					// start restart app timer if not running
					startAppRestartTimer();
					// remove alert message that displays no stories to play in
					// bosch view
					if (MySpinServerSDK.sharedInstance().isConnected()) {
						warningIVI_TV.setVisibility(View.GONE);
					}

				}
				break;
			case R.id.child1RL:

				updateCategories();

				Intent logInOptionsIntent = new Intent(HomeActivity.this,
						LoginOptionsActivity.class);
				startActivity(logInOptionsIntent);

				break;

			case R.id.child2RL:
				updateCategories();

				break;

			case R.id.childLogoutSectionRL:

				LoginModel loginModel = new LoginModel(HomeActivity.this);
				boolean isLoginWithGmail = prefStore.getBooleanData(
						RConstants.IS_LOGIN_WITH_GMAIL, false);
				if (isLoginWithGmail) {

					loginModel.logout();

				} else {

					loginModel.fbLogout(prefStore.getStringData(
							RConstants.FB_UID, null));

				}
				// post logs when user logs out
				postRareLogsToServerAndFlurry(RConstants.ActivityUserLogout);

				break;

			case R.id.child3Rl:
				updateCategories();

				break;

			case R.id.ib_next:

				if (connectionDetector.isConnectingToInternet()) {

					if (MySpinServerSDK.sharedInstance().isConnected()) {
						warningIVI_TV.setVisibility(View.GONE);
					}

					if (canPressButtons) {

						// start loader animation
						nextIB.setClickable(false);
						rewindBttn.setClickable(false);
						playPauseBttn.setClickable(false);
						nextIB.getBackground().setAlpha(
								RConstants.PLAYER_CONTROL_ALPHA);
						nextIB.getBackground().setAlpha(
								RConstants.PLAYER_CONTROL_ALPHA_DISABLE);

						setFixTextTitle();

						Thread startNextStoryThread = new Thread(
								new Runnable() {

									@Override
									public void run() {
										// play next audio
										// download the stream and call next.
										if (ContentManager
												.getContentManager(
														HomeActivity.this)
												.getPlaylistManager()
												.getPlaylistCount() < RConstants.MINIMUM_STORIES_TO_PLAY) {
											ContentManager
													.getContentManager(
															HomeActivity.this)
													.moveToNextRuleAndUpdateStoriesForCurrentRule();

										}
										if (!ContentManager.getContentManager(
												HomeActivity.this)
												.getFlagNoStoryToPlay()) {
											// ads banner should stay on screen
											// to the full duration of ads story
											if (ContentManager
													.getContentManager(
															HomeActivity.this)
													.getPlaylistManager()
													.getCurrentStory()
													.getPrimaryCategory() == RConstants.ADS_CATEGORY) {
												shouldStayBannerOnScreen = true;
											} else {
												shouldStayBannerOnScreen = false;
											}
											// play new story
											ContentManager.getContentManager(
													HomeActivity.this)
													.playNewStory();

										} else {
											runOnUiThread(new Runnable() {
												public void run() {

													if (!MySpinServerSDK
															.sharedInstance()
															.isConnected()) {

														if (!RecentlyDidAirplane) {
															AlertDialogManager alertDialogManager = new AlertDialogManager();
															alertDialogManager
																	.showAlertDialog(
																			HomeActivity.this,
																			RConstants.RIVET_RADIO,
																			RConstants.NO_STORY_TO_PLAY,
																			true);
														}
													} else {

														if (!RecentlyDidAirplane) {
															// set view visible
															// to give warning
															// message on Bosch
															// UI
															new BoschIVIAlertView()
																	.showAlertDialog(
																			HomeActivity.this,
																			2);
														}
													}

													nextIB.getBackground()
															.setAlpha(
																	RConstants.PLAYER_CONTROL_ALPHA);
													nextIB.setClickable(true);
													rewindBttn
															.setClickable(true);
													playPauseBttn
															.setClickable(true);
												}
											});
										}

									}
								});

						startNextStoryThread.start();

					}
					// start application restart timer if not running
					startAppRestartTimer();
				} else {

					// pause the story player if not connected with Internet
					if (ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().isPlaying()) {
						isPlayerPlaying = false;
						ShouldPlayAfterCallDisconnect = false;
						audioFocusLost = false;
						setContorlsColorForEnableMode();
						playPauseBttn.setImageResource(R.drawable.button_play);
						ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().play();
					}

					if (MySpinServerSDK.sharedInstance().isConnected()) {

						// set view visible to give warning message on Bosch UI
						new BoschIVIAlertView().showAlertDialog(
								HomeActivity.this, 1);

					} else {
						AlertDialogManager alertDialogManager = new AlertDialogManager();
						alertDialogManager.showAlertDialog(HomeActivity.this,
								RConstants.RIVET_RADIO,
								RConstants.NO_INTERNET_CONNECTION, true);
					}
				}

				break;

			}
		}
	}

	private void setFixTextTitle() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			String storyTitle = ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getCurrentStory().getTitle();
			if (storyTitle != null) {
				if (!storyTitle.isEmpty()) {
					if (storyTitle.length() <= 30) {
						titleTV.setGravity(Gravity.CENTER_VERTICAL);
					} else {
						titleTV.setGravity(Gravity.NO_GRAVITY);
					}
				}
			}
		}
	}

	public boolean isLoadingStories() {
		return isLoadingStories;
	}

	public void setLoadingStories(boolean isLoadingStories) {
		this.isLoadingStories = isLoadingStories;
	}

	@Override
	public void onBackPressed() {
		// do nothing eat it
		moveTaskToBack(true);
		cancelAppRestartTimer();
	}

	@Override
	protected void onResume() {

		super.onResume();

		// close the login panel after launching the activity
		if (RConstants.leftMenuLoaded) {
			RConstants.leftMenuLoaded = false;
			nextIB.setClickable(true);
			playPauseBttn.setClickable(true);
			leftchildFL.setVisibility(View.GONE);
			leftchildFL.setAnimation(rightToLeftAnim);
			leftchildFL.animate();
			mainchildRL.setAnimation(mainChildRLComes);
			mainchildRL.animate();
			rightToLeftAnim.start();
			mainChildRLComes.start();
			mainchildRL.setX(0);
		}

		if (RConstants.rightMenuLoaded) {
			RConstants.rightMenuLoaded = false;
			rightChildRl.setVisibility(View.GONE);
			rightChildRl.setAnimation(rightMenuHides);
			rightChildRl.animate();
			mainchildRL.setAnimation(rightMenuMainShows);
			mainchildRL.animate();
			rightMenuHides.start();
			rightMenuMainShows.start();
			mainchildRL.setX(0);
			rewindBttn.setClickable(true);
			bookMarkBttn.setClickable(true);
		}

		if (audioFocusLost) {
			// code to respect other audio source stop when they starts
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

			OnAudioFocus onAudioFocus = new OnAudioFocus(this);
			mAudioManager.requestAudioFocus(onAudioFocus,
					AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

			if (!ContentManager.getContentManager(HomeActivity.this)
					.getPlaylistManager().isPlaying()
					&& canPressButtons) {
				ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().play();
			}
		}

		if (!MySpinServerSDK.sharedInstance().isConnected()) {

			initAllUI();
			setAdapterInList();
		} else if (MySpinServerSDK.sharedInstance().isConnected()
				&& RConstants.BOSCH_CONNECTED_IN_LOGIN_ACTIVITIES) {
			// this code handle the flow if bosch is been connected in Login
			// Activities
			updateBoschView();
		}
		durationTV.setText("-0:00");
		// Get source of open for app re-engagement
		mobileAppTracker.setReferralSources(this);
		// MAT will not function unless the measureSession call is included
		mobileAppTracker.measureSession();

		// handle the callback for bluetooth devices buttons
		registerBluetoothButtoncontorlReceiverRegister();

		// start Restart Timer again
		startAppRestartTimer();

		// post logs when application comes in foreground
		postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityAppInForeground);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// cancel restart timer if app goes in background
		updateCategories();
		cancelAppRestartTimer();

		// post logs while app goes in background

		postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityAppInBackground);
	}

	class DownloadStoryUrlThread extends Thread {
		public void run() {

			if (ContentManager.getContentManager(HomeActivity.this)
					.getPlaylistManager().getCurrentStory() != null) {
				ContentManager.getContentManager(HomeActivity.this)
						.playFirstStory();
			}
		}
	}

	class ServiceDataReciever extends ResultReceiver {

		public ServiceDataReciever(Handler handler) {
			super(handler);

		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {

			super.onReceiveResult(resultCode, resultData);

			if (resultCode == RConstants.resultIsPause) {

				runOnUiThread(new UpdateUi(RConstants.SHOW_PLAY_BUTTON));

			} else if (resultCode == RConstants.SEND_NOTIFICATION_WHEN_INTERNET_CONNECTION_LOW) {

				runOnUiThread(new UpdateUi(
						RConstants.SEND_NOTIFICATION_WHEN_INTERNET_CONNECTION_LOW));

			} else if (resultCode == RConstants.BLUETOOTH_META_CHANGE) {

				// update bluetooth interface

				bluetoothNotifyChange(RConstants.META_CHANGED);

			} else if (resultCode == RConstants.resultIsPlay) {

				runOnUiThread(new UpdateUi(RConstants.SHOW_PAUSE_BUTTON));

			} else if (resultCode == RConstants.resultIsProgress) {

				RConstants.currentPosOfAudioPlayer = resultData
						.getInt(RConstants.currentPosition);
				runOnUiThread(new UpdateUi(RConstants.resultIsProgress));

			} else if (resultCode == RConstants.UPDATE_UI_NEXT_ENABLE_ACTION) {

				totalDuration = resultData.getInt(RConstants.audioDuration);
				runOnUiThread(new UpdateUi(
						RConstants.UPDATE_UI_NEXT_ENABLE_ACTION));

			} else if (resultCode == RConstants.UPDATE_UI_ACTION) {

				runOnUiThread(new UpdateUi(RConstants.UPDATE_UI_ACTION));

			} else if (resultCode == RConstants.UPDATE_UI_UNLOCK_ACTION) {

				runOnUiThread(new UpdateUi(RConstants.UPDATE_UI_UNLOCK_ACTION));

			} else if (resultCode == RConstants.UPDATE_UI_REWIND_ACTION) {

				runOnUiThread(new UpdateUi(RConstants.UPDATE_UI_REWIND_ACTION));

			} else if (resultCode == RConstants.START_NEW_STORY) {

				if (!MySpinServerSDK.sharedInstance().isConnected()) {

					adsIV.setVisibility(View.GONE);
				}
				uploadedTimeTV.setVisibility(View.VISIBLE);
				producerTV.setVisibility(View.VISIBLE);

				// set story like false and post logs to server and flurry

				if (connectionDetector.isConnectingToInternet()) {

					if (MySpinServerSDK.sharedInstance().isConnected()) {
						warningIVI_TV.setVisibility(View.GONE);
					}

					postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityStoryEnded);
					postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityNewStoryStarted);
					if (ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory() != null) {
						// set title text fit
						runOnUiThread(new Runnable() {
							public void run() {
								setFixTextTitle();
							}
						});
					}
					runOnUiThread(new UpdateUi(RConstants.UPDATE_UI_ACTION));
					ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().setPlaying(false);
					if (ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().getPlaylistCount() < RConstants.MINIMUM_STORIES_TO_PLAY) {
						ContentManager.getContentManager(HomeActivity.this)
								.moveToNextRuleAndUpdateStoriesForCurrentRule();

					}
					// download the audio stream and then call next. //we need
					// to handle if no story to play
					// if no new stories to play then don't call this method

					if ((!ContentManager.getContentManager(HomeActivity.this)
							.getFlagNoStoryToPlay())) {
						ContentManager.getContentManager(HomeActivity.this)
								.playNewStory();
						// ads banner should stay on screen to the full duration
						// of ads story
						if (ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().getCurrentStory()
								.getPrimaryCategory() == RConstants.ADS_CATEGORY) {
							shouldStayBannerOnScreen = true;
						} else {
							shouldStayBannerOnScreen = false;
						}
						shouldPlayWhenNewCategoriesSelected = false;
					} else {

						if (MySpinServerSDK.sharedInstance().isConnected()) {
							if (!RecentlyDidAirplane) {
								// set view visible to give warning message on
								// Bosch UI
								new BoschIVIAlertView().showAlertDialog(
										HomeActivity.this, 2);

								shouldPlayWhenNewCategoriesSelected = true;

							}
						} else {

							if (!RecentlyDidAirplane) {
								AlertDialogManager alertDialogManager = new AlertDialogManager();
								alertDialogManager.showAlertDialog(
										HomeActivity.this,
										RConstants.RIVET_RADIO,
										RConstants.NO_STORY_TO_PLAY, true);
							}

							shouldPlayWhenNewCategoriesSelected = true;

						}

					}

				} else {

					if (MySpinServerSDK.sharedInstance().isConnected()) {

						// set view visible to give warning message on Bosch UI

						new BoschIVIAlertView().showAlertDialog(
								HomeActivity.this, 1);

					} else {
						AlertDialogManager alertDialogManager = new AlertDialogManager();
						alertDialogManager.showAlertDialog(HomeActivity.this,
								RConstants.RIVET_RADIO,
								RConstants.NO_INTERNET_CONNECTION, true);
					}
				}

				RecentlyDidAirplane = false;

			}

		}

	}

	class UpdateUi implements Runnable {

		int whatIsUpdate = 0;

		public UpdateUi(int whatIsUpdate) {
			this.whatIsUpdate = whatIsUpdate;
		}

		@Override
		public void run() {

			if (whatIsUpdate == RConstants.SHOW_PAUSE_BUTTON) {

				ShouldPlayAfterCallDisconnect = true;
				playPauseBttn.setImageResource(R.drawable.button_pause);
				nextIB.setClickable(true);
				playPauseBttn.setClickable(true);
				rewindBttn.setClickable(true);

			} else if (whatIsUpdate == RConstants.SEND_NOTIFICATION_WHEN_INTERNET_CONNECTION_LOW) {

				// show notification to user if Internet connection is low
				if (!MySpinServerSDK.sharedInstance().isConnected()) {
					showToast("Internet connection is low ");
				} else {
					lowConnectivityTV.setVisibility(View.VISIBLE);
				}

			} else if (whatIsUpdate == RConstants.SEND_NOTIFICATION_WHEN_STRONG_INTERNET_CONNECTION) {

				lowConnectivityTV.setVisibility(View.GONE);

			} else if (whatIsUpdate == RConstants.SHOW_PLAY_BUTTON) {
				nextIB.setClickable(true);
				playPauseBttn.setClickable(true);
				rewindBttn.setClickable(true);

				loaderIV.setVisibility(View.VISIBLE);

				playPauseBttn.setImageResource(R.drawable.button_play);
				// we need to disable buttons

			} else if (whatIsUpdate == RConstants.UPDATE_UI_ACTION_DISABLE_INPUT) {

				nextIB.setClickable(false);
				playPauseBttn.setClickable(false);
				playPauseBttn.setImageResource(R.drawable.button_pause);
				rewindBttn.setClickable(false);
				bookMarkBttn.setClickable(false);

			} else if (whatIsUpdate == RConstants.UPDATE_UI_WITH_NO_STORY_ACTION) {

				if (!RecentlyDidAirplane) {
					if (MySpinServerSDK.sharedInstance().isConnected()) {
						// set view visible to give warning message on Bosch UI
						new BoschIVIAlertView().showAlertDialog(
								HomeActivity.this, 2);
					} else {

						AlertDialogManager alertDialogManager = new AlertDialogManager();
						alertDialogManager.showAlertDialog(HomeActivity.this,
								RConstants.RIVET_RADIO,
								RConstants.NO_STORY_TO_PLAY, true);
					}
				}
			} else if (whatIsUpdate == RConstants.UPDATE_UI_ACTION_ENABLE_INPUT) {

				nextIB.setClickable(true);
				playPauseBttn.setClickable(true);
				rewindBttn.setClickable(true);
				playPauseBttn.setImageResource(R.drawable.button_play);
				bookMarkBttn.setClickable(true);

			} else if (whatIsUpdate == RConstants.resultIsProgress) {

				storyProgressBar.setMax(totalDuration);
				storyProgressBar
						.setProgress(RConstants.currentPosOfAudioPlayer);

				if (RConstants.shouldUpdateTimer) {
					if (totalDuration == RConstants.currentPosOfAudioPlayer) {
						durationTV.setText("-0:00");
					} else {
						durationTV
								.setText(convertMillis((long) (totalDuration - RConstants.currentPosOfAudioPlayer)));
					}
				} else {
					durationTV.setText("-0:00");
				}

			} else if (whatIsUpdate == RConstants.UPDATE_UI_NEXT_ENABLE_ACTION) {

				ShouldPlayAfterCallDisconnect = true;
				storyProgressBar.setMax(totalDuration);
				playPauseBttn.setImageResource(R.drawable.button_pause);
				loaderIV.setVisibility(View.INVISIBLE);

				isPlayerPlaying = true;

				if (frameAnimation.isRunning()) {
					frameAnimation.stop();
				}

				playPauseBttn.setClickable(true);
				rewindBttn.setClickable(true);
				nextIB.setClickable(true);

				setContorlsColorForEnableMode();

			} else if (whatIsUpdate == RConstants.UPDATE_UI_UNLOCK_ACTION) {
				setContorlsColorForEnableMode();
				nextIB.setClickable(true);
				playPauseBttn.setClickable(true);
				rewindBttn.setClickable(true);
				playPauseBttn.setImageResource(R.drawable.button_pause);
				ShouldPlayAfterCallDisconnect = true;
				loaderIV.setVisibility(View.INVISIBLE);

				isPlayerPlaying = true;

				if (frameAnimation.isRunning()) {
					frameAnimation.stop();
				}

			} else if (whatIsUpdate == RConstants.UPDATE_UI_REWIND_ACTION) {

				nextIB.setClickable(true);
				playPauseBttn.setClickable(true);
				rewindBttn.setClickable(true);
				playPauseBttn.setImageResource(R.drawable.button_pause);
				ShouldPlayAfterCallDisconnect = true;
				loaderIV.setVisibility(View.INVISIBLE);

				isPlayerPlaying = true;

				if (frameAnimation.isRunning()) {
					frameAnimation.stop();
				}

				setContorlsColorForEnableMode();

			} else if (whatIsUpdate == RConstants.UPDATE_UI_ACTION) {

				nextIB.setClickable(false);
				playPauseBttn.setClickable(false);
				rewindBttn.setClickable(false);

				if (ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory() != null) {

					setFixTextTitle();

					String catName = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getCategoryName();
					int categoryId = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getParentCategoryID();
					String title = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory().getTitle();

					// set title tv font size
					setTitleFontSize(title);
					// notify bluetooth interface every time when updates UI
					bluetoothNotifyChange(RConstants.PLAYSTATE_CHANGED);

					categoryTV.setText(catName);
					if (categoryId == RConstants.WELCOME_CHILD_CATEGORY
							|| categoryId == RConstants.WELCOME_PARENT_CATEGORY) {

						titleTV.setText(RConstants.RIVET_WITH
								+ ContentManager
										.getContentManager(HomeActivity.this)
										.getPlaylistManager().getCurrentStory()
										.getAnchorName());
						categoryTV.setText(RConstants.WELCOME_RIVET);
					} else {
						titleTV.setText(title);
					}

					// show ads if its time to display adds
					if (categoryId == RConstants.ADS_CATEGORY) {

						if (!MySpinServerSDK.sharedInstance().isConnected()) {
							loadAdsInImageView();
							uploadedTimeTV.setVisibility(View.GONE);
							producerTV.setVisibility(View.GONE);
						}

					} else {
						if (!MySpinServerSDK.sharedInstance().isConnected()) {
							adsIV.setVisibility(View.GONE);

						}
						uploadedTimeTV.setVisibility(View.VISIBLE);
						producerTV.setVisibility(View.VISIBLE);
						uploadedTimeTV.setText(String
								.valueOf(getAudioUploadedTime()));
					}

					String source = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory().getSource();
					String[] arraySource = new String[2];
					if (source != null) {
						arraySource = source.split(";");
					} else {
						arraySource[0] = "";
					}
					if (categoryId != RConstants.ADS_CATEGORY) {
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

							producerTV.setText("| Source: " + arraySource[0]);

						} else {

							producerTV.setText("Source: " + arraySource[0]);
						}
					}

					if (categoryId == RConstants.IPOD_MUSIC_CATEGORY) {
						producerTV.setText("");
						uploadedTimeTV.setText("");

					}

					int catID = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getParentCategoryID();

					// update color of progress bar each time category change
					storyProgressBar
							.setProgress(RConstants.currentPosOfAudioPlayer);
					if (RConstants.shouldUpdateTimer) {
						if (totalDuration == RConstants.currentPosOfAudioPlayer) {
							durationTV.setText("-0:00");
						} else {
							durationTV
									.setText(convertMillis((long) (totalDuration - RConstants.currentPosOfAudioPlayer)));
						}
					} else {
						durationTV.setText("-0:00");
					}

					// set category colors
					int categoryColor = HHRHelper.getCategoryColor(catID);
					parentRL.setBackgroundColor(getResources().getColor(
							categoryColor));

				}// if story!=null ends here

			} else if (whatIsUpdate == RConstants.UPDATE_UI_ON_ORIENTATION_CHANGED_ACTION) {

				if (ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory() != null) {
					setFixTextTitle();
					setContorlsColorForEnableMode();
					nextIB.setClickable(true);
					playPauseBttn.setClickable(true);
					rewindBttn.setClickable(true);
					if (isPlayerPlaying) {
						playPauseBttn.setImageResource(R.drawable.button_pause);
					} else {
						playPauseBttn.setImageResource(R.drawable.button_play);
					}
					ShouldPlayAfterCallDisconnect = true;

					String catName = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getCategoryName();
					int categoryId = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getParentCategoryID();
					String title = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory().getTitle();

					setTitleFontSize(title);
					categoryTV.setText(catName);
					if (categoryId == RConstants.WELCOME_CHILD_CATEGORY
							|| categoryId == RConstants.WELCOME_PARENT_CATEGORY) {

						titleTV.setText(RConstants.RIVET_WITH
								+ ContentManager
										.getContentManager(HomeActivity.this)
										.getPlaylistManager().getCurrentStory()
										.getAnchorName());
						categoryTV.setText(RConstants.WELCOME_RIVET);
					} else {
						titleTV.setText(title);
					}
					if (categoryId == RConstants.ADS_CATEGORY) {

						if (!MySpinServerSDK.sharedInstance().isConnected()) {
							uploadedTimeTV.setVisibility(View.GONE);
							producerTV.setVisibility(View.GONE);
							loadAdsInImageView();
						}
					} else {
						if (!MySpinServerSDK.sharedInstance().isConnected()) {

							adsIV.setVisibility(View.GONE);
						}
						uploadedTimeTV.setVisibility(View.VISIBLE);
						producerTV.setVisibility(View.VISIBLE);

						uploadedTimeTV.setText(getAudioUploadedTime());
					}

					String source = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory().getSource();
					String[] arraySource = new String[2];
					if (source != null) {
						arraySource = source.split(";");
					} else {
						arraySource[0] = "";
					}

					if (categoryId != RConstants.ADS_CATEGORY) {
						if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
							producerTV.setText("| Source: " + arraySource[0]);
						} else {
							producerTV.setText("Source: " + arraySource[0]);
						}
					}

					if (categoryId == RConstants.IPOD_MUSIC_CATEGORY) {
						producerTV.setText("");
						uploadedTimeTV.setText("");

					}
					int catID = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getParentCategoryID();

					// update progress bar
					storyProgressBar
							.setProgress(RConstants.currentPosOfAudioPlayer);

					if (totalDuration == RConstants.currentPosOfAudioPlayer) {
						durationTV.setText("-0:00");
					} else {
						durationTV
								.setText(convertMillis((long) (totalDuration - RConstants.currentPosOfAudioPlayer)));
					}

					// set category colors
					int categoryColor = HHRHelper.getCategoryColor(catID);
					parentRL.setBackgroundColor(getResources().getColor(
							categoryColor));
				}
			}
		}

		private void loadAdsInImageView() {

			// thread starts to load bitmap for ads
			new Thread(new Runnable() {

				@Override
				public void run() {

					String imageUrl = ContentManager
							.getContentManager(HomeActivity.this)
							.getPlaylistManager().getCurrentStory()
							.getAdsImageURL();

					if (imageUrl != null) {

						final Bitmap AdsBitmap = getBitmapFromURL(imageUrl);

						runOnUiThread(new Runnable() {

							@Override
							public void run() {

								adsIV.setVisibility(View.VISIBLE);

								float aspectRatio = AdsBitmap.getWidth()
										/ AdsBitmap.getHeight();

								int newHeight = (int) (getScreenWidth() / aspectRatio);

								Bitmap fitToScreenBitmap = Bitmap
										.createScaledBitmap(AdsBitmap,
												getScreenWidth(), (newHeight) ,
												true);

								if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
									adsIV.getLayoutParams().width = getScreenWidth();
									adsIV.getLayoutParams().height = newHeight;
									adsIV.setImageBitmap(fitToScreenBitmap);
								} else {
									adsIV.getLayoutParams().width = dpToPx(320);
									adsIV.getLayoutParams().height = dpToPx(50);
									adsIV.setImageBitmap(AdsBitmap);
								}
							}
						});
					}
				}
			}).start();

		}

		@SuppressLint("SimpleDateFormat")
		private CharSequence getAudioUploadedTime() {

			String uploadTime = "";

			long uploadedTime = ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getCurrentStory().getUploadTimestamp();

			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'H:m:s'Z'");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

			String uploadedTimeGMT = dateFormat.format(uploadedTime);
			String currentTimeGMT = dateFormat.format(System
					.currentTimeMillis());

			long timeAgo = 0;
			try {

				Date uploadDate = dateFormat.parse(uploadedTimeGMT);
				Date currentDate = dateFormat.parse(currentTimeGMT);
				timeAgo = currentDate.getTime() - uploadDate.getTime();
			} catch (ParseException e) {

				e.printStackTrace();
			}

			String uploadedBy = ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getCurrentStory().getProducedBy();

			int daysAgo = (int) (timeAgo / (1000 * 60 * 60 * 24));
			int hours = (int) ((timeAgo - (1000 * 60 * 60 * 24 * daysAgo)) / (1000 * 60 * 60));
			int minutes = (int) (timeAgo - (1000 * 60 * 60 * 24 * daysAgo) - (1000 * 60 * 60 * hours))
					/ (1000 * 60);
			int seconds = (int) (timeAgo / 1000) % 60;
			int monthsAgo = daysAgo / 30;

			if (monthsAgo == 1) {

				uploadTime = monthsAgo + RConstants.MONTH_AGO + uploadedBy;
			} else if (monthsAgo > 1) {

				uploadTime = monthsAgo + RConstants.MONTHS_AGO + uploadedBy;
			} else if (daysAgo == 1) {

				uploadTime = daysAgo + RConstants.DAY_AGO + uploadedBy;
			} else if (daysAgo > 1) {

				uploadTime = daysAgo + RConstants.DAYS_AGO + uploadedBy;
			} else if (hours > 1) {
				uploadTime = hours + RConstants.HOURS_AGO + uploadedBy;
			} else if (hours > 0 && hours <= 1) {
				uploadTime = hours + RConstants.HOUR_AGO + uploadedBy;
			} else if (hours == 0 && minutes == 0) {
				uploadTime = seconds + RConstants.SECONDS_AGO + uploadedBy;
			} else if (minutes > 0) {
				uploadTime = minutes + RConstants.MINUTES_AGO + uploadedBy;
			}

			return uploadTime;
		}
	}

	private void bluetoothNotifyChange(String what) {
		// enable this code to check connection

		Intent blueToothIntent = null;
		if (what.equals(RConstants.PLAYSTATE_CHANGED)) {
			blueToothIntent = new Intent(RConstants.AVRCP_PLAYSTATE_CHANGED);
		} else if (what.equals(RConstants.META_CHANGED)) {
			blueToothIntent = new Intent(RConstants.AVRCP_META_CHANGED);
		} else {
			return;
		}

		if (ContentManager.getContentManager(HomeActivity.this)
				.getPlaylistManager().getCurrentStory() != null) {
			blueToothIntent.putExtra(RConstants.ARTIST, ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getCurrentStory().getProducedBy());
			blueToothIntent.putExtra(RConstants.TRACK, ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getCurrentStory().getTitle());
			blueToothIntent.putExtra(RConstants.PLAYING, true);
			blueToothIntent.putExtra(RConstants.DURATION, totalDuration);
			blueToothIntent.putExtra(RConstants.POSITION,
					RConstants.currentPosOfAudioPlayer);
			sendBroadcast(blueToothIntent);

		}
	}

	private class UIUpdateOnStoryLoadTask extends
			AsyncTask<Void, Integer, Long> {

		@Override
		protected void onPreExecute() {
			bufferingTV.setVisibility(View.VISIBLE);
			bufferingTV.setText(RConstants.LOADING_STORIES);
			setLoadingStories(true);
			loaderIV.setVisibility(View.VISIBLE);
			frameAnimation.start();
			nextIB.setClickable(false);
			playPauseBttn.setClickable(false);
			rewindBttn.setClickable(false);

		}

		protected void onPostExecute(Long result) {
			bufferingTV.setVisibility(View.INVISIBLE);

		}

		@Override
		protected Long doInBackground(Void... params) {
			prefStore.setBooleanData(RConstants.RUNNING_FIRST_TIME, false);
			// this will make sure the when app disconnect from Bosch it play
			// story which is currentStory.
			// getContentManager().getPlaylistManager().clearQueue();
			int multiplier = 1;
			for (;;) {

				if (!ContentManager.getContentManager(HomeActivity.this)
						.isUpdatePlayListComplete()
						&& RConstants.STORY_UPLOAD_TO_DB) {
					ContentManager.getContentManager(HomeActivity.this)
							.updatePlyListforFirstTime();

				}

				if (ContentManager.getContentManager(HomeActivity.this)
						.getFlagNoStoryToPlay()) {

					runOnUiThread(new UpdateUi(
							RConstants.UPDATE_UI_WITH_NO_STORY_ACTION));

					break;
				} else {

					if (ContentManager.getContentManager(HomeActivity.this)
							.isReadyToPlay()) {
						// let check if we are late and we have missed
						// notification
						// and isReadytoplay is set
						canPressButtons = true;
						runOnUiThread(new UpdateUi(RConstants.UPDATE_UI_ACTION));
						downloadStoryAndStartPlaylistManger();
						break;
					} else {
						try {
							long totalTimeInMillis = (multiplier * 200);
							if (totalTimeInMillis >= RConstants.SHOW_ERROR_TIME) {
								runOnUiThread(new Runnable() {

									@Override
									public void run() {

										if (!connectionDetector
												.isConnectingToInternet()) {
											if (!MySpinServerSDK
													.sharedInstance()
													.isConnected()) {
												new AlertDialogManager()
														.showAlertDialog(
																HomeActivity.this,
																RConstants.RIVET_RADIO,
																RConstants.NO_INTERNET_CONNECTION,
																true);
											} else {
												new BoschIVIAlertView()
														.showAlertDialog(
																HomeActivity.this,
																1);
											}

										} else if (!ConnectionDetector
												.isConnectedFast(HomeActivity.this)) {
											if (!MySpinServerSDK
													.sharedInstance()
													.isConnected()) {
												new AlertDialogManager()
														.showAlertDialog(
																HomeActivity.this,
																RConstants.RIVET_RADIO,
																RConstants.SLOW_INTERNET_CONNECTION,
																true);
											} else {
												new BoschIVIAlertView()
														.showAlertDialog(
																HomeActivity.this,
																3);
											}

										}
									}
								});

								break;
							}

							multiplier++;
							Thread.sleep(200);

						} catch (InterruptedException e) {

							e.printStackTrace();
						}
					}

				}
			}
			return null;
		}
	}

	public void downloadStoryAndStartPlaylistManger() {

		DownloadStoryUrlThread threadStoryUrl = new DownloadStoryUrlThread();
		threadStoryUrl.start();
		try {
			threadStoryUrl.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String convertMillis(long milliseconds) {
		String formattedTime;
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
		String mySeconds = null;
		if (seconds < 10) {
			mySeconds = "0" + seconds;
		} else {
			mySeconds = String.valueOf(seconds);
		}

		if (hours == 0 && minutes == 0) {
			formattedTime = "-0:" + mySeconds;
		} else if (hours == 0) {
			formattedTime = "-" + minutes + ":" + mySeconds;
		} else {
			formattedTime = "-" + hours + ":" + minutes + ":" + mySeconds;
		}

		return formattedTime;
	}

	public void LogoutButtonGone() {
		childLogoutSectionRL.setVisibility(View.GONE);
		childLoginSectionRL.setVisibility(View.GONE);
		dividerIV.setVisibility(View.GONE);
		child1RL.setVisibility(View.VISIBLE);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Share");
		runOnUiThread(new Runnable() {

			@Override
			public void run() {

				bookMarkBttn.getBackground().setAlpha(
						RConstants.PLAYER_CONTROL_ALPHA);
			}
		});
		if (ContentManager.getContentManager(HomeActivity.this)
				.getPlaylistManager().getCurrentStory() != null) {
			if (ContentManager.getContentManager(HomeActivity.this)
					.getPlaylistManager().getCurrentStory().getIsLike() == 1) {
				menu.add(0, v.getId(), 1, RConstants.UNLIKE_STORY);
			} else {
				menu.add(0, v.getId(), 1, RConstants.LIKE_STORY);
			}
		} else {
			menu.add(0, v.getId(), 1, RConstants.LIKE_STORY);
		}

		menu.add(0, v.getId(), 2, RConstants.SHARE_STORY_FACEBOOK);
		menu.add(0, v.getId(), 3, RConstants.SHARE_STORY_TWITTER);
		menu.add(0, v.getId(), 4, RConstants.SHARE_STORY_EMAIL);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if (ContentManager.getContentManager(HomeActivity.this)
				.getPlaylistManager().getCurrentStory() != null) {

			if (item.getOrder() == 1
					&& item.getTitle().equals(RConstants.LIKE_STORY)) {
				item.setTitle(RConstants.UNLIKE_STORY);
				postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityLikeStory);
				ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory().setIsLike(1);

				// not to get Database locked exception
				StoryLikeAndUnlikeTask storyLikeAndUnlikeTask = new StoryLikeAndUnlikeTask(
						true);
				Thread storyLikeAndUnlikeThread = new Thread(
						storyLikeAndUnlikeTask);
				storyLikeAndUnlikeThread.start();

			} else if (item.getOrder() == 1
					&& item.getTitle().equals(RConstants.UNLIKE_STORY)) {
				item.setTitle(RConstants.LIKE_STORY);
				ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory().setIsLike(0);

				// not to get Database locked exception
				StoryLikeAndUnlikeTask storyLikeAndUnlikeTask = new StoryLikeAndUnlikeTask(
						false);
				Thread storyLikeAndUnlikeThread = new Thread(
						storyLikeAndUnlikeTask);
				storyLikeAndUnlikeThread.start();

			} else if (item.getOrder() == 2) {
				String storyTitle = (ContentManager
						.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory().getTitle());
				String storyURL = RConstants.SHARE_STORY_URL
						+ ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().getCurrentStory()
								.getTrackID();
				String status = storyTitle + "\n" + storyURL;

				String userUID = prefStore.getStringData(
						RConstants.FB_LOGIN_PROVIDER_UID, null);
				boolean login = prefStore.getBooleanData(
						RConstants.FB_USER_IS_LOGIN, false);

				ShareOnFacebook shareOnFacebook = new ShareOnFacebook(
						HomeActivity.this, storyTitle, storyURL, status,
						userUID);
				if (login) {
					// load image for fb sharing
					if (RConstants.sharingFBBitmap != null) {
						shareOnFacebook
								.createFacebookDialog(RConstants.sharingFBBitmap);
					} else {
						shareOnFacebook.createFacebookDialog(null);
					}
				} else {

					if (connectionDetector.isConnectingToInternet()) {
						FacebookLoginTask fbLoginTask = shareOnFacebook
								.getFbLoginTask();
						fbLoginTask.execute();
					} else {
						showToast("No Internet Connection");
					}
					// showToast(RConstants.LOGIN_FIRST_FB);
				}

			} else if (item.getOrder() == 3) {
				// share on twitter
				shareOnTwitter.startShareOnTwitter();

			} else if (item.getOrder() == 4) {
				// share via email
				EmailSender emailSender = new EmailSender(HomeActivity.this);
				emailSender.sendEmailMessage();
			} else {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {

		super.onNewIntent(intent);
		// get status after login
		storyDbAdapter = StoryDBAdapter.getStoryDBAdapter(HomeActivity.this);
		final Uri uri = intent.getData();
		if (uri != null
				&& uri.getScheme().equals(RConstants.RIVET_PACKAGE_NAME)) {
			Log.i(TAG, RConstants.LOGIN_ACTIVITY);
			if (ContentManager.getContentManager(HomeActivity.this)
					.getPlaylistManager().getCurrentStory() != null) {

				shareOnTwitter.getContentOfStatus();

				if (!shareOnTwitter.isTwitterLoggedInAlready()) {
					// oAuth verifier
					final String verifier = uri
							.getQueryParameter(RConstants.URL_TWITTER_OAUTH_VERIFIER);

					try {

						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
								try {

									// Get the access token
									accessToken = RConstants.twitter
											.getOAuthAccessToken(
													RConstants.requestToken,
													verifier);

									if (RConstants.BUILD_DEBUBG) {
										Log.i(RConstants.TWITTER_AUTH_TOKEN,
												"> " + accessToken.getToken());
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
						thread.start();
						thread.join();
					} catch (Exception e) {
						// Check log for login errors
						if (RConstants.BUILD_DEBUBG) {
							Log.e(TAG, "> " + e.getMessage());
						}
						e.printStackTrace();
					}
				}

				// After getting access token, access token secret
				// store them in application preferences
				prefStore.setStringData(RConstants.PREF_KEY_OAUTH_TOKEN,
						accessToken.getToken());
				prefStore.setStringData(RConstants.PREF_KEY_OAUTH_SECRET,
						accessToken.getTokenSecret());
				prefStore.setBooleanData(RConstants.PREF_KEY_TWITTER_LOGIN,
						true);

				// make the status visible

				shareOnTwitter.createTwitterDialog();
				shareOnTwitter.makeStatusVisible();
			} else {
				showToast(RConstants.NO_STORY_TO_SHARE_TWITTER);
			}
		}

	}

	private class IPodeThread implements Runnable {

		@Override
		public void run() {

			StoryDBAdapter storyDbAdapter = StoryDBAdapter
					.getStoryDBAdapter(HomeActivity.this);
			IPodMusicFinder iPodeFinder = new IPodMusicFinder(
					HomeActivity.this, storyDbAdapter);
			iPodeFinder.start();

		}

	}

	private class UserCallEventDetector extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(RConstants.PHONE_STATE)) {
				if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
						TelephonyManager.EXTRA_STATE_RINGING)) {
					// This code will execute when the phone has an incoming
					// call

					if (ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().isPlaying()) {
						ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().play();
						isPlayerPlaying = false;
						playPauseBttn.setImageResource(R.drawable.button_play);

					}

				} else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE)
						.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
					// This code will execute when the call is disconnected
					// phone state is again IDLE if phone disconnected.
					// OFFTHEHOOK is when phone call is accepted
					if (ShouldPlayAfterCallDisconnect
							&& !ContentManager
									.getContentManager(HomeActivity.this)
									.getPlaylistManager().isPlaying()) {

						ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().play();
						isPlayerPlaying = true;
						playPauseBttn.setImageResource(R.drawable.button_pause);

					}

				}

			} else if ((intent.getAction()
					.equals(RConstants.DONT_SHOW_AD_BANNER))
					&& (!shouldStayBannerOnScreen)) {

				// don't get runtimeException here run this code on UI thread
				if (!MySpinServerSDK.sharedInstance().isConnected()) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							while (true) {
								try {
									adsIV.setVisibility(View.GONE);
									break;
								} catch (RuntimeException e) {
									e.printStackTrace();
									try {
										Thread.sleep(100);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							}
						}
					});

				}

			} else if (intent.getAction().equals(RConstants.OUTGOING_CALLS)) {

				if (ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().isPlaying()) {
					ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().play();
					isPlayerPlaying = false;
					playPauseBttn.setImageResource(R.drawable.button_play);
				}

			} else if (intent.getAction().equals(RConstants.SERVICE_STATE)) {

				if (!isNotONAirplaneMode) {
					new Thread(new Runnable() {

						@Override
						public void run() {

							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if ((new ConnectionDetector(HomeActivity.this))
									.isConnectingToInternet()) {
								if (!ContentManager
										.getContentManager(HomeActivity.this)
										.getPlaylistManager().isPlaying()) {
									isPlayerPlaying = true;
									isNotONAirplaneMode = true;
									runOnUiThread(new UpdateUi(
											RConstants.UPDATE_UI_ACTION_ENABLE_INPUT));
									ContentManager
											.getContentManager(
													HomeActivity.this)
											.getPlaylistManager().play();
									RecentlyDidAirplane = true;
								}
							}
						}
					}).start();
				} else {
					if (!(new ConnectionDetector(HomeActivity.this))
							.isConnectingToInternet()) {
						if (ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().isPlaying()) {
							isPlayerPlaying = false;
							isNotONAirplaneMode = false;
							runOnUiThread(new UpdateUi(
									RConstants.UPDATE_UI_ACTION_DISABLE_INPUT));
							ContentManager.getContentManager(HomeActivity.this)
									.getPlaylistManager().play();
							RecentlyDidAirplane = true;
						}
					}
				}

			}

		}

	}

	private class RestartApplicationByTimer extends TimerTask {

		@Override
		public void run() {

			// start splash screen after 6 hours

			long timeAgo = getTimeToRestartApplication();

			int daysAgo = (int) (timeAgo / (1000 * 60 * 60 * 24));
			int hours = (int) ((timeAgo - (1000 * 60 * 60 * 24 * daysAgo)) / (1000 * 60 * 60));

			if ((hours >= RConstants.DELAY_IN_HOURS || daysAgo >= 1)
					&& (RConstants.DELAY_IN_HOURS != 0)) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {

						Intent restartApp = new Intent(HomeActivity.this,
								SplashActivity.class);
						startActivity(restartApp);
						ContentManager.getContentManager(HomeActivity.this)
								.getPlaylistManager().stopAudioService();
						ContentManager.getContentManager(HomeActivity.this)
								.resetContentManager();
						finish();
					}
				});

			}

		}

	}

	public void resetCheckedItemSize() {
		checkedItemsNumber = 0;
	}

	public void increaseCheckedItemSize() {
		checkedItemsNumber++;
	}

	public void decreaseCheckedItemSize() {
		checkedItemsNumber--;
	}

	@SuppressLint("SimpleDateFormat")
	private long getTimeToRestartApplication() {

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'H:m:s'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		long currentTime = System.currentTimeMillis();
		String startTimeGMT = dateFormat.format(appStartTime);
		String EndTimeGMT = dateFormat.format(currentTime);

		long timeAgo = 0;
		try {

			Date startDate = dateFormat.parse(startTimeGMT);
			Date currentDate = dateFormat.parse(EndTimeGMT);
			timeAgo = currentDate.getTime() - startDate.getTime();

		} catch (ParseException e) {

			e.printStackTrace();
		}

		return timeAgo;
	}

	private class DownLoadTrackThread implements Runnable {

		@Override
		public void run() {
			mobileAppTracker.setDebugMode(true);
			mobileAppTracker.setAllowDuplicates(true);
			// See sample code at
			// http://developer.android.com/google/play-services/id.html
			try {
				Info adInfo = AdvertisingIdClient
						.getAdvertisingIdInfo(getApplicationContext());
				mobileAppTracker.setGoogleAdvertisingId(adInfo.getId(),
						adInfo.isLimitAdTrackingEnabled());
			} catch (IOException e) {
				// Unrecoverable error connecting to Google Play services (e.g.,
				// the old version of the service doesn't support getting
				// AdvertisingId).
				mobileAppTracker.setAndroidId(Secure.getString(
						getContentResolver(), Secure.ANDROID_ID));
			} catch (GooglePlayServicesNotAvailableException e) {
				// Google Play services is not available entirely.
				mobileAppTracker.setAndroidId(Secure.getString(
						getContentResolver(), Secure.ANDROID_ID));
			} catch (GooglePlayServicesRepairableException e) {
				// Encountered a recoverable error connecting to Google Play
				// services.
				mobileAppTracker.setAndroidId(Secure.getString(
						getContentResolver(), Secure.ANDROID_ID));
			} catch (NullPointerException e) {
				// getId() is sometimes null
				mobileAppTracker.setAndroidId(Secure.getString(
						getContentResolver(), Secure.ANDROID_ID));
			}
		}
	} // DownLoadTrackThread ends here

	class OnAudioFocus implements AudioManager.OnAudioFocusChangeListener {

		Context homeActivity;

		public OnAudioFocus(Context homeActivity) {
			this.homeActivity = homeActivity;
		}

		public void onAudioFocusChange(int focusChange) {
			switch (focusChange) {

			case AudioManager.AUDIOFOCUS_GAIN:
				if (!ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().isPlaying()
						&& audioFocusLost) {
					ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().play();
					isPlayerPlaying = true;
					audioFocusLost = false;
				}

				break;

			case AudioManager.AUDIOFOCUS_LOSS:
				if (ContentManager.getContentManager(HomeActivity.this)
						.getPlaylistManager().isPlaying()) {
					ContentManager.getContentManager(HomeActivity.this)
							.getPlaylistManager().play();
					isPlayerPlaying = false;
					audioFocusLost = true;

					runOnUiThread(new Runnable() {
						public void run() {
							if (isPlayerPlaying) {
								playPauseBttn
										.setImageResource(R.drawable.button_play);
							} else {
								playPauseBttn
										.setImageResource(R.drawable.button_pause);
							}
						}
					});
					// post log external audio interruption
					postLogsToServerAndInDatabaseForFlurry(RConstants.ActivityExternalAudioInterruption);
				}

				break;

			}
		}
	}

	@Override
	public void onConnectionStateChanged(boolean arg0) {

		updateBoschView();

	}

	private void updateBoschView() {
		if (MySpinServerSDK.sharedInstance().isConnected()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			setContentView(R.layout.activity_home_bosch);

			initAllUIForBosch();

			if (ContentManager.getContentManager(HomeActivity.this)
					.getPlaylistManager().getCurrentStory() != null) {
				setFixTextTitle();
				String storyTitle = ContentManager
						.getContentManager(HomeActivity.this)
						.getPlaylistManager().getCurrentStory().getTitle();
				if (storyTitle != null)
					setTitleFontSize(storyTitle);
			}
			loadCategoriesInList();
			runOnUiThread(new UpdateUi(RConstants.UPDATE_UI_ACTION));

			bookMarkBttn.setClickable(false);
			nextIB.setClickable(true);
			playPauseBttn.setClickable(true);
			rewindBttn.setClickable(true);

			if (isPlayerPlaying) {
				playPauseBttn.setImageResource(R.drawable.button_pause);
			} else {
				playPauseBttn.setImageResource(R.drawable.button_play);
			}

		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			// remove orientation restriction
		//	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
			if (RConstants.BUILD_DEBUBG) {
				logI(TAG, "Bosch not connected");
			}
		}

	}

	// post category logs on server when user enabled or disabled the category
	private class PostLogsForCategory implements Runnable {

		private int categoryEnableOrDisable;
		private String categoryName;

		public PostLogsForCategory(int categoryEnableOrDisable,
				String categoryName) {

			this.categoryEnableOrDisable = categoryEnableOrDisable;
			this.categoryName = categoryName;
		}

		@Override
		public void run() {

			Story currentStory = ContentManager
					.getContentManager(HomeActivity.this).getPlaylistManager()
					.getCurrentStory();
			String enableOrDisable = null;
			if (categoryEnableOrDisable == RConstants.ActivityCategoryEnable) {
				enableOrDisable = "enabled";
			} else {
				enableOrDisable = "disabled";
			}

			if (currentStory != null) {
				String userActivity = ActivitiesUserApp
						.getAppropriateAction(categoryEnableOrDisable);

				String actionLogsToPostServer = "[{\n" + '"'
						+ RConstants.ACTION_TYPE + '"' + " : " + '"'
						+ userActivity + '"' + "," + "\n" + '"'
						+ RConstants.ACTIVITY_TIMESTAMP + '"' + " : " + '"'
						+ (System.currentTimeMillis() / 1000) + '"' + ","
						+ "\n" + '"' + RConstants.USER_ID + '"' + " : " + '"'
						+ uuid + '"' + "," + "\n" + '"' + RConstants.DATA_LOGS
						+ '"' + " : " + '"' + "{" + "\\n    " + "\\" + '"'
						+ RConstants.APP_VERSION + "\\" + '"' + " : " + "\\"
						+ '"' + versionName + "\\" + '"' + "," + "\\n    "
						+ "\\" + '"' + RConstants.PLATFORM + "\\" + '"' + " : "
						+ "\\" + '"' + RConstants.OS_NAME + "\\" + '"' + ","
						+ "\\n    " + "\\" + '"' + RConstants.GIGYA_UID + "\\"
						+ '"' + " : " + "\\" + '"' + uid + "\\" + '"' + ","
						+ "\\n    " + "\\" + '"' + RConstants.CURRENT_TRACK_ID
						+ "\\" + '"' + " : " + currentStory.getTrackID() + ","
						+ "\\n    " + "\\" + '"'
						+ RConstants.CURRENT_TRACK_TITLE + "\\" + '"' + " : "
						+ "\\" + '"' + currentStory.getTitle() + "\\" + '"'
						+ "," + "\\n    " + "\\" + '"' + categoryName + "\\"
						+ '"' + " : " + "\\" + '"' + enableOrDisable + "\\"
						+ '"' + "," + "\\n    " + "\\" + '"'
						+ RConstants.CURRENT_TRACK_POSITIONS_SECONDS + "\\"
						+ '"' + " : " + RConstants.currentPosOfAudioPlayer
						+ "\\n" + "}" + '"' + "}]";

				PostUserAction postUserAction = new PostUserAction(
						actionLogsToPostServer);
				postUserAction.startLogging();

			}
		}

	}

	@Override
	protected void onStop() {

		// When this activity gets stopped unregister for mySPIN connection
		// events.
		try {
			MySpinServerSDK.sharedInstance().unregisterConnectionStateListener(
					this);
		} catch (MySpinException e) {
			e.printStackTrace();
		}

		super.onStop();

	}

	@Override
	protected void onDestroy() {

		// dismiss login progress dialog if visible
		new LoginModel(HomeActivity.this).destroyProgressDialog();
		super.onDestroy();

		// unregister call detector receiver
		unregisterReceiver(callDetectReciever);

		// terminate ads thread when activity kills
		terminateAdsThread();

		// stop service when activity kills
		ContentManager.getContentManager(HomeActivity.this)
				.getPlaylistManager().stopAudioService();

		// send activity logs to server when activity kills
		String userActivity = ActivitiesUserApp
				.getAppropriateAction(RConstants.ActivityKillApplication);

		String actionLogsToPostServer = "[{\n" + '"' + RConstants.ACTION_TYPE
				+ '"' + " : " + '"' + userActivity + '"' + "," + "\n" + '"'
				+ RConstants.ACTIVITY_TIMESTAMP + '"' + " : " + '"'
				+ (System.currentTimeMillis() / 1000) + '"' + "," + "\n" + '"'
				+ RConstants.USER_ID + '"' + " : " + '"' + uid + '"' + ","
				+ "\n" + '"' + RConstants.DATA_LOGS + '"' + " : " + '"' + "{"
				+ "\\n    " + "\\" + '"' + RConstants.APP_VERSION + "\\" + '"'
				+ " : " + "\\" + '"' + versionName + "\\" + '"' + ","
				+ "\\n    " + "\\" + '"' + RConstants.PLATFORM + "\\" + '"'
				+ " : " + "\\" + '"' + RConstants.OS_NAME + "\\" + '"' + ","
				+ "\\n    " + "\\" + '"' + RConstants.GIGYA_UID + "\\" + '"'
				+ " : " + "\\" + '"' + uid + "\\" + '"' + "\\n" + "}" + '"'
				+ "}]";

		PostUserAction postUserAction = new PostUserAction(
				actionLogsToPostServer);
		postUserAction.startLogging();

		// unregister component for media buttons
		mAudioManager
				.unregisterMediaButtonEventReceiver(mRemoteControlResponder);

	}

	private class SaveCategoryInDatabaseTask implements Runnable {

		@Override
		public void run() {

			for (;;) {

				try {
					CategoryDBAdapter categoryDbAdapterForWrite = CategoryDBAdapter
							.getCategoryDBAdapterForWrite(HomeActivity.this);
					categoryDbAdapterForWrite.deleteAllMyCategories();
					Integer[] mKeys = mapCategoryList.keySet().toArray(
							new Integer[mapCategoryList.size()]);
					for (int indx = 0; indx < mapCategoryList.size(); indx++) {

						Category category = mapCategoryList.get(mKeys[indx]);
						// add only selected items
						if (category.getisChecked())
							categoryDbAdapterForWrite.addMyCategory(category);
					}
				} catch (SQLiteDatabaseLockedException dbLockedException) {

					dbLockedException.printStackTrace();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
				break;
			}
		}

	}

	private class StoryLikeAndUnlikeTask implements Runnable {

		private boolean likeOrUnlike;

		public StoryLikeAndUnlikeTask(boolean likeOrUnlike) {

			this.likeOrUnlike = likeOrUnlike;
		}

		@Override
		public void run() {

			// run in loop untill gets updated in database
			for (;;) {

				try {
					if (likeOrUnlike) {

						storyDbAdapter.setStoryLiked(ContentManager
								.getContentManager(HomeActivity.this)
								.getPlaylistManager().getCurrentStory()
								.getTrackID(), true);

					} else {

						storyDbAdapter.setStoryLiked(ContentManager
								.getContentManager(HomeActivity.this)
								.getPlaylistManager().getCurrentStory()
								.getTrackID(), false);
					}

					break;
				} catch (SQLiteDatabaseLockedException dbLockedException) {

					dbLockedException.printStackTrace();

					try {
						Thread.sleep(100);
					} catch (InterruptedException interruptedException) {

						interruptedException.printStackTrace();
					}
				}
			}

		} // ends run method

	} // ends StoryLikeAndUnlikeTask

	public void setTitleFontSize(String title) {
		int titleReductionFactor = 0;
		int titleLength = title.length();

		if (!MySpinServerSDK.sharedInstance().isConnected()) {

			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				if (titleLength <= (26)) {

					titleTV.setTextSize(31);
				} else if ((titleLength > (26)) && (titleLength <= (40))) {

					titleTV.setTextSize(26);

				} else if ((titleLength > (40)) && (titleLength <= (55))) {

					titleTV.setTextSize(22);
				} else if ((titleLength > (55)) && (titleLength <= 70)) {

					titleTV.setTextSize(21);
				} else {
					// titleTV.setTextSize(16);
					titleTV.setTextSize(19);
				}
			} else {
				if (titleLength <= (30)) {

					titleTV.setTextSize(31);
				} else if ((titleLength > (30)) && (titleLength < (60))) {
					titleTV.setTextSize(24);

				} else {

					titleTV.setTextSize(19);
				}
			}

		} else {

			titleReductionFactor = 11;

			if (titleLength <= (26 - titleReductionFactor)) {

				titleTV.setTextSize(30);
			} else if ((titleLength > (26 - titleReductionFactor))
					&& (titleLength <= (40 - titleReductionFactor))) {

				titleTV.setTextSize(25);

			} else if ((titleLength > (40 - titleReductionFactor))
					&& (titleLength <= (55 - titleReductionFactor))) {

				titleTV.setTextSize(24);
			} else if ((titleLength > (55 - titleReductionFactor))
					&& (titleLength <= 70 - titleReductionFactor)) {

				titleTV.setTextSize(21);
			} else {
				// titleTV.setTextSize(16);
				titleTV.setTextSize(19);
			}

		}

	}

	private class AddAdsInDBTimer extends TimerTask {

		@Override
		public void run() {

			AdsBuilder adsBuilder = new AdsBuilder(HomeActivity.this);
			adsBuilder.start();

		}

	}
	
	
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}

}// Home Ends