package com.mirraw.android.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.models.PaymentOptionsDetail.AvailablePaymentOptions;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.models.address.AddressArray;
import com.mirraw.android.ui.fragments.PayOptionsFragment;
import com.payu.sdk.Constants;
import com.payu.sdk.Params;
import com.payu.sdk.PayU;
import com.payu.sdk.Payment;
import com.payu.sdk.PaymentListener;
import com.payu.sdk.ProcessPaymentActivity;
import com.payu.sdk.exceptions.HashException;
import com.payu.sdk.exceptions.MissingParameterException;

import java.util.ArrayList;

/*import android.content.SharedPreferences;
import android.preference.PreferenceManager;*/
//import com.mirraw.android.R;

public class PaymentActivity extends AnimationActivity
        implements
        View.OnClickListener, PaymentListener {

    Bundle extras;
    private String mProductsString;

    private String tag = PaymentActivity.class.getSimpleName();

    private SharedPreferencesManager mAppSharedPrefs;
    private AvailablePaymentOptions mAvailablePaymentOptions;
    private String mCurrencyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppSharedPrefs = new SharedPreferencesManager(this);
        initViews();
        //mProductsString = getIntent().getStringExtra("PRODUCT_LIST");

        if (savedInstanceState == null) {
            PayOptionsFragment fragment = new PayOptionsFragment();
            /*Bundle bundle = new Bundle();
            bundle.putSerializable(PayU.PAYMENT_OPTIONS, getIntent().getExtras().getSerializable(PayU.PAYMENT_OPTIONS));
            fragment.setArguments(bundle);*/
            getSupportFragmentManager().beginTransaction().add(R.id.payment_options_layout, fragment, "paymentOptions").commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        getData();
        initAddressContainer();
        initOrderLayouts();
    }

    private void initOrderLayouts() {
        String availOptionsString = getIntent().getStringExtra("AVAILABLE_PAY_OPTIONS");
        Gson gson = new Gson();
        mAvailablePaymentOptions = (AvailablePaymentOptions) gson.fromJson(availOptionsString, AvailablePaymentOptions.class);
        mCurrencyCode = String.valueOf(Character.toChars((char) Integer.parseInt(mAvailablePaymentOptions.getDetails().getHexSymbol(), 16)));
        initOrderLayout();

    }

    private void initOrderLayout() {
        int totalQty = 0;
        try {
            totalQty = mAvailablePaymentOptions.getDetails().getItemCount();
        } catch (Exception e) {

        }
        TextView totalItems = (TextView) findViewById(R.id.quantity);
        if (totalQty == 1)
            totalItems.setText(totalQty + " Item");
        else
            totalItems.setText(totalQty + " Items");

        TextView orderAmtView = (TextView) findViewById(R.id.total_order_amt);
        TextView shippingAmtView = (TextView) findViewById(R.id.shipping_amt);
        TextView discountAmtView = (TextView) findViewById(R.id.discount_amt);
        TextView totalAmtView = (TextView) findViewById(R.id.total_amt);
        TextView youPay = (TextView) findViewById(R.id.total_payment);


        if (mAvailablePaymentOptions.getDetails().getHexSymbol().equalsIgnoreCase("20B9")) {
            orderAmtView.setText(mCurrencyCode + String.valueOf(mAvailablePaymentOptions.getDetails().getItemTotal().intValue()).trim());
            shippingAmtView.setText(mCurrencyCode + String.valueOf(mAvailablePaymentOptions.getDetails().getShippingTotal().intValue()).trim());
            discountAmtView.setText(mCurrencyCode + String.valueOf(mAvailablePaymentOptions.getDetails().getDiscount().intValue()).trim());
            totalAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal().intValue());
            youPay.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal().intValue());
        } else {
            orderAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getItemTotal().toString().trim());
            shippingAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getShippingTotal().toString().trim());
            discountAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getDiscount().toString().trim());
            totalAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal());
            youPay.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getTotal());
        }

    }

    private void initAddressContainer() {
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(mLatestAddress.getName());

        TextView addr = (TextView) findViewById(R.id.address);
        addr.setText(mLatestAddress.getStreetAddress());

        TextView city = (TextView) findViewById(R.id.city);
        city.setText(mLatestAddress.getCity());

        TextView state = (TextView) findViewById(R.id.state);
        state.setText(mLatestAddress.getState() + " - " + mLatestAddress.getPincode());

        TextView country = (TextView) findViewById(R.id.country);
        country.setText(mLatestAddress.getCountry());

        TextView mobile = (TextView) findViewById(R.id.mobile);
        mobile.setText(" : " + mLatestAddress.getPhone());
    }

    private Address mLatestAddress;

    private void getData() {
        String json = mAppSharedPrefs.getAddresses();

        Gson gson = new Gson();
        AddressArray mAddressArrayObject = gson.fromJson(json, AddressArray.class);
        ArrayList<Address> addressArray = mAddressArrayObject.getAddresses();
        chooseDefaultAddress(addressArray);
    }

    private void chooseDefaultAddress(ArrayList<Address> addressArray) {
        mLatestAddress = addressArray.get(0);

        for (int i = 0; i < addressArray.size(); i++) {
            if (addressArray.get(i).getDefault() == 1) {
                mLatestAddress = addressArray.get(i);
                break;
            }
        }

        for (int i = 0; i < addressArray.size(); i++) {
            if (addressArray.get(i).getShipAddressStatus()) {
                mLatestAddress = addressArray.get(i);
                break;
            }
        }
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPaymentOptionSelected(PayU.PaymentMode paymentMode) {/*
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString(PayU.OFFER_KEY, extras.getString(PayU.OFFER_KEY));
        bundle.putString(PayU.DROP_CATEGORY, extras.getString(PayU.DROP_CATEGORY));
        bundle.putString(PayU.ENFORCE_PAYMETHOD, extras.getString(PayU.ENFORCE_PAYMETHOD));
        bundle.putString(PayU.DISABLE_CUSTOM_BROWSER, extras.getString(PayU.DISABLE_CUSTOM_BROWSER));

        switch (paymentMode) {
            case EMI:
                transaction.replace(com.payu.sdk.R.id.fragmentContainer, new EmiDetailsFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case CC:
                CardsFragment cardsFragment = new CardsFragment();
                cardsFragment.setArguments(bundle);
                transaction.replace(com.payu.sdk.R.id.fragmentContainer, cardsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case NB:
                transaction.replace(com.payu.sdk.R.id.fragmentContainer, new NetBankingFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case STORED_CARDS:
                StoredCardFragment storedCardFragment = new StoredCardFragment();
                storedCardFragment.setArguments(bundle);
                transaction.replace(com.payu.sdk.R.id.fragmentContainer, storedCardFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case PAYU_MONEY:
                *//* open the payment process webview *//*
                try {
                    payuMoney();
                } catch (MissingParameterException e) {
                    e.printStackTrace();
                } catch (HashException e) {
                    e.printStackTrace();
                }
                break;
            case CASH:
                transaction.replace(com.payu.sdk.R.id.fragmentContainer, new CashCardFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            default:
                // default
                break;
        }

    */
    }

    private void payuMoney() throws MissingParameterException, HashException {
        Payment payment;
        Payment.Builder builder = new Payment().new Builder();
        Params requiredParams = new Params();
//        requiredParams.put("service_provider", "payu_paisa");

        builder.set(PayU.MODE, String.valueOf(PayU.PaymentMode.PAYU_MONEY));
        for (String key : getIntent().getExtras().keySet()) {
            builder.set(key, String.valueOf(getIntent().getExtras().get(key)));
            requiredParams.put(key, builder.get(key));
        }

        payment = builder.create();

        String postData = PayU.getInstance(this).createPayment(payment, requiredParams);
        Logger.d("postdata", postData);

        Intent intent = new Intent(this, ProcessPaymentActivity.class);
        intent.putExtra(Constants.POST_DATA, postData);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        startActivityForResult(intent, PayU.RESULT);
    }


    @Override
    public void onGetResponse(String responseMessage) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayU.RESULT) {
            /*setResult(resultCode, data);
            finish();*/
            if (resultCode == RESULT_OK) {
                //success
                if (data != null) {
                    Logger.v("Response", "Response: " + data.getStringExtra("result").toString());
                    //deleteBoughtProductFromCart();
                    Intent thankYou = new Intent(PaymentActivity.this, ThankYouActivity.class);
                    thankYou.putExtra("response", data.getStringExtra("result").toString());
                    thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //thankYou.putExtra("PRODUCT_LIST", getIntent().getStringExtra("PRODUCT_LIST"));
                    startActivity(thankYou);
                    finish();
                    //Toast.makeText(this, "Success" /*+ data.getStringExtra("result")*/, Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //failed
                if (data != null) {
                    Logger.v(tag, "Response Failed: " + data.getStringExtra("result"));
                    /*Intent sorry = new Intent(ConfirmAddressActivity.this, SorryActivity.class);
                    sorry.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(sorry);*/
                    //Toast.makeText(this, "Failed" + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
