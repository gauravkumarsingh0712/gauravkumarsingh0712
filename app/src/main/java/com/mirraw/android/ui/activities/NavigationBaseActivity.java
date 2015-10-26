package com.mirraw.android.ui.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.menus.Menu;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.fragments.ExpandingMenuFragment;
import com.mirraw.android.ui.fragments.MenusFragment;

/**
 * Created by nimesh on 25/8/15.
 */
public class NavigationBaseActivity extends BaseActivity implements MenusFragment.ExpandingMenuActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);

        if (mActionBarDrawerToggle != null  && mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void showMenusFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new MenusFragment();
        ft.replace(R.id.navContainer, fragment);
        ft.commit();
    }

    ActionBarDrawerToggle mActionBarDrawerToggle;
    DrawerLayout mDrawerLayout;

    @Override
    public void showExpandingMenuFragment(String columns) {
        if(NavigationBaseActivity.this != null)
        {
            Gson gson = new Gson();
            Menu menu = gson.fromJson(columns, Menu.class);
            if (menu.getMenuColumns().size() > 1) {
                ExpandingMenuFragment fragment = ExpandingMenuFragment.newInstance(mDrawerLayout, columns);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right);
                ft.replace(R.id.navContainer, fragment, ExpandingMenuFragment.sTag);
//        ft.commit();
                ft.addToBackStack(ExpandingMenuFragment.sTag);
                //http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
                ft.commitAllowingStateLoss();
            } else {
                Logger.d(TAG, "Only one category");
            }

        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.DrawerLayout);
//        showMenusFragment();
    }
}
