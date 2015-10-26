
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DesignerOrder {

    @Expose
    private String state;
    @SerializedName("courier_company")
    @Expose
    private String courierCompany;
    @SerializedName("tracking_number")
    @Expose
    private String trackingNumber;
    @SerializedName("line_items")
    @Expose
    private List<LineItem> lineItems = new ArrayList<LineItem>();

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * 
     * @return
     *     The courierCompany
     */
    public String getCourierCompany() {
        return courierCompany;
    }

    /**
     * 
     * @param courierCompany
     *     The courier_company
     */
    public void setCourierCompany(String courierCompany) {
        this.courierCompany = courierCompany;
    }

    /**
     * 
     * @return
     *     The trackingNumber
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * 
     * @param trackingNumber
     *     The tracking_number
     */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    /**
     * 
     * @return
     *     The lineItems
     */
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    /**
     * 
     * @param lineItems
     *     The line_items
     */
    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

}
