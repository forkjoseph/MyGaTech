package com.mygatech;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class MainService extends Service {
	protected static CookieManager manager;
	protected static SharedPreferences mainSp;
	private int mInterval = 600000; // Every hour
	protected static final String STING_ADD = "StingAddress";


//	private int mInterval = 3000; // test purpose
	private Handler mHandler = new Handler();

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {	
		StrictMode.ThreadPolicy policy = 
		        new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		CookieSyncManager.createInstance(getApplicationContext());
	    CookieSyncManager.getInstance().startSync();
	    manager = CookieManager.getInstance();
	   	manager.acceptCookie();
	   	manager.setAcceptCookie(true);
	    mHandler.post(mStatusChecker);
	    
		return START_REDELIVER_INTENT;
	}
	
	Runnable mStatusChecker = new Runnable() {
	    @Override 
	    public void run() {
	    	new stingCookieHandler().execute();
	    	mHandler.postDelayed(mStatusChecker, mInterval);
	    }
	};
	
	private class stingCookieHandler extends AsyncTask<Void, Void, String[]>{
		private String parseJsession(String mCookie){
			String mReturn = "";
			if(mCookie != null){
				String[] split = mCookie.split(";", -1);
				for(String temp : split){
					if(temp.toLowerCase(Locale.US).contains("jsessionid")){
						String[] arrayTemp = temp.split("=",2);
						for(String js : arrayTemp){
							if(js.toLowerCase(Locale.US).contains("jsessionid")){
								mReturn = ";"+js.toLowerCase(Locale.US);
							}else{
								mReturn+="="+js;
							}
						}
					}
				}
			}
			return mReturn;
		}
		
		@Override
		protected void onPostExecute(String[] params){
			try {
				for(String p : params) {
					if (p == null)
						throw new Exception("Param is null");
				}
				manager.setCookie("https://login.gatech.edu/cas/login", params[0]);
				manager.setCookie(params[1], params[2]);
				SharedPreferences stingSp = getSharedPreferences(MainService.STING_ADD, 0);
				Editor stingEditor = stingSp.edit();
				stingEditor.putString("Sting_URL",params[1]);
				stingEditor.commit();
	//			Log.e("check","Sting address:"+params[1]);
				Log.e("check","A:::::"+manager.getCookie("http://ride.stingerette.com/student_ui/"));
				Log.e("check","B:::::"+manager.getCookie("https://login.gatech.edu/cas/login"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			String[] back = new String[3];
			try{
//		    	Log.e("check", "checked:)");
				String login = "https://login.gatech.edu/cas/login?service=http://ride.stingerette.com/student_ui/";
				URL urlForLogin = new URL(login);		
				HttpsURLConnection connection = (HttpsURLConnection) urlForLogin.openConnection();
				connection.setInstanceFollowRedirects(false);
			    connection.setRequestProperty("Cookie","utmccn=(referral)");
			    connection.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
		        connection.setRequestProperty("Accept-Language", "en-US");
		        connection.setRequestProperty("User-Agent", "Mozilla");
		        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		        connection.setRequestProperty("Host", "login.gatech.edu");
		        connection.setRequestProperty("DNT", "1");
		        connection.setRequestProperty("Connection","Keep-Alive");
		        String cookie = connection.getHeaderField("Set-Cookie"); // Real cookie usage
				String js = parseJsession(cookie); // URL usage
				if (cookie != null)
					cookie = (cookie.split(";",2)[0]);
				else
					throw new Exception("Not connected");

				String strForHtml = login + js + "?service=http://ride.stingerette.com/student_ui/";
		        URL urlForHtml = new URL(strForHtml);
		        connection = (HttpsURLConnection)urlForHtml.openConnection();	    
				connection.setInstanceFollowRedirects(false);
		        connection.setRequestMethod("POST");
			    connection.setRequestProperty("Cookie",cookie+";utmccn=(referral)");
			    connection.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
		        connection.setRequestProperty("Accept-Language", "en-US");
		        connection.setRequestProperty("User-Agent", "Mozilla");
		        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		        connection.setRequestProperty("Host", "login.gatech.edu");
		        connection.setRequestProperty("DNT", "1");
		        connection.setRequestProperty("Connection","Keep-Alive");
		        connection.setRequestProperty("Referer", "https://login.gatech.edu/cas/login?service=http://ride.stingerette.com/student_ui/");
		        connection.setRequestProperty("Cache-Control", "no-cache");
		        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				mainSp = getSharedPreferences(MainActivity.USER_DETAILS, 0);
				final String user = mainSp.getString("username", null);       
			    final String pass = mainSp.getString("password", null); 
		        String urlParameters = "username="+user+"&password="+pass+"&lt=e1s1&_eventId=submit&submit=LOGIN";
		        connection.setDoOutput(true);
		        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		        wr.writeBytes(urlParameters);
		        wr.flush();
		
		        String stingAdd = "";
		        for (String header : connection.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : connection.getHeaderFields()
								.get(header)) {
							if(header.contains("Location")){
								stingAdd = value;
							}
						}
					}
				}
		        
		        URL stingURL = new URL(stingAdd);
		        HttpURLConnection conn = (HttpURLConnection)stingURL.openConnection();	    
		        conn.setRequestMethod("GET");
		        conn.setRequestProperty("Cookie",cookie+";utmccn=(referral)");
		        conn.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
		        conn.setRequestProperty("Accept-Language", "en-US");
		        conn.setRequestProperty("User-Agent", "Mozilla");
		        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
		        conn.setRequestProperty("Host", "ride.stingerette.com");
		        conn.setRequestProperty("DNT", "1");
		        conn.setRequestProperty("Connection","Keep-Alive");
		        conn.setRequestProperty("Cache-Control", "no-cache");
		        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

//		        Log.e("check",""+conn.getResponseCode());
//			    Log.e("check",""+conn.getResponseMessage());
			    String sessionId = "";
		        for (String header : conn.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : conn.getHeaderFields()
								.get(header)) {
							if(value.contains("sessionid="))
								sessionId = value.split(";", 2)[0];
						}
					}
				}
		        
		        if(conn.getResponseCode()==200)
		        	Log.e("check", sessionId);
				back[0] = cookie;
				back[1] = stingAdd;
				back[2] = sessionId;

			}catch(IOException e){
				e.getStackTrace();
				Log.e("check", "Not connected");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return back;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
