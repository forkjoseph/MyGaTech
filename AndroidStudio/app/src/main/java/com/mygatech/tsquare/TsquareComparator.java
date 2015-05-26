package com.mygatech.tsquare;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;




public class TsquareComparator implements Comparator<TsquareArrays> {
		private String clickedName;
		private String subject;
		
	public TsquareComparator(String subject, String clickedName){
		this.subject = subject;
		this.clickedName = clickedName;
	}

	public int compare(TsquareArrays t1, TsquareArrays t2) {
		Calendar thisDate = Calendar.getInstance();
		Calendar otherDate = Calendar.getInstance();

		if (t1.getContent().equalsIgnoreCase("(No grade)")
				|| t1.getContent().equalsIgnoreCase("NaN")) {
			t1.modify("content", "Jan 1, 2000 00:00 AM");
		}
		if (t2.getContent().equalsIgnoreCase("(No grade)")
				|| t2.getContent().equalsIgnoreCase("NaN")) {
			t2.modify("content", "Jan 1, 2000 00:00 AM");
		}
		
		
		else if (t1.getName().equals(subject) && t1.getBoard().equals(clickedName)
				&& t2.getName().equals(subject)
				&& t2.getBoard().equals(clickedName)) {
			thisDate = parseString(t1.getDate());
			otherDate = parseString(t2.getDate());
			// thisDate is after otherDate -> Negative
			// thisDate is before otherDate -> positive
		}
		return otherDate.compareTo(thisDate);
	}

	public Calendar parseString(String date){
		//MM DD,  YYYY HH:MM PM
		// 0  1,     2   3    4
		int hr, min, month, day, year;
		Calendar returnDate =  Calendar.getInstance();
		if (!date.equalsIgnoreCase("(No date)")){
			ArrayList<String> parseTemp = new ArrayList<String>();
			for(String temp : date.split("\\s+")){
				parseTemp.add(temp);
			}
			
			if(parseTemp.size() >3 ){
				if(parseTemp.get(4).equalsIgnoreCase("pm"))
					hr = 12 + Integer.parseInt(parseTemp.get(3).split(":")[0]);
				else
					hr = Integer.parseInt(parseTemp.get(3).split(":")[0]);
				min = Integer.parseInt(parseTemp.get(3).split(":")[1]);
				month = getMonth(parseTemp.get(0));
				day = Integer.parseInt(parseTemp.get(1).split(",")[0]);
				year = Integer.parseInt(parseTemp.get(2));
				returnDate.set(year, month, day, hr, min);
			}else{
				month = getMonth(parseTemp.get(0));
				day = Integer.parseInt(parseTemp.get(1).split(",", 2)[0]);
				year = Integer.parseInt(parseTemp.get(2));
				returnDate.set(year, month, day);
			}
		}
		return returnDate;
	}
	
	public enum Day {
		Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec;
	}
	
	public int getMonth(String month) {
		Day val = Day.valueOf(month);
		int value = 0;
		switch (val) {
		case Jan:
			value = Calendar.JANUARY;
			break;
		case Feb:
			value = Calendar.FEBRUARY;
			break;
		case Mar:
			value = Calendar.MARCH;
			break;
		case Apr:
			value = Calendar.APRIL;
			break;
		case May:
			value = Calendar.MAY;
			break;
		case Jun:
			value = Calendar.JUNE;
			break;
		case Jul:
			value = Calendar.JULY;
			break;
		case Aug:
			value = Calendar.AUGUST;
			break;
		case Sep:
			value = Calendar.SEPTEMBER;
			break;
		case Oct:
			value = Calendar.OCTOBER;
			break;
		case Nov:
			value = Calendar.NOVEMBER;
			break;
		case Dec:
			value = Calendar.DECEMBER;
			break;
		}
		return value;
	}
	
}
