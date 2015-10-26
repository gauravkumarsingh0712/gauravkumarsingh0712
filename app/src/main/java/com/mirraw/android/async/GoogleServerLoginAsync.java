package com.mirraw.android.async;

import android.content.Context;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by nimesh on 20/8/15.
 */
public class GoogleServerLoginAsync extends CoreAsync<Object, Void, Response> {

    public interface GoogleServerLoginLoader {
        void onPreGoogleServerLogin();

        void loadGoogleServerLogin();

        void onGoogleServerLoginComplete(Response response);

        void onGoogleServerLoginSuccess();

        void onGoogleServerLoginFailure();

    }

    GoogleServerLoginLoader mGoogleServerLoginLoader;

    public GoogleServerLoginAsync(GoogleServerLoginLoader mGoogleServerLoginLoader) {
        this.mGoogleServerLoginLoader = mGoogleServerLoginLoader;

    }

    @Override
    protected Response doInBackground(Object... params) {
        String email = (String) params[0];
        Context context = (Context) params[1];
        String scopes = (String) params[2];
        String accessToken = null;
        try {
            accessToken = GoogleAuthUtil.getToken(context, email, scopes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }

        Request request = (Request) params[3];
        request.getHeaders().put(Headers.ACCESS_TOKEN, accessToken);
        Response response = null;
        try {
            response = mApiClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if (response != null && mGoogleServerLoginLoader != null)
            mGoogleServerLoginLoader.onGoogleServerLoginComplete(response);
    }
}
