package com.mirraw.android.ui.fragments;


import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.productDetail.TabDetails;


public class ProductDetailShippingFragment extends android.support.v4.app.Fragment {


    public ProductDetailShippingFragment() {
        // Required empty public constructor
    }

    private TabDetails mTabDetails;

    static ProductDetailShippingFragment newInstance(TabDetails tabDetails){
        ProductDetailShippingFragment productDetailShippingFragment = new ProductDetailShippingFragment();

        Gson gson = new Gson();

        Bundle bundle = new Bundle();

        bundle.putString("tabDetails", gson.toJson(tabDetails));

        productDetailShippingFragment.setArguments(bundle);

        return productDetailShippingFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_product_detail_shipping, container, false);

        intViews(view);

        return view;
    }


    private TextView mLocationsIndia, mTimeIndia, mChargesIndia, mNoteInt, mTimeInt, mChargesInt;
    private void intViews(View view) {

        TextView shippingInIndia = (TextView) view.findViewById(R.id.shippingInIndia);
        shippingInIndia.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextView shippingInt = (TextView) view.findViewById(R.id.shippingInternational);
        shippingInt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mLocationsIndia = (TextView) view.findViewById(R.id.txtLocationsIndia);
        mTimeIndia = (TextView) view.findViewById(R.id.txtTimeIndia);
        mChargesIndia = (TextView) view.findViewById(R.id.txtChargesIndia);
        mNoteInt = (TextView) view.findViewById(R.id.txtNoteInt);
        mTimeInt = (TextView) view.findViewById(R.id.txtTimeInt);
        mChargesInt = (TextView) view.findViewById(R.id.txtChargesInt);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            mTabDetails = gson.fromJson(bundle.getString("tabDetails"), TabDetails.class);
            updateViews();
        }
    }

    private void updateViews() {
        String strLocationsIndia = mTabDetails.getShipping().getInIndia().getLocations();

        if(!TextUtils.isEmpty(strLocationsIndia)){
            mLocationsIndia.setText(strLocationsIndia.trim());
        } else {
            mLocationsIndia.setVisibility(View.GONE);
        }

        String strTimeIndia = mTabDetails.getShipping().getInIndia().getTime();

        if(!TextUtils.isEmpty(strTimeIndia)){
            mTimeIndia.setText(strTimeIndia.trim());
        } else {
            mTimeIndia.setVisibility(View.GONE);
        }

        String strChargesIndia = mTabDetails.getShipping().getInIndia().getCharges();

        if(!TextUtils.isEmpty(strChargesIndia)){
            mChargesIndia.setText(strChargesIndia.trim());
        } else {
            mChargesIndia.setVisibility(View.GONE);
        }

        String strNoteInt = mTabDetails.getShipping().getOutOfIndia().getNote();

        if(!TextUtils.isEmpty(strNoteInt)){
            mNoteInt.setText(strNoteInt.trim());
        } else {
            mNoteInt.setVisibility(View.GONE);
        }

        String strTimeInt = mTabDetails.getShipping().getOutOfIndia().getTime();

        if(!TextUtils.isEmpty(strTimeInt)){
            mTimeInt.setText(strTimeInt.trim());
        } else {
            mTimeInt.setVisibility(View.GONE);
        }

        String strChargesInt = mTabDetails.getShipping().getOutOfIndia().getCharges();

        if(!TextUtils.isEmpty(strChargesInt)){
            mChargesInt.setText(strChargesInt.trim());
        } else {
            mChargesInt.setVisibility(View.GONE);
        }
    }


}
