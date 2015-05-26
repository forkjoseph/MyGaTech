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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;

@SuppressLint("SetJavaScriptEnabled")
public class ReservActivity extends Activity {
	private WebView wv;
	private SharedPreferences sp;
	private AdView adView;
	private boolean logedIn;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sting_main);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Reserve Room Screen").build());

		sp = getSharedPreferences(MainActivity.USER_DETAILS, 0);
		wv = (WebView) findViewById(R.id.webView);

		wv.setVisibility(View.INVISIBLE);
		wv.loadUrl("https://www.gtevents.gatech.edu/VirtualEms/MobileLogin.aspx");
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed(); // Ignore SSL certificate errors
			}

			public void onPageFinished(WebView wv, String url) {
				if(url.equals("https://www.gtevents.gatech.edu/VirtualEms/MobileLogin.aspx")){
					final String user = sp.getString("username", null);
					final String pass = sp.getString("password", null);
					wv.loadUrl("javascript:document.getElementById('ctl00_pc_UserId').value = '"
							+ user
							+ "';document.getElementById('ctl00_pc_Password').value='"
							+ pass + // "';");
							"';document.all('ctl00_pc_btnLogin').click();");
				}else if(url.equals("https://www.gtevents.gatech.edu/VirtualEms/MobileBrowseReservations.aspx")
						&& !logedIn){
					wv.clearHistory();
					logedIn = true;
					wv.loadUrl("https://www.gtevents.gatech.edu/VirtualEms/MobileHome.aspx");
				}else if(url.equals("https://www.gtevents.gatech.edu/VirtualEms/MobileHome.aspx")){
					wv.setVisibility(View.VISIBLE);
				}else{
					for( int index = 0; index < wv.copyBackForwardList().getSize(); index++ ) {
					    WebHistoryItem item = wv.copyBackForwardList().getItemAtIndex(index);
					    Log.i("LIST: " , "WebView history: " + item.getUrl() );
					}
				}
			}
		});

		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU){
			return true; // do nth!!
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
		logedIn = false;
		CookieSyncManager.createInstance(this);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setSavePassword(false);
	}

	@Override
	public void onResume() {
		super.onResume();
		logedIn = false;
		CookieSyncManager.getInstance().startSync();
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CookieSyncManager.getInstance().stopSync();
	}
}
