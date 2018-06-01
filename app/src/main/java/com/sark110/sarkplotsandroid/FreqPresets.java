package com.sark110.sarkplotsandroid;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class FreqPresets {
    public String getM_legend() {
        return m_legend;
    }

    public float getM_startFreq() {
        return m_startFreq;
    }

    public float getM_stopFreq() {
        return m_stopFreq;
    }

    private String m_legend;
    private float m_startFreq;
    private float m_stopFreq;

    public FreqPresets(String legend, float start, float stop)
    {
        m_legend = legend;
        m_startFreq = start;
        m_stopFreq = stop;
    }

    public static FreqPresets[] getM_FreqPresetsDef() {
        return m_FreqPresetsDef;
    }

    private static FreqPresets[] m_FreqPresetsDef = new FreqPresets[] {
            new FreqPresets("",      0,    0),                              // Null
            new FreqPresets("10 - 100 kHz",      0.01f,    0.1f),         // "10 - 100 KHz",
            new FreqPresets("600M: 500 kHz",     0.1f,   0.9f),         // "600M: 500 KHz",
            new FreqPresets("160M: 1.8 MHz",     1.3f,   2.3f),       //"160M: 1.8 MHz",
            new FreqPresets("80M: 3.6 MHz",      1.6f,   5.6f),       //"80M: 3.6 MHz",
            new FreqPresets("60M: 5.3 MHz",      3.3f,   7.3f),       //"60M: 5.3 MHz",
            new FreqPresets("40M: 7.1 MHz",      5.1f,   9.1f),       //"40M: 7.1 MHz",
            new FreqPresets("30M: 10.1 MHz",     8.1f,  12.1f),       //"30M: 10.1 MHz",
            new FreqPresets("HF RFID: 13.5 MHz", 11.5f,  15.6f),      //"HF RFID: 13.5 MHz",
            new FreqPresets("20M: 14.2 MHz",     12.2f,  16.2f),      //"20M: 14.2 MHz",
            new FreqPresets("17M: 18.1 MHz",     16.1f,  20.1f),      //"17M: 18.1 MHz",
            new FreqPresets("15M: 21.2 MHz",     19.2f,  23.2f),      //"15M: 21.2 MHz",
            new FreqPresets("12M: 24.9 MHz",     22.9f,  26.9f),      //"12M: 24.9 MHz",
            new FreqPresets("11M: 27.8 MHz",     25.8f,  29.8f),      //"11M: 27.8 MHz",
            new FreqPresets("10M: 29 MHz",       26.0f,  32.0f),      //"10M: 29 MHz",
            new FreqPresets("6M: 51 MHz",        48.0f,  54.0f),      //"6M: 51 MHz",
            new FreqPresets("4M: 70.1 MHz",      68.1f,  72.1f),      //"4M: 70.1 MHz",
            new FreqPresets("2M: 145 MHz",       142.0f, 148.0f),     //"2M: 145 MHz",
            new FreqPresets("1.25M: 223.5 MHz",    222.0f, 225.0f),   //"1.25M: 223.5 MHz",
            new FreqPresets("70cm: 435 MHz",    420.0f, 450.0f),      //"70cm: 435 MHz",
            new FreqPresets("HF",           3.0f,  30.0f),         //"Full HF",
            new FreqPresets("1 to 230MHz",         1.0f,  230.0f),
            new FreqPresets("1 to 500MHz",         1.0f,  500.0f),
            new FreqPresets("1 to 700MHz",         1.0f,  700.0f)
    };
}
