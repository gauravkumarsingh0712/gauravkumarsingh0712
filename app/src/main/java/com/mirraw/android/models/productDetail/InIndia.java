
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;

public class InIndia {

    @Expose
    private String locations;
    @Expose
    private String time;
    @Expose
    private String charges;

    /**
     * 
     * @return
     *     The locations
     */
    public String getLocations() {
        return locations;
    }

    /**
     * 
     * @param locations
     *     The locations
     */
    public void setLocations(String locations) {
        this.locations = locations;
    }

    /**
     * 
     * @return
     *     The time
     */
    public String getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 
     * @return
     *     The charges
     */
    public String getCharges() {
        return charges;
    }

    /**
     * 
     * @param charges
     *     The charges
     */
    public void setCharges(String charges) {
        this.charges = charges;
    }

}
