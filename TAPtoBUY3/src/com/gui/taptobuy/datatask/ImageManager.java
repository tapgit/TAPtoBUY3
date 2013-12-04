package com.gui.taptobuy.datatask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class ImageManager {


	public static Bitmap downloadImage(String URL){        
		Bitmap bitmap = null;
		InputStream in = null;        
		try {
			in = OpenHttpConnection(URL);
			bitmap = BitmapFactory.decodeStream(in);
			in.close();
		} catch (IOException e1) {
			Log.d("Image download", e1.getLocalizedMessage());            
		}
		return bitmap;                
	}

	private static InputStream OpenHttpConnection(String urlString) throws IOException{
		InputStream in = null;
		int response = -1;

		URL url = new URL(urlString); 
		URLConnection conn = url.openConnection();

		if (!(conn instanceof HttpURLConnection))                     
			throw new IOException("Not an HTTP connection");        
		try{
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setAllowUserInteraction(false);
			httpConn.setInstanceFollowRedirects(true);
			httpConn.setRequestMethod("GET");
			httpConn.connect();
			response = httpConn.getResponseCode();                 
			if (response == HttpURLConnection.HTTP_OK) {
				in = httpConn.getInputStream();                                 
			}                     
		}
		catch (Exception ex)
		{
			Log.d("Networking", ex.getLocalizedMessage());
			throw new IOException("Error connecting");
		}
		return in;     
	}

	public static boolean uploadImage(String imgLocalDir){
		HttpClient client = new DefaultHttpClient();
		String imgUploadDir = Main.hostName + "/images"; 
		HttpPost post = new HttpPost(imgUploadDir);
		File file = new File(imgLocalDir);
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT);
		entity.addPart("picture", new FileBody(file));
		post.setEntity(entity);
		try {
			HttpResponse resp = client.execute(post);
			if(resp.getStatusLine().getStatusCode() == 200){//save temporary imageLink
				return true;
			}
			else{
				return false;
			}
		} catch (ClientProtocolException e) {
			Log.e("Image uploading Client Protocol Exception",e.getLocalizedMessage());
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			Log.e("Image uploading IOException", e.getLocalizedMessage());
			e.printStackTrace();
			return false;
		}

	}

}




