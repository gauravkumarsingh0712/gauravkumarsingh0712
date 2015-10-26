
package com.mirraw.android.models.PaymentOptionsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayPal {

    @SerializedName("additional_charge")
    @Expose
    private Integer additionalCharge;
    @Expose
    private String value;
    @Expose
    private Boolean available;

    /**
     * 
     * @return
     *     The additionalCharge
     */
    public Integer getAdditionalCharge() {
        return additionalCharge;
    }

    /**
     * 
     * @param additionalCharge
     *     The additional_charge
     */
    public void setAdditionalCharge(Integer additionalCharge) {
        this.additionalCharge = additionalCharge;
    }

    /**
     * 
     * @return
     *     The value
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 
     * @return
     *     The available
     */
    public Boolean getAvailable() {
        return available;
    }

    /**
     * 
     * @param available
     *     The available
     */
    public void setAvailable(Boolean available) {
        this.available = available;
    }

}
