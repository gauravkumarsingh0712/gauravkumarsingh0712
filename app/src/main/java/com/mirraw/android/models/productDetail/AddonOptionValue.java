
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddonOptionValue {

    @Expose
    private Integer id;
    @SerializedName("p_name")
    @Expose
    private String pName;
    @Expose
    private Integer position;

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

}
