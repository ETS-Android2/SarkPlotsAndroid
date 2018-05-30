/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */

package com.sark110.sarkplotsandroid;

public class GblDefs {
    static final short PLOT_RS = 0;
    static final short PLOT_XS = 1;
    static final short PLOT_ZS_MAG = 2;
    static final short PLOT_ZS_ANGLE = 3;
    static final short PLOT_VSWR = 4;
    static final short PLOT_RHO_MAG = 5;
    static final short PLOT_RHO_ANGLE = 6;
    static final short PLOT_REF_PWR = 7;
    static final short PLOT_RL = 8;
    static final short PLOT_CL = 9;
    static final short PLOT_Q = 10;
    static final short PLOT_CS = 11;
    static final short PLOT_LS = 12;

    static final float MAX_FREQ = 700.0f;
    static final float MIN_FREQ = 0.01f;
    static final float MIN_SPAN = 0.01f;

    static final float DEF_FREQ_START = 3.0f;
    static final float DEF_FREQ_STOP = 30.0f;

    static final int MIN_STEPS = 5;
    static final int MAX_STEPS = 2000;

    static final float DEF_REF_IMP = 50.0f;
    static final float MAX_REF_IMP = 1000.0f;
}

