
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OptionTypeValue {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("option_type")
    @Expose
    private String optionType;

    /**
     * 
     * @return
     *     The pName
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
