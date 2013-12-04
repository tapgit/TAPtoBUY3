package com.gui.taptobuy.datatask;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.gui.taptobuy.Entities.Rating;

public class RatingsManager {
	
	ArrayList<Rating> ratingsList;

//	private ArrayList<Rating> getRatingsList(String sellerId){
//		HttpClient httpClient = new DefaultHttpClient();
//		String ratingsListDir = Main.hostName +"/ratingslist/" + sellerId;////
//		HttpGet get = new HttpGet(ratingsListDir);
//		get.setHeader("content-type", "application/json");
//		try
//		{
//			HttpResponse resp = httpClient.execute(get);
//			if(resp.getStatusLine().getStatusCode() == 200){
//				String jsonString = EntityUtils.toString(resp.getEntity());
//				JSONArray ratingsListArray = (new JSONObject(jsonString)).getJSONArray("ratingslist");
//				ratingsList = new ArrayList<Rating>();
//
//				JSONObject ratingsListElement = null;
//
//				for(int i=0; i<ratingsListArray.length();i++){
//					ratingsListElement = ratingsListArray.getJSONObject(i);
//					ratingsList.add(new Rating(ratingsListElement.getString("username"),ratingsListElement.getInt("rate")));
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
//		return ratingsList;
//	}
	
}
