package com.shifz.wordbird.models;

import com.shifz.wordbird.utils.Code;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class Request {

    public static final String TYPE_SYNONYM = "Synonym";
    public static final String TYPE_OPPOSITE = "Opposite";
    public static final String TYPE_MEANING = "Meaning";
    public static final String TYPE_RHYME = "Rhyme";
    public static final String TYPE_SENTENCE = "Sentence";
    public static final String TYPE_PLURAL = "Plural";
    public static final String TYPE_SINGULAR = "Singular";
    public static final String TYPE_PAST = "Past";
    public static final String TYPE_PRESENT = "Present";
    public static final String TYPE_START = "Start";
    public static final String TYPE_END = "End";
    public static final String TYPE_CONTAIN = "Contain";


    private final String type;
    private final boolean isClearHtml;
    private String userId;
    private String word;
    private String realWord;
    private Result result;
    private String code;
    private String urlId;


    public Request(String userId, String word, String type, Result result) {

        this.userId = userId;
        setWord(word);
        this.type = type;
        this.result = result;
        this.code = getCode(type);
        this.isClearHtml = shouldClearHtml(type);

    }

    public Request(String word, String type) {
        this(null, word, type, null);
    }

    public Request(String type) {
        this(null, null, type, null);
    }

    private static String getCode(String type) {

        switch (type) {

            case TYPE_SYNONYM:
                return Code.ANOTHER_WORD_FOR;
            case TYPE_OPPOSITE:
                return Code.THE_OPPOSITE_OF;
            case TYPE_MEANING:
                return Code.THE_MEANING_OF_THE_WORD;
            case TYPE_RHYME:
                return Code.WORDS_THAT_RHYME_WITH;
            case TYPE_SENTENCE:
                return Code.SENTENCES_WITH_THE_WORD;
            case TYPE_PLURAL:
                return Code.THE_PLURAL_OF;
            case TYPE_SINGULAR:
                return Code.THE_SINGULAR_OF;
            case TYPE_PAST:
                return Code.THE_PAST_TENSE_OF;
            case TYPE_PRESENT:
                return Code.THE_PRESENT_TENSE_OF;
            case TYPE_START:
                return Code.STARTING_WITH;
            case TYPE_END:
                return Code.ENDING_WITH;
            case TYPE_CONTAIN:
                return Code.CONTAINING;

            default:
                return null;
        }
    }

    private boolean shouldClearHtml(final String type) {
        switch (type) {
            case TYPE_MEANING:
                return true;
            default:
                return false;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        if (word != null) {
            this.realWord = word;
            try {
            /*Deleting two or more leading white spaces,
            * Converting to lowercase,
            * Replacing single white space with underscore,
            * Converting to utf-8 to prevent malformed url exception*/
                this.word = URLEncoder.encode(word.toLowerCase().trim().replaceAll("[\\s]{2,}", " ").replaceAll(" ", "_"), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public String getType() {
        return type;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getUnformattedWord() {
        return realWord;
    }

    public String getCode() {
        return code;
    }


    public boolean isClearHtml() {
        return this.isClearHtml;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public String getUrlId() {
        return urlId;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", isClearHtml=" + isClearHtml +
                ", userId='" + userId + '\'' +
                ", word='" + word + '\'' +
                ", realWord='" + realWord + '\'' +
                ", result=" + result +
                ", code='" + code + '\'' +
                ", urlId='" + urlId + '\'' +
                '}';
    }
}
