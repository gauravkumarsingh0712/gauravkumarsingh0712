
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Design {

    @Expose
    private Integer id;
    @Expose
    private String title;
    @SerializedName("design_href")
    @Expose
    private String designHref;
    @Expose
    private Double price;
    @SerializedName("discount_price")
    @Expose
    private Double discountPrice;
    @SerializedName("discount_percent")
    @Expose
    private Double discountPercent;
    @Expose
    private String designer;
    @Expose
    private Sizes sizes;

    /**
     *
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     *     The designHref
     */
    public String getDesignHref() {
        return designHref;
    }

    /**
     *
     * @param designHref
     *     The design_href
     */
    public void setDesignHref(String designHref) {
        this.designHref = designHref;
    }

    /**
     *
     * @return
     *     The price
     */
    public Double getPrice() {
        return price;
    }

    /**
     *
     * @param price
     *     The price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     *
     * @return
     *     The discountPrice
     */
    public Double getDiscountPrice() {
        return discountPrice;
    }

    /**
     *
     * @param discountPrice
     *     The discount_price
     */
    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    /**
     *
     * @return
     *     The discountPercent
     */
    public Double getDiscountPercent() {
        return discountPercent;
    }

    /**
     *
     * @param discountPercent
     *     The discount_percent
     */
    public void setDiscountPercent(Double discountPercent) {
        this.discountPercent = discountPercent;
    }

    /**
     *
     * @return
     *     The designer
     */
    public String getDesigner() {
        return designer;
    }

    /**
     *
     * @param designer
     *     The designer
     */
    public void setDesigner(String designer) {
        this.designer = designer;
    }

    /**
     *
     * @return
     *     The sizes
     */
    public Sizes getSizes() {
        return sizes;
    }

    /**
     *
     * @param sizes
     *     The sizes
     */
    public void setSizes(Sizes sizes) {
        this.sizes = sizes;
    }

}
