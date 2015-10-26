
package com.mirraw.android.models.Payu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Checksums {

    @SerializedName("payment_hash")
    @Expose
    private String paymentHash;
    @SerializedName("merchant_ibibo_codes")
    @Expose
    private String merchantIbiboCodes;
    @SerializedName("payment_related_details_for_mobile_sdk")
    @Expose
    private String paymentRelatedDetailsForMobileSdk;
    @SerializedName("vas_for_mobile_sdk")
    @Expose
    private String vasForMobileSdk;

    /**
     * 
     * @return
     *     The paymentHash
     */
    public String getPaymentHash() {
        return paymentHash;
    }

    /**
     * 
     * @param paymentHash
     *     The payment_hash
     */
    public void setPaymentHash(String paymentHash) {
        this.paymentHash = paymentHash;
    }

    /**
     * 
     * @return
     *     The merchantIbiboCodes
     */
    public String getMerchantIbiboCodes() {
        return merchantIbiboCodes;
    }

    /**
     * 
     * @param merchantIbiboCodes
     *     The merchant_ibibo_codes
     */
    public void setMerchantIbiboCodes(String merchantIbiboCodes) {
        this.merchantIbiboCodes = merchantIbiboCodes;
    }

    /**
     * 
     * @return
     *     The paymentRelatedDetailsForMobileSdk
     */
    public String getPaymentRelatedDetailsForMobileSdk() {
        return paymentRelatedDetailsForMobileSdk;
    }

    /**
     * 
     * @param paymentRelatedDetailsForMobileSdk
     *     The payment_related_details_for_mobile_sdk
     */
    public void setPaymentRelatedDetailsForMobileSdk(String paymentRelatedDetailsForMobileSdk) {
        this.paymentRelatedDetailsForMobileSdk = paymentRelatedDetailsForMobileSdk;
    }

    /**
     * 
     * @return
     *     The vasForMobileSdk
     */
    public String getVasForMobileSdk() {
        return vasForMobileSdk;
    }

    /**
     * 
     * @param vasForMobileSdk
     *     The vas_for_mobile_sdk
     */
    public void setVasForMobileSdk(String vasForMobileSdk) {
        this.vasForMobileSdk = vasForMobileSdk;
    }

}
