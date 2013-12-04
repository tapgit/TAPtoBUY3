package com.gui.taptobuy.activity;


import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class SignInActivity extends Activity implements View.OnClickListener {

	private Button cartB;
	private Button categoriesB;
	private Button signInB;
	private Button signOutB;
	private Button registerB;
	private Button bSearch;
	private TextView signInText;
	private ImageView signOutPic;
	private TextView registerText;
	//public static boolean signed = false;
	private Dialog dialog; 
	private EditText searchTextET;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_home);

		searchTextET = (EditText) findViewById(R.id.searchAtLogin);
		cartB = (Button) findViewById(R.id.bCart);
		categoriesB = (Button) findViewById(R.id.bCategories);
		signInB = (Button) findViewById(R.id.bSign_in);
		registerB = (Button) findViewById(R.id.bRegister); 
		bSearch = (Button) findViewById(R.id.bSearch);
		signOutB = (Button) findViewById(R.id.bSign_Out);
		signInText = (TextView)findViewById(R.id.textSign_in);
		signOutPic = (ImageView)findViewById(R.id.signOutPic);
		registerText = (TextView)findViewById(R.id.textRegister);

		bSearch.setOnClickListener(this);
		cartB.setOnClickListener(this);
		categoriesB.setOnClickListener(this);
		signInB.setOnClickListener(this);
		registerB.setOnClickListener(this);
		signOutB.setOnClickListener(this);	
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(Main.signed){			
			signInB.setVisibility(View.GONE);
			signInText.setVisibility(View.GONE);
			registerB.setVisibility(View.GONE);
			registerText.setVisibility(View.GONE);
			signOutB.setVisibility(View.VISIBLE);
			signOutPic.setVisibility(View.VISIBLE);		
		}		
	}

	@Override
	public void onClick(View v) { 

		dialog = new Dialog(SignInActivity.this);

		dialog.setContentView(R.layout.login_dialog);
		dialog.setTitle("Sign in");

		final EditText usernameET = (EditText) dialog.findViewById(R.id.etNameToLogin);
		final EditText passwordET = (EditText) dialog.findViewById(R.id.etPasswordToLogin);        
		Button btnSignIn = (Button) dialog.findViewById(R.id.bSignIn);

		switch( v.getId() ) {

		case R.id.bCart:
			if(!Main.signed){

				btnSignIn.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) 
					{			                
						String username = usernameET.getText().toString();
						String password = passwordET.getText().toString();
						if(username.equals("") || password.equals("")){
							Toast.makeText(SignInActivity.this, "Error, you must provide userID & password", Toast.LENGTH_SHORT).show();			
						}	
						new SignInTaskFromCartBtn().execute(username,password);         				                
					}
				});    
				dialog.show();
			}
			else{
				startActivity(new Intent(this, CartActivity.class));
			}
			break;

		case R.id.bCategories:			

			startActivity(new Intent(this, CategoryActivity.class));   		
			break;

		case R.id.bSign_in:			       
			Button dlgBtnSignIn = (Button) dialog.findViewById(R.id.bSignIn);	
			dlgBtnSignIn.setOnClickListener(new View.OnClickListener() 
			{	
				public void onClick(View v) 
				{	

					String username = usernameET.getText().toString();
					String password = passwordET.getText().toString();
					if(username.equals("") || password.equals("")){
						Toast.makeText(SignInActivity.this, "Error, you must provide userID & password", Toast.LENGTH_SHORT).show();			
					}	
					new SignInTaskFromSignInBtn().execute(username,password);
				}
			});    
			dialog.show();
			break;


		case R.id.bRegister:			
			startActivity(new Intent(this, RegisterActivity.class));
			break;

		case R.id.bSearch:    
			Intent intent = new Intent(this, SearchActivity.class);
			intent.putExtra("previousActivity", "SignInActivity");
			intent.putExtra("searchString", searchTextET.getText().toString());
			startActivity(intent);			
			break;

		case R.id.bSign_Out:			
			signInEnabler();
			break;
		}
	}

	private void signInDisabler()
	{
		Main.signed = true;
		signInB.setVisibility(View.GONE);
		signInText.setVisibility(View.GONE);
		registerB.setVisibility(View.GONE);
		registerText.setVisibility(View.GONE);
		signOutB.setVisibility(View.VISIBLE);
		signOutPic.setVisibility(View.VISIBLE);
	}
	private void signInEnabler()
	{
		Main.signed = false;
		signInB.setVisibility(View.VISIBLE);
		signInText.setVisibility(View.VISIBLE);
		registerB.setVisibility(View.VISIBLE);
		registerText.setVisibility(View.VISIBLE);
		signOutB.setVisibility(View.GONE);
		signOutPic.setVisibility(View.GONE);
	} 

	private boolean signIn(String username, String password){
		boolean correct = false;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(Main.hostName+ "/login");
		post.setHeader("content-type", "application/json");
		try
		{
			JSONObject userData = new JSONObject();

			userData.put("username", username);
			userData.put("password", password);

			StringEntity entity = new StringEntity(userData.toString());
			post.setEntity(entity);

			HttpResponse resp = httpClient.execute(post);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONObject json = new JSONObject(jsonString);
				if(json.getBoolean("admin")){
					Main.admin  = true;
				}
				else{
					Main.admin = false;
				}
				Main.userId = json.getInt("id");
				correct = true;
			}
			else{
				correct = false;
			}
		}
		catch(Exception ex)
		{
			Log.e("Password check","Error!", ex);
		}
		return correct;
	}
	private class SignInTaskFromSignInBtn extends AsyncTask<String,Integer,Boolean> {

		protected Boolean doInBackground(String... params) {
			return signIn(params[0], params[1]);
		}

		protected void onPostExecute(Boolean correct) {

			if (correct)
			{
				Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
				signInDisabler();
				dialog.dismiss();   
				if(Main.admin){
					SignInActivity.this.startActivity(new Intent(SignInActivity.this, AdministratorActivity.class));
				}
				else{
					Intent intent = new Intent(SignInActivity.this, SearchActivity.class);
					intent.putExtra("previousActivity", "SignInActivity");
					intent.putExtra("searchString", searchTextET.getText().toString());
					SignInActivity.this.startActivity(intent);			
				}
			}
			else{
				Toast.makeText(SignInActivity.this, "Incorrect Password or User", Toast.LENGTH_SHORT).show();
				signInEnabler();
			}
		}
	}
	private class SignInTaskFromCartBtn extends AsyncTask<String,Integer,Boolean> {

		protected Boolean doInBackground(String... params) {
			return signIn(params[0], params[1]);
		}

		protected void onPostExecute(Boolean correct) {

			if (correct)
			{
				Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
				signInDisabler();
				dialog.dismiss(); 
				
				if(Main.admin){
					SignInActivity.this.startActivity(new Intent(SignInActivity.this, AdministratorActivity.class));
				}
				else{
					Intent intent = new Intent(SignInActivity.this, CartActivity.class);
					//intent.putExtra("previousActivity", "SignInActivity");
					SignInActivity.this.startActivity(intent);			
				}
			}
			else{
				Toast.makeText(SignInActivity.this, "Incorrect Password or User", Toast.LENGTH_SHORT).show();
				signInEnabler();
			}
		}
	}
}
