
package com.mirraw.android.models.Payu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayuMobileCreateParams {

    @Expose
    private String productinfo;
    @Expose
    private String txnid;
    @Expose
    private String firstname;
    @Expose
    private String email;
    @Expose
    private String phone;
    @SerializedName("shipping_firstname")
    @Expose
    private String shippingFirstname;
    @SerializedName("shipping_phone")
    @Expose
    private String shippingPhone;
    @Expose
    private String hash;
    @SerializedName("merchant_ibibo_codes")
    @Expose
    private String merchantIbiboCodes;
    @SerializedName("payment_related_details_for_mobile_sdk")
    @Expose
    private String paymentRelatedDetailsForMobileSdk;
    @SerializedName("vas_for_mobile_sdk")
    @Expose
    private String vasForMobileSdk;
    @Expose
    private String address1;
    @Expose
    private String address2;
    @Expose
    private String city;
    @Expose
    private String state;
    @Expose
    private String country;
    @Expose
    private String zipcode;
    @SerializedName("shipping_address1")
    @Expose
    private String shippingAddress1;
    @SerializedName("shipping_address2")
    @Expose
    private String shippingAddress2;
    @SerializedName("shipping_city")
    @Expose
    private String shippingCity;
    @SerializedName("shipping_state")
    @Expose
    private String shippingState;
    @SerializedName("shipping_country")
    @Expose
    private String shippingCountry;
    @SerializedName("shipping_zipcode")
    @Expose
    private String shippingZipcode;

    /**
     * @return The productinfo
     */
    public String getProductinfo() {
        return productinfo;
    }

    /**
     * @param productinfo The productinfo
     */
    public void setProductinfo(String productinfo) {
        this.productinfo = productinfo;
    }

    /**
     * @return The txnid
     */
    public String getTxnid() {
        return txnid;
    }

    /**
     * @param txnid The txnid
     */
    public void setTxnid(String txnid) {
        this.txnid = txnid;
    }

    /**
     * @return The firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname The firstname
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The shippingFirstname
     */
    public String getShippingFirstname() {
        return shippingFirstname;
    }

    /**
     * @param shippingFirstname The shipping_firstname
     */
    public void setShippingFirstname(String shippingFirstname) {
        this.shippingFirstname = shippingFirstname;
    }

    /**
     * @return The shippingPhone
     */
    public String getShippingPhone() {
        return shippingPhone;
    }

    /**
     * @param shippingPhone The shipping_phone
     */
    public void setShippingPhone(String shippingPhone) {
        this.shippingPhone = shippingPhone;
    }

    /**
     * @return The hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash The hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return The merchantIbiboCodes
     */
    public String getMerchantIbiboCodes() {
        return merchantIbiboCodes;
    }

    /**
     * @param merchantIbiboCodes The merchant_ibibo_codes
     */
    public void setMerchantIbiboCodes(String merchantIbiboCodes) {
        this.merchantIbiboCodes = merchantIbiboCodes;
    }

    /**
     * @return The paymentRelatedDetailsForMobileSdk
     */
    public String getPaymentRelatedDetailsForMobileSdk() {
        return paymentRelatedDetailsForMobileSdk;
    }

    /**
     * @param paymentRelatedDetailsForMobileSdk The payment_related_details_for_mobile_sdk
     */
    public void setPaymentRelatedDetailsForMobileSdk(String paymentRelatedDetailsForMobileSdk) {
        this.paymentRelatedDetailsForMobileSdk = paymentRelatedDetailsForMobileSdk;
    }

    /**
     * @return The vasForMobileSdk
     */
    public String getVasForMobileSdk() {
        return vasForMobileSdk;
    }

    /**
     * @param vasForMobileSdk The vas_for_mobile_sdk
     */
    public void setVasForMobileSdk(String vasForMobileSdk) {
        this.vasForMobileSdk = vasForMobileSdk;
    }

    /**
     * @return The address1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * @param address1 The address1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * @return The address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * @param address2 The address2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return The zipcode
     */
    public String getZipcode() {
        return zipcode;
    }

    /**
     * @param zipcode The zipcode
     */
    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * @return The shippingAddress1
     */
    public String getShippingAddress1() {
        return shippingAddress1;
    }

    /**
     * @param shippingAddress1 The shipping_address1
     */
    public void setShippingAddress1(String shippingAddress1) {
        this.shippingAddress1 = shippingAddress1;
    }

    /**
     * @return The shippingAddress2
     */
    public String getShippingAddress2() {
        return shippingAddress2;
    }

    /**
     * @param shippingAddress2 The shipping_address2
     */
    public void setShippingAddress2(String shippingAddress2) {
        this.shippingAddress2 = shippingAddress2;
    }

    /**
     * @return The shippingCity
     */
    public String getShippingCity() {
        return shippingCity;
    }

    /**
     * @param shippingCity The shipping_city
     */
    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    /**
     * @return The shippingState
     */
    public String getShippingState() {
        return shippingState;
    }

    /**
     * @param shippingState The shipping_state
     */
    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    /**
     * @return The shippingCountry
     */
    public String getShippingCountry() {
        return shippingCountry;
    }

    /**
     * @param shippingCountry The shipping_country
     */
    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    /**
     * @return The shippingZipcode
     */
    public String getShippingZipcode() {
        return shippingZipcode;
    }

    /**
     * @param shippingZipcode The shipping_zipcode
     */
    public void setShippingZipcode(String shippingZipcode) {
        this.shippingZipcode = shippingZipcode;
    }

}
