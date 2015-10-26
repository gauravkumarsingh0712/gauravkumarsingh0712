package com.mirraw.android.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.database.SyncRequestManager;
import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 2/9/15.
 */
public class SyncService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
//        Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_LONG).show();
        SyncRequestManager manager = new SyncRequestManager();
        //manager.insertRow("Hello Pavi");

        Cursor cursor = manager.getCursor();

        /*String columnNames[] = new String[cursor.getColumnCount()];
        columnNames = cursor.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            Logger.v("Column Name", "Column Name: " + columnNames[i]);
        }*/

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            try {
                Logger.v("", "Column Value: " + cursor.getString(0));
                Logger.v("", "Column Value1: " + cursor.getString(1));

                final Request request = new Gson().fromJson(cursor.getString(1), Request.class);
                final long rowId = cursor.getLong(0);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Response response = new ApiClient().execute(request);
                            if (response.getResponseCode() == 200 || response.getResponseCode() == 422) {
                                new SyncRequestManager().deleteRow(rowId);
                            }
                        } catch (IOException e) {

                        }
                    }
                }).start();
            } catch (Exception e) {

            }
            cursor.moveToNext();
        }

        /*int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            i++;
            //if (i == 4)
            Logger.v("Row", "Row: " + cursor.getLong(0));
            manager.deleteRow(cursor.getLong(0));
            cursor.moveToNext();
        }
*/
        /*cursor = manager.getCursor();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Logger.v("", "Column Value: " + cursor.getString(0));
            Logger.v("", "Column Value1: " + cursor.getString(1));
            cursor.moveToNext();
        }*/

        this.stopSelf();

        return START_NOT_STICKY;
    }
}
