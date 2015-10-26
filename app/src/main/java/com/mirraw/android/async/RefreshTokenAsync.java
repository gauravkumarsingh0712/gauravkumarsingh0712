package com.mirraw.android.async;

import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.database.SyncRequestManager;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;

/**
 * Created by pavitra on 28/9/15.
 */
public class RefreshTokenAsync extends CoreAsync<Request, Void, Response> {
    String tag = RefreshTokenAsync.class.getSimpleName();
    private Request mRequest;

    @Override
    protected Response doInBackground(Request... params) {
        mRequest = params[0];

        Response response = null;
        try {
            response = mApiClient.getResponse(mRequest);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if (response != null) {
            //response.setResponseCode(401);
            if (response.getResponseCode() == 401) {
                Logger.v(tag, "Refresh token failed. Logging out.");
                LoginManager.onServerLogout();
            } else if (response.getResponseCode() == 200) {
                modifyHeaders(response);
            } else if (response.getResponseCode() == 0) {
                Logger.v(tag, "Inserting refresh token request in SyncRequestManager.");
                new SyncRequestManager().insertRow(new Gson().toJson(mRequest));
            }
        }
    }

    private void modifyHeaders(Response response) {
        try {
            Logger.v(tag, "Token Refreshed");
            SharedPreferencesManager mSharedPreferencesManager;// = new SharedPreferencesManager(Mirraw.getAppContext());

            Gson gson = new Gson();
            mSharedPreferencesManager = new SharedPreferencesManager(Mirraw.getAppContext());
            mSharedPreferencesManager.setLoginResponse(gson.toJson(response));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
