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

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.async.ForgotPasswordAsync;
import com.mirraw.android.models.ResetPassword.ResetPasswordError;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import java.util.HashMap;

/**
 * Created by Nimesh Luhana on 29-07-2015.
 */
public class ForgotPasswordFragment extends android.support.v4.app.Fragment implements  ForgotPasswordAsync.PasswordResetLoader, RippleView.OnRippleCompleteListener {


    EditText mEmail;
    RippleView mSubmitRippleView;
    ProgressDialog mProgressDialog;
    TextView mInstructionTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.fragment_forgot_password, container, false);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);


    }

    private void initViews(View view) {
        initMailId(view);
        initSubmit(view);
        initInstructionView(view);
        initProgressDialog();

    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(true);
        mProgressDialog.setMessage("Resetting Password ...");
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mForgotPasswordAsync != null)
                    mForgotPasswordAsync.cancel(true);
            }
        });

    }

    private void initInstructionView(View view) {
        mInstructionTextView = (TextView) view.findViewById(R.id.instruction);
        mInstructionTextView.setVisibility(View.VISIBLE);
    }

    private void initSubmit(View view) {
        mSubmitRippleView = (RippleView) view.findViewById(R.id.submit_ripple_view);
        mSubmitRippleView.setOnRippleCompleteListener(this);
    }

    private void initMailId(View view) {

        mEmail = (EditText) view.findViewById(R.id.mailId);


    }

    private boolean validate() {
        if (validateEmail()) {
            return true;
        } else {
            showSnackBar("Enter proper Email");
            mEmail.requestFocus();

        }
        return false;
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }

    private boolean validateEmail() {

        String emailregex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        return mEmail.getText().toString().matches(emailregex);
    }

    ForgotPasswordAsync mForgotPasswordAsync;


    @Override
    public void onPreResetPassword() {
        mProgressDialog.show();

        resetPassword();
    }

    @Override
    public void resetPassword() {


        HashMap<String, String> body = new HashMap<>();
        body.put("email", mEmail.getText().toString());
        HashMap<String, String> header = new HashMap<>();
        header.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        header.put(Headers.TOKEN, getString(R.string.token));
        Request request = new Request.RequestBuilder(ApiUrls.API_RESET_PASS, Request.RequestTypeEnum.POST).setHeaders(header).setBody(body).build();
        mForgotPasswordAsync = new ForgotPasswordAsync(this);
        mForgotPasswordAsync.execute(request);


    }

    @Override
    public void onResetLoaded(Response response) {

        if (isAdded()) {
            if (response.getResponseCode() == 200) {
                getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra("mailId", mEmail.getText().toString()));


                getActivity().finish();
            } else {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                onResetFailed(response);


            }
        }


    }

    @Override
    public void onResetFailed(Response response) {
        if (response.getResponseCode() == 0) {
            showSnackBar("No Internet Connection");
        } else {
            try {
                String responseBody = response.getBody();
                Gson gson = new Gson();
                ResetPasswordError resetPasswordError = gson.fromJson(responseBody, ResetPasswordError.class);
                java.util.List<String> errors = resetPasswordError.getErrors();
                showSnackBar(errors.get(0));
            } catch (Exception e) {
                showSnackBar("Something went wrong");
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mForgotPasswordAsync != null) {
            mForgotPasswordAsync.cancel(true);
        }

        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.submit_ripple_view:
                if (validate()) {
                    onPreResetPassword();

                }
                break;
        }
    }
}
