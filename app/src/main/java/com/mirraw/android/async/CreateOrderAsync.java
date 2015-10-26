package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 13/8/15.
 */
public class CreateOrderAsync extends CoreAsync<Request, Void, Response> {

    public interface CreateOrderLoader {
        public void createOrder(String pay_type);

        public void createOrderSuccessful(Response response);

        public void createOrderFailed(Response response);
    }

    private CreateOrderLoader mCreateOrderLoader;

    public CreateOrderAsync(CreateOrderLoader createOrderLoader) {
        mCreateOrderLoader = createOrderLoader;
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
            mCreateOrderLoader.createOrderSuccessful(response);
        else
            mCreateOrderLoader.createOrderFailed(response);
        /*} else if (response.getBody() == null && mDeleteItemCartLoader != null) {
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        }*/
    }
}
