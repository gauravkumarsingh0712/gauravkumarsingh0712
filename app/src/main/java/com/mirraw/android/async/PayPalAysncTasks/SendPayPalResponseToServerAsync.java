package com.mirraw.android.async.PayPalAysncTasks;

import com.mirraw.android.async.CoreAsync;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 21/8/15.
 */
public class SendPayPalResponseToServerAsync extends CoreAsync<Request, Void, Response> {

    public interface PaypalResponseSubmitter {
        public void submitPaypalResponse();

        public void onPaypalResponseSubmissionSuccess(Response response);

        public void onPaypalResponseSubmissionFailure(Response response);
    }

    private PaypalResponseSubmitter mPaypalResponseSubmitter;

    public SendPayPalResponseToServerAsync(PaypalResponseSubmitter paypalResponseSubmitter) {
        mPaypalResponseSubmitter = paypalResponseSubmitter;
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
            mPaypalResponseSubmitter.onPaypalResponseSubmissionSuccess(response);
        else
            mPaypalResponseSubmitter.onPaypalResponseSubmissionFailure(response);
    }
}
