package com.mirraw.android.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by varun on 23/7/15.
 */
public class SharedPreferencesManager {

    private Context mContext;
    private static SharedPreferences mSharedPreferences;
    private static final String PREFERENCES = "mirraw_preferences";

    public SharedPreferencesManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        mContext = context;
    }

    private static final String LOGIN_FRAGMENT = "LOGIN_FRAGMENT";

    public boolean getLoginFragmentShown() {
        return mSharedPreferences.getBoolean(LOGIN_FRAGMENT, false);
    }

    public void setLoginFragmentShown(boolean boolLoginFragment) {
        mSharedPreferences.edit().putBoolean(LOGIN_FRAGMENT, boolLoginFragment).commit();
    }

    public void clearLoginFragmentShown() {
        mSharedPreferences.edit().putBoolean(LOGIN_FRAGMENT, false).commit();
    }

    private static final String LOGIN_FLAG = "LOGIN";

    public boolean getLoggedIn() {
        return mSharedPreferences.getBoolean(LOGIN_FLAG, false);
    }

    public void setLoggedIn(boolean boolLogin) {
        mSharedPreferences.edit().putBoolean(LOGIN_FLAG, boolLogin).commit();
    }

    private static final String FB_LOGIN_FLAG = "FB_LOGIN";

    public boolean getFbLoggedIn() {
        return mSharedPreferences.getBoolean(FB_LOGIN_FLAG, false);
    }

    public void setFbLoggedIn(boolean boolFbLogIn) {
        mSharedPreferences.edit().putBoolean(FB_LOGIN_FLAG, boolFbLogIn).commit();
    }

    private static final String GOOGLE_LOGIN_FLAG = "GOOGLE_LOG_IN";

    public boolean getGoogleLoggedIn() {
        return mSharedPreferences.getBoolean(GOOGLE_LOGIN_FLAG, false);
    }

    public void setGoogleLogin(boolean boolGoogleLogIn) {
        mSharedPreferences.edit().putBoolean(GOOGLE_LOGIN_FLAG, boolGoogleLogIn).commit();
    }


    private static final String EMAIL_LOGIN_FLAG = "EMAIL_LOG_IN";

    public boolean getEmailLoggedIn() {
        return mSharedPreferences.getBoolean(EMAIL_LOGIN_FLAG, false);
    }

    public void setEmailLogin(boolean boolEmailLogIn) {
        mSharedPreferences.edit().putBoolean(EMAIL_LOGIN_FLAG, boolEmailLogIn).commit();
    }


    private static final String USER_NAME_KEY = "NAME";

    public String getUserName() {
        return mSharedPreferences.getString(USER_NAME_KEY, "");
    }

    public void setUserName(String strName) {
        mSharedPreferences.edit().putString(USER_NAME_KEY, strName).commit();
    }

    public void clearUserName() {
        mSharedPreferences.edit().putString(USER_NAME_KEY, "").commit();
    }

    private static final String USER_EMAIL_KEY = "EMAIL";

    public String getUserEmail() {
        return mSharedPreferences.getString(USER_EMAIL_KEY, "");
    }

    public void setUserEmail(String strEmail) {
        mSharedPreferences.edit().putString(USER_EMAIL_KEY, strEmail).commit();
    }

    public void clearUserEmail() {
        mSharedPreferences.edit().putString(USER_EMAIL_KEY, "").commit();
    }

    private static final String USER_ACCESS_TOKEN_KEY = "FB_ACCESS_TOKEN";

    public String getUserAccessToken() {
        return mSharedPreferences.getString(USER_ACCESS_TOKEN_KEY, "");
    }

    public void setUserAccessToken(String strAccessToken) {
        mSharedPreferences.edit().putString(USER_ACCESS_TOKEN_KEY, strAccessToken).commit();
    }

    public void clearUserAccessToken() {
        mSharedPreferences.edit().putString(USER_ACCESS_TOKEN_KEY, "").commit();
    }

    private static final String USER_ID = "ID";

    public long getUserId() {
        return mSharedPreferences.getInt(USER_ID, 0);
    }

    public void setUserId(long intUserId) {
        mSharedPreferences.edit().putLong(USER_ID, intUserId).commit();
    }

    public void clearUserId() {
        mSharedPreferences.edit().putLong(USER_ID, 0).commit();
    }


    private static final String ADDRESSES = "ADDRESSES";

    public String getAddresses() {
        return mSharedPreferences.getString(ADDRESSES, "");
    }

    public void setAddresses(String addresses) {
        mSharedPreferences.edit().putString(ADDRESSES, addresses).commit();
    }

    public void clearAddresses() {
        mSharedPreferences.edit().putString(ADDRESSES, "").commit();
    }


    private static final String EMAIL_LOGIN_RESPONSE = "EMAIL_LOGIN_RESPONSE";

    public String getLoginResponse() {
        return mSharedPreferences.getString(EMAIL_LOGIN_RESPONSE, "");
    }

    public void setLoginResponse(String login_response) {
        mSharedPreferences.edit().putString(EMAIL_LOGIN_RESPONSE, login_response).commit();
    }

    public void clearLoginResponse() {
        mSharedPreferences.edit().putString(EMAIL_LOGIN_RESPONSE, "").commit();
    }

    private static final String COUNTRIES = "COUNTRIES";

    public String getCountries() {
        return mSharedPreferences.getString(COUNTRIES, "");
    }

    public void setCountries(String countries) {
        mSharedPreferences.edit().putString(COUNTRIES, countries).commit();
    }
    private static final String STATES = "STATES";

    public String getStates() {
        return mSharedPreferences.getString(STATES, "");
    }

    public void setStates(String states) {
        mSharedPreferences.edit().putString(STATES, states).commit();
    }

    private static final String US_APK_TEST = "us_apk_test";

    public Boolean getUsApkTest() {
        return mSharedPreferences.getBoolean(US_APK_TEST, false);
    }

    public void setUsApkTest(Boolean boolUsApkTest) {
        mSharedPreferences.edit().putBoolean(US_APK_TEST, boolUsApkTest).commit();
    }

    private static final String STAGING_APK_TEST = "staging_apk_test";

    public Boolean getStagingApkTest() {
        return mSharedPreferences.getBoolean(STAGING_APK_TEST, false);
    }

    public void setStagingApkTest(Boolean boolStagingApkTest) {
        mSharedPreferences.edit().putBoolean(STAGING_APK_TEST, boolStagingApkTest).commit();
    }

    private static final String TAB_DETAILS = "TAB_DETAILS";

    public String getTabDetails() {
        return mSharedPreferences.getString(TAB_DETAILS, "");
    }

    public void setTabDetails(String strTabDetails) {
        mSharedPreferences.edit().putString(TAB_DETAILS, strTabDetails).commit();
    }
}
