package com.shifz.wordbird.servlets;

import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.utils.JSONHelper;
import com.shifz.wordbird.utils.NetworkHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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

                    //TODO:Collecting data from url
                    String data = new NetworkHelper(url).getResponse();
                    System.out.println("Data length: " + data.length());
                    data = data.replaceAll("[^'a-zA-Z]", " ");

                    System.out.println("Sterilized data: " + data);

                    out.write(data);


                    //TODO:Remove html from data
                    final List<String> words = new ArrayList<>();

                    //TODO:Remove unnecessary symbols
                    //TODO:Loop through each word
                    //TODO:Loop through each option
                    //TODO:Check if exist in db
                    //TODO:Check if it's invalid
                    //TODO:Add request to db
                    //TODO:Add result to db
                } catch (IOException e) {
                    out.write(JSONHelper.getErrorJSON("Invalid URL : " + url + " : " + e.getMessage()));
                }


            } else {
                out.write(JSONHelper.getErrorJSON("Invalid URL : " + url));
            }


        } else {
            out.write(JSONHelper.getErrorJSON("Missing param: url"));
        }


    }
}
