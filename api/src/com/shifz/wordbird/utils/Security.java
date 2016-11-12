package com.shifz.wordbird.utils;

import com.shifz.wordbird.database.Users;
import com.shifz.wordbird.models.User;

/**
 * Created by Shifar Shifz on 10/22/2015.
 */
public class Security {

    public static final String KEY_AUTHORIZATION = "Authorization";
    private final String authorization;

    public Security(String authorization) {
        this.authorization = authorization;
    }

    public User getUser() {

        if (this.authorization != null) {
            final Users usersTable = Users.getInstance();
            return usersTable.get(Users.COLUMN_API_KEY, this.authorization);
        }

        return null;
    }

}
