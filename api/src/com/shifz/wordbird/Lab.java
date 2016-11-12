package com.shifz.wordbird;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class Lab {


    public static void main(String[] args) throws IOException, JSONException {

        final BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://wordhippo.com/what-is/sentences-with-the-word/love.html").openStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }

        /*final BufferedReader br = new BufferedReader(new FileReader("index.htm"));
        String line = null;
        final StringBuilder response = new StringBuilder();
        while ((line = br.readLine()) != null) {
            response.append(line).append("\n");
        }


        final String resp = response.toString();*/


    }
}
