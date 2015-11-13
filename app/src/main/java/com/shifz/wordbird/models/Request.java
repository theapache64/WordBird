package com.shifz.wordbird.models;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class Request implements Serializable {

    public static final String KEY = "request";
    private String word, type;
    private String result;

    public Request(@NonNull String word, @NonNull String type, String result) {
        this.word = word;
        this.type = type;
        this.result = result;
    }

    public Request(@NonNull String word, @NonNull String type) {
        this(word, type, null);
    }

    public String getWord() {
        return this.word;
    }

    public String getType() {
        return this.type;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return this.result;
    }


    public void setWord(String word) {
        this.word = word;
    }
}
