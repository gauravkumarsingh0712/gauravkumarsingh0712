
package com.mirraw.android.models.productDetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

    @Expose
    private String occasion;
    @Expose
    private String work;
    @Expose
    private String look;
    @SerializedName("bottom fabric")
    @Expose
    private String bottomFabric;
    @SerializedName("bottom color")
    @Expose
    private String bottomColor;
    @SerializedName("top color")
    @Expose
    private String topColor;
    @SerializedName("top fabric")
    @Expose
    private String topFabric;
    @SerializedName("chunari fabric")
    @Expose
    private String chunariFabric;
    @SerializedName("chunari color")
    @Expose
    private String chunariColor;
    @SerializedName("bottom style")
    @Expose
    private String bottomStyle;

    /**
     * 
     * @return
     *     The occasion
     */
    public String getOccasion() {
        return occasion;
    }

    /**
     * 
     * @param occasion
     *     The occasion
     */
    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    /**
     * 
     * @return
     *     The work
     */
    public String getWork() {
        return work;
    }

    /**
     * 
     * @param work
     *     The work
     */
    public void setWork(String work) {
        this.work = work;
    }

    /**
     * 
     * @return
     *     The look
     */
    public String getLook() {
        return look;
    }

    /**
     * 
     * @param look
     *     The look
     */
    public void setLook(String look) {
        this.look = look;
    }

    /**
     * 
     * @return
     *     The bottomFabric
     */
    public String getBottomFabric() {
        return bottomFabric;
    }

    /**
     * 
     * @param bottomFabric
     *     The bottom fabric
     */
    public void setBottomFabric(String bottomFabric) {
        this.bottomFabric = bottomFabric;
    }

    /**
     * 
     * @return
     *     The bottomColor
     */
    public String getBottomColor() {
        return bottomColor;
    }

    /**
     * 
     * @param bottomColor
     *     The bottom color
     */
    public void setBottomColor(String bottomColor) {
        this.bottomColor = bottomColor;
    }

    /**
     * 
     * @return
     *     The topColor
     */
    public String getTopColor() {
        return topColor;
    }

    /**
     * 
     * @param topColor
     *     The top color
     */
    public void setTopColor(String topColor) {
        this.topColor = topColor;
    }

    /**
     * 
     * @return
     *     The topFabric
     */
    public String getTopFabric() {
        return topFabric;
    }

    /**
     * 
     * @param topFabric
     *     The top fabric
     */
    public void setTopFabric(String topFabric) {
        this.topFabric = topFabric;
    }

    /**
     * 
     * @return
     *     The chunariFabric
     */
    public String getChunariFabric() {
        return chunariFabric;
    }

    /**
     * 
     * @param chunariFabric
     *     The chunari fabric
     */
    public void setChunariFabric(String chunariFabric) {
        this.chunariFabric = chunariFabric;
    }

    /**
     * 
     * @return
     *     The chunariColor
     */
    public String getChunariColor() {
        return chunariColor;
    }

    /**
     * 
     * @param chunariColor
     *     The chunari color
     */
    public void setChunariColor(String chunariColor) {
        this.chunariColor = chunariColor;
    }

    /**
     * 
     * @return
     *     The bottomStyle
     */
    public String getBottomStyle() {
        return bottomStyle;
    }

    /**
     * 
     * @param bottomStyle
     *     The bottom style
     */
    public void setBottomStyle(String bottomStyle) {
        this.bottomStyle = bottomStyle;
    }

}
