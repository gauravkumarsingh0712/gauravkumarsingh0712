
package com.mirraw.android.models.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OptionTypeValue {

    @SerializedName("p_name")
    @Expose
    private String pName;
    @SerializedName("option_type")
    @Expose
    private String optionType;

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
     *     The optionType
     */
    public String getOptionType() {
        return optionType;
    }

    /**
     * 
     * @param optionType
     *     The option_type
     */
    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

}
