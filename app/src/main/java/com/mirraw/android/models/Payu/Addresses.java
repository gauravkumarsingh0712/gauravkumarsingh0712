
package com.mirraw.android.models.Payu;

import com.google.gson.annotations.Expose;

public class Addresses {

    @Expose
    private Billing billing;
    @Expose
    private Shipping shipping;

    /**
     * 
     * @return
     *     The billing
     */
    public Billing getBilling() {
        return billing;
    }

    /**
     * 
     * @param billing
     *     The billing
     */
    public void setBilling(Billing billing) {
        this.billing = billing;
    }

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

}
