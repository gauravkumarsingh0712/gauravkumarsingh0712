package com.mirraw.android.Utils;

import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.models.address.AddressArray;

import java.util.ArrayList;

/**
 * Created by pavitra on 8/9/15.
 */
public class AddressUtil {

    public static int shipping_id = -1;
    public static int billing_id = -1;
    public static Address mShippingAddress;
    public static Address mBillingAddress;

    private static SharedPreferencesManager mAppSharedPrefs;

    public static int getShipping_id() {
        shipping_id = -1;
        mAppSharedPrefs = new SharedPreferencesManager(Mirraw.getAppContext());
        ArrayList<Address> addressArray;
        AddressArray addressArrayObject = new Gson().fromJson(mAppSharedPrefs.getAddresses(), AddressArray.class);
        Logger.v("", "Address: " + mAppSharedPrefs.getAddresses());
        addressArray = addressArrayObject.getAddresses();

        if (addressArray.size() == 1)
            shipping_id = addressArray.get(0).getId();

        for (int i = 0; i < addressArray.size(); i++) {
            if (addressArray.get(i).getShipAddressStatus()) {
                shipping_id = addressArray.get(i).getId();
                break;
            }
        }

        if (shipping_id == -1) {
            for (int i = 0; i < addressArray.size(); i++) {
                if (addressArray.get(i).getDefault() == 1) {
                    shipping_id = addressArray.get(i).getId();
                    break;
                }
            }
        }
        return shipping_id;
    }

    public static int getBilling_id() {
        billing_id = -1;
        mAppSharedPrefs = new SharedPreferencesManager(Mirraw.getAppContext());
        ArrayList<Address> addressArray;
        AddressArray addressArrayObject = new Gson().fromJson(mAppSharedPrefs.getAddresses(), AddressArray.class);
        addressArray = addressArrayObject.getAddresses();

        if (addressArray.size() == 1)
            return addressArray.get(0).getId();

        for (int i = 0; i < addressArray.size(); i++) {
            if (addressArray.get(i).getDefault() == 1) {
                billing_id = addressArray.get(i).getId();
                break;
            }
        }

        return billing_id;
    }

    public static Address getShippingAddress() throws Exception {
        mAppSharedPrefs = new SharedPreferencesManager(Mirraw.getAppContext());
        AddressArray addressArrayObject = new Gson().fromJson(mAppSharedPrefs.getAddresses(), AddressArray.class);
        ArrayList<Address> addressArray = addressArrayObject.getAddresses();

        mShippingAddress = addressArray.get(0);
        getShipping_id();

        if (shipping_id != -1) {
            for (int i = 0; i < addressArray.size(); i++) {
                if (addressArray.get(i).getId() == shipping_id) {
                    mShippingAddress = addressArray.get(i);
                }
            }
        }
        return mShippingAddress;
    }

    public static Address getBillingAddress() throws Exception {
        mBillingAddress = null;
        AddressArray addressArrayObject = new Gson().fromJson(mAppSharedPrefs.getAddresses(), AddressArray.class);
        ArrayList<Address> addressArray = addressArrayObject.getAddresses();

        mBillingAddress = addressArray.get(0);
        getBilling_id();

        if (billing_id != -1) {
            for (int i = 0; i < addressArray.size(); i++) {
                if (addressArray.get(i).getId() == billing_id) {
                    mBillingAddress = addressArray.get(i);
                }
            }
        }
        return mBillingAddress;
    }
}
