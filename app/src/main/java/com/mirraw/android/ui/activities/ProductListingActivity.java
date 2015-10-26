package com.mirraw.android.ui.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.interfaces.SortFilterChangeListener;
import com.mirraw.android.ui.fragments.ProductListingFragment;
import com.nineoldandroids.animation.ObjectAnimator;

public class ProductListingActivity extends BaseActivity implements SortFilterChangeListener, RippleView.OnRippleCompleteListener
//        implements MenusFragment.ExpandingMenuActivity
{


    public interface SortFilterClickListener {
        public void onSortClicked();

        public void onFilterClicked();
    }

    private SortFilterClickListener mSortFilterClickListener;

    public static final String TAG = ProductDetailActivity.class.getSimpleName();

    DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_listing);
        initToolbar();
        setupActionBarBackButton();
        showFragment();
        initFilterAndSort();
//        showMenusFragment();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void showFragment() {
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ProductListingFragment();
        fragment.setArguments(getIntent().getExtras());
        ft.replace(R.id.container, fragment);
        ft.commit();
        mSortFilterClickListener = (SortFilterClickListener) fragment;
    }

    TextView mFilter, mSort;
    private RelativeLayout mFilterSortButtonLayout;
    ImageView filterImageView, sortImageView;
    private LinearLayout sortLinearLayout, filterLinearLayout;
    private RippleView mSortRippleView, mFilterRippleView;

    private void initFilterAndSort() {
        mFilterSortButtonLayout = (RelativeLayout) findViewById(R.id.sortFilterListingRLL);
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
        super.onComplete(rippleView);
        switch (rippleView.getId()) {
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
        //mFilterSortButtonLayout.setVisibility(View.VISIBLE);
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
        ObjectAnimator.ofFloat(mFilterSortButtonLayout, "alpha", 1, 0.75f, 0.5f, 0.25f, 0).setDuration(200).start();
    }


    //
//    private void showMenusFragment() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = new MenusFragment();
//        ft.replace(R.id.navContainer, fragment);
//        ft.commit();
//    }
//
//
//    @Override
//    public void showExpandingMenuFragment(String columns) {
//        ExpandingMenuFragment fragment = ExpandingMenuFragment.newInstance(mDrawerLayout, columns);
//
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//
////        Fragment prev = getSupportFragmentManager().findFragmentByTag(ExpandingMenuFragment.sTag);
////
////        if (prev != null) {
////            ft.remove(prev);
////        }
////        ft.addToBackStack(null);
//
//        ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right);
//        ft.replace(R.id.navContainer, fragment);
//        ft.addToBackStack(ExpandingMenuFragment.sTag);
//        ft.commitAllowingStateLoss();
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
