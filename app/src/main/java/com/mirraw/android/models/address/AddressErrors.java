package com.mirraw.android.models.address;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressErrors {

    @Expose
    private List<String> name = new ArrayList<String>();
    @Expose
    private List<String> phone = new ArrayList<String>();
    @Expose
    private List<String> pincode = new ArrayList<String>();
    @SerializedName("street_address")
    @Expose
    private List<String> streetAddress = new ArrayList<String>();
    @Expose
    private List<String> city = new ArrayList<String>();
    @Expose
    private List<String> state = new ArrayList<String>();
    @Expose
    private List<String> country = new ArrayList<String>();

    /**
     * @return The name
     */
    public List<String> getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(List<String> name) {
        this.name = name;
    }

    /**
     * @return The phone
     */
    public List<String> getPhone() {
        return phone;
    }

    /**
     * @param phone The phone
     */
    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    /**
     * @return The pincode
     */
    public List<String> getPincode() {
        return pincode;
    }

    /**
     * @param pincode The pincode
     */
    public void setPincode(List<String> pincode) {
        this.pincode = pincode;
    }

    /**
     * @return The streetAddress
     */
    public List<String> getStreetAddress() {
        return streetAddress;
    }

    /**
     * @param streetAddress The street_address
     */
    public void setStreetAddress(List<String> streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * @return The city
     */
    public List<String> getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(List<String> city) {
        this.city = city;
    }

    /**
     * @return The state
     */
    public List<String> getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(List<String> state) {
        this.state = state;
    }

    /**
     * @return The country
     */
    public List<String> getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(List<String> country) {
        this.country = country;
    }

}
