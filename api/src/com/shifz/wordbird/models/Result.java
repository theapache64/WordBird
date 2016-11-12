package com.shifz.wordbird.models;

/**
 * Created by Shifar Shifz on 10/22/2015.
 */
public class Result {

    public static final int SOURCE_DATABASE = 0;
    public static final int SOURCE_NETWORK = 1;
    private String id,result;


    private final int source;
    private boolean isSuccess;

    public Result(int source, String id, String result, final boolean isSuccess) {
        this.source = source;
        this.id = id;
        this.result = result;
        this.isSuccess = isSuccess;
    }

    public Result(int source, String result, final boolean isSuccess) {
        this(source,null,result,isSuccess);
    }

    public boolean isFromNetwork() {
        return this.source == SOURCE_NETWORK;
    }

    @Override
    public String toString() {
        return this.result;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public int isSuccessInt() {
        return isSuccess ? 1 : 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
