
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class RangeFilter {

    @Expose
    private String name;
    @Expose
    private Keys keys;
    @Expose
    private java.util.List<List> list = new ArrayList<List>();
    @Expose
    private String position;

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The keys
     */
    public Keys getKeys() {
        return keys;
    }

    /**
     * 
     * @param keys
     *     The keys
     */
    public void setKeys(Keys keys) {
        this.keys = keys;
    }

    /**
     * 
     * @return
     *     The list
     */
    public java.util.List<List> getList() {
        return list;
    }

    /**
     * 
     * @param list
     *     The list
     */
    public void setList(java.util.List<List> list) {
        this.list = list;
    }

    /**
     * 
     * @return
     *     The position
     */
    public String getPosition() {
        return position;
    }

    /**
     * 
     * @param position
     *     The position
     */
    public void setPosition(String position) {
        this.position = position;
    }

}
