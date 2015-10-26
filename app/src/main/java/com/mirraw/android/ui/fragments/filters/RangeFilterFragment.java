package com.mirraw.android.ui.fragments.filters;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.searchResults.List;
import com.mirraw.android.models.searchResults.RangeFilter;
import com.mirraw.android.ui.adapters.filters.RangeFilterAdapter;

/**
 * Created by vihaan on 20/7/15.
 */
public class RangeFilterFragment extends Fragment implements RangeFilterAdapter.RangeFilterListener{

    private  String mHexValue ;
    public static final String KEY_HEX_VALUE = "KEY_HEX_VALUE";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_range_filter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        setAdapter(getRangeFilterString());
    }

    private RecyclerView mRecyclerView;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
    }

    private String getRangeFilterString()
    {
        String filter;
        Bundle bundle = getArguments();
        filter = bundle.getString("rangeFilter");
        mHexValue = bundle.getString(KEY_HEX_VALUE);

        return filter;
    }

    public RangeFilter getRangeFilter()
    {
        return mRangeFilter;
    }

    private java.util.List<List> mList;
    private RangeFilter mRangeFilter;
    private RangeFilterAdapter mAdapter;
    private void setAdapter(String filter)
    {
        Gson gson = new Gson();
        mRangeFilter = gson.fromJson(filter, RangeFilter.class);

        mList = mRangeFilter.getList();
        String title = mRangeFilter.getName();
        mAdapter = new RangeFilterAdapter(this, mList,getActivity(), mHexValue,title);
        mRecyclerView.setAdapter(mAdapter);
    }



    private List mSelectedList;
    private int mSelectedItemPosition = -1;

    @Override
    public void onRangeFilterClicked(View view, int position) {
       mSelectedList = mList.get(position);
        mSelectedItemPosition = position;
    }

    public List getSelectedFilter()
    {
        return mSelectedList;
    }

    public void clearFilter()
    {
        if(mSelectedItemPosition != -1)
        {
            List list = mList.get(mSelectedItemPosition);
            list.setSelected(false);
            mAdapter.resetSelectedItem();
            mAdapter.notifyDataSetChanged();
        }
        mSelectedList = null;
    }

    public String getSelectedFilterString()
    {
        String filter = "";
        if(mSelectedList != null)
        {
            StringBuilder filterBuilder = new StringBuilder();
            filter =  filterBuilder
                    .append("&")
                    .append(mRangeFilter.getKeys().getMin())
                    .append("=")
                    .append(mSelectedList.getValues().getMin())
                    .append("&")
                    .append(mRangeFilter.getKeys().getMax())
                    .append("=")
                    .append(mSelectedList.getValues().getMax()).toString();
        }

        return filter;
    }
}
