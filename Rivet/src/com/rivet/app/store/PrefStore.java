package com.rivet.app.store;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.rivet.app.common.RConstants;

public class PrefStore {

	private SharedPreferences sharedPreferences;

	public PrefStore(Context context) {
		sharedPreferences = (SharedPreferences) context.getSharedPreferences(
				RConstants.RIVET_PREFERENCES, Context.MODE_PRIVATE);
	}

	public void setStringData(String key, String value) {

		Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	
	public String getStringData(String key, String defaultValue) {
		String value = sharedPreferences.getString(key, defaultValue);
		return value;
	}

	public void setLongData(String key, Long value) {

		Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	
	public Long getLongData(String key, Long defaultValue) {
		Long value = sharedPreferences.getLong(key, defaultValue);
		return value;
	}

	
	public void setBooleanData(String key, boolean value) {

		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	public boolean getBooleanData(String key, boolean defaultValue) {

		boolean value = sharedPreferences.getBoolean(key, defaultValue);

		return value;
	}

	public int getIntegerData(String key , int defaultValue) {
		
		int value	=	sharedPreferences.getInt(key, defaultValue);
		return value;
	}
	
	public void setIntegerData(String key , int value){
		
		Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();

	}
	
	public void setFloatData(String key , float value){
		
		Editor editor = sharedPreferences.edit();
		editor.putFloat(key, value);
		editor.commit();
		
	}
	
	public float getFloatData(String key , float defaultValue){
		
		float value = sharedPreferences.getFloat(key, defaultValue);
		return value;
		
	}

}
