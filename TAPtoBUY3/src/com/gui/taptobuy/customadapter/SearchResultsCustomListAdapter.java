package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.Bid;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.Entities.Rating;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.SearchActivity;
import com.gui.taptobuy.activity.SearchActivity.MyViewItem;

import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.datatask.RatingsManager;
import com.gui.taptobuy.phase1.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class SearchResultsCustomListAdapter extends BaseAdapter implements OnClickListener{

	private SearchActivity activity;	
	private LayoutInflater layoutInflater;
	private ArrayList<Product> items;
	public static ListView ratingsListView;
	private ArrayList<Rating> ratingsList;

	public SearchResultsCustomListAdapter (SearchActivity a, ImageView i, LayoutInflater l, ArrayList<Product> items)
	{
		this.activity = a;		
		this.layoutInflater = l;
		this.items = items;
	}
	////////////////////////////////////////////////////////// 
	@Override
	public int getCount() {
		return this.items.size();
	}

	@Override
	public boolean areAllItemsEnabled () 
	{
		return true;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}
	///////////////////////////////////////////////////////////////////////
	@Override
	public View getView(int position, View itemRow, ViewGroup parent) {
		MyViewItem itemHolder;
		final Product item = items.get(position);
		if(item instanceof ProductForAuction)
		{        	
			itemRow = layoutInflater.inflate(R.layout.bidproduct_row, parent, false); 
			itemHolder = new MyViewItem();
			itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.BidProductPic);
			itemHolder.productName = (TextView) itemRow.findViewById(R.id.BidProdName);
			itemHolder.sellerUserName = (TextView) itemRow.findViewById(R.id.BidSellerUserName);
			itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.BidPrice);
			itemHolder.bidsAmount = (TextView) itemRow.findViewById(R.id.bids);
			itemHolder.timeRemaining = (TextView) itemRow.findViewById(R.id.BidRemaningTime);                      
			itemHolder.sellerRating = (RatingBar)itemRow.findViewById(R.id.BidSellerRating);

			itemHolder.sellerRating.setTag(itemHolder);
			itemHolder.itemPic.setTag(itemHolder);
			itemRow.setTag(itemHolder);

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
			itemRow = layoutInflater.inflate(R.layout.buyitproduct_row, parent, false); 
			itemHolder = new MyViewItem();
			itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.BuyItProductPic);
			itemHolder.productName = (TextView) itemRow.findViewById(R.id.BuyItProdName);
			itemHolder.sellerUserName = (TextView) itemRow.findViewById(R.id.BuyItSellerID);
			itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.BuyItPrice);        
			itemHolder.timeRemaining = (TextView) itemRow.findViewById(R.id.BuyItRemaningTime);
			itemHolder.buyItNow = (TextView) itemRow.findViewById(R.id.BuyItNowText);        
			itemHolder.sellerRating = (RatingBar)itemRow.findViewById(R.id.BuyItSellerRating);            

			itemHolder.sellerRating.setTag(itemHolder);
			itemHolder.itemPic.setTag(itemHolder);
			itemRow.setTag(itemHolder);  

			double shippingPrice = item.getShippingPrice();
			if(shippingPrice == 0){
				itemHolder.priceAndShiping.setText("$" + ((ProductForSale) item).getInstantPrice() +" (Free Shipping)");
			}
			else{
				itemHolder.priceAndShiping.setText("$" + ((ProductForSale) item).getInstantPrice() +" (Shipping: $" + shippingPrice + ")"); 
			}        
		}
		itemRow.setOnClickListener(this);  
		
		itemHolder.sellerRating.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
					if (event.getAction() == MotionEvent.ACTION_UP) {
						final Dialog dialog = new Dialog(activity);
						dialog.setContentView(R.layout.ratinglist);
						dialog.setTitle("Seller's ratings");						
						ratingsListView = (ListView) dialog.findViewById(R.id.ratingsList);
						new getRatingsListTask().execute(item.getId() +"");
						Button closeDialog = (Button) dialog.findViewById(R.id.ratingsCloseB);
						
						closeDialog.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) 
							{	
								dialog.dismiss();
							}
						}); 
						dialog.show();	
					}
					return true;
				}
			});
		
		itemHolder.item = item;
		itemHolder.productName.setText(item.getTitle());   		
		itemHolder.sellerUserName.setText(item.getSellerUsername());		
		itemHolder.sellerRating.setRating((float)item.getSellerRate());
		itemHolder.timeRemaining.setText(item.getTimeRemaining());	
		itemHolder.itemPic.setImageBitmap(item.getImg());

		return itemRow;
	}

	@Override
	public void onClick(View v) 
	{
		MyViewItem itemHolder = (MyViewItem) v.getTag();    
		new productInfoTask().execute(itemHolder.item.getId() + "");
	}

	private Product getProductInfo(String productId){
		HttpClient httpClient = new DefaultHttpClient();
		String productInfoDir = Main.hostName +"/productInfo/" + productId;
		HttpGet get = new HttpGet(productInfoDir);
		get.setHeader("content-type", "application/json");
		Product theItem = null;
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONObject json = new JSONObject(jsonString);
				JSONObject itemInfoJson = json.getJSONObject("productInfo");
				if(json.getBoolean("forBid")){
					theItem = new ProductForAuctionInfo(itemInfoJson.getInt("id"), itemInfoJson.getString("title"), itemInfoJson.getString("timeRemaining"), 
							itemInfoJson.getDouble("shippingPrice"), itemInfoJson.getString("imgLink"),  itemInfoJson.getString("sellerUsername"), 
							itemInfoJson.getDouble("sellerRate"),  itemInfoJson.getDouble("startinBidPrice"),  itemInfoJson.getDouble("currentBidPrice"),  itemInfoJson.getInt("totalBids"),
							itemInfoJson.getString("product"),itemInfoJson.getString("model"),itemInfoJson.getString("brand"),itemInfoJson.getString("dimensions"),itemInfoJson.getString("description"));
				}
				else{
					theItem = new ProductForSaleInfo(itemInfoJson.getInt("id"), itemInfoJson.getString("title"), itemInfoJson.getString("timeRemaining"), 
							itemInfoJson.getDouble("shippingPrice"), itemInfoJson.getString("imgLink"),  itemInfoJson.getString("sellerUsername"), 
							itemInfoJson.getDouble("sellerRate"), itemInfoJson.getInt("remainingQuantity"), itemInfoJson.getDouble("instantPrice"),
							itemInfoJson.getString("product"),itemInfoJson.getString("model"),itemInfoJson.getString("brand"),itemInfoJson.getString("dimensions"),itemInfoJson.getString("description"));
				}
			}
			else{
				Log.e("JSON","ProductInfo json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Product Info","Error!", ex);
		}
		return theItem;
	}

	private class productInfoTask extends AsyncTask<String,Void,Product> {
		Product downloadedProductInfo;
		ProgressDialog dialog;
		Intent intent;		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(activity, "Please wait...", "Loading Item");
			dialog.show();
		}
		protected Product doInBackground(String... params) {
			return getProductInfo(params[0]);//get product info
		}
		protected void onPostExecute(Product productInfo ) {
			downloadedProductInfo = productInfo;
			//download image
			new DownloadImageTask().execute(productInfo.getImgLink());
		
		}			
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {			
		
			protected Bitmap doInBackground(String... params) {
				return ImageManager.downloadImage(params[0]);
			}
			protected void onPostExecute(Bitmap result) {
				downloadedProductInfo.setImg(result);
				if(downloadedProductInfo instanceof ProductForAuctionInfo){//for auction
					BidProductInfoActivity.showingProductInfo = (ProductForAuctionInfo) downloadedProductInfo;
					intent = new Intent(activity, BidProductInfoActivity.class);
					intent.putExtra("previousActivity", "Search");
					dialog.dismiss();					
					activity.startActivity(intent);			
				}
				else{//for sale
					BuyItProductInfoActivity.showingProductInfo = (ProductForSaleInfo) downloadedProductInfo;					
					intent = new Intent(activity, BuyItProductInfoActivity.class);
					intent.putExtra("previousActivity", "Search");				
					dialog.dismiss();	
					activity.startActivity(intent);
				}
			}
		}
	}
	
	//// ratings list stuff
	
	private ArrayList<Rating> getRatingsList(String productId){
		HttpClient httpClient = new DefaultHttpClient();
		String ratingsListDir = Main.hostName +"/ratinglist/" + productId;////
		HttpGet get = new HttpGet(ratingsListDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray ratingsListArray = (new JSONObject(jsonString)).getJSONArray("ratingslist");
				ratingsList = new ArrayList<Rating>();

				JSONObject ratingsListElement = null;				
				for(int i=0; i<ratingsListArray.length();i++){
					ratingsListElement = ratingsListArray.getJSONObject(i);
					ratingsList.add(new Rating(ratingsListElement.getString("buyerUN"),ratingsListElement.getInt("buyerRate")));
				}

			}
			else{
				Log.e("JSON","ratingslist json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("ratingsList","Error!", ex);
		}
		return ratingsList;
	}
	
	private class getRatingsListTask extends AsyncTask<String,Void,ArrayList<Rating>> {
		protected ArrayList<Rating> doInBackground(String... productId) {
			return getRatingsList(productId[0]);//get ratinglist de ratings puestos a este seller
		}
		protected void onPostExecute(ArrayList<Rating> ratingsList ) {
			ratingsListView.setAdapter(new RatingsCustomListAdapter(activity, layoutInflater, ratingsList));
		}			
	}
	
}
