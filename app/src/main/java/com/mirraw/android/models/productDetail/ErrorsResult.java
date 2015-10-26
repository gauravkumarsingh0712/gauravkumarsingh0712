package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ErrorsResult {

    @SerializedName("errors")
    @Expose
    private Errors errors;

    /**
     * @return The errors
     */
    public Errors getErrors() {
        return errors;
    }

    /**
     * @param errors The errors
     */
    public void setErrors(Errors errors) {
        this.errors = errors;
    }

}