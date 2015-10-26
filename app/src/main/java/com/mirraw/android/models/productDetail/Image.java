
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {

    @SerializedName("is_master")
    @Expose
    private Boolean isMaster;
    @Expose
    private Sizes sizes;

    /**
     * 
     * @return
     *     The isMaster
     */
    public Boolean getIsMaster() {
        return isMaster;
    }

    /**
     * 
     * @param isMaster
     *     The is_master
     */
    public void setIsMaster(Boolean isMaster) {
        this.isMaster = isMaster;
    }

    /**
     * 
     * @return
     *     The sizes
     */
    public Sizes getSizes() {
        return sizes;
    }

    /**
     * 
     * @param sizes
     *     The sizes
     */
    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

}
