package com.mirraw.android.ui.fragments;


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
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.async.RegisterAsync;
import com.mirraw.android.models.Register.Errors;
import com.mirraw.android.models.Register.RegisterError;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.RegisterSuccessActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nimesh Luhana on 28-07-2015.
 */
public class RegistrationFragment extends android.support.v4.app.Fragment implements RegisterAsync.Registeration, RippleView.OnRippleCompleteListener {


    EditText mName, mEmail, mPassword, mConfirmPassword;
    ProgressDialog mProgressDialog;
    View mView;
    RippleView mRegisterRippleView;
    View.OnClickListener mLoginClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.fragment_register, container, false);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        initViews(view);
        mLoginClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        };

    }

    private void initViews(View view) {
        initName(view);
        initEmail(view);
        initPassword(view);
        initConfirmPassword(view);
        initRegister(view);
        initProgressDialog();


    }


    private void initConfirmPassword(View view) {
        mConfirmPassword = (EditText) view.findViewById(R.id.userConfirmPassword);

    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Registering ...");
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mRegisterAsync != null)
                    mRegisterAsync.cancel(true);
            }
        });

    }

    private void initRegister(View view) {

        mRegisterRippleView = (RippleView) view.findViewById(R.id.register_ripple_view);
        mRegisterRippleView.setOnRippleCompleteListener(this);

    }

    private void initPassword(View view) {
        mPassword = (EditText) view.findViewById(R.id.userPassword);

    }

    private void initEmail(View view) {
        mEmail = (EditText) view.findViewById(R.id.userEmail);
    }

    private void initName(View view) {
        mName = (EditText) view.findViewById(R.id.userName);

    }


    private Boolean validate() {

        if (validateEmail()) {
            if (validatePassword()) {
                if (validateConfirmPassword()) {
                    return true;
                } else {
                    showSnackBar("Password and Confirm Password Fields don't match");
                    mConfirmPassword.requestFocus();
                    return false;

                }

            } else {
                showSnackBar("Enter password with atleast 8 characters");
                mPassword.requestFocus();
                return false;
            }
        } else {
            showSnackBar("Enter proper Email");
            mEmail.requestFocus();
            return false;
        }
    }


    private boolean validateConfirmPassword() {
        if (mPassword.getText().toString().contentEquals(mConfirmPassword.getText().toString())) {
            return true;
        } else
            return false;
    }

    private boolean validatePassword() {
        if (mPassword.getText().toString().trim().length() >= 8)
            return true;
        else
            return false;
    }

    private boolean validateEmail() {

        String emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return mEmail.getText().toString().matches(emailregex);
    }

    private boolean validateName() {
        if (mName.getText().toString().trim().length() == 0)
            return false;
        else
            return true;

    }

    private void showSnackBar(String msg) {

        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }

    RegisterAsync mRegisterAsync;


    @Override
    public void onPreRegister() {

        mProgressDialog.show();
        register();
    }

    @Override
    public void register() {


        HashMap<String, String> body = new HashMap<>();
        // body.put("name", mName.getText().toString());
        body.put("email", mEmail.getText().toString());
        body.put("password", mPassword.getText().toString());
        //body.put("password_confirmation", mConfirmPassword.getText().toString());

        //body.put("confirm_success_url",mName.getText().toString());
        HashMap<String, String> header = new HashMap<>();
        header.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        header.put(Headers.TOKEN, getString(R.string.token));
        Request request = new Request.RequestBuilder(ApiUrls.API_REGISTER, Request.RequestTypeEnum.POST).setBody(body).setHeaders(header).build();
        mRegisterAsync = new RegisterAsync(this);
        mRegisterAsync.execute(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRegisterAsync != null)
            mRegisterAsync.cancel(true);
    }

    @Override
    public void onRegisterLoaded(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (response.getResponseCode() == 200) {
                Toast.makeText(getActivity(), "Registration Successful. You can now login using these credentials", Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else {


                onRegistrationFailed(response);

            }
        }

    }

    @Override
    public void onRegistrationFailed(Response response) {
        if (response.getResponseCode() == 0) {
            showSnackBar("No Internet Connection");
        } else {
            String responseBody = response.getBody();
            Gson gson;
            try {

                gson = new Gson();
                RegisterError registerError = gson.fromJson(responseBody, RegisterError.class);
                Errors errors = registerError.getErrors();
                List<String> error_messages = errors.getFullMessages();
                showSnackBar(error_messages.get(0));
            } catch (Exception e) {
                showSnackBar("Something went wrong");
            }

        }

//
//        String fullMessage = null;
//        try {
//            JSONObject responseBody = new JSONObject(response.getBody());
//            if (responseBody.has("errors")) {
//                if (responseBody.getJSONObject("errors").has("full_messages")) {
//                    fullMessage = responseBody.getJSONObject("errors").getString("full_messages");
//                }
//                //          String errorsBody= (String) responseBody.get("errors");
////                    JSONObject errors=new JSONObject(errorsBody);
////                    String fullMessage= (String) errors.get("full_messages");
//
//                showSnackBar(fullMessage);
//
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.register_ripple_view:
                if (validate()) {
                    onPreRegister();

                }
                break;
        }


    }

//    @Override
//    public void onRegisterationFailed(String body, int responseCode) {
//        try {
//            JSONObject bodyJson = new JSONObject(body);
//            if (bodyJson.has("errors")) {
//                Toast.makeText(getActivity(), bodyJson.toString(), Toast.LENGTH_LONG).show();
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

}
