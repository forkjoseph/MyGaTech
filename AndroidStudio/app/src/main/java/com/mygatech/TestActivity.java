package com.mygatech;

import java.io.BufferedReader;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.mygatech.R.color;
import com.mygatech.webview.RestDatabaseHandler;
import com.mygatech.webview.Restaurant;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TestActivity extends Activity{
	private GoogleMap mGoogleMap;
	PolylineOptions options;
	public String getCookie(String siteName,String CookieName){     
	    String CookieValue = null;

	    CookieManager cookieManager = CookieManager.getInstance();
	    CookieManager.setAcceptFileSchemeCookies(true);
	    String cookies = cookieManager.getCookie(siteName);       
	    String[] temp=cookies.split("[;]");
	    for (String ar1 : temp ){
	        if(ar1.contains(CookieName)){
	            String[] temp1=ar1.split("[=]");
	            CookieValue = temp1[1];
	        }
	    }              
	    return CookieValue; 
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sting_main);
	    CookieManager.setAcceptFileSchemeCookies(true);

		WebView wv = (WebView)findViewById(R.id.webView);
		wv.loadUrl("https://t-square.gatech.edu/dav/gtc-e0d2-c7f1-5cfa-8550-c5570099f882");
//		wv.setWebViewClient(new WebViewClient());
		wv.setWebChromeClient(new WebChromeClient());
		wv.setWebViewClient(new WebViewClient(){
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			    handler.proceed(); // Ignore SSL certificate errors
			    Log.d("Message:",handler.obtainMessage().toString());
			}
		});
		wv.setDownloadListener(new DownloadListener() {
        @Override
        public void onDownloadStart(String url, String userAgent,
                String contentDisposition, String mimetype,
                long contentLength) {
            // handle download, here we use brower to download, also you can try other approach.
//			new downloadFromServer().execute(url);
    		Log.d("JSESSIONID", getCookie("https://t-square.gatech.edu", "JSESSIONID"));
//    		Log.d("JSESSIONID", getCookie("https://t-square.gatech.edu", "BIGipServerfiles-t-square-http"));

        	
			
        	Log.d("Download requested:", url + " " + userAgent + " " + contentDisposition + " " + mimetype);

//            Uri uri = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            startActivity(intent);
        }});
//		setContentView(R.layout.map_activity);
//		mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.gMap)).getMap();
//
//		getActionBar().setTitle("GT Map");
//		getActionBar().setDisplayHomeAsUpEnabled(true);
//		
//		LatLng culc = new LatLng(33.7744833, -84.3964000);
//		options = new PolylineOptions();
////		try {
////			loadRouteNight();
////		} catch (IOException e) {
//			// TODO Auto-generated catch block
////			e.printStackTrace();
////		} 
//		new getOnlyNightRoute().execute();
//		options.geodesic(true);
//		
//		mGoogleMap.addMarker(new MarkerOptions()
//				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
//				.snippet("MyGaTech").position(culc))
//				.showInfoWindow();
//		mGoogleMap.setMyLocationEnabled(true);
//		mGoogleMap.getUiSettings().setCompassEnabled(true);
//		mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
//		mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(culc, 15));
		
	}
	
	
	
	public class downloadFromServer extends AsyncTask<String, Void, Void> {
		
		@Override
		protected Void doInBackground(String... url) {

			AssetManager am = getAssets();
        	Certificate ca = null;
        	InputStream caInput = null;
        	try {
				CertificateFactory cf = CertificateFactory.getInstance("X.509");
	        	caInput = am.open("gt-server-root.crt");
	            ca = cf.generateCertificate(caInput);
	            
	            System.out.println("ca=" + ca.getPublicKey());

			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
			    try {
					caInput.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        	String keyStoreType = KeyStore.getDefaultType();
        	KeyStore keyStore;
			try {
				keyStore = KeyStore.getInstance(keyStoreType);
				keyStore.load(null, null);
	        	keyStore.setCertificateEntry("ca", ca);
	        	String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
	        	TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
				tmf.init(keyStore);
				SSLContext context = SSLContext.getInstance("TLS");
				context.init(null, tmf.getTrustManagers(), null);
				URL mURL = new URL(url[0]);
				HttpsURLConnection urlConnection =
					    (HttpsURLConnection)mURL.openConnection();
				urlConnection.setSSLSocketFactory(context.getSocketFactory());
				InputStream in = urlConnection.getInputStream();
				Log.d("INput:" , in.toString());
			} catch (KeyStoreException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (CertificateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (KeyManagementException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36";

	public class getOnlyRedRoute extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void aaaa){
//			mGoogleMap.addPolygon(options);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
//			try {
////				options.addAll(drawOnlyRed());
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			return null;
		}
		public ArrayList<LatLng> drawOnlyRed() throws ClientProtocolException, IOException, JSONException{
			String stopJson = "http://dev.m.gatech.edu/api/buses/shape";
			URL url = new URL(stopJson);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(c.getInputStream()));
		    StringBuilder result = new StringBuilder();

		    String inputStr;
		    while ((inputStr = rd.readLine()) != null)
		    	result.append(inputStr);
		    JSONArray jarr = new JSONArray(result.toString());
		    ArrayList<LatLng> arr = new ArrayList<LatLng> ();
			for(int i = 0; i < jarr.length(); i ++) {
				JSONObject o = jarr.getJSONObject(i);
				if (o.get("route_id").equals("red")) {
					double lat = o.getDouble("shape_pt_lat");
					double lon = o.getDouble("shape_pt_lon");
					arr.add(new LatLng(lat, lon));
					System.out.println(" located at lat:" + o.getDouble("shape_pt_lat") + " lot:" + o.getDouble("shape_pt_lon"));
				}
			}
			return arr;
		}
	}
	
	public class getOnlyNightRoute extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void aaaa){
			mGoogleMap.addPolyline(options);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				options.addAll(drawOnlyRed());
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		public ArrayList<LatLng> drawOnlyRed() throws ClientProtocolException, IOException, JSONException{
			String stopJson = "http://dev.m.gatech.edu/api/buses/shape";
			URL url = new URL(stopJson);
			HttpURLConnection c = (HttpURLConnection) url.openConnection();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(c.getInputStream()));
		    StringBuilder result = new StringBuilder();

		    String inputStr;
		    while ((inputStr = rd.readLine()) != null)
		    	result.append(inputStr);
		    JSONArray jarr = new JSONArray(result.toString());
		    ArrayList<LatLng> arr = new ArrayList<LatLng> ();
			for(int i = 0; i < jarr.length(); i ++) {
				JSONObject o = jarr.getJSONObject(i);
				if (o.get("route_id").equals("trolley")) {
					double lat = o.getDouble("shape_pt_lat");
					double lon = o.getDouble("shape_pt_lon");
					if (lat != 0.0 && lon != 0.0 && (o.get("shape_id").equals("ferstdr2fershemrt")
							||o.get("shape_id").equals("martastat2techrec"))){
						arr.add(new LatLng(lat, lon));
						Log.d((String) o.get("shape_pt_sequence"), " located at lat:" +
								o.getDouble("shape_pt_lat") + " lot:" + o.getDouble("shape_pt_lon"));
					}
				}
			}
			return arr;
		}
	}
	
	private void loadRouteNight() throws IOException {
		final Resources resources = getResources();
		InputStream is = resources.openRawResource(R.raw.route_night);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("//")) {
					String[] s = TextUtils.split(line, ",");
					if (s.length == 2)
						options.add(new LatLng(Double.parseDouble(s[0]), Double.parseDouble(s[1])));
				}
			}
			options.color(color.yellow);
			mGoogleMap.addPolyline(options);
			
		} finally {
			br.close();
		}
	}
	
}
