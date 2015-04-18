package com.mygatech.tsquare;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;

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
import com.mygatech.LoginChecker;
import com.mygatech.LoginTask;
import com.mygatech.MainActivity;
import com.mygatech.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Showing menu (announcement, gradebook and etc)
 * @author Joseph
 *
 */
public class TsquareMenu extends Activity {
	public static final String MENU_LINK = "MenuLinks";
	private static final String TSQUARESUB = "tsquaresub.txt";
	private static final String TSQUARE = "tsquare.txt";
	private static final String dir = Environment.getExternalStorageDirectory() + "/MyGaTech/";
	private ListView listView;
	private StableArrayAdapter adapter;
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<String> urlList = new ArrayList<String>();
	private HashMap<String, String> urlMap = new HashMap<String, String>();
	protected static Queue<TsquareArrays> menuLink = new ArrayDeque<TsquareArrays>();
	private ArrayList<TsquareArrays> menuList = new ArrayList<TsquareArrays>();
	public SharedPreferences menuSp, subjectName;
	protected Editor menuEditor;
	private Typeface typeface;
	private MenuItem menuItem;
	private String privateTitle;


	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tsquare_class);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		typeface = Typeface.createFromAsset(getAssets(), "font.ttf");


		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder
				.createAppView()
				.set(Fields.SCREEN_NAME,
						"T-Square Menu " + getActionBar().getTitle()).build());
		privateTitle = getIntent().getExtras().getString("subjectName");

		
	}
	
	protected void onResume(){
		super.onResume();
		setTitle(privateTitle);

		Set<String> set = new HashSet<String>();
		nameList.clear();
		urlList.clear();
		menuList.clear();
			
		
		FileInputStream fis;
		BufferedReader buffer;
		try {
			fis = new FileInputStream(dir + TSQUARESUB);
			buffer = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			while ((line = buffer.readLine()) != null) {
				String[] lineArray = line.split(";", -1);
				TsquareArrays ts = new TsquareArrays(lineArray[0], lineArray[1],
						lineArray[2]);
				if (ts.getName().equalsIgnoreCase(getTitle().toString()) && 
						!menuList.contains(ts)){
					menuList.add(ts);
				}
			}
			fis.close();
		} catch (Throwable e) {
			e.getStackTrace();
		}
		
		for(Object obj: menuList){
			if(obj != null){
				TsquareArrays elem = (TsquareArrays)obj;
				if(elem.getSet().get("subject").equals(privateTitle)){
					set.add(elem.getBoard());
					urlList.add(elem.getLink());
					urlMap.put(elem.getBoard(), elem.getLink());
					Log.i("check", elem.getBoard() + " of " + elem.getLink() + " is added");
				}
			}
		}

		//To get rid of the duplicate board lists, set is used. 
		//Somehow contains method does not work :( 
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			nameList.add(it.next());
		}

		if (nameList.size() == 0) {
			((ViewFlipper)findViewById(R.id.tsquareFlipper)).showNext();
			((TextView)findViewById(R.id.txtnoicon)).setText("Nothing to show!\nGo to sleep nerds\n"+
			"My GaTech will let you know once you need to see something!");		
			((TextView)findViewById(R.id.txtnoicon)).setPadding(10, 5, 10, 5);
			((TextView)findViewById(R.id.txtnoicon)).setGravity(Gravity.CENTER_HORIZONTAL);
			((ImageView)findViewById(R.id.imagenoicon)).setImageResource(R.drawable.noicon);
			((ImageView)findViewById(R.id.imagenoicon)).setPadding(5, 5, 5, 5);
		} else {
			listView = (ListView) findViewById(R.id.tsquareListView);
			adapter = new StableArrayAdapter(this, R.layout.tsquare_row, nameList);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {
		private Context context;
		private List<String> Ids;
		private int rowResourceId;
		ArrayList<ImageView> imgV;
		ArrayList<AnimationDrawable> aniV;
		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
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
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(rowResourceId, parent, false);
			ImageView icon = (ImageView) rowView.findViewById(R.id.icon);
			Button button = (Button) rowView.findViewById(R.id.tsquareBtn);
			button.setText(nameList.get(position));
			button.setBackgroundColor(Color.WHITE);
			button.setTypeface(typeface);
			button.setTextSize(35);
			try {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				icon.setImageBitmap(TsquareMain
						.decodeSampledBitmapFromResource(getResources(),
								getIcon(nameList.get(position)), 100, 100));
			} catch (Exception e) {
				e.printStackTrace();
			} 
			final ImageView loading = (ImageView) rowView
					.findViewById(R.id.loading);
			loading.setBackgroundResource(R.anim.ani_spinner);
			loading.setVisibility(View.INVISIBLE);
			final AnimationDrawable frameAnimation = (AnimationDrawable) loading
					.getBackground();
			imgV.add(loading);
			aniV.add(frameAnimation);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					loading.setVisibility(View.VISIBLE);
					frameAnimation.start();
					overridePendingTransition(R.anim.fade, R.anim.hold);
					String board_name = nameList.get(position);
					if (board_name.equals("Piazza")) {
						PackageManager pk = getPackageManager();
						Intent intent;
						try {
							intent = pk
									.getLaunchIntentForPackage("com.piazza.android");
							if (intent == null)
								throw new PackageManager.NameNotFoundException();
							intent.addCategory(Intent.CATEGORY_LAUNCHER);
							startActivity(intent);
						} catch (PackageManager.NameNotFoundException e) {
							Intent mIntent = new Intent(Intent.ACTION_VIEW);
							mIntent.setData(Uri
									.parse("market://details?id=com.piazza.android"));
							startActivity(mIntent);
							finish();
						}
						finish();
					} else if (board_name.equals("Syllabus") || board_name.equals("Section Info")
							|| board_name.equals("Forums") || board_name.equals("Chat Room")
							|| board_name.equals("Wiki"))  {
						Intent intent = new Intent(getApplicationContext(), TsquareSubMenu .class);
						intent.putExtra("boardName", board_name);
						intent.putExtra("subjectName", getTitle());
						intent.putExtra("link", urlMap.get(board_name));
						startActivity(intent);
					}else {
						Intent intent = new Intent(getApplicationContext(),TsquareSubMenu.class);
						intent.putExtra("boardName", board_name);
						intent.putExtra("subjectName", getTitle());
						intent.putExtra("link", urlMap.get(board_name));
						startActivity(intent);
					}
				}

			});

			return rowView;
		}
	}
	
	
	private ArrayList<TsquareArrays> tsquareMenu;
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_load:
			menuItem = item;
			menuItem.setActionView(R.layout.progressbar);
			menuItem.expandActionView();
			menuSp = getSharedPreferences(TsquareMain.MENU_LIST,0);
			String JS = menuSp.getString("TsquareJS", null);
			String BIGIP = menuSp.getString("Bigip", null);
			String TICKET = menuSp.getString("Ticket", null);
			Toast.makeText(this, "Updating :) ", Toast.LENGTH_LONG).show();
			tsquareMenu = new ArrayList<TsquareArrays>();
			try {
				FileInputStream fis;
				BufferedReader buffer;
				try {
					fis = new FileInputStream(dir + TSQUARE);
					buffer = new BufferedReader(new InputStreamReader(fis));
					String line = "";
					while ((line = buffer.readLine()) != null) {
						String[] lineArray = line.split(";", -1);
						TsquareArrays ts = new TsquareArrays(lineArray[0], lineArray[1],
								lineArray[2], lineArray[3], lineArray[4], lineArray[5]);
						if (ts.getName().equalsIgnoreCase(getTitle().toString()) && !tsquareMenu.contains(ts)
								&& (ts.getBoard().equalsIgnoreCase("Announcements") 
									|| ts.getBoard().equalsIgnoreCase("Gradebook")
									|| ts.getBoard().equalsIgnoreCase("Assignments"))){
							tsquareMenu.add(ts);
						}
					}
					fis.close();
				} catch (Throwable e) {
					e.getStackTrace();
				}
				
				LoginCheck loginCheck = new LoginCheck(this,
								new URL("https://t-square.gatech.edu/portal/pda/gtc-aaa"),
								JS + "; " + BIGIP );
				if (loginCheck.execute("").get() < 0) 
					new LoginTaskExtended(getApplicationContext()).execute("");
				else
					Log.d("check", "already logged in ");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} finally {
				for(Map.Entry<String, String> s : urlMap.entrySet()){
					if (s.getKey().equalsIgnoreCase("Announcements") || s.getKey().equalsIgnoreCase("Gradebook") || 
						s.getKey().equalsIgnoreCase("Assignments")) {
						new Update().execute(s.getKey(), s.getValue(), TICKET, JS, BIGIP);
					}
				}
				if(menuItem != null) {
					menuItem.collapseActionView();
					menuItem.setActionView(null);
				}
			}
			break;
		case android.R.id.home:
			onBackPressed();
//			NavUtils.navigateUpFromSameTask(this);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
	
	private void notifyStatus(String name, String content){
		NotificationManager notificationManager =
    		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	Intent notifIntent = new Intent(getApplicationContext(), TsquareMenu.class);
    	PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(), 0, notifIntent, 0);

    	/*
    	 *	GOSU_DEPRECATED const Color 	none (0x00000000)
			GOSU_DEPRECATED const Color 	black (0xff000000)
			GOSU_DEPRECATED const Color 	gray (0xff808080)
			GOSU_DEPRECATED const Color 	white (0xffffffff)
			GOSU_DEPRECATED const Color 	aqua (0xff00ffff)
			GOSU_DEPRECATED const Color 	red (0xffff0000)
			GOSU_DEPRECATED const Color 	green (0xff00ff00)
			GOSU_DEPRECATED const Color 	blue (0xff0000ff)
			GOSU_DEPRECATED const Color 	yellow (0xffffff00)
			GOSU_DEPRECATED const Color 	fuchsia (0xffff00ff)
			GOSU_DEPRECATED const Color 	cyan (0xff00ffff) 
    	*/
    	long[] pattern = {1000, 200};
    	Notification notification = new Notification.Builder(getApplicationContext())
    									.setContentTitle(name)
    									.setContentIntent(pendingIntent)
    									.setContentText(content)
    									.setPriority(Thread.MAX_PRIORITY)
    									.setSmallIcon(R.drawable.tsquare_logo2)
    									.setLights(0xffffff00,1000,200)
    									.setVibrate(pattern)
    									.setAutoCancel(true)
    									.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
    									.build();
		notificationManager.notify((int) System.currentTimeMillis(), notification);
	}
	
	private class Update extends AsyncTask<String, Void, String> {
		private HttpsURLConnection connection;
		private String TICKET, JS, BIGIP;
		
		@Override
		protected String doInBackground(String... params) {
			String boardName = params[0];
			String boardLink = params[1];
			this.TICKET = params[2];
			this.JS = params[3];
			this.BIGIP = params[4];
			if (boardName.equalsIgnoreCase("Announcements")){
				for(TsquareArrays ts : getAnnouncements(boardLink)) {
					if (!tsquareMenu.contains(ts)){
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(dir + TSQUARE, true);
							Log.e("Written", ts.getName() + ts.getTitle());
							fos.write((ts.getName() + ";").getBytes() );
							fos.write((ts.getBoard()+ ";").getBytes());
							fos.write((ts.getTitle()+ ";").getBytes());
							fos.write((ts.getContent()+ ";").getBytes());
							fos.write((ts.getDate()+ ";").getBytes());
							fos.write((ts.getLink()+ ";").getBytes());
							fos.write(System.getProperty("line.separator").getBytes());
							fos.close();
							notifyStatus(ts.getName() + " " + ts.getBoard(), ts.getTitle());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			} else if (boardName.equalsIgnoreCase("Gradebook")) {
				for(TsquareArrays ts : getGradebook(boardLink)) {
					if (!tsquareMenu.contains(ts)){
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(dir + TSQUARE, true);
							Log.e("check", "Written" + ts.getName());
							fos.write((ts.getName() + ";").getBytes() );
							fos.write((ts.getBoard()+ ";").getBytes());
							fos.write((ts.getTitle()+ ";").getBytes());
							fos.write((ts.getContent()+ ";").getBytes());
							fos.write((ts.getDate()+ ";").getBytes());
							fos.write((ts.getLink()+ ";").getBytes());
							fos.write(System.getProperty("line.separator").getBytes());
							fos.close();
							notifyStatus(ts.getName() + " " + ts.getBoard(), ts.getTitle());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			} else if (boardName.equalsIgnoreCase("Assignments")) {
				for(TsquareArrays ts : getAssignment(boardLink)) {
					if (!tsquareMenu.contains(ts)){
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(dir + TSQUARE, true);
							Log.e("check", "Written" + ts.getName());
							fos.write((ts.getName() + ";").getBytes() );
							fos.write((ts.getBoard()+ ";").getBytes());
							fos.write((ts.getTitle()+ ";").getBytes());
							fos.write((ts.getContent()+ ";").getBytes());
							fos.write((ts.getDate()+ ";").getBytes());
							fos.write((ts.getLink()+ ";").getBytes());
							fos.write(System.getProperty("line.separator").getBytes());
							fos.close();
							notifyStatus(ts.getName() + " " + ts.getBoard(), ts.getTitle());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}
			Log.e(boardName, boardLink);
			Log.e("We are going to use", TICKET + " " + JS + " "+  BIGIP + " " );
			return null;
		}
		
		private ArrayList<TsquareArrays> getAssignment(String link) {
			ArrayList<TsquareArrays> curList = new ArrayList<TsquareArrays>();
			try{
				URL menuURL = new URL(link);
				connection = (HttpsURLConnection) menuURL.openConnection();
		        connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("GET");
		        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
				connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
				connection.setRequestProperty("Host", "t-square.gatech.edu");
				connection.setRequestProperty("DNT", "1");
		        connection.setRequestProperty("Referer", link);
		        connection.setRequestProperty("Cookie", JS+"; "+ BIGIP);
		        connection.setRequestProperty("Connection","Keep-Alive");
		        
		        if(connection.getResponseCode()== 302){
			        menuURL = new URL(connection.getHeaderField("Location"));
		        	
		        	connection = (HttpsURLConnection) menuURL.openConnection();
			        connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Transfer-Encoding", "chunked");
			        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
					connection.setRequestProperty("Host", "t-square.gatech.edu");
					connection.setRequestProperty("DNT", "1");
			        connection.setRequestProperty("Referer", "https://t-square.gatech.edu/portal/pda?ticket=ST-"+TICKET+"?force.login=yes");
			        connection.setRequestProperty("Cookie", JS+"; "+ BIGIP);
			        connection.setRequestProperty("Connection","Keep-Alive");
			       
			        BufferedReader in = new BufferedReader(
	                        new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer html = new StringBuffer();
					
					while ((inputLine = in.readLine()) != null) {
						html.append(inputLine);
					}
					in.close();
					
					Document menuDoc =  Jsoup.parse(html.toString());
			        Elements menuElems = menuDoc.select(jsoupParser("Assignments", privateTitle, 1));

					TsquareArrays temp = null;
					String date = null;
					int index = 0;
					String title = null;
					for(Element menuElem : menuElems){
						if(index%3==0){
							title = menuElem.select("a").text();
							temp = new TsquareArrays(title, menuElem.attr("href"));
						}else if(index%3 == 1){
							date = menuElem.select("td").text(); // Open
						}else{
							String content = menuElem.select("td").text(); // Due
							temp = new TsquareArrays(privateTitle, "Assignments", title, content, date, temp.getLink());
							curList.add(temp);
				        	Log.e("jsoupAss",">" + temp.getName() + temp.getTitle());// + ":" + temp.getLink());							
						}
						index++;
					}
					
		        }
			}catch(MalformedURLException  e){
				e.getStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return curList;
		}
		
		private ArrayList<TsquareArrays> getGradebook(String link) {
			ArrayList<TsquareArrays> curList = new ArrayList<TsquareArrays>();
			try{
				URL menuURL = new URL(link);
				connection = (HttpsURLConnection) menuURL.openConnection();
		        connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("GET");
		        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
				connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
				connection.setRequestProperty("Host", "t-square.gatech.edu");
				connection.setRequestProperty("DNT", "1");
		        connection.setRequestProperty("Referer", link);
		        connection.setRequestProperty("Cookie", JS+"; "+ BIGIP);
		        connection.setRequestProperty("Connection","Keep-Alive");
		        
		        if(connection.getResponseCode()== 302){
			        menuURL = new URL(connection.getHeaderField("Location"));
		        	menuURL = new URL(menuURL.toString()+"/studentView.jsf");
		        	
		        	connection = (HttpsURLConnection) menuURL.openConnection();
			        connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Transfer-Encoding", "chunked");
			        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
					connection.setRequestProperty("Host", "t-square.gatech.edu");
					connection.setRequestProperty("DNT", "1");
			        connection.setRequestProperty("Referer", "https://t-square.gatech.edu/portal/pda?ticket=ST-"+TICKET+"?force.login=yes");
			        connection.setRequestProperty("Cookie", JS+"; "+ BIGIP);
			        connection.setRequestProperty("Connection","Keep-Alive");
			       
			        BufferedReader in = new BufferedReader(
	                        new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer html = new StringBuffer();
					
					while ((inputLine = in.readLine()) != null) {
						html.append(inputLine);
					}
					in.close();
					
					Document menuDoc =  Jsoup.parse(html.toString());
			        Elements menuElems = menuDoc.select(jsoupParser("Gradebook", privateTitle, 1));

					for(Element menuElem : menuElems){
	    				TsquareArrays temp;
						if(menuElem.children().size()>=4 && menuElem.hasAttr("id")){
							String itemName = menuElem.select(".left").text();
							String itemDate = menuElem.select(".center").first().text();
							String itemGrade = "";
							if(menuElem.select(".center").get(1).hasText()){
								itemGrade = menuElem.select(".center").get(1).text();
							}
							if(itemDate.equals("-")){
								String dateTemp = "(No date)";
								itemDate = dateTemp;
							}
							if(itemGrade.equals("-")){
								String gradeTemp = "(No grade)";
								itemGrade = gradeTemp;
							}
							//else{
							//	itemGrade = link.select(".center").get(1).select(".Not counted towards course grade").text();
							//}
							temp = new TsquareArrays(privateTitle, "Gradebook", itemName, itemGrade, itemDate, "NaN");
							curList.add(temp);
							Log.e("jsoupGra", itemName + " " + itemDate + " " + itemGrade);
							
						}
					}
					
		        }
			}catch(MalformedURLException  e){
				e.getStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return curList;
		}
		
		private ArrayList<TsquareArrays> getAnnouncements(String link) {
			ArrayList<TsquareArrays> curList = new ArrayList<TsquareArrays>();
			try{
				URL menuURL = new URL(link);
				connection = (HttpsURLConnection) menuURL.openConnection();
		        connection.setInstanceFollowRedirects(false);
				connection.setRequestMethod("GET");
		        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
				connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
				connection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
				connection.setRequestProperty("Host", "t-square.gatech.edu");
				connection.setRequestProperty("DNT", "1");
		        connection.setRequestProperty("Referer", link);
		        connection.setRequestProperty("Cookie", JS+"; "+ BIGIP);
		        connection.setRequestProperty("Connection","Keep-Alive");
		        
		        if(connection.getResponseCode()== 302){
		        	menuURL = new URL(connection.getHeaderField("Location"));
		        	menuURL = new URL(menuURL.toString()+"?panel=Main");		        
			        connection = (HttpsURLConnection) menuURL.openConnection();
			        connection.setInstanceFollowRedirects(false);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Transfer-Encoding", "chunked");
			        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					connection.setRequestProperty("Accept-Language", "en-US;q=0.6,en;q=0.4");
					connection.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");
					connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
					connection.setRequestProperty("Host", "t-square.gatech.edu");
					connection.setRequestProperty("DNT", "1");
			        connection.setRequestProperty("Referer", "https://t-square.gatech.edu/portal/pda?ticket=ST-"+TICKET+"?force.login=yes");
			        connection.setRequestProperty("Cookie", JS+"; "+ BIGIP);
			        connection.setRequestProperty("Connection","Keep-Alive");
			       
			        BufferedReader in = new BufferedReader(
	                        new InputStreamReader(connection.getInputStream()));
					String inputLine;
					StringBuffer html = new StringBuffer();
					
					while ((inputLine = in.readLine()) != null) {
						html.append(inputLine);
					}
					in.close();
					
					Document menuDoc = Jsoup.parse(html.toString());
					Elements menuElems = menuDoc.select(jsoupParser("announcements", privateTitle ,1));
					String stemp = null;
					TsquareArrays temp = null;
	 				int index = 0;
					for (Element menuElem : menuElems) {
						if (index % 2 == 0) {
							if (menuElem.attr("title").contains("View announcement")) 
								stemp = menuElem.attr("title").substring(18);
	
							if (stemp == null) 
								temp = new TsquareArrays(privateTitle,"Announcements",
										menuElem.attr("title"),menuElem.attr("href"));
							else 
								temp = new TsquareArrays(privateTitle,
										"Announcements", stemp,menuElem.attr("href"));
						} else {
							// curTemp; title; content; date
							String date = menuElem.select("td[headers=date]").text();
							temp = new TsquareArrays(temp.getName(),temp.getBoard(),
									temp.getTitle(), "NaN", date, temp.getLink());
							curList.add(temp);
						}
						index++;
	//					Log.e("jsoupAnn",">" + temp.getName() + " for " + temp.getBoard()
	//									+ ": " + temp.getTitle());// + ":" +
	//																// temp.getLink());
					}
		        }
				
			}catch(MalformedURLException  e){
				e.getStackTrace();
			} catch (ProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return curList;
		}
			
	private String jsoupParser(String titleName, String prev, int step) {
			if (titleName.toLowerCase(Locale.US).equals("announcements")) {
				Log.e("jsoup", "announcement lists");
				return "h4 a[href], td[headers=date]";
			} else if (titleName.equals("Gradebook")) {
				return "tr";
			} else if (titleName.toLowerCase(Locale.US).contains("assignments")) {
				return "td[headers=openDate], td[headers=dueDate], h4 a[href]";
			} else if (titleName.toLowerCase(Locale.US).contains("piazza")) {
				return "iframe";
			}
			return null;
		}
		

	};
	
	private int getIcon(String boardName) {
		boardName = boardName.toLowerCase(Locale.US);
		String temp = boardName.replaceAll("\\s","");
		boardName = temp;
		int icon = getResources().getIdentifier(
				boardName.toLowerCase(Locale.getDefault()) + "icon",
				"drawable", this.getPackageName());
		if(icon == 0x00000000)
			Log.d("boardName", boardName);
		return icon;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tsquare_menu, menu);
		return true;
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
	
	private class LoginCheck extends LoginChecker{
		public LoginCheck(Context context, URL privateLink, String cookie) {
			super(context, privateLink, cookie);
		}
		protected void onPostExecute(Integer result) {}
	}
	
	private class LoginTaskExtended extends LoginTask{
			public LoginTaskExtended(Context context) {
				super(context);
			}
	
			protected void onPostExecute(String result) {}
	}
}
