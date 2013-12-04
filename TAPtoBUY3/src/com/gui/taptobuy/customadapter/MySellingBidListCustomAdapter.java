package com.gui.taptobuy.customadapter;

import java.util.ArrayList;
import com.gui.taptobuy.Entities.Bid;
import com.gui.taptobuy.activity.MyBiddingActivity.MyBidHolder;
import com.gui.taptobuy.activity.MySellingActivity;
import com.gui.taptobuy.phase1.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class MySellingBidListCustomAdapter extends BaseAdapter {
	
	private MySellingActivity activity;	
	private LayoutInflater layoutInflater;
	private ArrayList<Bid> bids;		
	
	
	public MySellingBidListCustomAdapter (MySellingActivity a, LayoutInflater l, ArrayList<Bid> bids)
    {
    	this.activity = a;    	
    	this.layoutInflater = l;
    	this.bids = bids;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return bids.size();
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
	public View getView(int position, View bidRow, ViewGroup parent) 
	{		
		Bid listBid = bids.get(position);		
		bidRow = layoutInflater.inflate(R.layout.bids_row, parent, false); 
		MyBidHolder bidsHolder = new MyBidHolder();
        bidsHolder.bidPrice = (TextView) bidRow.findViewById(R.id.bidRow_BidPrice);
        bidsHolder.placerUsername  = (TextView) bidRow.findViewById(R.id.bidRow_BidderUsername);        
        bidsHolder.bidPrice.setText("$ " + Double.toString(listBid.getAmount()));
        bidsHolder.placerUsername.setText("" + listBid.getBidderUserName());        
        bidRow.setTag(bidsHolder);      
        bidsHolder.bidToshow = listBid;    
        return bidRow;
    }
}
