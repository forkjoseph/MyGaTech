package com.mygatech;


import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mygatech.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class BuzzportActivity extends Activity {
	private static CookieManager manager;
	private WebView wv;
//	private static HttpsURLConnection connection;
//	private static Editor editor;
	private ImageView loading;
	private AnimationDrawable frameAnimation;
	private Menu menu;
	
	@SuppressLint("SetJavaScriptEnabled")
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.buzz_main);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Buzzport Screen").build());

		wv= (WebView) findViewById(R.id.buzzWebView);  
		wv.getSettings().setJavaScriptEnabled(true);
	    wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setUseWideViewPort(true);//must be true
	    wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true); // javascript가 window.open()을 사용할 수 있도록 설정
	    wv.getSettings().setSupportMultipleWindows(true); // 여러개의 윈도우를 사용할 수 있도록 설정
	    wv.getSettings().setLoadsImagesAutomatically(true); // 웹뷰가 앱에 등록되어 있는 이미지 리소스를 자동으로 로드하도록 설정
	    wv.getSettings().setAllowFileAccessFromFileURLs(true);
	    wv.getSettings().setAppCacheEnabled(true);
	    wv.getSettings().setAllowUniversalAccessFromFileURLs(true);
	  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		this.menu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.buzzport_menu, menu);
		menu.getItem(1).setEnabled(false);
		menu.getItem(2).setEnabled(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		case R.id.buzzport:
			setTitle("Buzzport");
			Toast.makeText(this, "Tab to Buzzport", Toast.LENGTH_LONG)
	          .show();
			loading.setVisibility(View.VISIBLE);
			frameAnimation.start();
			wv.loadUrl("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu");
			menu.getItem(1).setEnabled(false);
			menu.getItem(2).setEnabled(true);
			break;
		case R.id.oscar:
			setTitle("Oscar");
			Toast.makeText(this, "Tab to Oscar", Toast.LENGTH_LONG)
	          .show();
			wv.loadUrl("http://buzzport.gatech.edu/render.UserLayoutRootNode.uP?uP_tparam=utf&utf=%2Fcp%2Fip%2Flogin%3Fsys%3Dsct%26url%3Dhttps%3A%2F%2Foscar.gatech.edu/pls/bprod%2Fztgkauth.zp_authorize_from_login");

			menu.getItem(1).setEnabled(true);
			menu.getItem(2).setEnabled(false);
//			wv.loadUrl("https://oscar.gatech.edu/pls/bprod/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu&msg=WELCOME");
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	public void onResume(){
		super.onResume();
		wv.setVisibility(View.INVISIBLE);

		loading = (ImageView) findViewById(R.id.loading);
		loading.setBackgroundResource(R.anim.ani_spinner);
		frameAnimation = (AnimationDrawable) loading.getBackground();
		frameAnimation.start();

		CookieSyncManager.createInstance(this);
		CookieSyncManager.getInstance().startSync();
		manager = CookieManager.getInstance();
		manager.acceptCookie();
		manager.setAcceptCookie(true);
		manager.setCookie("https://login.gatech.edu/cas/login", "Set-Cookie");
		try {
			wv.loadUrl("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu");
			wv.setWebViewClient(new WebViewClient() {
				public void onPageFinished(WebView wv, String url) {
					if (url.equals("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu")) {
						final String user = getSharedPreferences(
								MainActivity.USER_DETAILS, 0).getString(
								"username", null);
						final String pass = getSharedPreferences(
								MainActivity.USER_DETAILS, 0).getString(
								"password", null);
						wv.setVisibility(View.INVISIBLE);
						wv.loadUrl("javascript:document.getElementById('username').value = '"
								+ user
								+ "';document.getElementById('password').value='"
								+ pass + // "';");
								"';document.all('submit').click();");
					} else if (url
							.equals("https://buzzport.gatech.edu/cp/home/displaylogin")) {
						wv.setVisibility(View.INVISIBLE);
						wv.loadUrl("javascript:document.getElementById('login_btn').click()");
					} else if (url
							.equals("http://buzzport.gatech.edu/cps/welcome/loginok.html")) {
					} else if (url
							.equals("http://buzzport.gatech.edu/render.userLayoutRootNode.uP?uP_root=root")) {
						wv.clearHistory();
						loading.setVisibility(View.INVISIBLE);
						frameAnimation.stop();
						wv.setVisibility(View.VISIBLE);
						wv.getSettings().setLoadWithOverviewMode(true);
						menu.getItem(1).setEnabled(false);
						menu.getItem(2).setEnabled(true);
					}

				}
			});

		} catch (Exception e) {
			e.getStackTrace();
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

	@Override
    public void onPause(){
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

	
	
/*	public void getPOBox(View v){
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				cookieHandler cook = new cookieHandler();
				cook.execute();
			}
		});
	}*/
	
//	private class cookieHandler extends AsyncTask<Void, Void, Void>{
//		@Override
//		protected Void doInBackground(Void... params) {
//			try{
//				URL urlForLogin = new URL("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu");
//				wv.loadUrl("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu");
//				wv.setWebViewClient(new WebViewClient(){
//					public void onPageFinished(WebView wv, String url){
//		        		if(url.equals("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu")){
//	        			System.out.println("==========Login injection begins");
//	        			SharedPreferences spMain = getSharedPreferences(MainActivity.USER_DETAILS, 0);
//	        			final String user = spMain.getString("username", null);       
//	            	    final String pass = spMain.getString("password", null); 
//	            	    System.out.println(user + " " + pass);
//	        			wv.loadUrl("javascript:document.getElementById('username').value = '"+user+
//	        					"';document.getElementById('password').value='"+pass+//"';");
//	        					"';document.all('submit').click();");
//		        		}
//		        		
//		        		if(url.equals("http://buzzport.gatech.edu/render.userLayoutRootNode.uP?uP_root=root")){
//		        			wv.loadUrl("javascript:window.HtmlViewer.showHTML" +
//		                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//		        		}
//		        		
//		        		if (url.equals("https://oscar.gatech.edu/pls/bprod/hwwkppob.P_DispPOBox")){
//		        			wv.loadUrl("javascript:window.HtmlViewer.showHTML" +
//		                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//		        		}
//	        	}
//			});
//				
//			wv.loadUrl("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu");
//			String cookie = manager.getCookie("https://login.gatech.edu/cas/login?service=http%3A%2F%2Fbuzzport.gatech.edu");
//			connection = (HttpsURLConnection) urlForLogin.openConnection();
//            connection.setRequestProperty("Cookie",cookie);
//            connection.connect();
//            wv.loadUrl("https://buzzport.gatech.edu/misc/preauth.html?");
// 			URL urlForHtml = new URL("https://buzzport.gatech.edu/misc/preauth.html?");
//            String cookie2 = manager.getCookie("https://buzzport.gatech.edu/misc/preauth.html?");
//            connection = (HttpsURLConnection)urlForHtml.openConnection();
//            connection.setRequestProperty("Cookie", cookie2);
//            connection.connect();
//            Thread.sleep(7000);
//            HttpURLConnection connection2;
//            connection2 = (HttpURLConnection)(new URL("http://buzzport.gatech.edu/cps/welcome/loginok.html")).openConnection();
//            connection2.connect();
//    		BufferedReader br = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
//    		
//            wv.loadUrl("http://buzzport.gatech.edu/render.UserLayoutRootNode.uP?uP_tparam=utf&utf=%2Fcp%2Fip%2Flogin%3Fsys%3Dsct%26url%3Dhttps%3A%2F%2Foscar.gatech.edu/pls/bprod%2Fztgkauth.zp_authorize_from_login");
//            URL urlForOscar = new URL("http://buzzport.gatech.edu/render.UserLayoutRootNode.uP?uP_tparam=utf&utf=%2Fcp%2Fip%2Flogin%3Fsys%3Dsct%26url%3Dhttps%3A%2F%2Foscar.gatech.edu/pls/bprod%2Fztgkauth.zp_authorize_from_login");
//            String cookie3 = manager.getCookie("http://buzzport.gatech.edu/render.UserLayoutRootNode.uP?uP_tparam=utf&utf=%2Fcp%2Fip%2Flogin%3Fsys%3Dsct%26url%3Dhttps%3A%2F%2Foscar.gatech.edu/pls/bprod%2Fztgkauth.zp_authorize_from_login");
//            connection2 = (HttpURLConnection)urlForOscar.openConnection();
//            connection2.setRequestProperty("Cookie",cookie3);
//            connection2.connect();
//            Thread.sleep(3000);
//            
//            // PO BOX
//            wv.loadUrl("https://oscar.gatech.edu/pls/bprod/hwwkppob.P_DispPOBox");
//            URL urlForPO = new URL("https://oscar.gatech.edu/pls/bprod/hwwkppob.P_DispPOBox");
//            String cookie4 = manager.getCookie("https://oscar.gatech.edu/pls/bprod/hwwkppob.P_DispPOBox");
//            connection = (HttpsURLConnection)urlForPO.openConnection();
//            connection.setRequestProperty("Cookie", cookie4);
//            connection.connect();
//           
//    		Document doc =  Jsoup.parse(connection.getInputStream(),null, "https://oscar.gatech.edu/pls/bprod/hwwkppob.P_DispPOBox");
//    		Elements links = doc.select("blockquote, h4");
//    		String[] mail = new String[2];
//    		int i = 0;
//			for(Element link: links) {
//				if(i%2==0){
//					mail[i/2]= link.text();
//				}else{
//					String tString = mail[i/2];
//					mail[i/2] = tString+"\n"+link.text();
//				}
//				i++;
//			}
////			editor = sp.edit();
//            editor.putString("address",mail[0]);              
//            editor.putString("combination",mail[1]);
//            editor.putBoolean("hasUpdated", true);
//            editor.commit();
////			address = mail[0];
////			combi = mail[1];
//			//Log.e("check", address);
//			//Log.e("check", combi);
//			
//			overridePendingTransition(R.anim.fade, R.anim.hold);
//			
//			}catch(Throwable e){
//				e.printStackTrace();
//			}
//			return null;
//		}
//		
//	}
	
	
}
