package com.shifz.wordbird.core;

import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.models.Result;
import com.shifz.wordbird.utils.NetworkHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shifar Shifz on 10/23/2015.
 */
public class WordBirdGrabber {

    private static final String FIRST_DELIMITER = "<td width=\"340\" valign=\"top\">"; //Used to divide junk html from result - HEAD
    private static final String SECOND_DELIMITER = "<div id=\"mobilebottombannerad\">"; //Used to divide junk html from result -TAIL
    private static final String THIRD_DELIMITER = "<div class=\"wordtype\">"; //Used to divide word types
    private static final String FOURTH_DELIMITER = "</div>";
    private static final String KEY_HEAD = "head";
    private static final String KEY_BODY = "body";
    private static final String BASE_URL_FORMAT = "http://wordhippo.com/what-is/%s/%s.html";
    private static final String HTML_REMOVER_REGEX = "<.*?>";
    private static final String MEANING_GRABBER_REGEX = "<li>(.+)<\\/li>";
    private static final String DEFAULT_GRABBER_REGEX = "<.*>([\\w\\-]+(?:\\s\\w+)*)<\\/.*>";
    private static final Pattern noMatchFoundPattern = Pattern.compile("No (?:words|definitions|examples) found\\.");
    private static final String SENTENCE_GRABBER_REGEX = "<p>(.+).";
    private static final String SINGLE_LINE_GRABBER_REGEX = "(.+)\\.";
    private static final String SYMBOL_UK = "UK";
    private static final String SYMBOL_US = "US";
    private final Request request;
    private final Pattern grabPattern;

    public WordBirdGrabber(Request request) {
        this.request = request;
        this.grabPattern = Pattern.compile(
                getGrabberRegEx(
                        request.getType()
                )
        );
    }

    private static String getGrabberRegEx(String type) {

        switch (type) {

            case Request.TYPE_MEANING:
                return MEANING_GRABBER_REGEX;

            case Request.TYPE_SENTENCE:
                return SENTENCE_GRABBER_REGEX;

            case Request.TYPE_PAST:
            case Request.TYPE_PRESENT:
            case Request.TYPE_SINGULAR:
            case Request.TYPE_PLURAL:
                return SINGLE_LINE_GRABBER_REGEX;

            default:
                return DEFAULT_GRABBER_REGEX;
        }

    }

    private static String[] getWordTypeNodes(String networkResponse) {
        String resultNode = networkResponse.split(FIRST_DELIMITER)[1];
        resultNode = resultNode.split(SECOND_DELIMITER)[0];
        return resultNode.split(THIRD_DELIMITER);
    }

    private static String removeHtml(String group) {
        return group.replaceAll(HTML_REMOVER_REGEX, "");
    }

    private static boolean isNoMatchFound(final String node) {
        return noMatchFoundPattern.matcher(node).find();
    }

    public Result getResult() {

        //Not available in db, so the network
        final String url = String.format(BASE_URL_FORMAT, request.getCode(), request.getWord());

        final NetworkHelper networkHelper = new NetworkHelper(url);
        final String networkResponse = networkHelper.getResponse();

        //System.out.println("Network response : " + networkResponse);

        if (networkResponse != null) {

            try {

                final JSONArray jResponse = new JSONArray();

                for (final String wordTypeNode : getWordTypeNodes(networkResponse)) {

                    final String wordTypeName = wordTypeNode.split(FOURTH_DELIMITER)[0];

                    if (isNoMatchFound(wordTypeNode)) {
                        //No word found
                        //System.out.println("No match found :(");
                        return null;
                    }


                    if (!wordTypeName.trim().isEmpty()) {

                        final JSONObject jNode = new JSONObject();
                        jNode.put(KEY_HEAD, wordTypeName);

                        final JSONArray jBody = new JSONArray();

                        final Matcher synonymMatcher = getMatcher(wordTypeNode);

                        while (synonymMatcher.find()) {

                            String word = synonymMatcher.group(1);

                            //System.out.println("Result: " + word);

                            if (request.isClearHtml()) {
                                word = removeHtml(word);
                            }

                            if (!isInvalidWord(word)) {
                                jBody.put(word);
                            }

                        }

                        jNode.put(KEY_BODY, jBody);
                        jResponse.put(jNode);

                    }

                }

                return new Result(Result.SOURCE_NETWORK, jResponse.toString(), true);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

        }
        return null;
    }

    /**
     * To check if the passed word is valid.
     *
     * @param word
     * @return TRUE on invalid , FALSE on VALID
     */
    private boolean isInvalidWord(String word) {
        return word == null ||
                word.equals(SYMBOL_UK) ||
                word.equals(SYMBOL_US);
    }

    public Matcher getMatcher(String wordTypeNode) {
        return this.grabPattern.matcher(wordTypeNode);
    }
}
