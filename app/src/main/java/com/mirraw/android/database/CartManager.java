package com.mirraw.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.mirraw.android.models.productDetail.ProductDetail;

import java.util.ArrayList;

/**
 * Created by vihaan on 10/7/15.
 */
public class CartManager {

    public String mTableName = Tables.Cart.TABLE_NAME;

    private final DatabaseHelper mDatabaseHelper;
    private final SQLiteDatabase mSqLiteDatabase;

    public CartManager()
    {
        mDatabaseHelper = DatabaseHelper.getInstance();
        mSqLiteDatabase = mDatabaseHelper.getSqliteDatabase();
    }


    public void insertRow(String product){
        ContentValues values = null;
        values = new ContentValues();

        values.put(Tables.Cart.PRODUCT, product);

        mSqLiteDatabase.insert(mTableName, null, values);
    }

    public void saveProducts(ArrayList<ProductDetail> products)
    {
        Gson gson = new Gson();
        String data;
        for(ProductDetail productDetail : products)
        {
            data = gson.toJson(productDetail);
            insertRow(data);
        }
    }

    public Cursor getCursor()
    {
        Cursor cursor = null;
        cursor = mSqLiteDatabase.rawQuery("Select rowid _id, * from " + mTableName, null);
        return cursor;
    }

    public void clearTable()
    {
        mSqLiteDatabase.delete(mTableName, null, null);
    }


    public int getCartCount() {
        return getCursor().getCount();
    }
}
