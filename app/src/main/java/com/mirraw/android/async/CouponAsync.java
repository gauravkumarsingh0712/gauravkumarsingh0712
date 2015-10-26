package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 18/8/15.
 */
public class CouponAsync extends CoreAsync<Request, Void, Response> {

    public interface CouponApplyLoader {
        public void applyCoupon();

        public void couponAppliedSuccessfully(Response response);

        public void couponAppliedFailed(Response response);
    }

    private CouponApplyLoader mCouponApplyLoader;

    public CouponAsync(CouponApplyLoader couponApplyLoader) {
        mCouponApplyLoader = couponApplyLoader;
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
        if (response.getResponseCode() == 200 && mCouponApplyLoader != null)
            mCouponApplyLoader.couponAppliedSuccessfully(response);
        else if(mCouponApplyLoader != null)
            mCouponApplyLoader.couponAppliedFailed(response);
        /*} else if (response.getBody() == null && mDeleteItemCartLoader != null) {
            mDeleteItemCartLoader.cartItemDeletionFailed(response);
        }*/
    }
}

