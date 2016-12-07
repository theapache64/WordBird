package com.shifz.wordbird.models;

import java.util.List;

/**
 * Created by theapache64 on 3/12/16.
 */
public class Url {

    public static final int INDEX_THRESHOLD_IN_DAYS = 10;

    private String id;
    private final String url;
    private final boolean shouldReIndex;
    private boolean isIndexedAlready;
    private final List<String> words;
    private int wordsCount;
    private long totalTimeElapsedToFirstIndex;
    private long lastIndexedAt;

    public Url(String id, String url, List<String> words, boolean isIndexedAlready, boolean shouldReIndex) {
        this.id = id;
        this.url = url;
        this.words = words;
        this.shouldReIndex = shouldReIndex;
        this.isIndexedAlready = isIndexedAlready;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
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


    public void setId(String id) {
        this.id = id;
    }


    public void setIsIndexedAlready(boolean isIndexedAlready) {
        this.isIndexedAlready = isIndexedAlready;
    }

    public void setWordsCount(int wordsCount) {
        this.wordsCount = wordsCount;
    }

    public void setTotalTimeElapsedToFirstIndex(long totalTimeElapsedToFirstIndex) {
        this.totalTimeElapsedToFirstIndex = totalTimeElapsedToFirstIndex;
    }

    public void setLastIndexedAt(long lastIndexedAt) {
        this.lastIndexedAt = lastIndexedAt;
    }

    public int getWordsCount() {
        return wordsCount;
    }

    public long getTotalTimeElapsedToFirstIndex() {
        return totalTimeElapsedToFirstIndex;
    }

    public long getLastIndexedAt() {
        return lastIndexedAt;
    }

    @Override
    public String toString() {
        return "Url{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", shouldReIndex=" + shouldReIndex +
                ", isIndexedAlready=" + isIndexedAlready +
                ", words=" + words +
                ", wordsCount=" + wordsCount +
                ", totalTimeElapsedToFirstIndex=" + totalTimeElapsedToFirstIndex +
                ", lastIndexedAt=" + lastIndexedAt +
                '}';
    }
}
