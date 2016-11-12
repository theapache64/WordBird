package com.shifz.wordbird.models;

import android.support.annotation.StringRes;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class Menu {

    private final int title, descr;
    private final String code;
    private final boolean isLoopable;

    private static final Set<String> loopableSet = new HashSet<>();

    public Menu(@StringRes int title, @StringRes int descr, String code, boolean isLoopable) {
        this.title = title;
        this.descr = descr;
        this.code = code;
        this.isLoopable = isLoopable;
        if(this.isLoopable){
            loopableSet.add(code);
        }
    }


    public int getTitle() {
        return title;
    }

    public int getDescr() {
        return descr;
    }

    public String getCode() {
        return code;
    }

    public boolean isLoopable() {
        return isLoopable;
    }

    public static Set<String> getLoopableSet() {
        return loopableSet;
    }
}