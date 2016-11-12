package com.shifz.wordbird.models;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class User {

    private final String name, imei, apiKey;
    private String id;

    //Used for adding new user
    public User(final String name, final String imei, final String apiKey) {
        this.name = name;
        this.imei = imei;
        this.apiKey = apiKey;
    }

    //Used while retrieving a user
    public User(final String name, final String imei, final String apiKey, final String id) {
        this(name, imei, apiKey);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImei() {
        return imei;
    }

    public String getApiKey() {
        return apiKey;
    }


}
