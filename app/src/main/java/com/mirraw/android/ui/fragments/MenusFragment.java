package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.async.GetMenusAsync;
import com.mirraw.android.models.menus.Menu;
import com.mirraw.android.models.menus.Menus;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.adapters.MenusAdapter;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vihaan on 6/7/15.
 */

public class MenusFragment extends Fragment implements
        GetMenusAsync.MenusLoader,

        MenusAdapter.MenuClickListener, RippleView.OnRippleCompleteListener {

    public static final String tag = MenusFragment.class.getSimpleName();



    public interface ExpandingMenuActivity {
        void showExpandingMenuFragment(String columns);
    }

    private ExpandingMenuActivity mExpandingMenuActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mExpandingMenuActivity = (ExpandingMenuActivity) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_menus, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        if (sMenus == null || sMenus.size() == 0) {
            loadMenus();
        } else {
            showMenus();
        }
    }


    private void showMenus() {
        MenusAdapter menusAdapter = new MenusAdapter(this, sMenus);
        mMenusRecyclerView.setAdapter(menusAdapter);
        onMenuPostLoad();
    }

    private void initViews(View view) {
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
    }

    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetRL;

    private void initNoInternetView(View view) {
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainer);
        mNoInternetRL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mNoInternetRL.setVisibility(View.GONE);
        ((RippleView) view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel(View view) {
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
    }


    private RecyclerView mMenusRecyclerView;

    private void initRecyclerView(View view) {
        mMenusRecyclerView = (RecyclerView) view.findViewById(R.id.menusRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mMenusRecyclerView.setLayoutManager(manager);
    }


    private GetMenusAsync mGetMenusAsync;

    @Override
    public void onMenuPreLoad() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mNoInternetRL.setVisibility(View.GONE);
        mMenusRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void loadMenus() {

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(ApiUrls.API_MENUS, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        mGetMenusAsync = new GetMenusAsync(this);
        mGetMenusAsync.executeTask(request);
    }

    private static List<Menu> sMenus;

    @Override
    public void onMenusLoaded(Response response) {
        Logger.v("Response code:", "Response Code:" + response.getResponseCode() + response.getBody());
        Gson gson = new Gson();
        try {
            if (response.getResponseCode() == 200) {

                JSONObject json = new JSONObject(response.getBody());
                String jsonArray = json.getString("menus");

//            Type listOfTestObject = new TypeToken<List<Menu>>() {
//            }.getType();
                Menus menus = gson.fromJson(response.getBody(), Menus.class);
                sMenus = menus.getMenus();
                showMenus();
            } else {
                onMenusLoadFailed(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            couldNotLoadMenus();

        }

    }

    @Override
    public void onMenusLoadFailed(Response response) {
        Logger.v("Response code:", "Response Code:" + response.getResponseCode() + response.getBody());
        int responseCode = response.getResponseCode();
        if (responseCode == 0) {
            onNoInternet();
        } else {
            couldNotLoadMenus();
        }
    }

    @Override
    public void couldNotLoadMenus() {
        Toast.makeText(getActivity(), "Problem loading menu", Toast.LENGTH_LONG).show();
        onNoInternet();
    }

    @Override
    public void onMenuPostLoad() {
        mMenusRecyclerView.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
        mNoInternetRL.setVisibility(View.GONE);
    }

    private void onNoInternet() {
        mNoInternetRL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
        mMenusRecyclerView.setVisibility(View.GONE);
    }


    private void onRetryButtonClicked() {
        mNoInternetRL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.VISIBLE);
        loadMenus();
    }

    @Override
    public void onDestroy() {
        if (mGetMenusAsync != null) {
            mGetMenusAsync.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onMenuClicked(View view, int position) {

        if (mExpandingMenuActivity != null) {

            Menu menu = sMenus.get(position);
            Gson gson = new Gson();
            mExpandingMenuActivity.showExpandingMenuFragment(gson.toJson(menu));
        }
    }


    //    http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.retry_button_ripple_container:
                onRetryButtonClicked();
                break;
        }
    }
}
