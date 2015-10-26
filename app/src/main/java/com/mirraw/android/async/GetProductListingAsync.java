package com.mirraw.android.async;

import android.content.Context;
import android.text.TextUtils;

import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by varun on 30/7/15.
 */
public class GetProductListingAsync extends CoreAsync<Request, Void, Response> {

    public interface ProductListingLoader {
        public void loadProductListing();

        public void onProductListingLoaded(Response response);

        public void onProductListingLoadFailed(Response responseCode);

        public void couldNotLoadProductListing();

        public void onProductListingPostLoad();
    }

    ProductListingLoader mProductListingLoader;
    Context mContext;
    ApiClient mApiClient;

    public GetProductListingAsync(ProductListingLoader productListingLoader, Context context) {
        mApiClient = new ApiClient();
        this.mProductListingLoader = productListingLoader;
        this.mContext = context;
    }


    @Override
    protected Response doInBackground(Request... params) {

        Request url = params[0];
        Response response = null;

        try {
            response = mApiClient.execute(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);

//        if(response.getBody() != null && mProductListingLoader != null){
        if (!TextUtils.isEmpty(response.getBody()) && mProductListingLoader != null) {
            mProductListingLoader.onProductListingLoaded(response);
        }

        if (TextUtils.isEmpty(response.getBody()) && mProductListingLoader != null) {
            mProductListingLoader.onProductListingLoadFailed(response);
        }
    }
}
