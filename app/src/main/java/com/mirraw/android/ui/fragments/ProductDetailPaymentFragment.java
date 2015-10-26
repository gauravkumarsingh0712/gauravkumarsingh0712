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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductDetailPaymentFragment extends android.support.v4.app.Fragment {


    public ProductDetailPaymentFragment() {
        // Required empty public constructor
    }

    static ProductDetailPaymentFragment newInstance(TabDetails tabDetails) {
        ProductDetailPaymentFragment productDetailPaymentFragment = new ProductDetailPaymentFragment();

        Gson gson = new Gson();

        Bundle bundle = new Bundle();

        bundle.putString("tabDetails", gson.toJson(tabDetails));
        productDetailPaymentFragment.setArguments(bundle);


        return productDetailPaymentFragment;
    }

    TabDetails mTabDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_detail_payment, container, false);

        initViews(view);

        return view;
    }

    TextView mPaymentIndia, mPaymentInt;

    private void initViews(View view) {
        TextView paymentIndia = (TextView) view.findViewById(R.id.paymentIndia);
        paymentIndia.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        TextView paymentInt = (TextView) view.findViewById(R.id.paymentInt);
        paymentInt.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        mPaymentIndia = (TextView) view.findViewById(R.id.txtPaymentIndia);
        mPaymentInt = (TextView) view.findViewById(R.id.txtPaymentInt);

        Bundle bundle = getArguments();
        if (bundle != null) {
            Gson gson = new Gson();
            mTabDetails = gson.fromJson(bundle.getString("tabDetails"), TabDetails.class);
            updateViews();
        }

    }

    private void updateViews() {
        List<String> listPaymentIndia = mTabDetails.getPayments().getInIndia();

        String strPaymentsIndia = "";

        for (int i = 0; i < listPaymentIndia.size(); i++) {
            strPaymentsIndia += listPaymentIndia.get(i) + "\n";
        }
        if (!TextUtils.isEmpty(strPaymentsIndia)) {
            mPaymentIndia.setText(strPaymentsIndia.trim());
        } else {
            mPaymentIndia.setVisibility(View.GONE);
        }


        List<String> listPaymentInt = mTabDetails.getPayments().getOutOfIndia();

        String strPaymentsInt = "";

        for (int i = 0; i < listPaymentInt.size(); i++) {
            strPaymentsInt += listPaymentInt.get(i) + "\n";
        }
        if (!TextUtils.isEmpty(strPaymentsInt)) {
            mPaymentInt.setText(strPaymentsInt.trim());
        } else {
            mPaymentInt.setVisibility(View.GONE);
        }


    }


}
