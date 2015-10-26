package com.mirraw.android.async;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;


import java.io.IOException;
import java.net.HttpURLConnection;


public class GetSearchResultsAsync extends CoreAsync<Request, Void, Response> {

    public interface SearchResultsLoader {

        public void onSearchResultsPreLoad();

        public void loadSearchResults();

        public void onSearchResultsLoaded(Response response);

        public void onSearchResultsLoadFailed(Response responseCode);

        public void couldNotLoadSearchResults();

        public void onSearchResultsPostLoad();

        public void beforeSearchQueryEntered();


    }

    Context cxt;
    ApiClient mApiClient;
    SearchResultsLoader mSearchResultsLoader;


    public GetSearchResultsAsync(SearchResultsLoader searchResultsLoader, Context cxt) {
        this.mApiClient = new ApiClient();
        mSearchResultsLoader = searchResultsLoader;
        this.cxt = cxt;


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

        if (!TextUtils.isEmpty(response.getBody()) && mSearchResultsLoader != null) {
            mSearchResultsLoader.onSearchResultsLoaded(response);
        }
        if (TextUtils.isEmpty(response.getBody()) && mSearchResultsLoader != null) {
            mSearchResultsLoader.onSearchResultsLoadFailed(response);

        }


    }

}
