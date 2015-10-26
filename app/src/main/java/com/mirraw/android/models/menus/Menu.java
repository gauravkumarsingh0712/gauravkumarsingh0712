
package com.mirraw.android.models.menus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    @Expose
    private String title;
    @SerializedName("menu_columns")
    @Expose
    private List<MenuColumn> menuColumns = new ArrayList<MenuColumn>();

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
     *     The menuColumns
     */
    public List<MenuColumn> getMenuColumns() {
        return menuColumns;
    }

    /**
     * 
     * @param menuColumns
     *     The menu_columns
     */
    public void setMenuColumns(List<MenuColumn> menuColumns) {
        this.menuColumns = menuColumns;
    }

}
