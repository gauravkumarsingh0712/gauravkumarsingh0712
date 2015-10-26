package com.mirraw.android.ui.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.interfaces.SortFilterChangeListener;
import com.mirraw.android.ui.fragments.SearchResultsFragment;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by Nimesh Luhana on 08-07-2015.
 */

public class SearchResultsActivity extends AnimationActivity
        implements SearchView.OnQueryTextListener,
        View.OnClickListener,
        SortFilterChangeListener,
RippleView.OnRippleCompleteListener{

    private SearchView mSearchView;
    String mSearchTerm;
    private ProductListingActivity.SortFilterClickListener mSortFilterClickListener;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlack);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);


        actionBar.setDisplayHomeAsUpEnabled(true);
        showFragment();*/
        initToolbar();
        showFragment();
        initFilterAndSort();

    }

    private void initToolbar() {
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.requestFocus();
        mSearchView.setFocusable(false);
        mSearchView.setOnQueryTextListener(this);*/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    TextView mFilter, mSort;
    private RelativeLayout mFilterSortButtonLayout;
    ImageView filterImageView, sortImageView;
    private LinearLayout sortLinearLayout, filterLinearLayout;
    private RippleView mSortRippleView, mFilterRippleView;

    private void initFilterAndSort() {
        mFilterSortButtonLayout = (RelativeLayout) findViewById(R.id.sortFilterSearchRLL);
        mSortRippleView = (RippleView) findViewById(R.id.main_sort_layout);
        mFilterRippleView = (RippleView) findViewById(R.id.main_filter_layout);
        mFilter = (TextView) findViewById(R.id.filterButton);
        mSort = (TextView) findViewById(R.id.sortButton);
        filterImageView = (ImageView) findViewById(R.id.filter_imageview);
        sortImageView = (ImageView) findViewById(R.id.sort_imageview);
        mSortRippleView.setOnRippleCompleteListener(this);
        mFilterRippleView.setOnRippleCompleteListener(this);
        mFilterSortButtonLayout.setVisibility(View.GONE);
    }


    @Override
    public void onComplete(RippleView rippleView) {
        //super.onComplete(rippleView);

        switch (rippleView.getId()) {
            case R.id.main_sort_layout:
                mSortFilterClickListener.onSortClicked();
                break;
            case R.id.main_filter_layout:
                mSortFilterClickListener.onFilterClicked();
                break;
        }
    }


    private void showFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new SearchResultsFragment();
        ft.replace(R.id.container, fragment);
        ft.commit();
        mSortFilterClickListener = (ProductListingActivity.SortFilterClickListener) fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_results_activity_search_item, menu);
        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.requestFocus();
        mSearchView.setFocusable(false);

        mSearchView.setQueryHint("Search here...");
        mSearchView.setOnQueryTextListener(this);

        return true;


    }

    @Override
    public boolean onQueryTextChange(String search_term) {
        mSearchTerm = search_term;
        Intent i = new Intent("app_search");
        i.putExtra("searchTerm", search_term);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

        return false;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id) {
            case R.id.main_sort_layout:
                mSortFilterClickListener.onSortClicked();
                break;
            case R.id.main_filter_layout:
                mSortFilterClickListener.onFilterClicked();
                break;
        }
    }

    @Override
    public void updateSortView(int mSortId) {

        if (mSortId != 0) {
            sortImageView.getDrawable().setColorFilter(getResources().getColor(R.color.green_color), PorterDuff.Mode.MULTIPLY);
            mSort.setTextColor(getResources().getColor(R.color.green_color));
        } else {
            sortImageView.getDrawable().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
            mSort.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public void updateFilterView(String mFilterParams) {
        if (!mFilterParams.equalsIgnoreCase("")) {
            filterImageView.getDrawable().setColorFilter(getResources().getColor(R.color.green_color), PorterDuff.Mode.MULTIPLY);
            mFilter.setTextColor(getResources().getColor(R.color.green_color));
        } else {
            filterImageView.getDrawable().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
            mFilter.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    @Override
    public void showSortFilterView() {
        mFilterSortButtonLayout.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mFilterSortButtonLayout, "alpha", 0, 0.25f, 0.5f, 0.75f, 1).setDuration(500).start();
    }

    @Override
    public void hideSortFilterView() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mFilterSortButtonLayout, "alpha", 1, 0.75f, 0.5f, 0.25f, 0);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFilterSortButtonLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.setDuration(200).start();
    }

}
