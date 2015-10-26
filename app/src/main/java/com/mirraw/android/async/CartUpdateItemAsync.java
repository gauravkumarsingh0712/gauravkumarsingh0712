package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 7/8/15.
 */
public class CartUpdateItemAsync extends CoreAsync<Request, Void, Response> {

    public interface UpdateItemCartLoader {
        public void updateCartItem(int id, int quantity);

        public void cartItemUpdatedSuccessfully(Response response);

        public void cartItemUpdationFailed(Response response);
    }

    private UpdateItemCartLoader mUpdateItemCartLoader;

    public CartUpdateItemAsync(UpdateItemCartLoader updateItemCartLoader) {
        mUpdateItemCartLoader = updateItemCartLoader;
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
        if (response.getResponseCode() == 200)
            mUpdateItemCartLoader.cartItemUpdatedSuccessfully(response);
        else
            mUpdateItemCartLoader.cartItemUpdationFailed(response);
        /*} else if (response.getBody() == null && mDeleteItemCartLoader != null) {
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        }*/
    }
}
