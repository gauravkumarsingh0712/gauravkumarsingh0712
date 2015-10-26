package com.mirraw.android.models.address;


import com.mirraw.android.models.cart.*;

import java.util.ArrayList;

/**
 * Created by Gaurav on 8/8/2015.
 */
public class ListOfAddress {


    private ArrayList<Address> addresses = new ArrayList<Address>();

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
