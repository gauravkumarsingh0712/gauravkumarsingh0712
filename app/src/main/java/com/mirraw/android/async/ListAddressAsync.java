package com.mirraw.android.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.mirraw.android.R;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.fragments.CartFragment;

import java.io.IOException;

/**
 * Created by abc on 8/8/2015.
 */
public class ListAddressAsync extends CoreAsync<Request, Void, Response> {

    public interface AddressLoader {
        public void loadAddressList();

        public void loadAddressFailed(Response response);

        public void loadAddressSuccess(Response response);
    }

    private AddressLoader mAddressLoader;
    private Activity mActivity;
    public ListAddressAsync(AddressLoader addressLoader, Activity activity) {
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
        if (response != null && mAddressLoader != null) {
            mAddressLoader.loadAddressSuccess(response);
        } else if (mAddressLoader != null) {
            mAddressLoader.loadAddressFailed(response);
        }
    }
}
