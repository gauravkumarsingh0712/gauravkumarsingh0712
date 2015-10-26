
package com.mirraw.android.models.Register;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Data {

    @Expose
    private Object id;
    @Expose
    private String email;
    @SerializedName("accountable_id")
    @Expose
    private Object accountableId;
    @SerializedName("accountable_type")
    @Expose
    private Object accountableType;
    @Expose
    private Object uid;
    @Expose
    private String provider;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;
    @Expose
    private Object notified;
    @SerializedName("notified_date")
    @Expose
    private Object notifiedDate;
    @Expose
    private Object token;

    /**
     *
     * @return
     * The id
     */
    public Object getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Object id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The accountableId
     */
    public Object getAccountableId() {
        return accountableId;
    }

    /**
     *
     * @param accountableId
     * The accountable_id
     */
    public void setAccountableId(Object accountableId) {
        this.accountableId = accountableId;
    }

    /**
     *
     * @return
     * The accountableType
     */
    public Object getAccountableType() {
        return accountableType;
    }

    /**
     *
     * @param accountableType
     * The accountable_type
     */
    public void setAccountableType(Object accountableType) {
        this.accountableType = accountableType;
    }

    /**
     *
     * @return
     * The uid
     */
    public Object getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     * The uid
     */
    public void setUid(Object uid) {
        this.uid = uid;
    }

    /**
     *
     * @return
     * The provider
     */
    public String getProvider() {
        return provider;
    }

    /**
     *
     * @param provider
     * The provider
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     *
     * @return
     * The createdAt
     */
    public Object getCreatedAt() {
        return createdAt;
    }

    /**
     *
     * @param createdAt
     * The created_at
     */
    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    /**
     *
     * @return
     * The updatedAt
     */
    public Object getUpdatedAt() {
        return updatedAt;
    }

    /**
     *
     * @param updatedAt
     * The updated_at
     */
    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     *
     * @return
     * The notified
     */
    public Object getNotified() {
        return notified;
    }

    /**
     *
     * @param notified
     * The notified
     */
    public void setNotified(Object notified) {
        this.notified = notified;
    }

    /**
     *
     * @return
     * The notifiedDate
     */
    public Object getNotifiedDate() {
        return notifiedDate;
    }

    /**
     *
     * @param notifiedDate
     * The notified_date
     */
    public void setNotifiedDate(Object notifiedDate) {
        this.notifiedDate = notifiedDate;
    }

    /**
     *
     * @return
     * The token
     */
    public Object getToken() {
        return token;
    }

    /**
     *
     * @param token
     * The token
     */
    public void setToken(Object token) {
        this.token = token;
    }

}