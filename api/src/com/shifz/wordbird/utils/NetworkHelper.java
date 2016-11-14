package com.shifz.wordbird.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Shifar Shifz on 10/22/2015.
 */
public class NetworkHelper {

    private final String url;

    public NetworkHelper(final String url) {
        this.url = url;
    }

    public String getResponse() throws IOException {

        //System.out.println("->" + url);

        final URL urlOb = new URL(url);
        final InputStream is = urlOb.openStream();
        final InputStreamReader isr = new InputStreamReader(is);
        final BufferedReader br = new BufferedReader(isr);
        final StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line).append("\n");
        }
        is.close();
        isr.close();
        br.close();

        //System.out.println("Network response downloaded + ");

        return response.toString();

    }

}
