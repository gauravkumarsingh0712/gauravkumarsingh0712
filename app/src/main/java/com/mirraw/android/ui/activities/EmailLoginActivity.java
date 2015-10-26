package com.mirraw.android.ui.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import com.mirraw.android.R;
import com.mirraw.android.ui.fragments.EmailLoginFragment;
import com.mirraw.android.ui.fragments.RegistrationFragment;

/**
 * Created by Nimesh Luhana on 28-07-2015.
 */
public class EmailLoginActivity extends AnimationActivity {
    public static final int mLoginRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_email_login);
        overridePendingTransition(R.anim.slide_up_from_bottom, R.anim.activity_close_scale);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        showFragment();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.slide_down_to_bottom);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == ForgotPasswordActivity.mForgotPasswordActivity) {
            if (resultCode == RESULT_OK) {
                String mailId = data.getExtras().getString("mailId");

                Intent i = new Intent(this, PasswordResetMailActivity.class);
                i.putExtra("mailId", mailId);
                startActivity(i);
                finish();

            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFragment() {
        Fragment emailLoginFragment = new EmailLoginFragment();

        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, emailLoginFragment);
        fragmentTransaction.commit();


    }


}
