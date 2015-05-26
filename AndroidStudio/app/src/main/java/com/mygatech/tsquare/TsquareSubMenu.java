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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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

public class TsquareSubMenu extends Activity {
	private ListView listView;
	private ArrayAdapter<? extends Object> adapter;
	private static final String TSQUARESUB = "tsquaresub.txt";
	private static final String TSQUARE = "tsquare.txt";
	private static final String dir = Environment.getExternalStorageDirectory() + "/MyGaTech/";
	protected static final String FINAL_LINK = "FinalLink";
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<String> urlList = new ArrayList<String>();
	private ArrayList<TsquareArrays> classList = new ArrayList<TsquareArrays>();
	private Typeface typeface;
	private SharedPreferences menuSp;
	private String board_name, subject_name, board_link;
				//privateTitle = board
	private MenuItem menuItem;
	ArrayList<TsquareArrays> subMenuList;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tsquare_class);
		overridePendingTransition(R.anim.fade, R.anim.hold);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		EasyTracker.getInstance(this).activityStart(this);
		GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		Tracker tracker = GoogleAnalytics.getInstance(this).getTracker(MainActivity.TRACKING);
		tracker.send(MapBuilder.createAppView().set(Fields.SCREEN_NAME, "T-Square submenu " + actionBar.getTitle()).build());

		board_name = getIntent().getExtras().getString("boardName");
		subject_name = getIntent().getExtras().getString("subjectName");
		board_link = getIntent().getExtras().getString("link");
	}
	
	
	private void clearLists(){
		nameList.clear();
		urlList.clear();
		classList.clear();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.d("check", getTitle().toString());
		setTitle(board_name);

		
		typeface = Typeface.createFromAsset(getAssets(), "font.ttf");
		clearLists();
		
		FileInputStream fis;
		BufferedReader buffer;
		subMenuList = new ArrayList<TsquareArrays>();
		try {
			fis = new FileInputStream(dir + TSQUARE);
			buffer = new BufferedReader(new InputStreamReader(fis));
			String line = "";
			while ((line = buffer.readLine()) != null) {
				String[] lineArray = line.split(";", -1);
				TsquareArrays ts = new TsquareArrays(lineArray[0], lineArray[1],
						lineArray[2], lineArray[3], lineArray[4], lineArray[5]);
				if (ts.getName().equalsIgnoreCase(subject_name) && 
						ts.getBoard().equalsIgnoreCase(board_name) && 
						!subMenuList.contains(ts)){
					subMenuList.add(ts);
					Log.d("is", ts.getName() + " " + ts.getBoard() + " " + ts.getTitle() + "added");
				}
			}
			fis.close();
		} catch (Throwable e) {
			e.getStackTrace();
		}
		
		if (subMenuList.size() == 0 )
			Toast.makeText(getApplicationContext(), "No Item to show for " + subject_name + " " + board_name, Toast.LENGTH_LONG).show();

		
		Collections.sort(subMenuList, new TsquareComparator(subject_name, board_name));
		
		if (board_name.equals("Gradebook")) { // In case of gradebook
			Queue<String> queue = new ArrayDeque<String>();
			for (Object obj : subMenuList) {
				if (obj != null
						&& !((TsquareArrays) obj).getContent()
								.equalsIgnoreCase("Jan 1, 2000 00:00 AM")
						&& !classList.contains(obj)) {
					TsquareArrays elem = (TsquareArrays) obj;
					Log.d("DEBUG", elem.toString());
					queue.add(elem.getTitle());
					urlList.add(elem.getDate());
					classList.add(elem);
				}
			}
			String nameListString = "";
			while (!queue.isEmpty()
					&& !nameList.contains(nameListString = queue.poll())) {
				nameList.add(nameListString);
			}
			
//			clickedLink = getSharedPreferences(TsquareMenu.MENU_LINK, 0).getString("link", "https://t-square.gatech.edu/portal/pda");
//			finalEditor.putString("link", clickedLink);
//			finalEditor.commit();
			listView = (ListView) findViewById(R.id.tsquareListView);
			adapter = new GradeArryAdapter(this, R.layout.tsquare_grade,classList);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		} else if (board_name.equals("Syllabus") || board_name.equals("Section Info")
				|| board_name.equals("Forums") || board_name.equals("Chat Room")
				|| board_name.equals("Wiki")) {
			nameList.clear();
			Intent intent = new Intent(getApplicationContext(), TsquareWebView.class);
			intent.putExtra("boardName", board_name);
			intent.putExtra("subjectName", getTitle());
			intent.putExtra("link", subMenuList.get(0).getLink());
			startActivity(intent);
			finish();
		} else {
			
			Queue<String> queue = new ArrayDeque<String>();
			for (Object obj : subMenuList) {
				if (obj != null && !classList.contains(obj)) {
					TsquareArrays elem = (TsquareArrays) obj;
					queue.add(elem.getSet().get("title"));
					classList.add(elem);
					urlList.add(elem.getSet().get("link"));
				}
			}
			
			//We hate duplicates!!
			String otherString = "";
			while (!queue.isEmpty() && !nameList.contains(otherString = queue.poll())) {
				nameList.add(otherString);
			}
			
			listView = (ListView) findViewById(R.id.tsquareListView);
			adapter = new StableArrayAdapter(this, R.layout.tsquare_row, nameList);
			listView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	private class GradeArryAdapter extends ArrayAdapter<TsquareArrays> {
		private Context context;
		private List<TsquareArrays> Ids;
		private int rowResourceId;
		HashMap<TsquareArrays, Integer> mIdMap = new HashMap<TsquareArrays, Integer>();

		public GradeArryAdapter(Context context, int textViewResourceId,
				List<TsquareArrays> objects) {
			super(context, textViewResourceId, objects);

			this.context = context;
			this.Ids = objects;
			this.rowResourceId = textViewResourceId;

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
			Button button = (Button) rowView.findViewById(R.id.tsquareBtn);
			button.setBackgroundColor(Color.WHITE);
			TextView titleText = (TextView) rowView.findViewById(R.id.nameText);
//			String nameElem = nameList.get(position);
			titleText.setText(classList.get(position).getTitle()+ ", "
					+ classList.get(position).getDate());
			titleText.setBackgroundColor(Color.WHITE);
			titleText.setTypeface(typeface);
			titleText.setPadding(30, 0, 0, 0);
			titleText.setTextSize(25);
			TextView gradeText = (TextView) rowView
					.findViewById(R.id.gradeText);
			gradeText.setText(classList.get(position).getContent());
			gradeText.setBackgroundColor(Color.WHITE);
			gradeText.setPadding(30, 0, 0, 0);
			gradeText.setTextSize(30);

			return rowView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {
		private Context context;
		private List<String> Ids;
		private int rowResourceId;
		ImageView[] imgV = new ImageView[255];
		AnimationDrawable[] aniV = new AnimationDrawable[255];
		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.Ids = objects;
			this.rowResourceId = textViewResourceId;
			
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
			Button button = (Button) rowView.findViewById(R.id.tsquareBtn);
			button.setText(nameList.get(position));
			button.setBackgroundColor(Color.WHITE);
			button.setTypeface(typeface);
			button.setPadding(30, 0, 0, 0);
			final ImageView loading = (ImageView) rowView
					.findViewById(R.id.loading);
			loading.setBackgroundResource(R.anim.ani_spinner);
			loading.setVisibility(View.INVISIBLE);
			final AnimationDrawable frameAnimation = (AnimationDrawable) loading
					.getBackground();
			imgV[position] = loading;
			aniV[position] = frameAnimation;
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					loading.setVisibility(View.VISIBLE);
					frameAnimation.start();
					overridePendingTransition(R.anim.fade, R.anim.hold);
					String contentName = nameList.get(position);
					String contentLink = classList.get(position).getLink();
					Intent intent = new Intent(getApplicationContext(), TsquareFinalMenu.class);
					intent.putExtra("boardName", board_name);
					intent.putExtra("subjectName", subject_name);
					intent.putExtra("contentName", contentName);
					intent.putExtra("contentLink", contentLink);
					startActivity(intent);
				}
			});

			button.setTextSize(32);
			return rowView;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tsquare_menu, menu);
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
				if (ts.getName().equalsIgnoreCase(subject_name) && 
						ts.getBoard().equalsIgnoreCase(board_name) ){
					board_link = ts.getLink();
					break;
				}
			}
			fis.close();
		} catch (Throwable e) {
			e.getStackTrace();
		}
		return true;
	}


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
			Toast.makeText(this, "Updating :)", Toast.LENGTH_LONG).show();
			new Update().execute(board_name, board_link, TICKET, JS, BIGIP);
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
				ArrayList<TsquareArrays> arrTs = getAnnouncements(boardLink);
				Collections.sort(arrTs,	new TsquareComparator(subject_name, board_name));
				for(TsquareArrays ts : arrTs) {
					if (!subMenuList.contains(ts)){
						subMenuList.add(ts);
						Log.e("adding", ts.toString());
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(dir + TSQUARE, true);
							Log.e("check", "Written" + ts.getName() + ts.getTitle());
							fos.write((ts.getName() + ";").getBytes() );
							fos.write((ts.getBoard()+ ";").getBytes());
							fos.write((ts.getTitle()+ ";").getBytes());
							fos.write((ts.getContent()+ ";").getBytes());
							fos.write((ts.getDate()+ ";").getBytes());
							fos.write((ts.getLink()+ ";").getBytes());
							fos.write(System.getProperty("line.separator").getBytes());
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				Collections.sort(subMenuList, new TsquareComparator(subject_name, board_name));
				clearLists();
				Queue<String> queue = new ArrayDeque<String>();
				for (Object obj : subMenuList) {
					if (obj != null && !classList.contains(obj)) {
						TsquareArrays elem = (TsquareArrays) obj;
						queue.add(elem.getSet().get("title"));
						classList.add(elem);
						urlList.add(elem.getSet().get("link"));
					}
				}
				
				//We hate duplicates!!
				String otherString = "";
				while (!queue.isEmpty() && !nameList.contains(otherString = queue.poll())) {
					nameList.add(otherString);
				}
			} else if (boardName.equalsIgnoreCase("Gradebook")) {
				ArrayList<TsquareArrays> arrTs = getGradebook(boardLink);
				Collections.sort(arrTs,	new TsquareComparator(subject_name, board_name));
				for(TsquareArrays ts : arrTs) {
					if (!subMenuList.contains(ts)){
						subMenuList.add(ts);
						Log.e("adding", ts.toString());
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
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
				Collections.sort(subMenuList, new TsquareComparator(subject_name, board_name));
				clearLists();
				Queue<String> queue = new ArrayDeque<String>();
				for (Object obj : subMenuList) {
					if (obj != null
							&& !((TsquareArrays) obj).getContent()
									.equalsIgnoreCase("Jan 1, 2000 00:00 AM")
							&& !classList.contains(obj)) {
						TsquareArrays elem = (TsquareArrays) obj;
						queue.add(elem.getTitle());
						urlList.add(elem.getDate());
						classList.add(elem);
					}
				}
				String nameListString = "";
				while (!queue.isEmpty()
						&& !nameList.contains(nameListString = queue.poll())) {
					nameList.add(nameListString);
				}
			} else if (boardName.equalsIgnoreCase("Assignments")) {
				ArrayList<TsquareArrays> arrTs = getAssignment(boardLink);
				Collections.sort(arrTs,	new TsquareComparator(subject_name, board_name));
				for(TsquareArrays ts : arrTs) {
					if (!subMenuList.contains(ts)){
						subMenuList.add(ts);
						Log.e("adding", ts.toString());
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
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				
				Collections.sort(subMenuList, new TsquareComparator(subject_name, board_name));
				clearLists();
				Queue<String> queue = new ArrayDeque<String>();
				for (Object obj : subMenuList) {
					if (obj != null && !classList.contains(obj)) {
						TsquareArrays elem = (TsquareArrays) obj;
						queue.add(elem.getSet().get("title"));
						classList.add(elem);
						urlList.add(elem.getSet().get("link"));
					}
				}
				
				//We hate duplicates!!
				String otherString = "";
				while (!queue.isEmpty() && !nameList.contains(otherString = queue.poll())) {
					nameList.add(otherString);
				}
			}
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
			        Elements menuElems = menuDoc.select(jsoupParser("Assignments", subject_name, 1));

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
							temp = new TsquareArrays(subject_name, "Assignments", title, content, date, temp.getLink());
							curList.add(temp);
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
			        Elements menuElems = menuDoc.select(jsoupParser("Gradebook", subject_name, 1));

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
							temp = new TsquareArrays(subject_name, "Gradebook", itemName, itemGrade, itemDate, "NaN");
							curList.add(temp);
							
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
		        }
		        
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
				Elements menuElems = menuDoc.select(jsoupParser("announcements", subject_name ,1));
				String stemp = null;
				TsquareArrays temp = null;
 				int index = 0;
				for (Element menuElem : menuElems) {
					if (index % 2 == 0) {
						if (menuElem.attr("title").contains("View announcement")) 
							stemp = menuElem.attr("title").substring(18);

						if (stemp == null) 
							temp = new TsquareArrays(subject_name,"Announcements",
									menuElem.attr("title"),menuElem.attr("href"));
						else 
							temp = new TsquareArrays(subject_name,
									"Announcements", stemp,menuElem.attr("href"));
					} else {
						// curTemp; title; content; date
						String date = menuElem.select("td[headers=date]").text();
						temp = new TsquareArrays(temp.getName(),temp.getBoard(),
								temp.getTitle(), "NaN", date, temp.getLink());
						curList.add(temp);
					}
					index++;
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
		
		@Override
		protected void onPostExecute(String result) {
			if(menuItem != null) {
				menuItem.collapseActionView();
				menuItem.setActionView(null);
			}
			new ToastRun("Updated:)").run();
			adapter.notifyDataSetChanged();
		}
	};
	
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
