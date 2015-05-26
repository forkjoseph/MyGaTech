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
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mygatech.LoginChecker;
import com.mygatech.LoginTask;
import com.mygatech.MainActivity;
import com.mygatech.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.webkit.CookieManager;

public class TsquareService extends Service {
	static CookieManager manager;
	static HttpsURLConnection connection,connection2;
	private SharedPreferences menuSp;
	private static final String TSQUARESUB = "tsquaresub.txt";
	private static final String TSQUARE = "tsquare.txt";
	private static final String dir = Environment.getExternalStorageDirectory() + "/MyGaTech/";

	private Handler mHandler = new Handler();
//	private int mInterval = 60000; // Every hours
	private int mInterval = 3000000; // Every ten minuts

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {			//For t-square
		menuSp = getSharedPreferences(TsquareMain.MENU_LIST,0);
	    mHandler.post(mStatusChecker);
   
		return START_REDELIVER_INTENT;
	}

	
	
	Runnable mStatusChecker = new Runnable() {
	    @Override 
	    public void run() {
	    	if(getSharedPreferences(MainActivity.getUserDetails(),0).getBoolean("Student", false)){
		    	String JS = menuSp.getString("TsquareJS", null);
				String BIGIP = menuSp.getString("Bigip", null);
				String TICKET = menuSp.getString("Ticket", null);
			    ArrayList<TsquareArrays> menuList = new ArrayList<TsquareArrays>();
				FileInputStream fis = null;
				BufferedReader buffer;
				try {
					fis = new FileInputStream(dir + TSQUARESUB);
					buffer = new BufferedReader(new InputStreamReader(fis));
					String line = "";
					while ((line = buffer.readLine()) != null) {
						String[] lineArray = line.split(";", -1);
						TsquareArrays ts = new TsquareArrays(lineArray[0], lineArray[1],
								lineArray[2]);
						if ((ts.getBoard().equalsIgnoreCase("Announcements") 
								|| ts.getBoard().equalsIgnoreCase("Gradebook")
								|| ts.getBoard().equalsIgnoreCase("Assignments"))	&&
								!menuList.contains(ts)){
							menuList.add(ts);
						}
					}
					fis.close();
				} catch (Throwable e) {
					e.getStackTrace();
				} finally {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	
				}
				
			    ArrayList<TsquareArrays> alreaydHas = new ArrayList<TsquareArrays>();
				try {
					try {
						fis = new FileInputStream(dir + TSQUARE);
						buffer = new BufferedReader(new InputStreamReader(fis));
						String line = "";
						while ((line = buffer.readLine()) != null) {
							String[] lineArray = line.split(";", -1);
							TsquareArrays ts = new TsquareArrays(lineArray[0], lineArray[1],
									lineArray[2], lineArray[3], lineArray[4], lineArray[5]);
							if (!alreaydHas.contains(ts) && (ts.getBoard().equalsIgnoreCase("Announcements") 
										|| ts.getBoard().equalsIgnoreCase("Gradebook")
										|| ts.getBoard().equalsIgnoreCase("Assignments"))){
								alreaydHas.add(ts);
							}
						}
						fis.close();
					} catch (Throwable e) {
						e.getStackTrace();
					}
					
					LoginCheck loginCheck = new LoginCheck(TsquareService.this,
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
					for(TsquareArrays ts : alreaydHas){
						Log.e(ts.getName() + " " + ts.getBoard() , ts.getTitle() + " " + ts.getContent());
					}
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					for(TsquareArrays ts : menuList){
						new mAsyncTask(alreaydHas).execute(ts.getName(), ts.getBoard(), ts.getLink(), TICKET, JS, BIGIP);
					}
			    	mHandler.postDelayed(mStatusChecker, mInterval);
				}
	    	}
//	    	new mAsyncTask(alreaydHas).execute(subject, boardName, boardLink, TICKET, JS, BIGIP);
	    }
	};
	
	private class mAsyncTask extends AsyncTask<String, Void, String> {
		private String subject, boardName, boardLink, TICKET, JS, BIGIP;
		private HttpsURLConnection connection;
		private ArrayList<TsquareArrays> list;
		
		public mAsyncTask(ArrayList<TsquareArrays> list ){
			super();
			this.list = list;
		}
		
		@Override
		protected String doInBackground(String... params) {
//			try {
//				Thread.sleep(10000);
//			} catch (InterruptedException e) {
//				Log.e("someone", "woke me up");
//				e.printStackTrace();
//			}
//			Log.e("I slept", "well");
//			if(list == null)
//				Log.e("List is ", "null");
//			else
//				Log.e("List is", "not null");
//			return null;
			this.subject = params[0];
			this.boardName = params[1];
			this.boardLink = params[2];
			this.TICKET = params[3];
			this.JS= params[4];
			this.BIGIP = params[5];
			Log.d(subject + " " + boardName, boardLink);
			if (boardName.equalsIgnoreCase("Announcements")){
				for(TsquareArrays ts : getAnnouncements(boardLink)) {
					if (!list.contains(ts)){
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
							notifyAndLaunch(subject, boardName, ts.getTitle(), ts.getLink());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			} else if (boardName.equalsIgnoreCase("Gradebook")) {
				for(TsquareArrays ts : getGradebook(boardLink)) {
					if (!list.contains(ts)){
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
							notifyGradebook(ts.getName(), ts.getBoard(), ts.getTitle());
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			} else if (boardName.equalsIgnoreCase("Assignments")) {
				for(TsquareArrays ts : getAssignment(boardLink)) {
					if (!list.contains(ts)){
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
							notifyAndLaunch(subject, boardName, ts.getTitle(), ts.getLink());
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
			        Elements menuElems = menuDoc.select(jsoupParser("Assignments", subject, 1));

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
							temp = new TsquareArrays(subject, "Assignments", title, content, date, temp.getLink());
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
			        Elements menuElems = menuDoc.select(jsoupParser("Gradebook", subject, 1));

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
							temp = new TsquareArrays(subject, "Gradebook", itemName, itemGrade, itemDate, "NaN");
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
					Elements menuElems = menuDoc.select(jsoupParser("announcements", subject ,1));
					String stemp = null;
					TsquareArrays temp = null;
	 				int index = 0;
					for (Element menuElem : menuElems) {
						if (index % 2 == 0) {
							if (menuElem.attr("title").contains("View announcement")) 
								stemp = menuElem.attr("title").substring(18);
	
							if (stemp == null) 
								temp = new TsquareArrays(subject,"Announcements",
										menuElem.attr("title"),menuElem.attr("href"));
							else 
								temp = new TsquareArrays(subject,
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
	
	
	public boolean isNetworkConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
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
	
	public String jsoupParser(String titleName, String prev, int step) {
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
	
	private void notifyGradebook(String subjectName, String boardName, String contentName){
		NotificationManager notificationManager =
    		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	Intent notifIntent = new Intent(getApplicationContext(), TsquareSubMenu.class);
    	notifIntent.putExtra("subjectName", subjectName);
    	notifIntent.putExtra("boardName", boardName);
    	notifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//    	notifIntent.putExtra("contentName", contentName);
//    	notifIntent.putExtra("link", link);
    	//Need to make this to open the user oriented screen!
    	PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(), 0, notifIntent, 0);
    	long[] pattern = {1000, 200};
    	Notification notification = new Notification.Builder(getApplicationContext())
    									.setContentTitle(subjectName + " " + boardName)
    									.setContentIntent(pendingIntent)
    									.setContentText(contentName)
    									.setPriority(Thread.MAX_PRIORITY)
    									.setSmallIcon(R.drawable.tsquare_logo2)
    									.setLights(0xffffff00,1000,200)
    									.setVibrate(pattern)
    									.setAutoCancel(true)
    									.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
    									.build();
		notificationManager.notify((int) System.currentTimeMillis(), notification);
	}
	
	private void notifyAndLaunch(String subjectName, String boardName, String contentName, String contentLink){
		NotificationManager notificationManager =
    		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	Intent notifIntent = new Intent(this, TsquareNotifyView.class);
    	notifIntent.putExtra("subjectName", subjectName);
    	notifIntent.putExtra("boardName", boardName);
    	notifIntent.putExtra("contentName", contentName);
    	notifIntent.putExtra("contentLink", contentLink);
    	notifIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	notifIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
    	int notification_id = (int) System.currentTimeMillis();
    	PendingIntent pendingIntent =PendingIntent.getActivity(this, notification_id, notifIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	long[] pattern = {1000, 200};
    	Notification notification = new Notification.Builder(getApplicationContext())
    									.setContentTitle(subjectName + " " + boardName)
    									.setContentIntent(pendingIntent)
    									.setContentText(contentName)
    									.setPriority(Thread.MAX_PRIORITY)
    									.setSmallIcon(R.drawable.tsquare_logo2)
    									.setLights(0xffffff00,1000,200)
    									.setVibrate(pattern)
    									.setAutoCancel(true)
    									.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS)
    									.build();
		notificationManager.notify(notification_id, notification);
	}
	
	private void notifyStatus(String name, String content){
		NotificationManager notificationManager =
    		    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	Intent notifIntent = new Intent(getApplicationContext(), TsquareMenu.class);
    	//Need to make this to open the user oriented screen!
    	PendingIntent pendingIntent =PendingIntent.getActivity(getApplicationContext(), 0, notifIntent, 0);
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

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
