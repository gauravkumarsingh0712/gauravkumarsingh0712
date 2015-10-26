package com.mirraw.android.async;

import android.app.Activity;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by Gaurav on 8/5/2015.
 */
public class AddAddressAsync extends CoreAsync<Request, Void, Response> {

    public interface AddressLoader {
        public void addAddress();

        public void onAddAddressFailed(Response response);

        public void onAddAddressSuccess(Response response);
    }

    private AddressLoader mAddressLoader;
    private Activity mActivity;

    public AddAddressAsync(AddressLoader addressLoader, Activity activity) {
        mAddressLoader = addressLoader;
        mActivity = activity;
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
        if (mActivity.isFinishing()) {
            return;
        }
        if (mAddressLoader != null && response.getResponseCode() == 200) {
            mAddressLoader.onAddAddressSuccess(response);
        } else if (mAddressLoader != null && response.getResponseCode() != 200) {
            mAddressLoader.onAddAddressFailed(response);
        }
    }

}