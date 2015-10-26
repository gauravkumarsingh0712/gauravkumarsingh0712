package com.mirraw.android.async;

/**
 * Created by vihaan on 3/7/15.
 */

import android.os.AsyncTask;
import android.os.Build;

import com.mirraw.android.network.ApiClient;

/**
 * Created by vihaan on 05/06/15.
 */
public abstract class CoreAsync<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected ApiClient mApiClient;

    public CoreAsync()
    {

        mApiClient = new ApiClient();

    }

    public AsyncTask<Params, Progress, Result> executeTask(Params... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return executeOnExecutor(THREAD_POOL_EXECUTOR, params);
        } else {
            return execute(params);
        }
    }
}
