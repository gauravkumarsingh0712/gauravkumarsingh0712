package com.mirraw.android.ui.fragments;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.AddToCartAsync;
import com.mirraw.android.async.CodAsync;
import com.mirraw.android.async.GetProductDetailTabAsync;
import com.mirraw.android.async.ProductDetailAsync;
import com.mirraw.android.interfaces.BuyNowAddToCartDisplayListener;
import com.mirraw.android.models.address.AddressArray;
import com.mirraw.android.models.productDetail.AddonOptionType;
import com.mirraw.android.models.productDetail.AddonOptionValue;
import com.mirraw.android.models.productDetail.AddonType;
import com.mirraw.android.models.productDetail.AddonTypeValue;
import com.mirraw.android.models.productDetail.COD;
import com.mirraw.android.models.productDetail.Image;
import com.mirraw.android.models.productDetail.OptionTypeValue;
import com.mirraw.android.models.productDetail.ProductDetail;
import com.mirraw.android.models.productDetail.TabDetails;
import com.mirraw.android.models.productDetail.Variant;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.ui.activities.BaseActivity;
import com.mirraw.android.ui.activities.CartActivity;
import com.mirraw.android.ui.activities.ProductDetailActivity;
import com.mirraw.android.ui.adapters.AddonAdapter;
import com.mirraw.android.ui.adapters.AddonChildAdapter;
import com.mirraw.android.ui.adapters.ViewPagerAdapter;
import com.mirraw.android.ui.adapters.ViewPagerAdapterSpecs;
import com.mirraw.android.ui.widgets.CustomNestedScrollView;
import com.mirraw.android.ui.widgets.viewpagerIndicator.CirclePageIndicator;
import com.mirraw.android.ui.widgets.viewpagerIndicator.PageIndicator;
import com.mirraw.android.ui.widgets.viewpagerIndicator.ProductDetailSpecificationViewPager;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by vihaan on 8/7/15.
 */
public class ProductDetailFragment extends Fragment
        implements ProductDetailAsync.ProductLoader,
        View.OnClickListener,
        AddToCartAsync.AddedToCartLoader,
        GetProductDetailTabAsync.ProductDetailsTabLoader,
        CodAsync.CodLoader,
        ProductDetailActivity.BuyNowAddToCartClickListener, RippleView.OnRippleCompleteListener, AdapterView.OnItemSelectedListener {
    private SharedPreferencesManager mAppSharedPrefs;
    ProgressDialog mProgressDialog;
    Boolean mPlaceOrderClicked = false;

    int mCurrentItemIndex = 0;

    BuyNowAddToCartDisplayListener mBuyNowAddToCartDisplayListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBuyNowAddToCartDisplayListener = (BuyNowAddToCartDisplayListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        mAppSharedPrefs = new SharedPreferencesManager(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadProduct();
    }

    ProductDetailSpecificationFragment productDetailSpecificationFragment;// = new ProductDetailSpecificationFragment();

    private void initViews(View view) {
        initNoInternetView(view);
        initProductScrollView(view);
        initProductImageViewPager(view);
        initCheckCodLinearLayout(view);
        //setupTabHost();
        initArrows(view);

    }

    LinearLayout mCheckCodLinearLayout;

    private void initCheckCodLinearLayout(View view) {
        mCheckCodLinearLayout = (LinearLayout) view.findViewById(R.id.pincodeLL);
    }


    private void initArrows(View view) {
        leftArrowImage = (ImageView) view.findViewById(R.id.left_arrow);
        rightArrowImage = (ImageView) view.findViewById(R.id.right_arrow);
        leftArrowImage.setOnClickListener(this);
        rightArrowImage.setOnClickListener(this);
    }

    private ImageView leftArrowImage, rightArrowImage;
    //private NestedScrollView mProductDetailSV;
    private CustomNestedScrollView mProductDetailSV;
    private TextView mProductName, mDesigerName;
    private TextView mProductPrice;
    private TextView mProductOriginalPrice;
    private TextView mProductDiscountRate;
    private LinearLayout mOptionButtons;
    private TabLayout mTabLayout;
    private ProductDetailSpecificationViewPager mViewPager;
    private EditText mTxtCheckCod;
    private RippleView mCheckCodRippleView;
    private TextView mTxtAvailable;
    private RelativeLayout mTabsRLL;
    private RelativeLayout mConnectionContainerTabs;
    private LinearLayout mNoInternetLLTabs;
    private RippleView mRetryButtonRippleContainer;
    private ProgressWheel mProgressWheelTabs;
    private HorizontalScrollView mHorizontalScrollView;
    private LinearLayout mSizeLinearLayout, horizontalCircleLayout;
    private RelativeLayout mainSelectSizeLayout;
    private View mHorizontalScrollShadowView;
    private ProgressWheel mProgressWheelCod;

    private void initProductScrollView(View view) {
        /*mOptionButtons = (LinearLayout) view.findViewById(R.id.optionButtons);
        mOptionButtons.setVisibility(View.GONE);*/
        mProductDetailSV = (CustomNestedScrollView) view.findViewById(R.id.productDetailSV);
        mProductDetailSV.setVisibility(View.GONE);
        /*view.findViewById(R.id.addToCartButton).setOnClickListener(this);
        view.findViewById(R.id.buyNowButton).setOnClickListener(this);*/
        mProductName = (TextView) view.findViewById(R.id.productNameTextView);
        mDesigerName = (TextView) view.findViewById(R.id.designerNameTextView);
        mProductPrice = (TextView) view.findViewById(R.id.priceTextView);
        mProductOriginalPrice = (TextView) view.findViewById(R.id.originalPriceTextView);
        mProductDiscountRate = (TextView) view.findViewById(R.id.priceDiscountTextView);
        mTxtCheckCod = (EditText) view.findViewById(R.id.txtCheckCod);
        mTxtAvailable = (TextView) view.findViewById(R.id.txtAvailable);
        mProgressWheelCod = (ProgressWheel) view.findViewById(R.id.progressWheelCod);
        setCODEditTextListener();
        mCheckCodRippleView = (RippleView) view.findViewById(R.id.check_cod_ripple_view);
        mCheckCodRippleView.setOnRippleCompleteListener(this);
        initSpecificationTabs(view);
        mainSelectSizeLayout = (RelativeLayout) view.findViewById(R.id.main_select_sizeLinearLayout);
        mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
        mHorizontalScrollView.setHorizontalScrollBarEnabled(false);
    }

    private void initSpecificationTabs(View view) {
        mTabsRLL = (RelativeLayout) view.findViewById(R.id.tabsRL);

        mConnectionContainerTabs = (RelativeLayout) mTabsRLL.findViewById(R.id.connectionContainerTabs);
        mNoInternetLLTabs = (LinearLayout) mTabsRLL.findViewById(R.id.noInternetLL);
        mNoInternetLLTabs.setVisibility(View.GONE);
        mRetryButtonRippleContainer = (RippleView) mTabsRLL.findViewById(R.id.retry_button_ripple_container);
        mRetryButtonRippleContainer.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                loadTabDetails();
            }
        });
        mProgressWheelTabs = (ProgressWheel) mTabsRLL.findViewById(R.id.progressWheel);


        productDetailSpecificationFragment = new ProductDetailSpecificationFragment();

        mViewPager = (ProductDetailSpecificationViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setVisibility(View.VISIBLE);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            int dragthreshold = 30;
            int downX;
            int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = (int) event.getRawX();
                        downY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int distanceX = Math.abs((int) event.getRawX() - downX);
                        int distanceY = Math.abs((int) event.getRawY() - downY);

                        if (distanceY > distanceX && distanceY > dragthreshold) {
                            mViewPager.getParent().requestDisallowInterceptTouchEvent(false);
                            mProductDetailSV.getParent().requestDisallowInterceptTouchEvent(true);
                        } else if (distanceX > distanceY && distanceX > dragthreshold) {
                            mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
                            mProductDetailSV.getParent().requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        mProductDetailSV.getParent().requestDisallowInterceptTouchEvent(false);
                        mViewPager.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        mTabLayout.setVisibility(View.GONE);

        mainSelectSizeLayout = (RelativeLayout) view.findViewById(R.id.main_select_sizeLinearLayout);
        mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
        mHorizontalScrollShadowView = view.findViewById(R.id.left_shadow);

    }

    private void setCODEditTextListener() {
        mTxtCheckCod.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mTxtAvailable.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void setupTabs() {


        mTabLayout.setupWithViewPager(mViewPager);
        //mTabLayout.addTab(mTabLayout.newTab(), 0);

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager() {
        ViewPagerAdapterSpecs adapterSpecs = new ViewPagerAdapterSpecs(getChildFragmentManager());

        //ProductDetailSpecificationFragment fragSpecs = new ProductDetailSpecificationFragment(mProductDetail);
        if (mProductDetail != null) {
            ProductDetailSpecificationFragment fragSpecs = ProductDetailSpecificationFragment.newInstance(mProductDetail);

//        fragSpecs.newInstance(mProductDetail);
            //fragSpecs.setArguments(getArgs());

            adapterSpecs.addFragment(fragSpecs, "Specification");
        }

        ProductDetailShippingFragment productDetailShippingFragment = ProductDetailShippingFragment.newInstance(mTabDetail);

        adapterSpecs.addFragment(productDetailShippingFragment, "Shipping");

        ProductDetailPaymentFragment productDetailPaymentFragment = ProductDetailPaymentFragment.newInstance(mTabDetail);

        adapterSpecs.addFragment(productDetailPaymentFragment, "Payments");

        ProductDetailReturnsFragment productDetailReturnsFragment = ProductDetailReturnsFragment.newInstance(mTabDetail);
        adapterSpecs.addFragment(productDetailReturnsFragment, "Returns");

        if (mProductDetail != null && mProductDetail.getStitchingAvailable()) {
            ProductDetailStitchingFragment productDetailStitchingFragment = ProductDetailStitchingFragment.newInstance(mTabDetail);
            adapterSpecs.addFragment(productDetailStitchingFragment, "Stitching");
        }

        mViewPager.setAdapter(adapterSpecs);
        mTabLayout.setVisibility(View.VISIBLE);
        mProgressWheelTabs.setVisibility(View.GONE);
        mConnectionContainerTabs.setVisibility(View.GONE);
        //strState = mProductDetail.getState();
        if (!strState.trim().equalsIgnoreCase("in_stock") && mProductDetail.getVariants().size() == 0) {
            mBuyNowAddToCartDisplayListener.showOutOfStockButton();
        }
        mConnectionContainer.setVisibility(View.GONE);
        mProductDetailSV.setVisibility(View.VISIBLE);
        mBuyNowAddToCartDisplayListener.showBuyNowAddToCartButtons();

    }


    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetLL;
    private ProgressWheel mProgressWheel;

    private void initNoInternetView(View view) {
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainer);
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        mNoInternetLL.setVisibility(View.GONE);
        ((RippleView) view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
    }

    private ViewPager mProductImageViewPager;
    PageIndicator mIndicator;

    private void initProductImageViewPager(View view) {
        mProductImageViewPager = (ViewPager) view.findViewById(R.id.productImageViewPager);
        mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
    }

    @Override
    public void onProductPreLoad() {

    }

    private ProductDetailAsync mAsync = null;
    String productId;

    @Override
    public void loadProduct() {
        Bundle detailsBundle = getArguments();
        productId = detailsBundle.getString("productId");
        //productId = "420286";
        //Logger.d(ProductDetailFragment.class.getSimpleName(), "PRODUCTID: " + productId);
        mAsync = new ProductDetailAsync(this);
        //mCategoryAsync.executeTask(ApiUrls.API_PRODUCTS);
        //mAsync.executeTask(ApiUrls.API_PRODUCTS + productId);
        String url = ApiUrls.API_PRODUCTS + productId;
        //String url = ApiUrls.API_PRODUCTS + 253985; //USING FOR TEMP

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        mAsync.execute(request);
    }

    @Override
    public void onProductLoaded(Response response) {
        //mOptionButtons.setVisibility(View.VISIBLE);
        JSONObject json;
        try {
            if (response.getResponseCode() == 200) {
                Gson gson = new Gson();
                ProductDetail productDetail = gson.fromJson(response.getBody(), ProductDetail.class);
                int id = productDetail.getId();


                showProductInfo(productDetail);
            } else {
                onProductLoadFailed(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            couldNotLoadProduct();
        }
    }

    private ProductDetail mProductDetail;
    private String strState, strSymbol;

    private void showProductInfo(ProductDetail productDetail) {
        mProductDetail = productDetail;
        viewCheckCodLayout();
        strState = mProductDetail.getState();
        mProductName.setText(productDetail.getTitle());
        mDesigerName.setText("By " + productDetail.getDesigner().getName());


        strSymbol = String.valueOf(Character.toChars((char) Integer.parseInt(productDetail.getHexSymbol(), 16)));
        if (!productDetail.getPrice().equals(productDetail.getDiscountPrice())) {
            if (productDetail.getHexSymbol().equalsIgnoreCase("20B9")) {
                mProductPrice.setText(strSymbol + " " + productDetail.getDiscountPrice().intValue());
                mProductDiscountRate.setText(productDetail.getDiscountPercent().intValue() + "% OFF");
                mProductOriginalPrice.setText(strSymbol + " " + productDetail.getPrice().intValue());
            } else {
                mProductPrice.setText(strSymbol + " " + productDetail.getDiscountPrice());
                mProductDiscountRate.setText(productDetail.getDiscountPercent() + "% OFF");
                mProductOriginalPrice.setText(strSymbol + " " + productDetail.getPrice());
            }
            mProductOriginalPrice.setPaintFlags(mProductOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            if (productDetail.getHexSymbol().equalsIgnoreCase("20B9")) {
                mProductPrice.setText(strSymbol + " " + productDetail.getPrice().intValue());
            } else {
                mProductPrice.setText(strSymbol + " " + productDetail.getPrice());
            }
            mProductOriginalPrice.setText("");
        }

        showProductImages(productDetail.getImages());
        if (productDetail.getVariants().size() != 0) {
            mainSelectSizeLayout.setVisibility(View.VISIBLE);
            showProductSizeInfo(productDetail.getVariants());
        } else {
            mainSelectSizeLayout.setVisibility(View.GONE);
        }

        if (productDetail.getAddonTypes().size() != 0) {
            addonType(productDetail.getAddonTypes());
        }

        if (!mAppSharedPrefs.getTabDetails().equalsIgnoreCase("")) {
            Gson gson = new Gson();
            String tabDetailsBody = mAppSharedPrefs.getTabDetails();
            mTabDetail = gson.fromJson(tabDetailsBody, TabDetails.class);
            setupViewPager();
            setupTabs();
        }
        loadTabDetails();

    }

    private void viewCheckCodLayout() {
        if (!mProductDetail.getHexSymbol().equalsIgnoreCase("20B9")) {
            mCheckCodLinearLayout.setVisibility(View.GONE);
        }
    }

    ArrayList<Image> mImages;

    private void showProductImages(ArrayList<Image> images) {
        mImages = images;
        if (mImages.size() == 1) {
            leftArrowImage.setVisibility(View.INVISIBLE);
            rightArrowImage.setVisibility(View.INVISIBLE);
        } else if (mImages == null || mImages.size() == 0) {
            leftArrowImage.setVisibility(View.INVISIBLE);
            rightArrowImage.setVisibility(View.INVISIBLE);
        }

        ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        Gson gson = new Gson();
        for (int i = 0; i < images.size(); i++) {
            ProductImageFragment fragment = new ProductImageFragment();
            Image image = images.get(i);
            Bundle bundle = new Bundle();
            bundle.putString("image", image.getSizes().getLarge());
            bundle.putString("productId", productId);
            bundle.putSerializable("getimage", mImages);
            bundle.putInt("position", i);
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
        ViewPagerAdapter vpa = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), fragments);
        mProductImageViewPager.setAdapter(vpa);

        mProductImageViewPager.setPageMargin(16);
        mIndicator.setViewPager(mProductImageViewPager);
    }


    @Override
    public void onProductLoadFailed(Response response) {

        if (response.getResponseCode() == 0) {
            onNoInternet();
        } else {
            couldNotLoadProduct();
            onNoInternet();
        }
    }

    @Override
    public void couldNotLoadProduct() {
        Toast.makeText(getActivity(), "Problem loading products", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProductPostLoad() {

    }

    private void onNoInternet() {
        mConnectionContainer.setVisibility(View.VISIBLE);
        mNoInternetLL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
        mProductDetailSV.setVisibility(View.GONE);
        Utils.showSnackBar(getString(R.string.no_internet), getActivity(), Snackbar.LENGTH_LONG);
    }

    @Override
    public void onDestroy() {
        if (mAsync != null) {
            mAsync.cancel(true);
        }

        if (productDetailTabAsync != null) {
            productDetailTabAsync.cancel(true);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        LoginManager loginManager = new LoginManager(getActivity());
        switch (v.getId()) {


            case R.id.btnCheckCod:
                checkCod();
                break;

            case R.id.left_arrow:
                if (mCurrentItemIndex > 0) {
                    mCurrentItemIndex--;
                    mProductImageViewPager.setCurrentItem(mCurrentItemIndex);
                }
                break;

            case R.id.right_arrow:
                if (mCurrentItemIndex < mImages.size()) {
                    mCurrentItemIndex++;
                    mProductImageViewPager.setCurrentItem(mCurrentItemIndex);
                }
                break;


        }
    }


    private void addToCart() {
        if (mProductDetail != null) {
            if (Utils.isNetworkAvailable(getActivity()))
                startAddingToCart();
            else
                showSnackbar(getString(R.string.no_internet), Snackbar.LENGTH_SHORT);
        }
    }

    private AddressArray mAddressArrayObject;

    public void placeOrder() {
        addToCart();
    }

    private void onRetryButtonClicked() {
        mNoInternetLL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.VISIBLE);
        loadProduct();
    }

    private AddToCartAsync addToCartAsync;
    String[] stitchingSize;
    int[] optionValuesArray;
    String ids;

    @Override
    public void startAddingToCart() {
        mProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Adding Product to Cart ...", true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (addToCartAsync != null) {
                    addToCartAsync.cancel(true);
                }
            }
        });

        String url = ApiUrls.API_ADD_CART;

        //  HashMap<String, String> body = new HashMap<String, String>();
        JSONObject lineItemJson = null;
        JSONObject lineItemObject = new JSONObject();
        try {

            lineItemJson = new JSONObject();

            lineItemJson.put("design_id", mProductDetail.getId().toString());

            if (mProductDetail.getVariants().size() > 0) {
                lineItemJson.put("variant_id", String.valueOf(mProductVariantId));
            }

            lineItemJson.put("quantity", String.valueOf(1));

            if (productSizeTextView != null) {
                lineItemJson.put("name", size);
            }

            if (mProductDetail.getAddonTypes().size() != 0) {
                //optionValuesArray = new int[addonOptionValueId.length];

                JSONObject lineItemAddonsAttribute = new JSONObject();



                /*for (int i = 0; i < addonOptionValueId.length; i++) {
                    optionValuesArray[i] = addonOptionValueId[i];
                    if (i == 0) {
                        ids = ids + optionValuesArray[0];
                    } else {
                        ids = ids + ", " + optionValuesArray[i];
                    }
                }
                obj.put("addon_type_value_id", String.valueOf(mAddonTypeId));
                obj.put("notes", ids);
                //lineItemAddonsAttribute.put(obj);
                lineItemAddonsAttribute.put("0", obj);
                lineItemJson.put("line_item_addons_attributes", lineItemAddonsAttribute);*/

                int mainSize = addOnOptionsIds.size();
                for (int i = 0; i < mainSize; i++) {
                    JSONObject obj = new JSONObject();
                    int addOnOptionId = (int) addOnOptionsIds.get(i);

                    ArrayList<AddonTypeValue> addonTypeValueArrayList = (ArrayList<AddonTypeValue>) mAddOnType.get(i).getAddonTypeValues();
                    int size = addonTypeValueArrayList.size();
                    ids = "";
                    for (int j = 0; j < size; j++) {
                        int tempId = addonTypeValueArrayList.get(j).getId();
                        Boolean hasChild = addonTypeValueArrayList.get(j).getHasChild();
                        if (hasChild && tempId == addOnOptionId) {
                            ArrayList<AddonOptionType> addonOptionTypeArrayList = (ArrayList<AddonOptionType>) addonTypeValueArrayList.get(j).getAddonOptionTypes();
                            int addonOptionTypeArrayListSize = addonOptionTypeArrayList.size();
                            for (int k = 0; k < addonOptionTypeArrayListSize; k++) {
                                if (k == 0) {
                                    ids += addonOptionTypeArrayList.get(k).getSelectedId();
                                } else {
                                    ids += ", " + addonOptionTypeArrayList.get(k).getSelectedId();
                                }

                            }
                        }
                    }

                    /*ArrayList addOnOptionValueId = (ArrayList) addOnOptionValueIds.get(i);
                    int childSize = addOnOptionValueId.size();
                    ids = "";
                    for (int j = 0; j < childSize; j++) {
                        if (j == 0) {
                            ids = ids + addOnOptionValueId.get(j);
                        } else {
                            ids += ", " + addOnOptionValueId.get(j);
                        }
                    }*/


                    obj.put("addon_type_value_id", String.valueOf(addOnOptionId));
                    obj.put("notes", ids);
                    lineItemAddonsAttribute.put(String.valueOf(i), obj);
                }
                lineItemJson.put("line_item_addons_attributes", lineItemAddonsAttribute);
            }
            lineItemObject.put("line_item", lineItemJson);
            //lineItemJson.put("country", "US");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));

        try {
            //Logger.v("", "Login Response: " + sharedPreferencesManager.getLoginResponse());
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setBodyJson(lineItemObject).setHeaders(headerMap).build();
        addToCartAsync = new AddToCartAsync(this, getActivity());
        addToCartAsync.execute(request);
    }

    @Override
    public void addedToCartSuccessful(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (response.getResponseCode() == 200) {
                if (mPlaceOrderClicked) {
                    Intent intent = new Intent(getActivity(), CartActivity.class);
                    startActivity(intent);
                    //getActivity().finish();
                } else {
                    Utils.showSnackBar("Added to cart", getActivity(), Snackbar.LENGTH_SHORT);

                    if (getActivity() instanceof BaseActivity) {
                        BaseActivity baseActivity = (BaseActivity) getActivity();
                        baseActivity.loadCart();
                    }
                }
            } else {
                /*Gson gson = new Gson();
                String error = response.getBody();
                ErrorsResult errorsResult = gson.fromJson(error, ErrorsResult.class);
                Errors errorsMesg = errorsResult.getErrors();
                String showError = null;

                if (errorsMesg.getDesignId().size() != 0) {
                    showError = "design " + errorsMesg.getDesignId().get(0) + "\n";
                }
                if (errorsMesg.getStock().size() != 0) {
                    showError = "stock " + errorsMesg.getStock().get(0) + "\n";
                }*/
                try {
                    JSONObject jObject = new JSONObject(response.getBody());
                    jObject = jObject.getJSONObject("errors");
                    Iterator<?> keys = jObject.keys();
                    String key = "";
                    //while (keys.hasNext()) {
                    if (keys.hasNext()) {
                        key = (String) keys.next();
                        if (jObject.getJSONArray(key) instanceof JSONArray) {
                            JSONArray array = jObject.getJSONArray(key);
                            key = key + " " + array.get(0).toString();
                        }
                    }
                    addToCartFailed(key);
                } catch (JSONException e) {
                    Logger.v("", "Exception: " + e.getMessage().toString());
                }
            }
        }

    }

    @Override
    public void addedToCartFailed(int responseCode) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    }

    private void addToCartFailed(String errors) {
        Toast.makeText(getActivity(), errors, Toast.LENGTH_SHORT).show();
    }

    GetProductDetailTabAsync productDetailTabAsync;

    @Override
    public void loadTabDetails() {
        mNoInternetLLTabs.setVisibility(View.GONE);
        mProgressWheelTabs.setVisibility(View.VISIBLE);
        productDetailTabAsync = new GetProductDetailTabAsync(this, getActivity());
        String url = ApiUrls.API_PRODUCT_TAB_DETAILS;

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        productDetailTabAsync.execute(request);
    }

    private TabDetails mTabDetail;

    @Override
    public void onTabDetailsLoaded(Response response) {

        Gson gson = new Gson();

        mTabDetail = gson.fromJson(response.getBody(), TabDetails.class);

        if (mAppSharedPrefs.getTabDetails().equalsIgnoreCase("")) {
            setupViewPager();
            setupTabs();
        }
        mAppSharedPrefs.setTabDetails(response.getBody());
    }

    @Override
    public void onTabDetailsLoadingFailed(Response response) {
        mConnectionContainer.setVisibility(View.GONE);
        mProductDetailSV.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), "Could not load details", Toast.LENGTH_LONG).show();
        mProgressWheelTabs.setVisibility(View.GONE);
        mNoInternetLLTabs.setVisibility(View.VISIBLE);
        mBuyNowAddToCartDisplayListener.showBuyNowAddToCartButtons();
    }

    private CodAsync mCodAsync;

    private void checkCod() {
        String strPinCode = mTxtCheckCod.getText().toString();
        //Call async from here and get the result, will be either true or false and show a textfield saying COD available or not

        if (!strPinCode.trim().equalsIgnoreCase("") && strPinCode.trim().length() == 6) {
            if (mCodAsync != null) {
                mCodAsync.cancel(true);
            }
            Utils.hideSoftKeyboard(getActivity(), mTxtCheckCod);
            mProgressWheelCod.setVisibility(View.VISIBLE);
            mTxtAvailable.setVisibility(View.GONE);

            mCodAsync = new CodAsync(this);

            String url = ApiUrls.API_PRODUCTS + productId + "/cod?pincode=" + strPinCode;

            HashMap<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            requestHeaders.put(Headers.TOKEN, getString(R.string.token));

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                    .setHeaders(requestHeaders)
                    .build();

            mCodAsync.execute(request);
        } else {
            Utils.hideSoftKeyboard(getActivity(), mTxtCheckCod);
            showSnackbar("Please enter valid pincode", Snackbar.LENGTH_LONG);
        }

    }

    private void showSnackbar(String message, int time) {
        Snackbar snackbar = Snackbar.make(getView(), message, time)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        TextView snackbarTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(getResources().getColor(R.color.white));
        snackbar.getView().setPadding(0, 10, 0, 10);
        snackbar.show();
    }


    private COD mCod;

    @Override
    public void onCodLoaded(Response response) {
        String strResponse = response.getBody();
        Gson gson = new Gson();
        if (response.getResponseCode() == 200) {
            mCod = gson.fromJson(strResponse, COD.class);

            boolean codAvailable = mCod.getCod();
            mProgressWheelCod.setVisibility(View.GONE);
            mTxtAvailable.setVisibility(View.VISIBLE);
            if (codAvailable) {
                //Toast.makeText(getActivity(), "COD available", Toast.LENGTH_LONG).show();
                //mTxtCheckCod.setText("COD available");
                mTxtAvailable.setText("COD available");
                mTxtAvailable.setTextColor(getResources().getColor(R.color.green_color));
            } else {
                //Toast.makeText(getActivity(), "COD not available", Toast.LENGTH_LONG).show();
                //mTxtCheckCod.setText("COD not available");
                mTxtAvailable.setText("COD not available");
                mTxtAvailable.setTextColor(getResources().getColor(R.color.red));
            }
        } else {
            onCodLoadingFailed(response);
        }

    }

    @Override
    public void onCodLoadingFailed(Response response) {
        int responseCode = response.getResponseCode();
        if (responseCode == 0) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.no_internet), Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        } else if (responseCode == 500) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Server error", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }
        //Toast.makeText(getActivity(), "Please try again " + response.getResponseCode(), Toast.LENGTH_LONG).show();
    }

    private LoginDialogFragment mLoginDialogFragment;


    private List<Variant> mProductSize;
    private View view;
    private View lastClickCircleView = null;
    private boolean isSelectSize = false;
    private TextView productSizeTextView;
    private String size;
    private TextView sizeText;
    private int mProductVariantId;

    final String TAG = ProductDetailFragment.class.getSimpleName();

    public void showProductSizeInfo(List<Variant> productSizeList) {
        mProductSize = productSizeList;

        List<OptionTypeValue> optionTypeValues;

        mSizeLinearLayout = (LinearLayout) getView().findViewById(R.id.select_sizeLinearLayout);
        mSizeLinearLayout.removeAllViews();
        Logger.d(TAG, "ProductSize size: " + mProductSize.size());
        for (int i = 0; i < mProductSize.size(); i++) {


            view = LayoutInflater.from(getActivity()).inflate(R.layout.select_size_layout, mSizeLinearLayout, false);
            horizontalCircleLayout = (LinearLayout) view.findViewById(R.id.horizentol_main_layout);
            final int variantId = mProductSize.get(i).getId();
            productSizeTextView = (TextView) view.findViewById(R.id.sizeTextView);
            optionTypeValues = mProductSize.get(i).getOptionTypeValues();

            for (int j = 0; j < optionTypeValues.size(); j++) {

                productSizeTextView.setText(String.valueOf(optionTypeValues.get(j).getName()));
                Logger.d(TAG, "name size: " + String.valueOf(optionTypeValues.get(j).getName()));
            }

            mSizeLinearLayout.addView(view);

            if (mProductSize.get(i).getQuantity() > 0) {
                horizontalCircleLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView t = (TextView) v.findViewById(R.id.sizeTextView);
                        if (lastClickCircleView != null && sizeText != null) {
                            lastClickCircleView.setBackgroundResource(R.drawable.circle_layout_selector);
                            sizeText.setTextColor(getActivity().getResources().getColor(R.color.green_color));
                        }
                        mProductVariantId = variantId;
                        lastClickCircleView = v;
                        sizeText = t;
                        v.setBackgroundResource(R.drawable.circle_green_shape);
                        isSelectSize = true;
                        t.setTextColor(getActivity().getResources().getColor(R.color.white));
                        size = t.getText().toString();
                    }
                });
            } else {
                productSizeTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                productSizeTextView.setTextColor(getResources().getColor(R.color.green_color_dark));
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_layout_dark_bg));
            }


        }


    }

    private ArrayList addOnOptionsIds, addOnOptionValueIds;
    private Spinner spinnerMain;
    private List<AddonType> mAddOnType;
    private LinearLayout addonLinearLayout;
    //private List<AddonTypeValue> addonTypeValues;
    private ArrayList<ArrayList<AddonTypeValue>> tempAddonTypeValues;
    //    LinearLayout childLinearLayout;
    private int[] prodTimeArray;
    private double[] addonPriceArray;

    private void addonType(List<AddonType> addonTypes) {
        mAddOnType = addonTypes;


        tempAddonTypeValues = new ArrayList<ArrayList<AddonTypeValue>>();
        addOnOptionsIds = new ArrayList();
        addOnOptionValueIds = new ArrayList();

        Logger.d(TAG, "addonType addonTypes : " + addonTypes.size());
        addonLinearLayout = (LinearLayout) getView().findViewById(R.id.main_addon_layout);
        prodTimeArray = new int[mAddOnType.size()];
        addonPriceArray = new double[mAddOnType.size()];
        for (int i = 0; i < mAddOnType.size(); i++) {
            addonLinearLayout.setVisibility(View.VISIBLE);
            final View SpinnerView;
            SpinnerView = LayoutInflater.from(getActivity()).inflate(R.layout.spiner_item_layout, addonLinearLayout, false);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(40, 0, 0, 0);
            spinnerMain = (Spinner) SpinnerView.findViewById(R.id.main_addon_spinner);
            spinnerMain.setLayoutParams(lp);
            stitchingTextView = (TextView) SpinnerView.findViewById(R.id.stitching_size);
            stitchingTextView.setVisibility(view.VISIBLE);
            stitchingTextView.setText(mAddOnType.get(i).getName());
            final List<AddonTypeValue> addonTypeValues = mAddOnType.get(i).getAddonTypeValues();
            mAddOnType.get(i).getName();
            tempAddonTypeValues.add((ArrayList) addonTypeValues);
            AddonAdapter adapter = new AddonAdapter(addonTypeValues, strSymbol);
            spinnerMain.setPrompt(mAddOnType.get(i).getName());
            spinnerMain.setTag(i);
            spinnerMain.setAdapter(adapter);
            final ArrayList addonOptionValueId = null;

            spinnerMain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                LinearLayout childLinearLayout;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int spinnerPosition = (Integer) parent.getTag();

                    prodTimeArray[spinnerPosition] = addonTypeValues.get(position).getProdTime();
                    addonPriceArray[spinnerPosition] = addonTypeValues.get(position).getPrice();

                    addonOptionTypes = tempAddonTypeValues.get(spinnerPosition).get(position).getAddonOptionTypes();
                    mAddonTypeId = addonTypeValues.get(position).getId();


                    //addonTypeValues.get(spinnerPosition).setHasChild(false);
                    if (addonOptionTypes.size() > 0) {
                        childLinearLayout = new LinearLayout(getActivity());
                        childLinearLayout.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(50, 10, 50, 0);
                        childLinearLayout.setLayoutParams(lp);
                        addonTypeValues.get(position).setHasChild(true);

                        addOnOptionValue(SpinnerView, childLinearLayout, addonOptionTypes, spinnerPosition, addonOptionValueId);

                    } else if (childLinearLayout != null) {
                        int index = ((ViewGroup) SpinnerView.getParent()).indexOfChild(SpinnerView);
                        addonLinearLayout.removeViewAt(index + 1);
                        childLinearLayout = null;
                        addonTypeValues.get(position).setHasChild(false);
                    }

                    //addOnOptionsIds.add(spinnerPosition, mAddonTypeId);
                    if (addOnOptionsIds.size() < mAddOnType.size()) {
                        addOnOptionsIds.add(spinnerPosition, mAddonTypeId);
                    } else {
                        addOnOptionsIds.set(spinnerPosition, mAddonTypeId);
                    }

                    Logger.d(TAG, "onItemSelected addOnOptionsIds: " + addOnOptionsIds);
                    updateProdTime();
                    updateTotalPrice();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //spinnerMain.setOnItemSelectedListener(this);
            addonLinearLayout.addView(SpinnerView);
        }

        LayoutTransition layoutTransition = new LayoutTransition();
        mProductDetailSV.setLayoutTransition(layoutTransition);

    }

    private void updateTotalPrice() {
        int addonPriceArrayLength = addonPriceArray.length;
        double totalAddonPrice = 0;

        for (int i = 0; i < addonPriceArrayLength; i++) {
            totalAddonPrice += addonPriceArray[i];
        }
        TextView addonTotalPrice = (TextView) getView().findViewById(R.id.addon_total_price);
        totalAddonPrice = Utils.round(totalAddonPrice, 2);
        Double total = mProductDetail.getDiscountPrice() + totalAddonPrice;
        total = Utils.round(total, 2);

        if (totalAddonPrice > 0) {
            addonTotalPrice.setVisibility(View.VISIBLE);

            String strTotal = "Customization cost: " + strSymbol + " " + totalAddonPrice + "\n\nTotal: " + strSymbol + " " + total;
            addonTotalPrice.setText(strTotal);
        } else {
            addonTotalPrice.setVisibility(View.GONE);
        }
    }


    private void updateProdTime() {
        int prodTimeArrayLength = prodTimeArray.length;
        int max = prodTimeArray[0];
        for (int i = 0; i < prodTimeArrayLength; i++) {
            if (prodTimeArray[i] > max) {
                max = prodTimeArray[i];
            }
        }

        TextView addonProdTime = (TextView) getView().findViewById(R.id.addon_prod_time);

        if (max == 0) {
            addonProdTime.setVisibility(View.GONE);
        } else {
            addonProdTime.setVisibility(View.VISIBLE);
            addonProdTime.setText("Current customization will delay the delivery by " + max + " days");
        }
    }

    List<AddonOptionType> addonOptionTypes;
    private int mAddonTypeId = 0;


    private Spinner ChildSpinner;
    private List<AddonOptionValue> mAddOnOptionValue;
    private List<AddonOptionType> mAddonOptionTypes;
    private LinearLayout addOnChildLinearLayout;
    private TextView stitchingTextView;
    private View SpinnerView;
    private ArrayList<ArrayList<AddonOptionValue>> tempAddonOptionValue;
    String[] addonOptionValueString;


    private void addOnOptionValue(View spinnerView, LinearLayout childLinearLayout, List<AddonOptionType> addonOptionTypes, final int parentPosition, ArrayList addonOptionValueId) {

        Logger.d(TAG, "addOnOptionValue addonOptionTypes: " + addonOptionTypes.size());
        mAddonOptionTypes = addonOptionTypes;
//        mAddOnOptionValue = new List[mAddonOptionTypes.size()];
//        ChildSpinner = new Spinner[mAddonOptionTypes.size()];
//        SpinnerView = new View[mAddonOptionTypes.size()];
        tempAddonOptionValue = new ArrayList<ArrayList<AddonOptionValue>>();
        int index = ((ViewGroup) spinnerView.getParent()).indexOfChild(spinnerView);
        childLinearLayout.removeAllViews();
        addonOptionValueId = new ArrayList();
        final int arrayListSize = mAddOnType.get(parentPosition).getAddonTypeValues().size();
        for (int i = 0; i < mAddonOptionTypes.size(); i++) {

            SpinnerView = LayoutInflater.from(getActivity()).inflate(R.layout.spiner_item_layout, addonLinearLayout, false);
            ChildSpinner = (Spinner) SpinnerView.findViewById(R.id.main_addon_spinner);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(50, 0, 0, 0);
            ChildSpinner.setLayoutParams(lp);
            stitchingTextView = (TextView) SpinnerView.findViewById(R.id.stitching_size);
            stitchingTextView.setVisibility(view.VISIBLE);
            stitchingTextView.setText(mAddonOptionTypes.get(i).getPName());
            mAddOnOptionValue = mAddonOptionTypes.get(i).getAddonOptionValues();
            tempAddonOptionValue.add((ArrayList) mAddOnOptionValue);

            AddonChildAdapter adapter = new AddonChildAdapter(mAddOnOptionValue);
            ChildSpinner.setPrompt(mAddonOptionTypes.get(i).getPName());
            ChildSpinner.setGravity(Gravity.CENTER_HORIZONTAL);
            ChildSpinner.setTag(i);
            ChildSpinner.setAdapter(adapter);
            ChildSpinner.setVisibility(View.VISIBLE);
            final ArrayList finalAddonOptionValueId = addonOptionValueId;
            ChildSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    int childSpinnnerPosition = (Integer) parent.getTag();


//                    String str = tempAddonOptionValue.get(childSpinnnerPosition).get(position).getPName();
//                    System.out.println("value is : " + str);
//                    if (addonOptionValueString == null) {
//                        addonOptionValueString = new String[tempAddonOptionValue.size()];
//                        for (int i = 0; i < tempAddonOptionValue.size(); i++) {
//                            addonOptionValueString[i] = tempAddonOptionValue.get(childSpinnnerPosition).get(0).getPName();
//                        }
//                    }
//
//                    addonOptionValueString[childSpinnnerPosition] = str;

                    int optionValueId = tempAddonOptionValue.get(childSpinnnerPosition).get(position).getId();
                    mAddonOptionTypes.get(childSpinnnerPosition).setSelectedId(optionValueId);

                    /*//Logger.d(TAG, "onItemSelected value is : " + optionValueId);
                    if (finalAddonOptionValueId.size() < arrayListSize) {
                        finalAddonOptionValueId.add(childSpinnnerPosition, tempAddonOptionValue.get(childSpinnnerPosition).get(0).getId());
                    } else {
                        finalAddonOptionValueId.set(childSpinnnerPosition, optionValueId);
                    }

                    Logger.d(TAG, "addonOptionValueId : " + finalAddonOptionValueId);
                    //addOnOptionValueIds.add(parentPosition, addonOptionValueId);

                    if (addOnOptionValueIds.size() < mAddOnType.size()) {
                        addOnOptionValueIds.add(parentPosition, finalAddonOptionValueId);
                    } else {
                        addOnOptionValueIds.set(parentPosition, finalAddonOptionValueId);
                    }
                    Logger.d(TAG, "onItemSelected addOnOptionValueIds: " + addOnOptionValueIds.size());*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            childLinearLayout.addView(SpinnerView, i);
        }

        addonLinearLayout.addView(childLinearLayout, index + 1);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//        int spinnnerPosition = (Integer) parent.getTag();
//
//        addonOptionTypes = tempAddonTypeValues.get(spinnnerPosition).get(position).getAddonOptionTypes();
//        mAddonTypeId = addonTypeValues.get(position).getId();
//        if (addonOptionTypes.size() > 0) {
//            addOnChildLinearLayout = (LinearLayout) getView().findViewById(R.id.child_addon_layout);
//            addOnChildLinearLayout.setVisibility(View.VISIBLE);
//            addOnOptionValue(addonOptionTypes, position);
//        } else {
//
//            addOnChildLinearLayout.setVisibility(View.GONE);
//            addOnChildLinearLayout.removeAllViews();
//        }


    }

    @Override
    public void onBuyNowClicked() {
        try {
            mPlaceOrderClicked = true;

            if (mProductDetail != null && mProductDetail.getVariants().size() != 0) {
                if (!isSelectSize) {
                    showSnackbar("Select your size", Snackbar.LENGTH_SHORT);
                    animateHorizontalScrollView();
                } else {
                    placeOrder();
                }
            } else {
                placeOrder();
            }
        } catch (Exception e) {
            Gson gson = new Gson();
            String message = gson.toJson(mProductDetail);
            Mint.logExceptionMessage(TAG, message, e);
            showSnackbar(getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public void onAddToCartClicked() {
        try {
            mPlaceOrderClicked = false;
            if (mProductDetail != null && mProductDetail.getVariants().size() != 0) {
                if (!isSelectSize) {
                    showSnackbar("Select your size", Snackbar.LENGTH_SHORT);
                    animateHorizontalScrollView();
                } else {
                    addToCart();
                }
            } else {
                addToCart();
            }
        } catch (Exception e) {
            Gson gson = new Gson();
            String message = gson.toJson(mProductDetail);
            Mint.logExceptionMessage(TAG, message, e);
            showSnackbar(getString(R.string.some_error_occured), Snackbar.LENGTH_SHORT);
        }

    }

    private void animateHorizontalScrollView() {
        mProductDetailSV.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mProductDetailSV.smoothScrollTo((int) mHorizontalScrollView.getX(), mHorizontalScrollView.getBottom() + 300);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(
                        ObjectAnimator.ofInt(mProductDetailSV, "scrollY", mHorizontalScrollView.getBottom() + 300).setDuration(700),
                        ObjectAnimator.ofFloat(mSizeLinearLayout, "translationX", 0, 20, -20, 15, -15, 6, -6, 0)
                                .setDuration(900)
                );
                animatorSet.start();
            }
        }, 200);
    }


    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {

            case R.id.check_cod_ripple_view:
                checkCod();
                break;
            case R.id.retry_button_ripple_container:
                onRetryButtonClicked();
                break;


        }

    }
}
