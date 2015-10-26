package com.mirraw.android.interfaces;

import com.mirraw.android.models.searchResults.Filters;
import com.mirraw.android.models.searchResults.Sorts;

/**
 * Created by abc on 8/18/2015.
 */
public interface SortAndFilterButtonListener {

    void onSortButtonClicked(Sorts applicableSorts);

    void onFilterButtonClicked(Filters filters);
}
