package com.mirraw.android.async.PayPalAysncTasks;

import com.mirraw.android.async.CoreAsync;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 21/8/15.
 */
public class PaypalCreateOrderAsync extends CoreAsync<Request, Void, Response> {

    public interface PaypalCreateOrderLoader {
        public void createOrderPaypal();

        public void createPaypalOrderSuccessful(Response response);

        public void createPaypalOrderFailed(Response response);
    }

    private PaypalCreateOrderLoader mPaypalCreateOrderLoader;

    public PaypalCreateOrderAsync(PaypalCreateOrderLoader createPaypalOrderLoader) {
        mPaypalCreateOrderLoader = createPaypalOrderLoader;
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
        if (response.getResponseCode() == 200 && mPaypalCreateOrderLoader != null) {
            mPaypalCreateOrderLoader.createPaypalOrderSuccessful(response);
        }
        if (response.getResponseCode() != 200 && mPaypalCreateOrderLoader != null) {
            mPaypalCreateOrderLoader.createPaypalOrderFailed(response);
        }
        /*} else if (response.getBody() == null && mDeleteItemCartLoader != null) {
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        }*/
    }
}
