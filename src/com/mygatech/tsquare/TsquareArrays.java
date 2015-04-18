package com.mygatech.tsquare;

import java.util.HashMap;
import java.util.Map;


public class TsquareArrays {
	// Saving as a text file in emulator/0/MyGaTech/tsquare.txt
	// Saving format
	// Assignment
	// Subject;Board;Title;Due date;Posted date;Link;
	// Gradebook
	// Subject;Board;Title;Grade;Posted date;Commnet;
	// Announcement
	// Subject;Board;Title;;Posted date;Link;
	private Map<String, String> set = new HashMap<String, String>();

	public TsquareArrays(String subject, String board, String title,
			String content, String date, String link) {
		set.put("subject", subject);
		set.put("board", board);
		set.put("title", title);
		set.put("content", content);
		set.put("date", date);
		set.put("link", link);
	}

	public TsquareArrays(String subject, String link) {
		set.put("subject", subject);
		set.put("link", link);
	}

	public TsquareArrays(String subject, String board, String link) {
		set.put("subject", subject);
		set.put("board", board);
		set.put("link", link);
	}

	public TsquareArrays(String subject, String board, String title, String link) {
		set.put("subject", subject);
		set.put("board", board);
		set.put("title", title);
		set.put("link", link);
	}

	public TsquareArrays(String subject, String board, String title,
			String content, String link) {
		set.put("subject", subject);
		set.put("board", board);
		set.put("title", title);
		set.put("content", content);
		set.put("link", link);
	}
	
	public Map<String, String> getSet() {
		return set;
	}

	public String getName() { // name == subject
		return set.get("subject");
	}

	public String getBoard() {
		return set.get("board");
	}

	public String getTitle() {
		return set.get("title");
	}

	public String getContent() {
		return set.get("content");
	}
	
	public String getDate(){
		return set.get("date");
	}

	public String getLink() {
		return set.get("link");
	}
	
	public void modify(String key, String value){
		set.put(key, value);
	}
	
	@Override
	public boolean equals(Object obj){
		if(obj instanceof TsquareArrays){
			TsquareArrays that = (TsquareArrays)obj;
			return getLink().equals((that.getLink())) &&
					getDate().equals(that.getDate()) &&
					getContent().equals(that.getContent()) &&
					getTitle().equals(that.getTitle()) &&
					getBoard().equals(that.getBoard()) &&
					getName().equals(that.getName()) ? true : false;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return getName() + " " + getBoard() + " " + getTitle() + " " + getContent() + " " + getDate() + " " + getLink();
	}

}
