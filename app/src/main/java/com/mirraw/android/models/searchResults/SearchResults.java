
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;

public class SearchResults {

    @Expose
    private Search search;

    /**
     * 
     * @return
     *     The search
     */
    public Search getSearch() {
        return search;
    }

    /**
     * 
     * @param search
     *     The search
     */
    public void setSearch(Search search) {
        this.search = search;
    }

}
