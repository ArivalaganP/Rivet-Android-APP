package com.rivet.app.common;

import com.rivet.app.R;

public class HHRHelper {

	
	public static int getCategoryColor(int catID) {

		if (catID == RConstants.TOP_NEWS_CATEGORY){

			return R.color.topNewsColor;
		}else if (catID == RConstants.BREAKING_NEWS_CATEGORY){

			return R.color.breakingNewsColor;
		}
		else if (catID == RConstants.ENTERTAINMENT_CATEGORY)

			return R.color.entertainmentColor;
		else if (catID == RConstants.BUSINESS_CATEGORY)

			return R.color.businessColor;
		else if (catID == RConstants.LIFE_STYLE_CATEGORY ){

			return R.color.lifeStyleColor;
		}
		else if (catID == RConstants.TECHNOLOGY_CATEGORY)

			return R.color.technologyColor;
		else if (catID == RConstants.SPORTS_CATEGORY ){

			return R.color.sportsColor;
		
		}else if (catID == RConstants.POLITICS_CATEGORY){

			return R.color.politicsColor;
		}else if (catID == RConstants.SCIENCE_CATEGORY ){

			return R.color.scienceColor;
		}else if (catID == RConstants.CRIME_AND_COURT )
			//TODO: crime&court to be added color
			return R.color.outOfBoxColor;
		else if (catID == RConstants.IPOD_MUSIC_CATEGORY)

			return R.color.unassignedColor;

		return R.color.unassignedColor;
	}
}
