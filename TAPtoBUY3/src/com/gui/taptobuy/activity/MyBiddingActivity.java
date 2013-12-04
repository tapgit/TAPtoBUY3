package com.gui.taptobuy.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.Bid;
import com.gui.taptobuy.Entities.MyBiddingsProduct;
import com.gui.taptobuy.Entities.Product;

import com.gui.taptobuy.customadapter.BiddingsCustomListAdapter;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

public class MyBiddingActivity extends Activity{

	private ListView biddingList ;
	private LayoutInflater layoutInflator;
	public static ArrayList<MyBiddingsProduct> myBiddingsItems;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emptylist);
		this.layoutInflator = LayoutInflater.from(this);
		this.biddingList = (ListView) findViewById(R.id.ViewList);	
		new getMyBiddingsItemsTask().execute();
	}

	public static class MyViewAuctionItem {
		public TextView productName, sellerUserName, priceAndShiping,bidsAmount,timeRemaining,isWinningBid;
		public RatingBar sellerRating;		
		public ImageView itemPic;
		public MyBiddingsProduct item;		
	}

	private ArrayList<MyBiddingsProduct> getMyBiddingsItems(){
		HttpClient httpClient = new DefaultHttpClient();
		String myBiddingsDir = Main.hostName +"/mybiddings/" + Main.userId;
		HttpGet get = new HttpGet(myBiddingsDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray myBiddingsArray = (new JSONObject(jsonString)).getJSONArray("myBiddingsItems");
				myBiddingsItems = new ArrayList<MyBiddingsProduct>();

				JSONObject biddingElement = null;
				JSONObject jsonItem = null;
				MyBiddingsProduct anItem = null;

				for(int i=0; i<myBiddingsArray.length();i++){
					biddingElement = myBiddingsArray.getJSONObject(i);
					jsonItem = biddingElement.getJSONObject("item");

					anItem = new MyBiddingsProduct(jsonItem.getInt("id"), jsonItem.getString("title"), jsonItem.getString("timeRemaining"), 
							jsonItem.getDouble("shippingPrice"), jsonItem.getString("imgLink"),  jsonItem.getString("sellerUsername"), 
							jsonItem.getDouble("sellerRate"),  jsonItem.getDouble("startinBidPrice"),  jsonItem.getDouble("currentBidPrice"),
							jsonItem.getInt("totalBids"), jsonItem.getBoolean("winningBid"));
					
					myBiddingsItems.add(anItem);
				}
			}
			else{
				Log.e("JSON","My biddings json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("My biddings","Error!", ex);
		}
		return myBiddingsItems;
	}

	private class getMyBiddingsItemsTask extends AsyncTask<String,Void,ArrayList<MyBiddingsProduct>> {
		public  int downloadadImagesIndex = 0;
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(MyBiddingActivity.this, "Please wait...", "Loading :)");
			dialog.show();
		}	
		protected ArrayList<MyBiddingsProduct> doInBackground(String... params) {
			return getMyBiddingsItems();
		}
		protected void onPostExecute(ArrayList<MyBiddingsProduct> myBiddingsItems ) {
			//download images
			for(Product itm: myBiddingsItems){
				new DownloadImageTask().execute(itm.getImgLink());
			}
			biddingList.setAdapter(new BiddingsCustomListAdapter(MyBiddingActivity.this,MyBiddingActivity.this.layoutInflator, myBiddingsItems));
			dialog.dismiss();
		}			
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
				biddingList.invalidateViews();
				myBiddingsItems.get(downloadadImagesIndex++).setImg(result);
			}
		}
	}
	
	public static class MyBidHolder {
		public Bid bidToshow;
		public TextView bidPrice;
		public TextView placerUsername;
		public int productId;
		public int placerUserId;
	}
}
