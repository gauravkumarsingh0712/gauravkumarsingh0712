
package com.mirraw.android.models.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MenuColumn {

    @Expose
    private String title;
    @SerializedName("menu_items")
    @Expose
    private List<MenuItem> menuItems = new ArrayList<MenuItem>();

    /**
     * 
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 
     * @return
     *     The menuItems
     */
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    /**
     * 
     * @param menuItems
     *     The menu_items
     */
    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

}
