package com.mirraw.android.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.AddressUtil;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.CreateOrderAsync;
import com.mirraw.android.async.GetPaymentOptionsAsync;
import com.mirraw.android.async.ListAddressAsync;
import com.mirraw.android.models.PaymentOptionsDetail.AvailablePaymentOptions;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.payu.sdk.Constants;
import com.payu.sdk.PayU;
import com.splunk.mint.Mint;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfirmAddressActivity extends AnimationActivity
        implements
        ListAddressAsync.AddressLoader,
        GetPaymentOptionsAsync.PaymentOptionsLoader,
        CreateOrderAsync.CreateOrderLoader, RippleView.OnRippleCompleteListener {

    public static final String tag = ConfirmAddressActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    private String mAddressJson;

    private SharedPreferencesManager mAppSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialog = new ProgressDialog(ConfirmAddressActivity.this);

        setContentView(R.layout.activity_confirm_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppSharedPrefs = new SharedPreferencesManager(Mirraw.getAppContext());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Address mShipAddress, mBillAddress;

    private void getData() {
        mAddressJson = mAppSharedPrefs.getAddresses();
        Logger.v("ConfirmAddressActivity", "Pavi ADDRESS FROM SHAREDPREF: " + mAddressJson);

        try {
            mShipAddress = AddressUtil.getShippingAddress();
            mBillAddress = AddressUtil.getBillingAddress();
        } catch (Exception e) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        initViews();
    }

    private void setBillingShippingAddress(ArrayList<Address> addressArray) {
        if (addressArray.size() == 0 || addressArray == null)
            this.finish();
        else {
            mShipAddress = addressArray.get(0);
            mBillAddress = addressArray.get(0);

            for (int i = 0; i < addressArray.size(); i++) {
                Logger.v("", "DEFAULT STATUS: " + addressArray.get(i).getDefault());
                if (addressArray.get(i).getDefault() == 1) {
                    mBillAddress = addressArray.get(i);
                    mShipAddress = addressArray.get(i);
                    break;
                }
            }

            for (int i = 0; i < addressArray.size(); i++) {
                if (addressArray.get(i).getShipAddressStatus()) {
                    mShipAddress = addressArray.get(i);
                    break;
                }
            }
        }
    }

    private void addAddressButtonStatus(ArrayList<Address> addressArray) {
        if (getIntent().getExtras().getBoolean("firstAddress", false)) {
            findViewById(R.id.address_btn_layout).setVisibility(View.GONE);
            findViewById(R.id.edit_address_btn_layout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.address_btn_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.edit_address_btn_layout).setVisibility(View.VISIBLE);
        }
    }

    private void initViews() {
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(mShipAddress.getName());

        TextView addr = (TextView) findViewById(R.id.address);
        addr.setText(mShipAddress.getStreetAddress());

        TextView city = (TextView) findViewById(R.id.city);
        city.setText(mShipAddress.getCity());

        TextView state = (TextView) findViewById(R.id.state);
        state.setText(mShipAddress.getState() + " - " + mShipAddress.getPincode());

        TextView country = (TextView) findViewById(R.id.country);
        country.setText(mShipAddress.getCountry());

        TextView mobile = (TextView) findViewById(R.id.mobile);
        mobile.setText(" : " + mShipAddress.getPhone());

        ((RippleView) findViewById(R.id.address_btn_ripple_view)).setOnRippleCompleteListener(this);
        ((RippleView) findViewById(R.id.edit_address_btn_ripple_view)).setOnRippleCompleteListener(this);
        ((RippleView) findViewById(R.id.continuePaymentRippleView)).setOnRippleCompleteListener(this);

        /*findViewById(R.id.continuePaymentButton).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                payPalPayment(v);

                return false;
            }
        });*/
    }


    GetPaymentOptionsAsync mGetPaymentOptionsAsync;

    @Override
    public void loadPaymentOptions() {
        JSONObject location = null;
        try {
            JSONObject body = new JSONObject();
            JSONObject billingJson = new JSONObject();
            billingJson.put("pincode", mBillAddress.getPincode());
            billingJson.put("country", mBillAddress.getCountry());

            JSONObject shipingJson = new JSONObject();
            shipingJson.put("pincode", mShipAddress.getPincode());
            shipingJson.put("country", mShipAddress.getCountry());

            body.put("billing", billingJson);
            body.put("shipping", shipingJson);
            location = new JSONObject();
            location.put("location", body);
            Logger.v("PAYMENT OPTIONS", "BODY FOR PAYMENT OPTIONS: " + location.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mGetPaymentOptionsAsync = new GetPaymentOptionsAsync(this);

        mProgressDialog = ProgressDialog.show(this, "Please wait ...", "Fetching Payment Options ...", true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mGetPaymentOptionsAsync != null) {
                    mGetPaymentOptionsAsync.cancel(true);
                }
            }
        });

        String url = ApiUrls.API_GET_PAYMENT_OPTIONS;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBodyJson(location).setHeaders(headerMap).build();
        mGetPaymentOptionsAsync.executeTask(request);
    }

    private AvailablePaymentOptions mAvailablePaymentOptions;

    @Override
    public void onPaymentOptionsLoaded(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        //if (getIntent().getBooleanExtra("FREE_ORDER", false)) {
        mAvailablePaymentOptions = new Gson().fromJson(response.getBody(), AvailablePaymentOptions.class);
        if (mAvailablePaymentOptions.getDetails().getTotal() == 0) {
            createOrder(mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditDebitCard().getValue());
        } else {
            Intent paymentOptionsIntent = new Intent(ConfirmAddressActivity.this, PaymentActivity.class);
            paymentOptionsIntent.putExtra("AVAILABLE_PAY_OPTIONS", response.getBody());
            startActivity(paymentOptionsIntent);
        }
    }

    @Override
    public void onPaymentOptionsLoadedFailed(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if (response.getResponseCode() == 0) {
            showSnackBar("No Internet Connection.");
        } else {
            showSnackBar("Problem Loading Data");
        }
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }

    private CreateOrderAsync mCreateOrderAsync;

    @Override
    public void createOrder(String pay_type) {
        int shipping_id = AddressUtil.getShipping_id();
        int billing_id = AddressUtil.getBilling_id();

        if (shipping_id == -1)
            shipping_id = billing_id;

        Logger.v("", "Shipping Id: " + shipping_id);
        Logger.v("", "Billing Id: " + billing_id);

        String url = ApiUrls.API_CREATE_ORDER;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        JSONObject loginResponseJson;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            Logger.v("", "Token: " + getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
            Logger.v("", "Device-ID: " + NetworkUtil.getDeviceId(this));
            loginResponseJson = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, loginResponseJson.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            Logger.v("", "Access-Token: " + loginResponseJson.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, loginResponseJson.getJSONArray(Headers.CLIENT).get(0).toString());
            Logger.v("", "Client: " + loginResponseJson.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, loginResponseJson.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            Logger.v("", "Token-Type: " + loginResponseJson.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, loginResponseJson.getJSONArray(Headers.UID).get(0).toString());
            Logger.v("", "Uid: " + loginResponseJson.getJSONArray(Headers.UID).get(0).toString());

            JSONObject body = null;

            body = new JSONObject();
            JSONObject order = new JSONObject();
            order.put("shipping_address", String.valueOf(shipping_id));
            order.put("billing_address", String.valueOf(billing_id));
            order.put("pay_type", pay_type);
            body.put("order", order);
            Logger.v("ORDERS", "ORDERS: " + body.toString());

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setHeaders(headerMap).setBodyJson(body).build();
            mCreateOrderAsync = new CreateOrderAsync(this);
            mCreateOrderAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOrderSuccessful(Response response) {
        Intent thankYou = new Intent(this, ThankYouActivity.class);
        thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (response.getResponseCode() == 200) {
            thankYou.putExtra("response", response.getBody());
            Toast.makeText(this, "Order Created Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Some Error Ocurred", Toast.LENGTH_SHORT).show();
        }
        startActivity(thankYou);
        this.finish();
    }

    @Override
    public void createOrderFailed(Response response) {
        Toast.makeText(this, "Some Error Ocurred", Toast.LENGTH_SHORT).show();
        Intent thankYou = new Intent(this, ThankYouActivity.class);
        thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(thankYou);
        this.finish();
    }


    private void startPayment(final String availableOptions) {
        final HashMap<String, String> params = new HashMap<String, String>();
        double amount = 10;

        //params.put("amount", "10");
        params.put("surl", "https://dl.dropboxusercontent.com/s/dtnvwz5p4uymjvg/success.html");
        params.put("furl", "https://dl.dropboxusercontent.com/s/z69y7fupciqzr7x/furlWithParams.html");
        params.put("productinfo", "myprod");
        params.put("txnid", String.valueOf(System.currentTimeMillis()));
        //params.put("user_credentials", "0MQaQP:test");

        //params.remove("amount");

        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Calculating Hash. please wait..");
        mProgressDialog.show();

        final double finalAmount = amount;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.FETCH_DATA_URL);
                    List<NameValuePair> postParams = new ArrayList<NameValuePair>(5);
                    postParams.add(new BasicNameValuePair("command", "mobileHashTestWs"));
                    postParams.add(new BasicNameValuePair("key", (getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData).getString("payu_merchant_id")));
                    postParams.add(new BasicNameValuePair("var1", params.get("txnid")));
                    postParams.add(new BasicNameValuePair("var2", String.valueOf(finalAmount)));
                    postParams.add(new BasicNameValuePair("var3", params.get("productinfo")));
                    postParams.add(new BasicNameValuePair("var4", params.get("user_credentials")));
                    postParams.add(new BasicNameValuePair("hash", "payu"));
                    httppost.setEntity(new UrlEncodedFormEntity(postParams));
                    JSONObject response = new JSONObject(EntityUtils.toString(httpclient.execute(httppost).getEntity()));

                    // set the hash values here.

                    if (response.has("result")) {
                        PayU.merchantCodesHash = response.getJSONObject("result").getString("merchantCodesHash");
                        PayU.paymentHash = response.getJSONObject("result").getString("paymentHash");
                        PayU.vasHash = response.getJSONObject("result").getString("mobileSdk");
                        PayU.ibiboCodeHash = response.getJSONObject("result").getString("detailsForMobileSdk");

                        if (response.getJSONObject("result").has("deleteHash")) {
                            PayU.deleteCardHash = response.getJSONObject("result").getString("deleteHash");
                            PayU.getUserCardHash = response.getJSONObject("result").getString("getUserCardHash");
                            PayU.editUserCardHash = response.getJSONObject("result").getString("editUserCardHash");
                            PayU.saveUserCardHash = response.getJSONObject("result").getString("saveUserCardHash");
                        }
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Intent paymentOption = new Intent(ConfirmAddressActivity.this, PaymentActivity.class);
                    //Add bundle data here..
                    //paymentOption.putExtra(PayU.AMOUNT, finalAmount);
                    for (String key : params.keySet()) {
                        paymentOption.putExtra(key, params.get(key));
                    }

                    if (params.containsKey(PayU.DROP_CATEGORY)) {
                        PayU.dropCategory = params.get(PayU.DROP_CATEGORY).replaceAll("\\s+", "");
                        paymentOption.putExtra(PayU.DROP_CATEGORY, PayU.dropCategory);
                    }
                    if (params.containsKey(PayU.ENFORCE_PAYMETHOD)) {
                        PayU.enforcePayMethod = params.get(PayU.ENFORCE_PAYMETHOD);
                        paymentOption.putExtra(PayU.ENFORCE_PAYMETHOD, PayU.enforcePayMethod);
                    }
                    if (params.containsKey(PayU.USER_CREDENTIALS)) {
                        PayU.userCredentials = params.get(PayU.USER_CREDENTIALS);
                        paymentOption.putExtra(PayU.USER_CREDENTIALS, PayU.userCredentials);
                    }

                    paymentOption.putExtra(PayU.MERCHANT_KEY, getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData.getString("payu_merchant_id"));
                    paymentOption.putExtra("AVAILABLE_PAY_OPTIONS", availableOptions);
                    startActivity(paymentOption);
                    //PayU.getInstance(ConfirmAddressActivity.this).startPaymentProcess(finalAmount, params);
                    //PayU.getInstance(ConfirmAddressActivity.this).startPaymentProcess(finalAmount, params, new PayU.PaymentMode[]{PayU.PaymentMode.CC, PayU.PaymentMode.NB});

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Toast.makeText(ConfirmAddressActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Toast.makeText(ConfirmAddressActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }
                return null;
            }
        }.execute();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*PayU onActivityResult*/
        if (requestCode == PayU.RESULT) {
            if (resultCode == RESULT_OK) {
                //success
                if (data != null) {
                    Toast.makeText(this, "Success" + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //failed
                if (data != null) {
                    Toast.makeText(this, "Failed" + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {

        if (mAddAddressAsync != null) {
            mAddAddressAsync.cancel(true);
        }
        if (mCreateOrderAsync != null) {
            mCreateOrderAsync.cancel(true);
        }
        if (mGetPaymentOptionsAsync != null) {
            mGetPaymentOptionsAsync.cancel(true);
        }
        super.onDestroy();
    }

    private ListAddressAsync mAddAddressAsync;

    @Override
    public void loadAddressList() {
        String url = ApiUrls.API_ADD_ADDRESS;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        JSONObject head;// = new JSONObject();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        try {
            // Toast.makeText(this, "Login Response: " + sharedPreferencesManager.getLoginResponse(), Toast.LENGTH_LONG).show();
            //Logger.v("", "Login Response: " + sharedPreferencesManager.getLoginResponse());

            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            // Logger.v("", "Login Response: " + head.getJSONArray("Access-Token").get(0).toString());
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
        mAddAddressAsync = new ListAddressAsync(this, this);
        mAddAddressAsync.executeTask(request);
    }

    @Override
    public void loadAddressFailed(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (response.getResponseCode() == 0) {
            showSnackBar("No Internet Connection.");
        } else {
            showSnackBar("Problem Loading Data");
        }
    }

    @Override
    public void loadAddressSuccess(Response response) {
        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try {
                String getAddress = response.getBody();
                Intent viewAddressIntent = new Intent(ConfirmAddressActivity.this, ViewAddressActivity.class);
                viewAddressIntent.putExtra("getAddress", getAddress);
                startActivity(viewAddressIntent);
            } catch (Exception e) {
                loadAddressFailed(response);
                Gson gson = new Gson();
                String data = gson.toJson(response);
                Mint.logExceptionMessage(tag, data, e);
            }

        } else {
            loadAddressFailed(response);
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.continuePaymentRippleView:
                loadPaymentOptions();
                //startPayment();
                break;
            case R.id.address_btn_ripple_view:
                if (Utils.isNetworkAvailable(this)) {
                    Intent intent = new Intent(ConfirmAddressActivity.this, AddAddressActivity.class);
                    //intent.putExtra("FREE_ORDER", getIntent().getBooleanExtra("FREE_ORDER", false));
                    startActivity(intent);
                } else {
                    showSnackBar("No Internet Connection.");
                }
                break;
            case R.id.edit_address_btn_ripple_view:
                Intent viewAddressIntent = new Intent(ConfirmAddressActivity.this, ViewAddressActivity.class);
                viewAddressIntent.putExtra("getAddress", mAppSharedPrefs.getAddresses());
                startActivity(viewAddressIntent);
                break;
        }
    }
}
