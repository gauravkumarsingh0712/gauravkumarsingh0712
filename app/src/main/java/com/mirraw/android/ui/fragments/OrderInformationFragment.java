package com.mirraw.android.ui.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.OrderDetailsAsync;
import com.mirraw.android.models.orders.DesignerOrder;
import com.mirraw.android.models.orders.OrderDetails;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.OrderDetailsActivity;
import com.mirraw.android.ui.adapters.OrderInformationAdapter;
import com.mirraw.android.ui.widgets.RecyclerViewPauseOnScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderInformationFragment extends android.support.v4.app.Fragment implements
        OrderDetailsAsync.OrderDetailsLoader, OrderInformationAdapter.OrderInformationClickListener, RippleView.OnRippleCompleteListener {


    public OrderInformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_information, container, false);
        return view;
    }

    SharedPreferencesManager mSharedPreferencesManager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSharedPreferencesManager = new SharedPreferencesManager(getActivity());
        initViews(view);
        loadOrderDetails();
    }

    private void initViews(View view) {
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_order_information);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerViewPauseOnScrollListener(ImageLoader.getInstance(), false, true));
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel(View view) {
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
    }

    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetLL;

    private void initNoInternetView(View view) {
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainerOrderInformation);
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        ((RippleView)view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
    }

    OrderDetailsAsync mOrderDetailsAsync;

    @Override
    public void loadOrderDetails() {
        Bundle bundle = getArguments();
        String orderId = bundle.getString("orderId");

        mOrderDetailsAsync = new OrderDetailsAsync(this);

        String url = ApiUrls.API_CREATE_ORDER + "/" + orderId;

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        JSONObject head;

        try {
            head = new JSONObject(mSharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            requestHeaders.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            requestHeaders.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            requestHeaders.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            requestHeaders.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (Exception e) {

        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        mOrderDetailsAsync.execute(request);
    }

    OrderDetails mOrderDetails;
    OrderInformationAdapter mOrderInformationAdapter;

    @Override
    public void onOrderDetailsLoaded(Response response) {
        try {
            Gson gson = new Gson();
            if (response.getResponseCode() == 200) {
                mOrderDetails = gson.fromJson(response.getBody(), OrderDetails.class);
                mOrderInformationAdapter = new OrderInformationAdapter(mOrderDetails, this);
                mRecyclerView.setAdapter(mOrderInformationAdapter);
                mOrderInformationAdapter.notifyDataSetChanged();
                mProgressWheel.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Mint.logExceptionMessage(OrderInformationFragment.class.getSimpleName(), "OrderInformationFragment onOrderDetailsLoaded", e);
        }
    }

    @Override
    public void couldNotLoadOrderDetails(Response response) {
        int intCode = response.getResponseCode();
        if (intCode == 0) {
            onNoInternet();
        } else if (intCode == 204) {
            //Toast.makeText(getActivity(), "You haven't placed any order yet! :(", Toast.LENGTH_LONG).show();

            Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "No items in your order! :(", Snackbar.LENGTH_LONG)
                    .setAction("Close", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
            snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
            snackbar.show();

            mProgressWheel.setVisibility(View.GONE);
        } else if (intCode == 500) {
            Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
            onNoInternet();
        }
    }

    private void onNoInternet() {
        mConnectionContainer.setVisibility(View.VISIBLE);
        mNoInternetLL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
    }

    private void onRetryButtonClicked() {
        mNoInternetLL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.VISIBLE);
        loadOrderDetails();
    }


    @Override
    public void onOrderInformationClicked(View v, int position) {
        if (position > 0) {
            DesignerOrder designerOrder = mOrderDetails.getDesignerOrders().get(position - 1);
            Gson gson = new Gson();
            String strDesignerOrder = gson.toJson(designerOrder);
            String strOrderDetails = gson.toJson(mOrderDetails);
            Bundle bundle = new Bundle();
            bundle.putString("designerOrder", strDesignerOrder);
            bundle.putString("orderDetails", strOrderDetails);
            bundle.putInt("position", position - 1);
            Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
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
