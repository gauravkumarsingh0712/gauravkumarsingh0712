package com.mirraw.android.models.address;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by pavitra on 14/7/15.
 */
public class AddressArray {

    @Expose
    private ArrayList<Address> addresses;

    /**
     * @return The addresses
     */
    public ArrayList<Address> getAddresses() {
        return addresses;
    }

    /**
     * @param addresses The addresses
     */
    public void setAddresses(ArrayList<Address> addresses) {
        this.addresses = addresses;
    }


}
