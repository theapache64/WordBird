package com.shifz.wordbird.database;

import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.models.Result;
import com.shifz.wordbird.utils.CommonHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public class Requests extends BaseTable<Request> {

    private static final String COLUMN_RESULT = "result";
    private static final String COLUMN_IS_SUCCESS = "is_success";
    public static final String COLUMN_WORD = "word";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_TYPE = "type";

    private static Requests ourInstance = new Requests();


    public static Requests getInstance() {
        return ourInstance;
    }

    private Requests() {
        super("requests");
    }

    @Override
    public boolean add(Request request) {

        final java.sql.Connection con = Connection.getConnection();

        //Adding the result
        if (request.getResult().getId() == null) {

            //New request
            final String query = "INSERT INTO results (result) VALUES (?)";
            try {
                final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, request.getResult().toString());
                ps.execute();
                final ResultSet resultSet = ps.getGeneratedKeys();
                if (resultSet.next()) {
                    final String resultId = String.valueOf(resultSet.getInt(1));
                    request.getResult().setId(resultId);
                }
                resultSet.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        final String query = "INSERT INTO requests (user_id,word,type,is_success,result_id) VALUES (?,?,?,?,?);";
        boolean isAdded = false;
        try {

            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, request.getUserId());
            ps.setString(2, request.getWord());
            ps.setString(3, request.getType());

            final Result result = request.getResult();
            ps.setInt(4, result.isSuccessInt()); //is_success = true
            ps.setString(5, result.getId()); //result = null

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
    public Request get(String columnName, String columnValue) {
        throw new IllegalArgumentException("Method not supported yet");
    }

    //Return the result JSON if the result exists in the db
    public Result getResult(final Request request) {

        //TODO: Remove concatenation
        final String query =
                "SELECT\n" +
                        "rs.id,\n" +
                        "rs.result,\n" +
                        "r.is_success\n" +
                        "FROM requests r\n" +
                        "LEFT JOIN results rs ON rs.id = r.result_id\n" +
                        "WHERE type = ? AND word= ?  LIMIT 1";

        System.out.println("Query:" + query);


        final java.sql.Connection con = Connection.getConnection();
        Result dbResult = null;

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, request.getType());
            ps.setString(2, request.getWord());
            final ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                final String id = rs.getString(COLUMN_ID);
                final String result = rs.getString(COLUMN_RESULT);
                final boolean isSuccess = CommonHelper.parseBoolean(rs.getInt(COLUMN_IS_SUCCESS));
                dbResult = new Result(Result.SOURCE_DATABASE, id, result, isSuccess);
            }

            rs.close();
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

        return dbResult;
    }

}
