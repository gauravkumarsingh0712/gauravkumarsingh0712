
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;

public class Stitching {

    @Expose
    private String time;
    @Expose
    private String charges;
    @Expose
    private String returns;
    @Expose
    private String instructions;

    /**
     * 
     * @return
     *     The time
     */
    public String getTime() {
        return time;
    }

    /**
     * 
     * @param time
     *     The time
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * 
     * @return
     *     The charges
     */
    public String getCharges() {
        return charges;
    }

    /**
     * 
     * @param charges
     *     The charges
     */
    public void setCharges(String charges) {
        this.charges = charges;
    }

    /**
     * 
     * @return
     *     The returns
     */
    public String getReturns() {
        return returns;
    }

    /**
     * 
     * @param returns
     *     The returns
     */
    public void setReturns(String returns) {
        this.returns = returns;
    }

    /**
     * 
     * @return
     *     The instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * 
     * @param instructions
     *     The instructions
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

}
