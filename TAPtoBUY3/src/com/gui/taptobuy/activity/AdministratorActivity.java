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
	private Button Done;
	private TextView reportTitle;
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
		Done = (Button)findViewById(R.id.adminClose);
		reportTitle = (TextView)findViewById(R.id.report_Title);

		
		RegUser1.setOnClickListener(this);
		RegUser2.setOnClickListener(this);
		Admin2.setOnClickListener(this);
		Admin1.setOnClickListener(this);
		viewUser.setOnClickListener(this);
		createAcc.setOnClickListener(this);
		loadSales.setOnClickListener(this);		
		Done.setOnClickListener(this);
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
				}
				
				break;
				
			case R.id.adminTotalSalesB:					
				String startDate = fromDate.getText().toString();
				String endDate = toDate.getText().toString();	
				
				if(!startDate.equals("") && !endDate.equals("")){ // no esta bregando la condicion
					
				//reportTitle.setText("Total sales between " +startDate+ " to " +endDate);
					
				final Dialog dialog = new Dialog(this);
				dialog.setContentView(R.layout.admin_report_dialog);
				dialog.setTitle("Products report");
				
				reportList = (ListView) dialog.findViewById(R.id.report_list);
				try{
					new getReportTask().execute(startDate,endDate);/////////////////////////
				}
				catch(Exception e){
					Toast.makeText(this, "Error make sure the date is written in the proper format", Toast.LENGTH_LONG).show();
				}
				finally{
					Button okBTN = (Button) dialog.findViewById(R.id.report_CloseB);			
					okBTN.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) 
						{	
							dialog.dismiss();
						}
					});    
					dialog.show();	
				}	
				}
				else
					Toast.makeText(this, "You must specify a date interval", Toast.LENGTH_LONG).show();
		
				break;
				
			case R.id.adminClose:				
				this.finish();
				break;
		}		
	}
	
	public static class MyViewReport{

		public TextView productRevenue;
		public TextView soldAmount;
		public TextView product;
		public ProductReport reportProd;		
	}
	
  	private ArrayList<ProductReport> getAdminReport(String startDate, String endDate){
		HttpClient httpClient = new DefaultHttpClient();
		String reportDir = Main.hostName +"/report/"+startDate+"/"+endDate+"/";
		System.out.println(reportDir);
		HttpGet get = new HttpGet(reportDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				JSONArray reportArray = (new JSONObject(jsonString)).getJSONArray("reports");
				reports = new ArrayList<ProductReport>();
				
				JSONObject reportElement = null;

				for(int i=0; i<reportArray.length();i++){
					reportElement = reportArray.getJSONObject(i);
					// 
					reports.add(new ProductReport(reportElement.getString("product"), reportElement.getString("soldAmount"),reportElement.getString("revenue")));
				}
			}
			else{
				Log.e("JSON","report json could not be downloaded :(.");
			}
		}
		catch(Exception ex)
		{
			Log.e("reports","Error!", ex);
		}
		return reports;
	}

	private class getReportTask extends AsyncTask<String,Void,ArrayList<ProductReport>> {
		protected ArrayList<ProductReport> doInBackground(String... dates) {
			return getAdminReport(dates[0],dates[1]);// dates
		}
		protected void onPostExecute(ArrayList<ProductReport> reports ) {
			//llenar con array de reports
			//reportList.setAdapter(new AdminReportListAdapter(AdministratorActivity.this,layoutInflater, reports));
		}			
	}
}
