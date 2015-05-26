package com.mygatech.tsquare;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mygatech.MainActivity;
import com.mygatech.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Showing subjects
 * @author Joseph
 *
 */
public class TsquareMain extends Activity {
	private ListView listView;
	private StableArrayAdapter adapter;
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<String> urlList = new ArrayList<String>();
	private List<TsquareArrays> classList;
	protected static ArrayList<TsquareArrays> aList= new ArrayList<TsquareArrays>();
	protected static List<Object> menuList = new ArrayList<Object>();
	public static final String MENU_LIST = "MenuLists";
	public SharedPreferences menuSp;
	public Editor menuEditor;
	private Typeface typeface;
	HttpURLConnection connection;
	protected static String clickedName;
	private MenuItem menuItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tsquare_class);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		menuSp = getSharedPreferences(TsquareMain.MENU_LIST, 0);
		menuEditor = menuSp.edit();
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		HashMap<String, String> hitParameters = new HashMap<String, String>();
		hitParameters.put(Fields.HIT_TYPE, "appview");
		hitParameters.put(Fields.SCREEN_NAME, "Home Screen");
		tracker.send(hitParameters);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "T-Square Main").build());
		
		classList = new ArrayList<TsquareArrays>();
		nameList.clear();
		classList.clear();
		urlList.clear();
		aList.clear();
		
		String TSQUARE_SUB = "tsquaresub.txt";
		String dir = Environment.getExternalStorageDirectory() + "/MyGaTech/";
		FileInputStream fis;
		BufferedReader buffer;
		ArrayList<TsquareArrays> mainList = new ArrayList<TsquareArrays>();
		try {
			fis = new FileInputStream(dir + TSQUARE_SUB);
			buffer = new BufferedReader(new InputStreamReader(fis));
			String line = "";
            while((line = buffer.readLine()) != null){
            	mainList.add(new TsquareArrays(line.split(";",-1)[0], line.split(";",-1)[1],""));
            }
        	fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setTitle("T-Square: My classes");
		typeface = Typeface.createFromAsset(getAssets(), "font.ttf");

		Set<String> set = new HashSet<String>();
		for (Object obj : mainList) {			
			TsquareArrays elem = (TsquareArrays) obj;
			set.add(elem.getSet().get("subject"));
			classList.add(elem);
			urlList.add(elem.getSet().get("link"));
		}

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			nameList.add(it.next());
		}

		listView = (ListView) findViewById(R.id.tsquareListView);

		adapter = new StableArrayAdapter(this, R.layout.tsquare_row, nameList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();		
		
	}
	

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.fade, R.anim.hold);		
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {
		private  Context context;
	    private  List<String> Ids;
	    private  int rowResourceId;
	    ArrayList<ImageView> imgV;
	    ArrayList<AnimationDrawable> aniV;
	    HashMap<String,Integer> mIdMap = new HashMap<String, Integer>();
	    
	    public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
		      super(context, textViewResourceId, objects);
		      
		      this.context = context;
		      this.Ids = objects;
		      this.rowResourceId = textViewResourceId;
		      imgV = new ArrayList<ImageView>();
		      aniV = new ArrayList<AnimationDrawable>();
		      
		      for (int i = 0; i < Ids.size(); ++i) {
		        mIdMap.put(Ids.get(i), i);
		      }
		      
		    }
	    
	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent){
	        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        View rowView = inflater.inflate(rowResourceId, parent, false);
	        Button button = (Button) rowView.findViewById(R.id.tsquareBtn);
	        button.setText(nameList.get(position));
	        button.setBackgroundColor(Color.WHITE);
	        button.setTypeface(typeface);
			ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
			try {
				// image 100x100 adjusting
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeResource(getResources(), R.drawable.csicon,
						options);
				icon.setImageBitmap(decodeSampledBitmapFromResource(
						getResources(), getIcon(nameList.get(position)), 100,
						100));

			} catch (Exception e) {
				e.printStackTrace();
			} 
	        final ImageView loading = (ImageView)rowView.findViewById(R.id.loading);
	        loading.setBackgroundResource(R.anim.ani_spinner);
			loading.setVisibility(View.INVISIBLE);
    		final AnimationDrawable frameAnimation = (AnimationDrawable)loading.getBackground();
    		imgV.add(loading);
			aniV.add(frameAnimation);
    		button.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					loading.setVisibility(View.VISIBLE);
					frameAnimation.start();	
					overridePendingTransition(R.anim.fade, R.anim.hold);
					clickedName = nameList.get(position);
					Intent intent = new Intent(getApplicationContext(),TsquareMenu.class);
					intent.putExtra("subjectName", clickedName);
					startActivity(intent);
				}
    		});
    		
	        return rowView;
	    }
	    
	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }
	}
	
	private int getIcon(String subject){
		int icon = 0;
		Locale.setDefault(new Locale("US"));
		String subjOnly = subject.split("-")[0].toLowerCase(Locale.getDefault());
		String[] langs = {"kor", "fren", "japn", "ling", "psy", "span", "arbc", "russ", "chin"};
		for(String lang : langs){
			if(lang.equals(subjOnly))
				subjOnly = "lang";
		}

		icon = getResources().getIdentifier(subjOnly+"icon", "drawable", this.getPackageName());
		if(icon == 0)
			icon = getResources().getIdentifier("defaulticon", "drawable", this.getPackageName());
		return icon;
	}

	 public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	            int reqWidth, int reqHeight) {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeResource(res, resId, options);

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;
	        return BitmapFactory.decodeResource(res, resId, options);
	    }
	    
	 public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	        while ((halfHeight / inSampleSize) > reqHeight
	                && (halfWidth / inSampleSize) > reqWidth) {
	            inSampleSize *= 2;
	        }
	    }
	    return inSampleSize;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.tsquare_menu, menu);
		return true;
	}

	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_load:
			menuItem = item;
			menuItem.setActionView(R.layout.progressbar);
			menuItem.expandActionView();
			Toast.makeText(this, "Logging In :)", Toast.LENGTH_SHORT).show();
			TestTask tesk = new TestTask();
			tesk.execute("login");
			break;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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
	
	public class TestTask extends AsyncTask<String, Void, String> {

		//Make T-square logged in
		@Override
		protected String doInBackground(String... params) {
			try{
				CookieSyncManager.createInstance(getApplicationContext());
			    CookieSyncManager.getInstance().resetSync();
			    CookieSyncManager.getInstance().startSync();
			    CookieManager manager = CookieManager.getInstance();
			   	manager.acceptCookie();
			   	manager.setAcceptCookie(true);
				manager.setCookie("https://login.gatech.edu/cas/login", "Set-Cookie");
				manager.setCookie("https://t-square.gatech.edu/portal/pda", "Set-Cookie");
				manager.setCookie("https://t-square.gatech.edu/sakai-login-tool/container", "Set-Cookie");
			    HttpURLConnection.setFollowRedirects(true);

			    String login = "https://login.gatech.edu/cas/login";
				URL urlForLogin = new URL(login);		
				HttpsURLConnection connection = (HttpsURLConnection) urlForLogin.openConnection();
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
				cookie = (cookie.split(";",2)[0]);
				Log.e("check","Cookie1 outgoing: " + cookie);

		        //Response header fields
		        Log.e("check", "1111Response header fields==========================================");
		        for (String header : connection.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : connection.getHeaderFields()
								.get(header)) {
							Log.e("check", header + ": " + value);
						}
					}else{
						Log.e("check", "Header is null");
					}
				}
		        
		        //Take lt value for login
		        String lt = "";
		        Document doc =  Jsoup.parse(connection.getInputStream(),null, login);
				Elements links = doc.select("input[name=lt]");
				for(Element elem : links){
					lt = elem.attr("value");
				}       

				String strForHtml = login + js;
		        URL urlForHtml = new URL(strForHtml);
		        connection = (HttpsURLConnection)urlForHtml.openConnection();	    
		        connection.setRequestMethod("POST");
			    connection.setRequestProperty("Cookie",cookie+";utmccn=(referral)");
			    connection.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
		        connection.setRequestProperty("Accept-Language", "en-US");
		        connection.setRequestProperty("User-Agent", "Mozilla");
		        connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
		        connection.setRequestProperty("Host", "login.gatech.edu");
		        connection.setRequestProperty("DNT", "1");
		        connection.setRequestProperty("Connection","Keep-Alive");
		        connection.setRequestProperty("Referer", "https://login.gatech.edu/cas/login?service=https://t-square.gatech.edu/portal/pda");
		        connection.setRequestProperty("Cache-Control", "no-cache");
		        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		        final String user = getSharedPreferences(MainActivity.getUserDetails(), 0).getString("username", null);       
			    final String pass = getSharedPreferences(MainActivity.getUserDetails(), 0).getString("password", null); 
		        String urlParameters = "username="+user+"&password="+pass+"&lt="+lt+"&_eventId=submit&submit=LOGIN";
		        connection.setDoOutput(true);
		        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		        wr.writeBytes(urlParameters);
		        wr.flush();
		        wr.close();
			        				
		        Log.e("check", "After Post response==========================================");
		        String ticket = "";
		        Log.e("check",""+connection.getResponseCode());
			    Log.e("check",""+connection.getResponseMessage());
		        for (String header : connection.getHeaderFields().keySet()) {
					if (header != null) {
						for (String value : connection.getHeaderFields()
								.get(header)) {
							Log.e("check", header + ": " + value);
							if(value.contains("CASTGC")){
								value=value.split(";",2)[0];
								ticket+=value.split("-",2)[1];
							}
						}
					}
				}
		        
			    
			    if(connection.getResponseCode()==200 && ticket!=null){
			    	
			    	String tsquare = "https://t-square.gatech.edu/portal/pda";
					URL urlTsquare = new URL(tsquare);		
					connection = (HttpsURLConnection) urlTsquare.openConnection();
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Accept", "text/html, application/xhtml+xml, */*");
					connection.setRequestProperty("Accept-Language", "en-US");
					connection.setRequestProperty("User-Agent", "Mozilla");
					connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
					connection.setRequestProperty("Host", "t-square.gatech.edu");
					connection.setRequestProperty("DNT", "1");
					connection.setRequestProperty("Cookie", cookie);
					connection.setRequestProperty("Connection","Keep-Alive");
			       
				    String tsquareJS = "";
				    String bigip = "";
				    Log.e("check", "=========="+tsquare+"============");
				    Log.e("check",""+connection.getResponseCode());
				    Log.e("check",""+connection.getResponseMessage());
				    for (String header : connection.getHeaderFields().keySet()) {
						if (header != null) {
							for (String value : connection.getHeaderFields().get(header)) {
								Log.e("check", header + " " + value);
								if(value.contains("BIGip")){
									bigip = value.split(";",2)[0];
								}
								if(value.toLowerCase(Locale.US).contains("jsessionid")){
									tsquareJS = (value.split(";",2)[0]);
								}
							}
						}
					}
				    
				    String tF = "https://t-square.gatech.edu/portal/pda/?force.login=yes";
					URL uF = new URL(tF);		
					connection = (HttpsURLConnection) uF.openConnection();
					connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
					connection.setRequestProperty("Host", "login.gatech.edu");
					connection.setRequestProperty("DNT", "1");
					connection.setRequestProperty("Referer", " https://t-square.gatech.edu/portal/pda");
					connection.setRequestProperty("Cookie",cookie + "; " + ticket + ";utmccn=(referral)"); // tsquareJS+"; "+bigip + "; " + 
					connection.setRequestProperty("Connection","Keep-Alive");
			        Log.e("check", "=====================================================================");
			        Log.e("cookie", "COOKIE: " + cookie);
			        Log.e("cookie", "Ticket: " + ticket);
			        Log.e("cookie", "TsquareJS: " + tsquareJS);
			        Log.e("cookie", "BIGip: " + bigip);
			        Log.e("check",""+connection.getResponseCode());
				    Log.e("check",""+connection.getResponseMessage());
			        for (String header : connection.getHeaderFields().keySet()) {
						if (header != null) {
							for (String value : connection.getHeaderFields()
									.get(header)) {
									Log.e("check", header + ": " + value );
									if (value.toLowerCase(Locale.getDefault()).contains("jsessionid"))
										tsquareJS = (value.split(";", 2)[0]);
									if(value.contains("BIGip"))
										bigip = value.split(";",2)[0];
							}
						}else
							Log.e("check", "Header is null");
					}
			        
			        Log.e("cookie", "TsquareJS: " + tsquareJS);
			        Log.e("cookie", "BIGip: " + bigip);
			        String rdSakia = "https://t-square.gatech.edu/sakai-login-tool/container?force.login=yes";
			        URL urdSakia = new URL(rdSakia);
					connection = (HttpsURLConnection) urdSakia.openConnection();
					connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
					connection.setRequestProperty("Host", "login.gatech.edu");
					connection.setRequestProperty("DNT", "1");
					connection.setRequestProperty("Referer", " https://t-square.gatech.edu/portal/pda");
					connection.setRequestProperty("Cookie",tsquareJS + "; " + bigip + ";utmccn=(referral)"); // tsquareJS+"; "+bigip + "; " + 
					connection.setRequestProperty("Connection","Keep-Alive");
			        Log.e("check", "rdSakia====================================================================");
			        Log.e("check",""+connection.getResponseCode());
				    Log.e("check",""+connection.getResponseMessage());
			        for (String header : connection.getHeaderFields().keySet()) {
						if (header != null) {
							for (String value : connection.getHeaderFields()
									.get(header)) {
									Log.e("check", header + ": " + value );
									
							}
						}else{
							Log.e("check", "Header is null");
						}
					}
			        
			        String sakia = "https://login.gatech.edu/cas/login?service=https://t-square.gatech.edu/sakai-login-tool/container";
			        URL uSakia = new URL(sakia);
			        connection = (HttpsURLConnection) uSakia.openConnection();
			        connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
			        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
					connection.setRequestProperty("Host", "login.gatech.edu");
					connection.setRequestProperty("DNT", "1");
			        connection.setRequestProperty("Referer", "https://t-square.gatech.edu/portal/pda");//?ticket=ST-"+ticket);
			        connection.setRequestProperty("Cookie", cookie+"; CASTGC=TGT-"+ticket);
			        connection.setRequestProperty("Connection","Keep-Alive");
			        
			        Log.e("check", "sakia====================================================================");
			        Log.e("check",""+connection.getResponseCode());
				    Log.e("check",""+connection.getResponseMessage());
				    String nextURL = "";
			        for (String header : connection.getHeaderFields().keySet()) {
						if (header != null) {
							for (String value : connection.getHeaderFields()
									.get(header)) {
									Log.e("check", header + ": " + value );
								if(header.contains("Loca")){
									nextURL = value;
								}
							}
						}else{
							Log.e("check", "Header is null");
						}
					}
			        String finalTicket = nextURL.split("=", -1)[1];
			        Log.e("cookie", finalTicket);
			        
			        URL uSakiaContainer = new URL("https://t-square.gatech.edu/sakai-login-tool/container?ticket="+finalTicket);
			        connection = (HttpsURLConnection) uSakiaContainer.openConnection();
			        connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
			        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
					connection.setRequestProperty("Host", "t-square.gatech.edu");
					connection.setRequestProperty("DNT", "1");
			        connection.setRequestProperty("Referer", "https://t-square.gatech.edu/portal/pda?ticket=ST-"+ticket+"?force.login=yes");
			        connection.setRequestProperty("Cookie", tsquareJS+"; "+ bigip);
			        connection.setRequestProperty("Connection","Keep-Alive");
			        
			        Log.e("check", "sakia====================================================================");
			        Log.e("check",""+connection.getResponseCode());
				    Log.e("check",""+connection.getResponseMessage());

			        for (String header : connection.getHeaderFields().keySet()) {
						if (header != null) {
							for (String value : connection.getHeaderFields()
									.get(header)) {
									Log.e("check", header + ": " + value );
							}
						}else{
							Log.e("check", "Header is null");
						}
					}
			        
			        URL tsquareFinal = new URL("https://t-square.gatech.edu/portal/pda/?force.login=yes");
			        connection = (HttpsURLConnection) tsquareFinal.openConnection();
			        connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
			        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
					connection.setRequestProperty("Host", "t-square.gatech.edu");
					connection.setRequestProperty("DNT", "1");
			        connection.setRequestProperty("Referer", "https://t-square.gatech.edu/portal/pda?ticket=ST-"+ticket+"?force.login=yes");
			        connection.setRequestProperty("Cookie", tsquareJS+"; "+ bigip);
			        connection.setRequestProperty("Connection","Keep-Alive");
			        
			        Log.e("check", "tsquareFinal===============================================");
			        Log.e("check",""+connection.getResponseCode());
				    Log.e("check",""+connection.getResponseMessage());

			        for (String header : connection.getHeaderFields().keySet()) {
						if (header != null) {
							for (String value : connection.getHeaderFields()
									.get(header)) {
									Log.e("check", header + ": " + value );
							}
						}else{
							Log.e("check", "Header is null");
						}
					}
			        
			        Editor menuEditor = getSharedPreferences(TsquareMain.MENU_LIST, 0).edit();
			        menuEditor.clear().commit();
			        menuEditor.putString("Ticket", ticket);
			        menuEditor.putString("TsquareJS", tsquareJS);
			        menuEditor.putString("Bigip", bigip);
					menuEditor.commit();
			    }
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			new ToastRun("Logged In :) !").run();
			menuItem.collapseActionView();
			menuItem.setActionView(null);
		}
	};
	
	
	public String parseJsession(String mCookie){
		String[] split = mCookie.split(";", -1);
		String mReturn = "";
		for(String temp : split){
			if(temp.toLowerCase(Locale.US).contains("jsessionid")){
				String[] arrayTemp = temp.split("=",2);
				for(String js : arrayTemp){
					if(js.toLowerCase(Locale.US).contains("jsessionid")){
						mReturn = ";"+js.toLowerCase(Locale.getDefault());
					}else{
						mReturn+="="+js;
					}
				}
			}
		}
		return mReturn;
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
