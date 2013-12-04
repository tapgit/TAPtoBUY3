package com.gui.taptobuy.activity;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.gui.taptobuy.Entities.Address;
import com.gui.taptobuy.Entities.CreditCard;
import com.gui.taptobuy.Entities.User;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AccountSettingsActivity extends Activity implements OnClickListener {

	private Spinner cardsSpinner;
	private Spinner shipAddrSpinner;
	private Dialog Dialog;
	private Button addCard;
	private Button removeCard;
	private Button save;	
	private int userId;
	private boolean userToViewIsAdmin;

	private Button addShippingAdd;
	private Button removeShippingAdd;

	private User receivedUserdata;

	private EditText userName;
	private EditText firstname;
	private EditText password;
	private EditText lastname;
	private EditText email;
	private TextView shippingAdd;

	private int selectedShippingAddress = -1;
	private int selectedCreditCard = -1;


	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//IMPORTANTEEEEEEEEEEEEEE
		//mover lo de chekiar si es admin para el search para que cuando le de myTap salga el Admin activty
		//if(!user.isAdmin)
		setContentView(R.layout.account_settings);
		//else
		//setContentView(R.layout.account_admin);
		

		Intent intent = getIntent();
		if(intent.hasExtra("userId"))
		{
			userId = intent.getIntExtra("userId", 16);
			userToViewIsAdmin = intent.getBooleanExtra("isAdmin", false);
		}	
		else{
			userId = Main.userId;
		}
		
		addCard = (Button)findViewById(R.id.accSet_AddB);
		removeCard = (Button)findViewById(R.id.accSet_RemoveB);
		save = (Button)findViewById(R.id.accSet_SaveB);

		addShippingAdd = (Button)findViewById(R.id.accSet_AddSAButton);
		removeShippingAdd = (Button)findViewById(R.id.accSet_removeSAButton);

		firstname = (EditText) findViewById(R.id.accSet_Fname);
		lastname = (EditText)findViewById(R.id.accSet_Lname);
		password = (EditText)findViewById(R.id.accSet_Passsword);
		email = (EditText)findViewById(R.id.AccSet_email);
		userName = (EditText)findViewById(R.id.accSet_inputUserName);
		shippingAdd = (TextView)findViewById(R.id.accSet_ShippingAddressBox);

		addCard.setOnClickListener(this);
		removeCard.setOnClickListener(this);
		save.setOnClickListener(this);

		addShippingAdd.setOnClickListener(this);
		removeShippingAdd.setOnClickListener(this);

		cardsSpinner = (Spinner) findViewById (R.id.accSet_CardsSpinner);
		shipAddrSpinner = (Spinner) findViewById(R.id.accSet_AddressSpinner);


		// setting action for when an sorting instance is selected

		cardsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectedCreditCard = arg0.getSelectedItemPosition();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}

		});
		shipAddrSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){	
			@Override
			public void onItemSelected(AdapterView<?> arg0,View arg1,int arg2, long arg3) 
			{
				arg1.setSelected(true);
				int index = arg0.getSelectedItemPosition();
				Address selectedAddress = receivedUserdata.getShipping_addresses()[index];
				shippingAdd.setText(selectedAddress.getContact_name() + "\n" + selectedAddress.getStreet() +  "\n" + selectedAddress.getCity() + 
						" " + selectedAddress.getState() + " " + selectedAddress.getZip_code() + "\n" + selectedAddress.getCountry() + "\n" + 
						selectedAddress.getTelephone());
				selectedShippingAddress = index;
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}	        	
		});

		new getMyAccountSettingsTask().execute();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()){

		//////////////SHIPPING ADDRESS SETTINGS		

		case R.id.accSet_AddSAButton:
			Dialog = new Dialog(AccountSettingsActivity.this);

			Dialog.setContentView(R.layout.addshipping_address_dialog);
			Dialog.setTitle("Add New Shipping Address");

			final EditText country = (EditText) Dialog.findViewById(R.id.shippingADD_Country);
			final EditText streetAdd = (EditText) Dialog.findViewById(R.id.shippingADD_StreetAdd);  
			final EditText city = (EditText) Dialog.findViewById(R.id.shippingADD_City);  
			final EditText state = (EditText) Dialog.findViewById(R.id.shippingADD_State); 
			final EditText zipcode = (EditText) Dialog.findViewById(R.id.shippingADD_ZipCode); 
			final EditText phone = (EditText) Dialog.findViewById(R.id.shippingADD_TelNum); 
			final EditText contactName = (EditText) Dialog.findViewById(R.id.shippingADD_ContactName);

			Button btnAddSA = (Button) Dialog.findViewById(R.id.shippingADD_addB);			
			btnAddSA.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) 
				{			                
					String Country = country.getText().toString();
					String StreetAddress = streetAdd.getText().toString();
					String City = city.getText().toString();
					String State = state.getText().toString();
					String ZipCode = zipcode.getText().toString();
					String Telephone = phone.getText().toString();
					String ContactName = contactName.getText().toString();

					if(!Country.equals("")&&!StreetAddress.equals("")&&!City.equals("")&&!State.equals("")&&!ZipCode.equals("")&&!Telephone.equals(""))
					{
						//Add new shipping address:
						Address newShippingAddress = new Address(-1,Country, ContactName, StreetAddress, City, State, ZipCode, Telephone);
						Address[] olds = receivedUserdata.getShipping_addresses();
						Address[] news = new Address[olds.length+1];
						System.arraycopy(olds, 0, news, 0, olds.length);
						news[news.length-1] = newShippingAddress;
						receivedUserdata.setShipping_addresses(news);
						refreshSpinnersData();
						Dialog.dismiss();
					}	
					else{
						Toast.makeText(AccountSettingsActivity.this, "Error: You must fill every field", Toast.LENGTH_SHORT).show();
					}					      				                
				}
			});    
			Dialog.show();

			break;

		case R.id.accSet_removeSAButton:

			Address[] olds = receivedUserdata.getShipping_addresses();
			if(olds.length!=0){
				Address[] news = new Address[olds.length-1];
				ArrayList<Address> tmp = new ArrayList<Address>(Arrays.asList(olds));
				tmp.remove(selectedShippingAddress);
				for(int i=0;i<tmp.size();i++){
					news[i] = tmp.get(i);
				}
				receivedUserdata.setShipping_addresses(news);
				refreshSpinnersData();
			}
			break;			



			////////////////////CREDIT CARD SETTINGS

		case R.id.accSet_AddB:

			Dialog = new Dialog(AccountSettingsActivity.this);

			Dialog.setContentView(R.layout.addcard_dialog);
			Dialog.setTitle("Add Credit Card");

			final EditText CardNumET = (EditText) Dialog.findViewById(R.id.addCard_CardNum);
			final EditText CardHolderET = (EditText) Dialog.findViewById(R.id.addCard_HolderName);  
			final EditText CardExpDateET = (EditText) Dialog.findViewById(R.id.addCard_ExpDate);  

			final EditText billingContactNameEt = (EditText) Dialog.findViewById(R.id.etBillUserContactName);
			final EditText billingCountryEt = (EditText) Dialog.findViewById(R.id.etBillUserCountry);
			final EditText billingStreetEt = (EditText) Dialog.findViewById(R.id.etBillUserStreetAdd);
			final EditText billingCityEt = (EditText) Dialog.findViewById(R.id.etBillUserCity);
			final EditText billingStateEt = (EditText) Dialog.findViewById(R.id.etBillUserState);
			final EditText billingZipCodeEt = (EditText) Dialog.findViewById(R.id.etBillUserZipCode);
			final EditText billingTelephoneEt = (EditText) Dialog.findViewById(R.id.etBillUserTelNum);

			Button btnAddCard = (Button) Dialog.findViewById(R.id.addCardB);			
			btnAddCard.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) 
				{			                
					String CardNum = CardNumET.getText().toString();
					String CardHolder = CardHolderET.getText().toString();
					String CardDate = CardExpDateET.getText().toString();

					String contact_name = billingContactNameEt.getText().toString();
					String country = billingCountryEt.getText().toString();
					String street = billingStreetEt.getText().toString();
					String city = billingCityEt.getText().toString();
					String state = billingStateEt.getText().toString();
					String zip_code = billingZipCodeEt.getText().toString();
					String telephone = billingTelephoneEt.getText().toString();


					if(!CardNum.equals("")&&!CardHolder.equals("")&&!CardDate.equals("")&&!contact_name.equals("")&&!country.equals("")&&
							!street.equals("")&&!city.equals("")&&!state.equals("")&&!zip_code.equals("")&&!telephone.equals(""))
					{
						Address billing_address = new Address(-1,country, contact_name, street, city, state, zip_code, telephone);
						CreditCard newCreditCard = new CreditCard(CardNum, CardHolder, CardDate, billing_address);
						CreditCard[] olds = receivedUserdata.getCredit_cards();
						CreditCard[] news = new CreditCard[olds.length+1];
						System.arraycopy(olds, 0, news, 0, olds.length);
						news[news.length-1] = newCreditCard;
						receivedUserdata.setCredit_cards(news);
						refreshSpinnersData();
						Dialog.dismiss();
					}	
					else{
						Toast.makeText(AccountSettingsActivity.this, "Error: You must fill every field", Toast.LENGTH_SHORT).show();
					}					      				                
				}
			});    
			Dialog.show();

			break;

		case R.id.accSet_RemoveB:

			CreditCard[] oldsC = receivedUserdata.getCredit_cards();
			if(oldsC.length!=0){
				CreditCard[] news = new CreditCard[oldsC.length-1];
				ArrayList<CreditCard> tmp = new ArrayList<CreditCard>(Arrays.asList(oldsC));
				tmp.remove(selectedCreditCard);
				for(int i=0;i<tmp.size();i++){
					news[i] = tmp.get(i);
				}
				receivedUserdata.setCredit_cards(news);
				refreshSpinnersData();
			}	

			
			Toast.makeText(this, "Card successfully Removed", Toast.LENGTH_SHORT).show();	

			break;

		case R.id.accSet_SaveB:
			new saveSettingsTask().execute(receivedUserdata);
			AccountSettingsActivity.this.finish();
			break;		
		}
	}

	private boolean saveSettings(User user){
		HttpClient httpClient = new DefaultHttpClient();

		HttpPut put = new HttpPut(Main.hostName + "/user/" + Main.userId);
		put.setHeader("content-type", "application/json");		
		try
		{
			Gson g = new Gson();
			StringEntity entity = new StringEntity(g.toJson(user));
			put.setEntity(entity);
			HttpResponse resp = httpClient.execute(put);
			if(resp.getStatusLine().getStatusCode() == 200){
				return true;
			}
			else{
				return false;
			}
		}
		catch(Exception ex)
		{
			Log.e("Could not save user settings!","Error!", ex);
			return false;
		}
	}
	private class saveSettingsTask extends AsyncTask<User,Void,Boolean> {
		protected Boolean doInBackground(User... user) {
			return saveSettings(user[0]);
		}
		protected void onPostExecute(Boolean result) {
			if(result){
				Toast.makeText(AccountSettingsActivity.this, "Settings saved successfully", Toast.LENGTH_SHORT).show();
				AccountSettingsActivity.this.finish();
			}
			else{
				Toast.makeText(AccountSettingsActivity.this, "Error: Settings could not be saved", Toast.LENGTH_SHORT).show();
			}
		}			

	}

	private User getMyAccountSettings(){
		HttpClient httpClient = new DefaultHttpClient();
		String userAccountDir = Main.hostName +"/user/" + userId;  /////////////////////////////////////////////////////////////////////////////////////////////////
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
		ProgressDialog dialog;
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(AccountSettingsActivity.this, "Please wait...", "Loading User data");
			dialog.show();
		}	
		protected User doInBackground(Void... params) {
			return getMyAccountSettings();
		}
		protected void onPostExecute(User receivedUserdata) {

			firstname.setText(receivedUserdata.getFirstname());
			lastname.setText(receivedUserdata.getLastname());
			password.setText(receivedUserdata.getPassword());
			email.setText(receivedUserdata.getEmail());
			userName.setText(receivedUserdata.getUsername());
			refreshSpinnersData();
			dialog.dismiss();
		}			

	}
	private void refreshSpinnersData(){
		String[] shippingAddressesIdentifiers = null;
		String[] creditCardsIdentifiers = null;

		if(receivedUserdata.getShipping_addresses().length!=0){
			Address firstAddress = receivedUserdata.getShipping_addresses()[0];
			shippingAdd.setText(firstAddress.getContact_name() + "\n" + firstAddress.getStreet() +  "\n" + firstAddress.getCity() + 
					" " + firstAddress.getState() + " " + firstAddress.getZip_code() + "\n" + firstAddress.getCountry() + "\n" + 
					firstAddress.getTelephone());
			//Extraer los identificadores de cada shipping address para llenar el spinner:
			shippingAddressesIdentifiers = new String[receivedUserdata.getShipping_addresses().length];
			for(int i=0;i<shippingAddressesIdentifiers.length;i++){
				shippingAddressesIdentifiers[i] = receivedUserdata.getShipping_addresses()[i].getStreet();
			}
		}
		else{
			shippingAdd.setText("");
			shippingAddressesIdentifiers = new String[0];
		}
		if(receivedUserdata.getCredit_cards().length!=0){
			//Extraer los identificadores de cada credit card para llenar el spinner:
			creditCardsIdentifiers = new String[receivedUserdata.getCredit_cards().length];
			for(int i=0;i<creditCardsIdentifiers.length;i++){
				CreditCard tmpCrdCard = receivedUserdata.getCredit_cards()[i];
				creditCardsIdentifiers[i] = "xxxx-xxxx-xxxx-" + tmpCrdCard.getNumber().substring(12);
			}
		}
		else{
			creditCardsIdentifiers = new String[0];
		}
		ArrayAdapter<String> shippingAddressesAdapter = new ArrayAdapter<String>(AccountSettingsActivity.this,android.R.layout.simple_list_item_single_choice, shippingAddressesIdentifiers);
		shipAddrSpinner.setAdapter(shippingAddressesAdapter);
		ArrayAdapter<String> creditCardsAdapter = new ArrayAdapter<String>(AccountSettingsActivity.this,android.R.layout.simple_list_item_single_choice, creditCardsIdentifiers);
		cardsSpinner.setAdapter(creditCardsAdapter);			


	}
}
