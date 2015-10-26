
package com.mirraw.android.models.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    @SerializedName("line_items")
    @Expose
    private List<LineItem> lineItems = new ArrayList<LineItem>();
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @SerializedName("item_total")
    @Expose
    private String itemTotal;
    @Expose
    private String discount;
    @Expose
    private String total;

    /**
     * 
     * @return
     *     The lineItems
     */
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    /**
     * 
     * @param lineItems
     *     The line_items
     */
    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
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
     *     The itemTotal
     */
    public String getItemTotal() {
        return itemTotal;
    }

    /**
     * 
     * @param itemTotal
     *     The item_total
     */
    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }

    /**
     * 
     * @return
     *     The discount
     */
    public String getDiscount() {
        return discount;
    }

    /**
     * 
     * @param discount
     *     The discount
     */
    public void setDiscount(String discount) {
        this.discount = discount;
    }

    /**
     * 
     * @return
     *     The total
     */
    public String getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

}
