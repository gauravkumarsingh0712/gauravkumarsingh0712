package com.mirraw.android.sharedresources;

import android.util.Log;

import org.json.JSONException;

/**
 * Created by pavitra on 22/9/15.
 */
public class Logger {


    public static void d(String tag, String msg) {
        if (AppConfig.shouldLog()) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (AppConfig.shouldLog()) {
            Log.e(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (AppConfig.shouldLog()) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (AppConfig.shouldLog()) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (AppConfig.shouldLog()) {
            Log.w(tag, msg);
        }
    }

    public static void wtf(String tag, String msg) {
        if (AppConfig.shouldLog()) {
            Log.wtf(tag, msg);
        }
    }

    public static void e(String tag, String s, JSONException e) {
        if (AppConfig.shouldLog()) {
            Log.e(tag, s, e);
        }
    }
}
