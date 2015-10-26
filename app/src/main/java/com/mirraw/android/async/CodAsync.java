package com.mirraw.android.async;

import android.text.TextUtils;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by varun on 12/8/15.
 */
public class CodAsync extends CoreAsync<Request, Void, Response> {

    public interface CodLoader{
        public void onCodLoaded(Response response);
        public void onCodLoadingFailed(Response response);
    }

    private CodLoader mCodLoader;

    public CodAsync(CodLoader codLoader){
        this.mCodLoader = codLoader;
    }

    @Override
    protected Response doInBackground(Request... params) {

        Request request = params[0];

        Response response = null;

        try {
            response = mApiClient.execute(request);
        } catch (IOException e){
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);
        if(!TextUtils.isEmpty(response.getBody()) && mCodLoader != null){
            mCodLoader.onCodLoaded(response);
        }

        if (TextUtils.isEmpty(response.getBody()) && mCodLoader != null){
            mCodLoader.onCodLoadingFailed(response);
        }
    }
}
