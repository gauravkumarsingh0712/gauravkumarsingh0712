package com.mirraw.android.Utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mirraw.android.R;

/**
 * Created by vihaan on 11/7/15.
 */
public class Utils {


    public static String addHttpSchemeIfMissing(String url) {
        if (!url.toLowerCase().matches("^\\w+://.*")) {
            url = "http://" + url;
        }
        return url;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static void showSnackBar(String msg, Activity activity, int duration) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), msg, duration)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(activity.getResources().getColor(R.color.dark_grey));
        snackbar.getView().setPadding(0, 10, 0, 10);
        snackbar.show();
    }

    public static double round(double totalAddonPrice, int decimalPlaces) {
        if (decimalPlaces < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, decimalPlaces);
        totalAddonPrice = totalAddonPrice * factor;
        long tmp = Math.round(totalAddonPrice);
        return (double) tmp / factor;
    }

}