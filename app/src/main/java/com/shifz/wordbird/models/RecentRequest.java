package com.shifz.wordbird.models;

import android.support.annotation.NonNull;

/**
 * Created by Shifar Shifz on 10/25/2015.
 */
public class RecentRequest extends Request {

    private final String id,timeSpan;

    public RecentRequest(String id,@NonNull String word, @NonNull String type,final String timeSpan, String result) {
        super(word, type, result);
        this.id = id;
        this.timeSpan = timeSpan;
    }

    public RecentRequest( String id,@NonNull String word, @NonNull String type, final String timeSpan) {
        super(word, type);
        this.id = id;
        this.timeSpan = timeSpan;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public String getId() {
        return id;
    }
}
