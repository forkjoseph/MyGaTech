package com.mygatech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity implements
		StartResultReceiver.Receiver {
	public static final String TSQUARE_LOAD = "com.mygatech.TSQUARE_LOAD";
	private static String POSTURL = "http://tomcatjndi-mygatech.rhcloud.com/postservlet";
	private ProgressDialog pDialog;
	private EditText user, pass;
	private SharedPreferences sp;
	private Editor editor;
	private StartResultReceiver mReceiver;
	private ImageView loading;
	private AnimationDrawable frameAnimation;
	private LocalBroadcastManager bm;
	private ScrollView sv;
	private boolean hasValue;
	private String mPhoneNumber, mEmail, mVersion;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_main);
		
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "Start Screen").build());

		sv = (ScrollView) findViewById(R.id.sv);
		sp = getSharedPreferences(MainActivity.USER_DETAILS, MODE_PRIVATE);
		editor = sp.edit();
		editor.putBoolean("startActivityInUse", true);
		editor.commit();
		hasValue = sp.getBoolean("hasLoggedIn", false);
		user = (EditText) findViewById(R.id.username);
		pass = (EditText) findViewById(R.id.password);
		if (hasValue) {
			user.setText(sp.getString("username", null));
			setTitle("Change Username || Password");
		}
		mReceiver = new StartResultReceiver(new Handler());
		mReceiver.setReceiver(this);
		loading = (ImageView) findViewById(R.id.loading);
		loading.setBackgroundResource(R.anim.ani_spinner);
		loading.setVisibility(View.INVISIBLE);
		frameAnimation = (AnimationDrawable) loading.getBackground();
		// No soft keyboard pops up at onCreate
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		PackageInfo pInfo = null;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mVersion = pInfo.versionName;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
//	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//	    	return true;
//	    }

	    return super.onKeyDown(keyCode, event);
	}
	
	public void onBackPressed() {
		Log.d("GOING", "BACK");
		if (sp.getBoolean("startActivityInUse", true)){
			super.onBackPressed();
			overridePendingTransition(R.anim.torightenter, R.anim.torightleave);
		}else if (hasValue && sp.getBoolean("startActivityInUse", true)) {
			overridePendingTransition(R.anim.torightenter, R.anim.torightleave);
			super.onBackPressed();
			finish();
		}
		
	}

	public void onStart() {
		super.onStart();
		sv.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						int heightDiff = sv.getRootView().getHeight()
								- sv.getHeight();
						if (heightDiff > 150) { // if more than 150 pixels, it's
												// a keyboard !!
							// scroll and show login button
							sv.smoothScrollTo(0, sv.getChildAt(0).getBottom());
						}
					}
				});
		EasyTracker.getInstance(this).activityStart(this); // Add this method.
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStop(this); // Add this method.
	}

	public void onCheckBoxClicked(View view) {
		switch (view.getId()) {
//		case R.id.admin:
//			boolean checked = ((CheckBox) view).isChecked();
//			if (checked) {
//				user.setText("forkjoseph");
//			} else {
//				user.setText("");
//				pass.setText("");
//			}
//			break;

		case R.id.setPassVisi:
			boolean setVisi = ((CheckBox) view).isChecked();
			if (setVisi) {
				pass.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			} else {
				pass.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			break;

		case R.id.login:
			ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetworkInfo = connectivityManager
					.getActiveNetworkInfo();
//			if (user.getText().toString().equalsIgnoreCase("debug")){
//				finish();
//				editor.putBoolean("hasLoggedIn", true);
//				editor.commit();
//			}
			if (user.getText().toString().equals("")
					|| pass.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(),
						"Username or Password cannot be left blank.",
						Toast.LENGTH_LONG).show();
				break;
			} else if (activeNetworkInfo == null
					|| !activeNetworkInfo.isConnected()) {
				Toast.makeText(
						getApplicationContext(),
						"My GaTech requires mobile network.\nWI-FI is recommended to use.",
						Toast.LENGTH_LONG).show();
			} else if (!hasValue) {
				
				LayoutInflater inflator = LayoutInflater.from(this);
				View agree_view = inflator.inflate(R.layout.user_agree_dialog, null);
				ClickableSpan tos = new ClickableSpan(){

					@Override
					public void onClick(View widget) {
						final Resources resources = getResources();
			        	InputStream is = resources.openRawResource(R.raw.tos);
			        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
			        	StringBuilder tos = new StringBuilder();
			        	try {
			        		String line;
			                 while ((line = br.readLine()) != null) {
			                     tos.append(line);
			                     tos.append(System.getProperty("line.separator"));
			                 }
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} finally {
							try {
								br.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
			        	
						new AlertDialog.Builder(widget.getContext()).setTitle("Terms of service").setMessage(tos.toString())
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						}).show();

					}
					
				};
				
				TextView agree_tv = (TextView)agree_view.findViewById(R.id.tosIntro);
				final CheckBox agree_cb = (CheckBox)agree_view.findViewById(R.id.tosCB);
				agree_tv.setMovementMethod(LinkMovementMethod.getInstance());
				String CONTENT = "		MyGaTech version " + mVersion + "\n" + "		MyGaTech team is not officially affiliated with Georgia Institute of Technology.\n";
				String AGREE_FRONT = "		By using the service and reading the ";
				String AGREE_TOS = "terms of service";
				String AGREE_BACK  = ", he/she agrees to the terms of service.";
				int start = CONTENT.length()+AGREE_FRONT.length();
				int end = CONTENT.length()+AGREE_FRONT.length()+AGREE_TOS.length();
				
				Spannable span = new SpannableString(CONTENT + AGREE_FRONT + AGREE_TOS + AGREE_BACK);
				span.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, 0);
				span.setSpan(tos, start, end, 0);
				span.setSpan(new UnderlineSpan(), start, end, 0);
				agree_tv.setText(span);
				
				new AlertDialog.Builder(this)
				.setView(agree_view)
				.setTitle("User Agreement")
				.setPositiveButton("Continue",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								if(!agree_cb.isChecked()){
									new AlertDialog.Builder(StartActivity.this)
									.setMessage("You have to agree the terms of service to continue.")
									.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface d,int which) {
											d.dismiss();
										}
									}).show();
								}else{
									dialog.cancel();
									loading.setVisibility(View.VISIBLE);
									frameAnimation.start();
									Intent intent = new Intent(StartActivity.this, StartService.class);
									intent.putExtra("receiverTag", mReceiver);
									intent.putExtra("username", user.getText().toString());
									intent.putExtra("password", pass.getText().toString());
									startService(intent);
								}
							}
						}).show();
			} else {
				loading.setVisibility(View.VISIBLE);
				frameAnimation.start();
				Intent intent = new Intent(this, StartService.class);
				intent.putExtra("receiverTag", mReceiver);
				intent.putExtra("username", user.getText().toString());
				intent.putExtra("password", pass.getText().toString());
				startService(intent);
			}
			break;
		}
	}

	@Override
	public void onReceiveResult(int resultCode, Bundle resultBundle) {
		boolean correct = resultBundle.getBoolean("ServiceTag");
		frameAnimation.stop();
		loading.setVisibility(View.INVISIBLE);
		if (correct) {
			
			Toast msg = Toast
					.makeText(
							getBaseContext(),
							"Credential checked\nWelcome to My GaTech!\nYou are AWESOME!!!!",
							Toast.LENGTH_LONG);
			msg.show();
			editor.clear().commit();
			editor.putString("username", user.getText().toString());
			editor.putString("password", pass.getText().toString());
			editor.putBoolean("hasLoggedIn", true);
			editor.putBoolean("Student", true);
			editor.commit();
			
			setPersonalInfo();
			if (!hasValue) {
				new PostToServer().execute();
			}
//			else {
//				Intent openMainActivity = new Intent(StartActivity.this, MainActivity.class);
//				openMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//				startActivity(openMainActivity);
//				overridePendingTransition(R.anim.torightenter,R.anim.torightleave);
//			}

			bm = LocalBroadcastManager.getInstance(this);
			IntentFilter filter = new IntentFilter();
			filter.addAction(TSQUARE_LOAD);
			final ProgressDialog pd = new ProgressDialog(StartActivity.this);
			pd.setTitle("Updating T-Square");
			pd.setMessage("Loading... 0%");
			pd.setCanceledOnTouchOutside(false);
			pd.show();

			BroadcastReceiver progressReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					int progress = intent.getExtras().getInt("percent");
					// range 0 to 10000
					if (progress == 10000) {
						Intent openMainActivity = new Intent(
								StartActivity.this, MainActivity.class);
						openMainActivity
								.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						pd.cancel();
						startActivity(openMainActivity);
						overridePendingTransition(R.anim.torightenter,
								R.anim.torightleave);
//						startService(new Intent(StartActivity.this,
//								MainService.class));
						finish();
					} else {
						pd.setMessage("Loading... " + progress / 100 + "%");
						pd.setProgress(progress);
					}
				}
			};
			bm.registerReceiver(progressReceiver, filter);
			startService(new Intent(StartActivity.this,
					FirstTimeTsquareService.class));
			editor.putBoolean("startActivityInUse", false);
			editor.commit();
		} else {
			Toast.makeText(
					getBaseContext(),
					"Username or password is invalid.\nPlease check carefully :->",
					Toast.LENGTH_LONG).show();
		}
	}

	private class PostToServer extends AsyncTask<Void, Void, Void> {
		protected void onPreExecute() {
			super.onPreExecute();
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			// Showing progress dialog
			pDialog = new ProgressDialog(StartActivity.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();
			List<NameValuePair> np = new ArrayList<NameValuePair>();
			np.add(new BasicNameValuePair("username", user.getText().toString()));
			np.add(new BasicNameValuePair("pin", "000000"));
			np.add(new BasicNameValuePair("email", mEmail));
			np.add(new BasicNameValuePair("phone_num", mPhoneNumber));
			String jsonStr = sh.makeServiceCall(POSTURL, ServiceHandler.POST,
					np);
			if (jsonStr.equals("200"))
				Log.d("Server: ", "Okay!");
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			Intent openMainActivity = new Intent(StartActivity.this, MainActivity.class);
			openMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(openMainActivity);
			overridePendingTransition(R.anim.torightenter,R.anim.torightleave);
		}
	}

	public void setPersonalInfo() {
		// Get the phone number !
		TelephonyManager tMgr = (TelephonyManager) getApplicationContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneNumber = tMgr.getLine1Number();

		// Get the all emails , Select gmail first , If it does not -> select
		// first email
		// Else set to username@gatech.edu
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getApplicationContext())
				.getAccounts();
		mEmail = "";
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
	}
	
	

}
