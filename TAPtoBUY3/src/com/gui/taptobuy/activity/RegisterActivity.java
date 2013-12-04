package com.gui.taptobuy.activity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private EditText firstnameET;
	private EditText lastnameET;
	private EditText usernameET;
	private EditText password1ET;
	private EditText password2ET;
	private EditText emailET;
	//shipping address
	private EditText shipCountryET;
	private EditText shipContactNameET;
	private EditText shipStreetET;
	private EditText shipCityET;
	private EditText shipStateET;
	private EditText shipZipCodeET;
	private EditText shipTelephoneET;
	//billing address
	private EditText billCountryET;
	private EditText billContactNameET;
	private EditText billStreetET;
	private EditText billCityET;
	private EditText billStateET;
	private EditText billZipCodeET;
	private EditText billTelephoneET;
	//credit card
	private EditText creditCardNumberET;
	private EditText creditCardHoldersNameET;
	private EditText creditCardExpDateET;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.user_register);		
		initialize();		
	}

	private void initialize() {
		// TODO Auto-generated method stub

		firstnameET = (EditText) findViewById(R.id.etUserNameF);
		lastnameET = (EditText) findViewById(R.id.etUserNameL);
		usernameET = (EditText) findViewById(R.id.etUserName);
		password1ET = (EditText) findViewById(R.id.etUserPassword1);
		password2ET = (EditText) findViewById(R.id.etUserPassword2);
		emailET = (EditText) findViewById(R.id.etUserEmail);

		shipCountryET = (EditText) findViewById(R.id.etShipUserCountry);
		//FALTA:shipContactNameET = (EditText) findViewById(R.id.et);
		shipStreetET = (EditText) findViewById(R.id.etShipUserStreetAdd);
		shipCityET = (EditText) findViewById(R.id.etShipUserCity);
		shipStateET = (EditText) findViewById(R.id.etShipUserState);
		shipZipCodeET = (EditText) findViewById(R.id.etShipUserZipCode);
		shipTelephoneET = (EditText) findViewById(R.id.etShipUserTelNum);

		billCountryET = (EditText) findViewById(R.id.etBillUserCountry);
		//FALTA:billContactNameET = (EditText) findViewById(R.id.etUserNameF);
		billStreetET = (EditText) findViewById(R.id.etBillUserStreetAdd);
		billCityET = (EditText) findViewById(R.id.etBillUserCity);
		billStateET = (EditText) findViewById(R.id.etBillUserState);
		billZipCodeET = (EditText) findViewById(R.id.etBillUserZipCode);
		billTelephoneET = (EditText) findViewById(R.id.etBillUserTelNum);

		creditCardNumberET = (EditText) findViewById(R.id.etCreditCardNum);
		creditCardHoldersNameET = (EditText) findViewById(R.id.etCreditCardHolderName);
		creditCardExpDateET = (EditText) findViewById(R.id.etCreditCardExpirationDate);

		Button submitInf = (Button) findViewById(R.id.bSubmitUserInf);
		submitInf.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(v.equals((Button) findViewById(R.id.bSubmitUserInf)))
				{
					if(firstnameET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter your first name.", Toast.LENGTH_SHORT).show();										
					}
					else if(lastnameET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter your last name.", Toast.LENGTH_SHORT).show();										
					}
					else if(usernameET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter a username.", Toast.LENGTH_SHORT).show();										
					}
					else if(password1ET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();										
					}
					else if(password2ET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please confirm your password.", Toast.LENGTH_SHORT).show();										
					}
					else if(emailET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please anter an email.", Toast.LENGTH_SHORT).show();										
					}		
					else if(shipCountryET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the country for shipping address.", Toast.LENGTH_SHORT).show();										
					}
					else if(shipStreetET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the street for shipping address.", Toast.LENGTH_SHORT).show();										
					}
					else if(shipCityET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the city for shipping address.", Toast.LENGTH_SHORT).show();										
					}
					else if(shipStateET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the state for shipping address.", Toast.LENGTH_SHORT).show();										
					}
					else if(shipZipCodeET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the zipcode for shipping address.", Toast.LENGTH_SHORT).show();										
					}
					else if(shipTelephoneET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the telephone for shipping address.", Toast.LENGTH_SHORT).show();										
					}
					else if(billCountryET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the country for billing address.", Toast.LENGTH_SHORT).show();										
					}
					else if(billStreetET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the street for billing address.", Toast.LENGTH_SHORT).show();										
					}
					else if(billCityET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the city for billing address.", Toast.LENGTH_SHORT).show();										
					}
					else if(billStateET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the state for billing address..", Toast.LENGTH_SHORT).show();										
					}
					else if(billZipCodeET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the zipcode for billing address.", Toast.LENGTH_SHORT).show();										
					}
					else if(billTelephoneET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the telephone for billing address.", Toast.LENGTH_SHORT).show();										
					}	
					else if(creditCardNumberET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter your creditcard number.", Toast.LENGTH_SHORT).show();										
					}
					else if(creditCardHoldersNameET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the creditcard holder's name.", Toast.LENGTH_SHORT).show();										
					}
					else if(creditCardExpDateET.getText().toString().equals("")){
						Toast.makeText(RegisterActivity.this, "Please enter the creditcard expiration date.", Toast.LENGTH_SHORT).show();										
					}
					else if(!password1ET.getText().toString().equals(password2ET.getText().toString()) ) {			
						Toast.makeText(RegisterActivity.this, "Passwords don't match, try again", Toast.LENGTH_SHORT).show();			
					}
					else{
						new registerTask().execute();
					}	
				}
			}       	
		});	
	}
	private int register(){
		int result = -1;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Main.hostName + "/register");
		post.setHeader("content-type", "application/json");
		try
		{
			JSONObject json = new JSONObject();

			json.put("firstname", firstnameET.getText().toString());	
			json.put("lastname", lastnameET.getText().toString());
			json.put("username", usernameET.getText().toString());
			json.put("password", password1ET.getText().toString());
			json.put("email", emailET.getText().toString());
			JSONObject shipObj = new JSONObject();
			shipObj.put("country", shipCountryET.getText().toString());
			
			shipObj.put("street", shipStreetET.getText().toString());
			shipObj.put("city", shipCityET.getText().toString());
			shipObj.put("state", shipStateET.getText().toString());
			shipObj.put("zip_code", shipZipCodeET.getText().toString());
			shipObj.put("telephone", shipTelephoneET.getText().toString());
			json.put("shipping_address",shipObj);
			JSONObject billObj = new JSONObject();
			billObj.put("country", billCountryET.getText().toString());
			
			billObj.put("street", billStreetET.getText().toString());
			billObj.put("city", billCityET.getText().toString());
			billObj.put("state", billStateET.getText().toString());
			billObj.put("zip_code", billZipCodeET.getText().toString());
			billObj.put("telephone", billTelephoneET.getText().toString());
			json.put("billing_address",billObj);
			JSONObject crCard = new JSONObject();
			crCard.put("number", creditCardNumberET.getText().toString());
			crCard.put("holders_name", creditCardHoldersNameET.getText().toString());
			crCard.put("exp_date", creditCardExpDateET.getText().toString());
			json.put("credit_card", crCard);

			StringEntity entity = new StringEntity(json.toString());
			post.setEntity(entity);

			HttpResponse resp = httpClient.execute(post);
			if(resp.getStatusLine().getStatusCode() == 201){
				result = 0;
			}		
		}
		catch(Exception ex)
		{
			Log.e("Could not register","Error!", ex);
		}
		return result;
	}
	public class registerTask extends AsyncTask<Void,Void,Integer> {

		protected Integer doInBackground(Void... params) {
			return register();
		}

		protected void onPostExecute(Integer result) {

			if (result == 0)//user was created successfully
			{
				Toast.makeText(RegisterActivity.this, "You are now a member! :D", Toast.LENGTH_LONG).show();										
				startActivity(new Intent(RegisterActivity.this,SearchActivity.class));
			}
			else if(result == 1){
				//username is already taken
			}
			else if(result == 2){
				//another user has that email address
			}
			else{

			}
		}

	}
}
