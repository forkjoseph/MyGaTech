package com.mygatech;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mygatech.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

@SuppressLint("SetJavaScriptEnabled")
public class StingActivity extends Activity {
	private WebView wv;
	@SuppressWarnings("unused")
	private SharedPreferences sp, serviceSp;
	private AdView adView;
	private CookieManager manager;
	private ImageView loading;

	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sting_main);
		loading = (ImageView) findViewById(R.id.loading);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Stingerette Screen").build());

		loading.setBackgroundResource(R.anim.ani_spinner);
		final AnimationDrawable frameAnimation = (AnimationDrawable) loading
				.getBackground();
		frameAnimation.start();
		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().setBirthday(
				new GregorianCalendar(1992, 1, 1).getTime()).build();
		adView.loadAd(adRequest);
		
		if (!corretTime()) {
			new AlertDialog.Builder(this)
			.setTitle("Stingerette service hours")
			.setMessage(
					"Stingerette operates only between 6:00 PM and 6:50 AM.")
			.setPositiveButton("Ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							finish();
						}
					}).show();
		}else{

		// -------------- login part
//		if (!MainService.manager.hasCookies())
//			new stingCookieHandler().execute();
//		else {
			sp = getSharedPreferences(MainActivity.USER_DETAILS, 0);
//			serviceSp = getSharedPreferences(MainService.STING_ADD, 0);
//			String url = serviceSp.getString("Sting_URL", null);
			String url = "http://ride.stingerette.com/student_ui/";
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().startSync();
			manager = CookieManager.getInstance();
			manager.acceptCookie();
			manager.setAcceptCookie(true);
//			manager.setCookie("https://login.gatech.edu/cas/login",
//					MainService.manager
//							.getCookie("htstps://login.gatech.edu/cas/login"));
//			manager.setCookie(url, MainService.manager.getCookie(url));
			wv = (WebView) findViewById(R.id.webView);
			wv.getSettings().setJavaScriptEnabled(true);
			wv.loadUrl(url);
			wv.setWebViewClient(new WebViewClient() {
				public void onPageFinished(WebView wv, String url) {
					if (url.contains("https://login.gatech.edu/cas/login")) {
						wv.setVisibility(View.INVISIBLE);
						wv.loadUrl("javascript:document.getElementById('username').value = '"
								+ getSharedPreferences(
										MainActivity.USER_DETAILS, 0)
										.getString("username", null)
								+ "';document.getElementById('password').value='"
								+ getSharedPreferences(
										MainActivity.USER_DETAILS, 0)
										.getString("password", null)
								+ "';document.all('submit').click();");
					}else if (url.startsWith("http://ride.stingerette.com/student_ui/")){
						wv.setVisibility(View.VISIBLE);
						loading.setVisibility(View.INVISIBLE);
						frameAnimation.stop();
					}
				}
			});
//		}
		}

	}
	
	private boolean corretTime() {
		Calendar c = Calendar.getInstance(); 
		int hr = c.get(Calendar.HOUR_OF_DAY);
		int min = c.get(Calendar.MINUTE);
		int sec = c.get(Calendar.SECOND);
		
		if (hr > 17)
			return true;
		else if (hr ==17 && min >= 59 && sec >= 50)
			return true;
		else if (hr < 7)
			return true;
		
		return false;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		CookieSyncManager.createInstance(this);
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	@Override
	public void onResume() {
		super.onResume();
		CookieSyncManager.getInstance().startSync();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
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

@SuppressWarnings("unused")
		private class stingCookieHandler extends AsyncTask<Void, Void, String[]> {
		private String parseJsession(String mCookie) {
			String[] split = mCookie.split(";", -1);
			String mReturn = "";
			for (String temp : split) {
				if (temp.toLowerCase(Locale.US).contains("jsessionid")) {
					String[] arrayTemp = temp.split("=", 2);
					for (String js : arrayTemp) {
						if (js.toLowerCase(Locale.US).contains("jsessionid")) {
							mReturn = ";" + js.toLowerCase(Locale.US);
						} else {
							mReturn += "=" + js;
						}
					}
				}
			}
			return mReturn;
		}

		@Override
		protected void onPostExecute(String[] params) {
			manager.setCookie("https://login.gatech.edu/cas/login", params[0]);
			manager.setCookie(params[1], params[2]);
			SharedPreferences stingSp = getSharedPreferences(
					MainService.STING_ADD, 0);
			Editor stingEditor = stingSp.edit();
			stingEditor.putString("Sting_URL", params[1]);
			stingEditor.commit();
			Log.e("check",
					"A:::::"
							+ manager
									.getCookie("http://ride.stingerette.com/student_ui/"));
			Log.e("check",
					"B:::::"
							+ manager
									.getCookie("https://login.gatech.edu/cas/login"));
			String url = serviceSp.getString("Sting_URL", null);
			CookieSyncManager.createInstance(getApplicationContext());
			CookieSyncManager.getInstance().startSync();
			manager = CookieManager.getInstance();
			manager.acceptCookie();
			manager.setAcceptCookie(true);
			manager.setCookie("https://login.gatech.edu/cas/login",
					MainService.manager
							.getCookie("https://login.gatech.edu/cas/login"));
			manager.setCookie(url, MainService.manager.getCookie(url));
			wv = (WebView) findViewById(R.id.webView);
			wv.getSettings().setJavaScriptEnabled(true);
			wv.loadUrl(url);
			wv.setWebViewClient(new WebViewClient() {
				public void onPageFinished(WebView wv, String url) {
					loading.setVisibility(View.INVISIBLE);
				}
			});
		}

		@Override
		protected String[] doInBackground(Void... params) {
			String[] back = new String[3];
			try {
				String login = "https://login.gatech.edu/cas/login?service=http://ride.stingerette.com/student_ui/";
				URL urlForLogin = new URL(login);
				HttpsURLConnection connection = (HttpsURLConnection) urlForLogin
						.openConnection();
				connection.setInstanceFollowRedirects(false);
				connection.setRequestProperty("Cookie", "utmccn=(referral)");
				connection.setRequestProperty("Accept",
						"text/html, application/xhtml+xml, */*");
				connection.setRequestProperty("Accept-Language", "en-US");
				connection.setRequestProperty("User-Agent", "Mozilla");
				connection.setRequestProperty("Accept-Encoding",
						"gzip, deflate");
				connection.setRequestProperty("Host", "login.gatech.edu");
				connection.setRequestProperty("DNT", "1");
				connection.setRequestProperty("Connection", "Keep-Alive");
				String cookie = connection.getHeaderField("Set-Cookie"); // Real
																			// cookie
																			// usage
				String js = parseJsession(cookie); // URL usage
				cookie = (cookie.split(";", 2)[0]);

				String strForHtml = login + js
						+ "?service=http://ride.stingerette.com/student_ui/";
				URL urlForHtml = new URL(strForHtml);
				connection = (HttpsURLConnection) urlForHtml.openConnection();
				connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Cookie", cookie
						+ ";utmccn=(referral)");
				connection.setRequestProperty("Accept",
						"text/html, application/xhtml+xml, */*");
				connection.setRequestProperty("Accept-Language", "en-US");
				connection.setRequestProperty("User-Agent", "Mozilla");
				connection.setRequestProperty("Accept-Encoding",
						"gzip, deflate");
				connection.setRequestProperty("Host", "login.gatech.edu");
				connection.setRequestProperty("DNT", "1");
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection
						.setRequestProperty(
								"Referer",
								"https://login.gatech.edu/cas/login?service=http://ride.stingerette.com/student_ui/");
				connection.setRequestProperty("Cache-Control", "no-cache");
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				MainService.mainSp = getSharedPreferences(
						MainActivity.USER_DETAILS, 0);
				final String user = MainService.mainSp.getString("username",
						null);
				final String pass = MainService.mainSp.getString("password",
						null);
				String urlParameters = "username=" + user + "&password=" + pass
						+ "&lt=e1s1&_eventId=submit&submit=LOGIN";
				connection.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(
						connection.getOutputStream());
				wr.writeBytes(urlParameters);
				wr.flush();

				String stingAdd = "";
				for (String header : connection.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : connection.getHeaderFields().get(
								header)) {
							if (header.contains("Location")) {
								stingAdd = value;
							}
						}
					}
				}

				URL stingURL = new URL(stingAdd);
				HttpURLConnection conn = (HttpURLConnection) stingURL
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Cookie", cookie + ";utmccn=(referral)");
				conn.setRequestProperty("Accept",
						"text/html, application/xhtml+xml, */*");
				conn.setRequestProperty("Accept-Language", "en-US");
				conn.setRequestProperty("User-Agent", "Mozilla");
				conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
				conn.setRequestProperty("Host", "ride.stingerette.com");
				conn.setRequestProperty("DNT", "1");
				conn.setRequestProperty("Connection", "Keep-Alive");
				conn.setRequestProperty("Cache-Control", "no-cache");
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				String sessionId = "";
				for (String header : conn.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : conn.getHeaderFields().get(header)) {
							if (value.contains("sessionid="))
								sessionId = value.split(";", 2)[0];
						}
					}
				}

				if (conn.getResponseCode() == 200)
					Log.e("check", sessionId);
				back[0] = cookie;
				back[1] = stingAdd;
				back[2] = sessionId;

			} catch (IOException e) {
				e.getStackTrace();
				Log.e("check", "Not connected");
			}
			return back;
		}
	}


}