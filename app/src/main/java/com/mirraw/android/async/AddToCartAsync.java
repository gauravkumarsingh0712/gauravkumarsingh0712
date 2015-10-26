package com.mirraw.android.async;

import android.app.Activity;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.ProductDetailActivity;

import java.io.IOException;

/**
 * Created by pavitra on 5/8/15.
 */
public class AddToCartAsync extends CoreAsync<Request, Void, Response> {

    public interface AddedToCartLoader {
        public void startAddingToCart();

        public void addedToCartSuccessful(Response response);

        public void addedToCartFailed(int responseCode);
    }

    private AddedToCartLoader mAddToCartLoader;
    private Activity mActivity;

    public AddToCartAsync(AddedToCartLoader addCartLoader, Activity activity) {
        mAddToCartLoader = addCartLoader;
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
        if (response != null && mAddToCartLoader != null) {
            mAddToCartLoader.addedToCartSuccessful(response);
        } else if (response == null && mAddToCartLoader != null) {
            //mAddToCartLoader.addedToCartFailed(mApiClient.getResponseCode());
            mAddToCartLoader.addedToCartFailed(response.getResponseCode());
        }
    }
}

