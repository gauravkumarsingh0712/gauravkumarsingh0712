package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by nimesh on 5/8/15.
 */
public class RegisterAsync extends CoreAsync<Request, Void, Response> {

    ApiClient mApiClient;
    Registeration mRegisteration;

    public interface Registeration {

        public void register();

        public void onPreRegister();

        public void onRegisterLoaded(Response response);



        public void onRegistrationFailed(Response response);    }

    public RegisterAsync(Registeration registeration) {
        mRegisteration = registeration;
        this.mApiClient = new ApiClient();

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

        if (!TextUtils.isEmpty(response.getBody()) && mRegisteration != null) {
//            if (response.getResponseCode() == 200)
//                mRegisteration.onRegisteredSuccessfully();
//            else
//                mRegisteration.onNotRegisteredSuccessfully(response.getBody(), response.getResponseCode());
            mRegisteration.onRegisterLoaded(response);

        } else if (TextUtils.isEmpty(response.getBody()) && mRegisteration != null) {

            //mRegisteration.onNotRegisteredSuccessfully();

        }


    }
}
