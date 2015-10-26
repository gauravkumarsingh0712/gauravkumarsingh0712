package com.mirraw.android.async;

import android.app.Activity;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.AddBillingShippingActivity;

import java.io.IOException;

/**
 * Created by Gaurav on 8/5/2015.
 */
public class AddBillingAddressAsync extends CoreAsync<Request, Void, Response> {

    public interface AddressLoader {

        public void syncOneAdress();

        public void syncDifferentAddresses();

        public void saveSecondAddress();

        public void onEmptyAddress();

        public void onOneAddAddressLoaded(Response response);

        public void onDifferentAddAddressLoaded(Response response);

    }

    private AddressLoader mAddressLoader;
    private int mFlag = -1;
    private Activity mActivity;

    public AddBillingAddressAsync(AddressLoader addressLoader, int flag, Activity activity) {
        mAddressLoader = addressLoader;
        mFlag = flag;
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
            if (mFlag == AddBillingShippingActivity.SYNC_ONE_ADDRESS) {
                mAddressLoader.onOneAddAddressLoaded(response);
            } else if (mFlag == AddBillingShippingActivity.SYNC_DIFFERENT_ADDRESS) {
                mAddressLoader.onDifferentAddAddressLoaded(response);
            }
        } else if (response == null && mAddressLoader != null) {
            mAddressLoader.onEmptyAddress();
        }
    }

}
