package com.shifz.wordbird.utils;

import android.app.Application;

import com.shifz.wordbird.InActivity;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class App extends Application {

    public static String apiKey = null;

    @Override
    public void onCreate() {
        super.onCreate();

        apiKey = PrefHelper.getInstance(this).getPref(InActivity.KEY_API_KEY);
    }
}
