package com.sark110.sarkplotsandroid;

import com.sark110.sarkplotsandroid.R;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class ParamFragment extends Fragment{


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
        return inflater.inflate(R.layout.param_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    if (savedInstanceState != null) {
			EditText startfreq = Objects.requireNonNull(getActivity()).findViewById(R.id.editTextStartFreq);
			EditText stopfreq = getActivity().findViewById(R.id.editTextStopFreq);
			TextView freq = getActivity().findViewById(R.id.dispFreq);
			TextView vswr = getActivity().findViewById(R.id.dispVswr);
			
			startfreq.setText(savedInstanceState.getCharSequence("startfreq"));
			stopfreq.setText(savedInstanceState.getCharSequence("stopfreq"));
	    }
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle savedInstanceState){
		super.onSaveInstanceState(savedInstanceState);
		EditText startfreq = Objects.requireNonNull(getActivity()).findViewById(R.id.editTextStartFreq);
		EditText stopfreq = getActivity().findViewById(R.id.editTextStopFreq);
		TextView freq = getActivity().findViewById(R.id.dispFreq);
		TextView vswr = getActivity().findViewById(R.id.dispVswr);
		
		savedInstanceState.putCharSequence("StartFreq",startfreq.getText());
		savedInstanceState.putCharSequence("stopfreq", stopfreq.getText());
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

}
