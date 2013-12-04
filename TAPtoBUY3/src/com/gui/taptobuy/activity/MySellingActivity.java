package com.gui.taptobuy.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.widget.ListView;
import android.widget.Toast;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForSale;

import com.gui.taptobuy.customadapter.BiddingsCustomListAdapter;
import com.gui.taptobuy.customadapter.MySellingListCustomAdapter;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;

import com.gui.taptobuy.phase1.R;

public class MySellingActivity extends Activity  {
 private ListView list;
 private LayoutInflater layoutInflator;
 public static ArrayList<Product> itemsList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emptylist);	
		itemsList = new ArrayList<Product>();
		list = (ListView)findViewById(R.id.ViewList);	
		this.layoutInflator = LayoutInflater.from(this);
		//list.setAdapter(new MySellingListCustomAdapter(this,this.layoutInflator, itemsList));
		list.invalidateViews();
		new mySellingsProductsTask().execute();
	}	
	
	private ArrayList<Product> getMySellingsItems(){
		HttpClient httpClient = new DefaultHttpClient();
		String mySellingsDir = Main.hostName +"/sellings/" + Main.userId;
		HttpGet get = new HttpGet(mySellingsDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray mySellingsArray = (new JSONObject(jsonString)).getJSONArray("mySellingItems");
				itemsList = new ArrayList<Product>();

				JSONObject mySellingsElement = null;
				JSONObject jsonItem = null;
				Product anItem = null;

				for(int i=0; i<mySellingsArray.length();i++){
					mySellingsElement = mySellingsArray.getJSONObject(i);
					jsonItem = mySellingsElement.getJSONObject("item");
					if(mySellingsElement.getBoolean("forBid")){
						anItem = new ProductForAuction(jsonItem.getInt("id"), jsonItem.getString("title"), jsonItem.getString("timeRemaining"), 
								jsonItem.getDouble("shippingPrice"), jsonItem.getString("imgLink"),  jsonItem.getString("sellerUsername"), 
								jsonItem.getDouble("sellerRate"),  jsonItem.getDouble("startinBidPrice"),  jsonItem.getDouble("currentBidPrice"),  jsonItem.getInt("totalBids"));
					}
					else{
						anItem = new ProductForSale(jsonItem.getInt("id"), jsonItem.getString("title"), jsonItem.getString("timeRemaining"), 
								jsonItem.getDouble("shippingPrice"), jsonItem.getString("imgLink"),  jsonItem.getString("sellerUsername"), 
								jsonItem.getDouble("sellerRate"), jsonItem.getInt("remainingQuantity"), jsonItem.getDouble("instantPrice"));
					}				
				
					itemsList.add(anItem);
				}

			}
			else{
				Log.e("JSON","My Sellings json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("My Sellings","Error!", ex);
		}
		return itemsList;
	}

	private class mySellingsProductsTask extends AsyncTask<Void,Void,ArrayList<Product>> {
		public  int downloadadImagesIndex = 0;
		protected ArrayList<Product> doInBackground(Void... params) {
			return getMySellingsItems();//get mySellings result
		}
		protected void onPostExecute(ArrayList<Product> mySellingsItems ) {
			//download images
			for(Product itm: mySellingsItems){
				new DownloadImageTask().execute(itm.getImgLink());
			}
			list.setAdapter(new MySellingListCustomAdapter(MySellingActivity.this, MySellingActivity.this.layoutInflator, mySellingsItems));
		}			
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
				list.invalidateViews();
				itemsList.get(downloadadImagesIndex++).setImg(result);
			}
		}
	}
	public void mySellingsRefresh(){
		new mySellingsProductsTask().execute();
	}
}
