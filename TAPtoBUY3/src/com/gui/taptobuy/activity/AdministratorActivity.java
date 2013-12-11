package com.gui.taptobuy.activity;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gui.taptobuy.Entities.ProductReport;
import com.gui.taptobuy.customadapter.AdminReportListAdapter;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

public class AdministratorActivity extends Activity implements OnClickListener {
	
	private CheckBox RegUser1;
	private CheckBox Admin1;
	private EditText toViewUserId;
	private Button viewUser;	
	private Button createAcc;
	private EditText fromDate;
	private EditText toDate;
	private Button loadSales;			
	private LayoutInflater layoutInflater;
	private CheckBox RegUser2;
	private CheckBox Admin2;
	ArrayList<ProductReport> reports; 
	private ListView reportList;
	
	private boolean isAdmin1 = false;
	private boolean isAdmin2 = false;
		
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.account_admin);
		
		this.layoutInflater = LayoutInflater.from(this);
		
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
		
		
		RegUser1.setOnClickListener(this);
		RegUser2.setOnClickListener(this);
		Admin2.setOnClickListener(this);
		Admin1.setOnClickListener(this);
		viewUser.setOnClickListener(this);
		createAcc.setOnClickListener(this);
		loadSales.setOnClickListener(this);		
	}
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId())
		{		
			case R.id.checkAdmiUser:
				RegUser1.setChecked(false);
				isAdmin1 = true;
				break;
			
			case R.id.checkRegUser:
				Admin1.setChecked(false);
				isAdmin1 = false;
				break;
				
			case R.id.checkAdminUser2:
				RegUser2.setChecked(false);
				isAdmin2 = true;
				break;
			
			case R.id.checkRegUser2:
				Admin2.setChecked(false);
				isAdmin2 = false;
				break;
			
			case R.id.adminViewB:
				//id del usuario que el admin va a modificar o ver
				String userToView = toViewUserId.getText().toString();
					
				if(!RegUser1.isChecked()&&!Admin1.isChecked()){
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
			case R.id.adminTotalSalesB:
				
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.admin_report_dialog);
				dialog.setTitle("Item's Bids");
				reportList = (ListView) dialog.findViewById(R.id.report_list);
			  //  new getreportTask().execute(date);
				Button okBTN = (Button) dialog.findViewById(R.id.report_CloseB);			
				okBTN.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) 
					{	
						dialog.dismiss();
					}
				});    
				dialog.show();	
								
				break;				
		}		
	}
	
	public static class MyViewReport{

		public TextView productRevenue;
		public TextView soldAmount;
		public TextView product;
		public ProductReport reportProd;		
	}
	
//	private ArrayList<Bid> getBidList(String productId){
//		HttpClient httpClient = new DefaultHttpClient();
//		String bidListDir = Main.hostName +"/bidlist/" + productId;
//		HttpGet get = new HttpGet(bidListDir);
//		get.setHeader("content-type", "application/json");
//		try
//		{
//			HttpResponse resp = httpClient.execute(get);
//			if(resp.getStatusLine().getStatusCode() == 200){
//				String jsonString = EntityUtils.toString(resp.getEntity());
//				JSONArray bidListArray = (new JSONObject(jsonString)).getJSONArray("bidlist");
//				bidList = new ArrayList<Bid>();
//
//				JSONObject bidListElement = null;
//
//				for(int i=0; i<bidListArray.length();i++){
//					bidListElement = bidListArray.getJSONObject(i);
//					bidList.add(new Bid(-1, bidListElement.getDouble("amount"), -1, bidListElement.getString("username")));
//				}
//
//			}
//			else{
//				Log.e("JSON","bidlist json could not be downloaded.");
//			}
//		}
//		catch(Exception ex)
//		{
//			Log.e("BidList","Error!", ex);
//		}
//		return bidList;
//	}
//
//	private class getreportTask extends AsyncTask<String,Void,ArrayList<Bid>> {
//		protected ArrayList<Bid> doInBackground(String... date) {
//			return getBidList(productId[0]);//get bidlist de bids puestos a este product
//		}
//		protected void onPostExecute(ArrayList<Bid> bidList ) {
//			//llenar con array de bid
//			reportList.setAdapter(new AdminReportListAdapter(this,layoutInflater, bidList));
//		}			
//	}
}
