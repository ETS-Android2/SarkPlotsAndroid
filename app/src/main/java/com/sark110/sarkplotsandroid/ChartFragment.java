package com.sark110.sarkplotsandroid;

import com.sark110.sarkplotsandroid.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class ChartFragment extends Fragment{


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.chart_fragment, container, false);
	}

}
