/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */

package com.sark110.sarkplotsandroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends AppCompatActivity implements DataUpdateListener, OnItemSelectedListener {

	private int mNumSteps;
	private boolean mIsSingleSweep = false;
	private boolean mSweepRunning = false;
	private boolean mIsBluetooth = false;
	private int mSweepIdx;
	private ParamFragment mMyFrag = null;
	private ChartMaker mCharter;
	private Sweeper mSweeper = null;
	private DeviceIntf mDeviceIntf;
	private int mLeftPlot;
	private int mRightPlot;
	private FreqPresets[] mFreqPresets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.default_preferences, false);
		setContentView(R.layout.activity_main);

		/* Get stored preferences */
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final EditText et1=(EditText)findViewById(R.id.editTextStartFreq);
		final EditText et2=(EditText)findViewById(R.id.editTextStopFreq);
		et1.setText(prefs.getString("pref_startFreq", Float.toString(GblDefs.DEF_FREQ_START)));
		et2.setText(prefs.getString("pref_stopFreq", Float.toString(GblDefs.DEF_FREQ_STOP)));
		final float startFreq = et1.getText().toString().isEmpty()?GblDefs.DEF_FREQ_START:Float.valueOf(et1.getText().toString());
		final float stopFreq = et2.getText().toString().isEmpty()?GblDefs.DEF_FREQ_STOP:Float.valueOf(et2.getText().toString());
		/* Validation */
		et1.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if( et1.getText().toString().length() == 0 )
					et1.setError("Empty value");
				else if (Float.valueOf(et1.getText().toString()) < GblDefs.MIN_FREQ)
					et1.setError("Below min");
				else if (Float.valueOf(et1.getText().toString()) > (stopFreq - GblDefs.MIN_SPAN))
					et1.setError("Above stop");
				else if (Float.valueOf(et1.getText().toString()) > (GblDefs.MAX_FREQ - GblDefs.MIN_SPAN))
					et1.setError("Above max");
			}
		});
		et2.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if( et2.getText().toString().length() == 0 )
					et2.setError("Empty value");
				else if (Float.valueOf(et2.getText().toString()) < (GblDefs.MIN_FREQ + GblDefs.MIN_SPAN))
					et2.setError("Below min");
				else if (Float.valueOf(et2.getText().toString()) < (startFreq + GblDefs.MIN_SPAN))
					et2.setError("Below start");
				else if (Float.valueOf(et2.getText().toString()) > (GblDefs.MAX_FREQ))
					et2.setError("Above max");
			}
		});

		mLeftPlot = (int)prefs.getInt("LeftPlot", GblDefs.PLOT_VSWR);
		mRightPlot = (int)prefs.getInt("RightPlot", GblDefs.PLOT_ZS_MAG);

		/* Spinners for left and right plot */
		Spinner spinner = findViewById(R.id.spinnerLeftPlot);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				R.array.plot_param_list_item_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setSelection(mLeftPlot);
		spinner.setOnItemSelectedListener(this);
		spinner = (Spinner) findViewById(R.id.spinnerRightPlot);
		spinner.setAdapter(adapter);
		spinner.setSelection(mRightPlot);
		spinner.setOnItemSelectedListener(this);

		/* Spinner for presets */
		mFreqPresets = FreqPresets.getM_FreqPresetsDef();
		spinner = findViewById(R.id.spinnerPresets);
		java.util.ArrayList<String> legendPresets = new java.util.ArrayList<>();
		for (int i = 0; i  < mFreqPresets.length; i++)
			legendPresets.add(mFreqPresets[i].getM_legend());
		ArrayAdapter<String> adapterPresets = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, legendPresets);
		adapterPresets.setDropDownViewResource(android.R.layout.simple_spin‌​ner_dropdown_item);
		spinner.setAdapter(adapterPresets);
		spinner.setSelection(0);
		spinner.setOnItemSelectedListener(this);

		/* Draws chart with previously stored data */
		drawChart();

		/* Setup device interface */
		mIsBluetooth = prefs.getBoolean("pref_Bluetooth", false);
		if (mIsBluetooth)
			mDeviceIntf = new BluetoothLEIntf(this);
		else
			mDeviceIntf = new USBIntf(this);
		mDeviceIntf.onCreate();
		// Setup listener for connection events from the device
		mDeviceIntf.setDeviceIntfListener(new DeviceIntf.DeviceIntfListener() {
			@Override
			public void onConnectionStateChanged(DeviceIntf helper, final boolean isConnected) {
				TextView text = (TextView) findViewById(R.id.connect_text);
				if (isConnected)
					text.setText(getResources().getString(R.string.pf_connected));
				else {
					text.setText(getResources().getString(R.string.pf_disconnected));
				}
			}
		});

		if(savedInstanceState == null){
			mMyFrag = (ParamFragment)getSupportFragmentManager().findFragmentById(R.id.paramFragment);
		}
		if (savedInstanceState != null)   {
			mMyFrag = (ParamFragment) getSupportFragmentManager().getFragment(savedInstanceState, "paramFragment");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}
	
	@Override
	protected void onSaveInstanceState( Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		mMyFrag = (ParamFragment)getSupportFragmentManager().findFragmentById(R.id.paramFragment);
		getSupportFragmentManager().putFragment(savedInstanceState, "paramFragment", mMyFrag);
		
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	  super.onRestoreInstanceState(savedInstanceState);
	  mMyFrag = (ParamFragment) getSupportFragmentManager().getFragment(savedInstanceState, "myFragment");

	}
	
	@Override
	public void onPause(){
		super.onPause();
	}

	@Override
	public void onResume(){
		super.onResume();
		mDeviceIntf.onResume();
		mDeviceIntf.connect();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent settingIntent =  new Intent(this, SettingsActivity.class);
			startActivity(settingIntent);
			return true;
		}
        if(id == R.id.aboutbox){
            AboutBox.Show(MainActivity.this);
            return true;
        }
		if(id == R.id.action_exit){
			mDeviceIntf.close();
			finish();
			System.exit(0);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view,
							   int pos, long id) {
		if (parent.getId() == R.id.spinnerLeftPlot) {
			Spinner spinner = (Spinner) findViewById(R.id.spinnerLeftPlot);
			mLeftPlot = spinner.getSelectedItemPosition();
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
			editor.putInt("LeftPlot", mLeftPlot);
			editor.commit();
			drawChart();
		}
		else if (parent.getId() == R.id.spinnerRightPlot) {
			Spinner spinner = (Spinner) findViewById(R.id.spinnerRightPlot);
			mRightPlot = spinner.getSelectedItemPosition();
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
			editor.putInt("RightPlot", mRightPlot);
			editor.commit();
			drawChart();
		}
		else if (parent.getId() == R.id.spinnerPresets) {
			Spinner spinner = (Spinner) findViewById(R.id.spinnerPresets);
			int preset = spinner.getSelectedItemPosition();
			if (preset!=0) {
				EditText et1 = (EditText) findViewById(R.id.editTextStartFreq);
				EditText et2 = (EditText) findViewById(R.id.editTextStopFreq);
				et1.setText(String.valueOf(mFreqPresets[preset].getM_startFreq()));
				et2.setText(String.valueOf(mFreqPresets[preset].getM_stopFreq()));
			}
		}
	}

    @Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}


	@Override
	public void SweepDataUpdated(MeasureDataBin data){
		/* Updates progress bar */
		ProgressBar progressBar =(ProgressBar)findViewById(R.id.progressBar);
		progressBar.setProgress((mSweepIdx++ * 100)/ mNumSteps);

		/* Updates chart */
		drawChart();

		/* End sweep */
		if(data == null){
			mSweepRunning = false;
			if (mIsSingleSweep == false)
				launchSweeper();
		}
	}

	public void onSingleSweepClick(View v){
		
		if(mSweepRunning){
			mSweepRunning = false;
			mSweeper.cancel(true);
		}
		else {
			mIsSingleSweep = true;
			launchSweeper();
		}
	}

	public void onContSweepClick(View v){

		if(mSweepRunning){
			mSweepRunning = false;
			mSweeper.cancel(true);
		}
		else {
			mIsSingleSweep = false;
			launchSweeper();
		}
	}

	private void launchSweeper ()
	{
		mSweepRunning = true;
		mSweepIdx = 0;

		// Get Values for Sweeping
		EditText et1=(EditText)findViewById(R.id.editTextStartFreq);
		EditText et2=(EditText)findViewById(R.id.editTextStopFreq);
		float startFreq = et1.getText().toString().isEmpty()?GblDefs.DEF_FREQ_START:Float.valueOf(et1.getText().toString());
		float stopFreq = et2.getText().toString().isEmpty()?GblDefs.DEF_FREQ_STOP:Float.valueOf(et2.getText().toString());
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		mNumSteps = Integer.valueOf(prefs.getString("pref_Steps", getString(R.string.pf_default_steps)));

		mSweeper = new Sweeper(this, mDeviceIntf, mNumSteps, startFreq, stopFreq);
		mSweeper.addListener(this);
		mSweeper.execute();
	}


	public void drawChart(){
		mCharter =new ChartMaker(this);
		LineChart chart=(LineChart)findViewById(R.id.chart);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		float refImp = Float.valueOf(prefs.getString("pref_RefImp", getString(R.string.pf_default_ref_imp)));

		mCharter.drawGraph(chart, mLeftPlot, mRightPlot, refImp);
		float f = mCharter.getFreqMin();
		float min = mCharter.getVswrMin();

		TextView tvFreq = (TextView)findViewById(R.id.dispFreq);
		TextView tvVswr = (TextView)findViewById(R.id.dispVswr);
		tvFreq.setText( String.format("%1$,.3f MHz",f) );
		tvVswr.setText( String.format("%1$,.2f : 1",min) );
	}


	/*
	public void onUsbClick(View v){
		Intent intent = new Intent(this, SerialConsoleActivity.class);
		startActivity(intent);
	}
*/

	////////////////////////////////
	// Useful Message Boxes
	/////////////////////////////////
	
	public void popMessage(View v){
		popMessage("Pop-up", "Something activated me!");
	}
	
	public void popMessage(int title_id,int message_id){
		String title= getResources().getString(title_id);
		String message= getResources().getString(message_id);
		popMessage(title,message);
	}
	
	public void popMessage(String title,String message){
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"OK", new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
			      //alertDialog.cancel();
			   }
			});
		// Set the Icon for the Dialog
		//alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
	}

}
