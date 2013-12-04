package com.gui.taptobuy.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.MyHistoryProduct;
import com.gui.taptobuy.Entities.MyHistoryProductForAuction;
import com.gui.taptobuy.Entities.MyHistoryProductForSale;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForSale;

import com.gui.taptobuy.customadapter.BiddingsCustomListAdapter;
import com.gui.taptobuy.customadapter.MyHistoryBoughtListCustomAdapter;
import com.gui.taptobuy.customadapter.MyHistorySoldListCustomAdapter;
import com.gui.taptobuy.customadapter.SearchResultsCustomListAdapter;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyHistoryActivity extends Activity{
	public  ArrayList<MyHistoryProduct> historyBoughtItems;
	public  ArrayList<MyHistoryProduct> historySoldItems;
	private ListView boughtItemsList;
	private ListView soldItemsList;
	private LayoutInflater layoutInflator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_myhistory);
		this.layoutInflator = LayoutInflater.from(this);
		this.boughtItemsList = (ListView) findViewById(R.id.myHistory_boughtItems);

		historyBoughtItems = new ArrayList<MyHistoryProduct>();
		historySoldItems = new ArrayList<MyHistoryProduct>();

		this.soldItemsList = (ListView) findViewById(R.id.myHistory_SoldItems);
		new myHistoryTask().execute();	}

	public static class MyViewHistory {
		public TextView productName, sellerUserName, priceAndShiping,bidsAmount,wonOr,buyerUserN;
		public RatingBar sellerRating;		
		public ImageView itemPic;
		public MyHistoryProduct item;		

	}
	private void getMyHistoryItems(){
		HttpClient httpClient = new DefaultHttpClient();
		String myHistoryDir = Main.hostName +"/myhistory/" + Main.userId;
		HttpGet get = new HttpGet(myHistoryDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray searchResultArray = (new JSONObject(jsonString)).getJSONArray("myHistory");

				historyBoughtItems = new ArrayList<MyHistoryProduct>();
				historySoldItems = new ArrayList<MyHistoryProduct>();

				JSONObject searchElement = null;
				JSONObject j = null;
				MyHistoryProduct anItem = null;

				for(int i=0; i<searchResultArray.length();i++){
					searchElement = searchResultArray.getJSONObject(i);
					j = searchElement.getJSONObject("item");
					if(searchElement.getBoolean("forBid")){
						//anItem = new MyHistoryProductForAuction(j.getInt("id"), j.getInt("order_id"), j.getString("title"), j.getDouble("paidPrice"), j.getDouble("paidShippingPrice"), j.getString("imgLink"),j.getString("sellerUsername"),-1,-1);
						anItem = new MyHistoryProductForAuction(j.getInt("id"), j.getInt("order_id"), j.getString("title"), j.getDouble("paidPrice"), j.getDouble("paidShippingPrice"),  j.getString("imgLink"), null, j.getString("sellerUsername"),-1,-1);
					}
					else{
						anItem = new MyHistoryProductForSale(j.getInt("id"), j.getInt("order_id"), j.getString("title"), j.getDouble("paidPrice"), j.getDouble("paidShippingPrice"), j.getString("imgLink"),null,j.getString("sellerUsername"), -1, -1);
					}
					if(searchElement.getBoolean("sold")){
						historySoldItems.add(anItem);
					}
					else{
						historyBoughtItems.add(anItem);
					}
				}

			}
			else{
				Log.e("JSON","Myhistory json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Myhistory","Error!", ex);
		}
	}

	private class myHistoryTask extends AsyncTask<Void,Void,ArrayList<MyHistoryProduct>> {
		public  int downloadadImagesIndexBought = 0;
		public  int downloadadImagesIndexSold = 0;
		private Dialog dialog;
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(MyHistoryActivity.this, "Please wait...", "Loading!!");
			dialog.show();
		}
		protected ArrayList<MyHistoryProduct> doInBackground(Void... params) {
			getMyHistoryItems();//get myhistory result
			return null;//
		}
		protected void onPostExecute ( ArrayList<MyHistoryProduct> unused) {
			//download images
			for(MyHistoryProduct itm: historyBoughtItems){
				new DownloadImageBoughtItemTask().execute(itm.getImgLink());
			}
			for(MyHistoryProduct itm: historySoldItems){
				new DownloadImageSoldItemTask().execute(itm.getImgLink());
			}
			boughtItemsList.setAdapter(new MyHistoryBoughtListCustomAdapter(MyHistoryActivity.this,layoutInflator, historyBoughtItems));	
			soldItemsList.setAdapter(new MyHistorySoldListCustomAdapter(MyHistoryActivity.this,layoutInflator, historySoldItems));
			dialog.dismiss();
		}
		
		private class DownloadImageBoughtItemTask extends AsyncTask<String, Void, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
				boughtItemsList.invalidateViews();
				historyBoughtItems.get(downloadadImagesIndexBought++).setImg(result);
			}
		}
		private class DownloadImageSoldItemTask extends AsyncTask<String, Void, Bitmap> {
			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
				soldItemsList.invalidateViews();
				historySoldItems.get(downloadadImagesIndexSold++).setImg(result);
			}
		}
	}

}
