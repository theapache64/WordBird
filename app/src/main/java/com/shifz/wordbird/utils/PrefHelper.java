package com.shifz.wordbird.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class PrefHelper {

    private static final String PREFERENCE_NAME = "wordbird_pref";
    private static PrefHelper instance;
    private final SharedPreferences pref;

    private PrefHelper(final Context context) {
        this.pref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static PrefHelper getInstance(final Context context) {
        if (instance == null) {
            instance = new PrefHelper(context);
        }
        return instance;
    }

    public void savePref(final String key,final String value){
        this.pref.edit().putString(key,value).commit();
    }

    public String getPref(final String key){
       return this.pref.getString(key,null);
    }


}
