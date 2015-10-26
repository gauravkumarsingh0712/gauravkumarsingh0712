
package com.mirraw.android.models.cart;

import com.google.gson.annotations.Expose;

public class CartItems {

    @Expose
    private Cart cart;

    /**
     * 
     * @return
     *     The cart
     */
    public Cart getCart() {
        return cart;
    }

    /**
     * 
     * @param cart
     *     The cart
     */
    public void setCart(Cart cart) {
        this.cart = cart;
    }

}
