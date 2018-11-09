package com.sark110.sarkplotsandroid;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.List;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class Sweeper extends AsyncTask<Void,MeasureDataBin,Void> implements DataUpdateListener{
	public final static String USB_CONSOLE = "usbConsole";
	
	private int mSteps;
	private float mStartFreq;
	private float mStopFreq;
	private Context mContext;
	private List<DataUpdateListener> mListeners = new ArrayList<DataUpdateListener>();
	private SQLiteDatabase mDb;
	private SweepDatabaseHelper mDbh;
	private DeviceIntf mDeviceIntf;
	private ArrayList<MeasureDataBin> mInComing =new ArrayList<MeasureDataBin>();
	private boolean mFastScan;

	Sweeper(Context context, DeviceIntf deviceIntf, int steps, float startFreq, float stopFreq, boolean fastScan){
		this.mContext = context;
		this.mDeviceIntf = deviceIntf;
		this.mFastScan = fastScan;

		if (steps < GblDefs.MIN_STEPS)
			steps = GblDefs.MIN_STEPS;
		else if (steps > GblDefs.MAX_STEPS)
			steps = GblDefs.MAX_STEPS;
		this.mSteps = steps;

		if (startFreq < GblDefs.MIN_FREQ || stopFreq > GblDefs.MAX_FREQ ||
				startFreq > (stopFreq -GblDefs.MIN_SPAN) ||
				stopFreq < (startFreq +GblDefs.MIN_SPAN)
				) {
			this.mStartFreq = GblDefs.DEF_FREQ_START;
			this.mStopFreq = GblDefs.DEF_FREQ_STOP;
		}
		else {
			this.mStartFreq = startFreq;
			this.mStopFreq = stopFreq;
		}
	}
	
	public void addListener(DataUpdateListener listener){
		this.mListeners.add(listener);
	}
	
	/**
	 * Use this to load data buffered in mInComing
	 */
	private void loadData(){
		try{
			mDbh = new SweepDatabaseHelper(mContext);
			mDb = mDbh.getWritableDatabase();
			
			// empty the table
			mDb.delete(SweepDatabaseHelper.TABLE_SWEEPDATA, null, null);
			// start writing the data
			for(int i = 0; i< mInComing.size(); i++){
				ContentValues values = new ContentValues();
				MeasureDataBin sdata=new MeasureDataBin( (long)i, mInComing.get(i).getFreq(), mInComing.get(i).getRs(), mInComing.get(i).getXs());
				values.put(SweepDatabaseHelper.COLUMN_ID, sdata.getId());
				values.put(SweepDatabaseHelper.COLUMN_FREQ, sdata.getFreq());
				values.put(SweepDatabaseHelper.COLUMN_RS, sdata.getRs());
				values.put(SweepDatabaseHelper.COLUMN_XS, sdata.getXs());
				mDb.insert(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
			}
			mDbh.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(mDb != null && mDbh != null && mDb.isOpen()){
				mDb.close();
			}	
		}
	}

	/**
	 * These implement the AsyncTask, and will spin off in a new thread
	 *
	 */
	@Override
	protected Void doInBackground(Void... voids) {
		try{
			SweepDataDAO dbr = new SweepDataDAO(mContext);
			dbr.open();
			List<MeasureDataBin> data = dbr.getAllSweepData();
			int dbSize = data.size();
			dbr.close();

			float freqstep = (mStopFreq - mStartFreq)/ mSteps;
			
			mDbh = new SweepDatabaseHelper(mContext);
			mDb = mDbh.getWritableDatabase();

			if (mSteps != dbSize) {
				/* empty the table if num mSteps changed*/
				mDb.delete(SweepDatabaseHelper.TABLE_SWEEPDATA, null, null);
			}
			/* Sweep loop */
			if (mFastScan) {
				for (int i = 0; i < mSteps;) {
					if (isCancelled()) {
						break;
					}
					ContentValues values = new ContentValues();
					MeasureDataBin[] sdata = mDeviceIntf.MeasureCmdExt((mStartFreq + (i * freqstep)), freqstep);
					if (sdata == null)
						break;
					sdata[0].setId(i);
					values.put(SweepDatabaseHelper.COLUMN_ID, (long) i);
					values.put(SweepDatabaseHelper.COLUMN_FREQ, mStartFreq + (i * freqstep));
					values.put(SweepDatabaseHelper.COLUMN_RS, sdata[0].getRs());
					values.put(SweepDatabaseHelper.COLUMN_XS, sdata[0].getXs());
					mDb.replace(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
					publishProgress(sdata[0]);
					if (++i >= mSteps)
						break;
					sdata[1].setId(i);
					values.put(SweepDatabaseHelper.COLUMN_ID, (long) i);
					values.put(SweepDatabaseHelper.COLUMN_FREQ, mStartFreq + (i * freqstep));
					values.put(SweepDatabaseHelper.COLUMN_RS, sdata[1].getRs());
					values.put(SweepDatabaseHelper.COLUMN_XS, sdata[1].getXs());
					mDb.replace(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
					publishProgress(sdata[1]);
					if (++i >= mSteps)
						break;
					sdata[2].setId(i);
					values.put(SweepDatabaseHelper.COLUMN_ID, (long) i);
					values.put(SweepDatabaseHelper.COLUMN_FREQ, mStartFreq + (i * freqstep));
					values.put(SweepDatabaseHelper.COLUMN_RS, sdata[2].getRs());
					values.put(SweepDatabaseHelper.COLUMN_XS, sdata[2].getXs());
					mDb.replace(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
					publishProgress(sdata[2]);
					if (++i >= mSteps)
						break;
					sdata[3].setId(i);
					values.put(SweepDatabaseHelper.COLUMN_ID, (long) i);
					values.put(SweepDatabaseHelper.COLUMN_FREQ, mStartFreq + (i * freqstep));
					values.put(SweepDatabaseHelper.COLUMN_RS, sdata[3].getRs());
					values.put(SweepDatabaseHelper.COLUMN_XS, sdata[3].getXs());
					mDb.replace(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
					publishProgress(sdata[3]);
					if (++i >= mSteps)
						break;
				}
			}
			else {
				for (int i = 0; i < mSteps; i++) {
					if (isCancelled()) {
						break;
					}
					ContentValues values = new ContentValues();
					MeasureDataBin sdata = mDeviceIntf.MeasureCmd((mStartFreq + (i * freqstep)));
					if (sdata == null)
						break;
					sdata.setId(i);
					values.put(SweepDatabaseHelper.COLUMN_ID, (long) i);
					values.put(SweepDatabaseHelper.COLUMN_FREQ, mStartFreq + (i * freqstep));
					values.put(SweepDatabaseHelper.COLUMN_RS, sdata.getRs());
					values.put(SweepDatabaseHelper.COLUMN_XS, sdata.getXs());
					mDb.replace(SweepDatabaseHelper.TABLE_SWEEPDATA, null, values);
					publishProgress(sdata);
				}
			}
			mDbh.close();
			return null;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}finally{
			if(mDb != null && mDbh != null && mDb.isOpen()){
				mDb.close();
			}
		}
	}
	
	@Override
	protected void onProgressUpdate(MeasureDataBin...datas){
		for(DataUpdateListener du: mListeners){
			du.SweepDataUpdated(datas[0]);
		}
	}
	
	@Override
	protected void onPostExecute(Void v) {
		for(DataUpdateListener du: mListeners){
			du.SweepDataUpdated(null);
		}
	}
	
	/**
	 * End of AsyncTask methods
	 */

	// Listeners for connection data
	@Override
	public void SweepDataUpdated(MeasureDataBin data) {
		if(data != null){
			mInComing.add(data);
			for(DataUpdateListener du: mListeners){
				du.SweepDataUpdated(data);
			}
		}else{
			loadData();
			for(DataUpdateListener du: mListeners){
				du.SweepDataUpdated(null);
			}
		}
	}

}
