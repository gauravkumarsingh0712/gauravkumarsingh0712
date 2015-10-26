
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class IdFilter {

    @Expose
    private String name;
    @Expose
    private String key;
    @Expose
    private List<List_> list = new ArrayList<List_>();
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
     *     The key
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @param key
     *     The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 
     * @return
     *     The list
     */
    public List<List_> getList() {
        return list;
    }

    /**
     * 
     * @param list
     *     The list
     */
    public void setList(List<List_> list) {
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
