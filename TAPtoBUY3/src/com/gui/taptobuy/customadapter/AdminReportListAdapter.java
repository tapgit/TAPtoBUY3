package com.gui.taptobuy.customadapter;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gui.taptobuy.Entities.Product;
import com.gui.taptobuy.Entities.ProductForAuctionInfo;
import com.gui.taptobuy.Entities.ProductReport;
import com.gui.taptobuy.activity.AdministratorActivity;
import com.gui.taptobuy.activity.AdministratorActivity.MyViewReport;
import com.gui.taptobuy.activity.BidProductInfoActivity;
import com.gui.taptobuy.activity.MyBiddingActivity.MyViewAuctionItem;

import com.gui.taptobuy.datatask.ImageManager;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

public class AdminReportListAdapter extends BaseAdapter {
	
	private LayoutInflater layoutInflater;
	private ArrayList<ProductReport> reportInfo;	


	public AdminReportListAdapter (AdministratorActivity a, LayoutInflater l, ArrayList<ProductReport> reportInfo)
	{			
		this.layoutInflater = l;
		this.reportInfo = reportInfo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.reportInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View itemRow, ViewGroup parent) {
		MyViewReport itemHolder;
		ProductReport report = reportInfo.get(position);

		itemRow = layoutInflater.inflate(R.layout.admin_report_row, parent, false); 
		itemHolder = new MyViewReport();
		itemHolder.product =  (TextView) itemRow.findViewById(R.id.report_ProdName);
		itemHolder.soldAmount = (TextView) itemRow.findViewById(R.id.report_soldQTY);
		itemHolder.productRevenue = (TextView) itemRow.findViewById(R.id.report_revenue);			
		itemRow.setTag(itemHolder);
		
		itemHolder.productRevenue.setText(((ProductReport) report).getProdRevenue()+"");
		itemHolder.product.setText(((ProductReport) report).getProduct());
		itemHolder.soldAmount.setText(((ProductReport) report).getSoldAmount());
		itemHolder.reportProd = report; 	
		return itemRow;
	}	
}
