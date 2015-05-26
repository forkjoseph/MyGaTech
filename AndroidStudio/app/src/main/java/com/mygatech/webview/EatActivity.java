package com.mygatech.webview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mygatech.MainActivity;
import com.mygatech.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;
import android.view.MenuItem;
import android.view.View;

public class EatActivity extends Activity implements OnScrollListener{
	private ActionBar actionbar;
	private ListView listView;
	private Button nowButton;
	private TimePickerFragment timepicker;
	private StableAdapter adapter;
	private int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;

	public static RestDatabaseHandler db;
	private ArrayList<Restaurant> restList;

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
	
	public boolean todayExceptionCheck() throws IOException {
		Calendar c = Calendar.getInstance();
		int month = 1 + c.get(Calendar.MONTH); // Jan = 0 , ... , Dec = 11
		int day = c.get(Calendar.DAY_OF_MONTH);
		return checkForException (month, day);
	}
	
	public boolean checkForException (int month, int day) throws IOException {
		String today = "";
		if (day < 10) 
			today = month + ".0" + day;
		else
			today = month + "." + day;
		final Resources resources = getResources();
		InputStream is = resources.openRawResource(R.raw.eat_exception_data);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("/**")) {
					line = line.substring(3);
					ArrayList<String> s = new ArrayList<String> ();
					s.addAll(Arrays.asList(TextUtils.split(line, ",")));
					Log.d("Today is ", today);
					if (s.contains(today))
						return true;
				}
			}
		} finally {
			br.close();
		}
		return false;
	}
//	Fuck the exception date I'm not going to handle it now :( 
//	private ArrayList<Restaurant> getExcRestTime(int month, int day, double combiedHour) throws IOException {
//		ArrayList<Restaurant> result = loadExceptionData(month, day);
//		
//	}
//	private ArrayList<Restaurant> getExcRestNow() {
//		
//		
//	} 
	
	private ArrayList<Restaurant> loadExceptionData(int month, int day) throws IOException {
		String givenDate = "";
		if (day < 10) 
			givenDate = month + ".0" + day;
		else
			givenDate = month + "." + day;
		final Resources resources = getResources();
		InputStream is = resources.openRawResource(R.raw.eat_exception_data);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		ArrayList<Restaurant> result = new ArrayList<Restaurant>();
		try {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.startsWith("/")) {
					String[] s = TextUtils.split(line, ",");
					double parsedDate = Double.parseDouble(s[4].trim());
					if (s.length == 5 && parsedDate == Double.parseDouble(givenDate))  {
						Restaurant r = new Restaurant(s[0], s[1].trim(), s[2].trim(),
								Integer.parseInt(s[3].trim()), Double.parseDouble(s[4].trim()));
						result.add(r);
						Log.d("Rest", r.exceptionDebug());
					} else if (parsedDate > Double.parseDouble(givenDate))
						break;
				}
			}
			if (result.size() == 0) 
				result.add(new Restaurant("closed"));
		} finally {
			br.close();
		}
		return result;
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.eat_main);
		actionbar = getActionBar();
		listView = (ListView) findViewById(R.id.restListView);
		nowButton = (Button) findViewById(R.id.now);
		actionbar.setTitle("Where To Eat Today");
		nowButton.setText("Now");

		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Where To Eat").build());
	   
		db = new RestDatabaseHandler(this);
		if (db.getRestCount() == 0)
			dbManage(db);
//		try {
//			if (todayExceptionCheck()) {
//				Log.i("Today is ", "Exception date");
//				Calendar c = Calendar.getInstance();
//				int month = 1 + c.get(Calendar.MONTH); 
//				int day = c.get(Calendar.DAY_OF_MONTH);
//				restList = loadExceptionData(month, day);
//			} else { 
				restList = db.getRestListOpenToday();
				if (restList.size() == 0 )
					restList.add(new Restaurant("closed"));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		adapter = new StableAdapter(this, R.layout.eat_row, restList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setOnItemClickListener(adapter);
		listView.setClipToPadding(false);
		listView.setOnScrollListener(this);
		
//		listView.setScrollbarFadingEnabled(true); // show scroll always
					// Scroll appear and disappear
					/*
					 * list.setOnScrollListener(new OnScrollListener() { int
					 * mLastFirstVisibleItem = 0;
					 * 
					 * @Override public void onScroll(AbsListView view, int
					 * firstVisibleItem, int visibleItemCount, int totalItemCount) {
					 * Check if the last view is visible
					 * if(list.getFirstVisiblePosition() == 0){
					 * adapter.notifyDataSetChanged(); getActionBar().show(); } else{
					 * view.animate().setDuration(500).alpha(0).withStartAction(new
					 * Runnable(){ public void run(){ adapter.notifyDataSetChanged();
					 * getActionBar().hide(); } }); } if (++firstVisibleItem >
					 * totalItemCount) { Log.e("check", "A: "+firstVisibleItem);
					 * Log.e("check", "B: "+totalItemCount); getActionBar().hide();
					 * }else{ Log.e("check", "A: "+firstVisibleItem); Log.e("check",
					 * "B: "+totalItemCount); getActionBar().show(); } }
					 * 
					 * @Override public void onScrollStateChanged(AbsListView view, int
					 * scrollState) { final ListView lw = list;
					 * 
					 * if(scrollState == 0) Log.i("check", "scrolling stopped...");
					 * boolean mIsScrollingUp = false;
					 * 
					 * if (view.getId() == lw.getId()) { final int
					 * currentFirstVisibleItem = lw.getFirstVisiblePosition(); if
					 * (currentFirstVisibleItem > mLastFirstVisibleItem) {
					 * mIsScrollingUp = false; Log.i("check", "scrolling down..."); }
					 * else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
					 * mIsScrollingUp = true; Log.i("check", "scrolling up..."); }
					 * mLastFirstVisibleItem = currentFirstVisibleItem; } }
					 * 
					 * });
					 */
	}
	
	public void onNow(View view) throws IOException{
//		if (!todayExceptionCheck()) {
			if (nowButton.getText().equals("Now")){
				restList = db.getRestListOpenNow();
				if (restList.size() == 0 )
					restList.add(new Restaurant("closed"));
				nowButton.setText("Today");
				actionbar.setTitle("Where To Eat Now");
			} else if (nowButton.getText().equals("Today")){
				restList = db.getRestListOpenToday();
				if (restList.size() == 0 )
					restList.add(new Restaurant("closed"));
				actionbar.setTitle("Where To Eat Today");
				nowButton.setText("Now");
			}
//		} else {
//			if (nowButton.getText().equals("Now")){
//				restList = db.getRestListOpenNow();
//				if (restList.size() == 0 )
//					restList.add(new Restaurant("closed"));
//				nowButton.setText("Today");
//				actionbar.setTitle("Where To Eat Now");
//			} else if (nowButton.getText().equals("Today")){
//				restList = db.getRestListOpenToday();
//				if (restList.size() == 0 )
//					restList.add(new Restaurant("closed"));
//				actionbar.setTitle("Where To Eat Today");
//				nowButton.setText("Now");
//			}
//		}
		
		adapter = new StableAdapter(this, R.layout.eat_row, restList);
		listView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		listView.setOnItemClickListener(adapter);
	}
	
	public class TimePickerFragment extends Dialog implements View.OnClickListener{
		private int year, month, day, hour, minute;
		private TimePicker timePick;
		private DatePicker datePick;
		
		public TimePickerFragment(Context context) {
			super(context);
			super.setContentView(R.layout.custom_time);
			super.setTitle("Set Custom Time");
			super.setCancelable(true);
			super.setCanceledOnTouchOutside(false);
			Button ok = (Button) super.findViewById(R.id.OK);
			ok.setOnClickListener(this);
			Button cancel = (Button) super.findViewById(R.id.cancel);
			cancel.setOnClickListener(this);
			timePick = (TimePicker) findViewById(R.id.timePicker1);
			datePick = (DatePicker) findViewById(R.id.datePicker1);
		}
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.OK:
				this.year = datePick.getYear();
				this.month = datePick.getMonth();
				this.day = datePick.getDayOfMonth();
				this.hour = timePick.getCurrentHour();
				this.minute = timePick.getCurrentMinute();
				Calendar c = Calendar.getInstance();
				c.clear();
				c.set(year, month, day);
				int date = c.get(Calendar.DAY_OF_WEEK);
				++month;
				try {
//					if (checkForException(month, day)) {
						Log.i(month + "." + day + " is ", "Exception date");
//					} else { 
						double combined = hour + (((double)minute)/100);
						restList = db.getRestListByTime(combined, date);
						if (restList.size() == 0 )
							restList.add(new Restaurant("closed"));
						adapter = new StableAdapter(getBaseContext(), R.layout.eat_row, restList);
						listView.setAdapter(adapter);
						adapter.notifyDataSetChanged();
						listView.setOnItemClickListener(adapter);
						actionbar.setTitle("Atl. Time for " + year + "." 
								+ month + "." + day + ", " + hour + ":" + minute);
						dismiss();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
				} finally {
					dismiss();
				}
				break;
			case R.id.cancel:
				dismiss();
			}
		}
	}
	public void setTime(View v){
		switch(v.getId()){
		case R.id.CTime:
			timepicker = new TimePickerFragment(v.getContext());
			timepicker.show();
			break;
		}
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
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE)
            return;
		for (int i=0; i < totalItemCount; i++) {
			View listItem = view.getChildAt(i);
			if (listItem == null)
                break;
			// set the margin.
//            ((ViewGroup.MarginLayoutParams) title.getLayoutParams()).topMargin = topMargin;
//			listItem.requestLayout();
//			Log.d("Requestion", i + " " + listItem.getVisibility());
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mScrollState = scrollState;
	}
}