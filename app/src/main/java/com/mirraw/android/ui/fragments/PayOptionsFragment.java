package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.AddressUtil;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.CreateOrderAsync;
import com.mirraw.android.async.PayPalAysncTasks.PaypalCreateOrderAsync;
import com.mirraw.android.async.PayPalAysncTasks.SendPayPalResponseToServerAsync;
import com.mirraw.android.async.PayuAyncTasks.PayuCreateOrderAsync;
import com.mirraw.android.models.PayPal.CreateOrderPaypal;
import com.mirraw.android.models.PayPal.PayPalConfrimResponse.PaypalSuccessfulResponse;
import com.mirraw.android.models.PaymentOptionsDetail.AvailablePaymentOptions;
import com.mirraw.android.models.Payu.CreateOrderPayu;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.activities.ThankYouActivity;
import com.mirraw.android.ui.activities.VerifyPaypalResponseActivity;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.payu.sdk.Constants;
import com.payu.sdk.Params;
import com.payu.sdk.PayU;
import com.payu.sdk.Payment;
import com.payu.sdk.PaymentListener;
import com.payu.sdk.ProcessPaymentActivity;
import com.payu.sdk.exceptions.HashException;
import com.payu.sdk.exceptions.MissingParameterException;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pavitra on 17/7/15.
 */
public class PayOptionsFragment extends Fragment implements
        View.OnClickListener,
        CreateOrderAsync.CreateOrderLoader,
        PaypalCreateOrderAsync.PaypalCreateOrderLoader,
        PayuCreateOrderAsync.PayuCreateOrderLoader {

    private String tag = PayOptionsFragment.class.getSimpleName();

    PaymentListener mPaymentListener;
    ProgressDialog mProgressDialog;
    PayU.PaymentMode[] paymentOptions;
    private PayU.PaymentMode[] mAvailableOptions;

    private AvailablePaymentOptions mAvailablePaymentOptions;
    private SharedPreferencesManager mAppSharedPrefs;
    private String mCurrencyCode;

    public PayOptionsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppSharedPrefs = new SharedPreferencesManager(Mirraw.getAppContext());
        /*if (getArguments().getIntArray(PayU.PAYMENT_OPTIONS) != null) {
            int modes[] = getArguments().getIntArray(PayU.PAYMENT_OPTIONS);
            mAvailableOptions = new PayU.PaymentMode[modes.length];
            int i = 0;
            for (int m : modes) {
                mAvailableOptions[i++] = PayU.PaymentMode.values()[m];
            }
        }*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.payoptions_fragment, container, false);

        return initViews(view);
    }

    private View initViews(View view) {
        view.findViewById(R.id.payuMoney).setOnClickListener(this);
        getActivity().findViewById(R.id.order_layout).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.order_status_head).setVisibility(View.VISIBLE);

        String availOptionsString = getActivity().getIntent().getStringExtra("AVAILABLE_PAY_OPTIONS");
        Gson gson = new Gson();
        mAvailablePaymentOptions = gson.fromJson(availOptionsString, AvailablePaymentOptions.class);
        mCurrencyCode = String.valueOf(Character.toChars((char) Integer.parseInt(mAvailablePaymentOptions.getDetails().getHexSymbol(), 16)));

        /*TextView totalAmtView = (TextView) getActivity().findViewById(R.id.total_amt);
        totalAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal());*/

        TextView youPay = (TextView) getActivity().findViewById(R.id.total_payment);
        if (mAvailablePaymentOptions.getDetails().getHexSymbol().equalsIgnoreCase("20B9")) {
            youPay.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal().intValue());
        } else {
            youPay.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal());
        }

        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getPayPal().getAvailable()) {
            view.findViewById(R.id.paypal).setVisibility(View.GONE);
            view.findViewById(R.id.paypal_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.paypal).setOnClickListener(this);
        }
        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getBankDeposit().getAvailable()) {
            view.findViewById(R.id.bankdeposit).setVisibility(View.GONE);
            view.findViewById(R.id.bankdeposit_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.bankdeposit).setOnClickListener(this);
        }
        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditDebitCard().getAvailable()) {
            /***TODO: Comment was added below to disable stored cards*****/
            /*view.findViewById(R.id.stored_cards).setVisibility(View.GONE);
            view.findViewById(R.id.stored_cards_view).setVisibility(View.GONE);*/
            /***TODO: Comment was added above to disable stored cards*****/

            view.findViewById(R.id.card_payment).setVisibility(View.GONE);
            view.findViewById(R.id.card_view).setVisibility(View.GONE);
        } else {
            /***TODO: Comment was added below to disable stored cards*****/
            //view.findViewById(R.id.stored_cards).setOnClickListener(this);
            /***TODO: Comment was added above to disable stored cards*****/
            view.findViewById(R.id.card_payment).setOnClickListener(this);
        }
        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getNetBanking().getAvailable()) {
            view.findViewById(R.id.netbanking).setVisibility(View.GONE);
            view.findViewById(R.id.netbanking_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.netbanking).setOnClickListener(this);
        }
        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditCard().getAvailable()) {
            view.findViewById(R.id.creditCard).setVisibility(View.GONE);
            view.findViewById(R.id.creditCard_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.creditCard).setOnClickListener(this);
        }
        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashOnDelivery().getAvailable()) {
            view.findViewById(R.id.cod).setVisibility(View.GONE);
            view.findViewById(R.id.cod_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.cod).setOnClickListener(this);
        }
        if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashBeforeDelivery().getAvailable()) {
            view.findViewById(R.id.cbd).setVisibility(View.GONE);
            view.findViewById(R.id.cbd_view).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.cbd).setOnClickListener(this);
        }
        view.findViewById(R.id.payuMoney).setVisibility(View.GONE);
        /*if (!mAvailablePaymentOptions.getDetails().getPaymentOptions().getPayuMoney().getAvailable()) {
            view.findViewById(R.id.payuMoney).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.payuMoney).setOnClickListener(this);
        }*/

        return view;
    }

    private PayuCreateOrderAsync mPayuCreateOrderAsync;
    private boolean payuOrderCreated = false;

    public void createOrderPayu(String paytype) {
        int shipping_id = AddressUtil.getShipping_id();
        int billing_id = AddressUtil.getBilling_id();

        if (shipping_id == -1)
            shipping_id = billing_id;

        Logger.v("", "Shipping Id: " + shipping_id);
        Logger.v("", "Billing Id: " + billing_id);

        String url = ApiUrls.API_CREATE_ORDER;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject loginResponseJson;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            Logger.v("", "Token: " + getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            Logger.v("", "Device-ID: " + NetworkUtil.getDeviceId(getActivity()));
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
            order.put("pay_type", paytype);
            body.put("order", order);
            Logger.v("ORDERS", "ORDERS: " + body.toString());

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setHeaders(headerMap).setBodyJson(body).build();
            mPayuCreateOrderAsync = new PayuCreateOrderAsync(this);
            mPayuCreateOrderAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private CreateOrderPayu mCreateOrderPayu;

    @Override
    public void createPayuOrderSuccessful(Response response) {
        try {
            Logger.v("", "Payu Create Order: " + response.getBody());
            Gson gson = new Gson();
            mCreateOrderPayu = gson.fromJson(response.getBody(), CreateOrderPayu.class);
            payuOrderCreated = true;
            connectToPayu(mPayuId, mAmount, mPayType);
            //startPayment(mPayuId, mAmount, mPayType);
        } catch (Exception e) {
            createPayuOrderFailed(response);
        }
    }

    @Override
    public void createPayuOrderFailed(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            payuOrderCreated = false;

            if (response.getResponseCode() == 0) {
                showSnackBar("No Internet Connection.");
            } else {
                showSnackBar("Failed to create Payu Order");
            }
        }

    }

    private String PAYU_SUCCESS_URL = "surl";
    private String PAYU_FAILURE_URL = "furl";
    private String PAYU_PRODUCT_INFO = "productinfo";
    private String PAYU_TRANSACTION_ID = "txnid";
    private String PAYU_EMAIL = "email";
    private String PAYU_FIRSTNAME = "firstname";

    private String PAYU_BILL_ADDRESS1 = "address1";
    private String PAYU_BILL_ADDRESS2 = "address2";
    private String PAYU_BILL_CITY = "city";
    private String PAYU_BILL_STATE = "state";
    private String PAYU_BILL_COUNTRY = "country";
    private String PAYU_BILL_ZIPCODE = "zipcode";
    private String PAYU_BILL_PHONE = "phone";

    private String PAYU_SHIP_ADDRESS1 = "shipping_address1";
    private String PAYU_SHIP_ADDRESS2 = "shipping_address2";
    private String PAYU_SHIP_CITY = "shipping_city";
    private String PAYU_SHIP_STATE = "shipping_state";
    private String PAYU_SHIP_COUNTRY = "shipping_country";
    private String PAYU_SHIP_ZIPCODE = "shipping_zipcode";
    private String PAYU_SHIP_PHONE = "shipping_phone";


    private void connectToPayu(final int id, double amount, String paytype) {
        if (payuOrderCreated) {
            try {
                final HashMap<String, String> params = new HashMap<>();
                params.put(PAYU_SUCCESS_URL, ApiUrls.PAYU_SUCCESS_URL);
                params.put(PAYU_FAILURE_URL, ApiUrls.PAYU_FAILURE_URL);
                params.put(PAYU_PRODUCT_INFO, mCreateOrderPayu.getPayuMobileCreateParams().getProductinfo());
                params.put(PAYU_TRANSACTION_ID, mCreateOrderPayu.getPayuMobileCreateParams().getTxnid());
                params.put(PAYU_EMAIL, mCreateOrderPayu.getPayuMobileCreateParams().getEmail() == null ? "" : mCreateOrderPayu.getPayuMobileCreateParams().getEmail());
                params.put(PAYU_FIRSTNAME, mCreateOrderPayu.getPayuMobileCreateParams().getFirstname());

                /**TODO: ADDRESS PARAMS**/
                params.put(PAYU_BILL_ADDRESS1, mCreateOrderPayu.getPayuMobileCreateParams().getAddress1());
                params.put(PAYU_BILL_ADDRESS2, mCreateOrderPayu.getPayuMobileCreateParams().getAddress2());
                params.put(PAYU_BILL_CITY, mCreateOrderPayu.getPayuMobileCreateParams().getCity());
                params.put(PAYU_BILL_STATE, mCreateOrderPayu.getPayuMobileCreateParams().getState());
                params.put(PAYU_BILL_COUNTRY, mCreateOrderPayu.getPayuMobileCreateParams().getCountry());
                params.put(PAYU_BILL_ZIPCODE, mCreateOrderPayu.getPayuMobileCreateParams().getZipcode());
                params.put(PAYU_BILL_PHONE, mCreateOrderPayu.getPayuMobileCreateParams().getPhone());

                params.put(PAYU_SHIP_ADDRESS1, mCreateOrderPayu.getPayuMobileCreateParams().getShippingAddress1());
                params.put(PAYU_SHIP_ADDRESS2, mCreateOrderPayu.getPayuMobileCreateParams().getShippingAddress2());
                params.put(PAYU_SHIP_CITY, mCreateOrderPayu.getPayuMobileCreateParams().getShippingCity());
                params.put(PAYU_SHIP_STATE, mCreateOrderPayu.getPayuMobileCreateParams().getShippingState());
                params.put(PAYU_SHIP_COUNTRY, mCreateOrderPayu.getPayuMobileCreateParams().getShippingCountry());
                params.put(PAYU_SHIP_ZIPCODE, mCreateOrderPayu.getPayuMobileCreateParams().getShippingZipcode());
                params.put(PAYU_SHIP_PHONE, mCreateOrderPayu.getPayuMobileCreateParams().getShippingPhone());


                //params.put("user_credentials", "gtKFFx:test");
                mProgressDialog.setMessage("Connecting to Payu");
                final int finalAmount = (int) amount;

                PayU.paymentHash = mCreateOrderPayu.getPayuMobileCreateParams().getHash();
                PayU.vasHash = mCreateOrderPayu.getPayuMobileCreateParams().getVasForMobileSdk();
                PayU.ibiboCodeHash = mCreateOrderPayu.getPayuMobileCreateParams().getPaymentRelatedDetailsForMobileSdk();
                //PayU.ibiboCodeHash = "110f85b862578214e5c38637762a6fce9c34bd5e2d78bc34a486f0ee2e3e5d0fada221795de4b67952ec2cff6b4d0fe8877506e7a5480bbdbfdf9045521d8523";
                Logger.v("Pay Hash", "PAYHASH: " + mCreateOrderPayu.getPayuMobileCreateParams().getHash());
                Logger.v("Pay Hash", "PAYHASH VAS: " + mCreateOrderPayu.getPayuMobileCreateParams().getVasForMobileSdk());
                Logger.v("Pay Hash", "PAYHASH IBIBO: " + mCreateOrderPayu.getPayuMobileCreateParams().getPaymentRelatedDetailsForMobileSdk());
                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                getActivity().getIntent().putExtra(PayU.AMOUNT, finalAmount);
                for (String key : params.keySet()) {
                    getActivity().getIntent().putExtra(key, params.get(key));
                }
                if (params.containsKey(PayU.DROP_CATEGORY)) {
                    PayU.dropCategory = params.get(PayU.DROP_CATEGORY).replaceAll("\\s+", "");
                }
                if (params.containsKey(PayU.ENFORCE_PAYMETHOD)) {
                    PayU.enforcePayMethod = params.get(PayU.ENFORCE_PAYMETHOD);
                }
                if (params.containsKey(PayU.USER_CREDENTIALS)) {
                    PayU.userCredentials = params.get(PayU.USER_CREDENTIALS);
                }

                getActivity().getIntent().putExtra(PayU.MERCHANT_KEY, getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA).metaData.getString("payu_merchant_id"));

                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Bundle bundle = new Bundle();
                bundle.putString(PayU.DROP_CATEGORY, PayU.DROP_CATEGORY);
                bundle.putString(PayU.ENFORCE_PAYMETHOD, PayU.ENFORCE_PAYMETHOD);
                switch (id) {
                    /*case R.id.stored_cards:
                        StoredCardFrag storedCardFragment = new StoredCardFrag();
                        storedCardFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, storedCardFragment, "StoredCardsFragment").addToBackStack(null).commit();
                        break;*/
                    case R.id.card_payment:
                        CardDetailFragment cardDetailFragment = new CardDetailFragment();
                        cardDetailFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, cardDetailFragment, "CardDetailFragment").addToBackStack(null).commit();
                        break;
                    case R.id.netbanking:
                        NBFragment nbFragment = new NBFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, nbFragment, "NBFragment").addToBackStack(null).commit();
                        break;
                    case R.id.payuMoney:
                        try {
                            payuMoney();
                        } catch (MissingParameterException e) {
                            e.printStackTrace();
                        } catch (HashException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void payuMoney() throws MissingParameterException, HashException {
        Payment payment;
        Payment.Builder builder = new Payment().new Builder();
        Params requiredParams = new Params();

        builder.set(PayU.MODE, String.valueOf(PayU.PaymentMode.PAYU_MONEY));
//        getActivity().getIntent().removeExtra("AVAILABLE_PAY_OPTIONS");
        for (String key : getActivity().getIntent().getExtras().keySet()) {
            builder.set(key, String.valueOf(getActivity().getIntent().getExtras().get(key)));
            requiredParams.put(key, builder.get(key));
        }

        payment = builder.create();

        String postData = PayU.getInstance(getActivity()).createPayment(payment, requiredParams);
        Logger.d("postdata", postData);

        Intent intent = new Intent(getActivity(), ProcessPaymentActivity.class);
        intent.putExtra(Constants.POST_DATA, postData);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivityForResult(intent, PayU.RESULT);
    }

    private int mPayuId = -1;
    private double mAmount = 0;
    private String mPayType = "";


    private void startPayment(final int id, double amount, String paytype) {
        mPayuId = id;
        mAmount = amount;
        mPayType = paytype;

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Creating Payu Order...");
        mProgressDialog.show();

        payuOrderCreated = false;
        //payuOrderCreated = true;
        if (!payuOrderCreated) {
            createOrderPayu(paytype);
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            payuTest(id, amount, paytype);
        }
    }


    private void payuTest(final int id, double amount, String paytype) {
        final HashMap<String, String> params = new HashMap<String, String>();
        params.put("surl", "https://dl.dropboxusercontent.com/s/dtnvwz5p4uymjvg/success.html");
        params.put("furl", "https://dl.dropboxusercontent.com/s/z69y7fupciqzr7x/furlWithParams.html");
        params.put("productinfo", "myprod");
        params.put("txnid", "1441366787411");
        //params.put("user_credentials", "0MQaQP:test");
        mProgressDialog.setMessage("Connecting to Payu");
        final int finalAmount = (int) amount;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Constants.FETCH_DATA_URL);
                    List<NameValuePair> postParams = new ArrayList<NameValuePair>(5);
                    postParams.add(new BasicNameValuePair("command", "mobileHashTestWs"));
                    postParams.add(new BasicNameValuePair("hash", "payu"));

                    postParams.add(new BasicNameValuePair(PayU.MERCHANT_KEY, (getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA).metaData).getString("payu_merchant_id")));
                    //postParams.add(new BasicNameValuePair(PayU.FIRSTNAME, "pavi"));
                    //postParams.add(new BasicNameValuePair(PayU.EMAIL, ""));
                   /* postParams.add(new BasicNameValuePair(PayU.TXNID, params.get("txnid")));
                    postParams.add(new BasicNameValuePair(PayU.AMOUNT, String.valueOf(finalAmount)));
                    postParams.add(new BasicNameValuePair(PayU.PRODUCT_INFO, params.get("productinfo")));*/

                    postParams.add(new BasicNameValuePair("var1", params.get("txnid")));
                    postParams.add(new BasicNameValuePair("var2", String.valueOf(finalAmount)));
                    postParams.add(new BasicNameValuePair("var3", params.get("productinfo")));

                    //postParams.add(new BasicNameValuePair("var4", params.get("user_credentials")));
                    Logger.v("", "KEY: " + (getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA).metaData).getString("payu_merchant_id"));
                    Logger.v("", "Firstname: " + "pavi");
                    Logger.v("", "Email: " + "");
                    Logger.v("", "txnID: " + params.get("txnid"));
                    Logger.v("", "amount: " + String.valueOf(finalAmount));
                    Logger.v("", "productInfo: " + params.get("productinfo"));

                    httppost.setEntity(new UrlEncodedFormEntity(postParams));
                    JSONObject response = new JSONObject(EntityUtils.toString(httpclient.execute(httppost).getEntity()));
                    Logger.v("RESPONSE: ", "RESPONSE: " + response.toString(4));
                    if (response.has("result")) {
                        //PayU.merchantCodesHash = response.getJSONObject("result").getString("merchantCodesHash");
                        PayU.paymentHash = response.getJSONObject("result").getString("paymentHash");
                        Logger.v("PAYHASH", "PAYHASH: " + PayU.paymentHash);
                        PayU.vasHash = response.getJSONObject("result").getString("mobileSdk");
                        PayU.ibiboCodeHash = response.getJSONObject("result").getString("detailsForMobileSdk");

                        if (response.getJSONObject("result").has("deleteHash")) {
                            /*PayU.deleteCardHash = response.getJSONObject("result").getString("deleteHash");
                            PayU.getUserCardHash = response.getJSONObject("result").getString("getUserCardHash");
                            PayU.editUserCardHash = response.getJSONObject("result").getString("editUserCardHash");
                            PayU.saveUserCardHash = response.getJSONObject("result").getString("saveUserCardHash");*/
                        }
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    //Intent paymentOption = new Intent(getActivity(), PaymentActivity.class);
                    //Add bundle data here..
                    getActivity().getIntent().putExtra(PayU.AMOUNT, finalAmount);
                    for (String key : params.keySet()) {
                        getActivity().getIntent().putExtra(key, params.get(key));
                    }

                    if (params.containsKey(PayU.DROP_CATEGORY)) {
                        PayU.dropCategory = params.get(PayU.DROP_CATEGORY).replaceAll("\\s+", "");
                        //paymentOption.putExtra(PayU.DROP_CATEGORY, PayU.dropCategory);
                    }
                    if (params.containsKey(PayU.ENFORCE_PAYMETHOD)) {
                        PayU.enforcePayMethod = params.get(PayU.ENFORCE_PAYMETHOD);
                        //paymentOption.putExtra(PayU.ENFORCE_PAYMETHOD, PayU.enforcePayMethod);
                    }
                    if (params.containsKey(PayU.USER_CREDENTIALS)) {
                        PayU.userCredentials = params.get(PayU.USER_CREDENTIALS);
                        //paymentOption.putExtra(PayU.USER_CREDENTIALS, PayU.userCredentials);
                    }

                    getActivity().getIntent().putExtra(PayU.MERCHANT_KEY, getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA).metaData.getString("payu_merchant_id"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (mProgressDialog != null && mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Bundle bundle = new Bundle();
                bundle.putString(PayU.DROP_CATEGORY, PayU.DROP_CATEGORY);
                bundle.putString(PayU.ENFORCE_PAYMETHOD, PayU.ENFORCE_PAYMETHOD);
                switch (id) {
                    /***TODO: Comment was added below to disable stored cards*****/
                    /*case R.id.stored_cards:
                        StoredCardFrag storedCardFragment = new StoredCardFrag();
                        storedCardFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, storedCardFragment, "StoredCardsFragment").addToBackStack(null).commit();
                        break;*/
                    /***TODO: Comment was added above to disable stored cards*****/
                    case R.id.card_payment:
                        CardDetailFragment cardDetailFragment = new CardDetailFragment();
                        cardDetailFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, cardDetailFragment, "CardDetailFragment").addToBackStack(null).commit();
                        break;
                    case R.id.netbanking:
                        NBFragment nbFragment = new NBFragment();
                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, nbFragment, "NBFragment").addToBackStack(null).commit();
                        break;
                }
            }
        }.execute();
    }


    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        bundle.putString(PayU.DROP_CATEGORY, getActivity().getIntent().getExtras().getString(PayU.DROP_CATEGORY));
        bundle.putString(PayU.ENFORCE_PAYMETHOD, getActivity().getIntent().getExtras().getString(PayU.ENFORCE_PAYMETHOD));

        switch (v.getId()) {

            case R.id.paypal:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createOrderPaypal();
                        //cashOnDeliveryClicked();
                    }
                }, 200);
                break;
            case R.id.bankdeposit:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bankDepositClicked();
                        //cashOnDeliveryClicked();
                    }
                }, 200);
                break;
            /***TODO: Comment was added below to disable stored cards*****/
            /*case R.id.stored_cards:
                startPayment(R.id.stored_cards,
                        mAvailablePaymentOptions.getDetails().getItemTotal()
                                + mAvailablePaymentOptions.getDetails().getShippingTotal()
                                + mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditDebitCard().getAdditionalCharge()
                                - mAvailablePaymentOptions.getDetails().getDiscount(),
                        mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditDebitCard().getValue());
                break;*/
            /***TODO: Comment was added above to disable stored cards*****/

            case R.id.card_payment:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPayment(R.id.card_payment,
                                mAvailablePaymentOptions.getDetails().getItemTotal()
                                        + mAvailablePaymentOptions.getDetails().getShippingTotal()
                                        + mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditDebitCard().getAdditionalCharge()
                                        - mAvailablePaymentOptions.getDetails().getDiscount(),
                                mAvailablePaymentOptions.getDetails().getPaymentOptions().getCreditDebitCard().getValue());
                        //cashOnDeliveryClicked();
                    }
                }, 200);
                break;

            case R.id.netbanking:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPayment(R.id.netbanking,
                                mAvailablePaymentOptions.getDetails().getItemTotal()
                                        + mAvailablePaymentOptions.getDetails().getShippingTotal()
                                        + mAvailablePaymentOptions.getDetails().getPaymentOptions().getNetBanking().getAdditionalCharge()
                                        - mAvailablePaymentOptions.getDetails().getDiscount(),
                                mAvailablePaymentOptions.getDetails().getPaymentOptions().getNetBanking().getValue());
                        //cashOnDeliveryClicked();
                    }
                }, 200);
                break;
            case R.id.payuMoney:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startPayment(R.id.payuMoney,
                                mAvailablePaymentOptions.getDetails().getItemTotal()
                                        + mAvailablePaymentOptions.getDetails().getShippingTotal()
                                        + mAvailablePaymentOptions.getDetails().getPaymentOptions().getNetBanking().getAdditionalCharge()
                                        - mAvailablePaymentOptions.getDetails().getDiscount(),
                                mAvailablePaymentOptions.getDetails().getPaymentOptions().getNetBanking().getValue());
                    }
                }, 200);
                break;
            case R.id.creditCard:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createOrderPaypal();
                        //cashOnDeliveryClicked();
                    }
                }, 200);
                break;
            case R.id.cod:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cashOnDeliveryClicked();
                    }
                }, 200);
                break;
            case R.id.cbd:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //cashOnDeliveryClicked();
                        cashBeforeDeliveryClicked();
                    }
                }, 200);
                break;
        }
    }


    private void bankDepositClicked() {
        BankDepositFragment bankDepositFragment = new BankDepositFragment();
        Bundle bundle = new Bundle();
        String availOptionsString = getActivity().getIntent().getStringExtra("AVAILABLE_PAY_OPTIONS");
        bundle.putString("AVAILABLE_PAY_OPTIONS", availOptionsString);
        bankDepositFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, bankDepositFragment, "BankDepositFragment").addToBackStack(null).commit();

        //createOrder(mAvailablePaymentOptions.getDetails().getPaymentOptions().getBankDeposit().getValue());
    }

    private void cashOnDeliveryClicked() {
        CODFragment codFragment = new CODFragment();
        Bundle bundle = new Bundle();
        String availOptionsString = getActivity().getIntent().getStringExtra("AVAILABLE_PAY_OPTIONS");
        bundle.putString("AVAILABLE_PAY_OPTIONS", availOptionsString);
        codFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, codFragment, "CODFragment").addToBackStack(null).commit();

        //createOrder(mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashOnDelivery().getValue());
    }

    private void cashBeforeDeliveryClicked() {
        CBDFragment cbdFragment = new CBDFragment();
        Bundle bundle = new Bundle();
        String availOptionsString = getActivity().getIntent().getStringExtra("AVAILABLE_PAY_OPTIONS");
        bundle.putString("AVAILABLE_PAY_OPTIONS", availOptionsString);
        cbdFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, cbdFragment, "CBDFragment").addToBackStack(null).commit();
        //createOrder(mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashBeforeDelivery().getValue());
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

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject loginResponseJson;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            Logger.v("", "Token: " + getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            Logger.v("", "Device-ID: " + NetworkUtil.getDeviceId(getActivity()));
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
        Intent thankYou = new Intent(getActivity(), ThankYouActivity.class);
        thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (response.getResponseCode() == 200) {
            thankYou.putExtra("response", response.getBody());
            Toast.makeText(getActivity(), "Order Created Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Some Error Ocurred", Toast.LENGTH_SHORT).show();
        }
        startActivity(thankYou);
        getActivity().finish();
    }

    @Override
    public void createOrderFailed(Response response) {
        Toast.makeText(getActivity(), "Some Error Ocurred", Toast.LENGTH_SHORT).show();
        Intent thankYou = new Intent(getActivity(), ThankYouActivity.class);
        thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(thankYou);
        getActivity().finish();
    }

    PaypalCreateOrderAsync mPaypalAsync;

    @Override
    public void createOrderPaypal() {

        mPaypalAsync = new PaypalCreateOrderAsync(this);

        mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.creating_paypal), true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mPaypalAsync != null) {
                    mPaypalAsync.cancel(true);
                }
            }
        });

        int shipping_id = AddressUtil.getShipping_id();
        int billing_id = AddressUtil.getBilling_id();

        if (shipping_id == -1)
            shipping_id = billing_id;

        Logger.v("", "Shipping Id: " + shipping_id);
        Logger.v("", "Billing Id: " + billing_id);

        String url = ApiUrls.API_CREATE_ORDER;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject body = null;
        try {
            body = new JSONObject();
            JSONObject order = new JSONObject();
            order.put("shipping_address", String.valueOf(shipping_id));
            order.put("billing_address", String.valueOf(billing_id));
            order.put("pay_type", mAvailablePaymentOptions.getDetails().getPaymentOptions().getPayPal().getValue());

            body.put("order", order);

            Logger.v("ORDERS", "ORDERS: " + body.toString());

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setHeaders(headerMap).setBodyJson(body).build();
            mPaypalAsync.executeTask(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private CreateOrderPaypal mCreateOrderPaypal;

    @Override
    public void createPaypalOrderSuccessful(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            try {
                Logger.v("PayPal Create Order", "PayPal Create Order Response: " + response.getBody());
                Gson gson = new Gson();
                mCreateOrderPaypal = gson.fromJson(response.getBody(), CreateOrderPaypal.class);
                payPalPayment(getActivity().findViewById(R.id.paypal));
            } catch (Exception e) {
                e.printStackTrace();
                createPaypalOrderFailed(response);
            }
        }

    }

    @Override
    public void createPaypalOrderFailed(Response response) {
        /**TODO: Temporary call for testing**/
        //createPaypalOrderSuccessful(response);
        /**TODO: Temporary call for testing**/

        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (response.getResponseCode() == 0) {
                showSnackBar("No Internet Connection.");
            } else {
                showSnackBar("Failed to create Paypal Order");
            }
        }

    }

    private PaypalSuccessfulResponse mPaypalSuccessfulResponse;
    SendPayPalResponseToServerAsync mSendPayPalResponseToServerAsync;

    private Request mRequest;

    public void verifyOrderPaypal(PaymentConfirmation confirm) {
        Intent verifyPaypalResponse = new Intent(getActivity(), VerifyPaypalResponseActivity.class);
        Bundle b = new Bundle();
        b.putString(VerifyPaypalResponseActivity.PAYPAL_CONFIRM_RESPONSE_KEY, confirm.toJSONObject().toString());
        b.putInt(VerifyPaypalResponseActivity.PAYPAL_ORDER_ID_KEY, mCreateOrderPaypal.getId());
        b.putString(VerifyPaypalResponseActivity.PAYPAL_ORDER_NUMBER, mCreateOrderPaypal.getNumber());
        verifyPaypalResponse.putExtras(b);
        verifyPaypalResponse.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(verifyPaypalResponse);
        getActivity().finish();

    }


    /*PayPal Payment Process starts below*/
    private static final String TAG = "paymentExample";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     * <p>
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;

    // note that these credentials will differ between live & sandbox environments.
    //private static final String CONFIG_CLIENT_ID = "credential from developer.paypal.com";

    /**
     * TODO: PRODUCTION CLIENT ID BELOW "AZ0pCrui9pL8d2lO7R-OsZo_IL59BDjOOjVPkxZO8--SPoRqKp2d4Fc9hhZoletq3C81KSg-BCbIOsbF"
     */
    private static final String CONFIG_CLIENT_ID = "AZ0pCrui9pL8d2lO7R-OsZo_IL59BDjOOjVPkxZO8--SPoRqKp2d4Fc9hhZoletq3C81KSg-BCbIOsbF";

    /**
     * TODO: TEST CLIENT ID BELOW "Ad5Fw_O1vB9Mh4WlT6TNvZiNyNheCd1eZMBvcDr9TcM4m-WuyZdvtlQJEpveqzsEn-OetYbKoz7HIL_U"
     **/
    //private static final String CONFIG_CLIENT_ID = "Ad5Fw_O1vB9Mh4WlT6TNvZiNyNheCd1eZMBvcDr9TcM4m-WuyZdvtlQJEpveqzsEn-OetYbKoz7HIL_U";

    /**
     * TODO: TEST SECRET BELOW "EAET6Q1DMQwoLkL2AmzgSCOL0bw5_Dc3JWXN_c6OStqC4mYwTo4efsW9fBwz7ng_tirRPUFqDet9CaFf"
     */
    //private static final String CONFIG_SECRET = "EAET6Q1DMQwoLkL2AmzgSCOL0bw5_Dc3JWXN_c6OStqC4mYwTo4efsW9fBwz7ng_tirRPUFqDet9CaFf";

    //private static final String CONFIG_EMAIL = "pavitra.sahu@mirraw.com";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .acceptCreditCards(false)
                    //.rememberUser(false)
            /*.defaultUserEmail(CONFIG_EMAIL)*/
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("MIRRAW")//;
            .merchantPrivacyPolicyUri(Uri.parse("http://www.mirraw.com/pages/privacy"))
            .merchantUserAgreementUri(Uri.parse("http://www.mirraw.com/pages/terms"));
            /*.merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));*/


    private void payPalPayment(View v) {
        createMethod();
        onBuyPressed(v);
    }

    private void createMethod() {
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);
    }


    public void onBuyPressed(View pressed) {
        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(getActivity(), com.paypal.android.sdk.payments.PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {

        return new PayPalPayment(new BigDecimal(mCreateOrderPaypal.getPaypalMobileCreateParams().getTotal()),
                mCreateOrderPaypal.getPaypalMobileCreateParams().getCurrencyCode().toString(),
                "Total: ",
                paymentIntent).invoiceNumber("");

        /*return new PayPalPayment(new BigDecimal(mAvailablePaymentOptions.getDetails().getItemTotal()
                + mAvailablePaymentOptions.getDetails().getShippingTotal()
                + mAvailablePaymentOptions.getDetails().getPaymentOptions().getPayPal().getAdditionalCharge()
                - mAvailablePaymentOptions.getDetails().getDiscount()), "USD", "Item Detail",
                paymentIntent).invoiceNumber("");*/
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));

        if (mPaypalAsync != null) {
            mPaypalAsync.cancel(true);
        }
        if (mCreateOrderAsync != null) {
            mCreateOrderAsync.cancel(true);
        }
        if (mSendPayPalResponseToServerAsync != null) {
            mSendPayPalResponseToServerAsync.cancel(true);
        }
        if (mPayuCreateOrderAsync != null) {
            mPayuCreateOrderAsync.cancel(true);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        super.onDestroy();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*PayPal onActivityResult*/
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Logger.i(TAG, "Confirm: " + confirm.toJSONObject().toString(4));
                        Logger.i(TAG, "Payment: " + confirm.getPayment().toJSONObject().toString(4));
                        Logger.i(TAG, "Describe Contents: " + confirm.describeContents());
                        Logger.i(TAG, "Proof od Payment: " + confirm.getProofOfPayment().toJSONObject().toString(4));
                        Logger.i(TAG, "PayPal create Order: " + mCreateOrderPaypal.getId().toString());
                        /**
                         * •••••••••••••
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        /*Toast.makeText(
                                getActivity().getApplicationContext(),
                                "PaymentConfirmation info received from PayPal", Toast.LENGTH_SHORT)
                                .show();*/

                        Logger.v(TAG, "Response PaymentConfirmation info received from PayPal");
                        verifyOrderPaypal(confirm);

                    } catch (JSONException e) {
                        Logger.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Logger.i(TAG, "The user canceled.");
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                Logger.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Logger.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Logger.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Future Payment code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Logger.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Logger.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Logger.i(
                        "FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_PROFILE_SHARING) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth =
                        data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Logger.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Logger.i("ProfileSharingExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "Profile Sharing code received from PayPal", Toast.LENGTH_LONG)
                                .show();

                    } catch (JSONException e) {
                        Logger.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Logger.i("ProfileSharingExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Logger.i(
                        "ProfileSharingExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }


    /**
     * TODO: NEW PAYU INTEGRATION CODE BELOW
     ***/


    /*private void showCardFragment(String pay_type) {
        //fetchPayuHash(pay_type);
        getHashFromServer();
    }


    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;

    private void getHashFromServer() {
        mPaymentParams = new PaymentParams();
        payuConfig = new PayuConfig();

        mPaymentParams.setKey("gtKFFx");
        mPaymentParams.setAmount("10.0");
        mPaymentParams.setProductInfo("Ethnic Wear");
        mPaymentParams.setFirstName("");
        mPaymentParams.setEmail("");
        mPaymentParams.setTxnId("M800773556");
        mPaymentParams.setSurl("");
        mPaymentParams.setFurl("");
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");
        payuConfig.setEnvironment(PayuConstants.MOBILE_STAGING_ENV);
    }


    private NewPayuCreateOrderAsync mNewPayuCreateOrderAsync;

    public void fetchPayuHash(String pay_type) {
        int shipping_id = getShippingAddressId();
        int billing_id = getBillingAddressId();

        if (shipping_id == -1)
            shipping_id = billing_id;

        Logger.v("", "Shipping Id: " + shipping_id);
        Logger.v("", "Billing Id: " + billing_id);

        String url = ApiUrls.API_CREATE_ORDER;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject loginResponseJson;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            Logger.v("", "Token: " + getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            Logger.v("", "Device-ID: " + NetworkUtil.getDeviceId(getActivity()));
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
            mNewPayuCreateOrderAsync = new NewPayuCreateOrderAsync(this);
            mNewPayuCreateOrderAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newPayuCreatePayuOrderSuccessful(Response response) {

    }

    @Override
    public void newPayuCreatePayuOrderFailed(Response response) {

    }
*/
}
