package com.shifz.wordbird.utils;


import com.shifz.wordbird.database.BaseTable;

/**
 * Created by theapache64 on 26/11/16.
 */
public class CommonUtils {
    public static boolean parseBoolean(String s) {
        return s != null && s.equals(BaseTable.TRUE);
    }
}
