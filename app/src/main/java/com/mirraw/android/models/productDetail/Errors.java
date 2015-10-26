package com.mirraw.android.models.productDetail;

/**
 * Created by abc on 8/24/2015.
 */

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Errors {

    @SerializedName("stock")
    @Expose
    private List<String> stock = new ArrayList<String>();

    @SerializedName("design_id")
    @Expose
    private List<String> designId = new ArrayList<String>();

    /**
     * @return The designId
     */
    public List<String> getDesignId() {
        return designId;
    }

    /**
     * @param designId The design_id
     */
    public void setDesignId(List<String> designId) {
        this.designId = designId;
    }

    /**
     * @return The stock
     */
    public List<String> getStock() {
        return stock;
    }

    /**
     * @param stock The stock
     */
    public void setStock(List<String> stock) {
        this.stock = stock;
    }

}