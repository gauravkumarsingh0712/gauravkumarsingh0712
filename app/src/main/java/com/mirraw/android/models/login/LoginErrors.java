package com.mirraw.android.models.login;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class LoginErrors {

    @Expose
    private Boolean success;
    @Expose
    private List<String> errors = new ArrayList<String>();

    /**
     * @return The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * @return The errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     * @param errors The errors
     */
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}