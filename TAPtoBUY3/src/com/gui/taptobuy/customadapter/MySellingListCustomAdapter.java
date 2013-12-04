package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.gui.taptobuy.Entities.Bid;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.User;

import com.gui.taptobuy.activity.AccountSettingsActivity;
import com.gui.taptobuy.activity.CartActivity;
import com.gui.taptobuy.activity.MySellingActivity;
import com.gui.taptobuy.activity.SearchActivity;
import com.gui.taptobuy.activity.SearchActivity.MyViewItem;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MySellingListCustomAdapter extends BaseAdapter implements OnClickListener {
	private MySellingActivity activity;
	private static LayoutInflater layoutInflater;
	private ArrayList<Product> items;	
	private ArrayList<Bid> bidList;
	private ListView bidListView ;
	
	public MySellingListCustomAdapter (MySellingActivity a, LayoutInflater l, ArrayList<Product> products)
    {
    	this.activity = a;    	
    	this.layoutInflater = l;
    	this.items = products;
    }

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.items.size();
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
	public View getView(int position, View itemRow, ViewGroup parent) {
		
		Product item = items.get(position);
		MyViewItem itemHolder;
		
		if(activity instanceof MySellingActivity){
			if(item instanceof ProductForAuction)
			{        	
				itemRow = layoutInflater.inflate(R.layout.selling_bidproductrow, parent, false); 
				itemHolder = new MyViewItem();
				itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.mySell_BidProductPic);
				itemHolder.productName = (TextView) itemRow.findViewById(R.id.mySell_BidProdName);					
				itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.mySell_BidPrice);					
				itemHolder.timeRemaining = (TextView) itemRow.findViewById(R.id.mySell_BidRemaningTime);
				itemHolder.bidsAmount = (TextView) itemRow.findViewById(R.id.mySell_bids);
				itemHolder.bidListB = (Button) itemRow.findViewById(R.id.mySell_BidList);
				itemHolder.AcceptBid = (Button) itemRow.findViewById(R.id.mySell_AcceptBidB);
				itemHolder.Quit = (Button) itemRow.findViewById(R.id.mySell_QuitB);		
				
				itemHolder.itemPic.setTag(itemHolder);
				itemRow.setTag(itemHolder);
				itemHolder.bidListB.setTag(itemHolder);
				itemHolder.Quit.setTag(itemHolder);
				
				itemHolder.bidListB.setOnClickListener(this);
				itemHolder.AcceptBid.setOnClickListener(this);
				itemHolder.Quit.setOnClickListener(this);
				itemRow.setOnClickListener(this);
				itemHolder.bidsAmount.setText(((ProductForAuction) item).getTotalBids()+" bids");
				
				double shippingPrice = item.getShippingPrice();
				if(shippingPrice == 0){
					itemHolder.priceAndShiping.setText("$" + ((ProductForAuction) item).getCurrentBidPrice()+" (Free Shipping)");
				}
				else{
					itemHolder.priceAndShiping.setText("$" + ((ProductForAuction) item).getCurrentBidPrice()+" (Shipping: $" + shippingPrice + ")"); 
				}
			}
			else //for sale
			{	        
				itemRow = layoutInflater.inflate(R.layout.selling_buyitproductrow, parent, false); 
				itemHolder = new MyViewItem();
				itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.mySell_BuyNProductPic);
				itemHolder.productName = (TextView) itemRow.findViewById(R.id.mySell_BuyNProdName);					
				itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.mySell_BuyNPrice);					
				itemHolder.timeRemaining = (TextView) itemRow.findViewById(R.id.mySell_BuyNRemaningTime);
				itemHolder.Quit = (Button) itemRow.findViewById(R.id.mySell_QuitfromSellingB);					
				itemHolder.qty = (TextView) itemRow.findViewById(R.id.mSell_Quantity);
				
				itemHolder.itemPic.setTag(itemHolder);
				itemRow.setTag(itemHolder);
				itemHolder.Quit.setTag(itemHolder);
				itemHolder.Quit.setOnClickListener(this);
				itemHolder.qty.setText("Qty: " + ((ProductForSale)item).getRemainingQuantity());
				double shippingPrice = item.getShippingPrice();
				if(shippingPrice == 0){
					itemHolder.priceAndShiping.setText("$" + ((ProductForSale) item).getInstantPrice() +" (Free Shipping)");
				}
				else{
					itemHolder.priceAndShiping.setText("$" + ((ProductForSale) item).getInstantPrice() +" (Shipping: $" + shippingPrice + ")"); 
				}        
			}
			
			itemRow.setOnClickListener(this);  			
			itemHolder.item = item;		
			itemHolder.productName.setText(item.getTitle()); 		
			itemHolder.timeRemaining.setText(item.getTimeRemaining());
			itemHolder.itemPic.setImageBitmap(item.getImg());
							
		}           
        return itemRow;
	}

	@Override
	public void onClick(View v) {
		MyViewItem itemHolder = (MyViewItem) v.getTag(); 
		switch(v.getId()){		
		case R.id.mySell_BidList:
			final Dialog dialog = new Dialog(activity);
			dialog.setContentView(R.layout.bidlist_dialog);
			dialog.setTitle("Item's Bids");
			bidListView = (ListView) dialog.findViewById(R.id.bidlistView);
		    new getBidListTask().execute(itemHolder.item.getId() + "");
			Button okBTN = (Button) dialog.findViewById(R.id.bidOkButton);			
			okBTN.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) 
				{	
					dialog.dismiss();
				}
			});    
			dialog.show();	
			
			break;
		case R.id.mySell_AcceptBidB:
			Toast.makeText(this.activity, "Your Auction has not ended yet", Toast.LENGTH_SHORT).show();
			break;
		case R.id.mySell_QuitB:
			new quitFromSaleTask().execute(itemHolder.item.getId() + "");
			activity.mySellingsRefresh();//refresh
			break;
		case R.id.mySell_QuitfromSellingB:
			new quitFromSaleTask().execute(itemHolder.item.getId() + "");
			activity.mySellingsRefresh(); //refresh
			break;
		}
		
	}
	private boolean quitFromSellings(String productID){
		HttpClient httpClient = new DefaultHttpClient();
		HttpDelete delete = new HttpDelete(Main.hostName + "/sellings/" + Main.userId + "/" + productID);
		try
		{
			HttpResponse resp = httpClient.execute(delete);
			if(resp.getStatusLine().getStatusCode() == 204){
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception ex)
		{
			Log.e("Could not remove this item!","Error!", ex);
			return false;
		}
	}
	private class quitFromSaleTask extends AsyncTask<String,Void,Boolean> {
		protected Boolean doInBackground(String... productId) {
			return quitFromSellings(productId[0]);
		}
		protected void onPostExecute(Boolean result) {
			if(result){
				Toast.makeText(activity, "Item removed.", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(activity, "Error: Item could not be removed.", Toast.LENGTH_SHORT).show();
			}
		}			

	}
	
	private ArrayList<Bid> getBidList(String productId){
		HttpClient httpClient = new DefaultHttpClient();
		String bidListDir = Main.hostName +"/bidlist/" + productId;
		HttpGet get = new HttpGet(bidListDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray bidListArray = (new JSONObject(jsonString)).getJSONArray("bidlist");
				bidList = new ArrayList<Bid>();

				JSONObject bidListElement = null;

				for(int i=0; i<bidListArray.length();i++){
					bidListElement = bidListArray.getJSONObject(i);
					bidList.add(new Bid(-1, bidListElement.getDouble("amount"), -1, bidListElement.getString("username")));
				}

			}
			else{
				Log.e("JSON","bidlist json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("BidList","Error!", ex);
		}
		return bidList;
	}

	private class getBidListTask extends AsyncTask<String,Void,ArrayList<Bid>> {
		protected ArrayList<Bid> doInBackground(String... productId) {
			return getBidList(productId[0]);//get bidlist de bids puestos a este product
		}
		protected void onPostExecute(ArrayList<Bid> bidList ) {
			//llenar con array de bid
			bidListView.setAdapter(new MySellingBidListCustomAdapter(activity,layoutInflater, bidList));
		}			
	}

}
