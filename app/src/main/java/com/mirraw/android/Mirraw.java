package com.mirraw.android;

import android.app.Application;
import android.content.Context;

import com.mirraw.android.sharedresources.Logger;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.splunk.mint.Mint;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by vihaan on 10/7/15.
 */
public class Mirraw extends Application {

    private static Context sContext;
    public static final String tag = Mirraw.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
        initImageLoader(sContext);
        if (BuildConfig.DEBUG) {
            Logger.d(tag, "App running in debug mode");
            // Mint.initAndStartSession(Mirraw.this, "ce8f3bc3");
        } else {
            Logger.d(tag, "App not running in debug mode");
            Mint.initAndStartSession(Mirraw.this, "ce8f3bc3");
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/MYRIADPRO-REGULAR.OTF")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }


    public static Context getAppContext() {
        return sContext;
    }



    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        if (BuildConfig.DEBUG) {
            //config.writeDebugLogs(); // Remove for release app
        }


        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
        
    }




}
