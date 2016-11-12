package com.shifz.wordbird.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;

import com.shifz.wordbird.models.RecentRequest;
import com.shifz.wordbird.models.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordbird";
    private static final int DATABASE_VERSION = 1;
    private static DBHelper ourInstance;


    private final static String CREATE_TABLE_REQUESTS = "CREATE TABLE requests(" +
            " id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            " word VARCHAR(50) NOT NULL, " +
            " type VARCHAR(30) CHECK ( type IN ('Synonym','Opposite','Meaning','Rhyme','Sentence','Plural','Singular','Past','Present','Start','End','Contain')) NOT NULL, " +
            " result TEXT, " +
            " is_deleted TINYINT NOT NULL DEFAULT 0," +
            " created_at TEXT NOT NULL" +
            " );";

    public static DBHelper getInstance(final Context context) {
        if (ourInstance == null) {
            ourInstance = new DBHelper(context);
        }
        return ourInstance;
    }

    private DBHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_REQUESTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS requests");
        onCreate(db);
    }

    /**
     * Return request if it's already added in db
     *
     * @param request a Request object with getWord()!=null and getType()!=null;
     * @return request with result and isSuccess if exists otherwirse NULL
     */
    public Request getRequest(final Request request) {
        final String query = "SELECT result FROM requests WHERE word = ? AND type = ?";
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor requestCursor = db.rawQuery(query, new String[]{request.getWord(), request.getType()});
        if (requestCursor != null && requestCursor.moveToFirst()) {

            //Has valid data
            final String result = requestCursor.getString(0);

            final Request dbRequest = new Request(request.getWord(), request.getType(), result);

            //Closing cursor
            requestCursor.close();

            //Returning modified request
            return dbRequest;
        }
        return null;
    }

    public void addRequest(final Request request) {
        final String query = "INSERT INTO requests (word,type,result,created_at) VALUES (?,?,?,?);";
        final SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query, new String[]{
                request.getWord(),
                request.getType(),
                request.getResult(),
                String.valueOf(System.currentTimeMillis())
        });
    }

    public List<RecentRequest> getRecentRequests() {

        final String query = "SELECT id,word,type,created_at FROM requests r WHERE r.result NOTNULL AND is_deleted = 0 ORDER BY r.id DESC";
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor recentCursor = db.rawQuery(query, null);
        if (recentCursor != null && recentCursor.moveToNext()) {

            final List<RecentRequest> recentRequestList = new ArrayList<>();

            do {
                final String id = recentCursor.getString(0);
                final String word = recentCursor.getString(1);
                final String type = recentCursor.getString(2);
                final long createdAt = recentCursor.getLong(3);
                final String timeSpan = DateUtils
                        .getRelativeTimeSpanString(
                                createdAt,
                                System.currentTimeMillis(),
                                DateUtils.MINUTE_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL
                        ).toString();

                recentRequestList.add(new RecentRequest(id, word, type, timeSpan));
            } while (recentCursor.moveToNext());

            recentCursor.close();

            return recentRequestList;
        }
        return null;
    }


    public void setRequestIsDeleted(String id, final boolean delete) {
        final String query = "UPDATE requests SET is_deleted = ? WHERE id = ?";
        final SQLiteDatabase db = super.getWritableDatabase();
        final String delStatus = delete ? "1" : "0";
        db.execSQL(query, new String[]{delStatus, id});
    }


    public void setAllRequestIsDelete(final boolean isDelete) {
        final String delFlag = isDelete ? "1" : "0";
        final String query = "UPDATE requests SET is_deleted = ?";
        final SQLiteDatabase db = super.getWritableDatabase();
        db.execSQL(query, new String[]{delFlag});
    }
}
