package com.gui.taptobuy.activity;
import com.gui.taptobuy.Entities.ProductForAuction;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
		}
		else{
			placeBid = (Button) findViewById(R.id.BidInfoPlaceBidb);
			bidInput = (EditText) findViewById(R.id.BidInfoPlaceBidInput);
			placeBid.setOnClickListener(this);
			bidInput.setOnClickListener(this);
		}
		// if(previousActivity.equals("Search"))

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
			String bidPrice = bidInput.getText().toString();
			//chakiar si la cantidad entrada en bid input es valida
			if(!bidPrice.equals("")){	
				Toast.makeText(this, "Your bid "+bidInput.getText().toString()+" has been placed", Toast.LENGTH_SHORT).show();
				//anadir producto a Mybidding y aumentar el num de bids y current bid del producto
			}
			else{
				Toast.makeText(this, "Error: you must provide a bidding quantity", Toast.LENGTH_LONG).show();
			}
		}	
	}
}