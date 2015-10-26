package com.mirraw.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.fragments.ExpandingMenuFragment;
import com.mirraw.android.ui.fragments.HomeFragment;
import com.mirraw.android.ui.fragments.MenusFragment;
import com.mirraw.android.ui.fragments.SingleCategoryMenuFragment;


public class HomeActivity extends BaseActivity
        implements MenusFragment.ExpandingMenuActivity,
        HomeFragment.FragmentLoader {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initToolbar();
        mToolbar.setLogo(R.drawable.menu_bar);
        initViews();
        showFragment();
        showMenusFragment();

        onServerLogout(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onServerLogout(intent);
    }

    private void onServerLogout(Intent intent) {
        try {
            if (intent.getExtras().getBoolean(LoginManager.LOGOUT_KEY)) {
                Logger.v("", "Logout option clicked");
                onLoginLogoutOptionClicked();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
            return;

        }
        super.onBackPressed();

        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }


    private void initViews() {
        initNavigationDrawer();
    }

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;

    private void initNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mActionBarDrawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        mDrawerLayout,
                        R.string.app_name,
                        R.string.app_name
                ) {

        };
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();
    }

    private void showFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new HomeFragment();
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void showMenusFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new MenusFragment();
        ft.replace(R.id.navContainer, fragment);
        ft.commit();
    }


    @Override
    public void showExpandingMenuFragment(String columns) {
        /*Bundle bundle = new Bundle();
        bundle.putString("menu", columns);

        Fragment fragment = new ExpandingMenuFragment(mDrawerLayout);
        fragment.setArguments(bundle);*/

        Gson gson = new Gson();
        com.mirraw.android.models.menus.Menu menu = gson.fromJson(columns, com.mirraw.android.models.menus.Menu.class);
        if (menu.getMenuColumns().size() > 1) {
            ExpandingMenuFragment fragment = ExpandingMenuFragment.newInstance(mDrawerLayout, columns);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

//        Fragment prev = getSupportFragmentManager().findFragmentByTag(ExpandingMenuFragment.sTag);
//
//        if (prev != null) {
//            ft.remove(prev);
//        }
//        ft.addToBackStack(null);

            ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right);
            ft.replace(R.id.navContainer, fragment);
            ft.addToBackStack(ExpandingMenuFragment.sTag);
            ft.commitAllowingStateLoss();
        } else {
            Logger.d(TAG, "Only 1 category");
            //TODO: Open a different fragment to directly show the list.
            SingleCategoryMenuFragment fragment = SingleCategoryMenuFragment.newInstance(mDrawerLayout, columns);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right);
            ft.replace(R.id.navContainer, fragment);
            ft.addToBackStack(SingleCategoryMenuFragment.sTag);
            ft.commitAllowingStateLoss();
        }
    }

    private Menu menu;

    @Override
    public void onFragmentLoaded() {

        boolean isLoggedIn = mSharedPreferencesManager.getLoggedIn();
        if (!isLoggedIn) {
            showLoginFragment();
        }
    }
}