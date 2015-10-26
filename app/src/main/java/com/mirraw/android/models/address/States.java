package com.mirraw.android.models.address;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;


public class States {

    @Expose
    private List<State> states = new ArrayList<State>();

    /**
     *
     * @return
     * The states
     */
    public List<State> getStates() {
        return states;
    }

    /**
     *
     * @param states
     * The states
     */
    public void setStates(List<State> states) {
        this.states = states;
    }

}