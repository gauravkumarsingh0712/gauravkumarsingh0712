
package com.mirraw.android.models.address;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class Countries {

    @Expose
    private List<Country> countries = new ArrayList<Country>();

    /**
     * @return The countries
     */
    public List<Country> getCountries() {
        return countries;
    }

    /**
     * @param countries The countries
     */
    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

}