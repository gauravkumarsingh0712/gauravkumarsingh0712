
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;

public class TabDetails {

    @Expose
    private Shipping shipping;
    @Expose
    private Stitching stitching;
    @Expose
    private Payments payments;
    @Expose
    private Returns returns;

    /**
     * 
     * @return
     *     The shipping
     */
    public Shipping getShipping() {
        return shipping;
    }

    /**
     * 
     * @param shipping
     *     The shipping
     */
    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    /**
     * 
     * @return
     *     The stitching
     */
    public Stitching getStitching() {
        return stitching;
    }

    /**
     * 
     * @param stitching
     *     The stitching
     */
    public void setStitching(Stitching stitching) {
        this.stitching = stitching;
    }

    /**
     * 
     * @return
     *     The payments
     */
    public Payments getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(Payments payments) {
        this.payments = payments;
    }

    /**
     * 
     * @return
     *     The returns
     */
    public Returns getReturns() {
        return returns;
    }

    /**
     * 
     * @param returns
     *     The returns
     */
    public void setReturns(Returns returns) {
        this.returns = returns;
    }

}
