package com.shifz.wordbird.servlets;


import com.shifz.wordbird.database.Users;
import com.shifz.wordbird.models.User;
import com.shifz.wordbird.utils.JSONHelper;
import com.shifz.wordbird.utils.Validator;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
@WebServlet(urlPatterns = {"/IN"})
public class INServlet extends HttpServlet {

    private static final Validator validator = new Validator(Users.COLUMN_NAME, Users.COLUMN_IMEI);
    //Used to generate a new user
    private static final int API_KEY_LENGTH = 10;
    private static final String apiEngine = "0123456789AaBbCcDdEeFfGgHhIiJjKkLkMmNnOoPpQqRrSsTtUuVvWwXxYyZ";
    private static Random random;

    public static String getNewApiKey() {
        if (random == null) {
            random = new Random();
        }
        final StringBuilder apiKeyBuilder = new StringBuilder();
        for (int i = 0; i < API_KEY_LENGTH; i++) {
            apiKeyBuilder.append(apiEngine.charAt(random.nextInt(apiEngine.length())));
        }
        return apiKeyBuilder.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        final PrintWriter out = resp.getWriter();
        resp.setContentType("application/json");
        out.write(JSONHelper.getErrorJSON("GET method not supported"));
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("application/json");
        final PrintWriter out = resp.getWriter();

        validator.setRequest(req);

        //Checking if the request contains required fields
        if (validator.hasRequiredParams()) {

            final String imei = req.getParameter(Users.COLUMN_IMEI);

            //Checking if any account exist with the requested imei
            final Users users = Users.getInstance();
            User user = users.get(Users.COLUMN_IMEI, imei);
            final boolean isAlreadyExist = user != null;
            if (!isAlreadyExist) {
                //Account not exists, so creating new one
                final String name = req.getParameter(Users.COLUMN_NAME);
                user = new User(name, imei, getNewApiKey());
                users.add(user);
            }

            final String message = isAlreadyExist ? "Welcome back!" : "Welcome to WordBird!";

            //At this point, we've a user - no matter new or old.
            final String successJson = JSONHelper.getSuccessJSON(message, Users.COLUMN_API_KEY, user.getApiKey());
            out.write(successJson);

        } else {
            out.write(JSONHelper.getErrorJSON(validator.getErrorReport()));
        }

        out.flush();
        out.close();
    }
}
