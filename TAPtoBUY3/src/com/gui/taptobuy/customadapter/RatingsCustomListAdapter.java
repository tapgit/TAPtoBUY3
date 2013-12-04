package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import com.gui.taptobuy.Entities.Rating;
import com.gui.taptobuy.phase1.R;

public class RatingsCustomListAdapter extends BaseAdapter{
	
	private Activity activity;	
	private LayoutInflater layoutInflater;
	private ArrayList<Rating> ratings;		
	
	
	public RatingsCustomListAdapter (Activity a, LayoutInflater l, ArrayList<Rating> ratings)
    {
    	this.activity = a;    	
    	this.layoutInflater = l;
    	this.ratings = ratings;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println(ratings.size()+"   del adapter");
		return ratings.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View ratingRow, ViewGroup parent) 
	{		
		Rating rating = ratings.get(position);		
		ratingRow = layoutInflater.inflate(R.layout.rating_row, parent, false); 
		MyRatingHolder ratingHolder = new MyRatingHolder();
        ratingHolder.buyerUserName = (TextView) ratingRow.findViewById(R.id.ratingBuyerUN);
        ratingHolder.buyerRating  = (RatingBar) ratingRow.findViewById(R.id.ratingGiven);        
        ratingHolder.buyerUserName.setText(rating.getBuyerUN());
        ratingHolder.buyerRating.setRating(rating.getBuyerRate());
        ratingRow.setTag(ratingHolder);      
        ratingHolder.ratingToshow = rating;    
        return ratingRow;
    }
	
	public static class MyRatingHolder{
		public Rating ratingToshow;
		public RatingBar buyerRating;
		public TextView buyerUserName;		
	}

}
