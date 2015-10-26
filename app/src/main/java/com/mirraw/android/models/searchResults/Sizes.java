
package com.mirraw.android.models.searchResults;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sizes {

    @Expose
    private String thumb;
    @Expose
    private String small;
    @SerializedName("small_m")
    @Expose
    private String smallM;
    @Expose
    private String large;
    @Expose
    private String original;

    /**
     * @return The thumb
     */
    public String getThumb() {
        return thumb;
    }

    /**
     * @param thumb The thumb
     */
    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    /**
     * @return The small
     */
    public String getSmall() {
        return small;
    }

    /**
     * @param small The small
     */
    public void setSmall(String small) {
        this.small = small;
    }

    /**
     *
     * @return
     * The smallM
     */
    public String getSmallM() {
        return smallM;
    }

    /**
     *
     * @param smallM
     * The small_m
     */
    public void setSmallM(String smallM) {
        this.smallM = smallM;
    }

    /**
     * @return The large
     */
    public String getLarge() {
        return large;
    }

    /**
     * @param large The large
     */
    public void setLarge(String large) {
        this.large = large;
    }

    /**
     * @return The original
     */
    public String getOriginal() {
        return original;
    }

    /**
     * @param original The original
     */
    public void setOriginal(String original) {
        this.original = original;
    }

}
