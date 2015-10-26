
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Payments {

    @SerializedName("in_india")
    @Expose
    private List<String> inIndia = new ArrayList<String>();
    @SerializedName("out_of_india")
    @Expose
    private List<String> outOfIndia = new ArrayList<String>();

    /**
     * 
     * @return
     *     The inIndia
     */
    public List<String> getInIndia() {
        return inIndia;
    }

    /**
     * 
     * @param inIndia
     *     The in_india
     */
    public void setInIndia(List<String> inIndia) {
        this.inIndia = inIndia;
    }

    /**
     * 
     * @return
     *     The outOfIndia
     */
    public List<String> getOutOfIndia() {
        return outOfIndia;
    }

    /**
     * 
     * @param outOfIndia
     *     The out_of_india
     */
    public void setOutOfIndia(List<String> outOfIndia) {
        this.outOfIndia = outOfIndia;
    }

}
