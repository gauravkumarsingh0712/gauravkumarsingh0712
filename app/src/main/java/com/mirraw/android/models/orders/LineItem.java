
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LineItem {

    @Expose
    private String title;
    @Expose
    private Sizes sizes;
    @SerializedName("designer_name")
    @Expose
    private String designerName;
    @Expose
    private Integer quantity;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @Expose
    private String price;
    @SerializedName("line_item_addons")
    @Expose
    private List<LineItemAddon> lineItemAddons = new ArrayList<LineItemAddon>();
    @Expose
    private String notes;
    @Expose
    private Variant variant;
    @Expose
    private String total;

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The sizes
     */
    public Sizes getSizes() {
        return sizes;
    }

    /**
     * @param sizes The sizes
     */
    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

    /**
     * @return The designerName
     */
    public String getDesignerName() {
        return designerName;
    }

    /**
     * @param designerName The designer_name
     */
    public void setDesignerName(String designerName) {
        this.designerName = designerName;
    }

    /**
     * @return The quantity
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * @param quantity The quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * @return The hexSymbol
     */
    public String getHexSymbol() {
        return hexSymbol;
    }

    /**
     * @param hexSymbol The hex_symbol
     */
    public void setHexSymbol(String hexSymbol) {
        this.hexSymbol = hexSymbol;
    }

    /**
     * @return The currencySymbol
     */
    public String getCurrencySymbol() {
        return currencySymbol;
    }

    /**
     * @param currencySymbol The currency_symbol
     */
    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    /**
     * @return The price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(String price) {
        this.price = price;
    }

    /**
     * @return The lineItemAddons
     */
    public List<LineItemAddon> getLineItemAddons() {
        return lineItemAddons;
    }

    /**
     * @param lineItemAddons The line_item_addons
     */
    public void setLineItemAddons(List<LineItemAddon> lineItemAddons) {
        this.lineItemAddons = lineItemAddons;
    }

    /**
     * @return The notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * @param notes The notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @return The variant
     */
    public Variant getVariant() {
        return variant;
    }

    /**
     * @param variant The variant
     */
    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    /**
     * @return The total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total The total
     */
    public void setTotal(String total) {
        this.total = total;
    }

}
