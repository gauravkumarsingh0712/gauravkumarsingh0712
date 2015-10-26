
package com.mirraw.android.models.address;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Country {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String code;
    @SerializedName("has_states")
    @Expose
    private Boolean hasStates;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return The hasStates
     */
    public Boolean getHasStates() {
        return hasStates;
    }

    /**
     * @param hasStates The has_states
     */
    public void setHasStates(Boolean hasStates) {
        this.hasStates = hasStates;
    }

}