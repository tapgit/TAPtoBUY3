package com.gui.taptobuy.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForSale;

import com.gui.taptobuy.customadapter.CartCustomListAdapter;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;
import com.gui.taptobuy.phase1.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class CartActivity extends Activity implements OnClickListener{	

	public static ListView itemsList;
	private LayoutInflater layoutInflator;
	private Button SelectAllB;
	private Button buySelectedB;
	private Button removeB;

	public static ArrayList<ProductForSale> cartResultItems;
	public static ArrayList<Integer> checkoutListIDs;
	public static ArrayList<CheckBox> checkboxList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.cart);

		cartResultItems = new ArrayList<ProductForSale>();
		checkoutListIDs = new ArrayList<Integer>();
		checkboxList = new ArrayList<CheckBox>();


		this.layoutInflator = LayoutInflater.from(this);
		itemsList = (ListView)findViewById(R.id.listView1);

		SelectAllB = (Button) findViewById(R.id.cartSellectAllB);
		SelectAllB.setOnClickListener(this);

		buySelectedB = (Button) findViewById(R.id.cartBuySelectedB);
		buySelectedB.setOnClickListener(this);

		removeB = (Button) findViewById(R.id.cartRemoveSelectedB);
		removeB.setOnClickListener(this);

		new cartProductsTask().execute();//get products on cart


		//Toast.makeText(this, "size: " + this.checkboxList.size(), Toast.LENGTH_SHORT).show();	
	}


	@Override
	public void onClick(View v) {	

		switch (v.getId())
		{		
		case R.id.cartSellectAllB:
			if(cartResultItems.isEmpty()){
				Toast.makeText(this, "Cart is empty...", Toast.LENGTH_SHORT).show();
			}
			else{
				for(CheckBox check: checkboxList){
					if(!check.isChecked()){
						check.setChecked(true);					
					}
				}	
			}
			break;

		case R.id.cartBuySelectedB:
			// pasar la lista de los items que estan en el cart para la lista del checkout
			if(cartResultItems.isEmpty()){
				Toast.makeText(this, "Cart is empty...", Toast.LENGTH_SHORT).show();
			}
			else if(checkoutListIDs.isEmpty()){
				Toast.makeText(this, "Please select one or more item to buy", Toast.LENGTH_SHORT).show();
			}
			else{
				Intent intent = new Intent(this,OrderCheckoutActivity.class);
				intent.putIntegerArrayListExtra("productsID", checkoutListIDs);
				intent.putExtra("previousActivity", "Cart");
				startActivity(intent);	
			}	
			break;

		case R.id.cartRemoveSelectedB:
			if(cartResultItems.isEmpty()){
				Toast.makeText(this, "Cart is empty...", Toast.LENGTH_SHORT).show();
			}
			else if(checkoutListIDs.isEmpty()){
				Toast.makeText(this, "Please select one or more item to remove from cart", Toast.LENGTH_SHORT).show();
			}
			else{
				for(Integer i: checkoutListIDs){
					for(ProductForSale p: cartResultItems){
						if(p.getId()==i.intValue()){
							cartResultItems.remove(p);
							break;
						}
					}
				}
				for(CheckBox check: checkboxList){
					if(check.isChecked()){
						check.setChecked(false);					
					}
				}
				itemsList.invalidateViews();//refresh
			}
			break;
		}
	}

	private ArrayList<ProductForSale> getCartItems(){
		HttpClient httpClient = new DefaultHttpClient();
		String cartDir = Main.hostName +"/cart/" + Main.userId;
		HttpGet get = new HttpGet(cartDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray cartResultArray = (new JSONObject(jsonString)).getJSONArray("cart");
				cartResultItems = new ArrayList<ProductForSale>();

				JSONObject searchElement = null;
				JSONObject jsonItem = null;
				ProductForSale anItem = null;

				for(int i=0; i<cartResultArray.length();i++){
					searchElement = cartResultArray.getJSONObject(i);
					jsonItem = searchElement.getJSONObject("item");
					anItem = new ProductForSale(jsonItem.getInt("id"), jsonItem.getString("title"), jsonItem.getString("timeRemaining"), 
							jsonItem.getDouble("shippingPrice"), jsonItem.getString("imgLink"),  jsonItem.getString("sellerUsername"), 
							jsonItem.getDouble("sellerRate"), jsonItem.getInt("remainingQuantity"), jsonItem.getDouble("instantPrice"));

					cartResultItems.add(anItem);
				}

			}
			else{
				Log.e("JSON","cart json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Cart","Error!", ex);
		}
		return cartResultItems;
	}

	private class cartProductsTask extends AsyncTask<Void,Void,ArrayList<ProductForSale>> {
		public  int downloadadImagesIndex = 0;
		protected ArrayList<ProductForSale> doInBackground(Void... params) {
			return getCartItems();//get cart items for this user
		}
		protected void onPostExecute(ArrayList<ProductForSale> cartResultItems ) {
			//download images

			for(ProductForSale itm: cartResultItems){
				new DownloadImageTask().execute(itm.getImgLink());
			}
			itemsList.setAdapter(new CartCustomListAdapter(CartActivity.this,layoutInflator, cartResultItems));
		}			
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
				itemsList.invalidateViews();
				cartResultItems.get(downloadadImagesIndex++).setImg(result);
			}
		}

	}
}
