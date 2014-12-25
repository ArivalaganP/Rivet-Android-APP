package com.rivet.app.adapter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.rivet.app.abstracts.RDBAdapterBase;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Category;

public class CategoryDBAdapter extends RDBAdapterBase {

	/************************************** Table categoryInfo ************************************************/
	private static final String DATABASE_CATEGORY_INFO_CREATE = "create table if not exists categoryInfo(cid integer "
			+ "primary key autoincrement, parentCategoryId integer , isTimeSensitive integer ,categoryId integer, isLearning integer, isHidden integer ,name text, "
			+ "channelId text);";
	public static final String C_ID = "cid";
	public static final String CATEGORY_ID = "categoryId";
	public static final String CATEGORY_IS_LEARNING = "isLearning";
	public static final String CATEGORY_IS_HIDDEN = "isHidden";
	public static final String CATEGORY_NAME = "name";
	public static final String CATEGORY_PARENT_ID = "parentCategoryId";
	public static final String CATEGORY_TIME_SENSITIVE = "isTimeSensitive";

	private static final String DATABASE_CATEGORY_INFO_TABLE = "categoryInfo";
	
	/************************************** Table myCategory ************************************************/
	public static final String MC_ID = "mcid";
	public static final String My_CATEGORY_ID = "myCategoryId";
	public static final String My_CATEGORY_NAME = "myCategoryName";
	public static final String My_CATEGORY_IS_CHECKED = "isChecked";

	private static final String DATABASE_MY_CATEGORY_TABLE = "myCategory";

	private static final String DATABASE_My_CATEGORY__CREATE = "create table if not exists myCategory(mcid integer "
			+ "primary key autoincrement, myCategoryId integer, myCategoryName text, "
			+ "isChecked boolean);";

	private static CategoryDBAdapter mCategoryDBAdapterForRead = null;
	private static CategoryDBAdapter mCategoryDBAdapterForWrite = null;

	/****************************************************************************
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context with in which to work
	 *****************************************************************************/
	private CategoryDBAdapter(Context ctx , boolean isForWrite) {
		super(ctx , isForWrite );

		createTable(DATABASE_CATEGORY_INFO_CREATE , isForWrite);
		createTable(DATABASE_My_CATEGORY__CREATE , isForWrite );

	}

	public static CategoryDBAdapter getCategoryDBAdapterForRead(Context ctx) {

		synchronized (CategoryDBAdapter.class) {

			if (mCategoryDBAdapterForRead == null) {
				mCategoryDBAdapterForRead = new CategoryDBAdapter(ctx , false);
			}
		}
		return mCategoryDBAdapterForRead;

	}
	
	public static CategoryDBAdapter getCategoryDBAdapterForWrite(Context ctx) {

		synchronized (CategoryDBAdapter.class) {

			if (mCategoryDBAdapterForWrite == null) {
				mCategoryDBAdapterForWrite = new CategoryDBAdapter(ctx , true);
			}
		}
		
		return mCategoryDBAdapterForWrite;

	}
	
	
	public ArrayList<String> getAllDeselectedCategoriesNames(ArrayList<Integer> parentCatIds){
		
		ArrayList<String> subcategoriesNames = new ArrayList<String>();
		
		String whereCluase = " where " + CATEGORY_PARENT_ID + " = " + parentCatIds.get(0) ;
		
		for(int indxClause = 1 ; indxClause < parentCatIds.size() ; indxClause ++){
			whereCluase += " or " + CATEGORY_PARENT_ID + " = " + parentCatIds.get(indxClause);
		}
		
		for(int i = 0 ; i < parentCatIds.size() ; i++){
			whereCluase += " or " + CATEGORY_ID + " = " + parentCatIds.get(i);
		}
		
		String query = "select * from categoryInfo " + whereCluase ; 
		Cursor cursor ;
		
		if(mDbForRead != null){
		 cursor = mDbForRead.rawQuery(query, null);
		}else{
			cursor = mDbForWrite.rawQuery(query, null);
		}
		
		if (cursor.moveToFirst()){
			   do{
				  
			      String subCategoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
			      
			      subcategoriesNames.add(subCategoryName);
			      // do what ever you want here
			   }while(cursor.moveToNext());
			}
			cursor.close();
		
		
		return subcategoriesNames;
	}
	
	
	public ArrayList<String> getAllSeletedCategoriesNames(ArrayList<Integer> parentCatIds){
		
		ArrayList<String> subcategoriesNames = new ArrayList<String>();
	
		String whereCluase = " where " + CATEGORY_PARENT_ID + " = " + parentCatIds.get(0) ;
		
		for(int indxClause = 1 ; indxClause < parentCatIds.size() ; indxClause ++){
			whereCluase += " or " + CATEGORY_PARENT_ID + " = " + parentCatIds.get(indxClause);
		}
		
		for(int i = 0 ; i < parentCatIds.size() ; i++){
			whereCluase += " or " + CATEGORY_ID + " = " + parentCatIds.get(i);
		}
		
		String query = "select * from categoryInfo " + whereCluase ; 
		Cursor cursor ;
		
		if(mDbForRead != null){
		 cursor = mDbForRead.rawQuery(query, null);
		}else{
			cursor = mDbForWrite.rawQuery(query, null);
		}
		
		if (cursor.moveToFirst()){
			   do{
				  
			      String subCategoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
			      
			      subcategoriesNames.add(subCategoryName);
			      // do what ever you want here
			   }while(cursor.moveToNext());
			}
			cursor.close();
		
		
		return subcategoriesNames;
		
	}

	// methods of category

	public int isCategoryExist(Category category) {
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where " + CATEGORY_ID + "=" + category.getCategoryId();

		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		int Id = 0;
		if (cursor.moveToFirst()) {
			do {
				Id = cursor.getInt(cursor.getColumnIndex(C_ID));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return Id;
	}
	
	
	
	public String getCategoryNameByCategoryId(int catId) {
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where " + CATEGORY_ID + "=" + catId;
		Cursor cursor ;

		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		String categoryName = "";
		if (cursor.moveToFirst()) {
			do {
				categoryName = cursor.getString(cursor.getColumnIndex(CATEGORY_NAME));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return categoryName;
	}

	public Cursor getCategoryInfo() {
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE;
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		return cursor;
	}

	public Cursor getCategories() {
		// customize only the categories which parent id and ishidden is zero
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where parentCategoryId = 0 and isHidden = 0";

		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		return cursor;

	}

	public ArrayList<Category> getAllCategories() {
		String whereCluase = " where " + CATEGORY_ID + " <> " + 1 + " AND " + CATEGORY_ID + " <> " + 30 + " AND " + CATEGORY_ID + " <> " + 31 ;
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE + whereCluase ;
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}

		ArrayList<Category> categoryArr = new ArrayList<Category>();

		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();

				category.setCategoryId(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_ID)));
				category.setName(cursor.getString(cursor
						.getColumnIndex(CATEGORY_NAME)));

				categoryArr.add(category);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return categoryArr;

	}

	public void addCategoryInfo(Category category) {

		int id = isCategoryExist(category);
		if (id > 0)
			return;

		String catName = category.getName().trim();
		Log.i("CategoryDBAdaper", "catid: "+category.getCategoryId()+"category Name "+category.getName());
		ContentValues values = new ContentValues();
		values.put(CATEGORY_ID, category.getCategoryId());
		values.put(CATEGORY_IS_HIDDEN, category.getIsHidden());
		values.put(CATEGORY_IS_LEARNING, category.getIsLearning());
		values.put(CATEGORY_NAME, catName);
		values.put(CATEGORY_TIME_SENSITIVE, CATEGORY_TIME_SENSITIVE);
		values.put(CATEGORY_PARENT_ID, category.getParentCategoryId());

		mDbForWrite.insert(DATABASE_CATEGORY_INFO_TABLE, null, values);

		return;

	}

	public int categoryExistInMyCategoryById(Category category) {
		
		String query = "select * from " + DATABASE_MY_CATEGORY_TABLE
				+ " where " + My_CATEGORY_ID + " = " + category.getCategoryId()+" AND "+ My_CATEGORY_IS_CHECKED +" = 1";
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		int Id = 0;
		if (cursor.moveToFirst()) {
			do {
				Id = cursor.getInt(cursor.getColumnIndex(My_CATEGORY_ID));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return Id;
	}
	
	public int categoryWelcomeExistInMyCategoryByName(Category category){
		
		String query = "select * from " + DATABASE_MY_CATEGORY_TABLE
				+ " where " + My_CATEGORY_NAME + " = " + '"' + RConstants.WELCOME + '"' 
				+ " AND "+ My_CATEGORY_IS_CHECKED +" = 1";
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		int Id = 0;
		if (cursor.moveToFirst()) {
			do {
				Id = cursor.getInt(cursor.getColumnIndex(My_CATEGORY_ID));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return Id;
	}

	public void deleteAllMyCategories() {
		String query = "delete from " + DATABASE_MY_CATEGORY_TABLE;
		mDbForWrite.execSQL(query);
	}

	public void addMyCategory(Category category) {

		int idByName = categoryExistInMyCategoryById(category);
		if (idByName > 0)
			return;

		ContentValues values = new ContentValues();
		String catName = category.getName().trim();
		values.put(My_CATEGORY_ID, category.getCategoryId());
		values.put(My_CATEGORY_NAME, catName);
		values.put(My_CATEGORY_IS_CHECKED, category.getisChecked());
		mDbForWrite.insert(DATABASE_MY_CATEGORY_TABLE, null, values);

	}

	public int getMyCategoriesCount() {
		int count=0;
		String query = "select count(mcid) as cnt from " + DATABASE_MY_CATEGORY_TABLE;
	Cursor cursor ;
	
	if(mDbForRead != null){
		 cursor = mDbForRead.rawQuery(query, null);
		}else{
			cursor = mDbForWrite.rawQuery(query, null);
		}
	
		
				if (cursor.moveToFirst()) {
					do {
					
						count = cursor.getInt(0);
					}while(cursor.moveToNext());
				}
				
				return count;		
	}
	
	
	
	public ArrayList<Category> getAllMyCategories() {
		String query = "select * from " + DATABASE_MY_CATEGORY_TABLE;
		ArrayList<Category> listCategory = new ArrayList<Category>();
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setName(cursor.getString(cursor
						.getColumnIndex(CategoryDBAdapter.My_CATEGORY_NAME)));
				category.setCategoryId(cursor.getInt(cursor
						.getColumnIndex(CategoryDBAdapter.My_CATEGORY_ID)));
				int isChecked = cursor
						.getInt(cursor
								.getColumnIndex(CategoryDBAdapter.My_CATEGORY_IS_CHECKED));
				category.setIsChecked((isChecked == 1) ? true : false);
				listCategory.add(category);
			} while (cursor.moveToNext());

		}
		return listCategory;
	}

	public void updateMyCategory(Category category) {
		ContentValues values = new ContentValues();
		values.put(My_CATEGORY_IS_CHECKED, category.getisChecked());

		mDbForWrite.update(DATABASE_MY_CATEGORY_TABLE, values, My_CATEGORY_ID + "="
				+ category.getCategoryId(), null);
	}

	public ArrayList<Category> getRequiredCategory(int ischecked) {
		ArrayList<Category> categoryArr = new ArrayList<Category>();
		String query = "select * from " + DATABASE_MY_CATEGORY_TABLE
				+ " where " + My_CATEGORY_IS_CHECKED + " = " + ischecked;
		Cursor cursor;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		

		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();

				category.setCategoryId(cursor.getInt(cursor
						.getColumnIndex(My_CATEGORY_ID)));
				category.setName(cursor.getString(cursor
						.getColumnIndex(My_CATEGORY_NAME)));

				categoryArr.add(category);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return categoryArr;

	}
	
	

	public int getCategoryIDByName(String categoryName) {

		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where " + CATEGORY_NAME + " = " + "'" + categoryName.trim()
				+ "'";
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		int categoryId = 0;
		if (cursor.moveToFirst()) {
			do {
				categoryId = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
			} while (cursor.moveToNext());
		}
		cursor.close();

		return categoryId;
	}

	public ArrayList<Integer> getCategoriesIDByName(String categoryName) {

		ArrayList<Integer> catIdList = new ArrayList<Integer>();
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where " + CATEGORY_NAME + " = " + "'" + categoryName.trim()
				+ "'";
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		int categoryId = 0;
		if (cursor.moveToFirst()) {
			do {
				categoryId = cursor.getInt(cursor.getColumnIndex(CATEGORY_ID));
				catIdList.add(categoryId);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return catIdList;
	}

	public ArrayList<Category> getSubcategoriesByParentId(int parentId) {
		ArrayList<Category> subCategoris = new ArrayList<Category>();

		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where " + CATEGORY_PARENT_ID + " = " + parentId;
		
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setCategoryId(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_ID)));
				category.setName(cursor.getString(cursor
						.getColumnIndex(CATEGORY_NAME)));
				category.setIsHidden(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_IS_HIDDEN)));
				category.setIsTimeSensitive(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_TIME_SENSITIVE)));
				category.setIsLearning(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_IS_LEARNING)));
				subCategoris.add(category);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return subCategoris;
	}
	
	public int getParentCategoryID(int catID) {
		int categoryID=0;
		String query = "select "+CATEGORY_PARENT_ID+"  from " + DATABASE_CATEGORY_INFO_TABLE
				+ " where " + CATEGORY_ID + " = " + catID;
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		if (cursor.moveToFirst()) {
			do {
				
					categoryID =cursor.getInt(cursor.getColumnIndex(CATEGORY_PARENT_ID));
						 
				} while (cursor.moveToNext());
		}
		cursor.close();
		
		if(categoryID==0){
			categoryID = catID;
		}
		return categoryID;
	}

	public ArrayList<Category> getCategoryBreakingNews() {
		
		ArrayList<Category> breakingNewsCategory = new ArrayList<Category>();
		String query = "select * from " + DATABASE_CATEGORY_INFO_TABLE +
				" where " + CATEGORY_ID + " = 1" 
		 		+" or " + CATEGORY_ID + " = 31" + " or " + CATEGORY_ID + " = 30";
		
		
		Cursor cursor ;
		
		if(mDbForRead != null){
			 cursor = mDbForRead.rawQuery(query, null);
			}else{
				cursor = mDbForWrite.rawQuery(query, null);
			}
		
		if (cursor.moveToFirst()) {
			do {
				Category category = new Category();
				category.setCategoryId(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_ID)));
				category.setName(cursor.getString(cursor
						.getColumnIndex(CATEGORY_NAME)));
				// category.setIsHidden(cursor.getInt(cursor
				// .getColumnIndex(CATEGORY_IS_HIDDEN)));
				category.setIsTimeSensitive(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_TIME_SENSITIVE)));
				category.setIsLearning(cursor.getInt(cursor
						.getColumnIndex(CATEGORY_IS_LEARNING)));
				breakingNewsCategory.add(category);
			} while (cursor.moveToNext());
		}
		cursor.close();

		return breakingNewsCategory;
	}

	

}
