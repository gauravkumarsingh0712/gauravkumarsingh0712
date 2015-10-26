package com.mirraw.android.interfaces;

/**
 * Created by varun on 4/9/15.
 */
public interface SortFilterChangeListener {
    public void updateSortView(int mSortId);
    public void updateFilterView(String mFilterParams);

    public void showSortFilterView();
    public void hideSortFilterView();

}
