package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.mirraw.android.sharedresources.Logger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.FacebookServerLoginAsync;
import com.mirraw.android.models.login.SocialLoginErrors;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.EmailLoginActivity;
import com.mirraw.android.ui.activities.RegisterActivity;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by varun on 23/7/15.
 */
public class LoginDialogFragment extends DialogFragment
        implements
        View.OnClickListener, FacebookServerLoginAsync.FacebookServerLoginLoader, RippleView.OnRippleCompleteListener {


    public interface PostFacebookLogin {
        void loadCartCount();

    }

    public interface GoogleLoginProvider {
        public void onGoogleSignIn();


    }

    public interface LoginCallbacks {
        void onLoginSuccess();

        void onLoginFailure();

    }

    PostFacebookLogin mPostFacebookLogin;
    LoginCallbacks mLoginCallbacks;

    public static final String TAG = LoginDialogFragment.class.getSimpleName();
    LoginResult mLoginResult;
    ProgressDialog mProgressDialog;


    @Override
    public void onPreFacebookServerLogin() {
        mProgressDialog.show();
        loadFacebookServerLogin();
    }

    @Override
    public void loadFacebookServerLogin() {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(Headers.ACCESS_TOKEN, mLoginResult.getAccessToken().getToken().toString());
        String url;

        headers.put(Headers.PROVIDER, "facebook");
        url = ApiUrls.API_FACEBOOK_AUTH + "?resource_class=Account";

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(headers).build();


        mFacebookServerLoginAsync.executeTask(request);
    }

    Response mResponse;

    @Override
    public void onFacebookServerLoginComplete(Response response) {
        if (isAdded()) {
            mResponse = response;
            if (response.getResponseCode() == 200) {
                onFacebookServerLoginSuccess(mLoginCallbacks);
                dismissAllowingStateLoss();
            } else
                onFacebookServerLoginFailure();
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            dismissAllowingStateLoss();
        }
    }

    @Override
    public void onFacebookServerLoginSuccess(LoginCallbacks loginCallbacks) {

        mLoginManager.onFacebookLogin(mLoginResult, loginCallbacks);
        Gson gson = new Gson();
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        sharedPreferencesManager.setLoginResponse(gson.toJson(mResponse));
        mPostFacebookLogin.loadCartCount();
    }

    @Override
    public void onFacebookServerLoginFailure() {
        Gson gson = new Gson();
        SocialLoginErrors socialLoginErrors = gson.fromJson(mResponse.getBody(), SocialLoginErrors.class);
        mLoginManager.onFacebookLogOut();
        Toast.makeText(getActivity(), "Login Failed : " + socialLoginErrors.getError(), Toast.LENGTH_LONG).show();
//        Toast.makeText(getActivity(), socialLoginErrors.getError(), Toast.LENGTH_LONG).show();
    }


    private GoogleLoginProvider mGoogleLoginProvider;

    public LoginDialogFragment() {
        //Empty Constructor required for DialogFragment
    }


    RippleView mLoginRippleView, mRegisterRippleView;


    FacebookServerLoginAsync mFacebookServerLoginAsync;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mGoogleLoginProvider = (GoogleLoginProvider) activity;
        mPostFacebookLogin = (PostFacebookLogin) activity;
        mLoginCallbacks = (LoginCallbacks) activity;
        initProgressDialog();
    }

    private LoginButton mFbLoginButton;
    private CallbackManager mCallbackManager;
    private LoginManager mLoginManager;
    //
    private SignInButton mSignInButton;

    private SharedPreferencesManager mSharedPreferencesManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.BorderlessDialogAnimated);
        mFacebookServerLoginAsync = new FacebookServerLoginAsync(this);


    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Verifying with server...");
        mProgressDialog.setCancelable(false);


    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Dialog mDialog = new Dialog(getActivity(), R.style.DialogSlideAnim);
        Dialog mDialog = super.onCreateDialog(savedInstanceState);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(mDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.windowAnimations = R.style.SlideUpDownDialog;
        mDialog.getWindow().setAttributes(layoutParams);
        return mDialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.fragment_dialog_login, container, false);
        getDialog().setCanceledOnTouchOutside(true);

        mLoginManager = new LoginManager(getActivity());
        mSharedPreferencesManager = new SharedPreferencesManager(getActivity());
        initViews(view);

        return view;
    }

    private void initViews(View view) {
        initLogin(view);
        initRegister(view);

        initFacebookLogin(view);
        initGoogleLogin(view);


    }

    private void initRegister(View view) {
        mRegisterRippleView = (RippleView) view.findViewById(R.id.registerRippleView);
        mRegisterRippleView.setOnRippleCompleteListener(this);
    }

    private void initLogin(View view) {
        mLoginRippleView = (RippleView) view.findViewById(R.id.loginRippleView);
        mLoginRippleView.setOnRippleCompleteListener(this);
    }

    private void initFacebookLogin(View view) {
        //Initialisation of the Login button and the Callback Manager
        mCallbackManager = CallbackManager.Factory.create();
        mFbLoginButton = (LoginButton) view.findViewById(R.id.btnFacebookLogin);
        mFbLoginButton.setReadPermissions(Arrays.asList("user_friends", "email"));

        //Setting the Fragment for the Log in Button to the Current Fragment
        mFbLoginButton.setFragment(this);
        mFbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Toast.makeText(getActivity(), "Login Successful " + loginResult, Toast.LENGTH_LONG).show();
                //Call onFacebookLogin Method from LoginManager class passing the loginResult Object
                mLoginResult = loginResult;
                onPreFacebookServerLogin();
                Logger.d(TAG, loginResult.toString());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getActivity(), getString(R.string.login_cancelled), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Logger.d(TAG, e.getMessage());
                Toast.makeText(getActivity(), "Login Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initGoogleLogin(View view) {
        mSignInButton = (SignInButton) view.findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        setGooglePlusButtonText(mSignInButton, "Google");
    }

    private void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.sign_in_button:
                if (mGoogleLoginProvider != null) {
                    mGoogleLoginProvider.onGoogleSignIn();
                }
                break;

        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.loginRippleView:
                onLoginButtonClicked();
                break;
            case R.id.registerRippleView:
                onRegisterButtonClicked();
                break;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EmailLoginActivity.mLoginRequestCode:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), "You are logged in as \n" + data.getStringExtra("mailId"), Toast.LENGTH_SHORT).show();
                        mLoginManager.onEmailLogin(data.getStringExtra("mailId").toString());
                        dismiss();
                        if (mLoginCallbacks != null) {
                            mLoginCallbacks.onLoginSuccess();
                        }
                        break;
                    default:
                        if (mLoginCallbacks != null) {
                            mLoginCallbacks.onLoginFailure();
                        }
                        //Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            default: {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);

            }
        }
    }


    public void onLoginButtonClicked() {
        Intent i = new Intent(getActivity(), EmailLoginActivity.class);
        startActivityForResult(i, EmailLoginActivity.mLoginRequestCode);

    }


    public void onRegisterButtonClicked() {
        Intent i = new Intent(getActivity(), RegisterActivity.class);
        startActivity(i);
    }

    @Override
    public void onDestroy() {
        if (mFacebookServerLoginAsync != null)
            mFacebookServerLoginAsync.cancel(true);
        super.onDestroy();
    }
}
