
package com.mirraw.android.models;

import com.google.gson.annotations.Expose;

public class Photo {

    @Expose
    private Sizes sizes;

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
