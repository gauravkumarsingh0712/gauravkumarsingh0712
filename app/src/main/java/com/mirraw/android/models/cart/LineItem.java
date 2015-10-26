
package com.mirraw.android.models.cart;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class LineItem {

    @Expose
    private Integer id;
    @SerializedName("design_id")
    @Expose
    private Integer designId;
    @Expose
    private String title;
    @Expose
    private Sizes sizes;
    @Expose
    private String designer;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @SerializedName("snapshot_price")
    @Expose
    private String snapshotPrice;
    @Expose
    private Integer quantity;
    @Expose
    private String total;
    @SerializedName("line_item_addons")
    @Expose
    private List<LineItemAddons> lineItemAddons = new ArrayList<LineItemAddons>();
    @Expose
    private Object notes;
    @Expose
    private Variant variant;
    @Expose
    private Integer stock;
    @SerializedName("required_stock")
    @Expose
    private Boolean requiredStock;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The designId
     */
    public Integer getDesignId() {
        return designId;
    }

    /**
     * @param designId The design_id
     */
    public void setDesignId(Integer designId) {
        this.designId = designId;
    }

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
     * @return The designer
     */
    public String getDesigner() {
        return designer;
    }

    /**
     * @param designer The designer
     */
    public void setDesigner(String designer) {
        this.designer = designer;
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
     * @return The snapshotPrice
     */
    public String getSnapshotPrice() {
        return snapshotPrice;
    }

    /**
     * @param snapshotPrice The snapshot_price
     */
    public void setSnapshotPrice(String snapshotPrice) {
        this.snapshotPrice = snapshotPrice;
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

    /**
     * @return The lineItemAddons
     */
    public List<LineItemAddons> getLineItemAddons() {
        return lineItemAddons;
    }

    /**
     * @param lineItemAddons The line_item_addons
     */
    public void setLineItemAddons(List<LineItemAddons> lineItemAddons) {
        this.lineItemAddons = lineItemAddons;
    }

    /**
     * @return The notes
     */
    public Object getNotes() {
        return notes;
    }

    /**
     * @param notes The notes
     */
    public void setNotes(Object notes) {
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
     * @return The stock
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * @param stock The stock
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * @return The requiredStock
     */
    public Boolean getRequiredStock() {
        return requiredStock;
    }

    /**
     * @param requiredStock The required_stock
     */
    public void setRequiredStock(Boolean requiredStock) {
        this.requiredStock = requiredStock;
    }

}
