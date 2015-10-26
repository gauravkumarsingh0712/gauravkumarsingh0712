package com.mirraw.android.Utils;

import android.util.TypedValue;

import com.mirraw.android.Mirraw;

/**
 * Created by varun on 21/8/15.
 */
public class DensityUtils {

    public DensityUtils() {

    }

    public int getDensity() {
        int density = Mirraw.getAppContext().getResources().getDisplayMetrics().densityDpi;

        return density;
    }

    public static int getPxFromDp(float Dp) {
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                Dp,
                Mirraw.getAppContext().getResources().getDisplayMetrics()
        );
        return px;
    }

}
