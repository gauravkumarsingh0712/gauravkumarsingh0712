package com.mirraw.android.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.CartAsync;
import com.mirraw.android.async.GoogleServerLoginAsync;
import com.mirraw.android.async.LogoutAsync;
import com.mirraw.android.models.cart.CartItems;
import com.mirraw.android.models.login.SocialLoginErrors;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.AppConfig;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.fragments.LoginDialogFragment;
import com.splunk.mint.Mint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by nimesh on 18/8/15.
 */
public class BaseActivity extends AnimationActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LogoutAsync.LogoutLoader,
        LoginDialogFragment.GoogleLoginProvider,
        CartAsync.CartLoader,
        LoginDialogFragment.PostFacebookLogin,
        GoogleServerLoginAsync.GoogleServerLoginLoader,
        RippleView.OnRippleCompleteListener,
        View.OnClickListener, LoginDialogFragment.LoginCallbacks {


    public SharedPreferencesManager mSharedPreferencesManager;
    private Menu menu;
    ProgressDialog mServerVerificationProgressDialog;
    private LoginDialogFragment mLoginDialogFragment;
    public static final String TAG = "Some derived class";
    private CartAsync mCartAsync;
    public static int sCartCount;
    Bundle mSavedInstanceState;
    boolean mMyOrdersClicked = false;
    Toolbar mToolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeBlack);
        super.onCreate(savedInstanceState);
        mSharedPreferencesManager = new SharedPreferencesManager(Mirraw.getAppContext());
        FacebookSdk.sdkInitialize(getApplicationContext());

        mSavedInstanceState = savedInstanceState;
        initGoogleApiClient();
        initProgressDialog();
    }

    private void initGoogleApiClient() {

        mGoogleApiClient = buildGoogleApiClient();
//        setCartCount();
//        loadCart();
        if (mSavedInstanceState != null) {
            mSignInProgress = mSavedInstanceState
                    .getInt(SAVED_PROGRESS, STATE_DEFAULT);

        }

    }

    private void initProgressDialog() {
        mServerVerificationProgressDialog = new ProgressDialog(this);
        mServerVerificationProgressDialog.setMessage("Verifying with server...");
        mServerVerificationProgressDialog.setCancelable(false);


    }

    void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }


    void setupActionBarBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        ((RippleView) menu.findItem(R.id.action_cart).getActionView().findViewById(R.id.rippleView)).setOnRippleCompleteListener(this);
        this.menu = menu;
        updateMenuLoginStatus();
        loadCart();
        setCartCount();
        if (!AppConfig.testForUS()) {
            menu.findItem(R.id.action_us).setVisible(false);
        }
        if (!AppConfig.testForStaging()) {
            menu.findItem(R.id.action_staging).setVisible(false);
        } else {
            if (mSharedPreferencesManager.getStagingApkTest()) {
                menu.findItem(R.id.action_staging).setChecked(true);
            } else {
                menu.findItem(R.id.action_staging).setChecked(false);
            }
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_search:
                Intent i = new Intent(this, SearchResultsActivity.class);
                startActivity(i);
                break;
            case R.id.action_login_logout:
                onLoginLogoutOptionClicked();
                break;
            case R.id.action_track_orders:
                mMyOrdersClicked = true;
                onTrackOrdersOptionClicked();
                break;
            case R.id.action_us:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mSharedPreferencesManager.setUsApkTest(false);
                } else {
                    item.setChecked(true);
                    mSharedPreferencesManager.setUsApkTest(true);
                }
                break;
            case R.id.action_staging:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mSharedPreferencesManager.setStagingApkTest(false);
                } else {
                    item.setChecked(true);
                    mSharedPreferencesManager.setStagingApkTest(true);
                }
                Intent restartApp = new Intent(this, SplashActivity.class);
                restartApp.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(restartApp);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void onTrackOrdersOptionClicked() {
        if (mSharedPreferencesManager.getLoggedIn()) {
            Intent intent = new Intent(this, OrdersActivity.class);
            startActivity(intent);
        } else {
            onLoginLogoutOptionClicked();
        }
    }

    public void setCartCount() {

        if (menu != null) {
            MenuItem menuItem = menu.findItem(R.id.action_cart);
            RelativeLayout cartRelativeLayout = (RelativeLayout) MenuItemCompat.getActionView(menuItem);
            TextView cartCountTextView = (TextView) cartRelativeLayout.findViewById(R.id.cartCountTextView);
            if (sCartCount > 0)
                cartCountTextView.setText("" + sCartCount);
            else
                cartCountTextView.setText("");

        }
    }

    private void updateMenuLoginStatus() {
        MenuItem menuLogout = menu.findItem(R.id.action_login_logout);
        boolean boolIsLoggedIn = mSharedPreferencesManager.getLoggedIn();
        if (boolIsLoggedIn) {
            menuLogout.setTitle("Logout");
        } else {
            menuLogout.setTitle("Login");
        }
    }

    private boolean mReconnectingGoogleAccount = false;

    protected void onLoginLogoutOptionClicked() {
        LoginManager loginManager = new LoginManager(this);
        if (mSharedPreferencesManager.getLoggedIn()) {
            if (mSharedPreferencesManager.getFbLoggedIn()) {
                startLogout();
                loginManager.onFacebookLogOut();
            } else if (mSharedPreferencesManager.getGoogleLoggedIn()) {

                //when user is logged in some different activity
                // we need to sign in the user first before logging him out.
                if (mGoogleApiClient.isConnected()) {
                    startLogout();
                    loginManager.onGoogleLogOut(mGoogleApiClient);
                } else {
                    mReconnectingGoogleAccount = true;
                    mGoogleApiClient.connect();
                }

            } else if (mSharedPreferencesManager.getEmailLoggedIn()) {
                startLogout();
                //loginManager.onEmailLogOut();
            }
        } else {
            mSharedPreferencesManager.clearLoginFragmentShown();
            showLoginFragment();
        }

        invalidateOptionsMenu();
    }


    LogoutAsync mLogoutAsync;

    @Override
    public void startLogout() {
        logoutUser();
        mLogoutAsync = new LogoutAsync(this);
        String url = ApiUrls.API_LOGOUT;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            Logger.v("", "Login Response: " + head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.DELETE).setHeaders(headerMap).build();
        mLogoutAsync.executeTask(request);
    }

    private void logoutFailed(String errors) {
        showSnackBar(errors);
    }

    private void logoutUser() {
        new LoginManager(this).onEmailLogOut();
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_SHORT).show();
    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.action_cart:
                /*LoginManager loginManager = new LoginManager(this);
                if (!mSharedPreferencesManager.getLoggedIn()) {
                    mSharedPreferencesManager.clearLoginFragmentShown();
                    showLoginFragment();
                } else {*/
                Intent intent = new Intent(this, CartActivity.class);

                startActivity(intent);
                //}
                break;
        }

    }

//    @Override
//    public void onFragmentLoaded() {
//
//        boolean isLoggedIn = mSharedPreferencesManager.getLoggedIn();
//        if (!isLoggedIn) {
//            showLoginFragment();
//        }
//    }

    public void showLoginFragment() {
        try {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            mLoginDialogFragment = new LoginDialogFragment();
            if (mLoginDialogFragment != null && mLoginDialogFragment.isVisible()) {

            } else {
                if (!mSharedPreferencesManager.getLoginFragmentShown()) {
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    Fragment prev = getSupportFragmentManager().findFragmentByTag(mLoginDialogFragment.TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    mLoginDialogFragment.show(ft, mLoginDialogFragment.TAG);
                    mSharedPreferencesManager.setLoginFragmentShown(true);
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
            Mint.logExceptionMessage("LoginDialogFragment", "Exception in shwowing loaginDialogFragment", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    // If the error resolution was successful we should continue
                    // processing errors.
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    // If the error resolution was not successful or the user canceled,
                    // we should stop processing errors.
                    mSignInProgress = STATE_DEFAULT;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    // If Google Play services resolved the issue with a dialog then
                    // onStart is not called so we need to re-attempt connection here.
                    mGoogleApiClient.connect();
                }
                break;
        }

        this.invalidateOptionsMenu();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logout = menu.findItem(R.id.action_login_logout);

        if (logout != null) {
            if (mSharedPreferencesManager.getLoggedIn()) {
                logout.setTitle("Logout");
            } else {
                logout.setTitle("Login");
            }
        }
        return true;
    }


    //Google login code

    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;

    private static final int RC_SIGN_IN = 0;

    private static final String SAVED_PROGRESS = "sign_in_progress";

    // Client ID for a web server that will receive the auth code and exchange it for a
    // refresh token if offline access is requested.
    private static final String WEB_CLIENT_ID = "WEB_CLIENT_ID";

    // Base URL for your token exchange server, no trailing slash.
    private static final String SERVER_BASE_URL = "SERVER_BASE_URL";

    // URL where the client should GET the scopes that the server would like granted
    // before asking for a serverAuthCode
    private static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";

    // URL where the client should POST the serverAuthCode so that the server can exchange
    // it for a refresh token,
    private static final String SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes";


    // GoogleApiClient wraps our service connection to Google Play services and
    // provides access to the users sign in state and Google's APIs.
    private GoogleApiClient mGoogleApiClient;

    // We use mSignInProgress to track whether user has clicked sign in.
    // mSignInProgress can be one of three values:
    //
    //       STATE_DEFAULT: The default state of the application before the user
    //                      has clicked 'sign in', or after they have clicked
    //                      'sign out'.  In this state we will not attempt to
    //                      resolve sign in errors and so will display our
    //                      Activity in a signed out state.
    //       STATE_SIGN_IN: This state indicates that the user has clicked 'sign
    //                      in', so resolve successive errors preventing sign in
    //                      until the user has successfully authorized an account
    //                      for our app.
    //   STATE_IN_PROGRESS: This state indicates that we have started an intent to
    //                      resolve an error, and so we should not start further
    //                      intents until the current intent completes.
    private int mSignInProgress;

    // Used to store the PendingIntent most recently returned by Google Play
    // services until the user clicks 'sign in'.
    private PendingIntent mSignInIntent;

    // Used to store the error code most recently returned by Google Play services
    // until the user clicks 'sign in'.
    private int mSignInError;

    // Used to determine if we should ask for a server auth code when connecting the
    // GoogleApiClient.  False by default so that this sample can be used without configuring
    // a WEB_CLIENT_ID and SERVER_BASE_URL.
    private boolean mRequestServerAuthCode = false;

    // Used to mock the state of a server that would receive an auth code to exchange
    // for a refresh token,  If true, the client will assume that the server has the
    // permissions it wants and will not send an auth code on sign in.  If false,
    // the client will request offline access on sign in and send and new auth code
    // to the server.  True by default because this sample does not implement a server
    // so there would be nowhere to send the code.
    private boolean mServerHasToken = true;


    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)

                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(new Scope("https://www.googleapis.com/auth/userinfo.email"))
                .addScope(new Scope("https://www.googleapis.com/auth/plus.login"));

//        if (mRequestServerAuthCode) {
//            checkServerAuthConfiguration();
//            builder = builder.requestServerAuthCode(WEB_CLIENT_ID, this);
//        }

        return builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //    mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!mSharedPreferencesManager.getLoggedIn()) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mCartAsync != null) {
            mCartAsync.cancel(true);
        }
        if (mGoogleServerLoginAsync != null) {
            mGoogleServerLoginAsync.cancel(true);
        }
        if (mLogoutAsync != null) {
            mLogoutAsync.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }


    /* onConnected is called when our Activity successfully connects to Google
  * Play services.  onConnected indicates that an account was selected on the
  * device, that the selected account has granted any requested permissions to
  * our app and that we were able to establish a service connection to Google
  * Play services.
  */
    String mEmail;

    @Override
    public void onConnected(Bundle connectionHint) {
        // Reaching onConnected means we consider the user signed in.
        Logger.i(TAG, "onConnected");
        if (!mReconnectingGoogleAccount) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            mEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

            onPreGoogleServerLogin();


            // Indicate that the sign in process is complete.
            mSignInProgress = STATE_DEFAULT;
        } else {
            mReconnectingGoogleAccount = false;
            onLoginLogoutOptionClicked();
        }

    }

    private void dismissLoginFragment() {
        if (mLoginDialogFragment != null && mLoginDialogFragment.isVisible()) {
            mLoginDialogFragment.dismissAllowingStateLoss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menu != null) {
            setCartCount();
            loadCart();

        }
    }

    /* onConnectionFailed is called when our Activity could not connect to Google
     * Play services.  onConnectionFailed indicates that the user needs to select
     * an account, grant permissions or resolve an error in order to sign in.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Logger.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
        long r = result.getErrorCode();
        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
            Logger.w(TAG, "API Unavailable.");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            } else if (result.getErrorCode() == ConnectionResult.SIGN_IN_REQUIRED && mGoogleLoginClicked != true) {
                Toast.makeText(this, getString(R.string.login_cancelled), Toast.LENGTH_LONG).show();
            }


            mGoogleLoginClicked = false;
        }


        // In this sample we consider the user signed out whenever they do not have
        // a connection to Google Play services.
//        onSignedOut();
    }

    /* Starts an appropriate intent or dialog for user interaction to resolve
     * the current error preventing the user from being signed in.  This could
     * be a dialog allowing the user to select an account, an activity allowing
     * the user to consent to the permissions being requested by your app, a
     * setting to enable device networking, etc.
     */
    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Logger.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.
            createErrorDialog().show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();
    }

    private Dialog createErrorDialog() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    this,
                    RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Logger.e(TAG, "Google Play services resolution cancelled");
                            mSignInProgress = STATE_DEFAULT;
                        }
                    });
        } else {
            return new AlertDialog.Builder(this)
                    .setMessage(R.string.play_services_error)
                    .setPositiveButton(R.string.close,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Logger.e(TAG, "Google Play services error could not be "
                                            + "resolved: " + mSignInError);
                                    mSignInProgress = STATE_DEFAULT;
                                }
                            }).create();
        }
    }

    boolean mGoogleLoginClicked;

    @Override
    public void onGoogleSignIn() {
        mSignInProgress = STATE_SIGN_IN;
        mGoogleApiClient.connect();
        mGoogleLoginClicked = true;


    }


    @Override
    public void loadCart() {
        String url = ApiUrls.API_GET_CART_ITEMS;


        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
        try {
            head = new JSONObject(mSharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            Logger.v("", "Login Response: " + head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
        mCartAsync = new CartAsync(this);
        mCartAsync.executeTask(request);
    }

    @Override
    public void onEmptyCart(Response response) {

    }

    @Override
    public void onCartLoaded(Response response) {
        try {
            Gson gson = new Gson();
            CartItems cartItems = (CartItems) gson.fromJson(response.getBody(), CartItems.class);
            int cartTotalCount = 0;
            int noOfDistinctItems = cartItems.getCart().getLineItems().size();
            for (int i = 0; i < noOfDistinctItems; i++) {
                cartTotalCount += cartItems.getCart().getLineItems().get(i).getQuantity();

            }
            sCartCount = cartTotalCount;
            setCartCount();
        } catch (Exception e) {
            e.printStackTrace();
            Mint.logException(e);
        }
    }


    @Override
    public void loadCartCount() {
        loadCart();

    }

    @Override
    public void onPreGoogleServerLogin() {
        mServerVerificationProgressDialog.show();
        loadGoogleServerLogin();
    }

    GoogleServerLoginAsync mGoogleServerLoginAsync;

    @Override
    public void loadGoogleServerLogin() {
        HashMap<String, String> headers = new HashMap<String, String>();
        String scopes = "oauth2:" + "https://www.googleapis.com/auth/userinfo.email";
        String url;

        headers.put(Headers.PROVIDER, "google_oauth2");
        url = ApiUrls.API_GOOGLE_AUTH + "?resource_class=Account";

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(headers).build();

        mGoogleServerLoginAsync = new GoogleServerLoginAsync(this);
        mGoogleServerLoginAsync.executeTask(mEmail, this, scopes, request);
    }

    Response mResponse;

    @Override
    public void onGoogleServerLoginComplete(Response response) {
        mResponse = response;
        if (response.getResponseCode() == 200) {
            onGoogleServerLoginSuccess();
        } else if (response.getResponseCode() == 0) {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
            initGoogleApiClient();
        } else {
            onGoogleServerLoginFailure();

        }
        if (mServerVerificationProgressDialog != null && mServerVerificationProgressDialog.isShowing()) {
            mServerVerificationProgressDialog.dismiss();
        }

    }

    @Override
    public void onGoogleServerLoginSuccess() {

        if (mGoogleApiClient.isConnected()) {
            Gson gson = new Gson();
            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
            sharedPreferencesManager.setLoginResponse(gson.toJson(mResponse));
            loadCart();

            LoginManager loginManager = new LoginManager(this);
            loginManager.onGoogleLogin(mGoogleApiClient);

            dismissLoginFragment();
            onLoginSuccess();
        }
    }

    @Override
    public void onGoogleServerLoginFailure() {
        try {
            Gson gson = new Gson();
            String error = gson.fromJson(mResponse.getBody(), SocialLoginErrors.class).getError();

            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

        }
        if (mGoogleApiClient.isConnected() == true) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();


        }
        initGoogleApiClient();

    }

    @Override
    public void onComplete(RippleView rippleView) {

        switch (rippleView.getId()) {
            case R.id.rippleView:
                /*LoginManager loginManager = new LoginManager(this);
                if (!mSharedPreferencesManager.getLoggedIn()) {
                    mSharedPreferencesManager.clearLoginFragmentShown();
                    showLoginFragment();
                } else {*/
                Intent intent = new Intent(this, CartActivity.class);
                startActivity(intent);
                //}
                break;
        }
    }

    @Override
    public void onLoginSuccess() {
        if (mMyOrdersClicked == true) {
            mMyOrdersClicked = false;
            onTrackOrdersOptionClicked();
        }
    }

    @Override
    public void onLoginFailure() {

    }
}