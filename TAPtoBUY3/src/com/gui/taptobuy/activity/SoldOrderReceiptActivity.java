package com.gui.taptobuy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gui.taptobuy.Entities.MyHistoryProductForAuction;
import com.gui.taptobuy.Entities.Order;
import com.gui.taptobuy.customadapter.SoldOrderReceiptListCustomAdapter;
import com.gui.taptobuy.phase1.R;

public class SoldOrderReceiptActivity extends Activity implements OnClickListener {
	
	private TextView buyerUserName;
	private TextView orderID;
	private TextView sellerID;
	private TextView shippAdd;
	private ListView soldItems;
	private TextView totalPayment;
	private TextView paymentCard;
	private TextView purchasedDate;
	private Button okButton;
	private LayoutInflater layoutInflator;
	public static Order showingOrder;
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.seller_receipt);	
		
		this.layoutInflator = LayoutInflater.from(this);
		soldItems = (ListView)findViewById(R.id.sellerReceipt_itemList);
		okButton = (Button)findViewById(R.id.sellerReceipt_okB);
		orderID = (TextView)findViewById(R.id.sellerReceipt_orderID);
		totalPayment = (TextView)findViewById(R.id.sellerReceipt_totalPayment);
		paymentCard = (TextView)findViewById(R.id.sellerReceipt_paypalEmail);
		purchasedDate = (TextView)findViewById(R.id.sellerReceipt_Date);	
		
		if(showingOrder.getProducts().get(0) instanceof MyHistoryProductForAuction){
			orderID.setText("Order ID: auc-" + showingOrder.getId() + "");
		}
		else{
			orderID.setText("Order ID: inst-" + showingOrder.getId() + "");
		}
		totalPayment.setText("Total Payment: $" + showingOrder.getProducts().get(0).getPaidPrice() + "");
		paymentCard.setText(showingOrder.getPaymentMethod() + "");
		purchasedDate.setText("Date: " + showingOrder.getDate() + "");
		
		okButton.setOnClickListener(this);
		
		soldItems.setAdapter(new SoldOrderReceiptListCustomAdapter(SoldOrderReceiptActivity.this,SoldOrderReceiptActivity.this.layoutInflator, showingOrder.getProducts()));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.sellerReceipt_okB){			
			SoldOrderReceiptActivity.this.finish();
		}		
	}
}
