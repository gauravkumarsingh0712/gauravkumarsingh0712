package com.mirraw.android.interfaces;

/**
 * Created by vihaan on 1/7/15.
 */
public interface DataLoader {
    public void onNoInternet();
    public void getFirstPageData();
    public void setFirstPageData(String response);


}
