package com.gui.taptobuy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

public class MyTapActivity extends Activity implements View.OnClickListener {
	
	private Button AccountSet ;
	private Button SignOut;
	private Button SellAnItem;
	private Button MySelling;
	private Button BiddingItems;
	private Button MyHistory;	
	
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);			
		setContentView(R.layout.mytap);
		
		AccountSet = (Button) findViewById(R.id.myTap_AccountSet);
		SignOut = (Button) findViewById(R.id.myTap_SignOut);
		SellAnItem = (Button) findViewById(R.id.myTap_sellAnItem);
		MySelling = (Button) findViewById(R.id.myTap_MySellings); 
		BiddingItems = (Button) findViewById(R.id.myTap_BiddingItems);
		MyHistory = (Button) findViewById(R.id.myTap_MyHistory);
		
		AccountSet.setOnClickListener(this);
		SignOut.setOnClickListener(this);
		SellAnItem.setOnClickListener(this);
		MySelling.setOnClickListener(this);
		BiddingItems.setOnClickListener(this);
		MyHistory.setOnClickListener(this);
	}

	@Override
	public void onClick(View button) {
		
		switch (button.getId()){
		
		case R.id.myTap_AccountSet:
			// hacer un if para saber si el user es Admin or not
			
			startActivity(new Intent(this, AccountSettingsActivity.class));
			break;
			
		case R.id.myTap_SignOut:
			Main.signed = false;
			Intent home = new Intent(this, SignInActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);			
			
			break;
		
		case R.id.myTap_sellAnItem:
			startActivity(new Intent(this, SellAnItemActivity.class));
			break;
		
		
		case R.id.myTap_MySellings:
			startActivity(new Intent(this, MySellingActivity.class));
			break;
		
		
		case R.id.myTap_BiddingItems:
			startActivity(new Intent(this, MyBiddingActivity.class));
			break;
		
		
		case R.id.myTap_MyHistory:
			startActivity(new Intent(this, MyHistoryActivity.class));
			break;
		}
		
	}
}
