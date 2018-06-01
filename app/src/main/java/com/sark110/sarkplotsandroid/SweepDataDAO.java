package com.sark110.sarkplotsandroid;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class SweepDataDAO {

  // Database fields
  private SQLiteDatabase database;
  private SweepDatabaseHelper dbHelper;
  private String[] allColumns = { SweepDatabaseHelper.COLUMN_ID,
      SweepDatabaseHelper.COLUMN_FREQ,
      SweepDatabaseHelper.COLUMN_RS,
      SweepDatabaseHelper.COLUMN_XS
  };

  public SweepDataDAO(Context context) {
    dbHelper = new SweepDatabaseHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  // Don't use this yet!
  /*
  public SweepData createSweepData(float freq, float vswr) {
    ContentValues values = new ContentValues();
    values.put(SweepDatabaseHelper.COLUMN_FREQ, freq);
    values.put(SweepDatabaseHelper.COLUMN_VSWR, vswr);
    long insertId = database.insert(SweepDatabaseHelper.TABLE_SWEEPDATA, null,
        values);
    Cursor cursor = database.query(SweepDatabaseHelper.TABLE_SWEEPDATA,
        allColumns, SweepDatabaseHelper.COLUMN_ID + " = " + insertId, null,
        null, null, null);
    cursor.moveToFirst();
    SweepData newSweepData = cursorToSweepData(cursor);
    cursor.close();
    return newSweepData;
  }
  */
  public void clearData(){
	database.delete(SweepDatabaseHelper.TABLE_SWEEPDATA, null, null); 
  }
  
  public void insertData(MeasureDataBin row){
	  ContentValues values = new ContentValues();
	  values.put(SweepDatabaseHelper.COLUMN_ID, row.getId());
	  values.put(SweepDatabaseHelper.COLUMN_FREQ, row.getFreq());
	  values.put(SweepDatabaseHelper.COLUMN_RS, row.getRs());
	  values.put(SweepDatabaseHelper.COLUMN_XS, row.getXs());

	  database.insert(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
  }
  

  public void deleteSweepData(MeasureDataBin data) {
    long id = data.getId();
    System.out.println("SweepData deleted with id: " + id);
    database.delete(SweepDatabaseHelper.TABLE_SWEEPDATA, SweepDatabaseHelper.COLUMN_ID
        + " = " + id, null);
  }

  public List<MeasureDataBin> getAllSweepData() {
    List<MeasureDataBin> rows = new ArrayList<MeasureDataBin>();

    Cursor cursor = database.query(SweepDatabaseHelper.TABLE_SWEEPDATA,
        allColumns, null, null, null, null, null);

    cursor.moveToFirst();
    while (!cursor.isAfterLast()) {
        MeasureDataBin data = cursorToSweepData(cursor);
    	rows.add(data);
    	cursor.moveToNext();
    }
    // make sure to close the cursor
    cursor.close();
    return rows;
  }

  private MeasureDataBin cursorToSweepData(Cursor cursor) {
      MeasureDataBin data = new MeasureDataBin();
    data.setId(cursor.getLong(0));
    data.setFreq(cursor.getFloat(1));
    data.setZs(cursor.getFloat(2), cursor.getFloat(3));
    return data;
  }
  
}