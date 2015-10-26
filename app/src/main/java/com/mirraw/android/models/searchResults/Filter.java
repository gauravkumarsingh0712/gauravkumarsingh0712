package com.mirraw.android.models.searchResults;


import com.google.gson.annotations.Expose;


public class Filter {

    @Expose
    private String name;
    @Expose
    private String id;
    @Expose
    private Values values;

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The values
     */
    public Values getValues() {
        return values;
    }

    /**
     *
     * @param values
     * The values
     */
    public void setValues(Values values) {
        this.values = values;
    }

}