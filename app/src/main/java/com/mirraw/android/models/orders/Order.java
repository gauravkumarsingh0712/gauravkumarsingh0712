
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @Expose
    private Integer id;
    @Expose
    private String number;
    @SerializedName("pay_type")
    @Expose
    private String payType;
    @SerializedName("payment_state")
    @Expose
    private String paymentState;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @Expose
    private String total;

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
     *     The payType
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 
     * @param payType
     *     The pay_type
     */
    public void setPayType(String payType) {
        this.payType = payType;
    }

    /**
     * 
     * @return
     *     The paymentState
     */
    public String getPaymentState() {
        return paymentState;
    }

    /**
     * 
     * @param paymentState
     *     The payment_state
     */
    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The hexSymbol
     */
    public String getHexSymbol() {
        return hexSymbol;
    }

    /**
     * 
     * @param hexSymbol
     *     The hex_symbol
     */
    public void setHexSymbol(String hexSymbol) {
        this.hexSymbol = hexSymbol;
    }

    /**
     * 
     * @return
     *     The currencySymbol
     */
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    /**
     * 
     * @param currencySymbol
     *     The currency_symbol
     */
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    /**
     * 
     * @return
     *     The total
     */
    public String getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

}
