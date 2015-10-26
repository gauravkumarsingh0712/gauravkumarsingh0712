
package com.mirraw.android.models;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class BlocksList {

    @Expose
    private List<Block> blocks = new ArrayList<Block>();

    /**
     * 
     * @return
     *     The blocks
     */
    public List<Block> getBlocks() {
        return blocks;
    }

    /**
     * 
     * @param blocks
     *     The blocks
     */
    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

}
