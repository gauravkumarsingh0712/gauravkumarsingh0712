package com.mirraw.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.PayPalAysncTasks.SendPayPalResponseToServerAsync;
import com.mirraw.android.database.SyncRequestManager;
import com.mirraw.android.models.PayPal.PayPalConfrimResponse.PaypalSuccessfulResponse;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by pavitra on 8/9/15.
 */
public class VerifyPaypalResponseActivity extends AnimationActivity
        implements SendPayPalResponseToServerAsync.PaypalResponseSubmitter {

    public static final String tag = VerifyPaypalResponseActivity.class.getSimpleName();
    private Bundle bundle;

    private TextView mMessageValue, mOrderNo, mEmail;
    private ProgressWheel mProgressWheel;
    private LinearLayout mOrderLL, mEmailLL;

    private String mPaypalConfirmResponse;
    private int mOrderId;
    private String mOrderNumber;
    private PaypalSuccessfulResponse mPaypalSuccessfulResponse;

    private SharedPreferencesManager mAppsharedPrefs;

    public static String PAYPAL_CONFIRM_RESPONSE_KEY = "paypal_confirm_response";
    public static String PAYPAL_ORDER_ID_KEY = "paypal_txn_id";
    public static String PAYPAL_ORDER_NUMBER = "paypal_order_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_paypal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppsharedPrefs = new SharedPreferencesManager(this);
        bundle = getIntent().getExtras();
        mPaypalConfirmResponse = bundle.getString(PAYPAL_CONFIRM_RESPONSE_KEY);
        mOrderId = bundle.getInt(PAYPAL_ORDER_ID_KEY);
        mOrderNumber = bundle.getString(PAYPAL_ORDER_NUMBER);

        initViews();

        startVerification();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goHomeScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        goHomeScreen();
    }

    private void goHomeScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void initViews() {
        mMessageValue = (TextView) findViewById(R.id.message);
        mEmailLL = (LinearLayout) findViewById(R.id.emailLL);
        mOrderLL = (LinearLayout) findViewById(R.id.orderLL);
        mEmail = (TextView) findViewById(R.id.email);
        mOrderNo = (TextView) findViewById(R.id.order_no);
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);


        mEmailLL.setVisibility(View.INVISIBLE);
        mOrderLL.setVisibility(View.INVISIBLE);
    }

    private SendPayPalResponseToServerAsync mSendPayPalResponseToServerAsync;

    private void startVerification() {
        mMessageValue.setText(getString(R.string.paypal_start_verification_msg));
        mProgressWheel.setVisibility(View.VISIBLE);

        submitPaypalResponse();
    }

    private Request mRequest;

    @Override
    public void submitPaypalResponse() {
        mSendPayPalResponseToServerAsync = new SendPayPalResponseToServerAsync(this);
        mPaypalSuccessfulResponse = new Gson().fromJson(mPaypalConfirmResponse, PaypalSuccessfulResponse.class);

        String url = ApiUrls.API_PAYPAL_VERIFY + mOrderId + "/paypal_verify";

        Logger.v("", "PayPal Verify Url: " + url);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());

            HashMap<String, String> bodyMap = new HashMap<String, String>();

            bodyMap.put("paypal_txn_id", mPaypalSuccessfulResponse.getResponse().getId());
            Logger.v("", "Paypal_txn_id: " + mPaypalSuccessfulResponse.getResponse().getId());

            mRequest = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setHeaders(headerMap).setBody(bodyMap).build();
            mSendPayPalResponseToServerAsync.executeTask(mRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                Gson gson = new Gson();
                String request = gson.toJson(mRequest);
                Mint.logExceptionMessage(tag, request, e);
            } catch (Exception ex) {
                Mint.logExceptionMessage(tag, "mRequest was null", ex);
            }
        }
    }

    @Override
    public void onPaypalResponseSubmissionSuccess(Response response) {
        Logger.v("", "Paypal Verification: " + response.getBody().toString());
        Intent thankYou = new Intent(VerifyPaypalResponseActivity.this, ThankYouActivity.class);
        thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            JSONObject responseJson;
            if (response.getBody() == null || response.getBody().trim().equalsIgnoreCase("")) {
                responseJson = new JSONObject();
            } else {
                responseJson = new JSONObject(response.getBody());
            }
            responseJson.put("id", mOrderId);
            responseJson.put("number", mOrderNumber);
            response.setBody(responseJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.v(tag, "RESPONSE BODY: " + response.getBody());
        thankYou.putExtra("response", response.getBody());
        startActivity(thankYou);
        finish();
    }

    @Override
    public void onPaypalResponseSubmissionFailure(Response response) {

        mEmail.setText(mAppsharedPrefs.getUserEmail().toString());
        mOrderNo.setText(String.valueOf(mOrderId));

        mEmailLL.setVisibility(View.VISIBLE);
        mOrderLL.setVisibility(View.VISIBLE);

        mProgressWheel.setVisibility(View.INVISIBLE);

        if (response.getResponseCode() == 0) {
            mMessageValue.setText(getString(R.string.paypal_verify_no_internet_text));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("email", mAppsharedPrefs.getUserEmail().toString());
            map.put("order number", String.valueOf(mOrderId));
            Mint.logEvent("Paypal Verification Internet Error", MintLogLevel.Info, map);
            new SyncRequestManager().insertRow(new Gson().toJson(mRequest));
        } else {
            mMessageValue.setText(getString(R.string.paypal_verify_server_error_text));
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("email", mAppsharedPrefs.getUserEmail().toString());
            map.put("order number", String.valueOf(mOrderId));
            Mint.logEvent("Paypal Verification Server Error", MintLogLevel.Info, map);
        }
    }

    @Override
    protected void onDestroy() {
        if (mSendPayPalResponseToServerAsync != null) {
            mSendPayPalResponseToServerAsync.cancel(true);
        }
        super.onDestroy();
    }
}
