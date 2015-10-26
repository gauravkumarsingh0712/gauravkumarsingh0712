package com.mirraw.android.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.address.Country;
import com.mirraw.android.ui.fragments.SelectCountryFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SelectCountryActivity extends AppCompatActivity implements SelectCountryFragment.CountrySelectListener {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        showCountryFragment();

    }



    Context cxt = this;
    SelectCountryFragment.CountrySelectListener mCountrySelectListener;

    private void showCountryFragment() {
        mCountrySelectListener = new SelectCountryFragment.CountrySelectListener() {
            @Override
            public void onCountryClicked(Country country) {


            }
        };
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment f = new SelectCountryFragment();
        ft.replace(R.id.container, f);
        ft.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();


        return super.onOptionsItemSelected(item);
    }


    private void showStateFragment() {


    }

    @Override
    public void onCountryClicked(Country country) {
        Intent i = new Intent();
        Gson gson = new Gson();
        String country_details = gson.toJson(country);

        i.putExtra("countryDetails", country_details);
        setResult(RESULT_OK, i);
        finish();
    }
}