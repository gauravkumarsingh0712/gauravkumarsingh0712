
package com.mirraw.android.models.PayPal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOrderPaypal {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("paypal_mobile_create_params")
    @Expose
    private PaypalMobileCreateParams paypalMobileCreateParams;

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The number
     */
    public String getNumber() {
        return number;
    }

    /**
     * 
     * @param number
     *     The number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * 
     * @return
     *     The paypalMobileCreateParams
     */
    public PaypalMobileCreateParams getPaypalMobileCreateParams() {
        return paypalMobileCreateParams;
    }

    /**
     * 
     * @param paypalMobileCreateParams
     *     The paypal_mobile_create_params
     */
    public void setPaypalMobileCreateParams(PaypalMobileCreateParams paypalMobileCreateParams) {
        this.paypalMobileCreateParams = paypalMobileCreateParams;
    }

}
