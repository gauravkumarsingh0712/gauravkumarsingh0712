package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by vihaan on 8/7/15.
 */
public class ProductDetailAsync extends CoreAsync<Request, Void, Response> {

    private ProductLoader mProductLoader;

    public interface ProductLoader
    {
        public void onProductPreLoad();
        public void loadProduct();
        public void onProductLoaded(Response response);
        public void onProductLoadFailed(Response response);
        public void couldNotLoadProduct();
        public void onProductPostLoad();
    }

    public ProductDetailAsync(ProductLoader productLoader)
    {
        mProductLoader = productLoader;
    }

    @Override
    protected Response doInBackground(Request... params) {
        Request url = params[0];

        Response response = null;
        try {
            //Logger.d(ProductDetailAsync.class.getSimpleName(), "URL IS: " + url);
            response = mApiClient.execute(url);
        } catch (IOException e) {

            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if(!TextUtils.isEmpty(response.getBody()) && mProductLoader != null)
        {
            //Logger.d(ProductDetailAsync.class.getSimpleName(), "RESPONSE IS: " + response);
            mProductLoader.onProductLoaded(response);
        }
        else if(TextUtils.isEmpty(response.getBody()) && mProductLoader != null)
        {
            mProductLoader.onProductLoadFailed(response);
        }
    }

}
