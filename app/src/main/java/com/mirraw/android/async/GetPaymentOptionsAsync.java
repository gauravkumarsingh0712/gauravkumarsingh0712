package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 11/8/15.
 */
public class GetPaymentOptionsAsync extends CoreAsync<Request, Void, Response> {

    public interface PaymentOptionsLoader {
        public void loadPaymentOptions();
        public void onPaymentOptionsLoaded(Response response);
        public void onPaymentOptionsLoadedFailed(Response response);
    }

    private PaymentOptionsLoader mPaymentOptionsLoader;

    public GetPaymentOptionsAsync(PaymentOptionsLoader paymentOptionsLoader) {
        mPaymentOptionsLoader = paymentOptionsLoader;
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
        if (response.getResponseCode() == 200) {
            mPaymentOptionsLoader.onPaymentOptionsLoaded(response);
        } else {
            mPaymentOptionsLoader.onPaymentOptionsLoadedFailed(response);
        }
    }

}
