package com.shifz.wordbird.models.result;

/**
 * Created by Shifar Shifz on 10/24/2015 1:43 PM.
 */
public class Result {

    protected final String data;

    public Result(String data) {
        this.data = data;
    }

    public String string() {
        return this.data;
    }
}
