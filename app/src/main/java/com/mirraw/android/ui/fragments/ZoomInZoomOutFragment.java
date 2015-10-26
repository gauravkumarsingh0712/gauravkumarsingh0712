package com.mirraw.android.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.productDetail.Image;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * Created by Gaurav on 8/4/2015.
 */
public class ZoomInZoomOutFragment extends Fragment {
    ImageView mImage;
    static int displayTypeCount = 0;
    String mImageUrl;
    static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %% ID: %d";
    static final String SCALE_TOAST_STRING = "Scaled to: %.2ff";


    private PhotoViewAttacher mAttacher;

    private Toast mCurrentToast;

    private Matrix mCurrentDisplayMatrix = null;

    private static final String LOG_TAG = "image-test";


    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    Image image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //  View view = inflater.inflate(R.layout.fragment_zoomin, container, false);


        Bundle bundle = getArguments();

        mImageUrl = bundle.getString("image");

        System.out.println("bundle : " + mImageUrl);

        // ImageView mImageView = (ImageView) view.findViewById(R.id.iv_photo);

        PhotoView photoView = new PhotoView(container.getContext());

        // Now just add PhotoView to ViewPager and return it


        if (mImageUrl != null) {
            try {

                /*Picasso.with(getActivity()).load("http://" + mImageUrl).placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image).into(photoView);*/

                ImageLoader.getInstance()
                        .displayImage(Utils.addHttpSchemeIfMissing(mImageUrl), photoView, UILUtils.getImageOptions(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // The MAGIC happens here!
        //   mAttacher = new PhotoViewAttacher(mImageView);

        // Lets attach some listeners, not required though!
        //  mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
        //  mAttacher.setOnPhotoTapListener(new PhotoTapListener());
        return photoView;
    }

    private class PhotoTapListener implements PhotoViewAttacher.OnPhotoTapListener {

        @Override
        public void onPhotoTap(View view, float x, float y) {
            float xPercentage = x * 100f;
            float yPercentage = y * 100f;

            showToast(String.format(PHOTO_TAP_TOAST_STRING, xPercentage, yPercentage, view == null ? 0 : view.getId()));
        }
    }

    private void showToast(CharSequence text) {
        if (null != mCurrentToast) {
            mCurrentToast.cancel();
        }

        mCurrentToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        mCurrentToast.show();
    }

}
