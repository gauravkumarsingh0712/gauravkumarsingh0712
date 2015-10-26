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
public class ProductDetailReturnsFragment extends android.support.v4.app.Fragment {


    public ProductDetailReturnsFragment() {
        // Required empty public constructor
    }

    TabDetails mTabDetails;

    static ProductDetailReturnsFragment newInstance(TabDetails tabDetails){
        ProductDetailReturnsFragment productDetailReturnsFragment = new ProductDetailReturnsFragment();

        Gson gson = new Gson();

        Bundle bundle = new Bundle();

        bundle.putString("tabDetails", gson.toJson(tabDetails));
        productDetailReturnsFragment.setArguments(bundle);

        return productDetailReturnsFragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_product_details_returns, container, false);

        initViews(view);
        return view;
    }

    TextView mTime, mProcess, mRefunds;

    private void initViews(View view) {
        TextView time = (TextView) view.findViewById(R.id.time);
        time.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView process = (TextView) view.findViewById(R.id.process);
        process.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        TextView refunds = (TextView) view.findViewById(R.id.refunds);
        refunds.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mTime = (TextView) view.findViewById(R.id.txtTime);

        mProcess = (TextView) view.findViewById(R.id.txtProcess);

        mRefunds = (TextView) view.findViewById(R.id.txtRefunds);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            mTabDetails = gson.fromJson(bundle.getString("tabDetails"), TabDetails.class);
            updateViews();
        }
    }

    private void updateViews() {

        String strTime = mTabDetails.getReturns().getTime();

        if (!TextUtils.isEmpty(strTime)) {
            mTime.setText(strTime.trim());
        } else {
            mTime.setVisibility(View.GONE);
        }

        String strProcess = mTabDetails.getReturns().getProcess();

        if (!TextUtils.isEmpty(strProcess)) {
            mProcess.setText(strProcess.trim());
        } else {
            mProcess.setVisibility(View.GONE);
        }

        String strRefunds = mTabDetails.getReturns().getRefundsOrReplacements();

        if (!TextUtils.isEmpty(strRefunds)) {
            mRefunds.setText(strRefunds.trim());
        } else {
            mRefunds.setVisibility(View.GONE);
        }

    }


}
