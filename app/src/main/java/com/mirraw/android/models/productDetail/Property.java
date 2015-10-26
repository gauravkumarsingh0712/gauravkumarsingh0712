
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Property {

    @Expose
    private String type;
    @Expose
    private List<String> value = new ArrayList<String>();

    /**
     * 
     * @return
     *     The type
     */
    public String getType() {
        return type;
    }

    /**
     * 
     * @param type
     *     The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 
     * @return
     *     The value
     */
    public List<String> getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     The value
     */
    public void setValue(List<String> value) {
        this.value = value;
    }

}
