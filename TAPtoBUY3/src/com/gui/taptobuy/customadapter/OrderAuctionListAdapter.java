package com.gui.taptobuy.customadapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.OrderCheckoutActivity;
import com.gui.taptobuy.activity.SearchActivity.MyViewItem;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class OrderAuctionListAdapter extends BaseAdapter implements OnClickListener {
	
	private Activity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<Product> items;	

	public OrderAuctionListAdapter (Activity a, LayoutInflater l, ArrayList<Product> items)
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
	public Object getItem(int arg0) {
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
		final MyViewItem itemHolder;
		Product item = items.get(position);
		      	
			itemRow = layoutInflater.inflate(R.layout.order_auctioncheckout_row, parent, false); 
			itemHolder = new MyViewItem();
			itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.ordAuCheckRow_ProductPic);
			itemHolder.productName = (TextView) itemRow.findViewById(R.id.ordAuCheckRow_ProdName);
			itemHolder.sellerUserName = (TextView) itemRow.findViewById(R.id.ordAuCheckRow_SellerUN);
			itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.ordAuCheckRow_Price);					                     
			itemHolder.sellerRating = (RatingBar)itemRow.findViewById(R.id.ordAuCheckRow_SellerRating);			

			itemHolder.sellerRating.setTag(itemHolder);
			itemHolder.itemPic.setTag(itemHolder);
			itemRow.setTag(itemHolder);

			double shippingPrice = item.getShippingPrice();
			if(shippingPrice == 0){
				itemHolder.priceAndShiping.setText("$" + ((ProductForAuction) item).getCurrentBidPrice()+" (Free Shipping)");
			}
			else{
				itemHolder.priceAndShiping.setText("$" + ((ProductForAuction) item).getCurrentBidPrice()+" (Shipping: $" + shippingPrice + ")"); 
			}		
		
		itemRow.setOnClickListener(this); 					
		itemHolder.item = item;
		itemHolder.productName.setText(item.getTitle());   		
		itemHolder.sellerUserName.setText(item.getSellerUsername());		
		itemHolder.sellerRating.setRating((float)item.getSellerRate());
		itemHolder.itemPic.setImageBitmap(item.getImg());

		return itemRow;
	}

	@Override
	public void onClick(View v) {
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
		Intent intent;
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
					intent.putExtra("previousActivity", "OrderCheckout");
					activity.startActivity(intent);			
				}
				else{//for sale
					BuyItProductInfoActivity.showingProductInfo = (ProductForSaleInfo) downloadedProductInfo;					
					intent = new Intent(activity, BuyItProductInfoActivity.class);
					intent.putExtra("previousActivity", "OrderCheckout");
					activity.startActivity(intent);
				}
			}
		}
	}
}
