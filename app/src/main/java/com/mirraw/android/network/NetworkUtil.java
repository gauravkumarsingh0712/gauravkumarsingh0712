package com.mirraw.android.network;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by vihaan on 6/8/15.
 */
public class NetworkUtil {

    public static String getDeviceId(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
