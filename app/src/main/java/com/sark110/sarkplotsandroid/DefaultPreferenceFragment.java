package com.sark110.sarkplotsandroid;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class DefaultPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.default_preferences);
    }
}
