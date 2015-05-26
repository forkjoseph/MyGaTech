package com.mygatech;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mygatech.tsquare.TsquareMain;

import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;

public abstract class LoginTask extends AsyncTask<String, Void, String> {
		private Context context;
		
		public LoginTask(Context context){
			this.context = context;
		}
		
		
		//Make T-square logged in
		@Override
		protected String doInBackground(String... params) {
//			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			try{
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

		        final String user = context.getSharedPreferences(MainActivity.USER_DETAILS, 0).getString("username", null);       
			    final String pass = context.getSharedPreferences(MainActivity.USER_DETAILS, 0).getString("password", null); 
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
			        String finalTicket = "";//nextURL.split("=", -1)[1];
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
			        
			        Editor menuEditor = context.getSharedPreferences(TsquareMain.MENU_LIST, 0).edit();
			        menuEditor.clear().commit();
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
		
		protected abstract void onPostExecute(String result);
		
		private String parseJsession(String mCookie){
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

}