package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by vihaan on 3/7/15.
 */
public class GetBlocksAsync extends CoreAsync<Request, Void, Response> {

    public interface BlocksLoader
    {
        public void loadBlocks();
        public void onBlocksLoaded(Response response);
        public void onBlocksLoadFailed(Response response);
        public void couldNotLoadBlocks();
    }

    private BlocksLoader mBlocksLoader;
    public GetBlocksAsync(BlocksLoader blocksLoader) {
        mBlocksLoader = blocksLoader;
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
        if(response != null && mBlocksLoader != null)
        {
            mBlocksLoader.onBlocksLoaded(response);
        }
        else if(response == null && mBlocksLoader != null)
        {
            mBlocksLoader.onBlocksLoadFailed(response);
        }
    }
}
