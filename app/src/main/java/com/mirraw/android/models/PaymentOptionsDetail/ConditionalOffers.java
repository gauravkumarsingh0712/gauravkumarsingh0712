
package com.mirraw.android.models.PaymentOptionsDetail;

import com.google.gson.annotations.Expose;

public class ConditionalOffers {

    @Expose
    private Shipping shipping;

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
