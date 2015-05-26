package com.mygatech.forfutureuse;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mygatech.MainActivity;
import com.mygatech.R;
import com.mygatech.R.anim;
import com.mygatech.R.id;
import com.mygatech.R.layout;
import com.mygatech.tsquare.TsquareArrays;
import com.mygatech.tsquare.TsquareMain;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class TsquareHandler extends Activity {
	static CookieManager manager;
	WebView wv;
	static HttpURLConnection connection,connection2;
	protected static int numClass;
	public static final String CLASS_LIST = "ClassLists";
	protected static ArrayList<Object> aList = new ArrayList<Object>();
	private SharedPreferences mainSp, classSp;
	private Editor classEditor;
	private Document doc;
	protected TsquareManager db = new TsquareManager(this);
	
	//TsquareHandler -> TsquareMain -> TsquareMenu -> TsquareSubMenu -> TsquareFinalMenu
	
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tsquare_main);
		setTitle("Loading for you");
//		mainSp = getSharedPreferences(MainActivity.USER_DETAILS, 0);

		wv = (WebView) findViewById(R.id.webView);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.setVisibility(View.INVISIBLE);

		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().startSync();
		manager = CookieManager.getInstance();
		manager.acceptCookie();
		manager.setAcceptCookie(true);
		manager.setCookie("https://login.gatech.edu/cas/login", "Set-Cookie");
		manager.setCookie("https://t-square.gatech.edu/portal/pda",
				"Set-Cookie");
		manager.setCookie(
				"https://t-square.gatech.edu/sakai-login-tool/container",
				"Set-Cookie");

		classSp = getSharedPreferences(TsquareHandler.CLASS_LIST, 0);
		classEditor = classSp.edit();
		boolean upToDate = classSp.getBoolean("upToDate", false);
	
//		if (!mainSp.getString("username", null).equals("forkjoseph") && !upToDate) {
//			startService(new Intent(getApplicationContext(),
//					BackgroundDetectionService.class));
//			finish();
//		} else 
			if (!mainSp.getString("username", null).equals(null)) {
			//admin test purpose text file input reader and save to db
			aList.clear();
			String TSQUARE = "tsquare.txt";
			String TSQUARE_SUB = "tsquaresub.txt";
			String dir = Environment.getExternalStorageDirectory() + "/MyGaTech/";
			FileInputStream fis;
			BufferedReader buffer;
			try {
				fis = new FileInputStream(dir + TSQUARE_SUB);
				buffer = new BufferedReader(new InputStreamReader(fis));
				String line = "";
				db.deleteAll();
	            while((line = buffer.readLine()) != null){
	            	aList.add(new TsquareArrays(line.split(";",-1)[0], line.split(";",-1)[1],""));
//	                String[] lineArray = line.split(";", -1);
//	                if(lineArray.length > 5){
//		                aList.add(new TsquareArrays(lineArray[0], lineArray[1],
//								lineArray[2], lineArray[3], lineArray[4],
//								lineArray[5]));
//	                }else if (lineArray.length > 0){
//	                	aList.add(new TsquareArrays(lineArray[0], lineArray[1]));
//	                }
//					db.addSubj(new TsquareArrays(lineArray[0], lineArray[1],
//							lineArray[2], lineArray[3], lineArray[4],
//							lineArray[5]));
	            }  
//				fis.close();
//				fis = new FileInputStream(dir + TSQUARE);
//				buffer = new BufferedReader(new InputStreamReader(fis));
//				while((line = buffer.readLine()) != null){
//	                String[] lineArray = line.split(";", -1);
//					db.addSubj(new TsquareArrays(lineArray[0], lineArray[1],
//							lineArray[2], lineArray[3], lineArray[4],
//							lineArray[5]));
//				}
				fis.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			/*int i = 0;
			for (String temp : db.getSubjList()){
				aList.add(temp);
			}
			numClass = i;*/
			startActivity(new Intent(getApplicationContext(),
					TsquareMain.class));
			
		} else {
			
			aList.clear();
			int i = 0;
			for (Entry<String, ?> map : classSp.getAll().entrySet()) {
				if (!(map.getValue() instanceof Boolean)
						&& !(map.getKey().equals("username"))
						&& !(map.getKey().equals("password"))) {
					aList.add(new TsquareArrays(map.getKey(), (String) map
							.getValue()));
				}
			}
			numClass = i;
			startActivity(new Intent(getApplicationContext(),
					TsquareMain.class));
		}

	}
	
	//Parsing using asyncTask & webview -> not recommended
	public class cookieHandler extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			try{
				//Logics for T-square login 
				URL urlForLogin = new URL("https://login.gatech.edu/cas/login");		
				String cookie = manager.getCookie("https://login.gatech.edu/cas/login");
				Log.e("check","Cookie1 has: "+cookie);
				connection = (HttpURLConnection) urlForLogin.openConnection();
				Log.e("check","Cookie1 outgoing: "+ connection.getRequestProperty("Cookie"));
			    connection.setRequestProperty("Cookie",cookie);
			    
			    //Request property header checker
	            for (String header : connection.getRequestProperties().keySet()) {
					if (header != null) {
						for (String value : connection.getRequestProperties()
								.get(header)) {
							Log.e("check", "Request Prop: " + header + " Value: " + value);
						}
					}else{
						Log.e("check", "Request Prop is null");
					}
				}
	            
	            //Response header fields
	            for (String header : connection.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : connection.getHeaderFields()
								.get(header)) {
							Log.e("check", "Header: " + header + " Value: " + value);
						}
					}else{
						Log.e("check", "Header is null");
					}
				}
	            
			    connection.connect();	
			    Log.e("connection",""+connection.getResponseCode());
			    Log.e("connection",""+connection.getResponseMessage());
			   
			    URL urlForHtml = new URL("https://t-square.gatech.edu/portal/pda?force.login=yes");
	            String cookie2 = manager.getCookie("https://t-square.gatech.edu/portal/pda?force.login=yes");
				Log.e("check","Cookie2 outgoing: "+ connection.getRequestProperty("Cookie"));
	            connection = (HttpURLConnection)urlForHtml.openConnection();
	            connection.setRequestProperty("Cookie", cookie2);
	            
	            Log.e("check", "Request properties:");
	            for (String header : connection.getRequestProperties().keySet()) {
					if (header != null) {
						for (String value : connection.getRequestProperties()
								.get(header)) {
							Log.e("check", "Request Prop2: " + header + " Value: " + value);
						}
					}else{
						Log.e("check", "Request Prop is null");
					}
				}
	            connection.connect();
	            
	            Log.e("check", "Header fields:");
	            for (String header : connection.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : connection.getHeaderFields()
								.get(header)) {
							Log.e("check", "Header2: " + header + " Value: " + value);
						}
					}else{
						Log.e("check", "Header is null");
					}
				}
				Log.e("check","Cookie3 outgoing: "+ connection.getRequestProperty("Cookie"));
				

				doc =  Jsoup.parse(connection.getInputStream(),null, "https://t-square.gatech.edu/portal/pda?force.login=yes");
				Elements links = doc.select("ul#pda-portlet-site-menu li a[href]");
	    		numClass = 0;
				for(Element link: links) {
					if(numClass != 0){
						TsquareArrays temp = new TsquareArrays(link.attr("title"), link.attr("href"));
						aList.add(temp);
						classEditor.putString(temp.getName(), temp.getLink());
					}
					numClass++;
				}
				numClass--;
				classEditor.putString("username", mainSp.getString("username", null));
				classEditor.putBoolean("upToDate", true);
				classEditor.commit();
				overridePendingTransition(R.anim.fade, R.anim.hold);
				startActivity(new Intent(getApplicationContext(), TsquareMain.class));
				
			}catch(Throwable e){
				e.getStackTrace();
				Log.e("check", "Not connected");
			}
			return null;
		}
	}
	
	public void update(){
		//update.setImageResource(R.drawable.updateinprogress);
		//update.setVisibility(View.VISIBLE);
		//Toast custom = new Toast(getApplicationContext());
		//custom.setText("List loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\nList loading\n Please wait!\n");
//		custom.setDuration(10000);
//		custom.setView(update);
//		custom.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//		custom.show();
		Toast.makeText(getApplicationContext(), "List loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\nList loading... Please wait! List loading... Please wait!\n",Toast.LENGTH_LONG).show();
		wv.loadUrl("https://login.gatech.edu/cas/login?service=https://t-square.gatech.edu/portal/pda");
		wv.setWebViewClient(new WebViewClient(){
			public void onPageFinished(WebView wv, String url){
        		if(url.equals("https://login.gatech.edu/cas/login?service=https://t-square.gatech.edu/portal/pda")
        			|| url.equals("https://login.gatech.edu/cas/login?service=https%3A%2F%2Ft-square.gatech.edu%2Fsakai-login-tool%2Fcontainer")
        			|| url.equals("https://login.gatech.edu/cas/login")){
        			
	    			Log.e("check","==========Login injection begins==========");
	    			final String user = mainSp.getString("username", null);       
	        	    final String pass = mainSp.getString("password", null); 
	    			Log.e("check", user + " is successfully logged in");
	    			wv.loadUrl("javascript:document.getElementById('username').value = '"+user+
	    					"';document.getElementById('password').value='"+pass+
	    					"';document.all('submit').click();");
        		}
        		
        		if (url.equals("https://t-square.gatech.edu/portal/pda?force.login=yes")){
        		   	new cookieHandler().execute();
        		}
        		
        		if ( url.startsWith("https://t-square.gatech.edu/portal/pda")&&
        			!url.equals("https://t-square.gatech.edu/portal/pda?force.login=yes")){
        			wv.loadUrl("https://t-square.gatech.edu/portal/pda?force.login=yes");
        		}
    	}
		});

		
		String cookie = "";
		while(cookie.equals("Set-Cookie") || cookie.equals("")){
			cookie = manager.getCookie("https://login.gatech.edu/cas/login");
		}
		
		Log.e("check","On start: "+cookie);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	public void onStart(){
		super.onStart();
	}
	
	public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	
	@Override
	public void onResume(){
    	super.onResume();
    	CookieSyncManager.getInstance().startSync();
    	finish();
    }
	
	
	@Override
    public void onPause(){
    	super.onPause();    	
    }
}
