
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Variant {

    @SerializedName("variant_id")
    @Expose
    private Integer variantId;
    @SerializedName("option_type_values")
    @Expose
    private List<OptionTypeValue> optionTypeValues = new ArrayList<OptionTypeValue>();

    /**
     * 
     * @return
     *     The variantId
     */
    public Integer getVariantId() {
        return variantId;
    }

    /**
     * 
     * @param variantId
     *     The variant_id
     */
    public void setVariantId(Integer variantId) {
        this.variantId = variantId;
    }

    /**
     * 
     * @return
     *     The optionTypeValues
     */
    public List<OptionTypeValue> getOptionTypeValues() {
        return optionTypeValues;
    }

    /**
     * 
     * @param optionTypeValues
     *     The option_type_values
     */
    public void setOptionTypeValues(List<OptionTypeValue> optionTypeValues) {
        this.optionTypeValues = optionTypeValues;
    }

}
