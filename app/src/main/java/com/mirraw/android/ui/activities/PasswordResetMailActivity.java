package com.mirraw.android.ui.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.mirraw.android.R;
import com.mirraw.android.ui.fragments.ForgotPasswordFragment;
import com.mirraw.android.ui.fragments.PasswordResetMailFragment;

public class PasswordResetMailActivity extends AnimationActivity {
    String mailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset_mail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mailId = getIntent().getExtras().getString("mailId");
        showFragment();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFragment() {
        Fragment passwordResetMailFragment = PasswordResetMailFragment.newInstance(mailId);

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, passwordResetMailFragment);
        fragmentTransaction.commit();


    }


}
