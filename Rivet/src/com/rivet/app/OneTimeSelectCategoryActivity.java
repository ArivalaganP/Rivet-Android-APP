package com.rivet.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.ContentManager;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.social.PostUserAction;

public class OneTimeSelectCategoryActivity extends BaseActivity implements
		View.OnClickListener {

	String TAG = "OneTimeSelectionActivity";
	RelativeLayout topNewsBttn, technologyBttn, scienceBttn, businessBttn, governmentTV ,
			lifestyleBttn;
	RelativeLayout sportsBttn, crimesAndCourtBttn, musicBttn , artTV ;
	
	Button nextBttn , backBttn ;
	ImageView backIv;
	private CategoryDBAdapter database;

	private ArrayList<String> selectedArrayList = new ArrayList<String>();

	// flags for all categories

	private boolean flagNews = false;
	private boolean flagTechnology = false;
	private boolean flagPolitics = false;
	private boolean flagScience = false;
	private boolean flagBusiness = false;
	private boolean flagLifeStyle = false;
	private boolean flagSports = false;
	private boolean flagCrimeCourt = false;
	private boolean flagArtAndEnt = false;
	private boolean flagMusic = false;
	private TextView selectTVText;
	private Typeface metaProTypeFace;
	private TextView title;
	RelativeLayout [] buttonsCategoryArr  ;
	private boolean animationDone = false ;
	
	int[] colorsCategory = { R.color.topNewsColor , R.color.politicsColor ,
							R.color.businessColor , R.color.sportsColor ,
							R.color.entertainmentColor, R.color.technologyColor ,
							R.color.scienceColor , R.color.lifeStyleColor , 
							R.color.outOfBoxColor , R.color.unassignedColor } ;
	private int indx = 0 ;
	
	private boolean firstTime = true ;
	private Timer timer;
	private CategoryDBAdapter categoryDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_one_time_select_category);
		buttonsCategoryArr = new RelativeLayout[10] ;
 		database = CategoryDBAdapter.getCategoryDBAdapterForWrite(this);
		metaProTypeFace = Typeface.createFromAsset(getAssets(), "MetaPro_Book.otf");
		topNewsBttn = (RelativeLayout) findViewById(R.id.bt_top_news);
		title = (TextView) findViewById(R.id.title);
		selectTVText = (TextView) findViewById(R.id.tv_select);
		categoryDbAdapter = CategoryDBAdapter.getCategoryDBAdapterForWrite(this);
		topNewsBttn.setContentDescription(String.valueOf(RConstants.TOP_NEWS_CATEGORY));
		topNewsBttn.setOnClickListener(this);
		technologyBttn = (RelativeLayout) findViewById(R.id.bt_technology);
		technologyBttn.setContentDescription(String.valueOf(RConstants.TECHNOLOGY_CATEGORY));
		technologyBttn.setOnClickListener(this);
		scienceBttn = (RelativeLayout) findViewById(R.id.bt_science);
		scienceBttn.setContentDescription(String.valueOf(RConstants.SCIENCE_CATEGORY));
		scienceBttn.setOnClickListener(this);
		businessBttn = (RelativeLayout) findViewById(R.id.bt_business);
        businessBttn.setContentDescription(String.valueOf(RConstants.BUSINESS_CATEGORY));
		governmentTV = (RelativeLayout) findViewById(R.id.governmentTV);
		governmentTV.setOnClickListener(this);
		governmentTV.setContentDescription(String.valueOf(RConstants.POLITICS_CATEGORY));
		businessBttn.setOnClickListener(this);
		lifestyleBttn = (RelativeLayout) findViewById(R.id.bt_lifestyle);
		lifestyleBttn.setContentDescription(String.valueOf(RConstants.LIFE_STYLE_CATEGORY));
		lifestyleBttn.setOnClickListener(this);
		sportsBttn = (RelativeLayout) findViewById(R.id.bt_sports);
		sportsBttn.setContentDescription(String.valueOf(RConstants.SPORTS_CATEGORY));
		sportsBttn.setOnClickListener(this);
		crimesAndCourtBttn = (RelativeLayout) findViewById(R.id.bt_crime_and_courts);
		crimesAndCourtBttn.setContentDescription(String.valueOf(RConstants.CRIME_AND_COURT));
		crimesAndCourtBttn.setOnClickListener(this);
		musicBttn = (RelativeLayout) findViewById(R.id.bt_music);
		musicBttn.setContentDescription(String.valueOf(RConstants.IPOD_MUSIC_CATEGORY));
		musicBttn.setOnClickListener(this);
		nextBttn = (Button) findViewById(R.id.bt_next);
		nextBttn.setOnClickListener(this);
		backBttn = (Button) findViewById(R.id.bt_back);
		backBttn.setOnClickListener(this);
		backIv = (ImageView) findViewById(R.id.iv_back);
		backIv.setOnClickListener(this);
		artTV = (RelativeLayout) findViewById(R.id.artTV);
		artTV.setOnClickListener(this);
		artTV.setContentDescription(RConstants.ENTERTAINMENT_CATEGORY+"");
		nextBttn.setClickable(false);
		 topNewsBttn.setClickable(false);
		 technologyBttn.setClickable(false);
		 scienceBttn.setClickable(false);
		 businessBttn.setClickable(false);
		 governmentTV.setClickable(false);
		 lifestyleBttn.setClickable(false);
		 sportsBttn.setClickable(false);
		 crimesAndCourtBttn.setClickable(false);
		 musicBttn.setClickable(false);
		 artTV.setClickable(false); 
		
			buttonsCategoryArr [0] = topNewsBttn ;
			buttonsCategoryArr [1] = governmentTV ;
			buttonsCategoryArr [2] = businessBttn ;
			buttonsCategoryArr [3] = sportsBttn ;
			buttonsCategoryArr [4] = artTV ;
			buttonsCategoryArr [5] = technologyBttn ;
			buttonsCategoryArr [6] = scienceBttn ;
			buttonsCategoryArr [7] = lifestyleBttn ;
			buttonsCategoryArr [8] = crimesAndCourtBttn ;
			buttonsCategoryArr [9] = musicBttn ;
		
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					for(;;){
						if(animationDone){
							runOnUiThread( new Runnable() {
								public void run() {
									 musicBttn.setBackgroundResource(R.drawable.category);
									 nextBttn.setClickable(true);
									 topNewsBttn.setClickable(true);
									 technologyBttn.setClickable(true);
									 scienceBttn.setClickable(true);
									 businessBttn.setClickable(true);
									 governmentTV.setClickable(true);
									 lifestyleBttn.setClickable(true);
									 sportsBttn.setClickable(true);
									 crimesAndCourtBttn.setClickable(true);
									 musicBttn.setClickable(true);
									 artTV.setClickable(true);
									 
								if(timer!= null){
									timer.cancel();	
									timer = null ;
								              	}
								}
							});
							break ;
						}else{
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
				}
			}).start();
			
			
			// this thread starts to initialize ContentManager only once in application life time
			if(runTime){
				InitiateContentManagerThread initializeContentManagerThread = new InitiateContentManagerThread();
				initializeContentManagerThread.start(); 
			}
			
	
		// set type face for items 
		selectTVText.setTypeface(metaProTypeFace);
		title.setTypeface(metaProTypeFace);
		backBttn.setTypeface(metaProTypeFace);
		nextBttn.setTypeface(metaProTypeFace);
		
				
		AnimationTimer animationTimer = new AnimationTimer();		
		timer = new Timer();
		timer.schedule(animationTimer, 500 , 160);
	
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_back:
			finish();
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.bt_next:
			// selected categories ids
			ArrayList<Integer> selectedCatIds = new ArrayList<Integer>();
			
			// deselected categories ids
			ArrayList<Integer> deselectedCatIds = new ArrayList<Integer>();
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(topNewsBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(technologyBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(governmentTV.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(scienceBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(businessBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(lifestyleBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(sportsBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(crimesAndCourtBttn.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(artTV.getContentDescription().toString())));
			deselectedCatIds.add(Integer.valueOf(Integer.parseInt(topNewsBttn.getContentDescription().toString())));
			
			
			
			if (selectedArrayList.size() == 0) {
				addCategoryInDatabase(topNewsBttn.getContentDescription().toString());
				addCategoryInDatabase(technologyBttn.getContentDescription().toString());
				addCategoryInDatabase(governmentTV.getContentDescription().toString());
				addCategoryInDatabase(scienceBttn.getContentDescription().toString());
				addCategoryInDatabase(businessBttn.getContentDescription().toString());
				addCategoryInDatabase(lifestyleBttn.getContentDescription().toString());
				addCategoryInDatabase(sportsBttn.getContentDescription().toString());
				addCategoryInDatabase(crimesAndCourtBttn.getContentDescription().toString());
				addCategoryInDatabase(artTV.getContentDescription().toString());
				addCategoryInDatabase(musicBttn.getContentDescription().toString());
				
				// this code is being written to post enables categories on server
				selectedCatIds.add(Integer.parseInt(topNewsBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(technologyBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(governmentTV.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(scienceBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(businessBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(lifestyleBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(sportsBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(crimesAndCourtBttn.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(artTV.getContentDescription().toString()));
				selectedCatIds.add(Integer.parseInt(topNewsBttn.getContentDescription().toString()));
				
				// clear all the deselected categories Ids list if not all categories are selected
				deselectedCatIds.clear();
				
			} else {
				for (int index = 0; index < selectedArrayList.size(); index++) {
					String categoryID = selectedArrayList.get(index);
					
					selectedCatIds.add(Integer.parseInt(categoryID));
					
					// remove category from deselected categories list if exists
					if(deselectedCatIds.contains(Integer.parseInt(categoryID))){
					deselectedCatIds.remove(Integer.valueOf(Integer.parseInt(categoryID)));
					}
					
					addCategoryInDatabase(categoryID);
				}
			}
			
			Intent homeIntent = new Intent(OneTimeSelectCategoryActivity.this,
					HomeActivity.class);
			homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(homeIntent);
			
			PostEnableCategoriesToServerTask postEnableCategoriesToServerTask = new PostEnableCategoriesToServerTask(selectedCatIds , deselectedCatIds);
			Thread postEnableCategoriesThread = new Thread(postEnableCategoriesToServerTask);
			postEnableCategoriesThread.start();
			
			break;
		case R.id.bt_top_news:

			String stringTopNews = topNewsBttn.getContentDescription().toString();
			if (!flagNews) {
				flagNews = true;
				buttonsCategoryArr[0].setBackgroundColor(getResources().getColor(colorsCategory[0]));
				selectedArrayList.add(stringTopNews);
			} else {
				flagNews = false;
				topNewsBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringTopNews);
			}

			break;
		case R.id.bt_technology:
			String stringTechnology = technologyBttn.getContentDescription().toString();
			if (!flagTechnology) {
				flagTechnology = true;
				buttonsCategoryArr[5].setBackgroundColor(getResources().getColor(colorsCategory[5]));
				selectedArrayList.add(stringTechnology);
			} else {
				flagTechnology = false;
				technologyBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringTechnology);
			}

			break;
		case R.id.governmentTV:

			String stringGovernment = governmentTV.getContentDescription().toString();
			if (!flagPolitics) {
				flagPolitics = true;
				buttonsCategoryArr[1].setBackgroundColor(getResources().getColor(colorsCategory[1]));
				selectedArrayList.add(stringGovernment);
			} else {
				flagPolitics = false;
				governmentTV.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringGovernment);
			}

			break;
		case R.id.bt_science:
			String stringScience = scienceBttn.getContentDescription().toString();
			if (!flagScience) {
				flagScience = true;
				buttonsCategoryArr[6].setBackgroundColor(getResources().getColor(colorsCategory[6]));
				selectedArrayList.add(stringScience);
			} else {
				flagScience = false;
				scienceBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringScience);
			}

			break;
		case R.id.bt_business:
			String stringBusiness = businessBttn.getContentDescription().toString();
			if (!flagBusiness) {
				flagBusiness = true;
				buttonsCategoryArr[2].setBackgroundColor(getResources().getColor(colorsCategory[2]));
				selectedArrayList.add(stringBusiness);
			} else {
				flagBusiness = false;
				businessBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringBusiness);
			}

			break;
		case R.id.bt_lifestyle:
			String stringLifeStyle = lifestyleBttn.getContentDescription().toString();

			if (!flagLifeStyle) {
				flagLifeStyle = true;
				buttonsCategoryArr[7].setBackgroundColor(getResources().getColor(colorsCategory[7]));
				selectedArrayList.add(stringLifeStyle);
			} else {
				flagLifeStyle = false;
				lifestyleBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringLifeStyle);
			}

			break;
		case R.id.bt_sports:
			String stringSports = sportsBttn.getContentDescription().toString();
			if (!flagSports) {
				flagSports = true;
				buttonsCategoryArr[3].setBackgroundColor(getResources().getColor(colorsCategory[3]));
				selectedArrayList.add(stringSports);
			} else {
				flagSports = false;
				sportsBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringSports);
			}

			break;
		case R.id.bt_crime_and_courts:

			String stringCrime = crimesAndCourtBttn.getContentDescription().toString();
			if (!flagCrimeCourt) {
				flagCrimeCourt = true;
				buttonsCategoryArr[8].setBackgroundColor(getResources().getColor(colorsCategory[8]));
				selectedArrayList.add(stringCrime);
			} else {
				flagCrimeCourt = false;
				crimesAndCourtBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringCrime);
			}

			break;
		case R.id.artTV:

			String stringArt = artTV.getContentDescription().toString();
			if (!flagArtAndEnt) {
				flagArtAndEnt = true;
				buttonsCategoryArr[4].setBackgroundColor(getResources().getColor(colorsCategory[4]));
				selectedArrayList.add(stringArt);
			} else {
				flagArtAndEnt = false;
				artTV.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringArt);
			}

			break;
		case R.id.bt_music:

			String stringMusic = musicBttn.getContentDescription().toString();
			if (!flagMusic) {
				flagMusic = true;
				buttonsCategoryArr[9].setBackgroundColor(getResources().getColor(colorsCategory[9]));
				selectedArrayList.add(stringMusic);
			} else {
				flagMusic = false;
				musicBttn.setBackgroundResource(R.drawable.category);
				selectedArrayList.remove(stringMusic);
			}

			break;

		}
	}

	
	private class AnimationTimer extends TimerTask {
		@Override
		public void run() {
			runOnUiThread(new Runnable() {
				public void run() {
					startAnimation();
				}
			});
		}
	}
	private void startAnimation() {
		
		if (!animationDone) {
			if (indx != 0) {
				buttonsCategoryArr[indx - 1].setBackgroundResource(R.drawable.category);
			} else {
				buttonsCategoryArr[9].setBackgroundResource(R.drawable.category);
			}
			buttonsCategoryArr[indx].setBackgroundColor(getResources().getColor(colorsCategory[indx]));
		}

		if (indx < 9) {
			indx++;
		} else if (firstTime && indx == 9) {
			indx = 0;
			firstTime = false;
		} else if (!firstTime && indx == 9) {
			animationDone = true;
		}

	}

	private void addCategoryInDatabase(String catID) {

		Category category = new Category();
		String categoryName  = null ;
		if(catID.equals(String.valueOf(RConstants.IPOD_MUSIC_CATEGORY))){
			categoryName = RConstants.MUSIC_FROM_DEVICE ;
		}else{
		 categoryName = database.getCategoryNameByCategoryId(Integer.parseInt(catID));
		}
		category.setName(categoryName);
		category.setCategoryId(Integer.parseInt(catID));
		category.setIsChecked(true);
		database.addMyCategory(category);
		

	}
	
	private class InitiateContentManagerThread extends Thread {
		@Override
		public void run() {
			super.run();
			ContentManager cntentManager = ContentManager.getContentManager(OneTimeSelectCategoryActivity.this);
			cntentManager.initiate();
		}

	}
	
private class PostEnableCategoriesToServerTask implements Runnable{
	
	private ArrayList<Integer> catListSelectedIds;
	private ArrayList<Integer> catListDeselectedIds;

	public PostEnableCategoriesToServerTask(ArrayList<Integer> catListSelectedIds , ArrayList<Integer> catListDeselectedIds) {
		
		this.catListSelectedIds = catListSelectedIds ;
		this.catListDeselectedIds = catListDeselectedIds ;
	}

	@Override
	public void run() {
		
			ArrayList<String> DeselectedcategoryNames = new ArrayList<String>();

			if (catListDeselectedIds.size() > 1) {
				DeselectedcategoryNames = categoryDbAdapter
						.getAllDeselectedCategoriesNames(catListDeselectedIds);
			}

			if (catListSelectedIds.size() > 1) {
				ArrayList<String> selectedCategoryNames = categoryDbAdapter
						.getAllSeletedCategoriesNames(catListSelectedIds);
		
				if (selectedCategoryNames.size() > 1) {
					String Categories = "{" + "\\n    " + "\\" + '"'
							+ RConstants.APP_VERSION + "\\" + '"' + " : "
							+ "\\" + '"' + versionName + "\\" + '"' + ","
							+ "\\n    " + "\\" + '"' + RConstants.PLATFORM
							+ "\\" + '"' + " : " + "\\" + '"'
							+ RConstants.OS_NAME + "\\" + '"' + "," + "\\n    "
							+ "\\" + '"' + RConstants.GIGYA_UID + "\\" + '"'
							+ " : " + "\\" + '"' + uid + "\\" + '"' + ","
							+ "\\n    " + "\\" + '"' + RConstants.LATITIDE
							+ "\\" + '"' + " : " + locationPoints.getLatts()
							+ "," + "\\n    " + "\\" + '"'
							+ RConstants.LONGTITUDE + "\\" + '"' + " : "
							+ locationPoints.getLongs() + "," + "\\n    ";
		
					for (int i = 0; i < (selectedCategoryNames.size() - 1); i++) {
						Categories += "\\" + '"' + selectedCategoryNames.get(i)
								+ "\\" + '"' + " : " + "\\" + '"'
								+ RConstants.ENABLED_CATEGORY + "\\" + '"'
								+ "," + "\\n    ";
					}
		
					for (int j = 0; j < DeselectedcategoryNames.size(); j++) {

						Categories += "\\" + '"'
								+ DeselectedcategoryNames.get(j) + "\\" + '"'
								+ " : " + "\\" + '"'
								+ RConstants.DISABLED_CATEGORY + "\\" + '"'
								+ "," + "\\n    ";
					}
		
					Categories += "\\"
							+ '"'
							+ selectedCategoryNames.get(selectedCategoryNames
									.size() - 1) + "\\" + '"' + " : " + "\\"
							+ '"' + RConstants.ENABLED_CATEGORY + "\\" + '"'
							+ "\\n}";

					String actionLogsToPostServer = "[{\n" + '"' + "actionType"
							+ '"' + " : " + '"' + RConstants.LOG_CATEGORIES
							+ '"' + "," + "\n" + '"'
							+ RConstants.ACTIVITY_TIMESTAMP + '"' + " : " + '"'
							+ (System.currentTimeMillis() / 1000) + '"' + ","
							+ "\n" + '"' + RConstants.USER_ID + '"' + " : "
							+ '"' + uuid + '"' + "," + "\n" + '"'
							+ RConstants.DATA_LOGS + '"' + " : " + '"'
							+ Categories + '"' + "\n" + "}]";
		  
//					Log.i("FirstTime", actionLogsToPostServer);
			PostUserAction postUserAction = new PostUserAction(actionLogsToPostServer);
			postUserAction.startLogging();
			
		}
		
		
		Map<String, String> logsParams = new HashMap<String, String>();
	 	  String logKey = RConstants.LOG_CATEGORIES;
	   	  
	   	  logsParams.put(RConstants.ACTION_TYPE, RConstants.CATEGORIES_SELECTED);
	   	  
	   	 for(int j = 0 ; j < selectedCategoryNames.size() ; j++){
	   		 logsParams.put(selectedCategoryNames.get(j), RConstants.ENABLED_CATEGORY);
	   	 }
	   	  
	   	  FlurryAgent.logEvent(logKey , logsParams);
		
		}
	}
	
}

}
