package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by Nimesh Luhana on 21-07-2015.
 */
public class GetCountriesListAsync extends CoreAsync<Request, Void, Response> {


    public interface CountriesLoader {
        void onCountriesPreload();

        void loadCountries();

        void onCountriesLoaded(Response response);

        void onCountriesLoadFailed(int response);

        void couldNotLoadCountries();

        void onCountriesPostLoad();
    }


    CountriesLoader mCountriesLoader;
    boolean mIsCountriescached = false;

    public GetCountriesListAsync(CountriesLoader countriesLoader, boolean isCountriescached) {
        this.mCountriesLoader = countriesLoader;
        this.mIsCountriescached = isCountriescached;
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

        if (response.getResponseCode() == 200 && mCountriesLoader != null)
            mCountriesLoader.onCountriesLoaded(response);
        else if (response.getResponseCode() != 200 && mCountriesLoader != null && !mIsCountriescached) {
            //mCountriesLoader.onCountriesLoadFailed(mApiClient.getResponseCode());
            mCountriesLoader.onCountriesLoadFailed(response.getResponseCode());
        }


    }
}
