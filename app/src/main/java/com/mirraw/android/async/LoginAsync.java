package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 4/8/15.
 */
public class LoginAsync extends CoreAsync<Request, Void, Response> {

    public interface LoginLoader {
        public void startLogin();

        public void onLoginResponseLoaded(Response response);

        public void onloginSuccess(Response response);

        public void onLoginFailed(Response response);

        public void couldNotGetLoginResponse();
    }

    private LoginLoader mLoginLoader;

    public LoginAsync(LoginLoader blocksLoader) {
        mLoginLoader = blocksLoader;
    }


    @Override
    protected Response doInBackground(Request... params) {
        Request request = params[0];
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
        if (response != null && mLoginLoader != null) {
            mLoginLoader.onLoginResponseLoaded(response);
        } else if (response == null && mLoginLoader != null) {
            mLoginLoader.onLoginFailed(response);
        }
    }
}
