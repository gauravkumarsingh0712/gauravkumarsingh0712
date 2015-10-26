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
import com.mirraw.android.models.searchResults.IdFilter;
import com.mirraw.android.models.searchResults.List;
import com.mirraw.android.models.searchResults.List_;
import com.mirraw.android.models.searchResults.RangeFilter;
import com.mirraw.android.ui.adapters.filters.IdFilterAdapter;
import com.mirraw.android.ui.adapters.filters.RangeFilterAdapter;

import java.util.ArrayList;

/**
 * Created by vihaan on 20/7/15.
 */
public class IdFilterFragment extends Fragment implements IdFilterAdapter.IdFilterListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_id_filter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view);
        setAdapter(getIdFilterString());
    }

    private RecyclerView mRecyclerView;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
    }

    private String getIdFilterString()
    {
        String filter;
        Bundle bundle = getArguments();
        filter = bundle.getString("idFilter");
        return filter;
    }


    private java.util.List<List_> mList;
    private IdFilter mIdFilter = null;
    private IdFilterAdapter mAdapter;
    private void setAdapter(String filter)
    {
        Gson gson = new Gson();
        mIdFilter = gson.fromJson(filter, IdFilter.class);

        mList = mIdFilter.getList();
        mAdapter = new IdFilterAdapter(this, mList,getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    public IdFilter getIdFilter()
    {
        return mIdFilter;
    }

    private java.util.List<List_> mSelectedList = new ArrayList<List_>();

    @Override
    public void onIdFilterClicked(View view, int position) {

        List_ list = mList.get(position);

        if(mSelectedList.contains(list))
        {
            mSelectedList.remove(list);
        }
        else
        {
            mSelectedList.add(list);
        }
    }

    public java.util.List<List_>  getSelectedFilters()
    {
        return mSelectedList;
    }

    public void clearFilters()
    {
        if(mSelectedList != null)
        {
            for (int i =0; i < mSelectedList.size(); i++)
            {
                List_ selectedList = mSelectedList.get(i);
                if(mSelectedList.contains(selectedList))
                {
                   int position =  mSelectedList.indexOf(selectedList);
                    List_ list = mSelectedList.get(position);
                    list.setSelected(false);
                }
            }

            if(mAdapter != null)
            {
                mAdapter.notifyDataSetChanged();
            }

            mSelectedList.clear();
        }
    }
}
