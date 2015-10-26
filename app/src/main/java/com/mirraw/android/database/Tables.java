package com.mirraw.android.database;

/**
 * Created by vihaan on 10/7/15.
 */
public class Tables {

    public static class Cart {
        public static final String TABLE_NAME = "cart";

        public static final String PRODUCT = "product";
        public static String COLUMNS =
                PRODUCT + " text not null ";

        public static final String SCHEMA_CART = "Create table  " + TABLE_NAME + "(" + COLUMNS + ")";
    }

    public static class SyncRequests {
        public static final String TABLE_NAME = "sync_requests";

        public static final String REQUESTS = "requests";
        public static String COLUMNS = REQUESTS + " text not null ";

        public static final String SCHEMA_SYNC_REQUESTS = "Create table " + TABLE_NAME + "(" + COLUMNS + ")";
    }

}
