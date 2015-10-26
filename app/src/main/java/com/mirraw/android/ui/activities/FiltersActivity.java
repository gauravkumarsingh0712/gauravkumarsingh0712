package com.mirraw.android.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mirraw.android.R;
import com.mirraw.android.models.searchResults.Filters;
import com.mirraw.android.models.searchResults.IdFilter;
import com.mirraw.android.models.searchResults.List_;
import com.mirraw.android.models.searchResults.RangeFilter;
import com.mirraw.android.ui.fragments.filters.IdFilterFragment;
import com.mirraw.android.ui.fragments.filters.RangeFilterFragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vihaan on 17/7/15.
 */
public class FiltersActivity extends AppCompatActivity implements RippleView.OnRippleCompleteListener {

    private Filters mFilters;
    private ArrayList<String> mFilterTitles;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private List<Fragment> mRangeFilterFragments;
    private List<Fragment> mIdFilterFragments;
    private String mHexValue = "";


    public static final int FILTER_REQ_CODE = 1001;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Bundle bundle = getIntent().getExtras();
        String filters = bundle.getString("filters");
        mHexValue = bundle.getString(RangeFilterFragment.KEY_HEX_VALUE);
        initViews();
        setupFilters(filters, mViewPager);
    }

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);

        ((RippleView) findViewById(R.id.applyButton)).setOnRippleCompleteListener(this);
        ((RippleView) findViewById(R.id.clearButton)).setOnRippleCompleteListener(this);
    }

    private void setupFilters(String filters, ViewPager viewPager) {

//        setSelectedFilters();
        Gson gson = new Gson();

        mFilters = gson.fromJson(filters, Filters.class);

        mFilterTitles = getFilterTitles(mFilters);
        mRangeFilterFragments = getRangeFilterFragments(mFilters.getRangeFilters());
        mIdFilterFragments = getIdFilterFragments(mFilters.getIdFilters());
        mFragments.addAll(mRangeFilterFragments);
        mFragments.addAll(mIdFilterFragments);

        setupViewPager(viewPager, mFragments, mFilterTitles);
    }

//    private void setSelectedFilters()
//    {
//        Bundle bundle = getIntent().getExtras();
//        String selectedRangeFilters = bundle.getString("selectedRangeFilters");
//        String selectedIdFilters = bundle.getString("selectedIdFilters");
//
//        setSelectedRangeFiltersFilters(selectedRangeFilters);
//        setSelectedIdFilters(selectedIdFilters);
//    }


    private List<Fragment> getIdFilterFragments(List<IdFilter> idFilters) {
        List<Fragment> fragments = new ArrayList<Fragment>();

        Gson gson = new Gson();
        String data;
        for (int i = 0; i < idFilters.size(); i++) {

            IdFilter idFilter = idFilters.get(i);
            data = gson.toJson(idFilter);

            Bundle bundle = new Bundle();
            bundle.putString("idFilter", data);

            IdFilterFragment idFilterFragment = new IdFilterFragment();
            idFilterFragment.setArguments(bundle);

            fragments.add(idFilterFragment);
        }

        return fragments;
    }

    private List<Fragment> getRangeFilterFragments(List<RangeFilter> rangeFilters) {
        List<Fragment> fragments = new ArrayList<Fragment>();

        Gson gson = new Gson();
        String data;
        for (int i = 0; i < rangeFilters.size(); i++) {

            RangeFilter rangeFilter = rangeFilters.get(i);
            data = gson.toJson(rangeFilter);

            Bundle bundle = new Bundle();
            bundle.putString("rangeFilter", data);
            bundle.putString(RangeFilterFragment.KEY_HEX_VALUE, mHexValue);

            RangeFilterFragment rangeFilterFragment = new RangeFilterFragment();
            rangeFilterFragment.setArguments(bundle);

            fragments.add(rangeFilterFragment);
        }

        return fragments;
    }

    private ArrayList<String> getFilterTitles(Filters filters) {
        ArrayList<String> filterTitles = new ArrayList<String>();

        List<RangeFilter> rangeFilters = filters.getRangeFilters();

        if (rangeFilters != null) {
            for (int i = 0; i < rangeFilters.size(); i++) {
                filterTitles.add(rangeFilters.get(i).getName());
            }
        }

        List<IdFilter> idFilters = filters.getIdFilters();
        if (idFilters != null) {
            for (int i = 0; i < idFilters.size(); i++) {
                filterTitles.add(idFilters.get(i).getName());
            }
        }
        return filterTitles;
    }

    private void setupViewPager(ViewPager viewPager, List<Fragment> fragments, List<String> titles) {
        Adapter adapter = new Adapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void applyFilter() {
        String filterQueryParams = getSelectedFilterQueryParams();
        Intent intent = new Intent();
        intent.putExtra("filterQueryParams", filterQueryParams);
//        intent.putExtra("selectedRangeFilters", getSelectedRangeFilters());
//        intent.putExtra("selectedIdFilters", getSelectedIdFilters());
        intent.putExtra("filters", getFilters());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void clearFilters() {
        clearRangeFilters();
        clearIdFilters();
    }

    private void clearRangeFilters() {
        RangeFilterFragment fragment;
        for (int i = 0; i < mRangeFilterFragments.size(); i++) {
            fragment = (RangeFilterFragment) mRangeFilterFragments.get(i);
            fragment.clearFilter();
        }
    }

    private void clearIdFilters() {
        IdFilterFragment fragment;
        for (int i = 0; i < mIdFilterFragments.size(); i++) {
            fragment = (IdFilterFragment) mIdFilterFragments.get(i);
            fragment.clearFilters();
        }
    }

    private String getFilters() {
        String filtersString = "";
        Gson gson = new Gson();

        Filters filters = new Filters();
        filters.setRangeFilters(getRangeFilters());
        filters.setIdFilters(getIdFilters());

        Type type = new TypeToken<Filters>() {
        }.getType();

        filtersString = gson.toJson(filters, type);
        return filtersString;
    }

    private List<RangeFilter> getRangeFilters() {
        List<RangeFilter> rangeFilters = new ArrayList<RangeFilter>();
        RangeFilterFragment rangeFilterFragment;
        for (int i = 0; i < mRangeFilterFragments.size(); i++) {
            rangeFilterFragment = (RangeFilterFragment) mRangeFilterFragments.get(i);
            rangeFilters.add(rangeFilterFragment.getRangeFilter());
        }

        return rangeFilters;
    }

    private List<IdFilter> getIdFilters() {
        List<IdFilter> idFilters = new ArrayList<IdFilter>();
        IdFilterFragment idFilterFragment;
        for (int i = 0; i < mIdFilterFragments.size(); i++) {
            idFilterFragment = (IdFilterFragment) mIdFilterFragments.get(i);
            idFilters.add(idFilterFragment.getIdFilter());
        }

        return idFilters;
    }

//    private Map<Integer, RangeFilter> mSelectedRangeFilters;
//    private Map<Integer,java.util.List<List_>> mSelectedIdFilters;
//
//    private String getSelectedRangeFilters()
//    {
//        Gson gson = new Gson();
//        String rangeFilters = gson.toJson(mSelectedRangeFilters);
//        return rangeFilters;
//    }
//
//    private void setSelectedRangeFiltersFilters(String selectedRangeFilters)
//    {
//        Gson gson = new Gson();
//        Type typeOfHashMap = new TypeToken<Map<Integer, RangeFilter>>() { }.getType();
//        mSelectedRangeFilters = gson.fromJson(selectedRangeFilters, typeOfHashMap);
//    }
//
//    private String getSelectedIdFilters()
//    {
//        Gson gson = new Gson();
//        String rangeFilters = gson.toJson(mSelectedIdFilters);
//        return rangeFilters;
//    }

//    private void setSelectedIdFilters(String selectedIdFilters)
//    {
//        Gson gson = new Gson();
//        Type typeOfHashMap = new TypeToken<Map<Integer, RangeFilter>>() { }.getType();
//        mSelectedRangeFilters = gson.fromJson(selectedIdFilters, typeOfHashMap);
//    }


    private String getSelectedFilterQueryParams() {
        return getSelectedRangeFilterQueryParams() + getSelectedIdFilterQueryParams();
    }

    private String getSelectedIdFilterQueryParams() {
        IdFilterFragment idFilterFragment;
        java.util.List<List_> filterList;
        IdFilter idFilter;
        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < mIdFilterFragments.size(); i++) {
            idFilterFragment = (IdFilterFragment) mIdFilterFragments.get(i);
            idFilter = idFilterFragment.getIdFilter();
            filterList = idFilterFragment.getSelectedFilters();
            for (int j = 0; j < filterList.size(); j++) {
                List_ filter = filterList.get(j);
                if (j == 0) {
                    queryBuilder.append("&" + idFilter.getKey() + "=" + filter.getValue());
                } else {
                    queryBuilder.append("," + filter.getValue());
                }
            }
        }
        return queryBuilder.toString();
    }

    private String getSelectedRangeFilterQueryParams() {
        RangeFilterFragment rangeFilterFragment;
        java.util.List<com.mirraw.android.models.searchResults.List> filterList;
        StringBuilder queryBuilder = new StringBuilder();
        RangeFilter rangeFilter;
//        mSelectedRangeFilters = new HashMap<Integer, RangeFilter>();
        for (int i = 0; i < mRangeFilterFragments.size(); i++) {
            rangeFilterFragment = (RangeFilterFragment) mRangeFilterFragments.get(i);
            rangeFilter = rangeFilterFragment.getRangeFilter();
//            mSelectedRangeFilters.put(i, rangeFilter);
            queryBuilder.append(rangeFilterFragment.getSelectedFilterString());
        }
        return queryBuilder.toString();
    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.applyButton:
                applyFilter();
                break;

            case R.id.clearButton:
                clearFilters();
                break;
        }


    }

    static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            mFragments = fragments;
            mFragmentTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_down_to_bottom);
    }
}
