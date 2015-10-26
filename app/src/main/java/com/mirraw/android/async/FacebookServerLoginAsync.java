package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.fragments.LoginDialogFragment;

import java.io.IOException;

/**
 * Created by nimesh on 20/8/15.
 */
public class FacebookServerLoginAsync extends CoreAsync<Request, Void, Response> {

    public interface FacebookServerLoginLoader {
        void onPreFacebookServerLogin();

        void loadFacebookServerLogin();

        void onFacebookServerLoginComplete(Response response);

        void onFacebookServerLoginSuccess(LoginDialogFragment.LoginCallbacks loginCallbacks);

        void onFacebookServerLoginFailure();

    }

    FacebookServerLoginLoader mFacebookServerLoginLoader;

    public FacebookServerLoginAsync(FacebookServerLoginLoader mFacebookServerLoginLoader) {
        this.mFacebookServerLoginLoader = mFacebookServerLoginLoader;

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
        if (response.getBody() != null && mFacebookServerLoginLoader != null)
            mFacebookServerLoginLoader.onFacebookServerLoginComplete(response);


    }
}
