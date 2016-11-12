package com.shifz.wordbird.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Used to validate params in the request
 * Created by Shifar Shifz on 10/18/2015.
 */
public class Validator {

    private final String[] reqParams;
    private HashSet<String> request;
    private HashSet<String> missingFields;

    public Validator(String... reqParams) {
        this.reqParams = reqParams;
    }

    public void setRequest(HttpServletRequest req) {
        this.request = new HashSet<>();
        final Enumeration<String> reqNames = req.getParameterNames();
        while (reqNames.hasMoreElements()) {
            this.request.add(reqNames.nextElement());
        }
    }

    public boolean hasRequiredParams() {

        if (request == null) {
            throw new IllegalArgumentException("You must call setRequest() before calling hasRequiredParams()");
        }

        missingFields = new HashSet<>();
        for (final String reqParam : reqParams) {
            if (!this.request.contains(reqParam)) {
                missingFields.add(reqParam);
            }
        }
        return missingFields.size() == 0;
    }

    public Set<String> getMissingFields() {
        return this.missingFields;
    }

    public String getErrorReport() {
        if (this.missingFields.size() > 0) {
            final StringBuilder errorReport = new StringBuilder("Missing required fields ");
            for (final String errorField : this.missingFields) {
                errorReport.append(errorField).append(",");
            }
            return errorReport.substring(0, errorReport.length() - 1);
        } else {
            return null;
        }
    }

}
