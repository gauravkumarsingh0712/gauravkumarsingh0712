
package com.mirraw.android.models.PayPal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaypalMobileCreateParams {

    @SerializedName("currency_code")
    @Expose
    private String currencyCode;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("total")
    @Expose
    private Double total;

    /**
     * @return The currencyCode
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * @param currencyCode The currency_code
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * @return The clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId The client_id
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return The number
     */
    public String getNumber() {
        return number;
    }

    /**
     * @param number The number
     */
    public void setNumber(String number) {
        this.number = number;
    }

    /**
     * @return The total
     */
    public Double getTotal() {
        return total;
    }

    /**
     * @param total The total
     */
    public void setTotal(Double total) {
        this.total = total;
    }

}
