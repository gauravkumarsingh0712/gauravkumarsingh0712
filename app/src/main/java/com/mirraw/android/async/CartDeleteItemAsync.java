package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 6/8/15.
 */
public class CartDeleteItemAsync extends CoreAsync<Request, Void, Response> {

    public interface DeleteItemCartLoader {
        public void removeItemFromCart(int id);

        public void cartItemDeletedSuccessfully(Response response);

        public void cartItemDeletionFailed(Response response);
    }

    private DeleteItemCartLoader mDeleteItemCartLoader;

    public CartDeleteItemAsync(DeleteItemCartLoader deleteItemCartLoader) {
        mDeleteItemCartLoader = deleteItemCartLoader;
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
                mDeleteItemCartLoader.cartItemDeletedSuccessfully(response);
        else
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        /*} else if (response.getBody() == null && mDeleteItemCartLoader != null) {
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        }*/
    }
}
