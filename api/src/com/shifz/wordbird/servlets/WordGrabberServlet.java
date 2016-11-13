package com.shifz.wordbird.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by theapache64 on 13/11/16.
 */
@WebServlet(urlPatterns = {"/grab"})
public class WordGrabberServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //TODO:Getting url
        //TODO:Collecting data from url
        //TODO:Remove html from data
        //TODO:Remove unnecessary symbols
        //TODO:Loop through each word
        //TODO:Loop through each option
        //TODO:Check if exist in db
        //TODO:Check if it's invalid
        //TODO:Add request to db
        //TODO:Add result to db
    }
}
