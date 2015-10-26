
package com.mirraw.android.models.Register;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Errors {

    @Expose
    private List<String> email = new ArrayList<String>();
    @SerializedName("full_messages")
    @Expose
    private List<String> fullMessages = new ArrayList<String>();

    /**
     *
     * @return
     * The email
     */
    public List<String> getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(List<String> email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The fullMessages
     */
    public List<String> getFullMessages() {
        return fullMessages;
    }

    /**
     *
     * @param fullMessages
     * The full_messages
     */
    public void setFullMessages(List<String> fullMessages) {
        this.fullMessages = fullMessages;
    }

}