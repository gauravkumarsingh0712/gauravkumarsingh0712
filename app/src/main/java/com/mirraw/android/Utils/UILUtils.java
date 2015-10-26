package com.mirraw.android.Utils;

import android.graphics.Bitmap;

import com.mirraw.android.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by varun on 3/9/15.
 */
public class UILUtils {

    private static DisplayImageOptions options;

    public static DisplayImageOptions getImageOptions() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_image_small)
                .showImageForEmptyUri(R.drawable.placeholder_image_small)
                .showImageOnFail(R.drawable.placeholder_image_small)
                .displayer(new FadeInBitmapDisplayer(1000))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return options;
    }

    public static DisplayImageOptions getImageOptionsSmall() {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder_image_small)
                .showImageForEmptyUri(R.drawable.placeholder_image_small)
                .showImageOnFail(R.drawable.placeholder_image_small)
                .displayer(new FadeInBitmapDisplayer(1000))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        return options;
    }
}
