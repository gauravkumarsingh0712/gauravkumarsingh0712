
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails {

    @Expose
    private String id;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @Expose
    private String state;
    @SerializedName("courier_company")
    @Expose
    private String courierCompany;
    @SerializedName("tracking_number")
    @Expose
    private String trackingNumber;
    @SerializedName("hex_symbol")
    @Expose
    private String hexSymbol;
    @SerializedName("designer_orders")
    @Expose
    private List<DesignerOrder> designerOrders = new ArrayList<DesignerOrder>();
    @SerializedName("currency_symbol")
    @Expose
    private String currencySymbol;
    @SerializedName("item_total")
    @Expose
    private String itemTotal;
    @Expose
    private String discounts;
    @Expose
    private String shipping;
    @Expose
    private String cod;
    @Expose
    private String total;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return The courierCompany
     */
    public String getCourierCompany() {
        return courierCompany;
    }

    /**
     * @param courierCompany The courier_company
     */
    public void setCourierCompany(String courierCompany) {
        this.courierCompany = courierCompany;
    }

    /**
     * @return The trackingNumber
     */
    public String getTrackingNumber() {
        return trackingNumber;
    }

    /**
     * @param trackingNumber The tracking_number
     */
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
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
     * @return The designerOrders
     */
    public List<DesignerOrder> getDesignerOrders() {
        return designerOrders;
    }

    /**
     * @param designerOrders The designer_orders
     */
    public void setDesignerOrders(List<DesignerOrder> designerOrders) {
        this.designerOrders = designerOrders;
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
     * @return The itemTotal
     */
    public String getItemTotal() {
        return itemTotal;
    }

    /**
     * @param itemTotal The item_total
     */
    public void setItemTotal(String itemTotal) {
        this.itemTotal = itemTotal;
    }

    /**
     * @return The discounts
     */
    public String getDiscounts() {
        return discounts;
    }

    /**
     * @param discounts The discounts
     */
    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }

    /**
     * @return The shipping
     */
    public String getShipping() {
        return shipping;
    }

    /**
     * @param shipping The shipping
     */
    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    /**
     * @return The cod
     */
    public String getCod() {
        return cod;
    }

    /**
     * @param cod The cod
     */
    public void setCod(String cod) {
        this.cod = cod;
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
