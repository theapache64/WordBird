package com.shifz.wordbird.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class JSONHelper {


    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_RESULT = "result";

    //Simple helper method to send quick responses
    public static String getErrorJSON(final String error) {
        final JSONObject jOb = new JSONObject();
        try {
            jOb.put(KEY_ERROR, true);
            jOb.put(KEY_MESSAGE, error);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jOb.toString();
    }

    //Simple helper method to send quick responses
    public static String getSuccessJSON(final String message, final String successKey, final String successValue) {
        final JSONObject jOb = new JSONObject();
        try {
            jOb.put(KEY_ERROR, false);
            jOb.put(KEY_MESSAGE, message);
            jOb.put(successKey, successValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jOb.toString();
    }

    public static String getResultJSON(final String message, final JSONArray resultArr) {
        final JSONObject jOb = new JSONObject();
        try {
            jOb.put(KEY_ERROR, false);
            jOb.put(KEY_MESSAGE, message);
            jOb.put(KEY_RESULT, resultArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jOb.toString();
    }

}
