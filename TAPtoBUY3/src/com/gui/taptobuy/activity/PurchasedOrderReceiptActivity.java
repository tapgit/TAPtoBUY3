package com.gui.taptobuy.activity;

import java.util.ArrayList;

import com.gui.taptobuy.Entities.MyHistoryProduct;
import com.gui.taptobuy.Entities.MyHistoryProductForAuction;
import com.gui.taptobuy.Entities.Order;
import com.gui.taptobuy.customadapter.PurchasedOrderReceiptListCustomAdapter;
import com.gui.taptobuy.customadapter.SoldOrderReceiptListCustomAdapter;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PurchasedOrderReceiptActivity extends Activity implements OnClickListener {
	
	private TextView orderID;
	private TextView shippAdd;
	private ListView boughtItems;
	private TextView totalPayment;
	private TextView paymentCard;
	private TextView purchasedDate;
	private Button okButton;
	private LayoutInflater layoutInflator;
	public static Order showingOrder;
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.order_receipt);	
		this.layoutInflator = LayoutInflater.from(this);
		boughtItems = (ListView)findViewById(R.id.receipt_itemList);
		okButton = (Button)findViewById(R.id.receipt_okB);
		orderID = (TextView)findViewById(R.id.receipt_orderID);
		shippAdd = (TextView)findViewById(R.id.receipt_ShippingAdress);
		totalPayment = (TextView)findViewById(R.id.receipt_totalPayment);
		paymentCard = (TextView)findViewById(R.id.receipt_paymentMethod);
		purchasedDate = (TextView)findViewById(R.id.receipt_Date);	
		
		okButton.setOnClickListener(this);
		if(showingOrder.getProducts().get(0) instanceof MyHistoryProductForAuction){
			orderID.setText("Order ID: auc-" + showingOrder.getId() + "");
		}
		else{
			orderID.setText("Order ID: inst-" + showingOrder.getId() + "");
		}
		ArrayList<MyHistoryProduct> products = showingOrder.getProducts();
		double totalPaymentNum = 0;
		for(MyHistoryProduct p: products){
			totalPaymentNum+=p.getPaidPrice();
		}
		totalPayment.setText("Total Payment: $" + totalPaymentNum + "");
		paymentCard.setText("Payment Method: " + showingOrder.getPaymentMethod() + "");
		purchasedDate.setText("Date: " + showingOrder.getDate() + "");

		
		boughtItems.setAdapter(new PurchasedOrderReceiptListCustomAdapter(PurchasedOrderReceiptActivity.this,PurchasedOrderReceiptActivity.this.layoutInflator, showingOrder.getProducts()));
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.receipt_okB){			
			PurchasedOrderReceiptActivity.this.finish();
		}		
	}
}
