package com.mirraw.android.async;

import android.content.Context;
import android.text.TextUtils;

import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by varun on 10/8/15.
 */
public class GetProductDetailTabAsync extends CoreAsync<Request, Void, Response> {

    public interface ProductDetailsTabLoader {
        public void loadTabDetails();

        public void onTabDetailsLoaded(Response response);

        public void onTabDetailsLoadingFailed(Response response);
    }

    private ProductDetailsTabLoader mProductDetailsTabLoader;
    Context mContext;
    ApiClient mApiClient;

    public GetProductDetailTabAsync(ProductDetailsTabLoader productDetailsTabLoader, Context context) {
        mApiClient = new ApiClient();
        this.mContext = context;
        this.mProductDetailsTabLoader = productDetailsTabLoader;
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

        if (!TextUtils.isEmpty(response.getBody()) && mProductDetailsTabLoader != null && response.getResponseCode() == 200) {
            mProductDetailsTabLoader.onTabDetailsLoaded(response);
        }
        if (response.getResponseCode() != 200 && mProductDetailsTabLoader != null) {
            mProductDetailsTabLoader.onTabDetailsLoadingFailed(response);
        }
    }
}
