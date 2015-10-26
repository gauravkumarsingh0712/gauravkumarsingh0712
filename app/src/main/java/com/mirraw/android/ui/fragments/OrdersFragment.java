package com.mirraw.android.ui.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.OrderTrackingAsync;
import com.mirraw.android.models.orders.Orders;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.OrderInformationActivity;
import com.mirraw.android.ui.adapters.OrdersAdapter;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends android.support.v4.app.Fragment implements
        OrderTrackingAsync.OrdersLoader, OrdersAdapter.OrdersClickListener, RippleView.OnRippleCompleteListener {


    SharedPreferencesManager mSharedPreferencesManager;
    String TAG = OrdersFragment.class.getSimpleName();

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_tracking, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        loadOrders();
    }

    private void initViews(View view) {
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
    }

    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetLL;

    private void initNoInternetView(View view) {
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainerOrders);
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        ((RippleView) view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel(View view) {
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    int totalItemCount;
    int lastVisibleItem;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_orders);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                totalItemCount = mLinearLayoutManager.getChildCount();
                totalItemCount = mOrdersAdapter.getItemCount();
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();

                if (lastVisibleItem == totalItemCount - 1) {

                    if (pageCounter <= mTotalPages) {
                        Logger.d(TAG, "pagecounter<=totalItemCount: " + pageCounter + " " + totalItemCount);
                        loadOrders();
                    } else {
                        Logger.d(TAG, "Last page done");
                        mOrdersAdapter.lastPage();
                    }
                }
            }
        });
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
        loadOrders();
    }


    OrderTrackingAsync mOrderTrackingAsync = null;
    int pageCounter = 1;

    @Override
    public void loadOrders() {
//        if (mOrderTrackingAsync != null && mOrderTrackingAsync.getStatus() != AsyncTask.Status.RUNNING){
        if (mOrderTrackingAsync != null && mOrderTrackingAsync.getStatus() == AsyncTask.Status.RUNNING) {

        } else {
            mOrderTrackingAsync = new OrderTrackingAsync(this);
            String url = ApiUrls.API_CREATE_ORDER + "?page=" + pageCounter;

            HashMap<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            requestHeaders.put(Headers.TOKEN, getString(R.string.token));

            JSONObject head;
            mSharedPreferencesManager = new SharedPreferencesManager(Mirraw.getAppContext());
            System.out.println("login data : " + mSharedPreferencesManager.getLoginResponse());
            try {
                head = new JSONObject(mSharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
                requestHeaders.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
                requestHeaders.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
                requestHeaders.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
                requestHeaders.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                    .setHeaders(requestHeaders)
                    .build();

            mOrderTrackingAsync.execute(request);
        }
    }

    Orders mOrders;
    Orders newPageOrders;
    OrdersAdapter mOrdersAdapter;
    int mTotalPages;

    @Override
    public void onOrdersLoaded(Response response) {
        String strResponse = response.getBody();
        System.out.println("responcre code  order : " + response.getResponseCode());
        try {
            Gson gson = new Gson();
            if (response.getResponseCode() == 200) {
                if (pageCounter == 1) {
                    mOrders = gson.fromJson(strResponse, Orders.class);
                    mOrdersAdapter = new OrdersAdapter(mOrders, this);
                    mRecyclerView.swapAdapter(mOrdersAdapter, false);
                    mOrdersAdapter.notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressWheel.setVisibility(View.GONE);
                    mTotalPages = mOrders.getTotalPages();
                } else {
                    newPageOrders = gson.fromJson(strResponse, Orders.class);
                    mOrders.getOrders().addAll(newPageOrders.getOrders());
                    mOrdersAdapter = new OrdersAdapter(mOrders, this);
                    mRecyclerView.swapAdapter(mOrdersAdapter, false);
                    mOrdersAdapter.notifyDataSetChanged();
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mProgressWheel.setVisibility(View.GONE);

                }

                if (pageCounter == mTotalPages) {
                    mOrdersAdapter.lastPage();
                }

                setNextPage();
                Logger.d(TAG, "Page Counter: " + pageCounter);
            } else {
                onOrderLoadingFailed(response);
            }

        } catch (Exception e) {
            onOrderLoadingFailed(response);
        }
    }

    private void setNextPage() {
        pageCounter++;
    }

    @Override
    public void onOrderLoadingFailed(Response response) {
        int intCode = response.getResponseCode();
        if (pageCounter == 1) {
            if (intCode == 0) {
                onNoInternet();
            } else if (intCode == 204) {
                //Toast.makeText(getActivity(), "You haven't placed any order yet! :(", Toast.LENGTH_LONG).show();
                Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "You haven't placed any order yet! :(", Snackbar.LENGTH_INDEFINITE)
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
        } else {
            if (intCode == 0) {
                onNoInternetBottom();
            } else if (intCode == 204) {
                //Toast.makeText(getActivity(), "You haven't placed any order yet! :(", Toast.LENGTH_LONG).show();
                couldNotLoadProductListingBottom();
            } else if (intCode == 500) {
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
                onNoInternetBottom();
            }
        }

    }

    public void onNoInternetBottom() {
        mOrdersAdapter.hideProgress();
    }

    public void couldNotLoadProductListingBottom() {
        mOrdersAdapter.hideProgress();
        Toast.makeText(getActivity(), "Problem loading Orders", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOrderTrackingAsync != null) {
            mOrderTrackingAsync.cancel(true);
        }

    }

    @Override
    public void onOrdersClicked(View v, int position) {
        Bundle bundle = new Bundle();
        int id = mOrders.getOrders().get(position).getId();
        bundle.putString("orderId", String.valueOf(id));

        Intent intent = new Intent(getActivity(), OrderInformationActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRetryButtonClickedBottom() {
        try {
            mOrdersAdapter.showProgress();
            loadOrders();
        } catch (Exception e) {
            Mint.logExceptionMessage(TAG, "onRetryButtonBottom pageCoutner " + pageCounter, e);
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
