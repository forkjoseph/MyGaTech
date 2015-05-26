package com.mygatech;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.mygatech.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class FeedbackActivity extends FragmentActivity  implements ActionBar.TabListener {
	private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private static ArrayDeque<PostToServer> pt;
    private String[] tabs = { "Suggestion", "Error", "Contribute"};
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.feedback_main);
		viewPager = (ViewPager) findViewById(R.id.pager);
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
		pt = new ArrayDeque<PostToServer>();
		for(int i = 0; i < 10; i ++){
			pt.add(new PostToServer());
		}
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "FeedBack Screen").build());
 
        viewPager.setAdapter(mAdapter);
        actionBar = getActionBar();
        actionBar.setTitle("For better My GaTech");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
         
            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }
         
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
         
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
	}
	

	private class PostToServer extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;
		private Builder alertDialog;
		private final String FEEDBACKPOST = "https://tomcatjndi-mygatech.rhcloud.com/feedbackservlet";
		private String that;
		protected void onPreExecute() {
			super.onPreExecute();
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			pDialog = new ProgressDialog(FeedbackActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
            alertDialog = new AlertDialog.Builder(FeedbackActivity.this);  

		}

		@Override
		protected String doInBackground(String... arg0) {
			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> np = getPersonalInfo();
			np.add(new BasicNameValuePair("menu", arg0[0]));
			np.add(new BasicNameValuePair("feedback", arg0[1]));
			np.add(new BasicNameValuePair("anonymous", arg0[2]));
			np.add(new BasicNameValuePair("from", arg0[3]));
			that = arg0[3];
			String jsonStr = sh.makeServiceCall(FEEDBACKPOST, ServiceHandler.POST,
					np);
			Log.d("Got", jsonStr + "");
			return jsonStr;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pDialog.isShowing())
				pDialog.dismiss();
			if(result.contains("200")){
				if(that.equals("Suggestion")){
				alertDialog.setMessage("Thank you for your contribution")
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			       
			        }
			     }).show();
				}else if (that.equals("Error")){
					alertDialog.setMessage("Thank you for your contribution\nOnce we resolve the error, we will let you know")
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				       
				        }
				     }).show();
				}else if (that.equals("Contribute")){
					alertDialog.setMessage("Thank you for your contribution\nWe will contact you :)")
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				       
				        }
				     }).show();
				}
			}else{
				alertDialog.setMessage("Server is temporarily unavaliable.\nPlease try again.")
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			       
			        }
			     }).show();
			}
			pt.remove();
		}
	}
	
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	
	public  class TabsPagerAdapter extends FragmentPagerAdapter {
	    public TabsPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }
	 
	    @Override
	    public Fragment getItem(int index) {
	        switch (index) {
	        case 0:
	            return new Tab1();
	        case 1:
	        	return new Tab2();
	        case 2:
	        	return new Tab3();
	        }
	 
	        return null;
	    }
	 
	    @Override
	    public int getCount() {
	        return 3;
	    }
	}
	
	public static class Tab1 extends Fragment implements OnItemSelectedListener, TextWatcher, View.OnClickListener{
		private TextView wordLimit;
		private EditText feedback;
		private View rootView;
		private Spinner spinner;
		private CheckBox anony;
		private String menu;
		
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	super.onCreateView(inflater, container, savedInstanceState);
	        rootView = inflater.inflate(R.layout.feedback_tab1, container, false);
	        
	        spinner = (Spinner) rootView.findViewById(R.id.spinner);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
	                R.array.menus, R.layout.spinner_dropdown_item);
	        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(this);
	        
	        wordLimit = (TextView) rootView.findViewById(R.id.wordLimit);
	        wordLimit.setText(String.valueOf(500));
	        
	        feedback = (EditText)rootView.findViewById(R.id.feedback);
	        feedback.setBackgroundResource(R.drawable.backtext);
	        feedback.addTextChangedListener(this);
	        feedback.setPadding(5, 0, 5,0);
	        
	        Button send = (Button) rootView.findViewById(R.id.send);
	        send.setOnClickListener(this);
	        
	        anony = (CheckBox) rootView.findViewById(R.id.anony);	        
	        return rootView;
	    }

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	         menu = parent.getItemAtPosition(pos).toString();			
		}

		public void afterTextChanged(Editable s) {
			// Nothing
			int length = 500 - s.length();
			wordLimit.setText(String.valueOf(length));
		}

		public void onClick(View v) {
			if(!pt.isEmpty())
				pt.peekFirst().execute(menu, feedback.getText().toString(), String.valueOf(anony.isChecked()), "Suggestion");
			else{
				AlertDialog.Builder onBackBuilder = new AlertDialog.Builder(getActivity());
				onBackBuilder
			    .setMessage("Cannot post more than 10 feedback at a time.")
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			       
			        }
			     }).show();
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
	
	
	
	public static class Tab2 extends Fragment implements OnItemSelectedListener, TextWatcher, View.OnClickListener {
		private TextView wordLimit, replicate;
		private EditText feedback;
		private View rootView;
		private Spinner spinner;
		private String menu;
		
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	    	super.onCreateView(inflater, container, savedInstanceState);
	        rootView = inflater.inflate(R.layout.feedback_tab2, container, false);
	        
	        spinner = (Spinner) rootView.findViewById(R.id.spinner);
	        
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
	                R.array.menus, R.layout.spinner_dropdown_item_small_text);
	        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(this);
	        
	        
	        wordLimit = (TextView) rootView.findViewById(R.id.wordLimit);
	        wordLimit.setText(String.valueOf(500));
	        
	        feedback = (EditText)rootView.findViewById(R.id.feedback);
	        feedback.setBackgroundResource(R.drawable.backtext);
	        feedback.addTextChangedListener(this);
	        feedback.setPadding(5, 0, 5,0);
	        
	        Button send = (Button) rootView.findViewById(R.id.send);
	        send.setOnClickListener(this);
	        String error = "the ERROR";
	        SpannableString spString = new SpannableString("Please include steps to replicate " + error);
	        spString.setSpan(new RelativeSizeSpan(0.8f), 0, spString.length() - error.length(), 0);
			spString.setSpan(new UnderlineSpan(), spString.length() - error.length(), spString.length(), 0);
			spString.setSpan(new RelativeSizeSpan(1.3f), spString.length() - error.length(), spString.length(), 0);
			spString.setSpan(new ForegroundColorSpan(Color.RED), spString.length() - error.length(), spString.length(), 0);
	        replicate = (TextView) rootView.findViewById(R.id.replicate);
	        replicate.setText(spString);
	        
	        return rootView;
	    }

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	         menu = parent.getItemAtPosition(pos).toString();			
		}

		public void afterTextChanged(Editable s) {
			// Nothing
			int length = 500 - s.length();
			wordLimit.setText(String.valueOf(length));
		}

		public void onClick(View v) {
			if(!pt.isEmpty())
				pt.peekFirst().execute(menu, feedback.getText().toString(), "false", "Error");
			else{
				AlertDialog.Builder onBackBuilder = new AlertDialog.Builder(getActivity());
				onBackBuilder
			    .setMessage("Cannot post more than 10 feedback at a time.")
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			       
			        }
			     }).show();
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
	
	public static class Tab3 extends Fragment implements OnItemSelectedListener, TextWatcher, View.OnClickListener{
		private TextView wordLimit, replicate;
		private EditText feedback;
		private Spinner spinner;
		private String menu;
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        View rootView = inflater.inflate(R.layout.feedback_tab3, container, false);
	        
	        spinner = (Spinner) rootView.findViewById(R.id.spinner);
	        
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
	                R.array.contribute, R.layout.spinner_dropdown_item);
	        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(this);
	        
	        
	        wordLimit = (TextView) rootView.findViewById(R.id.wordLimit);
	        wordLimit.setText(String.valueOf(500));
	        
	        feedback = (EditText)rootView.findViewById(R.id.feedback);
	        feedback.setBackgroundResource(R.drawable.backtext);
	        feedback.addTextChangedListener(this);
	        feedback.setPadding(5, 0, 5,0);
	        
	        replicate = (TextView) rootView.findViewById(R.id.expert);
	        replicate.setText("Expertise:");
	        
	        Button send = (Button) rootView.findViewById(R.id.send);
	        send.setOnClickListener(this);
	        return rootView;
	    }
		
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, 
	            int pos, long id) {
	         menu = parent.getItemAtPosition(pos).toString();			
		}

		public void afterTextChanged(Editable s) {
			// Nothing
			int length = 500 - s.length();
			wordLimit.setText(String.valueOf(length));
		}

		public void onClick(View v) {
			Log.d("check", "AAAA");
			if(!pt.isEmpty())
				pt.peekFirst().execute(menu, feedback.getText().toString(), "false", "Contribute");
			else{
				AlertDialog.Builder onBackBuilder = new AlertDialog.Builder(getActivity());
				onBackBuilder
			    .setMessage("Cannot post more than 10 feedback at a time.")
			    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			       
			        }
			     }).show();
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {}
		public void onNothingSelected(AdapterView<?> arg0) {}
	}
	
	public List<NameValuePair> getPersonalInfo() {
		TelephonyManager tMgr = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mPhoneNumber = tMgr.getLine1Number();
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getApplicationContext())
				.getAccounts();
		String mEmail = "";
		boolean found = false;
		for (Account account : accounts) {
			if (emailPattern.matcher(account.name).matches()) {
				String possibleEmail = account.name;
				if (possibleEmail.contains("@gmail.com")) {
					mEmail = possibleEmail;
					found = true;
				} else if (!found)
					mEmail = possibleEmail;
			}
		}
		List<NameValuePair> np = new ArrayList<NameValuePair>();
		np.add(new BasicNameValuePair("username", 
				getSharedPreferences(MainActivity.USER_DETAILS, MODE_PRIVATE).getString("username", null)));
		np.add(new BasicNameValuePair("email", mEmail));
		np.add(new BasicNameValuePair("phone_num", mPhoneNumber));
		return np;
	}
	
	public void onDestroy(){
		super.onDestroy();
		for(PostToServer p : pt){
			if(p != null && p.pDialog != null && p.pDialog.isShowing())
				p.pDialog.dismiss();
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
