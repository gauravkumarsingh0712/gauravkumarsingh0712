package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.StringUtils;
import com.mirraw.android.models.productDetail.Designable;
import com.mirraw.android.models.productDetail.ProductDetail;
import com.mirraw.android.models.productDetail.Property;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductDetailSpecificationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProductDetailSpecificationFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductDetailSpecificationFragment extends android.support.v4.app.Fragment {


    private ProductDetail mProductDetail;

    public ProductDetailSpecificationFragment() {
        // Required empty public constructor
    }

    static ProductDetailSpecificationFragment newInstance(ProductDetail productDetail) {
        ProductDetailSpecificationFragment productDetailSpecificationFragment = new ProductDetailSpecificationFragment();

        Gson gson = new Gson();

        Bundle bundle = new Bundle();
        bundle.putString("productDetail", gson.toJson(productDetail));
        productDetailSpecificationFragment.setArguments(bundle);


        return productDetailSpecificationFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    TextView mSpecification, mProductId, mType, mShipping, mBottomFabric, mBottomColor, mTopFabric, mTopColor, mOccassion, mLook, mWork, mBottomStyle, mChunariLength;
    View view;
    LinearLayout mSpecsRLL;
    TableLayout mTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_product_detail_specification, container, false);

        initViews(view);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String strProductDetail = bundle.getString("productDetail");
            Gson gson = new Gson();
            //JsonObject jo = new JsonParser().parse(strProductDetail).getAsJsonObject();
            mProductDetail = gson.fromJson(strProductDetail, ProductDetail.class);
            fillDetails();
        }


        return view;
    }

    public void initViews(View view) {
        mSpecsRLL = (LinearLayout) view.findViewById(R.id.specsLL);
        mSpecification = (TextView) view.findViewById(R.id.txtSpecification);
        mTable = (TableLayout) view.findViewById(R.id.specsTable);

        mProductId = (TextView) view.findViewById(R.id.txtProductId);

        mShipping = (TextView) view.findViewById(R.id.txtShipping);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    public void fillDetails() {
        String strSpecification = "";
        if (mProductDetail.getSpecifications().getSpecification() != null) {
            strSpecification = mProductDetail.getSpecifications().getSpecification();
        }

        if (!strSpecification.trim().equalsIgnoreCase("")) {
            mSpecification.setText(strSpecification);
        } else {
            mSpecification.setVisibility(View.GONE);
        }

        mProductId.setText(String.valueOf(mProductDetail.getSpecifications().getProductId()));
        boolean shipping = mProductDetail.getSpecifications().getInternationalShipping();
        if (shipping) {
            mShipping.setText("Available worldwide");
        } else {
            mShipping.setVisibility(View.GONE);
            view.findViewById(R.id.shipping).setVisibility(View.GONE);
        }

        List<Property> propertyList = mProductDetail.getSpecifications().getProperties();

        int size = propertyList.size();


        for (int i = 0; i < size; i++) {
            String strType = propertyList.get(i).getType();

            List<String> lsValue = propertyList.get(i).getValue();
            int valueSize = lsValue.size();
            String strValue = "";

            for (int j = 0; j < valueSize; j++) {
                strValue += " " + lsValue.get(j) + ",";
            }

            strValue = strValue.substring(0, strValue.length() - 1);

            TableRow tblRow = new TableRow(getActivity());

            //TableRow.LayoutParams tblLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            tblRow.setLayoutParams(lp);

            TextView textViewType = new TextView(getActivity());
            textViewType.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //LinearLayout.LayoutParams layoutParamsType = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams layoutParamsType = new TableRow.LayoutParams(1);

            layoutParamsType.setMargins(0, 35, 0, 0);

            textViewType.setLayoutParams(layoutParamsType);
            textViewType.setText(StringUtils.removeUnderscores(strType));


            TextView textViewValue = new TextView(getActivity());
            TableRow.LayoutParams layoutParamsValue = new TableRow.LayoutParams(2);
            layoutParamsValue.setMargins(35, 35, 0, 0);
            textViewValue.setLayoutParams(layoutParamsValue);
            /*TableLayout.LayoutParams layoutParamsValue = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsValue.setMargins(35, 35, 0, 0);
            textViewValue.setLayoutParams(layoutParamsValue);*/

            textViewValue.setText(StringUtils.removeUnderscores(strValue));

            tblRow.addView(textViewType);
            tblRow.addView(textViewValue);

            if (!TextUtils.isEmpty(strValue)) {
                mTable.addView(tblRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }

        }

        List<Designable> designableList = mProductDetail.getSpecifications().getDesignable();

        int designableSize = designableList.size();

        for (int i = 0; i < designableSize; i++) {
            String strType = designableList.get(i).getType();
            String strValue = designableList.get(i).getValue();

            TableRow tblRow = new TableRow(getActivity());

            //TableRow.LayoutParams tblLayoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
            tblRow.setLayoutParams(lp);

            TextView textViewType = new TextView(getActivity());
            textViewType.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            //LinearLayout.LayoutParams layoutParamsType = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TableRow.LayoutParams layoutParamsType = new TableRow.LayoutParams(1);

            layoutParamsType.setMargins(0, 35, 0, 0);

            textViewType.setLayoutParams(layoutParamsType);
            textViewType.setText(StringUtils.removeUnderscores(strType));


            TextView textViewValue = new TextView(getActivity());
            TableRow.LayoutParams layoutParamsValue = new TableRow.LayoutParams(2);
            layoutParamsValue.setMargins(35, 35, 0, 0);
            textViewValue.setLayoutParams(layoutParamsValue);
            /*TableLayout.LayoutParams layoutParamsValue = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParamsValue.setMargins(35, 35, 0, 0);
            textViewValue.setLayoutParams(layoutParamsValue);*/

            textViewValue.setText(StringUtils.removeUnderscores(strValue));

            tblRow.addView(textViewType);
            tblRow.addView(textViewValue);

            if (!TextUtils.isEmpty(strValue)) {
                mTable.addView(tblRow, new TableLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            }

        }
        //mTable.requestLayout();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void showSpecifications(Bundle bundle);
    }

}
