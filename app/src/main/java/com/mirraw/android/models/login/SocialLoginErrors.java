
package com.mirraw.android.models.login;


import com.google.gson.annotations.Expose;


public class SocialLoginErrors {

    @Expose
    private String error;

    /**
     * @return The error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error The error
     */
    public void setError(String error) {
        this.error = error;
    }

}
