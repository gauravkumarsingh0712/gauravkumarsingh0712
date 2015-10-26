
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AddonType {

    @Expose
    private String name;
    @Expose
    private Integer position;
    @SerializedName("addon_type_values")
    @Expose
    private List<AddonTypeValue> addonTypeValues = new ArrayList<AddonTypeValue>();

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
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
     *     The addonTypeValues
     */
    public List<AddonTypeValue> getAddonTypeValues() {
        return addonTypeValues;
    }

    /**
     * 
     * @param addonTypeValues
     *     The addon_type_values
     */
    public void setAddonTypeValues(List<AddonTypeValue> addonTypeValues) {
        this.addonTypeValues = addonTypeValues;
    }

}
