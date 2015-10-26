package com.mirraw.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mirraw.android.Mirraw;

/**
 * Created by vihaan on 10/7/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mirraw.db";
    private static final int DATABASE_VERSION = 2;
    private SQLiteDatabase mSqliteDatabase;


    private static DatabaseHelper mDatabaseHelper = new DatabaseHelper(
            Mirraw.getAppContext());

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public static DatabaseHelper getInstance() {
        return mDatabaseHelper;
    }

    public SQLiteDatabase getSqliteDatabase() {
        if (mSqliteDatabase == null) {
            mSqliteDatabase = getWritableDatabase();
        }

        return mSqliteDatabase;
    }


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Tables.Cart.SCHEMA_CART);
        db.execSQL(Tables.SyncRequests.SCHEMA_SYNC_REQUESTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL(Tables.SyncRequests.SCHEMA_SYNC_REQUESTS);
                break;
        }
    }
}
