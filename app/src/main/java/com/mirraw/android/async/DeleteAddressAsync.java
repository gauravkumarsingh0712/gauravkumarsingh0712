package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by abc on 8/10/2015.
 */
public class DeleteAddressAsync extends CoreAsync<Request, Void, Response> {

    public interface AddressLoader {

        public void deletedAddress(int position);

        public void onDeleteAddressFailed(Response response);

        public void onDeleteAddressSuccess(Response response);
    }

    private AddressLoader mAddressLoader;

    public DeleteAddressAsync(AddressLoader addressLoader) {
        mAddressLoader = addressLoader;
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
        if (response != null && mAddressLoader != null && response.getResponseCode() == 200) {
            mAddressLoader.onDeleteAddressSuccess(response);
        } else if (response == null && mAddressLoader != null) {
            mAddressLoader.onDeleteAddressFailed(response);
        } else if (mAddressLoader != null) {
            mAddressLoader.onDeleteAddressFailed(response);
        }
    }

}
