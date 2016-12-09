package com.shifz.wordbird;

import com.shifz.wordbird.utils.Extractor;
import com.shifz.wordbird.utils.FileUtils;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class Lab {

    public static void main(String[] args) throws IOException, JSONException {

        final String data = FileUtils.read(System.getProperty("user.dir") + "/web/test_data/t1.html");
        final Set<String> urls = Extractor.extractUrls("http://example.com", data);
        for (final String url : urls) {
            System.out.println("x " + url);
        }
        System.out.println(urls.size());
    }
}
