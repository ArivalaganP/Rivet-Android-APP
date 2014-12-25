package com.rivet.app.core;

import com.rivet.app.abstracts.ManagerBase;
import com.rivet.app.core.pojo.Category;

import java.util.List;

/**
 * Created by brian on 12/6/14.
 */
public class CategoryManager  implements ManagerBase {

        private List<Category> categoryArray;
        public void finishProccessingCategories() {}
		
        
        
        public List<Category> getCategoryArray() {
			return categoryArray;
		}
		public void setCategoryArray(List<Category> categoryArray) {
			this.categoryArray = categoryArray;
		}


}
