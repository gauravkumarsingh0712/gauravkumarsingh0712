
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Orders {

    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    @Expose
    private ArrayList<Order> orders = new ArrayList<Order>();

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
     *     The orders
     */
    public ArrayList<Order> getOrders() {
        return orders;
    }

    /**
     * 
     * @param orders
     *     The orders
     */
    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

}
