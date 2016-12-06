package com.shifz.wordbird.database;

import com.shifz.wordbird.models.Url;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private UrlIndex() {
        super("url_index");
    }

    public static UrlIndex getInstance() {
        return instance;
    }

    @Override
    public Url get(String column, String value) {
        Url theUrl = null;
        final String query = String.format("f", column);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, value);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {

                final String id = rs.getString(COLUMN_ID);
                final String url = rs.getString(COLUMN_URL);
                final boolean isIndexed = rs.getBoolean(COLUMN_IS_INDEXED);
                final long timeElapsedToFirstIndexInSeconds = rs.getLong(COLUMN_TIME_ELAPSED_TO_FIRST_INDEX_IN_SEC);
                final long lastIndexedAt = rs.getLong(COLUMN_LAST_INDEXED_AT);

                final boolean shouldReIndex = TimeUnit.DAYS.convert(System.currentTimeMillis() - lastIndexedAt, TimeUnit.MILLISECONDS) >= Url.INDEX_THRESHOLD_IN_DAYS;
                theUrl = new Url()
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
}
