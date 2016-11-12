package com.shifz.wordbird.database;

/**
 * Created by Shifar Shifz on 10/18/2015.
 */
public abstract class Table<T> {
    public abstract boolean add(T t);
    public abstract T get(final String columnName, final String columnValue);

}
