
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class OutOfIndia {

    @Expose
    private List<String> locations = new ArrayList<String>();
    @Expose
    private String note;
    @Expose
    private String time;
    @Expose
    private String charges;

    /**
     * 
     * @return
     *     The locations
     */
    public List<String> getLocations() {
        return locations;
    }

    /**
     * 
     * @param locations
     *     The locations
     */
    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    /**
     * 
     * @return
     *     The note
     */
    public String getNote() {
        return note;
    }

    /**
     * 
     * @param note
     *     The note
     */
    public void setNote(String note) {
        this.note = note;
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
