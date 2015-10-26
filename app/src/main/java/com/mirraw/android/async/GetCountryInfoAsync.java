package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by pavitra on 13/7/15.
 */
public class GetCountryInfoAsync extends CoreAsync<Request, Void, Response> {

    public interface CountryInfoLoader
    {
        public void loadCountryInfo();
        public void onCountryInfoLoaded(String response);
        public void onCountryInfoLoadFailed(int responseCode);
        public void couldNotLoadCountryInfo();
    }

    private CountryInfoLoader mCountryInfoLoader;
    public GetCountryInfoAsync(CountryInfoLoader countryInfoLoader) {
        mCountryInfoLoader = countryInfoLoader;
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
        if(!TextUtils.isEmpty(response.getBody()) && mCountryInfoLoader != null)
        {
            mCountryInfoLoader.onCountryInfoLoaded(response.getBody());
        }
        else if(TextUtils.isEmpty(response.getBody()) && mCountryInfoLoader != null)
        {
            mCountryInfoLoader.onCountryInfoLoadFailed(response.getResponseCode());
        }
    }
}
