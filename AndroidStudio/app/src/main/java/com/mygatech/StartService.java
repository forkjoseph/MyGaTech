package com.mygatech;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class StartService extends IntentService {
	private ResultReceiver rec;
	private String strForHtml;
	private HttpsURLConnection conn;

	public StartService() {
		super("StartService");
		setIntentRedelivery(false);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			rec = intent.getParcelableExtra("receiverTag");
			String user = intent.getStringExtra("username");
			String pass = intent.getStringExtra("password");
			String login = "https://login.gatech.edu/cas/login";
			URL urlForLogin = new URL(login);
			conn = (HttpsURLConnection) urlForLogin.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Cookie", "utmccn=(referral)");
			conn.setRequestProperty("Accept","text/html, application/xhtml+xml, */*");
			conn.setRequestProperty("Accept-Language", "en-US");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D)");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			conn.setRequestProperty("Host", "login.gatech.edu");
			conn.setRequestProperty("DNT", "1");
			conn.setRequestProperty("Connection", "Keep-Alive");
			String cookie = conn.getHeaderField("Set-Cookie"); // Real usage
			String js = parseJsession(cookie); // URL usage
			cookie = (cookie.split(";", 2)[0]);
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String LT = "                        <input type=\"hidden\" name=\"lt\" value=\"";
			String input;
			// LT parameter algorithm
			while ((input = in.readLine()) != null) {
				if (input.startsWith("                        <input type=\"hidden\" name=\"lt\" value="))
					LT = input.substring(LT.length(), input.length()-4);
			}

			strForHtml = login + js;
			Log.d("StrForHTML:" , strForHtml);
			Log.d("LT:", LT);
			URL urlForHtml = new URL(strForHtml);
			conn = (HttpsURLConnection) urlForHtml.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", cookie + ";utmccn=(referral)");
			conn.setRequestProperty("Accept","text/html, application/xhtml+xml, */*");
			conn.setRequestProperty("Accept-Language", "en-US");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 4.1.1; Nexus 7 Build/JRO03D)");
			conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
			conn.setRequestProperty("Host", "login.gatech.edu");
			conn.setRequestProperty("DNT", "1");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Referer","https://login.gatech.edu/cas/login");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setRequestProperty("Content-Type",	"application/x-www-form-urlencoded");
			String urlParameters = "username=" + user + "&password=" + pass + "&lt=" + LT+ 
								   "&execution=e1s1" + "&_eventId=submit&submit=LOGIN";
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();

			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer html = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("class=\"errors\"")) {
					html.append(inputLine);
				}
			}
			in.close();
			
			if (!html.toString().contains("class=\"errors\"")) {
				Log.e("check", "Loged in successfully");
				Bundle b = new Bundle();
				b.putBoolean("ServiceTag", true);
				rec.send(0, b);
			} else {
				Log.e("check", "Wrong username or password!");
				Bundle b = new Bundle();
				b.putBoolean("ServiceTag", false);
				rec.send(0, b);
			}
		} catch (Throwable e) {
			Log.e("check", "ERROR");
		}
	}

	public static String parseJsession(String mCookie) {
		String[] split = mCookie.split(";", -1);
		String mReturn = "";
		for (String temp : split) {
			if (temp.toLowerCase(Locale.US).contains("jsessionid")) {
				String[] arrayTemp = temp.split("=", 2);
				for (String js : arrayTemp) {
					if (js.toLowerCase(Locale.US).contains("jsessionid")) {
						mReturn = ";" + js.toLowerCase(Locale.US);
					} else {
						mReturn += "=" + js;
					}
				}
			}
		}
		return mReturn;
	}


}
