package com.shifz.wordbird.models;

import java.util.List;
import java.util.Set;

/**
 * Created by theapache64 on 3/12/16.
 */
public class Url {

    public static final int INDEX_THRESHOLD_IN_DAYS = 10;

    private String id;
    private final String url;
    private final boolean shouldReIndex;
    private boolean isIndexedAlready;
    private Set<String> words;
    private int wordsCount;
    private long totalTimeElapsedToFirstIndex;
    private long lastIndexedAt;

    public Url(String id, String url, Set<String> words, boolean isIndexedAlready, boolean shouldReIndex) {
        this.id = id;
        this.url = url;
        this.shouldReIndex = shouldReIndex;
        this.isIndexedAlready = isIndexedAlready;

        setWords(words);
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

    public Set<String> getWords() {
        return words;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setIsIndexedAlready(boolean isIndexedAlready) {
        this.isIndexedAlready = isIndexedAlready;
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

    public void setWords(Set<String> words) {
        this.words = words;

        if (words != null) {
            this.wordsCount = words.size();
        }
    }
}
