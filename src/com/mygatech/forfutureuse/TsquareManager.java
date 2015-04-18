package com.mygatech.forfutureuse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.mygatech.tsquare.TsquareArrays;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TsquareManager extends SQLiteOpenHelper {

	//Saving as a text file in emulator/0/MyGaTech/tsquare.txt
	//Saving format
	//Assignment
	//Subject;Board;Title;Due date;Posted date;Link;
	//Gradebook
	//Subject;Board;Title;Grade;Posted date;Commnet;
	//Announcement
	//Subject;Board;Title;;Posted date;Link;
	
	private static final int DATABASE_VERSION = 1;	
	private static final String DATABASE_NAME = "tsquareDB";
	private static final String TABLE_CONTACTS = "columns";
	
	private static final String KEY_NAME = "subject";
    private static final String KEY_BOARD = "board";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    private static final String KEY_LINK = "link";
    
	private static final String[] KEY_WORDS = {KEY_NAME, KEY_BOARD, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_LINK};
	
	public TsquareManager(Context context) {
   	 	super(context, DATABASE_NAME, null, DATABASE_VERSION);
   	 	
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TSQUARE_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "(" 
				+ KEY_NAME + " TEXT," + KEY_BOARD + " TEXT," + KEY_TITLE
				+ " TEXT," + KEY_CONTENT + " TEXT," + KEY_DATE + " TEXT" + KEY_LINK + " TEXT" +  ")";
		db.execSQL(CREATE_TSQUARE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		onCreate(db);
	}


	protected class TsquareClass{
		String subject, board, title, content, date, link;
		
		public TsquareClass(String subject, String board, String title, String content, String date, String link){
			this.subject = subject;
			this.board = board;
			this.title = title;
			this.content = content;
			this.date = date;
			this.link = link;
		}
		
		public ArrayList<String> getAll(){
			ArrayList<String> all = new ArrayList<String>();
			all.add(subject);
			all.add(board);
			all.add(title);
			all.add(content);
			all.add(date);
			all.add(link);
			return all;
		}
		
		public String getSubject(){
			return subject;
		}
		
		public String getBoard(){ return board;}
		public String getTitle(){ return title;}
		public String getContent(){ return content;}
		public String getDate(){ return date;}
		
	}
	/**
	 * CRUD operations
	 */
	public void addSubj(TsquareArrays ts) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		for(Map.Entry<String, String> entry : ts.getSet().entrySet()){
			values.put(entry.getKey().getClass().getName(), entry.getValue());
		}
		db.insert(TABLE_CONTACTS, null, values);
		db.close();
	}

	public TsquareClass getSubj(String name) {
		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_NAME,
				KEY_BOARD, KEY_TITLE, KEY_CONTENT, KEY_DATE, KEY_LINK }, KEY_NAME + "=?",
				new String[] { String.valueOf(name) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		TsquareClass tsquareClass = new TsquareClass(cursor.getString(0),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getString(5));
		return tsquareClass;
	}
	
	/*
	public Cursor  query (String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) 
		Parameters
	table : The table name to compile the query against. 
	columns : A list of which columns to return. Passing null will return all columns, which is discouraged to prevent reading data from storage that isn't going to be used. 
	selection : A filter declaring which rows to return, formatted as an SQL WHERE clause (excluding the WHERE itself). Passing null will return all rows for the given table. 
	selectionArgs : You may include ?s in selection, which will be replaced by the values from selectionArgs, in order that they appear in the selection. The values will be bound as Strings. 
	groupBy : A filter declaring how to group rows, formatted as an SQL GROUP BY clause (excluding the GROUP BY itself). Passing null will cause the rows to not be grouped. 
	having : A filter declare which row groups to include in the cursor, if row grouping is being used, formatted as an SQL HAVING clause (excluding the HAVING itself). Passing null will cause all row groups to be included, and is required when row grouping is not being used. 
	orderBy : How to order the rows, formatted as an SQL ORDER BY clause (excluding the ORDER BY itself). Passing null will use the default sort order, which may be unordered. 
	limit : Limits the number of rows returned by the query, formatted as LIMIT clause. Passing null denotes no LIMIT clause. 
	cancellationSignal : A signal to cancel the operation in progress, or null if none. If the operation is canceled, then OperationCanceledException will be thrown when the query is executed. 
	Returns: A Cursor object, which is positioned before the first entry. Note that Cursors are not synchronized, see the documentation for more details.

	 */
	public String[] getSubjList(){
		String[] subjList = new String[this.getSubjCount()];
		SQLiteDatabase db = this.getWritableDatabase();
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

		Cursor cursor = db.rawQuery(selectQuery, null);
		int i = 0;
		if(cursor.moveToFirst()){
			do{
				subjList[i] = cursor.getString(0);
			}while(cursor.moveToNext());
		}
		return subjList;
	}

	public int getSubjCount() {
		String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	public int updateSubj(TsquareClass tsquareClass) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		ArrayList<String> all = tsquareClass.getAll();
		int i = 0;
		for(String arg : all){
			values.put(KEY_WORDS[i], arg);
			i++;
		}
		return db.update(TABLE_CONTACTS, values, KEY_NAME + " = ?",
				new String[] { String.valueOf(tsquareClass.getSubject()) });
	}

	public List<TsquareClass> getAllSubj() {
		List<TsquareClass> tsquareList = new ArrayList<TsquareClass>();
		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				TsquareClass tsquareClass = new TsquareClass(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
				tsquareList.add(tsquareClass);
			} while (cursor.moveToNext());
		}
		return tsquareList;
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
				System.out.println(deletedName);
			} while (cursor.moveToNext());
		}
		db.close();
	}
}
