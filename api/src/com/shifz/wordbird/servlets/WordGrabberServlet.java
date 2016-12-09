package com.shifz.wordbird.servlets;

import com.shifz.wordbird.core.WordBirdGrabber;
import com.shifz.wordbird.database.BaseTable;
import com.shifz.wordbird.database.Preference;
import com.shifz.wordbird.database.Requests;
import com.shifz.wordbird.database.UrlIndex;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.models.Result;
import com.shifz.wordbird.models.Url;
import com.shifz.wordbird.utils.*;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by theapache64 on 13/11/16.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/grab"})
public class WordGrabberServlet extends AdvancedBaseServlet {


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

    private static final int DEFAULT_DEEP_LINK_LEVEL = 0;
    private static final String KEY_DEEP_LINK_LEVEL = "deep_link_level";
    private int maxDeepLinkLevel;
    private int currentDeepLinkLevel = 0;
    private static final int DEEP_LINK_LEVEL_INFINITE = -1;
    private StringBuilder logBuilder;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    private void logIt(final String log) {
        System.out.println(log);
        logBuilder.append(log).append("\n");
    }

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{UrlIndex.COLUMN_URL};
    }

    @Override
    protected void doAdvancedPost() throws BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException, com.shifz.wordbird.utils.Request.RequestException, IOException {

        logBuilder = new StringBuilder();


        new Thread(new Runnable() {
            @Override
            public void run() {


                final String url = getStringParameter(UrlIndex.COLUMN_URL);

                try {

                    logIt("--------------------------");
                    logIt("Started");

                    maxDeepLinkLevel = getIntParameter(KEY_DEEP_LINK_LEVEL, DEFAULT_DEEP_LINK_LEVEL);
                    currentDeepLinkLevel = 0;
                    logIt("MAX DEEP LINK LEVEL = " + maxDeepLinkLevel);


                    grab(url);

                    logIt("Finished");
                    logIt("--------------------------");

                    //Mailing the log to admin email
                    MailHelper.sendMail(Preference.getInstance().getString(Preference.KEY_ADMIN_EMAIL), "WordBird- GRAB REPORT - " + url, logBuilder.toString());


                } catch (com.shifz.wordbird.utils.Request.RequestException | BaseTable.InsertFailedException | IOException | JSONException e) {
                    e.printStackTrace();
                    MailHelper.sendMail(Preference.getInstance().getString(Preference.KEY_ADMIN_EMAIL), "ERROR: WordBird- GRAB REPORT - " + url, e.getMessage());
                }

            }
        }).start();

        getWriter().write(new APIResponse("Grabber engine intialized. Response will be mailed to admin email", null).getResponse());

    }


    private void grab(String url) throws com.shifz.wordbird.utils.Request.RequestException, BaseTable.InsertFailedException, IOException, JSONException {

        logIt("Current deep link level: " + currentDeepLinkLevel);

        if (maxDeepLinkLevel != DEFAULT_DEEP_LINK_LEVEL && maxDeepLinkLevel != DEEP_LINK_LEVEL_INFINITE && currentDeepLinkLevel >= maxDeepLinkLevel) {
            logIt("Deep link level reached: MAX: " + maxDeepLinkLevel + " CURRENT: " + currentDeepLinkLevel);
            return;
        }


        if (url != null && !url.trim().isEmpty()) {

            logIt("Url: " + url);

            final long indexingStartedAt = System.currentTimeMillis();

            //Valid URL
            final UrlIndex urlIndexTable = UrlIndex.getInstance();

            //Checking if the url is indexed.
            Url theUrl = UrlIndex.getInstance().get(UrlIndex.COLUMN_URL, url);

            if (theUrl == null) {

                logIt("Adding url to index");

                //New url, does not exist in the index.
                theUrl = new Url(null, url, null, false, true);
                theUrl.setTotalTimeElapsedToFirstIndex(-1);
                final String urlId = urlIndexTable.addv3(theUrl);
                theUrl.setId(urlId);
            }

            logIt("Url: " + theUrl);


            logIt("Downloading network data...");
            final String data = new NetworkHelper(url).getResponse();

            if (data == null) {

                theUrl.setIsIndexedAlready(true);
                theUrl.setWords(null);
                theUrl.setTotalTimeElapsedToFirstIndex(0);
                theUrl.setLastIndexedAt(System.currentTimeMillis());

                logIt("Updating INVALID URL: " + theUrl);
                urlIndexTable.update(theUrl);

                getWriter().write(new APIResponse("Invalid URL: " + url).getResponse());
                return;
            }

            if (theUrl.shouldReIndex()) {

                logIt("Starting indexing...");

                logIt("Network data downloaded");

                //Extracting missing words only
                final Set<String> words = Extractor.extractWords(data, theUrl.isIndexedAlready() ? theUrl.getWords() : null);

                if (words != null && !words.isEmpty()) {

                    logIt("Analyzing words... " + words + " word(s) found");

                    final Requests requests = Requests.getInstance();

                    final String grabberUserId = Preference.getInstance().getString(Preference.KEY_GRABBER_USER_ID);

                    //looping through each word
                    for (final String word : words) {

                        //looping through each mode
                        for (final String type : TYPES) {

                            logIt(String.format("word: %s - type: %s", word, type));

                            if (!requests.isExist(Requests.COLUMN_WORD, word, Requests.COLUMN_TYPE, type)) {

                                final Request request = new Request(word, type);
                                request.setUserId(grabberUserId);

                                final WordBirdGrabber grabber = new WordBirdGrabber(request);
                                Result result = grabber.getResult();
                                request.setResult(result);
                                request.setUrlId(theUrl.getId());
                                requests.add(request);

                                logIt("Request added to database");

                            } else {

                                logIt("Data exists");
                            }
                        }
                    }

                    //Url indexing finished. So update the database table.
                    theUrl.setIsIndexedAlready(true);
                    theUrl.setWords(words);
                    final long elapsedTimeToFirstIndex = theUrl.getTotalTimeElapsedToFirstIndex() == -1 ? TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - indexingStartedAt) : theUrl.getTotalTimeElapsedToFirstIndex();
                    theUrl.setTotalTimeElapsedToFirstIndex(elapsedTimeToFirstIndex);
                    theUrl.setLastIndexedAt(System.currentTimeMillis());

                    logIt("Updating url : " + theUrl);
                    urlIndexTable.update(theUrl);
                    currentDeepLinkLevel++;

                } else {
                    logIt(theUrl.isIndexedAlready() ? "No new words found" : "No words found");
                }
            } else {
                logIt("No need to index. :) Already indexed and it's fresh.");
            }


            //Sending an email to the admin about the url that recently indexed.
            MailHelper.sendMail(Preference.getInstance().getString(Preference.KEY_ADMIN_EMAIL), "WordBird - URL Indexing finished", "Url indexed: " + theUrl.toString());

            if (maxDeepLinkLevel > 0) {

                logIt("Downloading deep link links...");

                //Doing deep index
                final Set<String> urls = Extractor.extractUrls(url, data);

                logIt(urls.size() + " link(s) found");

                for (final String deepLinkUrl : urls) {
                    logIt("Deep linking to " + deepLinkUrl);
                    grab(deepLinkUrl);
                }

            }

            getWriter().write(new APIResponse("Indexing finished", "url", theUrl.toString()).getResponse());

        } else {
            throw new com.shifz.wordbird.utils.Request.RequestException("Missing param: url");
        }
    }
}
