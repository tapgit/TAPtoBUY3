package com.gui.taptobuy.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.gui.taptobuy.phase1.R;

public class AdministratorActivity extends Activity implements OnClickListener {
	
	private CheckBox RegUser1;
	private CheckBox Admin1;
	private EditText toViewUserId;
	private Button viewUser;
	private Button modifyUser;
	private Button createAcc;
	private EditText fromDate;
	private EditText toDate;
	private Button loadSales;
	private EditText salesProd;
	private Button salesViewB;
	private EditText totalRev;
	private Button totalRevViewB;	
	
	private CheckBox RegUser2;
	private CheckBox Admin2;
	
	private boolean isAdmin1 = false;
	private boolean isAdmin2 = false;
	
	
	
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.account_admin);
		
		RegUser1 = (CheckBox) findViewById(R.id.checkRegUser);
		RegUser2 = (CheckBox) findViewById(R.id.checkRegUser2);
		Admin1 = (CheckBox) findViewById(R.id.checkAdmiUser);
		Admin2 = (CheckBox) findViewById(R.id.checkAdminUser2);
		toViewUserId = (EditText) findViewById(R.id.adminUserIDET);
		viewUser = (Button) findViewById(R.id.adminViewB);
		createAcc = (Button)findViewById(R.id.adminCreateAccB);
		fromDate = (EditText) findViewById(R.id.admin_dateFrom);
		toDate = (EditText) findViewById(R.id.admin_dateTo);
		loadSales = (Button) findViewById(R.id.adminTotalSalesB);
		salesProd = (EditText) findViewById(R.id.adminReportProductId);
		salesViewB = (Button) findViewById(R.id.adminViewBReport1);
		totalRev = (EditText) findViewById(R.id.adminReportTotalRevProdId);
		totalRevViewB = (Button) findViewById(R.id.adminViewBReport2);
		
		
		RegUser1.setOnClickListener(this);
		RegUser2.setOnClickListener(this);
		Admin2.setOnClickListener(this);
		Admin1.setOnClickListener(this);
		viewUser.setOnClickListener(this);
		createAcc.setOnClickListener(this);
		loadSales.setOnClickListener(this);
		salesViewB.setOnClickListener(this);
		totalRevViewB.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId())
		{		
			case R.id.checkAdmiUser:
				RegUser1.setChecked(false);
				break;
			
			case R.id.checkRegUser:
				Admin1.setChecked(false);
				break;
				
			case R.id.checkAdminUser2:
				RegUser2.setChecked(false);
				break;
			
			case R.id.checkRegUser2:
				Admin2.setChecked(false);
				break;
			
			case R.id.adminViewB:
				//id del usuario que el admin va a modificar o ver
				String userToView = toViewUserId.getText().toString();
					
				if(!RegUser2.isChecked()&&!Admin2.isChecked()){
					Toast.makeText(this, "You must specify if the user to create is Admin or Regular", Toast.LENGTH_LONG).show();
				}	
				else if(userToView == null){
					Toast.makeText(this, "You must provide an ID to view or modify a user", Toast.LENGTH_LONG).show();
				}
				//////
				else{					
					int userToViewID = Integer.parseInt(toViewUserId.getText().toString());					
					if (Admin1.isChecked()){
						isAdmin1 = true;							
					}
					intent = new Intent(this,AccountSettingsActivity.class);
					intent.putExtra("userId",userToViewID);
					intent.putExtra("isAdmin", isAdmin1);
					startActivity(intent);
				}
				break;
				
			case R.id.adminCreateAccB:
				
				if(!RegUser2.isChecked()&&!Admin2.isChecked()){
					Toast.makeText(this, "You must specify if the user to create is Admin or Regular", Toast.LENGTH_LONG).show();
				}				
				else{	
					if (Admin2.isChecked()){
						isAdmin2 = true;
						}	
					intent = new Intent(this,RegisterActivity.class);				
					intent.putExtra("isAdmin", isAdmin2);
					startActivity(intent);			
					break;
				}				
		}		
	}
}
