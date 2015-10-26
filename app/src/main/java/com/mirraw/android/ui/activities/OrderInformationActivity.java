package com.mirraw.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.mirraw.android.R;
import com.mirraw.android.ui.fragments.OrderInformationFragment;

public class OrderInformationActivity extends AnimationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlack);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_information);
        initToolbar();
        showFragment();
    }

    private void showFragment() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new OrderInformationFragment();
        fragment.setArguments(bundle);
        ft.replace(R.id.container, fragment);
        ft.commit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
