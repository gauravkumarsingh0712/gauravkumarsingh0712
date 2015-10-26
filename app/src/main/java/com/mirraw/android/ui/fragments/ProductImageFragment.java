package com.mirraw.android.ui.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.productDetail.Image;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.activities.ZoomInAndZoomOutActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by vihaan on 9/7/15.
 */
public class ProductImageFragment extends Fragment implements View.OnClickListener, RippleView.OnRippleCompleteListener {

    private String mImageUrl;
    private String productId;
    private int position;
    private ArrayList<Image> mImages;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mImageUrl = bundle.getString("image");
            productId = bundle.getString("productId");
            position = bundle.getInt("position");
            mImages = (ArrayList<Image>) bundle.getSerializable("getimage");
            System.out.println("mImages : " + mImages);

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_product_image, container, false);
        return view;
    }

    private ImageView mProductImageView;
    private RippleView mRippleView;
    String TAG = ProductImageFragment.class.getSimpleName();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProductImageView = (ImageView) view.findViewById(R.id.productImageView);
        mRippleView = (RippleView) view.findViewById(R.id.rippleView);
        mRippleView.setOnRippleCompleteListener(this);
        mProductImageView.setOnClickListener(this);
        Logger.d(TAG, "large image url: " + mImageUrl);
        if (mImageUrl != null) {
            try {

                /*Picasso.with(getActivity()).load("http://" + mImageUrl).placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image).into(mProductImageView);*/

                ImageLoader.getInstance()
                        .displayImage(Utils.addHttpSchemeIfMissing(mImageUrl), mProductImageView, UILUtils.getImageOptions(), new ImageLoadingListener() {
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
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.productImageView: {
                // Toast.makeText(getActivity(), "click on image", Toast.LENGTH_LONG).show();
//                showZoomInZoomOutActivity();
            }

        }

    }

    private void showZoomInZoomOutActivity() {
        Intent intent = new Intent(getActivity(), ZoomInAndZoomOutActivity.class);
        Bundle bundle = new Bundle();

        bundle.putString("productId", productId);
        bundle.putInt("position", position);
        bundle.putSerializable("getimg", mImages);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onComplete(RippleView rippleView) {
        showZoomInZoomOutActivity();
    }
}
