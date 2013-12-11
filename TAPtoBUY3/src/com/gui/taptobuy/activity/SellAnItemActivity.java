package com.gui.taptobuy.activity;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductForSaleInfo;
import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SellAnItemActivity extends Activity implements OnClickListener
{	
	//for image purposes
	private static final int SELECT_PICTURE = 1;
	private String picPathByFileManager;
	private String picPathByGallery;	
	private EditText picPathInput;

	private int prodID = -1;
	private double sellerRate = -1;
	private String sellerUsername = null;

	private EditText prodTitle;
	private EditText prodModel;
	private EditText prodBrand;
	private EditText prodDimen;
	private EditText prodDescrip;
	private EditText prodProduct;
	private EditText prodPrice;
	private EditText shippingPrice;
	private EditText prodTime;
	private EditText prodQty;
	private TextView priceTV;


	private CheckBox forBidCheck;
	private Product newProd;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.account_sellanitem);	 

		((Button) findViewById(R.id.sell_uploadPicB)).setOnClickListener(this);  
		((Button) findViewById(R.id.sell_sellItemB)).setOnClickListener(this); 

		prodTitle = (EditText) findViewById(R.id.sell_inputProdTitle);
		prodQty = (EditText) findViewById(R.id.sell_inputQty);
		prodModel = (EditText) findViewById(R.id.sell_inputModel);
		prodProduct = (EditText) findViewById(R.id.sell_inputProduct);
		prodBrand = (EditText) findViewById(R.id.sell_inputBrand);
		prodDimen = (EditText) findViewById(R.id.sell_inputDimensions);
		prodDescrip = (EditText) findViewById(R.id.sell_inputDescription);
		prodPrice = (EditText) findViewById(R.id.sell_inputPrice);
		shippingPrice = (EditText) findViewById(R.id.sell_inputShipping);	        
		prodTime = (EditText) findViewById(R.id.sell_inputNumofDays);
		forBidCheck = (CheckBox) findViewById(R.id.sell_ForBiddingCheck);
		picPathInput = (EditText)findViewById(R.id.sell_PicturePath);

		priceTV = (TextView)findViewById(R.id.sell_buyNowPricetext);



		forBidCheck.setOnClickListener(this);
	}
	@Override
	public void onClick(View v)
	{
		switch(v.getId()){

		case R.id.sell_ForBiddingCheck:
			if(forBidCheck.isChecked()){

				priceTV.setText("Starting price: $");
				prodQty.setText("1");
				prodQty.setEnabled(false);
			}
			else{
				priceTV.setText("Buy Now price: $");
				prodQty.setText("");
				prodQty.setEnabled(true);
			}
			break;

			//   select a file
		case R.id.sell_uploadPicB:
			Intent getPic = new Intent();
			getPic.setType("image/*");
			getPic.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(getPic,"Select Picture"), SELECT_PICTURE);
			break;

		case R.id.sell_sellItemB:
			if(prodTitle.getText().toString().equals("")||prodQty.getText().toString().equals("")||prodModel.getText().toString().equals("")||
					prodProduct.getText().toString().equals("")||prodBrand.getText().toString().equals("")||prodDimen.getText().toString().equals("")||
					prodDescrip.getText().toString().equals("")||prodPrice.getText().toString().equals("")||shippingPrice.getText().toString().equals("")||
					prodTime.getText().toString().equals("")||picPathInput.getText().toString().equals("")){
				Toast.makeText(SellAnItemActivity.this, "You must fill every field...", Toast.LENGTH_SHORT).show();
			}
			else{
				new sellProductTask().execute(newProd);
			}
			break;
		}	
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE) {
				Uri selectedImageUri = data.getData();


				//MEDIA GALLERY
				picPathByGallery = getRealPathFromURI(selectedImageUri);

				//setting the path to display
				if(picPathByGallery!=null){
					picPathInput.setText(picPathByGallery);	
					//Toast.makeText(SellAnItemActivity.this, picPathByGallery.toString(), Toast.LENGTH_LONG).show();
					new UploadImageTask().execute(picPathByGallery.toString());
				}
				else{
					Toast.makeText(SellAnItemActivity.this,"No image selected..", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	public String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}



	private boolean sellProduct(Product newProd){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Main.hostName+ "/sellings/" + Main.userId);
		post.setHeader("content-type", "application/json");
		try
		{
			Gson g = new Gson();
			if(forBidCheck.isChecked()){
				newProd = new ProductForAuctionInfo(-1, prodTitle.getText().toString(),prodTime.getText().toString(), Double.parseDouble(shippingPrice.getText().toString()), 
						null, null, -1,  Double.parseDouble(prodPrice.getText().toString()), Double.parseDouble(prodPrice.getText().toString()),
						0, prodProduct.getText().toString(), prodModel.getText().toString(), prodBrand.getText().toString(), 
						prodDimen.getText().toString(), prodDescrip.getText().toString());    				
			}
			else{    	    				
				newProd = new ProductForSaleInfo(-2, prodTitle.getText().toString(),prodTime.getText().toString(), Double.parseDouble(shippingPrice.getText().toString()), 
						null, null, -1, Integer.parseInt(prodQty.getText().toString()), Double.parseDouble(prodPrice.getText().toString()),
						prodProduct.getText().toString(), prodModel.getText().toString(), prodBrand.getText().toString(), prodDimen.getText().toString(), prodDescrip.getText().toString());
			}

			StringEntity entity = new StringEntity(g.toJson(newProd));
			post.setEntity(entity);
			HttpResponse resp = httpClient.execute(post);
			if(resp.getStatusLine().getStatusCode() == 200){
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception ex)
		{
			Log.e("Password check","Error!", ex);
			return false;
		}
	}
	private class sellProductTask extends AsyncTask<Product,Void,Boolean> {

		protected Boolean doInBackground(Product...params) {
			return sellProduct(params[0]);
		}

		protected void onPostExecute(Boolean ok) {
			if (ok)
			{
				Toast.makeText(SellAnItemActivity.this, "Your product has been place on sale", Toast.LENGTH_SHORT).show();
				SellAnItemActivity.this.finish();
			}
			else{
				Toast.makeText(SellAnItemActivity.this, "Product could not be put on sale...", Toast.LENGTH_SHORT).show();
			}
		}
	}


	private class UploadImageTask extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog dialog = null;
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(SellAnItemActivity.this, "Please wait...", "Uploading image");
			dialog.show();
		}
		protected Boolean doInBackground(String... imageLocalDir) {
			return ImageManager.uploadImage(imageLocalDir[0]);
		}
		protected void onPostExecute(Boolean result) {
			dialog.dismiss();
			if(result){
				Toast.makeText(SellAnItemActivity.this, "Image has been uploaded successfully.", Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(SellAnItemActivity.this, "Image could not be uploaded.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
