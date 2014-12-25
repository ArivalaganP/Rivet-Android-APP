package com.rivet.app.adapter;

import java.util.Map;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rivet.app.HomeActivity;
import com.rivet.app.R;
import com.rivet.app.common.HHRHelper;
import com.rivet.app.core.pojo.Category;

public class CustomizeAdapter extends BaseAdapter {

	private int layout;
	private Category category;
	private HomeActivity context;
	private Typeface metaProTypeFace;
	private CategoryDBAdapter catDBAdapter=null;
	private Map<Integer, Category> users;
	private Integer[] mKeys;
	public CustomizeAdapter(HomeActivity context,
			Map<Integer , Category> users, int layout, Typeface metaProTypeFace) {
		super();
		  this.users  = users;
	        mKeys = users.keySet().toArray(new Integer[users.size()]);
		this.context = context;
		this.layout = layout;
		this.metaProTypeFace = metaProTypeFace;
		catDBAdapter = CategoryDBAdapter.getCategoryDBAdapterForRead(context);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		category = (Category)getItem(position);

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);       
	         convertView = mInflater.inflate(layout, null);
		}

		TextView categoryTV = (TextView) convertView
				.findViewById(R.id.categoryTV);
		categoryTV.setTypeface(metaProTypeFace);
		ImageView selectedIB = (ImageView) convertView
				.findViewById(R.id.checkBox);
		TextView categoryColorTV = (TextView) convertView
				.findViewById(R.id.categoryColorTV);

		if (category.getisChecked()) {
			selectedIB.setVisibility(View.VISIBLE);
			categoryTV.setTextColor(context.getResources().getColor(
					context.selectedTextColor()));
			selectedIB.setImageResource(R.drawable.icon_checked);
		} else {

			categoryTV.setTextColor(context.getResources().getColor(
					context.unSelectedTextColor()));

			selectedIB.setVisibility(View.INVISIBLE);

		}

		// set color for each category
		categoryColorTV.setBackgroundColor(context.getResources().getColor(HHRHelper.getCategoryColor(catDBAdapter.getCategoryIDByName( category.getName().trim()))));

		categoryTV.setText(category.getName());

		return convertView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.users.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.users.get(mKeys[position]);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	

}
