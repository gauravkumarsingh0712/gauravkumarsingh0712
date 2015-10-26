
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Filters {

    @SerializedName("range_filters")
    @Expose
    private List<RangeFilter> rangeFilters = new ArrayList<RangeFilter>();
    @SerializedName("id_filters")
    @Expose
    private List<IdFilter> idFilters = new ArrayList<IdFilter>();

    /**
     * 
     * @return
     *     The rangeFilters
     */
    public List<RangeFilter> getRangeFilters() {
        return rangeFilters;
    }

    /**
     * 
     * @param rangeFilters
     *     The range_filters
     */
    public void setRangeFilters(List<RangeFilter> rangeFilters) {
        this.rangeFilters = rangeFilters;
    }

    /**
     * 
     * @return
     *     The idFilters
     */
    public List<IdFilter> getIdFilters() {
        return idFilters;
    }

    /**
     * 
     * @param idFilters
     *     The id_filters
     */
    public void setIdFilters(List<IdFilter> idFilters) {
        this.idFilters = idFilters;
    }

}
