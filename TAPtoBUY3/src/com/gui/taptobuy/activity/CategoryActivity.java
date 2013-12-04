package com.gui.taptobuy.activity;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;



import com.gui.taptobuy.Entities.Category;
import com.gui.taptobuy.customadapter.CategoriesCustomListAdapter;
import com.gui.taptobuy.datatask.Main;
import com.gui.taptobuy.phase1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



public class CategoryActivity extends Activity implements OnItemClickListener {
	
	private LayoutInflater layoutInflator;
	private ListView categoryList;
	private ArrayList<Category> showingCategories;
	private String currentParentCategoryName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emptylist);	
		categoryList = (ListView)findViewById(R.id.ViewList);
		this.layoutInflator = LayoutInflater.from(this);	
		categoryList.setOnItemClickListener(this);
		new getSubCategoriesTask().execute("0"); 
	}
		
	
	private ArrayList<Category> getSubCategories(String clickedCategoryId){//clickedCategoryId =>parentCat Id
		HttpClient httpClient = new DefaultHttpClient();
		String categoryDir = Main.hostName + "/subcategoriesof/" + clickedCategoryId;
//		if(currentParentCategoryName == null && clickedCategory.equals("All")){
//			categoryDir+="All";
//		}
//		else{
//			categoryDir+=currentParentCategoryName + "/" + clickedCategory;
//		}
		//currentParentCategoryName = new String(clickedCategoryId);
		HttpGet get = new HttpGet(categoryDir);
		get.setHeader("content-type", "application/json");
		try
		{
			HttpResponse resp = httpClient.execute(get);
			if(resp.getStatusLine().getStatusCode() == 200){
				String jsonString = EntityUtils.toString(resp.getEntity());
				
				JSONArray subCategoriesArray = (new JSONObject(jsonString)).getJSONArray("subcategories");
				
				JSONObject jsonCat = null;
				showingCategories = new ArrayList<Category>();
				
				for(int i=0; i<subCategoriesArray.length();i++){
					jsonCat = subCategoriesArray.getJSONObject(i);
					showingCategories.add(new Category(jsonCat.getString("name"), jsonCat.getInt("catId"), jsonCat.getBoolean("hasSubCategories")));
				}
			}
			else{
				Log.e("JSON","Categories json could not be downloaded.");
			}
		}
		catch(Exception ex)
		{
			Log.e("Categories","Error!", ex);
		}
		return showingCategories;
	}
	
	private class getSubCategoriesTask extends AsyncTask<String,Void,ArrayList<Category>> {

		protected ArrayList<Category> doInBackground(String... params) {
			return getSubCategories(params[0]);//download subcategories of clickedCategory
		}

		protected void onPostExecute(ArrayList<Category> showingCategories ) {
			categoryList.setAdapter(new CategoriesCustomListAdapter(CategoryActivity.this,CategoryActivity.this.layoutInflator, showingCategories));
		}
	}
	
	public static class MyViewCategory {
		public Category category;
		public TextView categoryName;
		public ImageView arrow;		
	}

	@Override
	public void onItemClick(AdapterView<?> categories, View v, int arg2, long arg3) 
	{
		MyViewCategory categoriesHolder = (MyViewCategory) v.getTag();
		if(categoriesHolder.category.hasSubCategories())		
		new getSubCategoriesTask().execute(categoriesHolder.category.getCatId() + "");	
		else{
			Intent search = new Intent(this, SearchActivity.class);
			search.putExtra("catId", categoriesHolder.category.getCatId());
			search.putExtra("previousActivity", "CategoryActivity");
			startActivity(search);
		}
		
	}
}
