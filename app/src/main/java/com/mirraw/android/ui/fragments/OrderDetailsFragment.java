package com.mirraw.android.ui.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.orders.DesignerOrder;
import com.mirraw.android.models.orders.OrderDetails;
import com.mirraw.android.ui.adapters.OrderDetailsAdapter;
import com.mirraw.android.ui.widgets.RecyclerViewPauseOnScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderDetailsFragment extends android.support.v4.app.Fragment {

    public OrderDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_details, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setRecyclerViewContent();
    }


    private void initViews(View view) {
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
    }

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_order_details);
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
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainerOrderDetails);
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
    }

    OrderDetails mOrderDetails;
    DesignerOrder mDesignerOrder;
    OrderDetailsAdapter mOrderDetailsAdapter;
    final static String TAG = OrderDetailsFragment.class.getSimpleName();

    private void setRecyclerViewContent() {
        try {
            Bundle bundle = getArguments();
            String strDesignerOrder = bundle.getString("designerOrder");
            String strOrderDetails = bundle.getString("orderDetails");
            int position = bundle.getInt("position");
            Gson gson = new Gson();
            mDesignerOrder = gson.fromJson(strDesignerOrder, DesignerOrder.class);
            mOrderDetails = gson.fromJson(strOrderDetails, OrderDetails.class);
            mOrderDetailsAdapter = new OrderDetailsAdapter(mOrderDetails, position);
            mRecyclerView.setAdapter(mOrderDetailsAdapter);
            mOrderDetailsAdapter.notifyDataSetChanged();
            mProgressWheel.setVisibility(View.GONE);
            String strDesigner = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(0).getDesignerName();
            getActivity().setTitle(strDesigner);
        } catch (Exception e) {
            Logger.wtf(TAG, e.getMessage());
            Mint.logExceptionMessage(TAG, "setRecyclerViewContent", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}