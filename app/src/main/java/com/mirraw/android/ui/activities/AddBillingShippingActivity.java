package com.mirraw.android.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.AddBillingAddressAsync;
import com.mirraw.android.constants.AddressRegex;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.models.address.AddressArray;
import com.mirraw.android.models.address.AddressErrors;
import com.mirraw.android.models.address.Country;
import com.mirraw.android.models.address.ErrorsResult;
import com.mirraw.android.models.address.State;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pavitra on 18/8/15.
 */
public class AddBillingShippingActivity extends AnimationActivity implements
        View.OnClickListener,
        AddBillingAddressAsync.AddressLoader,
        View.OnFocusChangeListener, RippleView.OnRippleCompleteListener {

    private SharedPreferencesManager mAppSharedPrefs;

    private CheckBox mShipToSameAddress;
    private Button mSaveButton;
    RippleView mRetryButtonRippleContainer;
    private EditText mNameBill, mCountryBill, mStateBill, mCityBill, mPinBill, mAddressBill, mMobileBill;
    private EditText mNameShip, mCountryShip, mStateShip, mCityShip, mPinShip, mAddressShip, mMobileShip;
    private LinearLayout mNoInternetLL, mShipAddressLayout, mBillAddressLayout;
    private ProgressWheel mProgressWheel;

    final int mCOUNTRY_BILL_ACTIVITY = 10;
    final int mSTATE_BILL_ACTIVITY = 5;

    final int mCOUNTRY_SHIP_ACTIVITY = 100;
    final int mSTATE_SHIP_ACTIVITY = 50;

    int mCountryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_ship_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAppSharedPrefs = new SharedPreferencesManager(this);

        initBillingLayout();
        initShippingLayout();
        initOtherViews();
    }

    private void initOtherViews() {
        mShipToSameAddress = (CheckBox) findViewById(R.id.shipToSameAddressCheckBox);
        mShipToSameAddress.setChecked(true);
        mShipToSameAddress.setOnClickListener(this);

        ((RippleView) findViewById(R.id.save_button_ripple_container)).setOnRippleCompleteListener(this);

        initProgressWheel();
        initNoInternetView();
    }

    private void initBillingLayout() {
        mBillAddressLayout = (LinearLayout) findViewById(R.id.bill_layout);

        mNameBill = (EditText) findViewById(R.id.nameBillEditText);
        //mLandmarkBill = (EditText) findViewById(R.id.landmarkBillEditText);
        mCountryBill = (EditText) findViewById(R.id.countryBillEditText);
        mStateBill = (EditText) findViewById(R.id.stateBillEditText);
        mCityBill = (EditText) findViewById(R.id.cityBillEditText);
        mPinBill = (EditText) findViewById(R.id.pinBillEditText);
        mAddressBill = (EditText) findViewById(R.id.addressBillEditText);
        mMobileBill = (EditText) findViewById(R.id.mobileBillEditText);

        mCountryBill.setOnClickListener(this);
        mCountryBill.setOnFocusChangeListener(this);
    }

    private void initShippingLayout() {
        mShipAddressLayout = (LinearLayout) findViewById(R.id.ship_layout);
        mShipAddressLayout.setVisibility(View.GONE);

        mNameShip = (EditText) findViewById(R.id.nameShipEditText);
        //mLandmarkShip = (EditText) findViewById(R.id.landmarkShipEditText);
        mCountryShip = (EditText) findViewById(R.id.countryShipEditText);
        mStateShip = (EditText) findViewById(R.id.stateShipEditText);
        mCityShip = (EditText) findViewById(R.id.cityShipEditText);
        mPinShip = (EditText) findViewById(R.id.pinShipEditText);
        mAddressShip = (EditText) findViewById(R.id.addressShipEditText);
        mMobileShip = (EditText) findViewById(R.id.mobileShipEditText);

        mCountryShip.setOnClickListener(this);
        mCountryShip.setOnFocusChangeListener(this);
    }

    private void initProgressWheel() {
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        mProgressWheel.setVisibility(View.GONE);
    }

    private void initNoInternetView() {
        mNoInternetLL = (LinearLayout) findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        mRetryButtonRippleContainer = ((RippleView) findViewById(R.id.retry_button_ripple_container));
        mRetryButtonRippleContainer.setOnRippleCompleteListener(this);
    }


    private String mJsonAddress;
    private ArrayList<Address> mAddressArray;
    private AddressArray mAddressArrayObject;
    private Address mAddressObject1, mAddressObject2;

    private void getAddressFromSharedPreference() {
        mJsonAddress = mAppSharedPrefs.getAddresses();

        mAddressArray = new ArrayList<Address>();
        if (mJsonAddress.equals("")) {
            mAddressArrayObject = new AddressArray();
        } else {
            mAddressArrayObject = new Gson().fromJson(mJsonAddress, AddressArray.class);
            mAddressArray = mAddressArrayObject.getAddresses();
        }
    }

    private void saveAddressSameBillShip() {
        getAddressFromSharedPreference();

        mAddressObject1 = new Address();

        mAddressObject1.setName(mNameBill.getText().toString().trim());
        //mAddressObject1.setLandmark(mLandmarkBill.getText().toString());
        mAddressObject1.setLandmark("");
        mAddressObject1.setCountry(mCountryBill.getText().toString().trim());
        mAddressObject1.setState(mStateBill.getText().toString().trim());
        mAddressObject1.setCity(mCityBill.getText().toString().trim());
        mAddressObject1.setPincode(mPinBill.getText().toString().trim());
        mAddressObject1.setStreetAddress(mAddressBill.getText().toString().trim());
        mAddressObject1.setPhone(mMobileBill.getText().toString().trim());
        mAddressObject1.setDefault(1);

        //Mark all shipAddressesStatus and billAddressesStatus as 'False'
        for (int i = 0; i < mAddressArray.size(); i++) {
            mAddressArray.get(i).setShipAddressStatus(false);
            mAddressArray.get(i).setBillAddressStatus(false);
        }

        mAddressObject1.setShipAddressStatus(true);
        mAddressObject1.setBillAddressStatus(true);

        syncAddressWithServer();
    }


    private void saveAddressDifferentBillShip() {
        //SAVE BILLING ADDRESS
        getAddressFromSharedPreference();

        mAddressObject1 = new Address();

        mAddressObject1.setName(mNameBill.getText().toString().trim());
        /*mAddressObject1.setLandmark(mLandmarkBill.getText().toString());*/
        mAddressObject1.setLandmark("");
        mAddressObject1.setCountry(mCountryBill.getText().toString().trim());
        mAddressObject1.setState(mStateBill.getText().toString().trim());
        mAddressObject1.setCity(mCityBill.getText().toString().trim());
        mAddressObject1.setPincode(mPinBill.getText().toString().trim());
        mAddressObject1.setStreetAddress(mAddressBill.getText().toString().trim());
        mAddressObject1.setPhone(mMobileBill.getText().toString().trim());
        mAddressObject1.setDefault(1);

        //Mark all billAddressesStatus as 'False'
        for (int i = 0; i < mAddressArray.size(); i++) {
            mAddressArray.get(i).setBillAddressStatus(false);
        }

        mAddressObject1.setBillAddressStatus(true);

        //SAVE SHIPPING ADDRESS
        mAddressObject2 = new Address();

        mAddressObject2.setName(mNameShip.getText().toString().trim());
        //mAddressObject2.setLandmark(mLandmarkShip.getText().toString());
        mAddressObject2.setLandmark("");
        mAddressObject2.setCountry(mCountryShip.getText().toString().trim());
        mAddressObject2.setState(mStateShip.getText().toString().trim());
        mAddressObject2.setCity(mCityShip.getText().toString().trim());
        mAddressObject2.setPincode(mPinShip.getText().toString().trim());
        mAddressObject2.setStreetAddress(mAddressShip.getText().toString().trim());
        mAddressObject2.setPhone(mMobileShip.getText().toString().trim());

        //Mark all shipAddressesStatus as 'False'
        for (int i = 0; i < mAddressArray.size(); i++) {
            mAddressArray.get(i).setShipAddressStatus(false);
        }

        mAddressObject2.setShipAddressStatus(true);

        syncAddressWithServer();
    }


    public void syncAddressWithServer() {
        if (mShipToSameAddress.isChecked()) {
            //saveAddressSameBillShip();
            //if (!(mBillingAddressAdded && mShippingAddressAdded))
            if (validBillAddress())
                syncOneAdress();
        } else {
            //saveAddressDifferentBillShip();
            //if (mBillingAddressAdded && !mShippingAddressAdded) {
            //firstAddressSaved = true;
            //saveSecondAddress();
            // } else if (!mBillingAddressAdded && !mShippingAddressAdded) {
            if (validBillAddress() && validShipAddress())
                syncDifferentAddresses();
            //}
        }
    }

    private boolean validBillAddress() {
        if (mNameBill.getText().toString().trim().length() > 0 && regexValidation(mNameBill.getText().toString().trim(), AddressRegex.NAME)) {
            if (mCountryBill.getText().toString().trim().length() > 0 && regexValidation(mCountryBill.getText().toString().trim(), AddressRegex.COUNTRY)) {
                if (mStateBill.getText().toString().trim().length() > 0 && regexValidation(mStateBill.getText().toString().trim(), AddressRegex.STATE)) {
                    if (mCityBill.getText().toString().trim().length() > 0 && regexValidation(mCityBill.getText().toString().trim(), AddressRegex.CITY)) {
                        if (mPinBill.getText().toString().trim().length() > 0 && regexValidation(mPinBill.getText().toString().trim(), AddressRegex.PINCODE)) {
                            if (mAddressBill.getText().toString().trim().length() > 0 && regexValidation(mAddressBill.getText().toString().trim(), AddressRegex.STREET)) {
                                if (!(mMobileBill.getText().toString().trim().length() >= 8) || !regexValidation(mMobileBill.getText().toString().trim(), AddressRegex.MOBILE)) {
                                    showSnackBar("Add Proper Billing Mobile Number.");
                                    mMobileBill.requestFocus();
                                    return false;
                                } else {
                                    return true;
                                }
                            } else {
                                showSnackBar("Add Proper Billing Street Address.");
                                mAddressBill.requestFocus();
                                return false;
                            }
                        } else {
                            if (mPinBill.getText().toString().trim().length() == 0) {
                                TextInputLayout pinTextInputLL = (TextInputLayout) findViewById(R.id.pinCodeBillTextInputLayout);
                                pinTextInputLL.setErrorEnabled(true);
                                pinTextInputLL.setError(getString(R.string.pinCodeBlankError));
                            } else {
                                showSnackBar("Add Proper Billing Pincode.");
                                mPinBill.requestFocus();
                            }
                            return false;
                        }
                    } else {
                        showSnackBar("Add Proper Billing City.");
                        mCityBill.requestFocus();
                        return false;
                    }
                } else {
                    showSnackBar("Add Proper Billing State.");
                    mStateBill.requestFocus();
                    return false;
                }
            } else {
                showSnackBar("Add Proper Billing Country.");
                mCountryBill.requestFocus();
                return false;
            }
        } else {
            showSnackBar("Add Proper Billing Name.");
            mNameBill.requestFocus();
            return false;
        }
    }


    private boolean validShipAddress() {
        if (mNameShip.getText().toString().trim().length() > 0 && regexValidation(mNameShip.getText().toString().trim(), AddressRegex.NAME)) {
            if (mCountryShip.getText().toString().trim().length() > 0 && regexValidation(mCountryShip.getText().toString().trim(), AddressRegex.COUNTRY)) {
                if (mStateShip.getText().toString().trim().length() > 0 && regexValidation(mStateShip.getText().toString().trim(), AddressRegex.STATE)) {
                    if (mCityShip.getText().toString().trim().length() > 0 && regexValidation(mCityShip.getText().toString().trim(), AddressRegex.CITY)) {
                        if (mPinShip.getText().toString().trim().length() > 0 && regexValidation(mPinShip.getText().toString().trim(), AddressRegex.PINCODE)) {
                            if (mAddressShip.getText().toString().trim().length() > 0 && regexValidation(mAddressShip.getText().toString().trim(), AddressRegex.STREET)) {
                                if (!(mMobileShip.getText().toString().trim().length() >= 8) || !regexValidation(mMobileShip.getText().toString().trim(), AddressRegex.MOBILE)) {
                                    showSnackBar("Add Proper Shipping Mobile Number.");
                                    mMobileShip.requestFocus();
                                    return false;
                                } else {
                                    return true;
                                }
                            } else {
                                showSnackBar("Add Proper Shipping Street Address.");
                                mAddressShip.requestFocus();
                                return false;
                            }
                        } else {
                            if (mPinShip.getText().toString().trim().length() == 0) {
                                TextInputLayout pinTextInputLL = (TextInputLayout) findViewById(R.id.pinCodeShipTextInputLayout);
                                pinTextInputLL.setErrorEnabled(true);
                                pinTextInputLL.setError(getString(R.string.pinCodeBlankError));
                            } else {
                                showSnackBar("Add Proper Shipping Pincode.");
                                mPinShip.requestFocus();
                            }
                            return false;
                        }
                    } else {
                        showSnackBar("Add Proper Shipping City.");
                        mCityShip.requestFocus();
                        return false;
                    }
                } else {
                    showSnackBar("Add Proper Shipping State.");
                    mStateShip.requestFocus();
                    return false;
                }
            } else {
                showSnackBar("Add Proper Shipping Country.");
                mCountryShip.requestFocus();
                return false;
            }
        } else {
            showSnackBar("Add Proper Shipping Name.");
            mNameShip.requestFocus();
            return false;
        }
    }

    private boolean regexValidation(String text, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    AddBillingAddressAsync mOneAddAddressAsync, mDiffAddAddressAsync, mDiff2AddAddressAsync;
    private ProgressDialog mProgressDialog;

    @Override
    public void syncOneAdress() {
        if (mOneAddAddressAsync != null &&
                (mOneAddAddressAsync.getStatus() == AsyncTask.Status.PENDING || mOneAddAddressAsync.getStatus() == AsyncTask.Status.RUNNING)) {
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.saving_addresses));
            mProgressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mOneAddAddressAsync != null) {
                        mOneAddAddressAsync.cancel(true);
                    }
                }
            });
            mProgressDialog.show();

            String url = ApiUrls.API_ADD_ADDRESS;

            HashMap<String, String> body = new HashMap<>();

            body.put("name", mAddressObject1.getName());
            body.put("street_address", mAddressObject1.getStreetAddress());
            body.put("city", mAddressObject1.getCity());
            body.put("state", mAddressObject1.getState());
            body.put("country", mAddressObject1.getCountry());
            body.put("pincode", mAddressObject1.getPincode());
            body.put("phone", mAddressObject1.getPhone());
            body.put("default", String.valueOf(1));

            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
            JSONObject head;
            HashMap<String, String> headerMap = new HashMap<>();
            try {
                head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
                headerMap.put(Headers.TOKEN, getString(R.string.token));
                headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
                headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
                headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
                headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
                headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));

                Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBody(body).setHeaders(headerMap).build();
                mOneAddAddressAsync = new AddBillingAddressAsync(this, 1, this);
                mOneAddAddressAsync.executeTask(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static final int SYNC_ONE_ADDRESS = 1, SYNC_DIFFERENT_ADDRESS = 2;

    @Override
    public void syncDifferentAddresses() {
        if (mDiffAddAddressAsync != null &&
                (mDiffAddAddressAsync.getStatus() == AsyncTask.Status.PENDING || mDiffAddAddressAsync.getStatus() == AsyncTask.Status.RUNNING)) {
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.saving_addresses));
            mProgressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mDiffAddAddressAsync != null) {
                        mDiffAddAddressAsync.cancel(true);
                    }
                }
            });
            mProgressDialog.show();

            String url = ApiUrls.API_ADD_ADDRESS;

            HashMap<String, String> body = new HashMap<>();

            body.put("name", mAddressObject1.getName());
            body.put("street_address", mAddressObject1.getStreetAddress());
            body.put("landmark", "");
            body.put("city", mAddressObject1.getCity());
            body.put("state", mAddressObject1.getState());
            body.put("country", mAddressObject1.getCountry());
            body.put("pincode", mAddressObject1.getPincode());
            body.put("phone", mAddressObject1.getPhone());
            body.put("default", String.valueOf(1));

            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
            JSONObject head;
            HashMap<String, String> headerMap = new HashMap<String, String>();
            try {
                head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
                headerMap.put(Headers.TOKEN, getString(R.string.token));
                headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
                headerMap.put(Headers.CLIENT, head.getJSONArray("Client").get(0).toString());
                headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray("Token-Type").get(0).toString());
                headerMap.put(Headers.UID, head.getJSONArray("Uid").get(0).toString());
                headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));

                Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBody(body).setHeaders(headerMap).build();
                mDiffAddAddressAsync = new AddBillingAddressAsync(this, SYNC_DIFFERENT_ADDRESS, this);
                mDiffAddAddressAsync.executeTask(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onEmptyAddress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onOneAddAddressLoaded(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        String jsonAddressArray = "";
        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try {
                mOneAddAddressAsync = null;
                JSONObject json = new JSONObject(response.getBody());
                mAddressObject1.setId(json.getInt("id"));
                mAddressArray.add(mAddressObject1);
                mAddressArrayObject.setAddresses(mAddressArray);
                jsonAddressArray = new Gson().toJson(mAddressArrayObject);
                mAppSharedPrefs.clearAddresses();
                mAppSharedPrefs.setAddresses(jsonAddressArray);

                Intent intent = new Intent(this, ConfirmAddressActivity.class);
                intent.putExtra("firstAddress", true);
                startActivity(intent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (response.getResponseCode() == 0) {
            showSnackBar(getString(R.string.no_internet));

        } else {
            try {
                Gson gson = new Gson();
                StringBuilder mesg = new StringBuilder();
                mesg.append("");
                List<String> errorList;

                String strResponse = response.getBody();

                ErrorsResult errorsResult = gson.fromJson(strResponse, ErrorsResult.class);

                AddressErrors addressErrors = errorsResult.getAddressErrors();

                if (addressErrors.getName().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getName();
                    mesg.append("Name " + errorList.get(0));
                }
                if (addressErrors.getStreetAddress().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getStreetAddress();
                    mesg.append(" Address " + errorList.get(0));
                }
                if (addressErrors.getCity().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getCity();
                    mesg.append(" City " + errorList.get(0));
                }
                if (addressErrors.getCountry().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getCountry();
                    mesg.append(" Country " + errorList.get(0));
                }
                if (addressErrors.getPhone().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getPhone();
                    mesg.append(" Phone " + errorList.get(0));
                }
                if (addressErrors.getState().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getState();
                    mesg.append(" State " + errorList.get(0));
                }
                if (addressErrors.getPincode().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getPincode();
                    mesg.append(" Pincode " + errorList.get(0));
                }
                Utils.showSnackBar(mesg.toString(), this, Snackbar.LENGTH_SHORT);
            } catch (Exception e) {
                Utils.showSnackBar(getString(R.string.problem_loading_data), this, Snackbar.LENGTH_SHORT);
            }
        }
    }


    private boolean firstAddressSaved = false;

    @Override
    public void onDifferentAddAddressLoaded(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        String jsonAddressArray = "";
        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try {
                if (!firstAddressSaved) {
                    mDiffAddAddressAsync = null;
                    JSONObject json = new JSONObject(response.getBody());
                    mAddressObject1.setId(json.getInt("id"));
                    mAddressArray.add(mAddressObject1);
                    mAddressArrayObject.setAddresses(mAddressArray);
                    jsonAddressArray = new Gson().toJson(mAddressArrayObject);
                    mAppSharedPrefs.clearAddresses();
                    mAppSharedPrefs.setAddresses(jsonAddressArray);
                    firstAddressSaved = true;
                    saveSecondAddress();
                } else {
                    mDiff2AddAddressAsync = null;
                    JSONObject json = new JSONObject(response.getBody());
                    mAddressObject2.setId(json.getInt("id"));
                    mAddressArray.add(mAddressObject2);
                    mAddressArrayObject.setAddresses(mAddressArray);
                    jsonAddressArray = new Gson().toJson(mAddressArrayObject);
                    mAppSharedPrefs.clearAddresses();
                    mAppSharedPrefs.setAddresses(jsonAddressArray);

                    Intent intent = new Intent(this, ConfirmAddressActivity.class);
                    //intent.putExtra("FREE_ORDER", getIntent().getBooleanExtra("FREE_ORDER", false));
                    intent.putExtra("firstAddress", true);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (response.getResponseCode() == 0) {
            if (firstAddressSaved) {
                saveSecondAddress();
            } else {
                syncDifferentAddresses();
            }
        } else {
            try {
                Gson gson = new Gson();
                StringBuilder mesg = new StringBuilder();
                mesg.append("");
                List<String> errorList;

                String strResponse = response.getBody();

                ErrorsResult errorsResult = gson.fromJson(strResponse, ErrorsResult.class);

                AddressErrors addressErrors = errorsResult.getAddressErrors();

                if (addressErrors.getName().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getName();
                    mesg.append("Name " + errorList.get(0) + "\n");
                } else if (addressErrors.getStreetAddress().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getStreetAddress();
                    mesg.append(" Address " + errorList.get(0) + "\n");
                } else if (addressErrors.getCity().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getCity();
                    mesg.append(" City " + errorList.get(0) + "\n");
                } else if (addressErrors.getCountry().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getCountry();
                    mesg.append(" Country " + errorList.get(0) + "\n");
                } else if (addressErrors.getPhone().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getPhone();
                    mesg.append(" Phone " + errorList.get(0) + "\n");
                } else if (addressErrors.getState().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getState();
                    mesg.append(" State " + errorList.get(0) + "\n");
                } else if (addressErrors.getPincode().size() != 0) {
                    errorList = errorsResult.getAddressErrors().getPincode();
                    mesg.append(" Pincode " + errorList.get(0) + "\n");
                }
                Utils.showSnackBar(mesg.toString(), this, Snackbar.LENGTH_SHORT);
            } catch (Exception e) {
                Utils.showSnackBar(getString(R.string.problem_loading_data), this, Snackbar.LENGTH_SHORT);
            }
        }
    }

    @Override
    public void saveSecondAddress() {
        if (mDiff2AddAddressAsync != null &&
                (mDiff2AddAddressAsync.getStatus() == AsyncTask.Status.PENDING || mDiff2AddAddressAsync.getStatus() == AsyncTask.Status.RUNNING)) {
        } else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.saving_addresses));
            mProgressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (mDiff2AddAddressAsync != null) {
                        mDiff2AddAddressAsync.cancel(true);
                    }
                }
            });
            mProgressDialog.show();

            String url = ApiUrls.API_ADD_ADDRESS;

            HashMap<String, String> body = new HashMap<String, String>();

            body.put("name", mAddressObject2.getName());
            body.put("street_address", mAddressObject2.getStreetAddress());
            //body.put("landmark", mAddressObject2.getLandmark());
            body.put("landmark", "");
            body.put("city", mAddressObject2.getCity());
            body.put("state", mAddressObject2.getState());
            body.put("country", mAddressObject2.getCountry());
            body.put("pincode", mAddressObject2.getPincode());
            body.put("phone", mAddressObject2.getPhone());

            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
            JSONObject head;
            HashMap<String, String> headerMap = new HashMap<String, String>();
            try {
                head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
                headerMap.put(Headers.TOKEN, getString(R.string.token));
                headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray("Access-Token").get(0).toString());
                headerMap.put(Headers.CLIENT, head.getJSONArray("Client").get(0).toString());
                headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray("Token-Type").get(0).toString());
                headerMap.put(Headers.UID, head.getJSONArray("Uid").get(0).toString());
                headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));

                Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBody(body).setHeaders(headerMap).build();
                mDiff2AddAddressAsync = new AddBillingAddressAsync(this, SYNC_DIFFERENT_ADDRESS, this);
                mDiff2AddAddressAsync.executeTask(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {

            case R.id.shipToSameAddressCheckBox:
                if (!mShipToSameAddress.isChecked()) {
                    mShipAddressLayout.setVisibility(View.VISIBLE);
                    final ScrollView scrollView = (ScrollView) findViewById(R.id.address_layout_scroll);

                    scrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Logger.v("", "X: " + (int) mShipAddressLayout.getX() + "Y: " + (int) mBillAddressLayout.getBottom());
                            scrollView.smoothScrollTo((int) mShipAddressLayout.getX(), (int) mBillAddressLayout.getBottom());
                            mNameShip.requestFocus();
                        }
                    }, 100);


                } else {
                    mShipAddressLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.countryBillEditText:
                i = new Intent(this, SelectCountryActivity.class);
                startActivityForResult(i, mCOUNTRY_BILL_ACTIVITY);
                break;
            case R.id.stateBillEditText:
                i = new Intent(this, SelectStateActivity.class);
                i.putExtra("countryId", mCountryId);
                startActivityForResult(i, mSTATE_BILL_ACTIVITY);
                break;
            case R.id.countryShipEditText:
                i = new Intent(this, SelectCountryActivity.class);
                startActivityForResult(i, mCOUNTRY_SHIP_ACTIVITY);
                break;
            case R.id.stateShipEditText:
                i = new Intent(this, SelectStateActivity.class);
                i.putExtra("countryId", mCountryId);
                startActivityForResult(i, mSTATE_SHIP_ACTIVITY);
                break;
        }
    }

    private void showSnackBar(String msg) {

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == mCOUNTRY_BILL_ACTIVITY) {
                String countryDetails = data.getStringExtra("countryDetails");
                Gson gson = new Gson();
                Country country = gson.fromJson(countryDetails, Country.class);
                mCountryBill.setText(country.getName());
                if (country.getHasStates() == true) //left for checking if the country has states else show user an edit text.
                {
                    Intent i = new Intent(this, SelectStateActivity.class);
                    mCountryId = country.getId();
                    i.putExtra("countryId", mCountryId);
                    startActivityForResult(i, mSTATE_BILL_ACTIVITY);
                    initStateView();
                } else {

                    if (mStateBill == null) {
                        mStateBill = (EditText) findViewById(R.id.stateBillEditText);
                    }

                    mStateBill.setText("");

                    mStateBill.setOnFocusChangeListener(null);
                    mStateBill.requestFocus();


                }
            }

            if (requestCode == mSTATE_BILL_ACTIVITY) {

                mStateBill.setOnFocusChangeListener(this);
                String stateDetails = data.getStringExtra("state_details");
                Gson gson = new Gson();
                State state = gson.fromJson(stateDetails, State.class);

                mStateBill.setText(state.getName());
                if (mCityBill == null) {
                    mCityBill = (EditText) findViewById(R.id.cityBillEditText);
                }

                mCityBill.setText("");
                mCityBill.requestFocus();
            }

            if (requestCode == mCOUNTRY_SHIP_ACTIVITY) {
                String countryDetails = data.getStringExtra("countryDetails");
                Gson gson = new Gson();
                Country country = gson.fromJson(countryDetails, Country.class);
                mCountryShip.setText(country.getName());
                if (country.getHasStates() == true) //left for checking if the country has states else show user an edit text.
                {
                    Intent i = new Intent(this, SelectStateActivity.class);
                    mCountryId = country.getId();
                    i.putExtra("countryId", mCountryId);
                    startActivityForResult(i, mSTATE_SHIP_ACTIVITY);
                    initStateView();
                } else {

                    if (mStateShip == null) {
                        mStateShip = (EditText) findViewById(R.id.stateShipEditText);
                    }

                    mStateShip.setText("");

                    mStateShip.setOnFocusChangeListener(null);
                    mStateShip.requestFocus();
                }
            }

            if (requestCode == mSTATE_SHIP_ACTIVITY) {

                mStateShip.setOnFocusChangeListener(this);
                String stateDetails = data.getStringExtra("state_details");
                Gson gson = new Gson();
                State state = gson.fromJson(stateDetails, State.class);

                mStateShip.setText(state.getName());
                if (mCityShip == null) {
                    mCityShip = (EditText) findViewById(R.id.cityShipEditText);
                }

                mCityShip.setText("");
                mCityShip.requestFocus();
            }


        }
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == mSTATE_BILL_ACTIVITY) {
                mStateBill.setText(null);
            }
            if (requestCode == mSTATE_SHIP_ACTIVITY) {
                mStateShip.setText(null);
            }

        }
    }


    private void initStateView() {
        mStateBill = (EditText) findViewById(R.id.stateBillEditText);
        mStateBill.setOnFocusChangeListener(this);

        mStateShip = (EditText) findViewById(R.id.stateShipEditText);
        mStateShip.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        Intent i;

        if (b == true) {
            switch (view.getId()) {


                case R.id.countryBillEditText:
                    i = new Intent(this, SelectCountryActivity.class);
                    startActivityForResult(i, mCOUNTRY_BILL_ACTIVITY);
                    break;
                case R.id.stateBillEditText:
                    i = new Intent(this, SelectStateActivity.class);
                    i.putExtra("countryId", mCountryId);
                    startActivityForResult(i, mSTATE_BILL_ACTIVITY);
                    break;

                case R.id.countryShipEditText:
                    i = new Intent(this, SelectCountryActivity.class);
                    startActivityForResult(i, mCOUNTRY_SHIP_ACTIVITY);
                    break;
                case R.id.stateShipEditText:
                    i = new Intent(this, SelectStateActivity.class);
                    i.putExtra("countryId", mCountryId);
                    startActivityForResult(i, mSTATE_SHIP_ACTIVITY);
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        if (mOneAddAddressAsync != null) {
            mOneAddAddressAsync.cancel(true);
        }
        if (mDiffAddAddressAsync != null) {
            mDiffAddAddressAsync.cancel(true);
        }
        if (mDiff2AddAddressAsync != null) {
            mDiff2AddAddressAsync.cancel(true);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.save_button_ripple_container:
                if (!mShipToSameAddress.isChecked()) {
                    //if (billingAddressValid() && shippingAddressValid()) {
                    saveAddressDifferentBillShip();
                    //}
                } else {
                    //if (billingAddressValid()) {
                    saveAddressSameBillShip();
                    //}
                }
                //syncAddressWithServer();
                break;
        }


    }
}
