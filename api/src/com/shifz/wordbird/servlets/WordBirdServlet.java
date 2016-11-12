package com.shifz.wordbird.servlets;

import com.shifz.wordbird.core.WordBirdGrabber;
import com.shifz.wordbird.database.Requests;
import com.shifz.wordbird.database.Users;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.models.Result;
import com.shifz.wordbird.models.User;
import com.shifz.wordbird.utils.JSONHelper;
import com.shifz.wordbird.utils.Security;
import org.json.JSONArray;
import org.json.JSONException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
@WebServlet(urlPatterns = {"/Get/*"})
public class WordBirdServlet extends HttpServlet {

    private static final int VALID_URL_PART_COUNT = 3;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        out.write(JSONHelper.getErrorJSON("POST method not supported"));
        out.flush();
        out.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Response type
        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();

        //System.out.println("Processing request...");

        //Checking the request validity
        final String pathInfo = req.getPathInfo();

        //System.out.println("PathInfo : "+pathInfo);

        if (pathInfo != null) {

            final String[] urlParts = pathInfo.split("/");
            if (urlParts.length == VALID_URL_PART_COUNT) {

               //System.out.println("Valid Request :)");

                final Security security = new Security(req.getHeader(Security.KEY_AUTHORIZATION));
                final User user = security.getUser();

                if (user != null) {

                   //System.out.println("Valid user :)");

                    final String operationType = urlParts[1];

                    final Request newRequest = new Request(operationType);

                   //System.out.println("Request created :)");

                    if (newRequest.getCode() != null) {

                       //System.out.println("Valid request :)");

                        //Valid request, now set the word and the userId
                        final String word = urlParts[2];
                        newRequest.setWord(word);

                        //Checking if the request already in db
                        final Requests requestsTable = Requests.getInstance();
                        Result result = requestsTable.getResult(newRequest);


                        if (result == null) {

                           //System.out.println("Not found  in db :| , Checking in network :)");

                            //Fresh word, not exist in db
                            final WordBirdGrabber wordGrabber = new WordBirdGrabber(newRequest);
                            result = wordGrabber.getResult();

                           //System.out.println("Network check completed");

                        }

                        if (result != null && result.isSuccess()) {


                           //System.out.println("Positive result");

                            //Awesome, we got valid result
                            try {
                                out.write(
                                        JSONHelper.getResultJSON(
                                                "Result found", //message
                                                new JSONArray(result.toString()) //Success value

                                        ));

                            } catch (JSONException e) {

                                e.printStackTrace();
                                out.write(
                                        JSONHelper.getErrorJSON(
                                                "Server error occured"
                                        ));
                            }

                        } else {

                           //System.out.println("Negative result :(");

                            //Failed to find the result
                            out.write(
                                    JSONHelper.getErrorJSON(
                                            String.format("No %s found for %s", newRequest.getType(), newRequest.getUnformattedWord()))
                            );


                        }

                        if (result == null) {
                            //not exist in db, tryed in network but negative
                            result = new Result(Result.SOURCE_NETWORK, null, false);
                        }


                        //Finally recording the request
                        newRequest.setUserId(user.getId());
                        newRequest.setResult(result);
                        requestsTable.add(newRequest);


                    } else {
                        //The request type is not supported
                        resp.setStatus(HttpURLConnection.HTTP_BAD_METHOD);
                        out.write(JSONHelper.getErrorJSON("Unsupported method " + operationType));
                    }

                } else {
                    //Invalid user
                    resp.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
                    out.write(JSONHelper.getErrorJSON("Unauthorized access"));
                }
            } else {
                //Damaged GET request
                resp.setStatus(HttpURLConnection.HTTP_BAD_REQUEST);
                out.write(JSONHelper.getErrorJSON("Request damaged"));
            }


        } else {
            //Invalid request
            resp.setStatus(HttpURLConnection.HTTP_NOT_FOUND);
            out.write(JSONHelper.getErrorJSON("Page not found"));
        }

        out.flush();
        out.close();

    }

}
