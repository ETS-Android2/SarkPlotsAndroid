package com.sark110.sarkplotsandroid;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SARK Plots for Android software
 *
 * @author EA4FRB - Melchor Varela <melchor.varela@gmail.com>
 * Copyright 2018
 */
public class FileHelper {
    final static String fileName = "sark110.csv";
    //final static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ea4frb/sarkplots/" ;
    final static String path = "";
    final static String TAG = FileHelper.class.getName();

    public static String ReadFile(Context context) {
        String line = null;

        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while ( (line = bufferedReader.readLine()) != null )
            {
                stringBuilder.append(line + System.getProperty("line.separator"));
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
            fileInputStream.close();
        }
        catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }
        catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return line;
    }

    public static boolean exportCSV (Context context, String fileName){
        try {

            FileOutputStream fileOutputStream = context.openFileOutput(fileName, 0);
            /* write header */
            String header = "\"Zplots Generated Data\"";
            fileOutputStream.write(header.getBytes());
            fileOutputStream.write(System.getProperty("line.separator").getBytes());
            header = "\"Freq(MHz)\",\"Rs\",\"Xs\"";
            fileOutputStream.write(header.getBytes());
            fileOutputStream.write(System.getProperty("line.separator").getBytes());

            /* get data from the database and write */
            SweepDataDAO db = new SweepDataDAO(context);
            db.open();
            List<MeasureDataBin> sdata = db.getAllSweepData();
            for(int i = 0; i < sdata.size(); i++){
                    String data = String.format(Locale.ENGLISH, "%.6f", sdata.get(i).getFreq()) + "," +
                                  String.format(Locale.ENGLISH, "%.6f", sdata.get(i).getRs()) + "," +
                                  String.format(Locale.ENGLISH, "%.6f", sdata.get(i).getXs());
                fileOutputStream.write(data.getBytes());
                fileOutputStream.write(System.getProperty("line.separator").getBytes());
            }
            db.close();

            fileOutputStream.close();
            return true;
        }  catch(FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }  catch(IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return  false;
    }
}
