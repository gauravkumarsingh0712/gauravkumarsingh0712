package com.mirraw.android.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mirraw.android.Mirraw;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.HomeActivity;
import com.mirraw.android.ui.fragments.LoginDialogFragment;

import org.json.JSONObject;

/**
 * Created by varun on 23/7/15.
 */
public class LoginManager implements ResultCallback<People.LoadPeopleResult> {


    public static final String TAG = LoginManager.class.getSimpleName();


    private String strUserEmail;
    private String strUserName;
    private long intUserId;
    private SharedPreferencesManager mSharedPreferencesManager;
    private Context mContext;

    ProgressDialog mProgressDialog;
    Activity mActivity;

    public LoginManager(Activity activity) {
        mContext = activity;
        mActivity = activity;
        mSharedPreferencesManager = new SharedPreferencesManager(mContext);


    }


    //Method to store the details of the users when logging in using Facebook
    //Store the details in a SharedPreference and pass it to an AsyncTask to send it to the server.
    //@param: LoginResult from the onSuccess event
    public void onFacebookLogin(LoginResult loginResult, final LoginDialogFragment.LoginCallbacks loginCallbacks) {
        //TODO: Store the details of the user in Shared Prefs, Call AsyncTask
        AccessToken strAccessToken = loginResult.getAccessToken();

        mSharedPreferencesManager.setUserAccessToken(strAccessToken.getToken());

//        Toast.makeText(Mirraw.getAppContext(), "LoginResult" + loginResult + " AccessToken : "
//                + strAccessToken, Toast.LENGTH_LONG).show();


        //To get the id, name & email of the user
        GraphRequest request = GraphRequest.newMeRequest(strAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                strUserEmail = "";
                strUserName = "";
                intUserId = 0;
                if (jsonObject.has("email") && !jsonObject.isNull("email")) {
                    strUserEmail = jsonObject.optString("email", "");
                }

                mSharedPreferencesManager.setUserEmail(strUserEmail);

                if (jsonObject.has("name") && !jsonObject.isNull("name")) {
                    strUserName = jsonObject.optString("name", "");
                }

                mSharedPreferencesManager.setUserName(strUserName);

                //TODO: TO BE REMOVED LATER

                Toast.makeText(Mirraw.getAppContext(), "Welcome " + strUserName, Toast.LENGTH_LONG).show();

                if (jsonObject.has("id") && !jsonObject.isNull("id")) {
                    intUserId = jsonObject.optLong("id", 0);
                }

                mSharedPreferencesManager.setUserId(intUserId);

                mSharedPreferencesManager.setLoggedIn(true);
                mSharedPreferencesManager.setFbLoggedIn(true);
                if (loginCallbacks != null) {
                    loginCallbacks.onLoginSuccess();
                }

            }
        });

        Bundle parameter = new Bundle();
        parameter.putString("fields", "id, name, email");

        request.setParameters(parameter);
        request.executeAsync();
    }

    public void onGoogleLogin(GoogleApiClient mGoogleApiClient) {
        //TODO: Store the details of the users in Shared prefs and call async task


        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);

        String strUserName = "";
        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            strUserName = currentPerson.getDisplayName();

        }
        mSharedPreferencesManager.setUserName(strUserName);

//        try {
//
//          String token=GoogleAuthUtil.getToken(mContext,strUserName,scopes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GoogleAuthException e) {
//            e.printStackTrace();
//        }
//


        //TODO: TO BE REMOVED LATER

        Toast.makeText(Mirraw.getAppContext(), "Welcome " + strUserName, Toast.LENGTH_LONG).show();

        strUserEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);


//        try {
//            String accessToken = GoogleAuthUtil.getToken(mContext, strUserEmail, "oauth2:"
//                    + Scopes.PLUS_ME + " "
//                    + "https://www.googleapis.com/auth/plus.login" + " "
//                    + "https://www.googleapis.com/auth/plus.profile.emails.read");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (GoogleAuthException e) {
//            e.printStackTrace();
//        }
        mSharedPreferencesManager.setUserEmail(strUserEmail);
        //
        //intUserId = Long.parseLong(currentPerson.getId().toString().trim());

        //mSharedPreferencesManager.setUserId(intUserId);
        mSharedPreferencesManager.setLoggedIn(true);
        mSharedPreferencesManager.setGoogleLogin(true);
    }

    public void onEmailLogin(String usrEmail) {
        //TODO: Store the details of the users in Shared prefs and call async task
        mSharedPreferencesManager.setLoggedIn(true);
        mSharedPreferencesManager.setEmailLogin(true);
        mSharedPreferencesManager.setUserEmail(usrEmail);
    }


    public void onFacebookLogOut() {
        com.facebook.login.LoginManager.getInstance().logOut();
        mSharedPreferencesManager.setFbLoggedIn(false);
        mSharedPreferencesManager.setLoggedIn(false);

        //Clear User Details
        mSharedPreferencesManager.clearUserAccessToken();
        mSharedPreferencesManager.clearUserEmail();
        mSharedPreferencesManager.clearUserName();
        mSharedPreferencesManager.clearUserId();
        mSharedPreferencesManager.clearLoginResponse();
        mSharedPreferencesManager.clearLoginFragmentShown();
    }

    public void onGoogleLogOut(GoogleApiClient googleApiClient) {

        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
        }

        mSharedPreferencesManager.setGoogleLogin(false);
        mSharedPreferencesManager.setLoggedIn(false);

        //Clear User Details
        mSharedPreferencesManager.clearUserName();
        mSharedPreferencesManager.clearUserEmail();
        mSharedPreferencesManager.clearLoginFragmentShown();
        mSharedPreferencesManager.clearLoginResponse();
    }

    public void onEmailLogOut() {
        mSharedPreferencesManager.setEmailLogin(false);
        mSharedPreferencesManager.setLoggedIn(false);
        mSharedPreferencesManager.clearUserEmail();

        //Clear User Details
        mSharedPreferencesManager.clearLoginResponse();
        mSharedPreferencesManager.clearLoginFragmentShown();
    }


    public static String LOGOUT_KEY = "logout_key";

    public static void onServerLogout() {

        Intent logout = new Intent(Mirraw.getAppContext(), HomeActivity.class);
        logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        logout.putExtra(LOGOUT_KEY, true);
        Mirraw.getAppContext().startActivity(logout);
    }


    Response mResponse;


    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }
}
