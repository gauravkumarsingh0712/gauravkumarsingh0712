
package com.mirraw.android.models.PaymentOptionsDetail;

import com.google.gson.annotations.Expose;

public class AvailablePaymentOptions {

    @Expose
    private Details details;

    /**
     * @return The details
     */
    public Details getDetails() {
        return details;
    }

    /**
     * @param details The details
     */
    public void setDetails(Details details) {
        this.details = details;
    }

}
