package com.gui.taptobuy.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.gui.taptobuy.Entities.ProductForSale;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
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
	public static int BuyNowitemId;

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
			if(Main.signed){
				Intent intent = new Intent(this, OrderCheckoutActivity.class);
				intent.putExtra("previousActivity", "BuyItProductInfo");
				intent.putExtra("productID", showingProductInfo.getId());
				startActivity(intent);
			}
			else{
				Toast.makeText(this, "You must be logged in to process the order", Toast.LENGTH_SHORT).show();
			}
		}
		else if(v.getId()== R.id.BuyInfoAddToCartb){
			if(Main.signed){
				new addToCartTask().execute(""+ showingProductInfo.getId());
			}
			else{
				Toast.makeText(this, "You must be logged in to place this item on your cart", Toast.LENGTH_SHORT).show();
			}
		}	
	}

	private int addToCart(String productId){
		int result = -1;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Main.hostName + "/cart/" + Main.userId + "/" + productId);
		try
		{
			HttpResponse resp = httpClient.execute(post);
			result = resp.getStatusLine().getStatusCode();
		}
		catch(Exception ex)
		{
			Log.e("Add to cart","Error!", ex);
		}
		return result;
	}
	public class addToCartTask extends AsyncTask<String,Void,Integer> {

		protected Integer doInBackground(String... bidPrice) {
			return addToCart(bidPrice[0]);
		}

		protected void onPostExecute(Integer result) {
			if(result == 200){//ok
				Toast.makeText(BuyItProductInfoActivity.this, "This product has been added to your cart.", Toast.LENGTH_SHORT).show();
				BuyItProductInfoActivity.this.finish();
			}	
			else if(result == 400){//badrequest(Item already on cart)
				Toast.makeText(BuyItProductInfoActivity.this, "This item is already on your cart.", Toast.LENGTH_SHORT).show();
			}
			else if(result == 404){//notFound(Ended Sale)
				Toast.makeText(BuyItProductInfoActivity.this, "This item is no longer available...", Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(BuyItProductInfoActivity.this, "Error: Cannot put this item on cart...", Toast.LENGTH_SHORT).show();
			}
		}

	}

}
