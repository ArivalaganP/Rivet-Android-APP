package com.rivet.app.core.pojo;


/**
 * Created by Brian on 12/6/14.
 */
public class Category extends Object {

	public  Category() {

	}
	
	public Category (int id , String name){
		this.categoryId = id;
		this.name = name;
	}

	private boolean allowRegions;
	int categoryId;
	int isLearning;
	int isHidden;
	int isTimeSensitive;
	String name;
	int parentCategoryId;
	private UserInfo enabled_user_info_r;
	private UserInfo fav_user_info_r;
	private boolean ischecked;

	public boolean isAllowRegions() {
		return allowRegions;
	}

	public void setAllowRegions(boolean allowRegions) {
		this.allowRegions = allowRegions;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getIsLearning() {
		return isLearning;
	}

	public void setIsLearning(int isLearning) {
		this.isLearning = isLearning;
	}

	public int getIsHidden() {
		return isHidden;
	}

	public void setIsHidden(int isHidden) {
		this.isHidden = isHidden;
	}

	public int getIsTimeSensitive() {
		return isTimeSensitive;
	}

	public void setIsTimeSensitive(int isTimeSensitive) {
		this.isTimeSensitive = isTimeSensitive;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentCategoryId() {
		return parentCategoryId;
	}

	public void setParentCategoryId(int parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}

	public UserInfo getEnabled_user_info_r() {
		return enabled_user_info_r;
	}

	public void setEnabled_user_info_r(UserInfo enabled_user_info_r) {
		this.enabled_user_info_r = enabled_user_info_r;
	}

	public UserInfo getFav_user_info_r() {
		return fav_user_info_r;
	}

	public void setFav_user_info_r(UserInfo fav_user_info_r) {
		this.fav_user_info_r = fav_user_info_r;
	}

	public boolean getisChecked() {
		return ischecked;
	}

	public void setIsChecked(boolean ischecked) {
		this.ischecked = ischecked;

	}

}
