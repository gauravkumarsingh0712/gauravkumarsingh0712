
package com.mirraw.android.models.Payu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateOrderPayu {

    @Expose
    private Integer id;
    @Expose
    private String number;
    @SerializedName("payu_mobile_create_params")
    @Expose
    private PayuMobileCreateParams payuMobileCreateParams;

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
     *     The payuMobileCreateParams
     */
    public PayuMobileCreateParams getPayuMobileCreateParams() {
        return payuMobileCreateParams;
    }

    /**
     * 
     * @param payuMobileCreateParams
     *     The payu_mobile_create_params
     */
    public void setPayuMobileCreateParams(PayuMobileCreateParams payuMobileCreateParams) {
        this.payuMobileCreateParams = payuMobileCreateParams;
    }

}
