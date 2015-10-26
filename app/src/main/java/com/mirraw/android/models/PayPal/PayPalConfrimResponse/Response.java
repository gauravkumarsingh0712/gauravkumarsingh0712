package com.mirraw.android.models.PayPal.PayPalConfrimResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Created by pavitra on 24/8/15.
 */
public class Response {


    @SerializedName("create_time")
    @Expose
    private String createTime;
    @Expose
    private String id;
    @Expose
    private String intent;
    @Expose
    private String state;

    /**
     * @return The createTime
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime The create_time
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The intent
     */
    public String getIntent() {
        return intent;
    }

    /**
     * @param intent The intent
     */
    public void setIntent(String intent) {
        this.intent = intent;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
