/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */

package com.sark110.sarkplotsandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SweepDatabaseHelper extends SQLiteOpenHelper{
	public static final String TABLE_SWEEPDATA = "sweepdata";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_FREQ = "freq";
	public static final String COLUMN_RS = "rs";
	public static final String COLUMN_XS = "xs";

	private static final String DATABASE_NAME = "sweep.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SWEEPDATA + "( " 
			+ COLUMN_ID	+ " integer primary key, " 
			+ COLUMN_FREQ + " real, "
			+ COLUMN_RS + " real, "
			+ COLUMN_XS + " real );";
	
	public SweepDatabaseHelper(Context context){
		super(context,DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	  public void onCreate(SQLiteDatabase database) {
	    database.execSQL(DATABASE_CREATE);
	  }
	
	@Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(SweepDatabaseHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SWEEPDATA);
	    onCreate(db);
	  }	
	
	
}
