package com.rivet.app.core.pojo;

import android.util.Log;

import com.rivet.app.common.RConstants;

public class Tracking {
	


	/**
	 * The player has closed
	 */
	public final static int EVENT_FINAL_RETURN = 0;

	/**
	 * The XML has loaded
	 */
	public final static int EVENT_IMPRESSION = 1;

	/**
	 * The video starts
	 */
	public final static int EVENT_START = 2;

	/**
	 * 25% of the video have player
	 */
	public final static int EVENT_FIRSTQ = 3;

	/**
	 * 50% of the video have played
	 */
	public final static int EVENT_MID = 4;

	/**
	 * 75% of the video have played
	 */
	public final static int EVENT_THIRDQ = 5;

	/**
	 * 100% of the video have played
	 */
	public final static int EVENT_COMPLETE = 6;

	/**
	 * Sound of player was muted
	 */
	public final static int EVENT_MUTE = 7;

	/**
	 * Sound of player was unmuted
	 */
	public final static int EVENT_UNMUTE = 8;

	/**
	 * Video was paused
	 */
	public final static int EVENT_PAUSE = 9;

	/**
	 * Video was resumed
	 */
	public final static int EVENT_RESUME = 10;

	/**
	 * Player went fullscreen
	 */
	public final static int EVENT_FULLSCREEN = 11;

	/**
	 * Mapping of event descriptions in VAST xml to internal names
	 */
	public final String[] EVENT_MAPPING = new String[] { "finalReturn",
			"impression", "start", "firstQuartile", "midpoint",
			"thirdQuartile", "complete", "mute", "unmute", "pause",
			"resume", "fullscreen" };

	private int event;

	private String url;

	private String TAG = "Tracking Pojo";

	/**
	 * Create a new bean for pinging tracking URLs upon specific VAST events
	 * 
	 * @param e
	 *            VAST Event name
	 * @param url
	 *            Tracking URL
	 */
	public Tracking(String e, String url) {
		this.event = findEvent(e);
		this.url = url;
		
		if(RConstants.BUILD_DEBUBG){
			
		Log.d(TAG , "VAST tracking url [" + e + ", " + this.event + "]: "
				+ this.url);
		
		}
	}

	private int findEvent(String event) {
		for (int i = 0; i < EVENT_MAPPING.length; i++) {
			if (EVENT_MAPPING[i].equals(event)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the static integer as internal representation of the VAST event
	 * 
	 * @return Internal integer for the event
	 */
	public int getEvent() {
		return this.event;
	}

	/**
	 * Get the tracking url associated with this event
	 * 
	 * @return Tracking url
	 */
	public String getUrl() {
		return this.url;
	}



}
