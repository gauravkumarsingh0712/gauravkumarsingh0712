package com.mirraw.android.ui.activities;


import com.google.gson.Gson;
import com.mirraw.android.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import android.view.MenuItem;


import com.mirraw.android.models.address.State;

import com.mirraw.android.ui.fragments.SelectStateFragment;

public class SelectStateActivity extends AnimationActivity implements SelectStateFragment.StateSelectListener {

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_state);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent i=getIntent();
        Bundle b=i.getExtras();
        int id=b.getInt("countryId");
        showStateFragment(id);

    }

    Context cxt = this;
    SelectStateFragment.StateSelectListener mStateSelectListener;

    private void showStateFragment(int countryId) {

        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Fragment f = SelectStateFragment.newInstance(countryId);
        ft.replace(R.id.container, f);
        ft.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStateClicked(State state) {
        Gson gson=new Gson();
        String state_details=gson.toJson(state);
        Intent i=new Intent(cxt,AddAddressActivity.class);
        i.putExtra("state_details",state_details);
        setResult(RESULT_OK,i);

        finish();

    }
}