
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Variant {

    @Expose
    private Integer id;
    @Expose
    private Integer quantity;
    @SerializedName("option_type_values")
    @Expose
    private List<OptionTypeValue> optionTypeValues = new ArrayList<OptionTypeValue>();

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     *
     * @param quantity
     * The quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     *
     * @return
     * The optionTypeValues
     */
    public List<OptionTypeValue> getOptionTypeValues() {
        return optionTypeValues;
    }

    /**
     *
     * @param optionTypeValues
     * The option_type_values
     */
    public void setOptionTypeValues(List<OptionTypeValue> optionTypeValues) {
        this.optionTypeValues = optionTypeValues;
    }

}