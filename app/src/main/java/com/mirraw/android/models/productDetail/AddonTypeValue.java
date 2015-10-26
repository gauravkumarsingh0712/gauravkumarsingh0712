
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddonTypeValue {

    @Expose
    private Integer id;
    @SerializedName("p_name")
    @Expose
    private String pName;
    @Expose
    private Integer position;
    @SerializedName("prod_time")
    @Expose
    private Integer prodTime;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @Expose
    private Double price;
    @Expose
    private Boolean hasChild = false;
    @SerializedName("addon_option_types")
    @Expose
    private List<AddonOptionType> addonOptionTypes = new ArrayList<AddonOptionType>();

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
     *     The pName
     */
    public String getPName() {
        return pName;
    }

    /**
     * 
     * @param pName
     *     The p_name
     */
    public void setPName(String pName) {
        this.pName = pName;
    }

    /**
     * 
     * @return
     *     The position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * 
     * @param position
     *     The position
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * 
     * @return
     *     The prodTime
     */
    public Integer getProdTime() {
        return prodTime;
    }

    /**
     * 
     * @param prodTime
     *     The prod_time
     */
    public void setProdTime(Integer prodTime) {
        this.prodTime = prodTime;
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
     *     The price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * 
     * @param price
     *     The price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 
     * @return
     *     The addonOptionTypes
     */
    public List<AddonOptionType> getAddonOptionTypes() {
        return addonOptionTypes;
    }

    /**
     * 
     * @param addonOptionTypes
     *     The addon_option_types
     */
    public void setAddonOptionTypes(List<AddonOptionType> addonOptionTypes) {
        this.addonOptionTypes = addonOptionTypes;
    }

    public Boolean getHasChild() {
        return hasChild;
    }

    public void setHasChild(Boolean hasChild) {
        this.hasChild = hasChild;
    }

}