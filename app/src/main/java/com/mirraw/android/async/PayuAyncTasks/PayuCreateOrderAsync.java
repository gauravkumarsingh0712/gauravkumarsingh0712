package com.mirraw.android.async.PayuAyncTasks;

import com.mirraw.android.async.CoreAsync;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 25/8/15.
 */
public class PayuCreateOrderAsync extends CoreAsync<Request, Void, Response> {

    public interface PayuCreateOrderLoader {
        public void createOrderPayu(String paytype);

        public void createPayuOrderSuccessful(Response response);

        public void createPayuOrderFailed(Response response);
    }

    private PayuCreateOrderLoader mPayuCreateOrderLoader;

    public PayuCreateOrderAsync(PayuCreateOrderLoader createPayuOrderLoader) {
        mPayuCreateOrderLoader = createPayuOrderLoader;
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
        if (response.getResponseCode() == 200 && mPayuCreateOrderLoader != null) {
            mPayuCreateOrderLoader.createPayuOrderSuccessful(response);
        }
        if (response.getResponseCode() != 200 && mPayuCreateOrderLoader != null) {
            mPayuCreateOrderLoader.createPayuOrderFailed(response);
        }
        /*} else if (response.getBody() == null && mDeleteItemCartLoader != null) {
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        }*/
    }
}
