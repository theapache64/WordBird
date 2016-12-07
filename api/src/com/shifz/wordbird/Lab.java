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

        final String data = FileUtils.read(System.getProperty("user.dir") + "/web/words.txt");
        final Set<String> words = Extractor.extractWords(data, null);
        for (final String word : words) {
            System.out.println("x " + word);
        }
        System.out.println(words.size());
    }
}
