package com.mygatech;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

public class MenuSettingsActivity extends Activity {
	private SharedPreferences sp, sp_internal;
	private Editor editor, editor_internal;
	private boolean isStudent;
	private MenuAdapter adapter;
	private Typeface typeface;
	private static final String settings = "SETTINGS";

	@SuppressLint("CommitPrefEdits")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Select all buttons!!!!!!!!!!!!!!!!!!!
		setContentView(R.layout.menu_settings);
		
		sp = getSharedPreferences(MainActivity.SETTINGS, MODE_PRIVATE);
		sp_internal = getSharedPreferences(settings, MODE_PRIVATE);
		isStudent = getSharedPreferences(MainActivity.getUserDetails(), 0).getBoolean("Student", false);
		
		editor_internal = sp_internal.edit();
		for(String s : MainActivity.activities){
			editor_internal.putBoolean(s, sp.getBoolean(s, true));
		}
		editor = sp.edit();
		
		typeface = Typeface.createFromAsset(getAssets(), "KeepCalm-Medium.ttf");
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Menu Setting Screen").build());

		TextView tv = (TextView)findViewById(R.id.settingTv);
		tv.setText("Select Menu Items");
		tv.setTextSize(30f);
		tv.setTypeface(typeface);
		
		
		final Button commit = (Button)findViewById(R.id.commit);
		commit.setText("Save");
		((Button)findViewById(R.id.back)).setText("Back");
		if(isStudent)
			adapter = new MenuAdapter(this, R.layout.menu_settings_row, MainActivity.activities);
		else
			adapter = new MenuAdapter(this, R.layout.menu_settings_row, MainActivity.activitiesGUEST);
		final ListView list = (ListView) findViewById(R.id.menuListView);
		list.setAdapter(adapter);

	}
	
	private class MenuAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		protected String[] src;
		int layout;
		
		public MenuAdapter(Context context, int layout, String[] src){
			inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.src = src;
			this.layout = layout;
		}
		
		public int getCount(){
			return src.length;
		}
		
		public String getItem(int position){
			return src[position];
		}
		
		public long getItemId(int position){
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			if(convertView == null){
				convertView = inflater.inflate(layout,parent, false);
			}
			
			CheckBox cb = (CheckBox)convertView.findViewById(R.id.menuCheckBox);
//			final float scale = getResources().getDisplayMetrics().density;
//			cb.setPadding(cb.getPaddingLeft() + (int)(10.0f * scale + 0.5f),
//			        cb.getPaddingTop(),
//			        cb.getPaddingRight(),
//			        cb.getPaddingBottom());
			cb.setBackgroundColor(Color.WHITE);
			cb.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			cb.setText(src[position]);
			cb.setChecked(sp_internal.getBoolean(cb.getText().toString(), true));
			cb.setTypeface(typeface);
    		
			return convertView;
		}

		
		public void onItemClick(View view){
			CheckBox check = (CheckBox) view;
			editor_internal.putBoolean(check.getText().toString(), check.isChecked());
			editor_internal.commit();
		}
	}
	
	public void onClick(View view) {
		if(view instanceof CheckBox){
			adapter.onItemClick(view);
		}
		if (view.getId() == R.id.commit) {
			for(String s : MainActivity.activities){
				editor.putBoolean(s, sp_internal.getBoolean(s, true));
			}
			editor.commit();
			Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(mainIntent);
			overridePendingTransition(R.anim.toleftenter, R.anim.toleftleave);
			finish();
		} else if (view.getId() == R.id.back){
			Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
			mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(mainIntent);
			overridePendingTransition(R.anim.toleftenter, R.anim.toleftleave);
			finish();
		}
	}
	
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(R.anim.toleftenter, R.anim.toleftleave);
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
