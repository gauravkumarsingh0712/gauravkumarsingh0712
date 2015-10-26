package com.mirraw.android.models.address;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pavitra on 14/7/15.
 */
public class Address {

    private Boolean shipAddressStatus = false;

    private Boolean billAddressStatus = false;

    @Expose
    private Integer id;
    @Expose
    private String name;
    @SerializedName("street_address")
    @Expose
    private String streetAddress;
    @Expose
    private String landmark;
    @Expose
    private String city;
    @Expose
    private String state;
    @Expose
    private String country;
    @Expose
    private String pincode;
    @Expose
    private String phone;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("default")
    @Expose
    private Integer _default = 0;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * @param streetAddress The street_address
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * @return The landmark
     */
    public String getLandmark() {
        return landmark;
    }

    /**
     * @param landmark The landmark
     */
    public void setLandmark(String landmark) {
        this.landmark = landmark;
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
     * @return The pincode
     */
    public String getPincode() {
        return pincode;
    }

    /**
     * @param pincode The pincode
     */
    public void setPincode(String pincode) {
        this.pincode = pincode;
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
     * @return The userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return The _default
     */
    public Integer getDefault() {
        return _default;
    }

    /**
     * @param _default The default
     */
    public void setDefault(Integer _default) {
        this._default = _default;
    }


    /**
     * @return The shipAddressStatus
     */
    public Boolean getShipAddressStatus() {
        return shipAddressStatus;
    }

    /**
     * @param shipAddressStatus The shipAddressStatus
     */
    public void setShipAddressStatus(Boolean shipAddressStatus) {
        this.shipAddressStatus = shipAddressStatus;
    }


    /**
     * @return The billAddressStatus
     */
    public Boolean getBillAddressStatus() {
        return billAddressStatus;
    }

    /**
     * @param billAddressStatus The billAddressStatus
     */
    public void setBillAddressStatus(Boolean billAddressStatus) {
        this.billAddressStatus = billAddressStatus;
    }
}
