
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Search {

    @Expose
    private Integer results;
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @SerializedName("previous_page")
    @Expose
    private Integer previousPage;
    @SerializedName("next_page")
    @Expose
    private Integer nextPage;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @Expose
    private List<Design> designs = new ArrayList<Design>();
    @Expose
    private Filters filters;
    @Expose
    private Sorts sorts;

    /**
     * 
     * @return
     *     The results
     */
    public Integer getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(Integer results) {
        this.results = results;
    }

    /**
     * 
     * @return
     *     The totalPages
     */
    public Integer getTotalPages() {
        return totalPages;
    }

    /**
     * 
     * @param totalPages
     *     The total_pages
     */
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * 
     * @return
     *     The previousPage
     */
    public Integer getPreviousPage() {
        return previousPage;
    }

    /**
     * 
     * @param previousPage
     *     The previous_page
     */
    public void setPreviousPage(Integer previousPage) {
        this.previousPage = previousPage;
    }

    /**
     * 
     * @return
     *     The nextPage
     */
    public Integer getNextPage() {
        return nextPage;
    }

    /**
     * 
     * @param nextPage
     *     The next_page
     */
    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    /**
     * 
     * @return
     *     The hexSymbol
     */
    public String getHexSymbol() {
        return hexSymbol;
    }

    /**
     * 
     * @param hexSymbol
     *     The hex_symbol
     */
    public void setHexSymbol(String hexSymbol) {
        this.hexSymbol = hexSymbol;
    }

    /**
     * 
     * @return
     *     The designs
     */
    public List<Design> getDesigns() {
        return designs;
    }

    /**
     * 
     * @param designs
     *     The designs
     */
    public void setDesigns(List<Design> designs) {
        this.designs = designs;
    }

    /**
     * 
     * @return
     *     The filters
     */
    public Filters getFilters() {
        return filters;
    }

    /**
     * 
     * @param filters
     *     The filters
     */
    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    /**
     * 
     * @return
     *     The sorts
     */
    public Sorts getSorts() {
        return sorts;
    }

    /**
     * 
     * @param sorts
     *     The sorts
     */
    public void setSorts(Sorts sorts) {
        this.sorts = sorts;
    }

}
