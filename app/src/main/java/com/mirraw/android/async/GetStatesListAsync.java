package com.mirraw.android.async;

import android.content.Context;

import com.mirraw.android.network.ApiClient;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by Nimesh Luhana on 21-07-2015.
 */
public class GetStatesListAsync extends CoreAsync<Request, Void, Response> {


    public interface StatesLoader {
        void onStatesPreload();

        void loadStates();

        void onStatesLoaded(String response);

        void onStatesLoadFailed(int response);

        void couldNotLoadStates();

        void onStatesPostLoad();
    }


    StatesLoader mStatesLoader;
    Context mContext;
    ApiClient mApiClient;

    public GetStatesListAsync(StatesLoader statesLoader, Context cxt) {
        mApiClient = new ApiClient();
        this.mStatesLoader = statesLoader;
        this.mContext = cxt;
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

        if (response.getResponseCode() == 200 && mStatesLoader != null)
            mStatesLoader.onStatesLoaded(response.getBody());
        else if (response.getResponseCode() != 200 && mStatesLoader != null) {
            //mStatesLoader.onStatesLoadFailed(mApiClient.getResponseCode());
            mStatesLoader.onStatesLoadFailed(response.getResponseCode());
        }
    }
}
