package com.rivet.app.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.rivet.app.abstracts.BuilderBase;
import com.rivet.app.adapter.CategoryDBAdapter;
import com.rivet.app.common.RConstants;
import com.rivet.app.core.pojo.Category;
import com.rivet.app.observer.CategoryBuilderObserver;
import com.rivet.app.webrequest.HttpExceptionListener;
import com.rivet.app.webrequest.HttpMethodType;
import com.rivet.app.webrequest.HttpRequest;
import com.rivet.app.webrequest.HttpResponseListener;

/**
 * Created by brian on 12/6/14.
 */
public class CategoryBuilder extends BuilderBase {

	private Context context;
	private CategoryDBAdapter database;
	private List<PropertyChangeListener> cBlistener = new ArrayList<PropertyChangeListener>();

	public CategoryBuilder(Context context) {
		this.context = context;
	}

	public void start() {
		String path = RConstants.BaseUrl + "categorytree/";
		database = CategoryDBAdapter.getCategoryDBAdapterForWrite(context);
		CategoryResponseListener categoryResponseListener = new CategoryResponseListener();
		
		
		Thread getCategoryThread = new Thread(new HttpRequest(path,
				HttpMethodType.GET, categoryResponseListener,
				categoryResponseListener, null, null , false , null));
		getCategoryThread.start();
		
	}

	public boolean setCategoryPreferences(Category cat, boolean enabled) {

		return true;
	}

	private void processCategoryData(String response) {
		// parse the json

		try {
			JSONArray categoriesArray = new JSONArray(response);
			if (!(categoriesArray.equals("[]"))) {
				for (int i = 0; i < categoriesArray.length(); i++) {

					Category categoryInfo = new Category();

					JSONObject category = categoriesArray.getJSONObject(i);
					String categoryId = category.getString("categoryID");
					categoryInfo.setCategoryId(Integer.parseInt(categoryId));
					categoryInfo.setName(category.getString("name"));
					if (category.getBoolean("allowRegions")) {
						categoryInfo.setAllowRegions(true);
					} else {
						categoryInfo.setAllowRegions(false);
					}
					categoryInfo.setParentCategoryId(0);
					

					// insert in database
					database.addCategoryInfo(categoryInfo);

					JSONArray subcategories = category  .getJSONArray("subCategories");
					// get sub-categories, if there are any
					if (!(subcategories.equals("[]"))) {
						traverseCategoriesInSubCategory(
								Integer.parseInt(categoryId), subcategories);
					}
				}
				
			}
			
			//add the Music category if not added
			Category categoryInfo = new Category();
			categoryInfo.setCategoryId(RConstants.IPOD_MUSIC_CATEGORY);
			categoryInfo.setName("Music from the device");
			categoryInfo.setParentCategoryId(0);
			database.addCategoryInfo(categoryInfo);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		notifyListeners(RConstants.FINISH_CATEGORIES_BUILDING);

	}

	private ArrayList<Category> traverseCategoriesInSubCategory(
			int parentCategoryId, JSONArray subcategories) {
		for (int subI = 0; subI < subcategories.length(); subI++) {
			try {
				JSONObject subCategory = subcategories.getJSONObject(subI);
				Category subCategoryInfo = new Category();
				int categoryId = subCategory.getInt("categoryID");
				subCategoryInfo.setCategoryId(subCategory.getInt("categoryID"));
				subCategoryInfo.setParentCategoryId(parentCategoryId);
				subCategoryInfo.setName(subCategory.getString("name"));
				if (subCategory.getBoolean("allowRegions")) {
					subCategoryInfo.setAllowRegions(true);
				} else {
					subCategoryInfo.setAllowRegions(false);
				}

				
				// insert subcategories in database
				database.addCategoryInfo(subCategoryInfo);

				JSONArray furtherSubcategories = subCategory
						.getJSONArray("subCategories");
				// get sub-categories, if there are any
				if (!(subcategories.equals("[]"))) {
					traverseCategoriesInSubCategory(categoryId,furtherSubcategories);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}// get subcategories loop
		
		
		return null;
	}

	public class CategoryResponseListener implements HttpResponseListener,
			HttpExceptionListener {

		@Override
		public void handleResponse(String response) {
			// TODO Auto-generated method stub
			if (response != null) {
				processCategoryData(response);
			}
		}

		@Override
		public void handleException(String exception) {
			// TODO Auto-generated method stub
			
			notifyListeners(RConstants.HHR_ERROR_SERVER_IS_DOWN_CATEGORY_BUILDER);
			
		}

	}
	
	public void addChangeListener(CategoryBuilderObserver categoryBuilderObserver) {
		// TODO Auto-generated method stub
		cBlistener.add(categoryBuilderObserver);
		
		
	}

	public void notifyListeners(String ruleBuildComplete ) {
	    for (PropertyChangeListener name : cBlistener) {
	      name.propertyChange(new PropertyChangeEvent(this, ruleBuildComplete, null, null));
	    }
	  }

}
