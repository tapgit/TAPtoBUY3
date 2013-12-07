package com.gui.taptobuy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gui.taptobuy.Entities.Order;
import com.gui.taptobuy.phase1.R;

public class SoldOrderReceiptActivity extends Activity implements OnClickListener {
	
	private TextView buyerUserName;
	private TextView orderID;
	private TextView sellerID;
	private TextView shippAdd;
	private ListView boughtItems;
	private TextView totalPayment;
	private TextView paymentCard;
	private TextView purchasedDate;
	private Button okButton;
	public static Order showingOrder;
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.seller_receipt);	
		
		boughtItems = (ListView)findViewById(R.id.sellerReceipt_itemList);
		okButton = (Button)findViewById(R.id.sellerReceipt_okB);
		orderID = (TextView)findViewById(R.id.sellerReceipt_orderID);
		totalPayment = (TextView)findViewById(R.id.sellerReceipt_totalPayment);
		paymentCard = (TextView)findViewById(R.id.sellerReceipt_paymentMethod);
		purchasedDate = (TextView)findViewById(R.id.sellerReceipt_Date);	
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.receipt_okB){			
			
			// Si viene del checkout //////////////////
//			Intent intent = new Intent (this, SearchActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);	
		}		
	}
}
