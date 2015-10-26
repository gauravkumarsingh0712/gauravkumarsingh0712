
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("is_master")
    @Expose
    private boolean isMaster;
    @Expose
    private Sizes sizes;

    /**
     * 
     * @return
     *     The isMaster
     */
    public boolean isIsMaster() {
        return isMaster;
    }

    /**
     * 
     * @param isMaster
     *     The is_master
     */
    public void setIsMaster(boolean isMaster) {
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
