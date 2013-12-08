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
import com.gui.taptobuy.Entities.Order;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.PurchasedOrderReceiptActivity;
import com.gui.taptobuy.activity.SoldOrderReceiptActivity;

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
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class MyHistoryBoughtListCustomAdapter extends BaseAdapter implements OnClickListener {
	private MyHistoryActivity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<MyHistoryProduct> items;	
	
	public static Order theOrder;
	private ArrayList<MyHistoryProduct> itemsInSelectedOrder;

	public MyHistoryBoughtListCustomAdapter (MyHistoryActivity a, LayoutInflater l, ArrayList<MyHistoryProduct> items)
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
		        	
			itemRow = layoutInflater.inflate(R.layout.myhistory_boughtprodrow, parent, false); 
			itemHolder = new MyViewHistory();
			itemHolder.itemPic =  (ImageView) itemRow.findViewById(R.id.myHist_BoughtProductPic);
			itemHolder.productName = (TextView) itemRow.findViewById(R.id.myHist_BoughtProdName);
			itemHolder.sellerUserName = (TextView) itemRow.findViewById(R.id.myHist_BoughtSellerID);
			itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.myHist_BoughtPrice);			                     
			itemHolder.sellerRating = (RatingBar)itemRow.findViewById(R.id.myHist_BoughtSellerRating);
			itemHolder.wonOr = (TextView)itemRow.findViewById(R.id.myHist_pruchOrWon);
			itemHolder.sellerRating.setTag(itemHolder);
			itemHolder.itemPic.setTag(itemHolder);
			itemRow.setTag(itemHolder);

			double shippingPrice = item.getPaidShippingPrice();
			
			if(item instanceof MyHistoryProductForAuction){
				itemHolder.wonOr.setText("Won!");
				if(shippingPrice == 0){
					itemHolder.priceAndShiping.setText("$" + ((MyHistoryProductForAuction) item).getPaidPrice()+" (Free Shipping)");
				}
				else{
					itemHolder.priceAndShiping.setText("$" + ((MyHistoryProductForAuction) item).getPaidPrice()+" (Shipping: $" + shippingPrice + ")"); 
				}			
			}
			else{
				itemHolder.wonOr.setText("Purchased");
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
		itemHolder.sellerUserName.setText("Sold by: "+item.getSellerUsername());		
		//itemHolder.sellerRating.setRating((float)item.getSellerRate());		
		itemHolder.itemPic.setImageBitmap(item.getImg());

		return itemRow;
	}

	@Override
	public void onClick(View v) {
		MyViewHistory  itemHolder = (MyViewHistory) v.getTag(); 
		itemsInSelectedOrder = new ArrayList<MyHistoryProduct>();
		ArrayList<MyHistoryProduct> tmpAuc = new ArrayList<MyHistoryProduct>();
		ArrayList<MyHistoryProduct> tmpIns = new ArrayList<MyHistoryProduct>();
		for(MyHistoryProduct p: items){
			if(p.getOrder_id()==itemHolder.item.getOrder_id() && p instanceof MyHistoryProductForAuction){
				tmpAuc.add(p);
			}
		}
		for(MyHistoryProduct p: items){
			if(p.getOrder_id()==itemHolder.item.getOrder_id() && p instanceof MyHistoryProductForSale){
				tmpIns.add(p);
			}
		}
		if(itemHolder.item instanceof MyHistoryProductForAuction){
			itemsInSelectedOrder = tmpAuc;
		}
		else{
			itemsInSelectedOrder = tmpIns;
		}
		
		String orderId = itemHolder.item.getOrder_id() + "";
		String forBid = "false";
		if(itemsInSelectedOrder.get(0) instanceof MyHistoryProductForAuction){
			forBid = "true";
		}
		new orderReceiptTask().execute(orderId, forBid);
	}

	private Order getOrderReceipt(String orderId, String forBid){
		HttpClient httpClient = new DefaultHttpClient();
		String orderDir = Main.hostName +"/orders/" + orderId + "/" + forBid + "/false";
		HttpGet get = new HttpGet(orderDir);
		get.setHeader("content-type", "application/json");
		Order theOrder = null;
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONObject json = new JSONObject(jsonString);
				JSONObject orderJson = json.getJSONObject("order");

				theOrder = new Order(orderJson.getInt("id"), orderJson.getString("date"), orderJson.getString("shippingAddressStr"), orderJson.getString("paymentMethod"), 
						orderJson.getDouble("paidPrice"), -1, null);
			}
			else{
				Log.e("JSON","Order json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Order","Error!", ex);
		}
		return theOrder;
	}

	private class orderReceiptTask extends AsyncTask<String,Void,Order> {

		protected Order doInBackground(String... params) {
			return getOrderReceipt(params[0], params[1]);//get order receipt
		}
		protected void onPostExecute(Order theOrder) {
			theOrder.setProducts(itemsInSelectedOrder);
			PurchasedOrderReceiptActivity.showingOrder = theOrder;
			Intent intent = new Intent(activity, PurchasedOrderReceiptActivity.class);
			activity.startActivity(intent);
		}
				
	}

}
