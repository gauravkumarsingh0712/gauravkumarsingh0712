package com.mirraw.android.sharedresources;

/**
 * Created by nimesh on 22/9/15.
 */
public class AppConfig {

    private static final boolean TEST_FOR_US = false;
    private static final boolean SHOULD_LOG = false;
    private static final boolean TEST_FOR_STAGING = false;

    public static boolean testForUS() {
        return TEST_FOR_US;
    }

    public static boolean shouldLog() {
        return SHOULD_LOG;
    }

    public static boolean testForStaging() {
        return TEST_FOR_STAGING;
    }

}
