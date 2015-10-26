package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by varun on 14/8/15.
 */
public class OrderTrackingAsync extends CoreAsync<Request, Void, Response> {

    public interface OrdersLoader {
        public void loadOrders();
        public void onOrdersLoaded(Response response);
        public void onOrderLoadingFailed(Response response);
    }

    private OrdersLoader mOrdersLoader;

    public OrderTrackingAsync(OrdersLoader ordersLoader){
        this.mOrdersLoader = ordersLoader;
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

        if (!TextUtils.isEmpty(response.getBody()) && mOrdersLoader != null){
            mOrdersLoader.onOrdersLoaded(response);
        }

        if (TextUtils.isEmpty(response.getBody()) && mOrdersLoader != null){
            mOrdersLoader.onOrderLoadingFailed(response);
        }

    }
}
