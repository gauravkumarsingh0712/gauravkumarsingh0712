package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.GetCountriesListAsync;
import com.mirraw.android.models.address.Countries;
import com.mirraw.android.models.address.Country;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.adapters.CountriesAdapter;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nimesh Luhana on 23-07-2015.
 */

public class SelectCountryFragment extends Fragment implements TextWatcher, GetCountriesListAsync.CountriesLoader,
        CountriesAdapter.CountryClickListener, RippleView.OnRippleCompleteListener {
    EditText mCountrySearchQuery;
    LinearLayout mNoInternetLayout;
    RippleView mRetryButtonRippleContainer;
    RecyclerView mRecyclerView;
    ProgressWheel mLoadingWheel;
    CountrySelectListener mCountrySelectListener;
    SharedPreferencesManager mSharedPreferencesManager;
    String mCountriesJsonString;
    boolean isCached = false;

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.retry_button_ripple_container:
                if (mCountriesAsync != null)
                    mCountriesAsync.cancel(true);
                loadCountries();
                loadData();
                break;
        }

    }


    public interface CountrySelectListener {
        void onCountryClicked(Country country);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mCountrySelectListener = (CountrySelectListener) activity;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferencesManager = new SharedPreferencesManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_select_country, container, false);
        return v;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initViews(view);

        mCountriesJsonString = mSharedPreferencesManager.getCountries();

        onCountriesPreload();

        loadData();

    }

    GetCountriesListAsync mCountriesAsync;

    void loadData() {
        if (!TextUtils.isEmpty(mCountriesJsonString)) {
            isCached = true;
            setCountriesAdapter(mCountriesJsonString);
            Response cachedResponse = new Response();
            cachedResponse.setBody(mCountriesJsonString);
            onCountriesLoaded(cachedResponse);


        }

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;// = new JSONObject();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        try {
            // Toast.makeText(getActivity(), "Login Response: " + sharedPreferencesManager.getLoginResponse(), Toast.LENGTH_LONG).show();
            //Logger.v("", "Login Response: " + sharedPreferencesManager.getLoginResponse());

            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put("token", getString(R.string.token));
            // Logger.v("", "Login Response: " + head.getJSONArray("Access-Token").get(0).toString());
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(ApiUrls.API_COUNTRIES, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();

        mCountriesAsync = new GetCountriesListAsync(this,isCached);
        mCountriesAsync.executeTask(request);

    }

    private void initViews(View view) {
        initSearchBox(view);
        initNoInternetLayout(view);
        initRecyclerView(view);
        initLoadingWheel(view);
        initNoMatchView(view);
    }

    TextView noSearchResuls;

    private void initNoMatchView(View view) {
        noSearchResuls = (TextView) view.findViewById(R.id.no_search_results);
    }

    GridLayoutManager mGridLayoutManager;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(getActivity(), 1);

        mRecyclerView.setLayoutManager(mGridLayoutManager);

    }

    private void initNoInternetLayout(View view) {

        mNoInternetLayout = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mRetryButtonRippleContainer = (RippleView) view.findViewById(R.id.retry_button_ripple_container);
        mRetryButtonRippleContainer.setOnRippleCompleteListener(this);

    }

    private void initSearchBox(View view) {
        mCountrySearchQuery = (EditText) view.findViewById(R.id.countrySearchQuery);
        mCountrySearchQuery.addTextChangedListener(this);
    }

    private void initLoadingWheel(View view) {
        mLoadingWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);

    }

    @Override
    public void onDestroy() {

        if (mCountriesAsync != null)
            mCountriesAsync.cancel(true);

        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


    }

    @Override
    public void afterTextChanged(Editable s) {

        if (s.length() > 0) {


            List<Country> mNewCountryList;

            mNewCountryList = new ArrayList<Country>();
            for (int i = 0; i < mCountry.size(); i++) {
                if (mCountry.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {

                    mNewCountryList.add(mCountry.get(i));

                }


            }
            if (mNewCountryList.size() == 0) {
                noSearchResuls();


            } else {
                CountriesAdapter countriesAdapter = new CountriesAdapter(this, mNewCountryList);

                mRecyclerView.swapAdapter(countriesAdapter, false);
                mGridLayoutManager.scrollToPosition(0);
                onCountriesPostLoad();
            }
        } else {
            CountriesAdapter countriesAdapter = new CountriesAdapter(this, mCountry);
            mRecyclerView.swapAdapter(countriesAdapter, false);
            onCountriesPostLoad();
        }


    }

    private void noSearchResuls() {
        noSearchResuls.setVisibility(View.VISIBLE);
        mLoadingWheel.setVisibility(View.GONE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);


    }

    @Override
    public void onCountryClicked(View v, int id) {
        Country country = null;
        String code = null;
        String name = null;
        Intent i = new Intent();
        for (int j = 0; j < mCountry.size(); j++) {
            country = mCountry.get(j);
            if (country.getId() == id) {
                break;
            }

        }

        mCountrySelectListener.onCountryClicked(country);


    }


    @Override
    public void onCountriesPreload() {

        noSearchResuls.setVisibility(View.GONE);
        mLoadingWheel.setVisibility(View.VISIBLE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mCountrySearchQuery.setVisibility(View.GONE);

    }

    @Override
    public void loadCountries() {
        noSearchResuls.setVisibility(View.GONE);
        mLoadingWheel.setVisibility(View.VISIBLE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mCountrySearchQuery.setVisibility(View.GONE);


    }

    List<Country> mCountry;

    @Override
    public void onCountriesLoaded(Response response) {
        try {
            if (TextUtils.isEmpty(mCountriesJsonString)) {
                mSharedPreferencesManager.setCountries(response.getBody());
                setCountriesAdapter(response.getBody());
            } else {
                if (!response.getBody().equalsIgnoreCase(mCountriesJsonString)) {
                    mSharedPreferencesManager.setCountries(response.getBody());
                    setCountriesAdapter(response.getBody());
                }
            }


            onCountriesPostLoad();
        } catch (Exception e) {
            e.printStackTrace();
            onCountriesLoadFailed(response.getResponseCode());
        }
    }

    private void setCountriesAdapter(String countriesJsonString) {
        Countries countries;
        Gson gson = new Gson();

        countries = gson.fromJson(countriesJsonString, Countries.class);
        mCountry = countries.getCountries();

        CountriesAdapter countriesAdapter = new CountriesAdapter(this, mCountry);
        mRecyclerView.swapAdapter(countriesAdapter, true);
    }

    @Override
    public void onCountriesLoadFailed(int responseCode) {
        if (responseCode == 204) {
            Toast.makeText(getActivity(), "Content not found", Toast.LENGTH_LONG).show();
        } else if (responseCode == 500) {
            Toast.makeText(getActivity(), "Internal Server Error", Toast.LENGTH_LONG).show();
        } else if (responseCode == 0) {

            couldNotLoadCountries();
        } else
            couldNotLoadCountries();

    }


    @Override
    public void couldNotLoadCountries() {
        mLoadingWheel.setVisibility(View.GONE);
        mNoInternetLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        noSearchResuls.setVisibility(View.GONE);
        mCountrySearchQuery.setVisibility(View.GONE);


    }

    @Override
    public void onCountriesPostLoad() {
        noSearchResuls.setVisibility(View.GONE);
        mLoadingWheel.setVisibility(View.GONE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mCountrySearchQuery.setVisibility(View.VISIBLE);
        mCountrySearchQuery.requestFocus();

        if (mCountrySearchQuery.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}
