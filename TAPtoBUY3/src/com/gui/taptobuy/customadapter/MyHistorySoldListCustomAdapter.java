package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.MyHistoryProduct;
import com.gui.taptobuy.Entities.MyHistoryProductForAuction;
import com.gui.taptobuy.Entities.MyHistoryProductForSale;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.MyHistoryActivity;
import com.gui.taptobuy.activity.MyHistoryActivity.MyViewHistory;

import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MyHistorySoldListCustomAdapter extends BaseAdapter implements OnClickListener {
	
	private MyHistoryActivity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<MyHistoryProduct> items;	

	public MyHistorySoldListCustomAdapter (MyHistoryActivity a, LayoutInflater l, ArrayList<MyHistoryProduct> items)
	{
		this.activity = a;		
		this.layoutInflater = l;
		this.items = items;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
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
		MyViewHistory itemHolder;
		MyHistoryProduct item = items.get(position);
		        	
			itemRow = layoutInflater.inflate(R.layout.myhistory_soldprodrow, parent, false); 
			itemHolder = new MyViewHistory();
			itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.myHist_soldProductPic);
			itemHolder.productName = (TextView) itemRow.findViewById(R.id.myHist_soldProdName);
			itemHolder.buyerUserN = (TextView) itemRow.findViewById(R.id.myHist_soldBuyersName);
			itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.myHist_soldPrice);		
						
			itemHolder.itemPic.setTag(itemHolder);
			itemRow.setTag(itemHolder);

			double shippingPrice = item.getPaidShippingPrice();
			
			if(item instanceof MyHistoryProductForAuction)
			{
			itemHolder.buyerUserN.setText("Won by: "+item.getSellerUsername());
				if(shippingPrice == 0){					
					itemHolder.priceAndShiping.setText("$" + ((MyHistoryProductForAuction) item).getPaidPrice()+" (Free Shipping)");
				}
				else{
					itemHolder.priceAndShiping.setText("$" + ((MyHistoryProductForAuction) item).getPaidPrice()+" (Shipping: $" + shippingPrice + ")"); 
				}					
			}
			else
			{	
			itemHolder.buyerUserN.setText("Purchased by: "+item.getSellerUsername());	
				if(shippingPrice == 0){
					itemHolder.priceAndShiping.setText("$" + ((MyHistoryProductForSale) item).getPaidPrice() +" (Free Shipping)");
				}
				else{
					itemHolder.priceAndShiping.setText("$" + ((MyHistoryProductForSale) item).getPaidPrice() +" (Shipping: $" + shippingPrice + ")"); 
				} 
			}		
		itemRow.setOnClickListener(this);  
		
		itemHolder.item = item;
		itemHolder.productName.setText(item.getTitle());  		
		itemHolder.itemPic.setImageBitmap(item.getImg());

		return itemRow;
	}

	@Override
	public void onClick(View v) {
		
		MyViewHistory  itemHolder = (MyViewHistory) v.getTag(); 	
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
		Dialog dialog;
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
					intent.putExtra("previousActivity", "MyHistory");
					dialog.dismiss();					
					activity.startActivity(intent);			
				}
				else{//for sale
					BuyItProductInfoActivity.showingProductInfo = (ProductForSaleInfo) downloadedProductInfo;					
					intent = new Intent(activity, BuyItProductInfoActivity.class);
					intent.putExtra("previousActivity", "MyHistory");				
					dialog.dismiss();	
					activity.startActivity(intent);
				}
			}
		}
	}
}
