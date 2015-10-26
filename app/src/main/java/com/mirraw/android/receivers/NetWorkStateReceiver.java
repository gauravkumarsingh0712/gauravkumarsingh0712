package com.mirraw.android.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mirraw.android.services.SyncService;

/**
 * Created by pavitra on 31/8/15.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    Intent mService = null;
    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (isConnected(context)) {
            if (firstConnect) {
                if (mService != null) {
                    context.stopService(mService);
                }
                mService = new Intent(context, SyncService.class);
                context.startService(mService);
                firstConnect = false;
            }
        } else {
            firstConnect = true;
            mService = new Intent(context, SyncService.class);
            context.stopService(mService);
        }
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        return isConnected;
    }
}