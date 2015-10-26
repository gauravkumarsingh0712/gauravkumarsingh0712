package com.mirraw.android.interfaces;

/**
 * Created by varun on 30/7/15.
 */
public interface PaginatedDataLoader extends DataLoader {
    public void getNextPage();
    public void setNextPage();
}
