package com.shifz.wordbird.database;

import com.shifz.wordbird.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class Users extends BaseTable<User> {

    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_API_KEY = "api_key";

    private static Users ourInstance = new Users();

    private Users() {
        super("users");
    }

    public static Users getInstance() {
        return ourInstance;
    }

    @Override
    public boolean add(User user) {

        final String query = "INSERT INTO users (name,imei,api_key) VALUES (?,?,?);";
        boolean isAdded = false;
        final java.sql.Connection con = Connection.getConnection();
        try {

            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user.getName());
            ps.setString(2, user.getImei());
            ps.setString(3, user.getApiKey());
            isAdded = ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isAdded;
    }

    @Override
    public User get(String columnName, String columnValue) {
        final String query = String.format("SELECT * FROM users WHERE %s = ?", columnName);
        User user = null;
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, columnValue);
            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String imei = rs.getString(COLUMN_IMEI);
                final String apiKey = rs.getString(COLUMN_API_KEY);

                user = new User(name, imei, apiKey, id);

                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return user;
    }
}
