package com.mirraw.android.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.ModifyAddressAsync;
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

public class EditAddressActivity extends AnimationActivity implements
        View.OnClickListener,
        ModifyAddressAsync.ModifyAddress,
        View.OnFocusChangeListener, RippleView.OnRippleCompleteListener {
    final int mCOUNTRY_ACTIVITY = 10;
    final int mSTATE_ACTIVITY = 5;
    TextInputLayout mCountryName;
    EditText mCountry;
    TextInputLayout mStateName;
    EditText mState;
    EditText mCity;
    private ProgressDialog mProgressDialog;
    int mCountryId;

    String mJsonAddress;

    private SharedPreferencesManager mAppSharedPrefs;
    private int mPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPosition = getIntent().getIntExtra("POSITION", 0);
        Logger.v("", "POSITION: " + mPosition);

        mAppSharedPrefs = new SharedPreferencesManager(this);
        initViews();
    }

    private void initViews() {
        initAddressLayout();
        initProgressWheel();
        initNoInternetView();
        initCountryView();
        initCityView();
        initId();
        getAddress();
        if (mAddressArray != null && mAddressArray.size() > 0) {
            populateDetails();
        }
        findViewById(R.id.shipToSameAddressCheckBox).setVisibility(View.GONE);
    }

    private void initCountryView() {
        mCountryName = (TextInputLayout) findViewById(R.id.countryName);
        mCountry = (EditText) findViewById(R.id.country);
        mCountry.setOnFocusChangeListener(this);
        mCountry.setOnClickListener(this);

    }

    private void initStateView() {
        mStateName = (TextInputLayout) findViewById(R.id.stateName);
        mState = (EditText) findViewById(R.id.state);
        mState.setOnFocusChangeListener(this);
    }

    private void initCityView() {
        mCity = (EditText) findViewById(R.id.cityEditText);
    }

    private void initAddressLayout() {
        initButtons();
    }

    private void initButtons() {
        ((RippleView) findViewById(R.id.save_ripple_view)).setOnRippleCompleteListener(this);
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel() {
        mProgressWheel = (ProgressWheel) findViewById(R.id.progressWheel);
    }

    private LinearLayout mNoInternetLL;

    private void initNoInternetView() {
        mNoInternetLL = (LinearLayout) findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        ((RippleView) findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
    }


    private void onRetryButtonClicked() {
        mNoInternetLL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.VISIBLE);
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

    private boolean regexValidation(String text, String regexPattern) {
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    private boolean validBillAddress() {
        if (mName.getText().toString().trim().length() > 0 && regexValidation(mName.getText().toString().trim(), AddressRegex.NAME)) {
            if (mCountry.getText().toString().trim().length() > 0 && regexValidation(mCountry.getText().toString().trim(), AddressRegex.COUNTRY)) {
                if (mState.getText().toString().trim().length() > 0 && regexValidation(mState.getText().toString().trim(), AddressRegex.STATE)) {
                    if (mCity.getText().toString().trim().length() > 0 && regexValidation(mCity.getText().toString().trim(), AddressRegex.CITY)) {
                        if (mPin.getText().toString().trim().length() > 0 && regexValidation(mPin.getText().toString().trim(), AddressRegex.PINCODE)) {
                            if (mAddress.getText().toString().trim().length() > 0 && regexValidation(mAddress.getText().toString().trim(), AddressRegex.STREET)) {
                                if (!(mMobile.getText().toString().trim().length() >= 8) || !regexValidation(mMobile.getText().toString().trim(), AddressRegex.MOBILE)) {
                                    showSnackBar("Add Proper Mobile Number.");
                                    mMobile.requestFocus();
                                    return false;
                                } else {
                                    return true;
                                }
                            } else {
                                showSnackBar("Add Proper Street Address.");
                                mAddress.requestFocus();
                                return false;
                            }
                        } else {
                            if (mPin.getText().toString().trim().length() == 0) {
                                TextInputLayout pinTextInputLL = (TextInputLayout) findViewById(R.id.pinCodeTextInputLayout);
                                pinTextInputLL.setErrorEnabled(true);
                                pinTextInputLL.setError(getString(R.string.pinCodeBlankError));
                            } else {
                                showSnackBar("Add Proper Pincode.");
                                mPin.requestFocus();
                            }
                            return false;
                        }
                    } else {
                        showSnackBar("Add Proper City.");
                        mCity.requestFocus();
                        return false;
                    }
                } else {
                    showSnackBar("Add Proper State.");
                    mState.requestFocus();
                    return false;
                }
            } else {
                showSnackBar("Add Proper Country.");
                mCountry.requestFocus();
                return false;
            }
        } else {
            showSnackBar("Add Proper Name.");
            mName.requestFocus();
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {

            case R.id.saveButton:
                if (validBillAddress())
                    saveAddress();
                break;

            case R.id.country:
                i = new Intent(this, SelectCountryActivity.class);
                startActivityForResult(i, mCOUNTRY_ACTIVITY);
                break;
            case R.id.state:
                i = new Intent(this, SelectStateActivity.class);
                i.putExtra("countryId", mCountryId);
                startActivityForResult(i, mSTATE_ACTIVITY);
                break;
        }
    }

    private Gson mGson = new Gson();
    private AddressArray mAddressArrayObject;
    private Address mAddressObject;
    private ArrayList<Address> mAddressArray;

    private void getAddress() {
        mJsonAddress = mAppSharedPrefs.getAddresses();

        mAddressArray = new ArrayList<>();
        if (mJsonAddress.equals("")) {
            mAddressArrayObject = new AddressArray();
            mAddressObject = new Address();
        } else {
            mAddressArrayObject = mGson.fromJson(mJsonAddress, AddressArray.class);
            mAddressArray = mAddressArrayObject.getAddresses();
            mAddressObject = mAddressArray.get(mPosition);
        }

    }

    String jsonAddressArray;
    private EditText mName;
    private EditText mMobile;
    private EditText mAddress;
    private EditText mPin;

    private void saveAddress() {
        String json = mAppSharedPrefs.getAddresses();
        Gson gson = new Gson();

        if (json.equals("")) {
            mAddressArrayObject = new AddressArray();
        } else {
            mAddressArrayObject = gson.fromJson(json, AddressArray.class);
        }

        initId();

        mAddressObject.setName(mName.getText().toString().trim());
        mAddressObject.setLandmark("");
        mAddressObject.setCountry(mCountry.getText().toString().trim());
        mAddressObject.setState(mState.getText().toString().trim());
        mAddressObject.setCity(mCity.getText().toString().trim());
        mAddressObject.setPincode(mPin.getText().toString().trim());
        mAddressObject.setStreetAddress(mAddress.getText().toString().trim());
        mAddressObject.setPhone(mMobile.getText().toString().trim());

        if (Utils.isNetworkAvailable(this)) {
            modifyAddress();
        } else {
            showSnackBar(getString(R.string.no_internet));
        }
    }

    public void initId() {
        mName = (EditText) findViewById(R.id.nameEditText);
        //mLandmark = (EditText) findViewById(R.id.landmarkEditText);
        mCountry = (EditText) findViewById(R.id.country);
        mState = (EditText) findViewById(R.id.state);
        mPin = (EditText) findViewById(R.id.pinEditText);
        mAddress = (EditText) findViewById(R.id.addressEditText);
        mMobile = (EditText) findViewById(R.id.mobileEditText);
        mCity = (EditText) findViewById(R.id.cityEditText);
    }

    int mId;

    private void populateDetails() {
        findViewById(R.id.shipToSameAddressCheckBox).setVisibility(View.GONE);

        mName = (EditText) findViewById(R.id.nameEditText);
        mName.setText(mAddressArray.get(mPosition).getName());

        mCountry = (EditText) findViewById(R.id.country);
        mCountry.setText(mAddressArray.get(mPosition).getCountry());

        mState = (EditText) findViewById(R.id.state);
        mState.setText(mAddressArray.get(mPosition).getState());

        mPin = (EditText) findViewById(R.id.pinEditText);
        mPin.setText(mAddressArray.get(mPosition).getPincode());

        mAddress = (EditText) findViewById(R.id.addressEditText);
        mAddress.setText(mAddressArray.get(mPosition).getStreetAddress());

        mMobile = (EditText) findViewById(R.id.mobileEditText);
        mMobile.setText(mAddressArray.get(mPosition).getPhone());

        EditText city = (EditText) findViewById(R.id.cityEditText);
        city.setText(mAddressArray.get(mPosition).getCity());
        mId = mAddressArray.get(mPosition).getId();
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
            if (requestCode == mCOUNTRY_ACTIVITY) {
                String countryDetails = data.getStringExtra("countryDetails");
                Gson gson = new Gson();
                Country country = gson.fromJson(countryDetails, Country.class);
                mCountry.setText(country.getName());
                if (country.getHasStates() == true) //left for checking if the country has states else show user an edit text.
                {
                    Intent i = new Intent(this, SelectStateActivity.class);
                    mCountryId = country.getId();
                    i.putExtra("countryId", mCountryId);

                    startActivityForResult(i, mSTATE_ACTIVITY);
                    initStateView();

                } else {

                    if (mState == null) {
                        mState = (EditText) findViewById(R.id.state);
                    }

                    mState.setText("");

                    mState.setOnFocusChangeListener(null);
                    mState.requestFocus();


                }
            }

            if (requestCode == mSTATE_ACTIVITY) {

                mState.setOnFocusChangeListener(this);
                String stateDetails = data.getStringExtra("state_details");
                Gson gson = new Gson();
                State state = gson.fromJson(stateDetails, State.class);

                mState.setText(state.getName());
                if (mCity == null) {
                    mCity = (EditText) findViewById(R.id.cityEditText);
                }

                mCity.setText("");

                mCity.requestFocus();

            }


        }
        if (resultCode == RESULT_CANCELED) {
            if (requestCode == mSTATE_ACTIVITY) {

                mState.setText(null);
            }

        }
    }

    private ModifyAddressAsync mModifyAddressAsync;

    @Override
    public void onModifyAddressSuccess(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try {
                JSONObject json = new JSONObject(response.getBody());
                mAddressObject.setId(json.getInt("id"));
                mAddressObject.setDefault(mAddressArray.get(mPosition).getDefault());
                mAddressObject.setShipAddressStatus(mAddressArray.get(mPosition).getShipAddressStatus());
                Logger.v("", "Address Mast: " + mAddressArray.get(mPosition).getDefault() + mAddressArray.get(mPosition).getShipAddressStatus());
                mAddressArray.remove(mPosition);
                mAddressArray.add(mPosition, mAddressObject);
                Logger.v("", "Address Mast: " + mAddressArray.get(mPosition).getDefault() + mAddressArray.get(mPosition).getShipAddressStatus());
                mAddressArrayObject.setAddresses(mAddressArray);
                jsonAddressArray = mGson.toJson(mAddressArrayObject);
                mAppSharedPrefs.clearAddresses();
                mAppSharedPrefs.setAddresses(jsonAddressArray);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
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
    public void onModifyAddressFailed(Response response) {
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
    public void modifyAddress() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.saving_addresses));
        mProgressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mModifyAddressAsync != null) {
                    mModifyAddressAsync.cancel(true);
                }
            }
        });
        mProgressDialog.show();

        String url = ApiUrls.API_ADD_ADDRESS + "/" + mId;
        HashMap<String, String> body = new HashMap<>();

        body.put("name", mAddressObject.getName());
        body.put("street_address", mAddressObject.getStreetAddress());
        body.put("landmark", "");
        body.put("city", mAddressObject.getCity());
        body.put("state", mAddressObject.getState());
        body.put("country", mAddressObject.getCountry());
        body.put("pincode", mAddressObject.getPincode());
        body.put("phone", mAddressObject.getPhone());
        body.put("default", mAddressObject.getDefault().toString());

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

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.PUT).setBody(body).setHeaders(headerMap).build();
            mModifyAddressAsync = new ModifyAddressAsync(this, this);
            mModifyAddressAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mModifyAddressAsync != null) {
            mModifyAddressAsync.cancel(true);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        Intent i;

        if (b == true) {
            switch (view.getId()) {


                case R.id.country:
                    i = new Intent(this, SelectCountryActivity.class);
                    startActivityForResult(i, mCOUNTRY_ACTIVITY);
                    break;
                case R.id.state:
                    i = new Intent(this, SelectStateActivity.class);
                    i.putExtra("countryId", mCountryId);
                    startActivityForResult(i, mSTATE_ACTIVITY);
                    break;

            }
        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.save_ripple_view:
                if (validBillAddress())
                    saveAddress();
                break;

            case R.id.retry_button_ripple_container:
                onRetryButtonClicked();
                break;
        }

    }
}
