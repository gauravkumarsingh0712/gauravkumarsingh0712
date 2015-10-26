
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;

public class List {

    @Expose
    private Values values;
    @Expose
    private Integer count;
    @Expose
    private String name;

    private boolean selected;


    /**
     * 
     * @return
     *     The values
     */
    public Values getValues() {
        return values;
    }

    /**
     * 
     * @param values
     *     The values
     */
    public void setValues(Values values) {
        this.values = values;
    }

    /**
     * 
     * @return
     *     The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 
     * @param count
     *     The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

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


    public boolean getSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }

}
