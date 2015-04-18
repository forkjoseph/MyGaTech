package com.mygatech;


import com.mygatech.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class EmailActivity extends Activity {
	private WebView wv;
	private SharedPreferences sp;
	private AdView adView;
	
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.sting_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "GT-Mail Screen").build());
		
		sp = getSharedPreferences(MainActivity.USER_DETAILS, 0);
		setTitle("GT Email");
	    adView = (AdView)findViewById(R.id.adView);
	    AdRequest adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);

        wv= (WebView) findViewById(R.id.webView);
	    wv.getSettings().setJavaScriptEnabled(true);
	    wv.setVisibility(View.INVISIBLE);
	    wv.loadUrl("https://mail.gatech.edu/GTpreauth/index.php?bounced=true");
		wv.loadUrl("https://mail.gatech.edu/GTpreauth/index.php?to_login=true");
		wv.loadUrl("https://login.gatech.edu/cas/login?service=https://mail.gatech.edu/zimbra/m/");
		wv.setWebViewClient(new WebViewClient(){
	    	 @Override
	    	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        	     handler.proceed(); // Ignore SSL certificate errors
        	 }
        	public void onPageFinished(WebView wv, String url){
        		if(url.contains("login")){
        			final String user = sp.getString("username", null);       
        			final String pass = sp.getString("password", null); 
        			wv.loadUrl("javascript:document.getElementById('username').value = '"+user+
        					"';document.getElementById('password').value='"+pass+//"';");
        					"';document.all('submit').click();");
        		}
        		if(url.contains("zimbra/m/zmain")){ 
        			//animation disappear
        			//loading.setVisibility(View.INVISIBLE);
        			//frameAnimation.stop();
        		    wv.setVisibility(View.VISIBLE);

        		}
        	}
        });
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocused){
        super.onWindowFocusChanged(hasFocused);
        if(hasFocused){
//        	Log.e("check", "triggerd");
//    		loading = (ImageView)findViewById(R.id.loading);
//    		loading.setVisibility(View.VISIBLE);
//    		loading.setBackgroundResource(R.anim.animation);
//    		frameAnimation = (AnimationDrawable) loading.getBackground();
//        	frameAnimation.start();
        }
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onStart() { 
        super.onStart(); 
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
        CookieSyncManager.createInstance(this);
	}   
	    
	@Override
	public void onResume(){
    	super.onResume();
    	CookieSyncManager.getInstance().startSync();
    }

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}
	
	@Override
    public void onPause(){
    	super.onPause();
    	CookieSyncManager.getInstance().stopSync();
    }
}
