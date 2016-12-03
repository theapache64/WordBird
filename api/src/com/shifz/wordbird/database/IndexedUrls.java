package com.shifz.wordbird.database;

/**
 * Created by theapache64 on 3/12/16.
 */
public class IndexedUrls extends BaseTable<IndexedUrls> {

    private static final IndexedUrls instance = new IndexedUrls();

    private IndexedUrls() {
        super("indexed_urls");
    }

    public static IndexedUrls getInstance() {
        return instance;
    }

}
