package com.mirraw.android.async;

import android.database.Cursor;
import android.text.TextUtils;

import com.mirraw.android.database.Tables;
import com.mirraw.android.models.productDetail.ProductDetail;
import com.google.gson.Gson;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vihaan on 10/7/15.
 */
public class CartAsync  extends CoreAsync<Request, Void, Response> {

    public interface CartLoader
    {
        public void loadCart();
        public void onEmptyCart(Response response);
        public void onCartLoaded(Response response);
    }

    private CartLoader mCartLoader;
    public CartAsync(CartLoader cartLoader)
    {
        mCartLoader = cartLoader;
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
        if(response.getResponseCode()==0){
            mCartLoader.onEmptyCart(response);
        }
        else {
            if (response.getBody() != null && mCartLoader != null) {
                mCartLoader.onCartLoaded(response);
            } else if (TextUtils.isEmpty(response.getBody()) && mCartLoader != null) {
                mCartLoader.onEmptyCart(response);
            }
        }
    }
}
