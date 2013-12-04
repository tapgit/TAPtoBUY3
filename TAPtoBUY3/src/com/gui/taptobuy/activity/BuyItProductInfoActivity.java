package com.gui.taptobuy.activity;

import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.phase1.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class BuyItProductInfoActivity extends Activity implements OnClickListener
{	
	public static ProductForSaleInfo showingProductInfo;
	private Button buyNow;
	private Button addtoCart;
	private ImageView prodPic;
	private RatingBar sellerRating;
	private TextView prodTitle, prodId, prodTime, prodBrand, prodDimen, prodDescrip, prodSellerUserN, prodPriceAndShip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.productinfo_buynow);
		
		Intent intent = getIntent();
		String previousActivity = intent.getStringExtra("previousActivity");

		prodPic = (ImageView) findViewById(R.id.BuyInfoProdPic);
		prodTitle = (TextView) findViewById(R.id.BuyInfoProdTitle);
		prodId = (TextView) findViewById(R.id.BuyInfoProdID);
		prodTime = (TextView) findViewById(R.id.buyInfotimeleft);
		prodBrand = (TextView) findViewById(R.id.BuyInfoProdBrand);
		prodDimen = (TextView) findViewById(R.id.BuyInfoDimensions);
		prodDescrip = (TextView) findViewById(R.id.BuyInfoDescription);
		prodSellerUserN = (TextView) findViewById(R.id.BuyInfoSellerUserName);
		prodPriceAndShip = (TextView) findViewById(R.id.BuyInfoPrice);
		sellerRating = (RatingBar)findViewById(R.id.BuyInfoSellerRate);
		
		// dependiendo de que activity la llamo, se activan o desactivan los botones de buy y addtoCArt
		
		if(previousActivity.equals("OrderCheckout")||previousActivity.equals("MyHistory")){
			buyNow = (Button) findViewById(R.id.BuyInfoBuyNowb);
			buyNow.setVisibility(View.GONE);
			addtoCart = (Button) findViewById(R.id.BuyInfoAddToCartb);
			addtoCart.setVisibility(View.GONE);
		}
		else if (previousActivity.equals("Cart")){
			buyNow = (Button) findViewById(R.id.BuyInfoBuyNowb);
			buyNow.setOnClickListener(this);
			addtoCart = (Button) findViewById(R.id.BuyInfoAddToCartb);
			addtoCart.setVisibility(View.GONE);
		}
		else if(previousActivity.equals("Search")){
			buyNow = (Button) findViewById(R.id.BuyInfoBuyNowb);
			buyNow.setOnClickListener(this);
			addtoCart = (Button) findViewById(R.id.BuyInfoAddToCartb);
			addtoCart.setOnClickListener(this);
		}	

		sellerRating.setRating((float)showingProductInfo.getSellerRate());
		prodPic.setImageBitmap(showingProductInfo.getImg());
		prodTitle.setText(showingProductInfo.getTitle());
		prodId.setText("Remaining Quantiy: " +showingProductInfo.getRemainingQuantity());
		prodTime.setText(showingProductInfo.getTimeRemaining());
		prodBrand.setText("Brand: " + showingProductInfo.getBrand());
		prodDimen.setText("Dimensions: " + showingProductInfo.getDimensions());
		prodDescrip.setText(showingProductInfo.getDescription());
		prodSellerUserN.setText("Seller: " + showingProductInfo.getSellerUsername());

		double shippingPrice = showingProductInfo.getShippingPrice();
		if(shippingPrice==0){
			prodPriceAndShip.setText("$"+showingProductInfo.getInstantPrice() + " (Free shipping)");
		}
		else{
			prodPriceAndShip.setText("$"+showingProductInfo.getInstantPrice() + " (Shipping: $" + showingProductInfo.getShippingPrice() + ")");
		}
	}

	@Override
	public void onClick(View v) {

		if(v.getId() == R.id.BuyInfoBuyNowb){
			Intent intent = new Intent(this, OrderCheckoutActivity.class);
			intent.putExtra("previousActivity", "BuyItProductInfo");
			intent.putExtra("productID", showingProductInfo.getId());
			startActivity(intent);
		}
		else if(v.getId()== R.id.BuyInfoAddToCartb){
			//envia el producto para el servidor con Id del carrito producto y usuario
			Toast.makeText(this, "This product has been added to your Cart", Toast.LENGTH_LONG).show();
		}	
	}

}
