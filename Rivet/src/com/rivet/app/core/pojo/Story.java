package com.rivet.app.core.pojo;

import java.util.ArrayList;

/**
 * Created by Brian on 12/6/14.
 */
public class Story {
    String additionalDescription;
    String categoryName;
    String anchorName;
    double audioLength;
    String bannerActionUrl;
    String bannerImageUrl;
    String categoriesString;
    int coverageID;
    long creationTimestamp;
    int displayCategory;
    long expirationTimestamp;
    int externalID;
    boolean isLead;
    ArrayList<String> keyWordsString;
    int primaryCategory;
    String producedBy;
    String source;
    int storyTypeID;
    String title;
    String titleActionUrl;
    String titleImageUrl;
    int trackID;
    long uploadTimestamp;
    String url;
    Category primary_category_r;
    ArrayList<String> additional_category_r;
    String stream_url;
    int parentCategoryID=0;
    int offset;
    int IsLike ;
    
    
    // Ads Info variables
    private String adsImageURL ;
    private String companionClickThroughUrl ;
    private String adsSystemName ;
    private String AdsDuration ;
    private String impressionTrackerUrl ;
    

    public int getIsLike() {
		return IsLike;
	}

	public void setIsLike(int isLike) {
		IsLike = isLike;
	}

	public int getParentCategoryID() {
		return parentCategoryID;
	}

	public void setParentCategoryID(int parentCategoryID) {
		this.parentCategoryID = parentCategoryID;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getAdditionalDescription() {
        return additionalDescription;
    }

    public void setAdditionalDescription(String additionalDescription) {
        this.additionalDescription = additionalDescription;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public double getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(double audioLength) {
        this.audioLength = audioLength;
    }

    public String getBannerActionUrl() {
        return bannerActionUrl;
    }

    public void setBannerActionUrl(String bannerActionUrl) {
        this.bannerActionUrl = bannerActionUrl;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public String getCategoriesString() {
        return categoriesString;
    }

    public void setCategoriesString(String categoriesString) {
        this.categoriesString = categoriesString;
    }

    public int getCoverageID() {
        return coverageID;
    }

    public void setCoverageID(int coverageID) {
        this.coverageID = coverageID;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public int getDisplayCategory() {
        return displayCategory;
    }

    public void setDisplayCategory(int displayCategory) {
        this.displayCategory = displayCategory;
    }

    public long getExpirationTimestamp() {
        return expirationTimestamp;
    }

    public void setExpirationTimestamp(long expirationTimestamp) {
        this.expirationTimestamp = expirationTimestamp;
    }

    public int getExternalID() {
        return externalID;
    }

    public void setExternalID(int externalID) {
        this.externalID = externalID;
    }

    public boolean getIsLead() {
        return isLead;
    }

    public void setIsLead(boolean isLead) {
        this.isLead = isLead;
    }

    public ArrayList<String> getKeyWordsString() {
        return keyWordsString;
    }

    public void setKeyWordsString(ArrayList<String> keyWordsString) {
        this.keyWordsString = keyWordsString;
    }

    public int getPrimaryCategory() {
        return primaryCategory;
    }

    public void setPrimaryCategory(int primaryCategory) {
        this.primaryCategory = primaryCategory;
    }

    public String getProducedBy() {
        return producedBy;
    }

    public void setProducedBy(String producedBy) {
        this.producedBy = producedBy;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getStoryTypeID() {
        return storyTypeID;
    }

    public void setStoryTypeID(int storyTypeID) {
        this.storyTypeID = storyTypeID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleActionUrl() {
        return titleActionUrl;
    }

    public void setTitleActionUrl(String titleActionUrl) {
        this.titleActionUrl = titleActionUrl;
    }

    public String getTitleImageUrl() {
        return titleImageUrl;
    }

    public void setTitleImageUrl(String titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
    }

    public int getTrackID() {
        return trackID;
    }

    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }

    public long getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(long uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Category getPrimary_category_r() {
        return primary_category_r;
    }

    public void setPrimary_category_r(Category primary_category_r) {
        this.primary_category_r = primary_category_r;
    }

    public ArrayList<String> getAdditional_category_r() {
        return additional_category_r;
    }

    public void setAdditional_category_r(ArrayList<String> additional_category_r) {
        this.additional_category_r = additional_category_r;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
    
    // getter and setter methods for ads

	public String getAdsImageURL() {
		return adsImageURL;
	}

	public void setAdsImageURL(String adsImageURL) {
		this.adsImageURL = adsImageURL;
	}

	public String getCompanionClickThroughUrl() {
		return companionClickThroughUrl;
	}

	public void setCompanionClickThroughUrl(String companionClickThroughUrl) {
		this.companionClickThroughUrl = companionClickThroughUrl;
	}

	public String getAdsSystemName() {
		return adsSystemName;
	}

	public void setAdsSystemName(String adsSystemName) {
		this.adsSystemName = adsSystemName;
	}

	public String getAdsDuration() {
		return AdsDuration;
	}

	public void setAdsDuration(String adsDuration) {
		AdsDuration = adsDuration;
	}

	public String getImpressionTrackerUrl() {
		return impressionTrackerUrl;
	}

	public void setImpressionTrackerUrl(String impressionTrackerUrl) {
		this.impressionTrackerUrl = impressionTrackerUrl;
	}


}