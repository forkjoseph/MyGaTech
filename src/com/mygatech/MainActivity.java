package com.mygatech;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.google.analytics.tracking.android.MapBuilder;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mygatech.external.LinkToQBot;
import com.mygatech.map.MapPane;
import com.mygatech.tsquare.TsquareMain;
import com.mygatech.tsquare.TsquareWVOnly;
import com.mygatech.webview.AboutActivity;
import com.mygatech.webview.BusActivity;
import com.mygatech.webview.CalendarActivity;
import com.mygatech.webview.EatActivity;
import com.mygatech.webview.RestDatabaseHandler;
import com.mygatech.webview.Restaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class MainActivity extends Activity {
	private Typeface typeface;
	protected static ArrayList<Menus> Items;
	private AdView adView;
	protected static final String USER_DETAILS = "UserDeails";
	protected static final String SETTINGS = "Settings";
	
	protected static final String[] activities = { "T-Square", "BuzzPort",
			"Where To Eat", "GT-Map", "NextBus", "Stingerette", "GT Mail",
			"Reserve Room 4 Me", "Calendar", "Request Maintenance", "QBot",
			"Send Feedback" };
	protected static final String[] activitiesGUEST = {
		"Where To Eat", "NextBus", "Calendar", "QBot",
		"Send Feedback", "Map" };
	protected static SharedPreferences sp, spFirst, spLogin;
	protected long clickedTime;
	protected MyAdapter adapter;
	protected BroadcastReceiver mReceiver;
	//Google GCM=====================================================
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCMDemo";
    
    public static final String TRACKING = "UA-47695293-1";
    GoogleCloudMessaging gcm;
    SharedPreferences prefs;
    String regid;
    private Context context;

	protected class Menus{
		private String title;
		private Class<?> cls;
		
		public Menus(String title, Class<?> cls){
			this.title = title;
			this.cls = cls;
		}
		
		public String getTitle(){return title;}
		public Class<?> getCls(){return cls;}
	}
	
	public static String getUserDetails() {
		return USER_DETAILS;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(TRACKING);		
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Main Screen").build());
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		typeface = Typeface.createFromAsset(getAssets(), "font.ttf");
		sp = getSharedPreferences(MainActivity.USER_DETAILS, 0);

		(sp.edit()).putBoolean("startActivityInUse", false).commit();
		SharedPreferences menuSp = getSharedPreferences(MainActivity.SETTINGS, 0);
		
		boolean hasLoggedIn = sp.getBoolean("hasLoggedIn", false);
		boolean isStudent = sp.getBoolean("Student", false);
		
		if (isNetworkConnected() && hasLoggedIn ) {			
			IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
			IntentFilter time = new IntentFilter(Intent.ACTION_TIME_TICK);
			IntentFilter battery = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			filter.addAction(Intent.ACTION_SCREEN_OFF);
			
			mReceiver = new MainReceiver();
			registerReceiver(mReceiver, filter);			
			registerReceiver(mReceiver, time);
			registerReceiver(mReceiver, battery);
		}
//		hasLoggedIn = true;//debug purpose
		if (!hasLoggedIn) {
			startActivity(new Intent(getApplicationContext(),
					GuestStudent.class));
			finish();
		} else {
			Items = new ArrayList<Menus>();
			if(menuSp.getBoolean("T-Square", true) && isStudent) {
				Items.add(new Menus("T-Square", TsquareMain.class)); } 
			if(menuSp.getBoolean("BuzzPort", true) && isStudent) {
				Menus buzzport = new Menus("BuzzPort", BuzzportActivity.class);
				Items.add(buzzport); }
			if(menuSp.getBoolean("Where To Eat", true)) {
				Menus eat = new Menus("Where To Eat", EatActivity.class);
				Items.add(eat);
				RestDatabaseHandler db = new RestDatabaseHandler(this);
				if (db.getRestCount() == 0 ){
					dbManage(db);
				}
			}
			if(menuSp.getBoolean("Map", true))
				Items.add(new Menus("GT Map", MapPane.class));
			if(menuSp.getBoolean("NextBus", true)) {
				Menus bus = new Menus("NextBus", BusActivity.class);
				Items.add(bus); }
			if(menuSp.getBoolean("Stingerette", true)&&isStudent) {
				Menus sting = new Menus("Stingerette", StingActivity.class);
				Items.add(sting); }
			if(menuSp.getBoolean("GT Mail", true)&&isStudent) {
				Menus email = new Menus("GT Mail", EmailActivity.class);
				Items.add(email); }
			if(menuSp.getBoolean("Reserve Room 4 Me", true)&&isStudent) {
				Menus reserve = new Menus("Reserve Room 4 Me",ReservActivity.class);
				Items.add(reserve); }
			if(menuSp.getBoolean("Calendar", true)) {
				Menus gtCalender = new Menus("Calendar", CalendarActivity.class);
				Items.add(gtCalender);}
			if(menuSp.getBoolean("Request Maintenance", true)&&isStudent){
				Menus maintenance = new Menus("Request Maintenance", MaintenanceActivity.class);
				Items.add(maintenance); }
			if(menuSp.getBoolean("QBot", true)){
				Menus qbot = new Menus("QBot", LinkToQBot.class);
				Items.add(qbot);}
			if(menuSp.getBoolean("Send Feedback", true)){
				Menus mail = new Menus("Send Feedback", FeedbackActivity.class);
				Items.add(mail);}
//			Items.add(new Menus("Test", TestActivity.class));
			
			adapter = new MyAdapter(this, R.layout.main_row, Items);
			final ListView list = (ListView) findViewById(R.id.mainList);
			list.setAdapter(adapter);
			
			
		}

		// AdMob adding part
		adView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().setBirthday(new GregorianCalendar(1992, 1, 1).getTime()).build();
		adView.loadAd(adRequest);

		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Google GCM
		// Register receivers for push notifications
		// Create and start push manager
		context= getApplicationContext();
//		if (checkPlayServices()) {
//			gcm = GoogleCloudMessaging.getInstance(this);
//			regid = getRegistrationId(context);
//			if (regid.isEmpty()) 
//				registerInBackground();
//		} else 
//			Log.i(TAG, "No valid Google Play Services APK found.");
		
	}

	
	//------ OPTION MENU 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.loginSetting:
			startActivity(new Intent(getApplicationContext(),
					StartActivity.class));
			overridePendingTransition(R.anim.toleftenter, R.anim.toleftleave);
			finish();

			break;
		case R.id.Settings:
			startActivity(new Intent(getApplicationContext(),
					MenuSettingsActivity.class));
			overridePendingTransition(R.anim.torightenter, R.anim.torightleave);
			break;
		case R.id.about:
			startActivity(new Intent(getApplicationContext(),
					AboutActivity.class));
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
			finish();

			break;
		case R.id.GUEST:
			startActivity(new Intent(getApplicationContext(),
					GuestStudent.class));
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
			finish();

			break;
//		case R.id.Impress:
//			getApplicationContext().startService(new Intent(getApplicationContext(),AFDService.class));
//			break;
			
			
		}
		return true;
	}   

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		for(AnimationDrawable aniTemp : adapter.aniArray){
    		aniTemp.stop();
    	}
    	for(ImageView imgTemp : adapter.imgArray){
    		imgTemp.setVisibility(View.INVISIBLE);
    	}
	}
	
	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		protected ArrayList<Menus> src;
		protected ArrayList<ImageView> imgArray = new ArrayList<ImageView>();
		protected ArrayList<AnimationDrawable> aniArray = new ArrayList<AnimationDrawable>();
		int layout;
		
		public MyAdapter(Context context, int layout, ArrayList<Menus> src){
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.src = src;
			this.layout = layout;
		}
		
		public int getCount(){
			return src.size();
		}
		
		public String getItem(int position){
			return src.get(position).getTitle();
		}
		
		public long getItemId(int position){
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			final int pos = position;
			if(convertView == null){
				convertView = inflater.inflate(layout,parent, false);
			}
			Button bt = (Button)convertView.findViewById(R.id.mainBtn);
			bt.setBackgroundColor(Color.WHITE);
			bt.setText(src.get(position).getTitle());
			bt.setTypeface(typeface);
			final ImageView loading = (ImageView)convertView.findViewById(R.id.loading);
    		loading.setBackgroundResource(R.anim.ani_spinner);
			loading.setVisibility(View.INVISIBLE);
    		final AnimationDrawable frameAnimation = (AnimationDrawable)loading.getBackground();
    		imgArray.add(loading);
    		aniArray.add(frameAnimation);
    		
			bt.setOnClickListener(new ButtonListener(loading, frameAnimation, pos));
			return convertView;
		}
	}
	
	private class ButtonListener implements Button.OnClickListener{
		private ImageView loading;
		private AnimationDrawable frameAnimation;
		private int pos;
		
		public ButtonListener(ImageView loading, AnimationDrawable frameAnimation, int pos){
			this.loading = loading;
			this.frameAnimation = frameAnimation;
			this.pos = pos;
		}	
		
		public void onClick(View v) {
			boolean launchIt = false;
			if(clickedTime == 0L){
				clickedTime = System.currentTimeMillis();
				launchIt = true;
			}else if(System.currentTimeMillis() - clickedTime > 1000){
				clickedTime = System.currentTimeMillis();
				launchIt = true;
			}
			if(launchIt){
				loading.setVisibility(View.VISIBLE);
				frameAnimation.start();
				Intent menuItem = new Intent(getApplicationContext(), adapter.src.get(pos).getCls());
				menuItem.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivityForResult(menuItem, pos);
			}
			
			overridePendingTransition(R.anim.fade, R.anim.hold);
		}		
	}

	public boolean isNetworkConnected(){
	    ConnectivityManager connectivityManager 
	            = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();    
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (adView != null)
			adView.destroy();
		if(mReceiver != null)
			unregisterReceiver(mReceiver);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    if(adapter != null){
	    	for(AnimationDrawable aniTemp : adapter.aniArray){
	    		aniTemp.stop();
	    	}
	    	for(ImageView imgTemp : adapter.imgArray){
	    		imgTemp.setVisibility(View.INVISIBLE);
	    	}
	    }
	    checkPlayServices();
	}
	
	@Override
	public void onPause(){
	    super.onPause();
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

	//--------------------------------------------------------------------------------------------------
	//GOOGLE GCM
	
	/**
	 * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
	 * or CCS to send messages to your app. Not needed for this demo since the
	 * device sends upstream messages to a server that echoes back the message
	 * using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
	    // Your implementation here.
	}
	
	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 *
	 * @param context application's context.
	 * @param regId registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	}
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	
	private void registerInBackground() {
	    new AsyncTask<Void, Void, String>() {
	        @Override
	        protected String doInBackground(Void... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(getString(R.string.SENDER_ID));
	                msg = "Device registered, registration ID=" + regid;

	                Log.e(TAG, msg);
	                // You should send the registration ID to your server over HTTP,
	                // so it can use GCM/HTTP or CCS to send messages to your app.
	                // The request to your server should be authenticated if your app
	                // is using accounts.
	                sendRegistrationIdToBackend();

	                // For this demo: we don't need to send it because the device
	                // will send upstream messages to a server that echo back the
	                // message using the 'from' address in the message.

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
		        	Log.d(TAG, msg);
	            }
	            return msg;
	        }

	        @Override
	        protected void onPostExecute(String msg) {
	        	//mDisplay.append(msg + "\n");
	        }
	    }.execute(null, null, null);
	    
	}
	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	public void dbManage(RestDatabaseHandler db) {
		Log.d("Delete: ", "Deleting...");
//		db.deleteAll();
		try {
			Log.d("Insert: ", "Inserting...");
			loadRegularData(db);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadRegularData(RestDatabaseHandler db) throws IOException {
		final Resources resources = getResources();
		InputStream is = resources.openRawResource(R.raw.eat_regular_data);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("//")) {
					String[] s = TextUtils.split(line, ",");
					if (s.length == 4) 
						db.addRest(new Restaurant(s[0], s[1].trim(), s[2].trim(), Integer
								.parseInt(s[3].trim())));
				}
			}
		} finally {
			br.close();
		}
	}
	
}
