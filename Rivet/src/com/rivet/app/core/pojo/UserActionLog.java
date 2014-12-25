package com.rivet.app.core.pojo;

public class UserActionLog {
	
	private String CurrentUserActionType ;
	private String currentStoryPosition ;
	private String currentStoryTitle ;
	private String currentStoryTrackId ;
	private int isPostedOnServer ;
	private int dbRowId;
	private long storyActionTimeStamp;
	private double longs=0.0;
	private double latts=0.0;
	
	
	public int isPostedOnServer(){
		return isPostedOnServer ;
	}
	
	public void setPostedOnServer(int isPostedOnServer){
		this.isPostedOnServer = isPostedOnServer ;
	}
	
	public String getCurrentUserActionType() {
		return CurrentUserActionType;
	}
	public void setCurrentUserActionType(String currentUserActionType) {
		CurrentUserActionType = currentUserActionType;
	}
	public String getCurrentStoryPosition() {
		return currentStoryPosition;
	}
	public void setCurrentStoryPosition(String currentStoryPosition) {
		this.currentStoryPosition = currentStoryPosition;
	}
	public String getCurrentStoryTitle() {
		return currentStoryTitle;
	}
	public void setCurrentStoryTitle(String currentStoryTitle) {
		this.currentStoryTitle = currentStoryTitle;
	}
	public String getCurrentStoryTrackId() {
		return currentStoryTrackId;
	}
	public void setCurrentStoryTrackId(String currentStoryTrackId) {
		this.currentStoryTrackId = currentStoryTrackId;
	}
	
	public void setStoryActionTimeStamp(long storyActionTimeStamp){
		this.storyActionTimeStamp = storyActionTimeStamp ;
	}
	
	public long getStoryActionTimeStamp() {
		// TODO Auto-generated method stub
		return storyActionTimeStamp;
	}

	/**
	 * 
	 * @param dbRowId :  getter setter for dbRowId are only used for delete the entry whose content has been posted to server
	 */
	public void setDbRowId(int dbRowId) {
		this.dbRowId = dbRowId ;
	}
	
	public int getDbRowId(){
		return dbRowId;
	}

	public double getLongs() {
		return longs;
	}

	public void setLongs(double longs) {
		this.longs = longs;
	}

	public double getLatts() {
		return latts;
	}

	public void setLatts(double latts) {
		this.latts = latts;
	}




}
