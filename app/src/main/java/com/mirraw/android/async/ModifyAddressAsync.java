package com.mirraw.android.async;

import android.app.Activity;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 10/9/15.
 */
public class ModifyAddressAsync extends CoreAsync<Request, Void, Response> {

    public interface ModifyAddress {

        public void modifyAddress();

        public void onModifyAddressFailed(Response response);

        public void onModifyAddressSuccess(Response response);
    }

    private ModifyAddress mModifyAddress;
    private Activity mActivity;

    public ModifyAddressAsync(ModifyAddress modifyAddress, Activity activity) {
        mModifyAddress = modifyAddress;
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
        if (response != null && mModifyAddress != null) {
            mModifyAddress.onModifyAddressSuccess(response);

        } else if (response == null && mModifyAddress != null) {
            mModifyAddress.onModifyAddressFailed(response);
        }
    }
}
