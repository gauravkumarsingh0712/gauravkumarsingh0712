package com.mirraw.android.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;


import com.mirraw.android.async.GetStatesListAsync;
import com.mirraw.android.models.address.State;
import com.mirraw.android.models.address.States;
import com.mirraw.android.network.ApiUrls;

import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.ui.adapters.StatesAdapter;
import com.pnikosis.materialishprogress.ProgressWheel;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nimesh Luhana on 23-07-2015.
 */

public class SelectStateFragment extends Fragment implements TextWatcher, GetStatesListAsync.StatesLoader,
        StatesAdapter.StateClickListener, RippleView.OnRippleCompleteListener {
    EditText mStateSearchQuery;
    LinearLayout mNoInternetLayout;
    RippleView mRetryButtonRippleContainer;
    RecyclerView mRecyclerView;
    ProgressWheel mLoadingWheel;
    StateSelectListener mStateSelectListener;
    int mCountryId;

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.retry_button_ripple_container:

                if (mStatesAsync != null)
                    mStatesAsync.cancel(true);
                loadStates();
                loadData();
                break;
        }

    }


    public interface StateSelectListener {
        void onStateClicked(State state);
    }


    public static SelectStateFragment newInstance(int countryId) {
        SelectStateFragment selectStateFragment = new SelectStateFragment();
        Bundle b = new Bundle();
        b.putInt("countryId", countryId);
        selectStateFragment.setArguments(b);
        return selectStateFragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        if (b != null) {

            mCountryId = b.getInt("countryId");

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mStateSelectListener = (StateSelectListener) activity;

        /*mStateSelectListener = (StateSelectListener) activity;*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_select_state, container, false);
        return v;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initViews(view);
        onStatesPreload();
        loadData();

    }

    GetStatesListAsync mStatesAsync;

    void loadData() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        Request request = new Request.RequestBuilder(ApiUrls.API_COUNTRIES + "/" + mCountryId + "/states", Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
        mStatesAsync = new GetStatesListAsync(this, getActivity());

        mStatesAsync.execute(request);


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
        mStateSearchQuery = (EditText) view.findViewById(R.id.stateSearchQuery);
        mStateSearchQuery.addTextChangedListener(this);
    }

    private void initLoadingWheel(View view) {
        mLoadingWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);

    }

    @Override
    public void onDestroy() {

        mStatesAsync.cancel(true);
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


            List<State> mNewStateList;

            mNewStateList = new ArrayList<State>();
            for (int i = 0; i < mState.size(); i++) {
                if (mState.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {

                    mNewStateList.add(mState.get(i));

                }


            }
            if (mNewStateList.size() == 0) {
                noSearchResuls();


            } else {
                StatesAdapter statesAdapter = new StatesAdapter(this, mNewStateList);

                mRecyclerView.swapAdapter(statesAdapter, false);
                mGridLayoutManager.scrollToPosition(0);
                onStatesPostLoad();
            }
        } else {
            StatesAdapter statesAdapter = new StatesAdapter(this, mState);
            mRecyclerView.swapAdapter(statesAdapter, false);
            onStatesPostLoad();
        }


    }

    private void noSearchResuls() {
        noSearchResuls.setVisibility(View.VISIBLE);
        mLoadingWheel.setVisibility(View.GONE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);


    }

    @Override
    public void onStateClicked(View v, int id) {
        State state = null;
        String code = null;
        String name = null;
        Intent i = new Intent();
        for (int j = 0; j < mState.size(); j++) {
            state = mState.get(j);
            if (state.getId() == id) {
                code = state.getCode();
                name = state.getName();
                break;
            }


        }

        mStateSelectListener.onStateClicked(state);


    }


    @Override
    public void onStatesPreload() {
        noSearchResuls.setVisibility(View.GONE);
        mLoadingWheel.setVisibility(View.VISIBLE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mStateSearchQuery.setVisibility(View.GONE);

    }

    @Override
    public void loadStates() {
        noSearchResuls.setVisibility(View.GONE);
        mLoadingWheel.setVisibility(View.VISIBLE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mStateSearchQuery.setVisibility(View.GONE);


    }

    List<State> mState;

    @Override
    public void onStatesLoaded(String response) {
        States states;

        Gson gson = new Gson();
        states = gson.fromJson(response, States.class);
        mState = states.getStates();
        StatesAdapter statesAdapter = new StatesAdapter(this, mState);

        mRecyclerView.setAdapter(statesAdapter);

        onStatesPostLoad();
    }

    @Override
    public void onStatesLoadFailed(int responseCode) {

        if (responseCode == 0)  //not connected to the internet
        {

            couldNotLoadStates();

        } else if (responseCode == 204) {
            Toast.makeText(getActivity(), "Content not found", Toast.LENGTH_LONG).show();
        } else if (responseCode == 500) {
            Toast.makeText(getActivity(), "Internal Server Error", Toast.LENGTH_LONG).show();
        } else
            couldNotLoadStates();

    }


    @Override
    public void couldNotLoadStates() {
        mLoadingWheel.setVisibility(View.GONE);
        mNoInternetLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        noSearchResuls.setVisibility(View.GONE);
        mStateSearchQuery.setVisibility(View.GONE);


    }

    @Override
    public void onStatesPostLoad() {
        noSearchResuls.setVisibility(View.GONE);
        mLoadingWheel.setVisibility(View.GONE);
        mNoInternetLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mStateSearchQuery.setVisibility(View.VISIBLE);
        mStateSearchQuery.requestFocus();

        if (mStateSearchQuery.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


}
