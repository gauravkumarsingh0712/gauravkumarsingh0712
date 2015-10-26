
package com.mirraw.android.models.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LineItemAddon {

    @Expose
    private String name;
    @SerializedName("snapshot_price")
    @Expose
    private String snapshotPrice;

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The snapshotPrice
     */
    public String getSnapshotPrice() {
        return snapshotPrice;
    }

    /**
     * 
     * @param snapshotPrice
     *     The snapshot_price
     */
    public void setSnapshotPrice(String snapshotPrice) {
        this.snapshotPrice = snapshotPrice;
    }

}
