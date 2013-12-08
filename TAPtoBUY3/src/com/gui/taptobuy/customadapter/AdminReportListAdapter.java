package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gui.taptobuy.Entities.MyBiddingsProduct;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductReport;
import com.gui.taptobuy.activity.AdministratorActivity;
import com.gui.taptobuy.activity.AdministratorActivity.MyViewReport;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.MyBiddingActivity;
import com.gui.taptobuy.activity.MyBiddingActivity.MyViewAuctionItem;

import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

public class AdminReportListAdapter extends BaseAdapter implements OnClickListener {
	private AdministratorActivity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<ProductReport> reportInfo;	


	public AdminReportListAdapter (AdministratorActivity a, LayoutInflater l, ArrayList<ProductReport> reportInfo)
	{
		this.activity = a;		
		this.layoutInflater = l;
		this.reportInfo = reportInfo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.reportInfo.size();
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
		MyViewReport itemHolder;
		ProductReport repo = reportInfo.get(position);

		itemRow = layoutInflater.inflate(R.layout.admin_report_row, parent, false); 
		itemHolder = new MyViewReport();
		itemHolder.product =  (ImageView) itemRow.findViewById(R.id.report_ProdName);
		itemHolder.soldAmount = (TextView) itemRow.findViewById(R.id.report_soldQTY);
		itemHolder.productRevenue = (TextView) itemRow.findViewById(R.id.report_revenue);			
		itemRow.setTag(itemHolder);


//		itemHolder.bidsAmount.setText(((MyBiddingsProduct) item).getTotalBids()+" bids");
//		
//		itemHolder.reportProd = repo;
//		itemRow.setOnClickListener(this);  	

	

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
