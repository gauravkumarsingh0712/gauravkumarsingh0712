
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Sorts {

    @Expose
    private String key;
    @Expose
    private List<List__> list = new ArrayList<List__>();

    /**
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return The list
     */
    public List<List__> getList() {
        return list;
    }

    /**
     * @param list The list
     */
    public void setList(List<List__> list) {
        this.list = list;
    }

}
