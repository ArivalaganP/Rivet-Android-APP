package com.rivet.app.abstracts;

import java.util.List;

import com.rivet.app.common.RConstants;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public  class  RDBAdapterBase {
	
	private  DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDbForWrite;
	private final Context mCtx;
	protected SQLiteDatabase mDbForRead;
	
	private static final String DATABASE_NAME = RConstants.DATABASE_NAME;
	private static final int DATABASE_VERSION = RConstants.DATABASE_VERSION;
	
	private static final String TAG="RDBAdapterBase";
	
	/****************************************************************************
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context with in which to work
	 *****************************************************************************/
	public RDBAdapterBase(Context ctx , boolean isForWrite) {
		this.mCtx = ctx;
		if (!isOpen())
			if(isForWrite){
				
			openForWrite();
			
			}else{
				
			openForRead();
			
			}
	}

	/******************************************************************************
	 * Open the RDBAdapterBase database. If it cannot be opened, try to create a
	 * new instance of the database. If it cannot be created, throw an exception
	 * to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws android.database.SQLException
	 *             if the database could be neither opened or created
	 ********************************************************************************/
	public RDBAdapterBase openForWrite() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDbForWrite = mDbHelper.getWritableDatabase();
		return this;
	}
	
	public RDBAdapterBase openForRead() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDbForRead = mDbHelper.getReadableDatabase();
		return this;
	}

	public void close() {
		if (isOpen()) {
			mDbHelper.close();
			mDbForWrite.close();
		}
	}

	public boolean isOpen() {
		boolean open = false;
		if (mDbForWrite != null && mDbForWrite.isOpen()) {
			open = true;
		}
		return open;
	}
	
	public void createTable(String cmd , boolean isForWrite){
		
		if(isForWrite){
		    mDbHelper.CreateTable(cmd, mDbForWrite);
		}else{
			mDbHelper.CreateTable(cmd, mDbForRead);
		}
		 
	}

	public static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			
		}

		@Override
		public void onCreate (SQLiteDatabase db) {
		
		
		}

		public void CreateTable(String cmd , SQLiteDatabase db){
			db.execSQL(cmd);
		}
		
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
			if(RConstants.BUILD_DEBUBG){
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will keep the old data");
			}
		
			
			if((oldVersion < RConstants.DATABASE_VERSION)){
				
				db.execSQL( "ALTER TABLE storyInfo ADD adsImageURL text");
				db.execSQL( "ALTER TABLE storyInfo ADD companionClickThroughURl text");
				db.execSQL( "ALTER TABLE storyInfo ADD adsSystemName text");
				db.execSQL( "ALTER TABLE storyInfo ADD adsDuration text");
				db.execSQL( "ALTER TABLE storyInfo ADD impressionTrackerUrl text");
				
			}
			
		}

	}
	
	public String ListToCommaSeperatedString(List<String> list) {

		StringBuilder commaSepValueBuilder = new StringBuilder();
		// Looping through the list
		if (list == null)
			return commaSepValueBuilder.toString();

		for (int i = 0; i < list.size(); i++) {
			// append the value into the builder
			commaSepValueBuilder.append(list.get(i));
			if (i != list.size() - 1) {
				commaSepValueBuilder.append(", ");
			}
		}
		return commaSepValueBuilder.toString();
	}


}
