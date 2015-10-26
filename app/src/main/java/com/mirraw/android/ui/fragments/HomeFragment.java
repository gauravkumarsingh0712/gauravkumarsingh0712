package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.mirraw.android.async.GetBlocksAsync;
import com.mirraw.android.models.Block;
import com.mirraw.android.models.BlocksList;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.ProductListingActivity;
import com.mirraw.android.ui.adapters.BlocksAdapter;
import com.mirraw.android.ui.widgets.RecyclerViewPauseOnScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by vihaan on 1/7/15.
 */
public class HomeFragment extends Fragment
        implements
        GetBlocksAsync.BlocksLoader,
        BlocksAdapter.BlockClickListener, RippleView.OnRippleCompleteListener {



    public interface FragmentLoader {
        public void onFragmentLoaded();
    }

    private FragmentLoader mFragmentLoader;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentLoader = (FragmentLoader) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadBlocks();
    }

    private void initViews(View view) {
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel(View view) {
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
    }


    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerViewPauseOnScrollListener(ImageLoader.getInstance(), false, true));
//        mRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
//        mRecyclerView.setHasFixedSize(true);
//
//        final GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
//        mRecyclerView.setLayoutManager(manager);
//        mRecyclerView.setVisibility(View.GONE);

    }


    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetLL;

    private void initNoInternetView(View view) {
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainer);
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        ((RippleView) view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
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
        loadBlocks();
    }

    private GetBlocksAsync mGetBlocksAscyn = null;

    @Override
    public void loadBlocks() {
        mGetBlocksAscyn = new GetBlocksAsync(this);

        String url = ApiUrls.API_BLOCKS;

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        mGetBlocksAscyn.executeTask(request);
    }

    private List<Block> mBlocks;

    @Override
    public void onBlocksLoaded(Response response) {
        Logger.v("Response code:", "Response Code: " + response.getResponseCode() + response.getBody());
        try {

            Gson gson = new Gson();

            if (response.getResponseCode() == 200) {
                BlocksList blocksList = gson.fromJson(response.getBody(), BlocksList.class);
                mBlocks = blocksList.getBlocks();
                BlocksAdapter blocksAdapter = new BlocksAdapter(mBlocks, this);
                mRecyclerView.setAdapter(blocksAdapter);
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressWheel.setVisibility(View.GONE);
                mConnectionContainer.setVisibility(View.GONE);
                mFragmentLoader.onFragmentLoaded();
            } else {
                onBlocksLoadFailed(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            couldNotLoadBlocks();
        }


    }


    @Override
    public void onBlocksLoadFailed(Response response) {
        Logger.v("Response code:", "Response Code: " + response.getResponseCode() + response.getBody());
        if (response.getResponseCode() == 0) {
            onNoInternet();
        } else {
            couldNotLoadBlocks();
        }
    }

    @Override
    public void couldNotLoadBlocks() {
        Toast.makeText(getActivity(), "Problem loading data", Toast.LENGTH_LONG).show();
        onNoInternet();
    }

    @Override
    public void onDestroy() {
        mGetBlocksAscyn.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onBlockClicked(View view, int position) {
        Block block = mBlocks.get(position);
        Gson gson = new Gson();
        Bundle bundle = new Bundle();

        bundle.putString("title", block.getName());
        bundle.putString("key", block.getKey());
        bundle.putString("value", block.getValue());
        Intent intent = new Intent(getActivity(), ProductListingActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
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
