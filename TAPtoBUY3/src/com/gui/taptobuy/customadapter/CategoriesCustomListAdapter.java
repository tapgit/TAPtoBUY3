package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import com.gui.taptobuy.Entities.Category;
import com.gui.taptobuy.activity.CategoryActivity;
import com.gui.taptobuy.activity.CategoryActivity.MyViewCategory;
import com.gui.taptobuy.phase1.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CategoriesCustomListAdapter extends BaseAdapter {
	
	private CategoryActivity activity;	
	private LayoutInflater layoutInflater;
	private ArrayList<Category> categories;		
	
	public CategoriesCustomListAdapter (CategoryActivity a, LayoutInflater l, ArrayList<Category> categories)
    {
    	this.activity = a;    	
    	this.layoutInflater = l;
    	this.categories = categories;
    }
///////////////////////////////////////////////////////////
	@Override
	public Object getItem(int position) {
		
		return null;
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getCount() {		
		return this.categories.size();
	}
///////////////////////////////////////////////////////////
	@Override
	public View getView(int position, View categoryRow, ViewGroup parent) 
	{
			MyViewCategory categoryHolder;
			Category currentCategory = categories.get(position);
			
			categoryRow = layoutInflater.inflate(R.layout.category_row, parent, false); 
            categoryHolder = new MyViewCategory();
            categoryHolder.categoryName = (TextView) categoryRow.findViewById(R.id.categoryTitle);
            
	        if(!currentCategory.hasSubCategories())
	        {        		                 
	            categoryHolder.arrow =(ImageView) categoryRow.findViewById(R.id.hasChildArrow); 
	            categoryHolder.arrow.setVisibility(View.GONE);
	            	                 
	        }
	        categoryRow.setTag(categoryHolder);	     
	        categoryHolder.category = currentCategory;
            categoryHolder.categoryName.setText(currentCategory.getName());
            

	        return categoryRow;
	    }	
}
