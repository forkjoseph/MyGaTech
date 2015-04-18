package com.mygatech;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class LoginChecker extends AsyncTask<String, Void, Integer> {
	@SuppressWarnings("unused")
	private Context context;
	private URL privateLink;
	private String cookie;
	
	public LoginChecker(Context context, URL privateLink, String cookie){
		this.context = context;
		this.privateLink = privateLink;
		this.cookie = cookie;
	}
	
	@Override
	protected Integer doInBackground(String... params) {
		HttpsURLConnection connection;
		try {
			Log.e("check Link", cookie + " " +  privateLink.toString());
			connection = (HttpsURLConnection) privateLink.openConnection();
			connection.setInstanceFollowRedirects(false);
		    connection.setRequestProperty("Cookie","utmccn=(referral)");
		    connection.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
	        connection.setRequestProperty("Accept-Language", "en-US");
	        connection.setRequestProperty("User-Agent", "Mozilla");
	        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
	        connection.setRequestProperty("Host", "login.gatech.edu");
	        connection.setRequestProperty("DNT", "1");
	        connection.setRequestProperty("Connection","Keep-Alive");
	        connection.setRequestProperty("Cookie", cookie);
	        Log.e("check", "From the login checker: " + connection.getResponseCode() + " " + connection.getResponseMessage());

	        for (String header : connection.getHeaderFields().keySet()) {
				if (header != null && header.equals("Location")){
					String value = connection.getHeaderFields().get(header).get(0);
					Log.e("value", value);
					
				}
			}
	        
	        if(connection.getContentLength() < 2300 && connection.getContentLength() > 0){
	        	Log.d("check", "Login required");
	        	
	        	return -1;
	        }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return 0;
	}
	
	protected abstract void onPostExecute(Integer result);

}
