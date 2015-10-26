package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 10/8/15.
 */
public class LogoutAsync extends CoreAsync<Request, Void, Void> {

    public interface LogoutLoader {
        public void startLogout();

    }

    private LogoutLoader mLogoutLoader;
    public LogoutAsync(LogoutLoader logoutLoader) {
        mLogoutLoader = logoutLoader;
    }


    @Override
    protected Void doInBackground(Request... params) {
        Request request = params[0];

        try {

            mApiClient.execute(request);
        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }
}

