package com.shifz.wordbird.utils;

import com.shifz.wordbird.models.Request;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Shifar Shifz on 10/23/2015.s
 */
public class OkHttpHelper {

    public static final String BASE_URL = App.DEBUG ? "http://192.168.0.101:8080" : "http://theapache64.xyz:8080/wordbird";
    public static final String KEY_ERROR = "error";
    public static final String KEY_MESSAGE = "message";
    private static final String GET_RESULT_URL_FORMAT = BASE_URL + "/Get/%s/%s";
    private static final String KEY_AUTHORIZATION = "Authorization";
    private static final OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getClient() {
        return client;
    }

    public static void getResult(final Request request, final Callback callback) {

        final String url = String.format(GET_RESULT_URL_FORMAT, request.getType(), request.getWord());
        final com.squareup.okhttp.Request okRequest = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .tag(Request.KEY)
                .addHeader(KEY_AUTHORIZATION, App.apiKey)
                .build();

        client.newCall(okRequest).enqueue(callback);

    }
}
