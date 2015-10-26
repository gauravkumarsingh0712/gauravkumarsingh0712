package com.mirraw.android.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.RefreshTokenAsync;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;

import org.json.JSONObject;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vihaan on 1/7/15.
 */
public class SplashActivity extends Activity {

    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        initViews();
        refreshToken();
    }

    private void refreshToken() {
        try {
            RefreshTokenAsync refreshTokenAsync = new RefreshTokenAsync();
            String url = ApiUrls.API_REFRESH_ACCESS_TOKEN;
            HashMap<String, String> body = new HashMap<>();
            HashMap<String, String> headerMap = new HashMap<>();

            JSONObject head;
            head = new JSONObject(new SharedPreferencesManager(Mirraw.getAppContext()).getLoginResponse()).getJSONObject("mHeaders");

            body.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            body.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());

            headerMap.put(Headers.TOKEN, Mirraw.getAppContext().getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(Mirraw.getAppContext()));

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBody(body).setHeaders(headerMap).build();
            refreshTokenAsync.executeTask(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        //closing transition animations
        //overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    private void initViews() {
        mShimmerViewContainer = (ShimmerFrameLayout) findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                Intent intent = new Intent(SplashActivity.this, AppIntroActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 2000);
    }


}
