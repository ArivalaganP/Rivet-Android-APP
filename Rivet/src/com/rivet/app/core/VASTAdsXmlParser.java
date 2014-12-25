package com.rivet.app.core;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.rivet.app.BaseActivity;
import com.rivet.app.adapter.StoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Story;
import com.rivet.app.core.pojo.Tracking;

public class VASTAdsXmlParser {
	
	public final static String TAG = "VASTAdsXmlParser";
	private String clickThroughUrl = null ;
//	private String clickTrackingUrl = null ;
	private int skipOffset = 0 ;
	private String impressionTrackerUrl = null ;
	private String AdsDuration = null ;
	private String adsAudioFileURL = null ;
	private List<Tracking> trackings = new ArrayList<Tracking>();
	private String data = null ;
	private String adsTitle = null ;
	private String adsSystemName = null ;
	private String adsImageURL = null ;
	private String companionClickThroughUrl = null ;
	private Context context;
	
	// pojo class to hold the ad data
	Story adsStory = new Story();
	private int latestAdTrackId;
	private boolean flagAdsLoadingFailed = false ;


	public VASTAdsXmlParser(Context context , String data) {
		this.trackings = new ArrayList<Tracking>();
		this.context = context ;
		this.data = data ;
		
	}
	
	synchronized public void start(){
		try {
			readVAST(this.data);
		} catch (Exception e) {
			if(RConstants.BUILD_DEBUBG){
			Log.i(TAG, "Error parsing VAST XML", e);
			}
			((BaseActivity)context).failedToLoadAds();
			if(RConstants.BUILD_DEBUBG){
			Log.e(TAG, "failed to Load ads");
			}
		}
		
	}

	private void readVAST(String data) throws XmlPullParserException,
			IOException {

		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(new StringReader(data));
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, null, RConstants.VAST_START_TAG);
		flagAdsLoadingFailed = true ;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (parser.getName().equals(RConstants.VAST_AD_TAG)) {
				flagAdsLoadingFailed = false ;
				readAd(parser);
				
			}
		}
		
		if(flagAdsLoadingFailed){
			((BaseActivity)context).failedToLoadAds();
			
			if(RConstants.BUILD_DEBUBG){
				Log.e(TAG, "failed to Load ads");
				}
		}
	
	}

	private void readAd(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_AD_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name.equals(RConstants.VAST_INLINE_TAG)) {
				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "VAST file contains inline ad information.");
				}
				readInLine(p);
			}
			if (name.equals(RConstants.VAST_WRAPPER_TAG)) {
				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "VAST file contains wrapped ad information.");
				}
			
				readWrapper(p);
			}
		}
	}

	private void readMediaFiles(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_MEDIAFILES_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_MEDIAFILE_TAG)) {
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_MEDIAFILE_TAG);
				// get ads audio url from xml file
				this.adsAudioFileURL = readText(p);
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_MEDIAFILE_TAG);
				
				 latestAdTrackId = ((BaseActivity)context).prefStore.getIntegerData(RConstants.LATEST_AD_TRACK_ID , 1);
				
				// insert ads information to database here 
				
				adsStory.setTitle(this.adsTitle);
				adsStory.setUrl(this.adsAudioFileURL);
				adsStory.setAdsImageURL(this.adsImageURL);
				adsStory.setCompanionClickThroughUrl(this.companionClickThroughUrl);
				adsStory.setTrackID(latestAdTrackId);
			    adsStory.setAdsSystemName(this.adsSystemName);
			    adsStory.setAdsDuration(this.AdsDuration);
			    adsStory.setCategoryName(RConstants.ADVERTISEMENT_TITLE);
			    adsStory.setImpressionTrackerUrl(this.impressionTrackerUrl);
			    adsStory.setPrimaryCategory(RConstants.ADS_CATEGORY);
			    adsStory.setExpirationTimestamp(1);
			    adsStory.setCreationTimestamp(System.currentTimeMillis());
			    adsStory.setUploadTimestamp(System.currentTimeMillis());
			    
			   StoryDBAdapter storyDBAdapter = StoryDBAdapter.getStoryDBAdapter(this.context);
			   storyDBAdapter.addAds(adsStory);
			   
			   // increase Ads track id each time it updates a new one in database
			   ((BaseActivity)context).prefStore.setIntegerData(RConstants.LATEST_AD_TRACK_ID, (latestAdTrackId + 1) );

				
				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Mediafile url: " + this.adsAudioFileURL);
				}
			} else {
				skip(p);
			}
		}
	}

	private void readTrackingEvents(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_TRACKINGEVENTS_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_TRACKING_TAG)) {
				String ev = p.getAttributeValue(null, "event");
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_TRACKING_TAG);
				this.trackings.add(new Tracking(ev, readText(p)));
				
				if(RConstants.BUILD_DEBUBG){
				Log.d(TAG, "Added VAST tracking \"" + ev + "\"");
				}
				
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_TRACKING_TAG);
			} else {
			//	skip(p);
			}
		}
	}

	private void readVideoClicks(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_VIDEOCLICKS_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_CLICKTHROUGH_TAG)) {
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_CLICKTHROUGH_TAG);
				this.clickThroughUrl = readText(p);
				
				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Video clickthrough url: " + clickThroughUrl);
				}
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_CLICKTHROUGH_TAG);
			} else if (name != null && name.equals(RConstants.VAST_CLICKTRACKING_TAG)) {
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_CLICKTRACKING_TAG);
			//	this.clickTrackingUrl = 
						readText(p);
				
				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Video clicktracking url: " + clickThroughUrl);
				}
				
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_CLICKTRACKING_TAG);
			} else {
				skip(p);
			}
		}
	}

	private void readLinear(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_LINEAR_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			String name = p.getName();
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (name != null && name.equals(RConstants.VAST_DURATION_TAG)) {
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_DURATION_TAG);
				// to get the duration of ads 
				this.AdsDuration = readText(p);
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_DURATION_TAG);
				
				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Video duration: " + this.AdsDuration);
				}
			} else if (name != null && name.equals(RConstants.VAST_MEDIAFILES_TAG)) {
				readMediaFiles(p);
			} else if (name != null && name.equals(RConstants.VAST_VIDEOCLICKS_TAG)) {
				readVideoClicks(p);
			} else {
				skip(p);
			}
		}
	}

	private void readCreative(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_CREATIVE_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_LINEAR_TAG)) {
				String skipoffsetStr = p.getAttributeValue(null, "skipoffset");
				if (skipoffsetStr != null && skipoffsetStr.indexOf(":") < 0) {
					skipOffset = Integer.parseInt(skipoffsetStr.substring(0,
							skipoffsetStr.length() - 1));
					if(RConstants.BUILD_DEBUBG){
					Log.i(TAG, "Linear skipoffset is " + skipOffset + " [%]");
					}
				} else if (skipoffsetStr != null
						&& skipoffsetStr.indexOf(":") >= 0) {
					skipOffset = -1;
					
					if(RConstants.BUILD_DEBUBG){
					Log.i(
							TAG,
							"Absolute time value ignored for skipOffset in VAST xml. Only percentage values will pe parsed.");
					}
				}
			readLinear(p);
			}else if(name != null && name.equals(RConstants.VAST_COMPAIN_ADS)){
				readCompanionAds(p);
			} else {
				skip(p);
			}
		}
	}
	
	public void readCompanionAds(XmlPullParser p) throws IOException , XmlPullParserException{
		

		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_COMPAIN_ADS);
		while (p.next() != XmlPullParser.END_TAG) {
			String name = p.getName();
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			 if (name != null && name.equals(RConstants.VAST_COMPAIN)) {
			
			    readCompanion(p);		    

			} else {
				skip(p);
			}
		}

	}
	
	public void readCompanion(XmlPullParser p) throws IOException , XmlPullParserException {

		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_COMPAIN);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_STATIC_RESOURCE)) {
				p.require(XmlPullParser.START_TAG, null,
						RConstants.VAST_STATIC_RESOURCE);
				// get ads image URL to show in banner
				this.adsImageURL = readText(p);
				p.require(XmlPullParser.END_TAG, null,
						RConstants.VAST_STATIC_RESOURCE);

				if (RConstants.BUILD_DEBUBG) {
					Log.i(TAG, "Ads Image URL " + this.adsImageURL);
				}
			} else if (name != null
					&& name.equals(RConstants.VAST_TRACKINGEVENTS_TAG)) {
				readTrackingEvents(p);
			}else if (name != null
					&& name.equals(RConstants.VAST_COMPAIN_CLICK_THROUGH)) {

				p.require(XmlPullParser.START_TAG, null,
						RConstants.VAST_COMPAIN_CLICK_THROUGH);
				this.companionClickThroughUrl = readText(p);
				p.require(XmlPullParser.END_TAG, null,
						RConstants.VAST_COMPAIN_CLICK_THROUGH);
				
			// update ads data at same raw in database
				adsStory.setTitle(this.adsTitle);
				adsStory.setUrl(this.adsAudioFileURL);
				adsStory.setAdsImageURL(this.adsImageURL);
				adsStory.setCompanionClickThroughUrl(this.companionClickThroughUrl);
				adsStory.setTrackID(latestAdTrackId);
			    adsStory.setAdsSystemName(this.adsSystemName);
			    adsStory.setAdsDuration(this.AdsDuration);
			    adsStory.setCategoryName(RConstants.ADVERTISEMENT_TITLE);
			    adsStory.setImpressionTrackerUrl(this.impressionTrackerUrl);
			    adsStory.setPrimaryCategory(RConstants.ADS_CATEGORY);
			    adsStory.setExpirationTimestamp(1);
			    adsStory.setCreationTimestamp(System.currentTimeMillis());
			    adsStory.setUploadTimestamp(System.currentTimeMillis());
			    
			   // update in database 
			    StoryDBAdapter storyDBAdapter = StoryDBAdapter.getStoryDBAdapter(this.context);
				storyDBAdapter.updateAds(adsStory);


				if (RConstants.BUILD_DEBUBG) {
					Log.i(TAG, "Companion click through url "
							+ this.companionClickThroughUrl);
				}
			} else {
				skip(p);
			}
		}

	}

	private void readCreatives(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_CREATIVES_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_CREATIVE_TAG)) {
				readCreative(p);
			} else {
				skip(p);
			}
		}
	}

	private void getWrappedVast(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_ADTAGURI_TAG);
		readText(p);
		p.require(XmlPullParser.END_TAG, null, RConstants.VAST_ADTAGURI_TAG);
		

	}

	private void readWrapper(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_WRAPPER_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_IMPRESSION_TAG)) {
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_IMPRESSION_TAG);
				// to get impression track url
				this.impressionTrackerUrl = readText(p);
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_IMPRESSION_TAG);

				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Impression tracker url: "
						+ this.impressionTrackerUrl);
				}
				
			} else if (name != null && name.equals(RConstants.VAST_CREATIVES_TAG)) {
				readCreatives(p);
			} else if (name != null && name.equals(RConstants.VAST_ADTAGURI_TAG)) {
				getWrappedVast(p);
			} else {
				skip(p);
			}
		}
	}
	

	private void readInLine(XmlPullParser p) throws IOException,
			XmlPullParserException {
		p.require(XmlPullParser.START_TAG, null, RConstants.VAST_INLINE_TAG);
		while (p.next() != XmlPullParser.END_TAG) {
			if (p.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = p.getName();
			if (name != null && name.equals(RConstants.VAST_IMPRESSION_TAG)) {
				
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_IMPRESSION_TAG);
				
				this.impressionTrackerUrl = readText(p);
				
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_IMPRESSION_TAG);

				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Impression tracker url: " + this.impressionTrackerUrl+"");
				
				}
			} else if (name != null && name.equals(RConstants.VAST_ADS_TITLE_TAG)){
				
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_ADS_TITLE_TAG);
				
				this.adsTitle = readText(p);
				
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_ADS_TITLE_TAG);
			
			}else if (name != null && name.equals(RConstants.VAST_ADS_SYSTEM_TAG)) {
				p.require(XmlPullParser.START_TAG, null, RConstants.VAST_ADS_SYSTEM_TAG);
				// get name of ads system name
				this.adsSystemName = readText(p);
				
				p.require(XmlPullParser.END_TAG, null, RConstants.VAST_ADS_SYSTEM_TAG);

				if(RConstants.BUILD_DEBUBG){
				Log.i(TAG, "Ads System Tag : " + "");
				}
				
			}else if (name != null && name.equals(RConstants.VAST_CREATIVES_TAG)) {
				readCreatives(p);
			} else {
				skip(p);
			}
		}
	}
	
	public void readAdTitle(){
		if(RConstants.BUILD_DEBUBG){
			Log.i(TAG, "Ads Title Tag : " + "");
			}
	}

	private void skip(XmlPullParser p) throws XmlPullParserException,
			IOException {
		if (p.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (p.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		} else {
			if(RConstants.BUILD_DEBUBG){
			Log.i(TAG, "No text: " + parser.getName());
			}
		}
		return result.trim();
	}




}
