package com.mirraw.android.Utils;

/**
 * Created by nimesh on 11/9/15.
 */
public class StringUtils {

    public static String removeUnderscores(String input) {

        if (input != null && input.contains("_")) {
            String s = input.replaceAll("_", " ");
            s = s + "";
            return s;
        } else {
            return input;
        }
    }


}