package com.mygatech.webview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RestDatabaseHandler extends SQLiteOpenHelper {
/**
 * 1. opens after 0 and close before 7 -> regular
 *    opens after 7 and close before 24 -> regular
 * 2. opens after 7 and closes at 24 -> look for   
 *    opens at 0 and closes before 7 -> extension
 * 3. opens at 0 and closes at 24 -> whole days
 * 4. opens after 7 and closes before 24 
 * 	  and then opens again 		-> two opening
 * 
 * -----------------------------------------------------------
 * 1. checks open and close time
 * 2. If (open > 0 && close < 7) || (open > 7 && close < 24)  -> regular
 * 3. If (open > 7 && close == 24) -> look for extension
 * 	     + (open == 0 && close < 7)
 * 4. If (open == 0 && close == 24) -> whole days
 * 5. 
 */
	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_NAME = "restManager";

	private static final String TABLE_CONTACTS = "rests";

	private static final String KEY_NAME = "name";
	private static final String KEY_OPEN_TIME = "open_time";
	private static final String KEY_CLOSE_TIME = "close_time";
	private static final String KEY_DATE = "date";

	public RestDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_REST_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_NAME + " TEXT," + KEY_OPEN_TIME + " REAL,"
				+ KEY_CLOSE_TIME + " REAL," + KEY_DATE + " INTEGER" + ")";
		db.execSQL(CREATE_REST_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(db);
	}

	/**
	 * CRUD operations
	 */
	public void addRest(Restaurant restaurant) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, restaurant.getName());
		values.put(KEY_OPEN_TIME, restaurant.getOpen_time());
		values.put(KEY_CLOSE_TIME, restaurant.getClose_time());
		values.put(KEY_DATE, restaurant.getDate());

		db.insert(TABLE_CONTACTS, null, values);
		db.close();
	}

	public Restaurant getRest(String name) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_NAME,
				KEY_OPEN_TIME, KEY_CLOSE_TIME, KEY_DATE }, KEY_NAME + "=?",
				new String[] { String.valueOf(name) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		Restaurant restaurant = new Restaurant(cursor.getString(0),
				cursor.getString(1), cursor.getString(2),
				(Integer.parseInt(cursor.getString(3))));
		return restaurant;
	}
	
	public ArrayList<Restaurant> getRestListByName(String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_NAME,
				KEY_OPEN_TIME, KEY_CLOSE_TIME, KEY_DATE }, KEY_NAME + "=?",
				new String[] { String.valueOf(name) }, null, null, null, null);
		ArrayList<Restaurant> list = new ArrayList<Restaurant>();
		if (cursor != null) {
			for(int i = 0; i < cursor.getCount(); i++){
				cursor.moveToNext();
				double open_time = cursor.getFloat(1);
				double close_time = cursor.getFloat(2);
				int date_of_week = (Integer.parseInt(cursor.getString(3)));
				Restaurant r = new Restaurant(cursor.getString(0),
						open_time +"", close_time + "", date_of_week);
				Log.i("getRestList: ", r.toString());
				list.add(r);
			}
		}
		return list;
	}
	
	
	/**
	 *  Today is ... SUNDAY through SATURDAY are contiguous (1 through 7) 
	 *  Sunday - 1
	 *  Monday - 2
	 *  ...
	 *  Friday - 6
	 *  Saturday - 7
	 * @return
	 */
	public ArrayList<Restaurant> getRestListOpenNow() {
		Calendar c = Calendar.getInstance(); 
		int date = c.get(Calendar.DAY_OF_WEEK);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		double time = hour + (((double)minute)/60);

		return getRestListByTime(time, date);
	}
	
	public ArrayList<Restaurant> getRestListOpenToday() {
		Calendar c = Calendar.getInstance(); 
		int date = c.get(Calendar.DAY_OF_WEEK);
		return getRestListByDate(date);
	}	
	
	private ArrayList<Restaurant> getRestListByDate(int date) {
		String _date = date +"";
		String _tomorrow = ((date+1) % 7) + "";
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_CONTACTS, null, 
				"("+ KEY_OPEN_TIME + " <= 7 AND " + KEY_OPEN_TIME + " != -1 AND "+ KEY_DATE + "=?) OR (" + 
				KEY_OPEN_TIME + " BETWEEN 0 AND 7 AND " + KEY_CLOSE_TIME + "!=0 AND " + 
				KEY_CLOSE_TIME + " <=7 AND "+ KEY_DATE + "=? AND " + KEY_NAME + "!=?)",
				new String[] { _date, _tomorrow, "WestSide Market"}, null, null, null);
		ArrayList<Restaurant> list = new ArrayList<Restaurant>();
		if (cursor != null) {
			for(int i = 0; i < cursor.getCount(); i++){
				if(cursor.moveToNext()){
					String name = cursor.getString(0);
					double open_time = cursor.getFloat(1);
					double close_time = cursor.getFloat(2);
					int date_of_week = (Integer.parseInt(cursor.getString(3)));
					if (close_time == 0) {
						if (!cursor.isLast() && !cursor.isAfterLast() && cursor.moveToNext()) {
							if (cursor.getString(0).equals(name) && cursor.getFloat(1) == 0) {
								close_time = cursor.getFloat(2);
							} else {
								cursor.moveToPrevious();
							}
						}
					}
					Restaurant r = new Restaurant(name,	open_time +"", close_time + "", date_of_week);
					Log.i("getRestList: ", r.debug());
					list.add(r);
				}
			}
		}
		return list;
	}
	
	public ArrayList<Restaurant> getRestListByTime(double time, int date) {
		String _time = time +"";
		String _date = date +"";
		String _tomorrow = ((date+1) % 7) + "";
		Log.e("Requested time: ", _time + " " + _date);
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = null;
		if (time <= 7 && time > 0) { // time after midnight
			cursor = db.query(TABLE_CONTACTS, null, 
					KEY_OPEN_TIME + "<=? AND "+ KEY_CLOSE_TIME + ">=? AND " + KEY_DATE + "=?",
					new String[] {_time, _time, _date}, null, null, null);
		} else {
			cursor = db.query(TABLE_CONTACTS, null, 
				"(" + KEY_OPEN_TIME + "<=? AND "+ KEY_CLOSE_TIME + ">=? AND " + KEY_DATE + "=?) OR (" +
				KEY_OPEN_TIME + "<=? AND "+ KEY_CLOSE_TIME + "=0 AND " + KEY_DATE + "=?) OR (" +
				KEY_OPEN_TIME + "=0 AND " + KEY_CLOSE_TIME + "<=7 AND " + KEY_DATE + "=?)",
				new String[] {_time, _time, _date, _time, _date, _tomorrow}, null, null, null);
		}
		ArrayList<Restaurant> list = new ArrayList<Restaurant>();
		if (cursor != null) {
			for(int i = 0; i < cursor.getCount(); i++){
				if(cursor.moveToNext()){
					String name = cursor.getString(0);
					double open_time = cursor.getFloat(1);
					double close_time = cursor.getFloat(2);
					int date_of_week = (Integer.parseInt(cursor.getString(3)));
					if (close_time == 0) {
						if (!cursor.isLast() && !cursor.isAfterLast() && cursor.moveToNext()) {
							if (cursor.getString(0).equals(name) && cursor.getFloat(1) == 0) {
								close_time = cursor.getFloat(2);
							} else {
								cursor.moveToPrevious();
							}
						}
					}
					
					if (open_time == 0) {
						
					}
					Restaurant r = new Restaurant(name,	open_time +"", close_time + "", date_of_week);
					Log.i("getRestList: ", r.toString());
					list.add(r);
				}
			}
		}
		return list;
	}
	
	@SuppressWarnings("unused")
	private double checkForExtension(SQLiteDatabase db, String name, int date) { 
		String tomorrow = ((date + 1) % 7) +"";
		Cursor extension = db.query(TABLE_CONTACTS, new String[] {KEY_CLOSE_TIME}, 
				KEY_NAME + "=? AND " + KEY_OPEN_TIME + "<=? AND "+ 
				KEY_CLOSE_TIME + "<=? AND " + KEY_DATE + "=?",
				new String[] {name, "0", "7", tomorrow}, null, null, null);
		if (extension == null || !extension.moveToFirst())
			return 0.0;
		else {
			return extension.getFloat(0);
		}
	}

	public int getRestCount() {
		String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public int updateRest(Restaurant restaurant) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, restaurant.getName());
		values.put(KEY_OPEN_TIME, restaurant.getOpen_time());
		values.put(KEY_CLOSE_TIME, restaurant.getClose_time());
		values.put(KEY_DATE, restaurant.getDate());
		return db.update(TABLE_CONTACTS, values, KEY_NAME + " = ?",
				new String[] { String.valueOf(restaurant.getName()) });
	}

	public List<Restaurant> getAllRest() {
		List<Restaurant> restList = new ArrayList<Restaurant>();
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				Restaurant restaurant = new Restaurant();
				restaurant.setName(cursor.getString(0));
				restaurant.setOpen_time(cursor.getString(1));
				restaurant.setClose_time(cursor.getString(2));
				restaurant.setDate(Integer.parseInt(cursor.getString(3)));
				restList.add(restaurant);
				Log.e("restaurant", restaurant.toString());
			} while (cursor.moveToNext());
		}
		return restList;
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
		Cursor cursor = db.rawQuery(selectQuery, null);
		String deletedName = "";

		if (cursor.moveToFirst()) {
			do {
				deletedName = cursor.getString(0);
				db.delete(TABLE_CONTACTS, KEY_NAME + " = ?",
						new String[] { String.valueOf(deletedName) });
			} while (cursor.moveToNext());
		}
		db.close();
	}

}
