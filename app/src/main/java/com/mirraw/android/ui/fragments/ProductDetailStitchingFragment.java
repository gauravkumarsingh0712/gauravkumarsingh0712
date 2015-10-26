package com.mirraw.android.ui.fragments;


import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.productDetail.TabDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailStitchingFragment extends android.support.v4.app.Fragment {


    public ProductDetailStitchingFragment() {
        // Required empty public constructor
    }

    TabDetails mTabDetails;

    static ProductDetailStitchingFragment newInstance(TabDetails tabDetails){
        ProductDetailStitchingFragment productDetailStitchingFragment = new ProductDetailStitchingFragment();

        Gson gson = new Gson();

        Bundle bundle = new Bundle();

        bundle.putString("tabDetails", gson.toJson(tabDetails));
        productDetailStitchingFragment.setArguments(bundle);

        return productDetailStitchingFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_product_detail_stitching, container, false);
        
        initViews(view);
        
        return view;
    }


    TextView mTime, mCharges, mReturns, mInst;
    private void initViews(View view) {

        TextView time = (TextView) view.findViewById(R.id.stitchingTime);
        time.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView charges = (TextView) view.findViewById(R.id.stitchingCharges);
        charges.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView returns = (TextView) view.findViewById(R.id.stitchingReturns);
        returns.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView inst = (TextView) view.findViewById(R.id.stitchingInst);
        inst.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mTime = (TextView) view.findViewById(R.id.txtStitchingTime);

        mCharges = (TextView) view.findViewById(R.id.txtStitchingCharges);

        mReturns = (TextView) view.findViewById(R.id.txtStitchingReturns);

        mInst = (TextView) view.findViewById(R.id.txtStitchingInst);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            mTabDetails = gson.fromJson(bundle.getString("tabDetails"), TabDetails.class);
            updateViews();
        }
    }

    private void updateViews() {
        String strTime = mTabDetails.getStitching().getTime();

        if(!TextUtils.isEmpty(strTime)){
            mTime.setText(strTime.trim());
        } else {
            mTime.setVisibility(View.GONE);
        }

        String strCharges = mTabDetails.getStitching().getCharges();

        if(!TextUtils.isEmpty(strCharges)){
            mCharges.setText(strCharges.trim());
        } else {
            mCharges.setVisibility(View.GONE);
        }

        String strReturns = mTabDetails.getStitching().getReturns();

        if(!TextUtils.isEmpty(strReturns)){
            mReturns.setText(strReturns.trim());
        } else {
            mReturns.setVisibility(View.GONE);
        }

        String strInst = mTabDetails.getStitching().getInstructions();

        if(!TextUtils.isEmpty(strInst)){
            mInst.setText(strInst.trim());
        } else {
            mInst.setVisibility(View.GONE);
        }
    }


}
