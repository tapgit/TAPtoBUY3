package com.gui.taptobuy.customadapter;

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
import com.gui.taptobuy.Entities.Order;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.MyHistoryActivity;
import com.gui.taptobuy.activity.PurchasedOrderReceiptActivity;
import com.gui.taptobuy.activity.SearchActivity;
import com.gui.taptobuy.activity.MyHistoryActivity.MyViewHistory;
import com.gui.taptobuy.activity.SoldOrderReceiptActivity;

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
	public static Order theOrder;

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
		String userId = Main.userId + "";
		String productId = itemHolder.item.getId() + "";
		String orderId = itemHolder.item.getOrder_id() + "";
		String sold = "true";
		String forBid = "false";
		if(itemHolder.item instanceof MyHistoryProductForAuction){
			forBid = "true";
		}
		
		new orderReceiptTask().execute(userId,productId,orderId,sold,forBid);
	}

	private Order getOrderReceipt(String userId, String productId, String orderId, String sold, String forBid){
		HttpClient httpClient = new DefaultHttpClient();
		String orderDir = Main.hostName +"/orders/" + userId + "/" + productId + "/" + orderId + "/" + sold + "/" + forBid;
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
				JSONArray itemsArray = orderJson.getJSONArray("items");
				MyHistoryProduct[] products = null;
				JSONObject tmpItemJson = null;
				
				if(json.getBoolean("forBid")){
					tmpItemJson = itemsArray.getJSONObject(0);
					products = new MyHistoryProductForAuction[1];
					products[0] = new MyHistoryProductForAuction(tmpItemJson.getInt("id"), tmpItemJson.getInt("order_id"), tmpItemJson.getString("title"),
							tmpItemJson.getDouble("paidPrice"), tmpItemJson.getInt("paidShippingPrice"), tmpItemJson.getString("imgLink"),
							tmpItemJson.getString("sellerUsername"), tmpItemJson.getDouble("sellerRate"), tmpItemJson.getInt("bidsAmount"));
				}
				else{
					products = new MyHistoryProductForSale[itemsArray.length()];
					for(int i=0; i<itemsArray.length();i++){
						tmpItemJson = itemsArray.getJSONObject(i);
						products[i] = new MyHistoryProductForSale(tmpItemJson.getInt("id"), tmpItemJson.getInt("order_id"), tmpItemJson.getString("title"),
								tmpItemJson.getDouble("paidPrice"), tmpItemJson.getInt("paidShippingPrice"), tmpItemJson.getString("imgLink"),
								tmpItemJson.getString("sellerUsername"), tmpItemJson.getDouble("sellerRate"), tmpItemJson.getInt("quantity"));
					}
				}
				theOrder = new Order(orderJson.getInt("id"), orderJson.getString("date"), orderJson.getString("sellerUsername"), orderJson.getString("buyerUsername"), orderJson.getString("shippingAddressStr"),
						orderJson.getString("paymentMethod"), orderJson.getDouble("paidPrice"), orderJson.getDouble("shippingPrice"), products);
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
		public  int downloadadImagesIndex = 0;
		
		protected Order doInBackground(String... params) {
			return getOrderReceipt(params[0], params[1],params[2],params[3],params[4]);//get order receipt
		}
		protected void onPostExecute(Order theOrder) {
			//download images
			//MyHistoryProduct[] products = theOrder.getProducts();
			//for(MyHistoryProduct itm: products){
			//	new DownloadImageTask().execute(itm.getImgLink());				
			//}
//			itemsList.setAdapter(new SearchResultsCustomListAdapter(SearchActivity.this,SearchActivity.this.pic,SearchActivity.this.layoutInflator, searchResultItems));
			//SoldOrderReceiptActivity.showingOrder = theOrder;
			Intent intent = new Intent(activity, SoldOrderReceiptActivity.class);
			activity.startActivity(intent);
		}
				
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
//				itemsList.invalidateViews();
				theOrder.getProducts()[downloadadImagesIndex++].setImg(result);
			}
		}
	}
}
