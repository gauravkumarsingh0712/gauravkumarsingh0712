
package com.mirraw.android.models.PaymentOptionsDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Details {

    @SerializedName("item_count")
    @Expose
    private Integer itemCount;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @SerializedName("item_total")
    @Expose
    private Double itemTotal;
    @SerializedName("shipping_total")
    @Expose
    private Double shippingTotal;
    @Expose
    private Double discount;
    @SerializedName("payment_options_key")
    @Expose
    private String paymentOptionsKey;
    @SerializedName("payment_options")
    @Expose
    private PaymentOptions paymentOptions;
    @SerializedName("conditional_offers")
    @Expose
    private ConditionalOffers conditionalOffers;
    @Expose
    private Double total;

    /**
     * 
     * @return
     *     The itemCount
     */
    public Integer getItemCount() {
        return itemCount;
    }

    /**
     * 
     * @param itemCount
     *     The item_count
     */
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
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
     *     The itemTotal
     */
    public Double getItemTotal() {
        return itemTotal;
    }

    /**
     * 
     * @param itemTotal
     *     The item_total
     */
    public void setItemTotal(Double itemTotal) {
        this.itemTotal = itemTotal;
    }

    /**
     * 
     * @return
     *     The shippingTotal
     */
    public Double getShippingTotal() {
        return shippingTotal;
    }

    /**
     * 
     * @param shippingTotal
     *     The shipping_total
     */
    public void setShippingTotal(Double shippingTotal) {
        this.shippingTotal = shippingTotal;
    }

    /**
     * 
     * @return
     *     The discount
     */
    public Double getDiscount() {
        return discount;
    }

    /**
     * 
     * @param discount
     *     The discount
     */
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    /**
     * 
     * @return
     *     The paymentOptionsKey
     */
    public String getPaymentOptionsKey() {
        return paymentOptionsKey;
    }

    /**
     * 
     * @param paymentOptionsKey
     *     The payment_options_key
     */
    public void setPaymentOptionsKey(String paymentOptionsKey) {
        this.paymentOptionsKey = paymentOptionsKey;
    }

    /**
     * 
     * @return
     *     The paymentOptions
     */
    public PaymentOptions getPaymentOptions() {
        return paymentOptions;
    }

    /**
     * 
     * @param paymentOptions
     *     The payment_options
     */
    public void setPaymentOptions(PaymentOptions paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    /**
     * 
     * @return
     *     The conditionalOffers
     */
    public ConditionalOffers getConditionalOffers() {
        return conditionalOffers;
    }

    /**
     * 
     * @param conditionalOffers
     *     The conditional_offers
     */
    public void setConditionalOffers(ConditionalOffers conditionalOffers) {
        this.conditionalOffers = conditionalOffers;
    }

    /**
     * 
     * @return
     *     The total
     */
    public Double getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(Double total) {
        this.total = total;
    }

}
