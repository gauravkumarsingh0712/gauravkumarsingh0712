
package com.mirraw.android.models.menus;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Menus {

    @Expose
    private List<Menu> menus = new ArrayList<Menu>();

    /**
     * 
     * @return
     *     The menus
     */
    public List<Menu> getMenus() {
        return menus;
    }

    /**
     * 
     * @param menus
     *     The menus
     */
    public void setMenus(List<Menu> menus) {
        this.menus = menus;
    }

}
