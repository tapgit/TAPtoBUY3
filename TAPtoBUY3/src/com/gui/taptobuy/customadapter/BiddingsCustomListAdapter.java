package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.MyBiddingsProduct;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.MyBiddingActivity;
import com.gui.taptobuy.activity.MyBiddingActivity.MyViewAuctionItem;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class BiddingsCustomListAdapter extends BaseAdapter implements OnClickListener {
	private MyBiddingActivity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<MyBiddingsProduct> items;	


	public BiddingsCustomListAdapter (MyBiddingActivity a, LayoutInflater l, ArrayList<MyBiddingsProduct> items)
	{
		this.activity = a;		
		this.layoutInflater = l;
		this.items = items;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View itemRow, ViewGroup parent) {
		MyViewAuctionItem itemHolder;
		MyBiddingsProduct item = items.get(position);

		itemRow = layoutInflater.inflate(R.layout.mybiddings_bidproduct_row, parent, false); 
		itemHolder = new MyViewAuctionItem();
		itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.BidProductPic);
		itemHolder.productName = (TextView) itemRow.findViewById(R.id.BidProdName);
		itemHolder.sellerUserName = (TextView) itemRow.findViewById(R.id.BidSellerUserName);
		itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.BidPrice);
		itemHolder.bidsAmount = (TextView) itemRow.findViewById(R.id.bids);
		itemHolder.timeRemaining = (TextView) itemRow.findViewById(R.id.BidRemaningTime);
		itemHolder.isWinningBid = (TextView) itemRow.findViewById(R.id.isWinningBid);
		itemHolder.sellerRating = (RatingBar)itemRow.findViewById(R.id.BidSellerRating);

		itemHolder.sellerRating.setTag(itemHolder);
		itemHolder.itemPic.setTag(itemHolder);
		itemRow.setTag(itemHolder);


		itemHolder.bidsAmount.setText(((MyBiddingsProduct) item).getTotalBids()+" bids");
		double shippingPrice = item.getShippingPrice();

		if(shippingPrice == 0){
			itemHolder.priceAndShiping.setText("$" + ((MyBiddingsProduct) item).getCurrentBidPrice()+" (Free Shipping)");
		}
		else{
			itemHolder.priceAndShiping.setText("$" + ((MyBiddingsProduct) item).getCurrentBidPrice()+" (Shipping: $" + shippingPrice + ")"); 
		}
		itemHolder.item = item;
		itemRow.setOnClickListener(this);  	

		itemHolder.productName.setText(item.getTitle());   		
		itemHolder.sellerUserName.setText(item.getSellerUsername());		
		itemHolder.sellerRating.setRating((float)item.getSellerRate());
		itemHolder.timeRemaining.setText(item.getTimeRemaining());
		if(item.isWinningBid()){
			itemHolder.isWinningBid.setTextColor(Color.rgb(24, 118, 21));
			itemHolder.isWinningBid.setText("Winning!");
		}
		else{
			itemHolder.isWinningBid.setTextColor(Color.rgb(255, 10, 10));
			itemHolder.isWinningBid.setText("Outbid!");
		}
		itemHolder.itemPic.setImageBitmap(item.getImg());

		return itemRow;
	}

	@Override
	public void onClick(View v) {
		MyViewAuctionItem itemHolder = (MyViewAuctionItem) v.getTag();    
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

				theItem = new ProductForAuctionInfo(itemInfoJson.getInt("id"), itemInfoJson.getString("title"), itemInfoJson.getString("timeRemaining"), 
						itemInfoJson.getDouble("shippingPrice"), itemInfoJson.getString("imgLink"),  itemInfoJson.getString("sellerUsername"), 
						itemInfoJson.getDouble("sellerRate"),  itemInfoJson.getDouble("startinBidPrice"),  itemInfoJson.getDouble("currentBidPrice"),  itemInfoJson.getInt("totalBids"),
						itemInfoJson.getString("product"),itemInfoJson.getString("model"),itemInfoJson.getString("brand"),itemInfoJson.getString("dimensions"),itemInfoJson.getString("description"));
			}
			else{
				Log.e("JSON","ProductInfo json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("ProductInfo","Error!", ex);
		}
		return theItem;
	}

	private class productInfoTask extends AsyncTask<String,Void,Product> {
		Product downloadedProductInfo;
		protected Product doInBackground(String... params) {
			return getProductInfo(params[0]);//get product info
		}
		protected void onPostExecute(Product productInfo ) {
			downloadedProductInfo = productInfo;
			//download image
			new DownloadImageTask().execute(productInfo.getImgLink());
		}			
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> 
		{			
			protected Bitmap doInBackground(String... params) {
				return ImageManager.downloadImage(params[0]);
			}
			protected void onPostExecute(Bitmap result) 
			{
				downloadedProductInfo.setImg(result);				
				BidProductInfoActivity.showingProductInfo = (ProductForAuctionInfo) downloadedProductInfo;					
				activity.startActivity(new Intent(activity, BidProductInfoActivity.class));		
			}
		}
	}

}
