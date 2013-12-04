package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.CartActivity;
import com.gui.taptobuy.activity.OrderCheckoutActivity;
import com.gui.taptobuy.activity.SearchActivity.MyViewItem;
import com.gui.taptobuy.phase1.R;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.BuyItProductInfoActivity;
import com.gui.taptobuy.activity.CartActivity;
import com.gui.taptobuy.activity.SearchActivity.MyViewItem;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

public class CartCustomListAdapter extends BaseAdapter implements OnClickListener{

	private CartActivity activity;
	private LayoutInflater layoutInflater;
	private ArrayList<ProductForSale> items;	

	public CartCustomListAdapter (CartActivity a, LayoutInflater l, ArrayList<ProductForSale> items)
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
		final MyViewItem itemHolder;
		final ProductForSale item = items.get(position);

		itemRow = layoutInflater.inflate(R.layout.cart_productrow, parent, false);
		itemHolder = new MyViewItem();
		itemHolder.item = item;

		itemHolder.itemPic = (ImageView) itemRow.findViewById(R.id.cartProductPic);
		itemHolder.productName = (TextView) itemRow.findViewById(R.id.cartProdName);
		itemHolder.sellerUserName = (TextView) itemRow.findViewById(R.id.cartSellerUsername);
		itemHolder.priceAndShiping = (TextView) itemRow.findViewById(R.id.cartPrice);
		itemHolder.sellerRating = (RatingBar)itemRow.findViewById(R.id.cartSellerRating);
		itemHolder.check = (CheckBox) itemRow.findViewById(R.id.cartSelectedCheck);
		//		
		//		if(CartActivity.checkboxList.size()<items.size()){
		//			CartActivity.checkboxList.add(itemHolder.check);
		//		}

		itemHolder.cartBuy = (Button) itemRow.findViewById(R.id.cartBuyNowB);
		itemHolder.cartRemove = (Button) itemRow.findViewById(R.id.cartBuyItRemoveB);	


		itemHolder.sellerRating.setTag(itemHolder);
		itemHolder.itemPic.setTag(itemHolder);
		itemHolder.cartBuy.setTag(itemHolder);
		itemHolder.check.setTag(itemHolder);
		itemHolder.cartRemove.setTag(itemHolder);
		//////////////////	
		itemHolder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{    
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if(isChecked){
					CartActivity.checkoutListIDs.add(itemHolder.item.getId());
					//Toast.makeText(activity,CartActivity.checkoutListIDs.size()+"", Toast.LENGTH_SHORT).show();
				}                  
				else{
					int checkedIndex = CartActivity.checkoutListIDs.indexOf(itemHolder.item.getId());
					CartActivity.checkoutListIDs.remove(checkedIndex);
					//Toast.makeText(activity,CartActivity.checkoutListIDs.size()+"", Toast.LENGTH_SHORT).show();
				}    
			}
		});

		if(position == 0 ){ //volvimos a empezar
			if(!CartActivity.checkboxList.isEmpty()) CartActivity.checkboxList.clear();
		}
		CartActivity.checkboxList.add(itemHolder.check);

		itemRow.setTag(itemHolder);
		itemRow.setOnClickListener(this);
		itemHolder.cartBuy.setOnClickListener(this);
		itemHolder.cartRemove.setOnClickListener(this);

		itemHolder.productName.setText(item.getTitle());
		itemHolder.sellerUserName.setText(item.getSellerUsername());

		double shippingPrice = item.getShippingPrice();
		if(shippingPrice == 0)
		{
			itemHolder.priceAndShiping.setText("$" + ((ProductForSale) item).getInstantPrice() +" (Free Shipping)");
		}
		else
		{
			itemHolder.priceAndShiping.setText("$" + ((ProductForSale) item).getInstantPrice() +" (Shipping: $" + shippingPrice + ")");
		}

		itemHolder.sellerRating.setRating((float)item.getSellerRate());
		//itemHolder.timeRemaining.setText(item.getTimeRemaining());
		itemHolder.itemPic.setImageBitmap(item.getImg());		

		return itemRow;
	}

	@Override
	public void onClick(View v)
	{
		MyViewItem itemHolder = (MyViewItem) v.getTag();

		if(v.getId() ==  R.id.cartBuyItRemoveB){
			int idToRemove = itemHolder.item.getId();
			for(ProductForSale p: items){
				if(p.getId()==idToRemove){
					items.remove(p);
					break;
				}
			}
			itemHolder.check.setChecked(false);// para que lo saque del array
			CartActivity.itemsList.invalidateViews(); //refresh
		}
		else if(v.getId() == R.id.cartBuyNowB){
			Intent intent = new Intent(activity,OrderCheckoutActivity.class);
			ArrayList<Integer> productID = new ArrayList<Integer>();
			productID.add(itemHolder.item.getId());
			//////////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////
			//productID tiene el id del producto que acabamos de darle buyitnow (y se envia a OrderCheckoutActivity)
			intent.putIntegerArrayListExtra("productsID", productID);
			intent.putExtra("previousActivity", "Cart");
			activity.startActivity(intent);
		}
		else			
			new productInfoTask().execute(itemHolder.item.getId() + ""); 		
	}

	private Product getProductInfo(String productId)
	{
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
				if(!json.getBoolean("forBid")){
					//					theItem = new ProductForAuctionInfo(itemInfoJson.getInt("id"), itemInfoJson.getString("title"), itemInfoJson.getString("timeRemaining"),
					//							itemInfoJson.getDouble("shippingPrice"), itemInfoJson.getString("imgLink"), itemInfoJson.getString("sellerUsername"),
					//							itemInfoJson.getDouble("sellerRate"), itemInfoJson.getDouble("startinBidPrice"), itemInfoJson.getDouble("currentBidPrice"), itemInfoJson.getInt("totalBids"),
					//							itemInfoJson.getString("product"),itemInfoJson.getString("model"),itemInfoJson.getString("brand"),itemInfoJson.getString("dimensions"),itemInfoJson.getString("description"));
					//				}
					//				else{
					theItem = new ProductForSaleInfo(itemInfoJson.getInt("id"), itemInfoJson.getString("title"), itemInfoJson.getString("timeRemaining"),
							itemInfoJson.getDouble("shippingPrice"), itemInfoJson.getString("imgLink"), itemInfoJson.getString("sellerUsername"),
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
				if(downloadedProductInfo instanceof ProductForSale)
				{				
					BuyItProductInfoActivity.showingProductInfo = (ProductForSaleInfo) downloadedProductInfo;
					Intent intent = new Intent(activity, BuyItProductInfoActivity.class);
					intent.putExtra("previousActivity", "Cart");
					dialog.dismiss();
					activity.startActivity(intent);
				}
			}
		}
	}

}
