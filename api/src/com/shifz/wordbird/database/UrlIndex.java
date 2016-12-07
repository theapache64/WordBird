package com.shifz.wordbird.database;

import com.shifz.wordbird.models.Url;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by theapache64 on 3/12/16.
 */
public class UrlIndex extends BaseTable<Url> {

    private static final UrlIndex instance = new UrlIndex();
    public static final String COLUMN_URL = "url";
    private static final String COLUMN_IS_INDEXED = "is_indexed";
    private static final String COLUMN_TIME_ELAPSED_TO_FIRST_INDEX_IN_SEC = "time_elapsed_to_first_index_in_sec";
    private static final String COLUMN_LAST_INDEXED_AT = "last_indexed_at";
    private static final String COLUMN_AS_WORDS = "words";

    private UrlIndex() {
        super("url_index");
    }

    public static UrlIndex getInstance() {
        return instance;
    }

    @Override
    public Url get(String column, String value) {

        Url theUrl = null;

        final String query = String.format("SELECT ui.id, ui.url, ui.is_indexed, ui.last_indexed_at, GROUP_CONCAT(r.word) AS words FROM url_index ui LEFT JOIN requests r ON r.url_id = ui.id WHERE %s = ? GROUP BY ui.id LIMIT 1;", column);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String url = rs.getString(COLUMN_URL);
                final boolean isIndexed = rs.getBoolean(COLUMN_IS_INDEXED);
                final long lastIndexedAt = rs.getLong(COLUMN_LAST_INDEXED_AT);
                final String words = rs.getString(COLUMN_AS_WORDS);
                List<String> wordList = null;
                if (words != null) {
                    wordList = Arrays.asList(words.split(","));
                }

                final boolean shouldReIndex = TimeUnit.DAYS.convert(System.currentTimeMillis() - lastIndexedAt, TimeUnit.MILLISECONDS) >= Url.INDEX_THRESHOLD_IN_DAYS;
                theUrl = new Url(id, url, wordList, isIndexed, shouldReIndex);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return theUrl;
    }

     /*
                        is_indexed = true;
                        totalWords = words.size();
                        timeElapsedToFirstIndexInSec = 9;
                        lastIndexedAt = System.currentTimeInMillis()l
                         */

    @Override
    public boolean update(Url url) {
        boolean isUpdated = false;
        final String query = "UPDATE url_index SET is_indexed = ?, total_words = ? , time_elapsed_to_first_index_in_sec = ?, last_indexed_at = ? WHERE id = ?";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            ps.setBoolean(1, url.isIndexedAlready());
            ps.setInt(2, url.getWordsCount());
            ps.setLong(3, url.getTotalTimeElapsedToFirstIndex());
            ps.setLong(4, url.getLastIndexedAt());

            ps.setString(5, url.getId());

            isUpdated = ps.executeUpdate() == 1;
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isUpdated;
    }
}
