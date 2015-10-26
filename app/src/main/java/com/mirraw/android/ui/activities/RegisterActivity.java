package com.mirraw.android.ui.activities;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.mirraw.android.R;
import com.mirraw.android.ui.fragments.RegistrationFragment;

/**
 * Created by Nimesh Luhana on 28-07-2015.
 */

public class RegisterActivity extends AnimationActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        overridePendingTransition(R.anim.slide_up_from_bottom, R.anim.activity_close_scale);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        showFragment();

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition( R.anim.activity_open_scale,R.anim.slide_down_to_bottom);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void showFragment() {
        Fragment registrationFragment=new RegistrationFragment();

        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,registrationFragment);
        fragmentTransaction.commit();



    }


}
