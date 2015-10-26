package com.mirraw.android.ui.activities;

//import android.content.SharedPreferences;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.andexert.library.RippleView;
import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.ListAddressAsync;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.models.address.ListOfAddress;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.MarginDecoration;
import com.mirraw.android.ui.adapters.ViewAddressAdapter;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

//import android.preference.PreferenceManager;

/**
 * Created by pavitra on 23/7/15.
 */
public class ViewAddressActivity extends AnimationActivity implements
        ListAddressAsync.AddressLoader,
        RippleView.OnRippleCompleteListener {
    private RecyclerView mAddressListView;
    private SharedPreferencesManager mAppSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.address_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getData();
        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAppSharedPrefs = new SharedPreferencesManager(this);
        loadAddressList();
    }

    private ProgressWheel mProgressWheel;
    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetLL;

    private void initViews() {
        mAddressListView = (RecyclerView) findViewById(R.id.recycler_view);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        mConnectionContainer = (RelativeLayout) findViewById(R.id.connectionContainer);
        mNoInternetLL = (LinearLayout) findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        ((RippleView) findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);

        mAddressListView.addItemDecoration(new MarginDecoration(this));
        mAddressListView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        mAddressListView.setLayoutManager(manager);
    }

    private ListAddressAsync mGetAddressAsync;

    @Override
    public void loadAddressList() {
        String url = ApiUrls.API_ADD_ADDRESS;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
        mGetAddressAsync = new ListAddressAsync(this, this);
        mGetAddressAsync.executeTask(request);
    }

    @Override
    public void loadAddressFailed(Response response) {
        onNoInternet();
        if (response.getResponseCode() == 0) {
            Utils.showSnackBar(getString(R.string.no_internet), this, Snackbar.LENGTH_LONG);
        } else {
            Utils.showSnackBar(getString(R.string.problem_loading_data), this, Snackbar.LENGTH_LONG);
        }
    }

    public void onNoInternet() {
        mConnectionContainer.setVisibility(View.VISIBLE);
        mNoInternetLL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
        mAddressListView.setVisibility(View.GONE);
    }

    @Override
    public void loadAddressSuccess(Response response) {
        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            mAddressListView.setVisibility(View.VISIBLE);
            mProgressWheel.setVisibility(View.GONE);
            mConnectionContainer.setVisibility(View.GONE);
            //getData(response.getBody());
            Logger.v("", "Pavi Address: ViewAddressActivity " + mAppSharedPrefs.getAddresses());
            getData(mAppSharedPrefs.getAddresses());
        } else {
            loadAddressFailed(response);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ViewAddressAdapter mViewAddressAdapter;

    private void getData(String addresses) {
        Gson gson = new Gson();
        //String addressJson = getIntent().getStringExtra("getAddress");
        String addressJson = addresses;
        ListOfAddress mAddressArrayObject = gson.fromJson(addressJson, ListOfAddress.class);
        ArrayList<Address> addressArray = mAddressArrayObject.getAddresses();
        System.out.println("Total names" + mAddressArrayObject.getAddresses());

        int defaultAddressPosition = -1;
        int shipAddressPosition = -1;


        for (int i = 0; i < addressArray.size(); i++) {
            /*TODO: temporary try catch block*/
            try {
                if (addressArray.get(i).getDefault() == 1) {
                    defaultAddressPosition = i;
                    break;
                }
            } catch (NullPointerException defaultNull) {
                //addressArray.get(i).setDefaultAddressStatus(true);
            }
            /*if (addressArray.get(i).getDefault() == 1) {
                defaultAddressPosition = i;
                break;
            }*/
        }

        if (defaultAddressPosition == -1) {
            defaultAddressPosition = 0;
            addressArray.get(defaultAddressPosition).setDefault(1);
        }

        for (int i = 0; i < addressArray.size(); i++) {
            if (addressArray.get(i).getShipAddressStatus()) {
                shipAddressPosition = i;
                break;
            }
        }

        if (shipAddressPosition == -1) {
            shipAddressPosition = defaultAddressPosition;
        }

        mViewAddressAdapter = new ViewAddressAdapter(this, this, addressArray, defaultAddressPosition, shipAddressPosition);
        mAddressListView.setAdapter(mViewAddressAdapter);
    }


    @Override
    protected void onDestroy() {
        if (mGetAddressAsync != null) {
            mGetAddressAsync.cancel(true);
        }
        if (mViewAddressAdapter != null) {
            mViewAddressAdapter.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {


            case R.id.retry_button_ripple_container:
                loadAddressList();
                break;
        }
    }
}
