package com.gui.taptobuy.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.gson.Gson;
import com.gui.taptobuy.Entities.Address;
import com.gui.taptobuy.Entities.CreditCard;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.User;

import com.gui.taptobuy.customadapter.OrderCustomListAdapter;

import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

public class OrderCheckoutActivity extends Activity implements OnClickListener{

	private Button placeOrder;
	private Spinner cardsSpinner;
	private Spinner shipAddrSpinner;
	private TextView shippingAdd;
	private ListView itemsList;
	private TextView totalPrice;
	private User receivedUserdata;
	public static ArrayList<Product>  items;
	private ArrayList<Integer> productsIDList;
	private LayoutInflater layoutInflator;
	private String totalPriceValue;
	private int orderID;

	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.order_checkout);		

		this.layoutInflator = LayoutInflater.from(this);

		Intent intent = getIntent();
		String previousAtivity = intent.getStringExtra("previousActivity");

		if(previousAtivity.equals("Cart")){
			productsIDList = intent.getIntegerArrayListExtra("productsID");//recoger arraylist de id's de los productos q vamos a comprar
		}
		else if(previousAtivity.equals("BuyItProductInfo")){
			productsIDList = new ArrayList<Integer>();
			productsIDList.add(intent.getIntExtra("productID", 0)); /// cudiado con el 0 - default value
		}		
		if(!productsIDList.isEmpty()) 
			new buyNowProductsTask().execute(productsIDList);


		itemsList = (ListView)findViewById(R.id.checkout_ItemsList);
		shippingAdd = (TextView)findViewById(R.id.checkout_ShippingAdress);
		totalPrice = (TextView) findViewById(R.id.checkout_TotalPayment);
		placeOrder = (Button) findViewById(R.id.checkout_PlaceOrderB);
		placeOrder.setOnClickListener(this);

		cardsSpinner = (Spinner) findViewById (R.id.checkout_CardsSpinner);
		shipAddrSpinner = (Spinner) findViewById(R.id.checkout_SpinnerAdd);			

		shipAddrSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){	
			@Override
			public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2, long arg3) 
			{
				int index = arg0.getSelectedItemPosition();
				Address selectedAddress = receivedUserdata.getShipping_addresses()[index];
				shippingAdd.setText(selectedAddress.getContact_name() + "\n" + selectedAddress.getStreet() +  "\n" + selectedAddress.getCity() + 
						" " + selectedAddress.getState() + " " + selectedAddress.getZip_code() + "\n" + selectedAddress.getCountry() + "\n" + 
						selectedAddress.getTelephone());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}	        	
		});

		new getMyAccountSettingsTask().execute();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.checkout_PlaceOrderB){			
			final Dialog dialog; 
			dialog = new Dialog(OrderCheckoutActivity.this); 
			dialog.setContentView(R.layout.order_placed_dialog);
			dialog.setTitle("Order placed Successfully"); 
			Button ok = (Button) dialog.findViewById(R.id.orderpalceOK); 
			ok.setOnClickListener(new View.OnClickListener() { 
				public void onClick(View v) { 
					//Intent search = new Intent(OrderCheckoutActivity.this,SearchActivity.class); 
					//search.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					//startActivity(search); 
					dialog.dismiss(); 
				}
			});
			dialog.show(); 
		}	
	}


	private ArrayList<Product> buyNowProducts(ArrayList<Integer> productsIDList){
		HttpClient httpClient = new DefaultHttpClient();

		HttpPost post = new HttpPost(Main.hostName + "/buynow/" + Main.userId);
		post.setHeader("content-type", "application/json");		
		try
		{
			JSONObject jsonObj = new JSONObject();
			JSONArray array = new JSONArray();
			for(Integer i: productsIDList){
				array.put(i);
			}
			jsonObj.put("productIdsToBuy", array);

			StringEntity entity = new StringEntity(jsonObj.toString());
			post.setEntity(entity);
			HttpResponse resp = httpClient.execute(post);

			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray productsToBuyArray = (new JSONObject(jsonString)).getJSONArray("productsToBuy");
				items = new ArrayList<Product>();

				JSONObject buyNowElement = null;
				JSONObject jsonItem = null;
				Product anItem = null;

				for(int i=0; i<productsToBuyArray.length();i++){
					buyNowElement = productsToBuyArray.getJSONObject(i);
					jsonItem = buyNowElement.getJSONObject("item");
					if(buyNowElement.getBoolean("forBid")){
						anItem = new ProductForAuction(jsonItem.getInt("id"), jsonItem.getString("title"), jsonItem.getString("timeRemaining"), 
								jsonItem.getDouble("shippingPrice"), jsonItem.getString("imgLink"),  jsonItem.getString("sellerUsername"), 
								jsonItem.getDouble("sellerRate"),  jsonItem.getDouble("startinBidPrice"),  jsonItem.getDouble("currentBidPrice"),  jsonItem.getInt("totalBids"));
					}
					else{
						anItem = new ProductForSale(jsonItem.getInt("id"), jsonItem.getString("title"), jsonItem.getString("timeRemaining"), 
								jsonItem.getDouble("shippingPrice"), jsonItem.getString("imgLink"),  jsonItem.getString("sellerUsername"), 
								jsonItem.getDouble("sellerRate"), jsonItem.getInt("remainingQuantity"), jsonItem.getDouble("instantPrice"));
					}
					items.add(anItem);
				}
				totalPriceValue = new JSONObject(jsonString).getString("total");

			}
			else{
				Log.e("JSON","products to buy json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Could get buy now products","Error!", ex);
		}
		return items;
	}

	private class buyNowProductsTask extends AsyncTask<ArrayList<Integer>,Void,ArrayList<Product>> {
		public  int downloadadImagesIndex = 0;
		protected ArrayList<Product> doInBackground(ArrayList<Integer>... productsIDList) {
			return buyNowProducts(productsIDList[0]);
		}
		protected void onPostExecute(ArrayList<Product> buyNowProducts ) {
			totalPrice.setText(totalPriceValue);
			//download images
			for(Product itm: buyNowProducts){
				new DownloadImageTask().execute(itm.getImgLink());
			}
			itemsList.setAdapter(new OrderCustomListAdapter(OrderCheckoutActivity.this, OrderCheckoutActivity.this.layoutInflator, buyNowProducts));
		}			
		private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

			protected Bitmap doInBackground(String... urls) {
				return ImageManager.downloadImage(urls[0]);
			}
			protected void onPostExecute(Bitmap result) {
				itemsList.invalidateViews();
				items.get(downloadadImagesIndex++).setImg(result);
			}
		}
	}


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	private User getMyAccountSettings(){
		HttpClient httpClient = new DefaultHttpClient();
		String userAccountDir = Main.hostName +"/user/" + Main.userId;
		HttpGet get = new HttpGet(userAccountDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONObject userJson = new JSONObject(jsonString);
				JSONArray shippingAddressesArray = userJson.getJSONArray("shipping_addresses");
				JSONArray creditCardsArray = userJson.getJSONArray("credit_cards");

				JSONObject jsonAddress = null;
				JSONObject jsonCreditCard = null;

				//Get shipping addresses
				Address[] shippingAddresses = new Address[shippingAddressesArray.length()];
				for(int i=0;i<shippingAddressesArray.length();i++){
					jsonAddress = shippingAddressesArray.getJSONObject(i);
					shippingAddresses[i] = new Address(jsonAddress.getInt("id"),jsonAddress.getString("country"), jsonAddress.getString("contact_name"), 
							jsonAddress.getString("street"), jsonAddress.getString("city"), jsonAddress.getString("state"), 
							jsonAddress.getString("zip_code"), jsonAddress.getString("telephone"));
				}
				//Get credit cards
				CreditCard[] creditCards = new CreditCard[creditCardsArray.length()];
				for(int i=0;i<creditCardsArray.length();i++){
					jsonCreditCard = creditCardsArray.getJSONObject(i);
					//get the billing address from this credit card		
					jsonAddress = jsonCreditCard.getJSONObject("billing_address");
					Address aBillingAddress = new Address(jsonAddress.getInt("id"),jsonAddress.getString("country"), jsonAddress.getString("contact_name"), 
							jsonAddress.getString("street"), jsonAddress.getString("city"), jsonAddress.getString("state"), 
							jsonAddress.getString("zip_code"), jsonAddress.getString("telephone"));

					creditCards[i] = new CreditCard(jsonCreditCard.getString("number"), jsonCreditCard.getString("holders_name"), 
							jsonCreditCard.getString("exp_date"), aBillingAddress);
				}

				receivedUserdata = new User(userJson.getInt("id"), userJson.getString("firstname"), userJson.getString("lastname"), 
						userJson.getString("username"), userJson.getString("password"), userJson.getString("email"), 
						shippingAddresses, creditCards);


			}
			else{
				Log.e("JSON","User account data could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Account Settings","Error!", ex);
		}
		return receivedUserdata;
	}

	private class getMyAccountSettingsTask extends AsyncTask<Void,Void,User> {
		protected User doInBackground(Void... params) {
			return getMyAccountSettings();
		}
		protected void onPostExecute(User receivedUserdata) {


			Address firstAddress = receivedUserdata.getShipping_addresses()[0];
			shippingAdd.setText(firstAddress.getContact_name() + "\n" + firstAddress.getStreet() +  "\n" + firstAddress.getCity() + 
					" " + firstAddress.getState() + " " + firstAddress.getZip_code() + "\n" + firstAddress.getCountry() + "\n" + 
					firstAddress.getTelephone());

			//Extraer los identificadores de cada shipping address para llenar el spinner:
			String[] shippingAddressesIdentifiers = new String[receivedUserdata.getShipping_addresses().length];
			for(int i=0;i<shippingAddressesIdentifiers.length;i++){
				shippingAddressesIdentifiers[i] = receivedUserdata.getShipping_addresses()[i].getStreet();
			}

			//Extraer los identificadores de cada credit card para llenar el spinner:
			String[] creditCardsIdentifiers = new String[receivedUserdata.getCredit_cards().length];
			for(int i=0;i<creditCardsIdentifiers.length;i++){
				CreditCard tmpCrdCard = receivedUserdata.getCredit_cards()[i];
				creditCardsIdentifiers[i] = "xxxx-xxxx-xxxx-" + tmpCrdCard.getNumber().substring(12);
			}

			ArrayAdapter<String> shippingAddressesAdapter = new ArrayAdapter<String>(OrderCheckoutActivity.this,android.R.layout.simple_list_item_single_choice, shippingAddressesIdentifiers);
			shipAddrSpinner.setAdapter(shippingAddressesAdapter);

			ArrayAdapter<String> creditCardsAdapter = new ArrayAdapter<String>(OrderCheckoutActivity.this,android.R.layout.simple_list_item_single_choice, creditCardsIdentifiers);
			cardsSpinner.setAdapter(creditCardsAdapter);

		}			

	}	

}
