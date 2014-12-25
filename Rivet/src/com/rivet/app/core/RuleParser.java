package com.rivet.app.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.util.Log;

import com.rivet.app.adapter.AdsDbAdapter;
import com.rivet.app.adapter.RulesDBAdapter;
import com.rivet.app.common.RConstants;

/**
 * Created by brian on 12/6/14.
 */
public class RuleParser implements Runnable {

	private RulesDBAdapter rulesDbAdapter;
	private AdsDbAdapter adsDbAdapter;
	public static String TAG = "RuleParser";
	
	public RuleParser(Context context) {
		rulesDbAdapter =  RulesDBAdapter.getRulesDBAdapter(context);
		adsDbAdapter = AdsDbAdapter.getAdsDbAdapter(context);
	}

	public String getRuleString() {
		return ruleString;
	}

	public void setRuleString(String ruleString) {
		this.ruleString = ruleString;
	}

	String ruleString;

	public void run() {
		
		//TODO: i might need to take in in ram not in db
		//clear the rules table 
		for(;;){
			
				try{
		
					try {
						rulesDbAdapter.resetRulesTable();
						
						AdRule adRule = new AdRule();
						IPodRule iPodRule = new IPodRule(0, 0, 0);
						PodcastRule podcastRule;
						SplashRule splashRule = new SplashRule(0);
						PlaylistRule playListRule;

						String rulesSeparator = "\n";
						String ruleParametersSeparator = "\\,";
						String[] rules = ruleString.split(rulesSeparator);
						ArrayList<SplashRule> splashRules = new ArrayList<SplashRule>();
						ArrayList<PodcastRule> podcastRules = new ArrayList<PodcastRule>();
						ArrayList<AdRule> adRules = new ArrayList<AdRule>();
						ArrayList<IPodRule> ipodRules = new ArrayList<IPodRule>();
						ArrayList<PlaylistRule> playlistRules = new ArrayList<PlaylistRule>(5);

						
						/* /* */for (int idx = 0; idx < rules.length; idx++) {
							String currentString = rules[idx];
							// check if this is a comment line
							String trimmedRule = currentString.trim();

							String trimmedRuleString = trimmedRule.replaceAll("\r\n", " ")
									.trim();
							// skip the empty lines
							if (trimmedRuleString.length() < 1) {
								continue;
							}

							if (trimmedRuleString.startsWith("#")) {
								if (RConstants.BUILD_DEBUBG) {
									Log.i(TAG, "Skipping commented rule" + trimmedRule);

								}
								continue;
							}

							String[] ruleParams = trimmedRuleString
									.split(ruleParametersSeparator);

							//
							if (ruleParams.length > 1) {
								if ((ruleParams[0]).equals(RConstants.ADVERTISEMENT_TITLE)) {
									int noOfStoriesPlayed = 0;
									int lengthOfStoriesPlayedInMintues = 0;
									int desiredMinimumAdsCount = 0;

									noOfStoriesPlayed = Integer.parseInt(ruleParams[1]
											.replaceAll(" ", ""));
									lengthOfStoriesPlayedInMintues = Integer
											.parseInt(ruleParams[2].replaceAll(" ", ""));
									desiredMinimumAdsCount = Integer.parseInt(ruleParams[3]
											.replaceAll(" ", ""));

									adRule.setNoOfStoriesPlayed(noOfStoriesPlayed);
									adRule.setLengthOfStoriesPlayedInMintues(lengthOfStoriesPlayedInMintues);
									adRule.setDesiredMinimumAdsCount(desiredMinimumAdsCount);

									adRules.add(adRule);
									
									// entry in database of ads rules
						         	adsDbAdapter.addAdsInDB(adRule);

								} else if (ruleParams[0].equals(RConstants.IPOD_TITLE)) {
									int numberOfStoriesPlayed = 0;
									int lengthOfStoriesPlayedInMinutes = 0;
									int desiredMinimumLengthOfSongsInMinutes = 0;

									numberOfStoriesPlayed = Integer.parseInt(ruleParams[1]
											.replaceAll(" ", ""));
									lengthOfStoriesPlayedInMinutes = Integer
											.parseInt(ruleParams[2].replaceAll(" ", ""));
									desiredMinimumLengthOfSongsInMinutes = Integer
											.parseInt(ruleParams[3].replaceAll(" ", ""));

									iPodRule.setNumberOfStoriesPlayed(numberOfStoriesPlayed);
									iPodRule.setLengthOfStoriesPlayedInMinutes(lengthOfStoriesPlayedInMinutes);
									iPodRule.setDesiredMinimumLengthOfSongsInMinutes(desiredMinimumLengthOfSongsInMinutes);

									ipodRules.add(iPodRule);
									
									rulesDbAdapter.addIPodRule(iPodRule);
						             
								} else if (ruleParams[0].equals(RConstants.POADCAST_TITLE)) {
									podcastRule = new PodcastRule(0, 0, "", 0);
									int mStoryLimit = 0;
									int mMaxLength = 0;
									String mCategoryName = "";
									int mStoryCount = 0;

									if (ruleParams.length < 3) {
										continue;
									}
									mCategoryName = ruleParams[1];
									if (mCategoryName.isEmpty()) {
										continue;
									}
									// if(/*category exists, i.e. is is a valid category?*/){
									// TODO:
									// continue;
									// }
									mStoryCount = Integer.parseInt(ruleParams[2].replaceAll(
											" ", ""));
									mMaxLength = Integer.parseInt(ruleParams[3].replaceAll(" ",
											""));

									podcastRule.setStoryLimit(mStoryLimit);
									podcastRule.setMaxLength(mMaxLength);
									podcastRule.setCategoryName(mCategoryName.trim());
									podcastRule.setStoryCount(mStoryCount);
								
									rulesDbAdapter.addPodcastRule(podcastRule);
								
									podcastRules.add(podcastRule);
									

								} else if (ruleParams[0].equals(RConstants.SPLASH_TITLE)) {
									int delayInHours = 0;

									if (ruleParams.length >= 2) {
										delayInHours = Integer.parseInt(ruleParams[1]
												.replaceAll(" ", ""));
										splashRule.setDelayInHours(delayInHours);
										RConstants.DELAY_IN_HOURS = delayInHours ;
										splashRules.add(splashRule);
										
									}
								} else // this is a categories rule then
								{
									// CATEGORY, # of normal stories, # of lead stories, REGION

									playListRule = new PlaylistRule("", 0, 0, null);

									String categoryName;
									int nonLeadStoriesCount = 0;
									int leadStoriesCount = 0;
									List<String> regions = new ArrayList<String>();

									categoryName = ruleParams[0];

									if (categoryName == null) {
										// skip empty ones
										continue;
									}
									
									nonLeadStoriesCount = Integer.parseInt(ruleParams[1].replaceAll(" ", ""));
									
									if(ruleParams.length > 2){
										//there could be chance there is no lead story like welcome
										leadStoriesCount = Integer.parseInt(ruleParams[2].replaceAll(" ", ""));
									}
									
									if (ruleParams.length > 3) {
										String region = ruleParams[3];
										String[] multipleRegions = region.split("|");
										for (int i = 0; i < multipleRegions.length; i++) {
											regions.add(multipleRegions[i]);
										}
										playListRule.setRegions(regions);
									}

									playListRule.setCategoryName(categoryName.trim());
									playListRule.setNonLeadStoriesCount(nonLeadStoriesCount);
									playListRule.setLeadStoriesCount(leadStoriesCount);
									
									// insert playListRule in database
									rulesDbAdapter.addPlayListRule(playListRule);
								
									playlistRules.add(playListRule);

								}// else categories rule
							} else {
								// report the detected error
								Log.d(TAG, RConstants.WRONG_RULE_DETECT);

							}
							
						}/* */
						//get out of loop
					break;

				} catch (NullPointerException nullPointerException) {
					Log.i(TAG, "null pointer while parsing rule");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					nullPointerException.printStackTrace();
				}

			} catch (SQLiteDatabaseLockedException sqlexception) {
				Log.i(TAG, "RuleBuilder DB Locked ");
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
			}
	}
	
	public RuleParser(String ruleString,Context context) {
		this.ruleString = ruleString;
		rulesDbAdapter =  RulesDBAdapter.getRulesDBAdapter(context);
		adsDbAdapter = AdsDbAdapter.getAdsDbAdapter(context);
	}

	public void doParsing() {
	}

}