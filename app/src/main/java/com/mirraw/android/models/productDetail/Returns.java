
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Returns {

    @Expose
    private String time;
    @Expose
    private String process;
    @SerializedName("refunds_or_replacements")
    @Expose
    private String refundsOrReplacements;

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
     *     The process
     */
    public String getProcess() {
        return process;
    }

    /**
     * 
     * @param process
     *     The process
     */
    public void setProcess(String process) {
        this.process = process;
    }

    /**
     * 
     * @return
     *     The refundsOrReplacements
     */
    public String getRefundsOrReplacements() {
        return refundsOrReplacements;
    }

    /**
     * 
     * @param refundsOrReplacements
     *     The refunds_or_replacements
     */
    public void setRefundsOrReplacements(String refundsOrReplacements) {
        this.refundsOrReplacements = refundsOrReplacements;
    }

}
