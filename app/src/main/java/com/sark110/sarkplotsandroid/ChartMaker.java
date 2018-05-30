/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */

package com.sark110.sarkplotsandroid;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Color;
import android.content.Context;
import android.view.MotionEvent;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class ChartMaker implements OnChartGestureListener, OnChartValueSelectedListener {
	private LineChart mChart;
	private LineDataSet mDataLeft;
	private LineDataSet mDataRight;
	public Context mContext;
	private float mVswrMin = 10.0F;
	private float mFreqMin = 1.0F;

	public ChartMaker(Context context){
		this.mContext =context;
	}
	
	public float getVswrMin(){
		return mVswrMin;
	}
	
	public float getFreqMin(){
		return mFreqMin;
	}
	
	public void readSweepData(int left, int right, float refImp){
		mVswrMin = 10.0F;
		mFreqMin = 1.0F;
		SweepDataDAO db = new SweepDataDAO(mContext);
		ArrayList<Entry> datapointsLeft = new ArrayList<Entry>();
		ArrayList<Entry> datapointsRight = new ArrayList<Entry>();
		db.open();
		List<MeasureDataBin> sdata = db.getAllSweepData();
		for(int i =0; i<sdata.size(); i++){
			float valLeft, valRight;
			sdata.get(i).set_RefImp(refImp);
			switch (left)
			{
				case GblDefs.PLOT_RS:
					valLeft = sdata.get(i).getRs();
					break;
				case GblDefs.PLOT_XS:
					valLeft = sdata.get(i).getXs();
					break;
				case GblDefs.PLOT_ZS_MAG:
					valLeft = sdata.get(i).getZsMag();
					break;
				case GblDefs.PLOT_ZS_ANGLE:
					valLeft = sdata.get(i).getZsAngle();
					break;
				default:
				case GblDefs.PLOT_VSWR:
					valLeft = sdata.get(i).getVswr();
					break;
				case GblDefs.PLOT_RHO_MAG:
					valLeft = sdata.get(i).getRhMag();
					break;
				case GblDefs.PLOT_RHO_ANGLE:
					valLeft = sdata.get(i).getRhAngle();
					break;
				case GblDefs.PLOT_REF_PWR:
					valLeft = sdata.get(i).getRefPwr();
					break;
				case GblDefs.PLOT_RL:
					valLeft = sdata.get(i).getRL();
					break;
				case GblDefs.PLOT_CL:
					valLeft = sdata.get(i).getCL();
					break;
				case GblDefs.PLOT_Q:
					valLeft = sdata.get(i).getQ();
					break;
				case GblDefs.PLOT_CS:
					valLeft = sdata.get(i).getCs();
					break;
				case GblDefs.PLOT_LS:
					valLeft = sdata.get(i).getLs();
					break;
			}
			switch (right)
			{
				case GblDefs.PLOT_RS:
					valRight = sdata.get(i).getRs();
					break;
				case GblDefs.PLOT_XS:
					valRight = sdata.get(i).getXs();
					break;
				case GblDefs.PLOT_ZS_MAG:
					valRight = sdata.get(i).getZsMag();
					break;
				case GblDefs.PLOT_ZS_ANGLE:
					valRight = sdata.get(i).getZsAngle();
					break;
				default:
				case GblDefs.PLOT_VSWR:
					valRight = sdata.get(i).getVswr();
					break;
				case GblDefs.PLOT_RHO_MAG:
					valRight = sdata.get(i).getRhMag();
					break;
				case GblDefs.PLOT_RHO_ANGLE:
					valRight = sdata.get(i).getRhAngle();
					break;
				case GblDefs.PLOT_REF_PWR:
					valRight = sdata.get(i).getRefPwr();
					break;
				case GblDefs.PLOT_RL:
					valRight = sdata.get(i).getRL();
					break;
				case GblDefs.PLOT_CL:
					valRight = sdata.get(i).getCL();
					break;
				case GblDefs.PLOT_Q:
					valRight = sdata.get(i).getQ();
					break;
				case GblDefs.PLOT_CS:
					valRight = sdata.get(i).getCs();
					break;
				case GblDefs.PLOT_LS:
					valRight = sdata.get(i).getLs();
					break;
			}

			Entry eLeft= new Entry(sdata.get(i).getFreq(), valLeft, (int)sdata.get(i).getId());
			datapointsLeft.add(eLeft);
			Entry eRight= new Entry(sdata.get(i).getFreq(), valRight, (int)sdata.get(i).getId());
			datapointsRight.add(eRight);
			if(sdata.get(i).getVswr() < mVswrMin && sdata.get(i).getVswr() > 0.5F ){
				mVswrMin = sdata.get(i).getVswr();
				mFreqMin = sdata.get(i).getFreq();
			}
		}

		String[] plot_param = mContext.getResources().getStringArray(R.array.plot_param_list_item_array);
		mDataLeft = new LineDataSet(datapointsLeft,plot_param[left]);
		mDataRight = new LineDataSet(datapointsRight,plot_param[right]);
		db.close();
	}

	public void drawGraph(LineChart chart, int left, int right, float refImp){
		
		if(chart == null){
			return;
		}
		mChart = chart;

		readSweepData(left, right, refImp);
		int background = Color.WHITE;
		int foreground = Color.BLUE;

//		chart.setOnChartGestureListener(this);
//		chart.setOnChartValueSelectedListener(this);

		// enable touch gestures
		chart.setTouchEnabled(true);
/*		// enable scaling and dragging
		chart.setDragEnabled(true);
		chart.setScaleEnabled(true);
		// if disabled, scaling can be done on x- and y-axis separately
		chart.setPinchZoom(true);
*/
		chart.setBackgroundColor(background);
		chart.setGridBackgroundColor(background);

        chart.getDescription().setEnabled(false);
		chart.setDrawBorders(true);


		// create a custom MarkerView (extend MarkerView) and specify the layout
		// to use for it
		MyMarkerView mv = new MyMarkerView(mContext, R.layout.custom_marker_view);
		mv.setChartView(chart); // For bounds control
		chart.setMarker(mv); // Set the marker to the chart

		if(mDataLeft == null || mDataLeft.getEntryCount() < 1 )
			return;
		
		mDataLeft.setAxisDependency(AxisDependency.LEFT);
		mDataLeft.setColor(Color.BLUE);
		mDataLeft.setCircleColor(Color.BLUE);
		mDataLeft.setCircleRadius(1.0F);
		mDataLeft.setValueTextColor(Color.BLUE);
		mDataLeft.setLineWidth(2.0F);

		mDataRight.setAxisDependency(AxisDependency.RIGHT);
		mDataRight.setColor(Color.RED);
		mDataRight.setCircleColor(Color.RED);
		mDataRight.setCircleRadius(1.0F);
		mDataRight.setValueTextColor(Color.RED);
		mDataRight.setLineWidth(2.0F);

		/* Customizations */
		Legend legend = chart.getLegend();
		legend.setTextSize(15f);
		YAxis yAxis = chart.getAxisLeft();
		yAxis.setTextSize(15F);
		yAxis = chart.getAxisRight();
		yAxis.setTextSize(15F);
		XAxis xAxis = chart.getXAxis();
		xAxis.setTextSize(15F);
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

		/* Populates data */
		ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
		dataSets.add(mDataLeft);
		dataSets.add(mDataRight);

		LineData data = new LineData(dataSets);
		chart.setData(data);
		chart.invalidate();
		
	}
	@Override
	public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
	}

	@Override
	public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
		// un-highlight values after the gesture is finished and no single-tap
		if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
			mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
	}

	@Override
	public void onChartLongPressed(MotionEvent me) {
	}

	@Override
	public void onChartDoubleTapped(MotionEvent me) {
	}

	@Override
	public void onChartSingleTapped(MotionEvent me) {
	}

	@Override
	public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
	}

	@Override
	public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
	}

	@Override
	public void onChartTranslate(MotionEvent me, float dX, float dY) {
	}

	@Override
	public void onValueSelected(Entry e, Highlight h) {
	}

	@Override
	public void onNothingSelected() {
	}
}
