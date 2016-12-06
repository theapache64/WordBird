package com.shifz.wordbird.models;

import java.util.List;

/**
 * Created by theapache64 on 3/12/16.
 */
public class Url {

    public static final int INDEX_THRESHOLD_IN_DAYS = 10;

    private final boolean shouldReIndex;
    private final boolean isIndexedAlready;
    private final List<String> words;

    public Url(List<String> words, boolean isIndexedAlready, boolean shouldReIndex) {
        this.words = words;
        this.shouldReIndex = shouldReIndex;
        this.isIndexedAlready = isIndexedAlready;
    }

    public boolean shouldReIndex() {
        return shouldReIndex;
    }

    public boolean isIndexedAlready() {
        return isIndexedAlready;
    }

    public List<String> getWords() {
        return words;
    }


}
