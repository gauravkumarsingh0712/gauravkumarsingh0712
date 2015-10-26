package com.mirraw.android.async;

import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.io.IOException;

/**
 * Created by vihaan on 6/7/15.
 */
public class GetMenusAsync extends CoreAsync<Request, Void, Response> {

    private MenusLoader mMenusLoader;

    public interface MenusLoader

    {
        public void onMenuPreLoad();

        public void loadMenus();

        public void onMenusLoaded(Response response);

        public void onMenusLoadFailed(Response response);

        public void couldNotLoadMenus();

        public void onMenuPostLoad();
    }

    public GetMenusAsync(MenusLoader menusLoader) {
        mMenusLoader = menusLoader;
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
        if (response.getBody() != null && mMenusLoader != null) {
            mMenusLoader.onMenusLoaded(response);
        } else if (response.getBody() == null && mMenusLoader != null) {
            mMenusLoader.onMenusLoadFailed(response);
        }
    }
}
