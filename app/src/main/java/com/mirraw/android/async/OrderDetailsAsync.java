package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by varun on 19/8/15.
 */
public class OrderDetailsAsync extends CoreAsync<Request, Void, Response> {

    public interface OrderDetailsLoader{
        public void loadOrderDetails();
        public void onOrderDetailsLoaded(Response response);
        public void couldNotLoadOrderDetails(Response response);
    }

    OrderDetailsLoader mOrderDetailsLoader;

    public OrderDetailsAsync(OrderDetailsLoader orderDetailsLoader){
        this.mOrderDetailsLoader = orderDetailsLoader;
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
        if(!TextUtils.isEmpty(response.getBody()) && mOrderDetailsLoader != null && response.getResponseCode() == 200){
            mOrderDetailsLoader.onOrderDetailsLoaded(response);
        }
        if(TextUtils.isEmpty(response.getBody()) && mOrderDetailsLoader != null){
            mOrderDetailsLoader.couldNotLoadOrderDetails(response);
        }
    }
}
