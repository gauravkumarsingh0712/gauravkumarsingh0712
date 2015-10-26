package com.mirraw.android.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.menus.Menu;
import com.mirraw.android.models.menus.MenuColumn;
import com.mirraw.android.ui.activities.ProductListingActivity;
import com.mirraw.android.ui.adapters.SingleCategoryMenuAdapter;

import java.lang.reflect.Field;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleCategoryMenuFragment extends android.support.v4.app.Fragment implements View.OnClickListener, SingleCategoryMenuAdapter.SingleCategoryMenuClickListener {

    public static final String sTag = SingleCategoryMenuFragment.class.getSimpleName();
    private static DrawerLayout mDrawerLayout;
    private TextView mMenuNameTextView;
    private RecyclerView mSinglecategoryRecyclerView;
    private List<MenuColumn> mColumns;
    private String mMenuName;

    public SingleCategoryMenuFragment() {
        // Required empty public constructor
    }

    public static SingleCategoryMenuFragment newInstance(DrawerLayout drawerLayout, String columns) {

        mDrawerLayout = drawerLayout;
        Bundle bundle = new Bundle();

        bundle.putString("menu", columns);

        SingleCategoryMenuFragment fragment = new SingleCategoryMenuFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String menuString = bundle.getString("menu");
            Gson gson = new Gson();
            Menu menu = gson.fromJson(menuString, Menu.class);
            mMenuName = menu.getTitle();
            mColumns = menu.getMenuColumns();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_category_menu, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setMenuName(mMenuName);
        populateRecyclerView();
    }

    private void initViews(View view) {
        mMenuNameTextView = (TextView) view.findViewById(R.id.menuNameTextView);
        mSinglecategoryRecyclerView = (RecyclerView) view.findViewById(R.id.singleCategoryRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mSinglecategoryRecyclerView.setLayoutManager(linearLayoutManager);
    }

    private void setMenuName(String mMenuName) {
        mMenuNameTextView.setText(mMenuName);
        mMenuNameTextView.setOnClickListener(this);
    }

    private void populateRecyclerView() {
        SingleCategoryMenuAdapter singleCategoryMenuAdapter = new SingleCategoryMenuAdapter(mColumns.get(0), this);
        singleCategoryMenuAdapter.setColumns(mColumns.get(0));
        mSinglecategoryRecyclerView.setAdapter(singleCategoryMenuAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menuNameTextView:
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = android.support.v4.app.Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {

        }
    }

    @Override
    public void onMenuItemClicked(String strTitle, String strKey, String strValue) {
        Intent intent = new Intent(getActivity(), ProductListingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("title", strTitle);
        bundle.putString("key", strKey);
        bundle.putString("value", strValue);

        intent.putExtras(bundle);
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawers();
        }

        startActivity(intent);
    }
}
