package com.mygatech.webview;

import java.lang.Math;

import com.mygatech.R;


public class Restaurant{
	protected String name;
	protected String open_time;
	protected String close_time;
	protected int date;
	protected double full_date;
	protected String details;
	private double latitude, longitude; 
	private int imgId;
	
	/**
	 * For closed
	 * @param name
	 */
	public Restaurant(String name) { 
		if (name.equalsIgnoreCase("closed")) {
			this.name = "Closed";
			this.details = "There is no open place to eat. I am so sorry :(";
			setImgSrcR("Closed");
		}
	}
	
	//Time -> HH:MM
	public Restaurant(String name, String open_time, String close_time, int date){
		this.name = name;
		this.open_time = open_time;
		this.close_time = close_time;
		this.date = date;
		setDetails(open_time, close_time);
		setLocationString(name);
		setImgSrcR(name);
	}
	
	//Exception 
	public Restaurant(String name, String open_time, String close_time, int date, double full_date) {
		this.name = name;
		this.open_time = open_time;
		this.close_time = close_time;
		this.date = date;
		this.full_date = full_date;
		setDetails(open_time, close_time);
		setLocationString(name);
		setImgSrcR(name);
	}

	public Restaurant(){
		//for instantiation
	}
	
	public String getName(){
		return name;
	}
	
	public String getOpen_time(){
		return open_time;
	}
	
	public String getClose_time(){
		return close_time;
	}
	
	public int getDate(){
		return date;
	}
	
	public double getFull_Date() {
		return full_date;
	}
	
	public boolean equalsDate(double full_date){
			return Math.abs(this.full_date - full_date) < 0.00001;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setOpen_time(String open_time){
		this.open_time = open_time;
	}
	
	public void setClose_time(String close_time){
		this.close_time = close_time;
	}
	
	public void setDate(int date){
		this.date = date;
	}
	public void setFull_date(float full_date) {
		this.full_date = full_date;
	}
	
	public void setDetails(String mOpen, String mClose) {
		String _open = calculateHours(mOpen);
		String _close = calculateHours(mClose);
		this.details = name + "\nOpens at " + _open + ", Closes at " + _close; 
	}
	
	public String toString(){
		return details;
//		return name + " " + open_time + " " + close_time + " " + date;
	}
	
	public String debug(){
		return open_time + " " + close_time + " " + date;
	}

	public String exceptionDebug(){
		return open_time + " " + close_time + " " + full_date;
	}
//	@Override
//	public boolean equals(Object o) {
//		
//	}
	//============ algorithm parts
	
	private String calculateHours(String time) {
		String time_calculate = "";
		double _time = Double.parseDouble(time);
		int hour = ((int)_time);
		int minutes = (int)(((_time - hour) * 100) % 60);
		if (hour < 12 && hour >= 0 ) {
			if (minutes < 10) 
				time_calculate = hour + ":0" + minutes + " AM";
			else
				time_calculate = hour + ":" + minutes + " AM";
		} else {
			hour -= 12;
			if (minutes < 10) 
				time_calculate = hour + ":0" + minutes + " PM";
			else
				time_calculate = hour + ":" + minutes + " PM";
		}
		return time_calculate;
	}

	//============ Geo location 
	public void setLocationString(String restName) {
		if (restName.equals("Brittain")) {
			this.latitude = 33.772427;
			this.longitude = -84.391427;
		} else if (restName.equals("Woodruff")) {
			this.latitude = 33.779049;
			this.longitude = -84.406496;
		} else if (restName.equals("North Ave")) {
			this.latitude = 33.771033;
			this.longitude = -84.39159;
		} else if (restName.equals("Burger Bytes")) {
			this.latitude = 33.773686;
			this.longitude = -84.398438;
		} else if (restName.equals("BuzzBy")) {
			this.latitude = 33.772525;
			this.longitude = -84.391159;
		} else if (restName.equals("Cafe Spice")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Chef Sharon's Station")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Chick-fil-A")) {
			this.latitude = 33.773651;
			this.longitude = -84.398263;
		} else if (restName.equals("Dunkin' Donuts")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("EastSide Market")) {
			this.latitude = 33.772525;
			this.longitude = -84.391159;
		} else if (restName.equals("Essential Eats")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Far East Fusion")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Ferst Place")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Freshens at H2O")) {
			this.latitude = 33.775567;
			this.longitude = -84.404209;
		} else if (restName.equals("Great Wraps")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Highland Bakery")) {
			this.latitude = 33.772686;
			this.longitude = -84.394428;
		} else if (restName.equals("Pizza Hut")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Rosia's Cantina")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Salad Bar")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		} else if (restName.equals("Starbucks @CULC")) {
			this.latitude = 33.774281;
			this.longitude = -84.396351;
		} else if (restName.equals("Subway")) {
			this.latitude = 33.773651;
			this.longitude = -84.398263;
		} else if (restName.equals("Taco Bell")) {
			this.latitude = 33.773651;
			this.longitude = -84.398263;
		} else if (restName.equals("WestSide Market")) {
			this.latitude = 33.779506;
			this.longitude = -84.405754;
		} else if (restName.equals("Zaya Mediterannean")) {
			this.latitude = 33.774193;
			this.longitude = -84.398822;
		}
	}
	
	public double getLat() {
		return latitude;
	}
	
	public double getLong() {
		return longitude;
	}
	
	//============ Image resource ID
	public int[] imgSrcR = {R.drawable.brittainhall,  R.drawable.woodruff, R.drawable.northave,
			R.drawable.burgerbytes, R.drawable.buzzby, R.drawable.cafespice,
			R.drawable.chefsharonsstation, R.drawable.chickfila, R.drawable.dunkin,
			R.drawable.eastsidemarket, R.drawable.essentialeats, R.drawable.fareatfusion,
			R.drawable.ferstplace, R.drawable.freshensath2o, R.drawable.greatwraps,
			R.drawable.highlandbakery, R.drawable.pizzahut, R.drawable.rositascantina,
			R.drawable.saladbar, R.drawable.starbucks, R.drawable.subway,
			R.drawable.tacobell, R.drawable.westsidemarket,R.drawable.zayamediterannean, R.drawable.closed};
	
	public void setImgSrcR(String restName) {
		this.imgId = getImgSrcR(restName);
	}
	
	public int getImgSrcR() {
		return this.imgId;
	}
	
	private int getImgSrcR(String restName){
		int imgSrcRName = 0;
		if(restName.equals("Brittain")){ imgSrcRName = imgSrcR[0];}
		if(restName.equals("Woodruff")){ imgSrcRName = imgSrcR[1];}
		if(restName.equals("North Ave")){ imgSrcRName = imgSrcR[2];}
		if(restName.equals("Burger Bytes")){ imgSrcRName = imgSrcR[3];}
		if(restName.equals("BuzzBy")){ imgSrcRName = imgSrcR[4];}
		if(restName.equals("Cafe Spice")){ imgSrcRName = imgSrcR[5];}
		if(restName.equals("Chef Sharon's Station")){ imgSrcRName = imgSrcR[6];}
		if(restName.equals("Chick-fil-A")){ imgSrcRName = imgSrcR[7];}
		if(restName.equals("Dunkin' Donuts")){ imgSrcRName = imgSrcR[8];}
		if(restName.equals("EastSide Market")){ imgSrcRName = imgSrcR[9];}
		if(restName.equals("Essential Eats")){ imgSrcRName = imgSrcR[10];}
		if(restName.equals("Far East Fusion")){ imgSrcRName = imgSrcR[11];}		
		if(restName.equals("Ferst Place")){ imgSrcRName = imgSrcR[12];}	
		if(restName.equals("Freshens at H2O")){ imgSrcRName = imgSrcR[13];}		
		if(restName.equals("Great Wraps")){ imgSrcRName = imgSrcR[14];}
		if(restName.equals("Highland Bakery")){ imgSrcRName = imgSrcR[15];}
		if(restName.equals("Pizza Hut")){imgSrcRName = imgSrcR[16];}
		if(restName.equals("Rosia's Cantina")){imgSrcRName = imgSrcR[17];}
		if(restName.equals("Salad Bar")){imgSrcRName = imgSrcR[18];}
		if(restName.equals("Starbucks @CULC")){imgSrcRName = imgSrcR[19];}
		if(restName.equals("Subway")){imgSrcRName = imgSrcR[20];}
		if(restName.equals("Taco Bell")){imgSrcRName = imgSrcR[21];}
		if(restName.equals("WestSide Market")){imgSrcRName = imgSrcR[22];}
		if(restName.equals("Zaya Mediterannean")){imgSrcRName = imgSrcR[23];}
		if(restName.equals("Closed")){imgSrcRName = imgSrcR[24];}
    	return imgSrcRName;
	}
	
	public String getLocationString(){
		String restName = this.name;
    	if(restName.equals("Brittain")){return "649 Techwood Drive NW, East Campus";}
    	else if(restName.equals("Woodruff")){return "890 Curran Street NW, West Campus";}
    	else if(restName.equals("North Ave")){return "120 North Ave NW Atlanta, GA 30313, East Campus";}
    	else if(restName.equals("Burger Bytes")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("BuzzBy")){ return "649 Techwood Drive NW Atlanta, GA, East Campus, Behind Brittain";}
    	else if(restName.equals("Cafe Spice")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Chef Sharon's Station")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Chick-fil-A")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Dunkin' Donuts")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("EastSide Market")){return "649 Techwood Drive NW Atlanta, GA, East Campus, Behind Brittain";}
    	else if(restName.equals("Essential Eats")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Far East Fusion")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Ferst Place")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center, 3rd floor";}
    	else if(restName.equals("Freshens at H2O")){return "750 Ferst Dr, Atlanta, GA, Located at CRC";}		
    	else if(restName.equals("Great Wraps")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Highland Bakery")){return "224 Uncle Heinie Way, Atlanta, GA 30313, ";}
    	else if(restName.equals("Pizza Hut")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Rosia's Cantina")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Salad Bar")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Starbucks @CULC")){return "266 4th St NW, Atlanta, GA, CULC";}
    	else if(restName.equals("Subway")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("Taco Bell")){return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";}
    	else if(restName.equals("WestSide Market")){ return "875 Curran Street, Atlanta, GA, Curran Street Parking Deck";}
    	else return "350 Ferst Drive, Atlanta, GA 30332-0458, Student Center";
    }	    
}

