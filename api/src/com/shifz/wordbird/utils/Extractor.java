package com.shifz.wordbird.utils;


import com.sun.istack.internal.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by theapache64 on 3/12/16.
 */
public class Extractor {

    private static final String WORD_REGEX = "[A-Za-z\\-]+";


    public static Set<String> extractWords(String data, @Nullable Set<String> wordsToExclude) {
        //Removing html tags
        data = data.replaceAll("<[^>]*>", "");
        //multiple spaces to single space
        data = data.replaceAll("(\\s{2,}|\\n+)", " ");
        //Converting to list
        final List<String> words = Arrays.asList(data.split(" "));
        //Removing duplicates
        final Set<String> wordSet = new HashSet<>();
        for (String word : words) {

            //trimming and removing comma
            word = word.trim().replaceAll("(,|'s|\\.s)", "").toLowerCase();

            if (!word.isEmpty() && word.matches(WORD_REGEX) && !wordSet.contains(word)) {

                if (wordsToExclude == null || !wordsToExclude.contains(word)) {
                    wordSet.add(word);
                }

            }

        }
        return wordSet;
    }

    public static List<String> extractUrls(String data) {
        return null;
    }
}
