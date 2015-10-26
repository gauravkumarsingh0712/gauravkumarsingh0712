
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddonOptionType {

    @Expose
    private Integer id;
    @SerializedName("p_name")
    @Expose
    private String pName;
    @Expose
    private Integer position;
    @Expose
    private Integer selectedId;
    @SerializedName("addon_option_values")
    @Expose
    private List<AddonOptionValue> addonOptionValues = new ArrayList<AddonOptionValue>();


    private List<AddonOptionValue> addonTypeValues = new ArrayList<AddonOptionValue>();

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
     * @return The pName
     */
    public String getPName() {
        return pName;
    }

    /**
     * @param pName The p_name
     */
    public void setPName(String pName) {
        this.pName = pName;
    }

    /**
     * @return The position
     */
    public Integer getPosition() {
        return position;
    }

    /**
     * @param position The position
     */
    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * @return The addonOptionValues
     */
    public List<AddonOptionValue> getAddonOptionValues() {
        return addonOptionValues;
    }

    /**
     * @param addonOptionValues The addon_option_values
     */
    public void setAddonOptionValues(List<AddonOptionValue> addonOptionValues) {
        this.addonOptionValues = addonOptionValues;
    }

    public Integer getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(Integer selectedId) {
        this.selectedId = selectedId;
    }

}