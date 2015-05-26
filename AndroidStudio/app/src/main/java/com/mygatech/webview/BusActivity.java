package com.mygatech.webview;

import java.util.GregorianCalendar;

import com.mygatech.MainActivity;
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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.GeolocationPermissions;

public class BusActivity extends Activity {
	private WebView wv;
	private AdView adView;

	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Bus Screen").build());

//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.sting_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		wv = (WebView) findViewById(R.id.webView);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setGeolocationEnabled(true);
		wv.loadUrl("http://www.nextbus.com/webkit/#_home");
		wv.setWebChromeClient(new WebChromeClient() {
			public void onGeolocationPermissionsShowPrompt(String origin,
					GeolocationPermissions.Callback callback) {
				callback.invoke(origin, true, false);
			}

		});
		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
				.setBirthday(new GregorianCalendar(1992, 1, 1).getTime())
				.build();
		adView.loadAd(adRequest);
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
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}
}
