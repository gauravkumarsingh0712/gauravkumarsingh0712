package com.mirraw.android.ui.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.LoginAsync;
import com.mirraw.android.models.login.LoginErrors;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.activities.ForgotPasswordActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Nimesh Luhana on 28-07-2015.
 */
public class EmailLoginFragment extends android.support.v4.app.Fragment
        implements View.OnClickListener,
        LoginAsync.LoginLoader, RippleView.OnRippleCompleteListener {


    EditText mEmail, mPassword;
    TextView mForgotPassword;
    RippleView mLoginRippleView;
    LoginAsync mLoginAsync;

    ProgressDialog mProgressDialog;

    Button mRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.fragment_login, container, false);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {

        initEmail(view);
        initPassword(view);
        initLogin(view);
        initForgotPass(view);
    }

    private void initForgotPass(View view) {
        mForgotPassword = (TextView) view.findViewById(R.id.forgotPassword);
        mForgotPassword.setOnClickListener(this);

    }


    private void initLogin(View view) {

        mLoginRippleView = (RippleView) view.findViewById(R.id.login_ripple_view);
        mLoginRippleView.setOnRippleCompleteListener(this);


    }

    private void initPassword(View view) {
        mPassword = (EditText) view.findViewById(R.id.userPassword);

    }

    private void initEmail(View view) {
        mEmail = (EditText) view.findViewById(R.id.userEmail);

    }

    private Boolean validate() {
        if (validateEmail()) {
            if (validatePassword())

            {

            } else {
                showSnackBar("Enter password with atleast 6 characters");
                mPassword.requestFocus();
                return false;
            }
        } else {
            showSnackBar("Enter proper Email");
            mEmail.requestFocus();
            return false;
        }
        return true;
    }


    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();

    }

    private boolean validatePassword() {
        if (mPassword.getText().toString().trim().length() >= 6)
            return true;
        else
            return false;
    }

    private boolean validateEmail() {

        String emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return mEmail.getText().toString().matches(emailregex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loginButton:
                if (validate())
                    startLogin();
                break;
            case R.id.forgotPassword:
                Intent i = new Intent(getActivity(), ForgotPasswordActivity.class);
                getActivity().startActivityForResult(i, ForgotPasswordActivity.mForgotPasswordActivity);
                break;


        }

    }


    @Override
    public void startLogin() {
        mProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Logging in ...", true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mLoginAsync != null) {
                    mLoginAsync.cancel(true);
                }
            }
        });
        //Request.RequestBuilder requestBuilder = Request.new RequestBuilder(ApiUrls.API_LOGIN, Request.RequestTypeEnum.GET);
        String url = ApiUrls.API_LOGIN;// + "?email=" + mEmail.getText().toString() + "&password=" + mPassword.getText().toString();
        HashMap<String, String> body = new HashMap<String, String>();
        body.put("email", mEmail.getText().toString());
        body.put("password", mPassword.getText().toString());

        HashMap<String, String> head = new HashMap<String, String>();
        head.put(Headers.DEVICE_ID, Utils.getDeviceId(getActivity()));
        Logger.v("DEVICE_ID", "DEVICE_ID: " + Utils.getDeviceId(getActivity()));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBody(body).setHeaders(head).build();
        mLoginAsync = new LoginAsync(this);
        mLoginAsync.executeTask(request);
    }

    @Override
    public void onLoginResponseLoaded(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }


            if (response.getResponseCode() == 200) {

                onloginSuccess(response);


            } else {

                onLoginFailed(response);

            }
        }


    }

    @Override
    public void onLoginFailed(Response response) {


        if (response.getResponseCode() == 0)
            showSnackBar("No Internet Connection");
        else {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(response.getBody());
            } catch (JSONException e) {
                e.printStackTrace();
                couldNotGetLoginResponse();
            }
            if (jsonObject.has("errors")) {

                toastLoginError(jsonObject.toString());
            } else
                couldNotGetLoginResponse();
        }
    }

    @Override
    public void couldNotGetLoginResponse() {
        showSnackBar(getString(R.string.something_went_wrong));
    }

    private void toastLoginError(String errors) {
        try {
            Gson gson = new Gson();
            LoginErrors loginErrors;
            loginErrors = gson.fromJson(errors, LoginErrors.class);
            Toast.makeText(getActivity(), loginErrors.getErrors().get(0), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            couldNotGetLoginResponse();
        }

    }

    @Override
    public void onloginSuccess(Response response) {
        try {
            Gson gson = new Gson();
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
            sharedPreferencesManager.setLoginResponse(gson.toJson(response));
            getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra("mailId", mEmail.getText().toString()));
            getActivity().finish();
        } catch (Exception e) {
            showSnackBar(getString(R.string.something_went_wrong));
        }
    }

    @Override
    public void onDestroy() {
        if (mLoginAsync != null) {
            mLoginAsync.cancel(true);
        }

        super.onDestroy();
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.login_ripple_view:
                if (validate())
                    startLogin();
                break;


        }

    }
}
