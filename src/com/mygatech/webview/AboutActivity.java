package com.mygatech.webview;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mygatech.MainActivity;
import com.mygatech.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {
	Typeface typeFace;
	private final String DEVEL = "Developer: ";
	private final String JOSEPH = "Supreme Leader";
	private final String RAOUL = "Bachchan, Amitabh";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_main);
		setTitle("About Dear Leaders");
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "About Pagef").build());

		RelativeLayout form = (RelativeLayout) findViewById(R.id.form);
		form.setBackground(getResources().getDrawable(R.drawable.background));
		form.getBackground().setAlpha(50);
		form.setPadding(10, 0, 10, 20);
		typeFace = Typeface.createFromAsset(getAssets(), "font.ttf");
		TextView tv = (TextView) findViewById(R.id.about);
//		tv.setTypeface(typeFace);

		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String myGaTech = "  MyGaTech ver. ";
		SpannableString spString = new SpannableString(myGaTech
				+ pInfo.versionName);
//		spString.setSpan(new UnderlineSpan(), 0, spString.length(), 0);
		spString.setSpan(new StyleSpan(Typeface.BOLD), myGaTech.length(), spString.length(), 0);
//		spString.setSpan(new RelativeSizeSpan(1.5f), 0, spString.length(), 0); // set
																				// size

		tv.setText(spString);
		tv.append("\n  Disclaimer: \n  MyGaTech.claim@gmail.com");
		tv.setGravity(Gravity.LEFT);
//		tv.setTextSize(15L);

		((ImageView) findViewById(R.id.imageJoseph))
				.setImageDrawable(getResources()
						.getDrawable(R.drawable.joseph3));
		((ImageView) findViewById(R.id.imageRaoul))
				.setImageDrawable(getResources().getDrawable(R.drawable.raoul2));
		((TextView) findViewById(R.id.textJoseph)).setText(DEVEL + JOSEPH);
		((TextView) findViewById(R.id.textJoseph)).setGravity(Gravity.CENTER_HORIZONTAL);
		((TextView) findViewById(R.id.textRaoul)).setText(DEVEL + RAOUL);
		((TextView) findViewById(R.id.textRaoul)).setGravity(Gravity.CENTER_HORIZONTAL);

		TextView textDes = (TextView)findViewById(R.id.textDescription);
		textDes.setTypeface(typeFace);
		textDes.setTextSize(20);
		textDes.setText("Project of first year GaTech CoC students\nPicture credit to Nick Selby.");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
			return true;
		default:
			overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
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
