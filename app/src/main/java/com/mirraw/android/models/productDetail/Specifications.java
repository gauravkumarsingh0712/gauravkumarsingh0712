
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Specifications {

    @SerializedName("product_id")
    @Expose
    private Integer productId;
    @Expose
    private String specification;
    @SerializedName("international_shipping")
    @Expose
    private Boolean internationalShipping;
    @Expose
    private List<Property> properties = new ArrayList<Property>();
    @Expose
    private List<Designable> designable = new ArrayList<Designable>();

    /**
     * 
     * @return
     *     The productId
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * 
     * @param productId
     *     The product_id
     */
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    /**
     * 
     * @return
     *     The specification
     */
    public String getSpecification() {
        return specification;
    }

    /**
     * 
     * @param specification
     *     The specification
     */
    public void setSpecification(String specification) {
        this.specification = specification;
    }

    /**
     * 
     * @return
     *     The internationalShipping
     */
    public Boolean getInternationalShipping() {
        return internationalShipping;
    }

    /**
     * 
     * @param internationalShipping
     *     The international_shipping
     */
    public void setInternationalShipping(Boolean internationalShipping) {
        this.internationalShipping = internationalShipping;
    }

    /**
     * 
     * @return
     *     The properties
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * 
     * @param properties
     *     The properties
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    /**
     * 
     * @return
     *     The designable
     */
    public List<Designable> getDesignable() {
        return designable;
    }

    /**
     * 
     * @param designable
     *     The designable
     */
    public void setDesignable(List<Designable> designable) {
        this.designable = designable;
    }

}
