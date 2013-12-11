package com.gui.taptobuy.activity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class BidProductInfoActivity extends Activity implements OnClickListener{

	private Button placeBid;
	private EditText bidInput;
	public static ProductForAuctionInfo showingProductInfo;	
	private ImageView prodPic;
	private RatingBar sellerRating;
	private TextView prodTitle, prodId,dollarSy,
	prodTime, prodBrand, prodDimen, prodDescrip, prodSellerUserN, prodPrice, prodShipPrice;
	private String bidPrice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.productinfo_bid);

		Intent intent = getIntent();


		prodPic = (ImageView) findViewById(R.id.BidInfoProdPic);
		prodTitle = (TextView) findViewById(R.id.BidInfoProdTitle);
		prodId = (TextView) findViewById(R.id.BidInfoProdID);
		prodTime = (TextView) findViewById(R.id.BidInfoTimeLeft);
		prodBrand = (TextView) findViewById(R.id.BidInfoProdBrand);
		prodDimen = (TextView) findViewById(R.id.BidInfoDimensions);
		prodDescrip = (TextView) findViewById(R.id.BidInfoDescription);
		prodSellerUserN = (TextView) findViewById(R.id.BidInfoUserName);
		prodPrice = (TextView) findViewById(R.id.BidsInfoCurrentBid);
		prodShipPrice = (TextView) findViewById(R.id.BidInfoShippingPrice);
		sellerRating = (RatingBar)findViewById(R.id.BidInfoSellerRate);

		if(intent.hasExtra("previousActivity")){
			String previousActivity = intent.getStringExtra("previousActivity");
			if(previousActivity.equals("OrderCheckout")){ //previousActivity.equals("MyHistory")){	
				dollarSy = (TextView)findViewById(R.id.BidInfoDollarSy);
				dollarSy.setVisibility(View.GONE);
				placeBid = (Button) findViewById(R.id.BidInfoPlaceBidb);
				placeBid.setVisibility(View.GONE);
				bidInput = (EditText) findViewById(R.id.BidInfoPlaceBidInput);
				bidInput.setVisibility(View.GONE);			
			}
			else if(previousActivity.equals("Search")){
			placeBid = (Button) findViewById(R.id.BidInfoPlaceBidb);
			bidInput = (EditText) findViewById(R.id.BidInfoPlaceBidInput);
			placeBid.setOnClickListener(this);
			}
		}

		sellerRating.setRating((float)showingProductInfo.getSellerRate());
		prodPic.setImageBitmap(showingProductInfo.getImg());
		prodTitle.setText(showingProductInfo.getTitle());
		prodId.setText("Product Id: " +showingProductInfo.getId());
		prodTime.setText(showingProductInfo.getTimeRemaining());
		prodBrand.setText("Brand: " + showingProductInfo.getBrand());
		prodDimen.setText("Dimensions: " + showingProductInfo.getDimensions());
		prodDescrip.setText(showingProductInfo.getDescription());
		prodSellerUserN.setText("Seller: " + showingProductInfo.getSellerUsername());
		prodPrice.setText("Current bid price: $"+showingProductInfo.getCurrentBidPrice());
		double shippingPrice = showingProductInfo.getShippingPrice();
		if(shippingPrice==0){
			prodShipPrice.setText("Free shipping");
		}
		else{
			prodShipPrice.setText("Shipping price: " + showingProductInfo.getShippingPrice()+"");
		}

	}
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.BidInfoPlaceBidb){			
			bidPrice = bidInput.getText().toString();			
			if(!bidPrice.equals("")){
				//new placeBidTask().execute(bidPrice);							
			}
			else{
				Toast.makeText(this, "Error: you must provide a bidding quantity", Toast.LENGTH_LONG).show();
			}
		}	
	}
	
//	private int placeBid(){
//		int result = -1;
//		HttpClient httpClient = new DefaultHttpClient();
//		HttpPost post = new HttpPost(Main.hostName + "/placeBid");
//		post.setHeader("content-type", "application/json");
//		try
//		{
//			JSONObject json = new JSONObject();
//
//			json.put("bidAmount", this.bidPrice);	
//			json.put("userId", Main.userId);
//			json.put("winning", true);
//		//	json.put("itemId", password1ET.getText().toString());
//			
//			StringEntity entity = new StringEntity(json.toString());
//			post.setEntity(entity);
//
//			HttpResponse resp = httpClient.execute(post);
//			if(resp.getStatusLine().getStatusCode() == 201){
//				result = 0;
//			}		
//		}
//		catch(Exception ex)
//		{
//			Log.e("Could not register","Error!", ex);
//		}
//		return result;
//	}
//	public class placeBidTask extends AsyncTask<String,Void,Integer> {
//
//		protected Integer doInBackground(String... bidPrice) {
//	//		return placeBid(bidPrice[0]);
//		}
//
//		protected void onPostExecute(Integer result) {
//
//			if (result == 0)//user was created successfully
//			{
//				Toast.makeText(BidProductInfoActivity.this, "Your bid of $"+bidPrice+" has been placed", Toast.LENGTH_SHORT).show();		
//			}
//			else if(result == 1){
//				//username is already taken
//			}
//			else if(result == 2){
//				//another user has that email address
//			}
//			else{
//
//			}
//		}
//
//	}
}