package com.shifz.wordbird.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by theapache64 on 7/12/16.
 */
public class FileUtils {
    public static String read(String filePath) throws IOException {
        final BufferedReader br = new BufferedReader(new FileReader(filePath));
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }
}
