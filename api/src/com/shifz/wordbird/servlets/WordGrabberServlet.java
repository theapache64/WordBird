package com.shifz.wordbird.servlets;

import com.shifz.wordbird.core.WordBirdGrabber;
import com.shifz.wordbird.database.Preference;
import com.shifz.wordbird.database.Requests;
import com.shifz.wordbird.database.UrlIndex;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.models.Result;
import com.shifz.wordbird.models.Url;
import com.shifz.wordbird.utils.Extractor;
import com.shifz.wordbird.utils.JSONHelper;
import com.shifz.wordbird.utils.NetworkHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by theapache64 on 13/11/16.
 */
@WebServlet(urlPatterns = {"/grab"})
public class WordGrabberServlet extends HttpServlet {

    private static final String URL_REGEX = "^(http:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";

    private static final String[] TYPES = {
            Request.TYPE_SYNONYM,
            Request.TYPE_OPPOSITE,
            Request.TYPE_MEANING,
            Request.TYPE_RHYME,
            Request.TYPE_SENTENCE,
            Request.TYPE_PLURAL,
            Request.TYPE_SINGULAR,
            Request.TYPE_PAST,
            Request.TYPE_PRESENT,
            Request.TYPE_START,
            Request.TYPE_END,
            Request.TYPE_CONTAIN
    };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final PrintWriter out = resp.getWriter();

        //Getting url
        final String url = req.getParameter("url");

        if (url != null && !url.trim().isEmpty()) {

            //url param is not empty!
            if (url.matches(URL_REGEX)) {

                try {

                    final Url theUrl = UrlIndex.getInstance().get(UrlIndex.COLUMN_URL, url);

                    if (theUrl.shouldIndex()) {

                        final String data = new NetworkHelper(url).getResponse();

                        //Extracting missing words only
                        final List<String> words = Extractor.extractWords(data, theUrl.isIndexedAlready() ? theUrl.getWords() : null);

                        if (words != null) {

                            final Requests requests = Requests.getInstance();

                            final String grabberUserId = Preference.getInstance().getString(Preference.KEY_GRABBER_USER_ID);

                            //looping through each word
                            for (final String word : words) {

                                //looping through each mode
                                for (final String type : TYPES) {

                                    if (!requests.isExist(Requests.COLUMN_WORD, word, Requests.COLUMN_TYPE, type)) {

                                        final Request request = new Request(word, type);
                                        request.setUserId(grabberUserId);

                                        final WordBirdGrabber grabber = new WordBirdGrabber(request);
                                        Result result = grabber.getResult();

                                        if (result == null) {
                                            //not exist in db, tryed in network but negative
                                            result = new Result(Result.SOURCE_NETWORK, null, false);
                                        }


                                        request.setResult(result);
                                        requests.add(request);

                                    } else {
                                        System.out.println(String.format("word: %s - type: %s - exists", word, type));
                                    }

                                }

                            }

                        } else {
                            System.out.println(theUrl.isIndexedAlready() ? "No new words found" : "No words found");
                        }


                    } else {
                        System.out.println("No need to index");
                    }


                } catch (IOException e) {
                    out.write(JSONHelper.getErrorJSON(url + " : " + e.getMessage()));
                }


            } else {
                out.write(JSONHelper.getErrorJSON("Invalid URL : " + url));
            }


        } else {
            out.write(JSONHelper.getErrorJSON("Missing param: url"));
        }


    }
}
