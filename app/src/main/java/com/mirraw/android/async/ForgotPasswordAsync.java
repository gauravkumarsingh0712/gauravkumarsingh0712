package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by nimesh on 6/8/15.
 */
public class ForgotPasswordAsync extends CoreAsync<Request, Void, Response> {

    public interface PasswordResetLoader {
        public void resetPassword();

        public void onPreResetPassword();



        public void onResetLoaded(Response response);

        public void onResetFailed(Response response);




    }

    ApiClient mApiClient;
    PasswordResetLoader mPasswordResetLoader;

    public ForgotPasswordAsync(PasswordResetLoader passwordResetLoader) {
        mApiClient = new ApiClient();
        mPasswordResetLoader = passwordResetLoader;

    }


    @Override
    protected Response doInBackground(Request... params) {
        Request request = params[0];
        Response response=null;
        try {
            response=mApiClient.execute(request);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if(!TextUtils.isEmpty(response.getBody())&&mPasswordResetLoader!=null)
        {
            mPasswordResetLoader.onResetLoaded(response);

        }



    }
}
