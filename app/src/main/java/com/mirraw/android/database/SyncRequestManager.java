package com.mirraw.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by pavitra on 3/9/15.
 */
public class SyncRequestManager {
    public String mTableName = Tables.SyncRequests.TABLE_NAME;

    private final DatabaseHelper mDatabaseHelper;
    private final SQLiteDatabase mSqLiteDatabase;

    public SyncRequestManager() {
        mDatabaseHelper = DatabaseHelper.getInstance();
        mSqLiteDatabase = mDatabaseHelper.getSqliteDatabase();
    }


    public void insertRow(String request) {
        ContentValues values = null;
        values = new ContentValues();

        values.put(Tables.SyncRequests.REQUESTS, request);

        mSqLiteDatabase.insert(mTableName, null, values);
    }


    public Cursor getCursor() {
        Cursor cursor = null;
        cursor = mSqLiteDatabase.rawQuery("Select rowid _id, * from " + mTableName, null);
        return cursor;
    }

    public void deleteRow(long rowId) {
        mSqLiteDatabase.execSQL("Delete from " + mTableName + " where rowid =" + rowId);
    }

    public void clearTable() {
        mSqLiteDatabase.delete(mTableName, null, null);
    }


    public int getRequestCount() {
        return getCursor().getCount();
    }
}


