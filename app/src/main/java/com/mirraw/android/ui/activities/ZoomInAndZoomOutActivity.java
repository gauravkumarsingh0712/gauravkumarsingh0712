package com.mirraw.android.ui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.mirraw.android.R;
import com.mirraw.android.Utils.HackyViewPager;
import com.mirraw.android.models.productDetail.Image;
import com.mirraw.android.ui.adapters.ViewPagerAdapter;
import com.mirraw.android.ui.fragments.ZoomInZoomOutFragment;
import com.mirraw.android.ui.widgets.viewpagerIndicator.CirclePageIndicator;
import com.mirraw.android.ui.widgets.viewpagerIndicator.PageIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Gaurav on 8/3/2015.
 */
public class ZoomInAndZoomOutActivity extends AnimationActivity implements View.OnClickListener {

    private static final String tag = ZoomInAndZoomOutActivity.class.getSimpleName();

    private int mSelectedImagePostion;
    private ArrayList<Image> mImages;
    private static final String ISLOCKED_ARG = "isLocked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitiy_zoomin_zoomout);
        Bundle bundle = getIntent().getExtras();

        mSelectedImagePostion = bundle.getInt("position");
        mImages =  (ArrayList<Image>) bundle.getSerializable("getimg");

        initViews();

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            ((HackyViewPager) mProductImageViewPager).setLocked(isLocked);
        }

        setupViewPager(mImages);
    }

    private void initViews()
    {
        initProductImageViewPager();
        findViewById(R.id.crossImageView).setOnClickListener(this);
    }

    private HackyViewPager mProductImageViewPager;
    PageIndicator mIndicator;
    private ImageView crossImageView;

    private void initProductImageViewPager() {
        mProductImageViewPager = (HackyViewPager) findViewById(R.id.productViewPager);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
    }

    private void setupViewPager(List<Image> images) {
        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        for (int i = 0; i < images.size(); i++) {
            ZoomInZoomOutFragment fragment = new ZoomInZoomOutFragment();
            Image image = images.get(i);
            Bundle bundle = new Bundle();
            bundle.putString("image", image.getSizes().getOriginal());

            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        ViewPagerAdapter vpa = new ViewPagerAdapter(getSupportFragmentManager(), fragments);
        mProductImageViewPager.setAdapter(vpa);
        mProductImageViewPager.setCurrentItem(mSelectedImagePostion);
        mProductImageViewPager.setPageMargin(16);
        mIndicator.setViewPager(mProductImageViewPager);
    }

    private boolean isViewPagerActive() {
        return (mProductImageViewPager != null && mProductImageViewPager instanceof HackyViewPager);
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) mProductImageViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.crossImageView:
                finish();
                break;
        }

    }
}

