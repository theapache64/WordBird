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
@WebServlet(urlPatterns = {"/grab"})
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

    private static final int DEFAULT_DEEP_LINK_LEVEL = 1;
    private static final String KEY_DEEP_LINK_LEVEL = "deep_link_level";
    private int maxDeepLinkLevel;
    private int currentDeepLinkLevel = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
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


        System.out.println("--------------------------");
        System.out.println("Started");

        maxDeepLinkLevel = super.getIntParameter(KEY_DEEP_LINK_LEVEL, DEFAULT_DEEP_LINK_LEVEL);

        final String url = getStringParameter(UrlIndex.COLUMN_URL);
        grab(url);
    }

    private void grab(String url) throws com.shifz.wordbird.utils.Request.RequestException, BaseTable.InsertFailedException, IOException, JSONException {

        if (currentDeepLinkLevel >= maxDeepLinkLevel) {
            System.out.println("Deeplink level reached: MAX: " + maxDeepLinkLevel + " CURRENT: " + currentDeepLinkLevel);
            return;
        }

        if (url != null && !url.trim().isEmpty()) {

            System.out.println("Url: " + url);

            final long indexingStartedAt = System.currentTimeMillis();

            //Valid URL


            final UrlIndex urlIndexTable = UrlIndex.getInstance();

            //Checking if the url is indexed.
            Url theUrl = UrlIndex.getInstance().get(UrlIndex.COLUMN_URL, url);

            if (theUrl == null) {

                System.out.println("Adding url to index");

                //New url, does not exist in the index.
                theUrl = new Url(null, url, null, false, true);
                theUrl.setTotalTimeElapsedToFirstIndex(-1);
                final String urlId = urlIndexTable.addv3(theUrl);
                theUrl.setId(urlId);
            }

            System.out.println("Url: " + theUrl);

            if (theUrl.shouldReIndex()) {

                System.out.println("Starting indexing...");

                System.out.println("Downloading network data...");
                final String data = new NetworkHelper(url).getResponse();

                System.out.println("Network data downloaded");

                //Extracting missing words only
                final Set<String> words = Extractor.extractWords(data, theUrl.isIndexedAlready() ? theUrl.getWords() : null);

                if (words != null && !words.isEmpty()) {

                    System.out.println("Analyzing words... " + words + " word(s) found");

                    final Requests requests = Requests.getInstance();

                    final String grabberUserId = Preference.getInstance().getString(Preference.KEY_GRABBER_USER_ID);

                    //looping through each word
                    for (final String word : words) {

                        //looping through each mode
                        for (final String type : TYPES) {

                            System.out.println(String.format("word: %s - type: %s", word, type));

                            if (!requests.isExist(Requests.COLUMN_WORD, word, Requests.COLUMN_TYPE, type)) {

                                final Request request = new Request(word, type);
                                request.setUserId(grabberUserId);

                                final WordBirdGrabber grabber = new WordBirdGrabber(request);
                                Result result = grabber.getResult();
                                request.setResult(result);
                                request.setUrlId(theUrl.getId());
                                requests.add(request);

                                System.out.println("Request added to database");

                            } else {

                                System.out.println("Data exists");
                            }
                        }
                    }

                    //Url indexing finished. So update the database table.
                    theUrl.setIsIndexedAlready(true);
                    theUrl.setWords(words);
                    final long elapsedTimeToFirstIndex = theUrl.getTotalTimeElapsedToFirstIndex() == -1 ? TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - indexingStartedAt) : theUrl.getTotalTimeElapsedToFirstIndex();
                    theUrl.setTotalTimeElapsedToFirstIndex(elapsedTimeToFirstIndex);
                    theUrl.setLastIndexedAt(System.currentTimeMillis());

                    System.out.println("Updating url : " + theUrl);
                    urlIndexTable.update(theUrl);

                    //Sending an email to the admin about the url that recently indexed.
                    MailHelper.sendMail(Preference.getInstance().getString(Preference.KEY_ADMIN_EMAIL), "WordBird - URL Indexing finished", "Url indexed: " + theUrl.toString());
                    currentDeepLinkLevel++;

                    //Doing deep index
                    final Set<String> urls = Extractor.extractUrls(url, data);
                    if (urls != null) {
                        for (final String deepLinkUrl : urls) {
                            System.out.println("Deeplinking to " + deepLinkUrl);
                            grab(deepLinkUrl);
                        }
                    }

                    getWriter().write(new APIResponse("Indexing finished", "url", theUrl.toString()).getResponse());

                } else {
                    getWriter().write(new APIResponse(theUrl.isIndexedAlready() ? "No new words found" : "No words found").getResponse());
                }

            } else {
                System.out.println("No need to index. :) Already indexed and it's fresh.");
                getWriter().write(new APIResponse("Indexed url", "url", url).getResponse());
            }

        } else {
            throw new com.shifz.wordbird.utils.Request.RequestException("Missing param: url");
        }

    }
}
