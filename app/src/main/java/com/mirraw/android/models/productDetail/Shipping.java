
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shipping {

    @SerializedName("in_india")
    @Expose
    private InIndia inIndia;
    @SerializedName("out_of_india")
    @Expose
    private OutOfIndia outOfIndia;

    /**
     * 
     * @return
     *     The inIndia
     */
    public InIndia getInIndia() {
        return inIndia;
    }

    /**
     * 
     * @param inIndia
     *     The in_india
     */
    public void setInIndia(InIndia inIndia) {
        this.inIndia = inIndia;
    }

    /**
     * 
     * @return
     *     The outOfIndia
     */
    public OutOfIndia getOutOfIndia() {
        return outOfIndia;
    }

    /**
     * 
     * @param outOfIndia
     *     The out_of_india
     */
    public void setOutOfIndia(OutOfIndia outOfIndia) {
        this.outOfIndia = outOfIndia;
    }

}
