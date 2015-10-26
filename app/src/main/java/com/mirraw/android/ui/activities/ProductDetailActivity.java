package com.mirraw.android.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.interfaces.BuyNowAddToCartDisplayListener;
import com.mirraw.android.ui.fragments.ProductDetailFragment;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by vihaan on 8/7/15.
 */
public class ProductDetailActivity extends BaseActivity implements
        BuyNowAddToCartDisplayListener,
        RippleView.OnRippleCompleteListener
//        , MenusFragment.ExpandingMenuActivity

{

    public interface BuyNowAddToCartClickListener {
        public void onBuyNowClicked();

        public void onAddToCartClicked();
    }

    private String mActivityName;
    DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        initToolbar();
        setActivityName();
        setupActionBarBackButton();
        showFragment();
//        showMenusFragment();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        initButtons();
        //    mGoogleApiClient = buildGoogleApiClient();
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

    private void setActivityName() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String data = bundle.getString("productTitle");
            //Gson gson = new Gson();
            //Block block = gson.fromJson(data, Block.class);
            mActivityName = data;//block.getName();
            setTitle(mActivityName);
        }
    }


    BuyNowAddToCartClickListener mBuyNowAddToCartClickListener;

    private void showFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ProductDetailFragment();
        fragment.setArguments(getIntent().getExtras());
        ft.replace(R.id.container, fragment);
        ft.commit();
        mBuyNowAddToCartClickListener = (BuyNowAddToCartClickListener) fragment;
    }

    private LinearLayout mOptionButtons;
    private View mShadowView;
    private Button mAddToCartButton, mBuyNowButton, mOutOfStockButton;
    private RippleView mAddToCartRippleView, mBuyNowRippleView;

    private void initButtons() {
        mOptionButtons = (LinearLayout) findViewById(R.id.optionButtons);
        mShadowView = findViewById(R.id.shadowView);
        //mOptionButtons.setVisibility(View.GONE);
        // mAddToCartButton = (Button) findViewById(R.id.addToCartButton);
//        mBuyNowButton = (Button) findViewById(R.id.buyNowButton);
        mOutOfStockButton = (Button) findViewById(R.id.outOfStockButton);
        mShadowView.setVisibility(View.GONE);
        mOptionButtons.setVisibility(View.GONE);
        mAddToCartRippleView = ((RippleView) findViewById(R.id.addToCartRippleView));
        mAddToCartRippleView.setOnRippleCompleteListener(this);
        mBuyNowRippleView = ((RippleView) findViewById(R.id.buyNowRippleView));
        mBuyNowRippleView.setOnRippleCompleteListener(this);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        super.onComplete(rippleView);
        int id = rippleView.getId();

        switch (id) {
            case R.id.addToCartRippleView:
                mBuyNowAddToCartClickListener.onAddToCartClicked();
                break;
            case R.id.buyNowRippleView:
                mBuyNowAddToCartClickListener.onBuyNowClicked();
                break;
        }
    }

    @Override
    public void showBuyNowAddToCartButtons() {
        mOptionButtons.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mOptionButtons, "alpha", 0, 0.25f, 0.5f, 0.75f, 1).setDuration(500).start();
        ObjectAnimator.ofFloat(mShadowView, "alpha", 0, 0.25f, 0.5f, 0.75f, 1).setDuration(700).start();
    }

    @Override
    public void showOutOfStockButton() {
        //mAddToCartButton.setVisibility(View.GONE);
        //mBuyNowButton.setVisibility(View.GONE);
        mAddToCartRippleView.setVisibility(View.GONE);
        mBuyNowRippleView.setVisibility(View.GONE);
        mOutOfStockButton.setVisibility(View.VISIBLE);
    }

    //    private void showMenusFragment() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        Fragment fragment = new MenusFragment();
//        ft.replace(R.id.navContainer, fragment);
//        ft.commit();
//    }


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
