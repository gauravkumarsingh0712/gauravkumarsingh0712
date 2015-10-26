
package com.mirraw.android.models.Register;


import com.google.gson.annotations.Expose;


public class RegisterError {

    @Expose
    private String status;
    @Expose
    private Data data;
    @Expose
    private Errors errors;

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The data
     */
    public Data getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(Data data) {
        this.data = data;
    }

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