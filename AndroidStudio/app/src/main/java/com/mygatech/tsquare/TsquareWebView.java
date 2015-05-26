package com.mygatech.tsquare;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mygatech.LoginChecker;
import com.mygatech.LoginTask;
import com.mygatech.MainActivity;
import com.mygatech.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class TsquareWebView extends Activity {
	@SuppressWarnings("unused")
	private String subjectName, boardName, contentLink;
	private CookieManager manager;
	private WebView wv;
	private MenuItem menuItem;

	public boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private class ToastRun implements Runnable{
		String mText;
	    public ToastRun(String text) {
	        mText = text;
	    }
	    
	    @Override
	    public void run(){
	         Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_SHORT).show();
	    }
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.tsquare_main);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(
				MainActivity.TRACKING);
		tracker.send(MapBuilder
				.createAppView()
				.set(Fields.SCREEN_NAME,
						"T-Square Final " + actionBar.getTitle()).build());
	
		
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		subjectName = getIntent().getExtras().getString("subjectName");
		boardName = getIntent().getExtras().getString("boardName");
		contentLink = getIntent().getExtras().getString("link");
		
		setTitle(boardName);
		
		if (isNetworkConnected()) {
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().startSync();
			manager = CookieManager.getInstance();
			manager.acceptCookie();
			manager.setAcceptCookie(true);
			manager.removeAllCookie();
			SharedPreferences menuSp = getSharedPreferences(TsquareMain.MENU_LIST, 0);
			manager.setCookie(contentLink, menuSp.getString("TsquareJS", null));
			manager.setCookie(contentLink, menuSp.getString("Bigip", null));
			CookieSyncManager.getInstance().sync();
			
			wv = (WebView) findViewById(R.id.webView);
			wv.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					if (menuItem != null) {
						menuItem.collapseActionView();
						menuItem.setActionView(null);
						new ToastRun("Done :)").run();
					}
					
				}
			});
			wv.setPadding(5, 0, 5, 0);
			try {
				Log.d("cookie is ", manager.getCookie(contentLink));
				if (new LoginCheck(this, new URL(contentLink), 
						manager.getCookie(contentLink)).execute().get() == 0)
					wv.loadUrl(contentLink);
				else{
					wv.setVisibility(View.INVISIBLE);
					new TestTask(this).execute();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else if (!((WifiManager) getSystemService(Context.WIFI_SERVICE))
				.isWifiEnabled()) {
			new AlertDialog.Builder(this)
					.setTitle("Network status alert")
					.setMessage(
							"Network is not connected.\nWould you like to enable the WI-FI?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
									WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
									wifiManager.setWifiEnabled(true);
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
		} else { // WIFI is on and network is not connected
			new AlertDialog.Builder(this)
					.setTitle("Network status alert")
					.setMessage(
							"Network is not connected with WI-FI.\nPlease check your network.")
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).show();
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
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_load:
			menuItem = item;
			menuItem.setActionView(R.layout.progressbar);
			menuItem.expandActionView();
			wv.reload();
			Toast.makeText(this, "Refreshing :)", Toast.LENGTH_LONG).show();
			break;
		case android.R.id.home:
			onBackPressed();
//			NavUtils.navigateUpFromSameTask(this);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tsquare_menu, menu);
		return true;
	}
	
	private class LoginCheck extends LoginChecker{
		public LoginCheck(Context context, URL privateLink, String cookie) {
			super(context, privateLink, cookie);
		}

		@Override
		protected void onPostExecute(Integer result) {
			
		}
	}

	private class TestTask extends LoginTask{
		
		public TestTask(Context context) {
			super(context);
		}

		protected void onPostExecute(String result) {
			CookieSyncManager.createInstance(getApplicationContext());
		    CookieSyncManager.getInstance().resetSync();
		    CookieSyncManager.getInstance().startSync();
		    CookieManager manager = CookieManager.getInstance();
		   	manager.acceptCookie();
		   	manager.setAcceptCookie(true);
			manager.setCookie("https://login.gatech.edu/cas/login", "Set-Cookie");
			manager.setCookie("https://t-square.gatech.edu/portal/pda", "Set-Cookie");
			manager.setCookie("https://t-square.gatech.edu/sakai-login-tool/container", "Set-Cookie");
			wv.clearHistory();
			manager.removeAllCookie();
			manager.setCookie(contentLink, getSharedPreferences(TsquareMain.MENU_LIST, 0).getString("TsquareJS", null));
			manager.setCookie(contentLink, getSharedPreferences(TsquareMain.MENU_LIST, 0).getString("Bigip", null));
			wv.loadUrl(contentLink);
			wv.setVisibility(View.VISIBLE);
			if(menuItem != null) {
				menuItem.collapseActionView();
				menuItem.setActionView(null);
			}
		}
	};
}
