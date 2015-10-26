package com.mirraw.android.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.CartAsync;
import com.mirraw.android.async.CartDeleteItemAsync;
import com.mirraw.android.async.CartUpdateItemAsync;
import com.mirraw.android.async.CouponAsync;
import com.mirraw.android.async.ListAddressAsync;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.models.address.AddressArray;
import com.mirraw.android.models.address.ListOfAddress;
import com.mirraw.android.models.cart.Cart;
import com.mirraw.android.models.cart.CartItems;
import com.mirraw.android.models.productDetail.ProductDetail;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.activities.AddBillingShippingActivity;
import com.mirraw.android.ui.activities.CartActivity;
import com.mirraw.android.ui.activities.ConfirmAddressActivity;
import com.mirraw.android.ui.adapters.CartAdapter;
import com.mirraw.android.ui.fragments.filters.NetworkStatusDialogFragment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by vihaan on 3/7/15.
 */
public class CartFragment extends Fragment implements
        CartAsync.CartLoader,
        CartDeleteItemAsync.DeleteItemCartLoader,
        CartUpdateItemAsync.UpdateItemCartLoader,
        CartAdapter.DataChanged,
        ListAddressAsync.AddressLoader,
        CouponAsync.CouponApplyLoader, RippleView.OnRippleCompleteListener {

    private SharedPreferencesManager mAppSharedPrefs;
    boolean couponApplied = false;
    boolean mPlaceOrderClicked = false;
    ProgressDialog mProgressDialog;

    public void continueToPayment() {
        if (mPlaceOrderClicked) {

            mPlaceOrderClicked = false;
            placeOrderClick();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mAppSharedPrefs = new SharedPreferencesManager(getActivity());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        loadCart();
    }


    private void initViews(View view) {
        initEmptyCartView(view);
        initProgressWheel(view);
        initCartListView(view);
        initPlaceOrderView(view);
        initProgressDialog();
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading_addresses));
        mProgressDialog.setOnCancelListener(new ProgressDialog.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mAddAddressAsync != null) {
                    mAddAddressAsync.cancel(true);
                }
            }
        });
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel(View view) {
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
        mProgressWheel.setVisibility(View.VISIBLE);
    }

    private TextView mNoInternetTextView;
    private LinearLayout mEmptyCartLayout;
    private RippleView mRetryButtonRippleContainer;

    private void initEmptyCartView(View view) {
        mEmptyCartLayout = (LinearLayout) view.findViewById(R.id.empty_cartLL);
        mEmptyCartLayout.setVisibility(View.GONE);

        mNoInternetTextView = (TextView) view.findViewById(R.id.noInternetTextView);
        mNoInternetTextView.setVisibility(View.GONE);

        mRetryButtonRippleContainer = (RippleView) view.findViewById(R.id.retry_button_ripple_container);
        mRetryButtonRippleContainer.setVisibility(View.GONE);
        mRetryButtonRippleContainer.setOnRippleCompleteListener(this);
    }

    private ListView mCartListView;

    private void initCartListView(View view) {
        mCartListView = (ListView) view.findViewById(R.id.cartListView);
        mCartListView.setVisibility(View.GONE);
        mCartListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
    }

    private LinearLayout mPlaceOrderLL;
    private View mShadowView;

    private void initPlaceOrderView(View view) {
        mPlaceOrderLL = (LinearLayout) view.findViewById(R.id.placeOrderLL);
        mShadowView = view.findViewById(R.id.shadowView);
        mPlaceOrderLL.setVisibility(View.GONE);
        mShadowView.setVisibility(View.GONE);
        ((RippleView) view.findViewById(R.id.rippleView)).setOnRippleCompleteListener(this);
    }

    private CartAsync mCartAsync;
    CartDeleteItemAsync mCartDeleteItemAsync;
    CouponAsync mCouponAsync;
    CartUpdateItemAsync mCartUpdateItemAsync;
    private ListAddressAsync mAddAddressAsync;

    @Override
    public void loadCart() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mCartListView.setVisibility(View.GONE);
        mRetryButtonRippleContainer.setVisibility(View.GONE);
        mNoInternetTextView.setVisibility(View.GONE);

        String url = ApiUrls.API_GET_CART_ITEMS;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        Logger.v("Token", "TOKEN: " + getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));

        Logger.v("Device Id", "DEVICE_ID: " + NetworkUtil.getDeviceId(getActivity()));
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            Logger.v("Access_Token", "ACCESS_TOKEN: " + head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            Logger.v("Client", "CLIENT: " + head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            Logger.v("Token Type", "TOKEN TYPE: " + head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            Logger.v("UID", "UID: " + head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
        mCartAsync = new CartAsync(this);
        mCartAsync.executeTask(request);
    }

    @Override
    public void onEmptyCart(Response response) {
        mProgressWheel.setVisibility(View.GONE);
        mCartListView.setVisibility(View.GONE);

        if (response.getResponseCode() == 0) {
            mNoInternetTextView.setVisibility(View.VISIBLE);
            mRetryButtonRippleContainer.setVisibility(View.VISIBLE);
        } else
            mEmptyCartLayout.setVisibility(View.VISIBLE);

        mCartListView.setVisibility(View.GONE);
        mShadowView.setVisibility(View.GONE);
        mPlaceOrderLL.setVisibility(View.GONE);
    }

    private CartAdapter mCartAdapter;
    private ArrayList<ProductDetail> mProductDetails;


    private CartItems mCartItems;

    @Override
    public void onCartLoaded(Response response) {
        Gson gson = new Gson();
        mCartItems = (CartItems) gson.fromJson(response.getBody(), CartItems.class);
        try {
            CartItems cartItems = (CartItems) gson.fromJson(response.getBody(), CartItems.class);
            Logger.v("", "Response Body: " + response.getBody());
            Logger.v("", "Response Code: " + response.getResponseCode());

            mShadowView.setVisibility(View.VISIBLE);
            mPlaceOrderLL.setVisibility(View.VISIBLE);
            Logger.v("LineItem Size", "LineItem Size: " + mCartItems.getCart().getLineItems().size());
            if (mCartItems.getCart().getLineItems().size() == 0) {
                onEmptyCart(response);
                return;
            }
            Logger.v("Total", "Total: " + mCartItems.getCart().getTotal() + "   " + mCartItems.getCart().getDiscount() + "   " + mCartItems.getCart().getItemTotal());
            mCartAdapter = new CartAdapter(getActivity(), mCartItems.getCart().getLineItems(), this);
            if (mCartListView.getFooterViewsCount() == 0) {
                addListViewFooter(mCartItems);
            } else {
                updateFooterData(cartItems.getCart());
            }
            mCartListView.setAdapter(mCartAdapter);
            mCartListView.setVisibility(View.VISIBLE);
            mProgressWheel.setVisibility(View.GONE);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Problem Loading Data.", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView mFooterItemTotal, mFooterDiscountAmt, mFooterTotalAmt;

    private View mFooterView;

    private void addListViewFooter(CartItems cartItems) {
        //footerAlreadyAdded
        if (couponApplied == false) {
            mFooterView = LayoutInflater.from(getActivity()).inflate(R.layout.cart_view_footer, mCartListView, false);

            mFooterItemTotal = (TextView) mFooterView.findViewById(R.id.item_total_amt);
            mFooterDiscountAmt = (TextView) mFooterView.findViewById(R.id.discount_amt);
            mFooterTotalAmt = (TextView) mFooterView.findViewById(R.id.total_amt);

            final EditText couponEditText = (EditText) mFooterView.findViewById(R.id.coupon_code_editText);

            couponEditText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Rect r = new Rect();
                            getView().getWindowVisibleDisplayFrame(r);
                            int screenHeight = getView().getRootView().getHeight();
                            // r.bottom is the position above soft keypad or device button.
                            // if keypad is shown, the r.bottom is smaller than that before.
                            int keypadHeight = screenHeight - r.bottom;
                            Log.d("KEYPAD", "keypadHeight = " + keypadHeight);
                            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                                // keyboard is opened
                                mCartListView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Select the last row so it will scroll into view...
                                        mCartListView.smoothScrollToPosition(mCartListView.getCount() - 1);
                                    }
                                }, 200);
                            }
                        }
                    });
                    return false;
                }
            });
        }
        try {
            updateFooterData(cartItems.getCart());
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        if (couponApplied == false) {
            ((RippleView) mFooterView.findViewById(R.id.apply_coupon_ripple_view)).setOnRippleCompleteListener(this);
        }

        mCartListView.addFooterView(mFooterView);
    }

    private void showSnackbar(String message, int time) {
        Snackbar snackbar = Snackbar.make(getView(), message, time)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        /*if (mCartAdapter != null) {

            ArrayList<ProductDetail> products = mCartAdapter.getProducts();
            CartManager cartManager = new CartManager();
            cartManager.clearTable();
            cartManager.saveProducts(products);
        }*/
    }


    @Override
    public void applyCoupon() {

        EditText couponCodeEditText = (EditText) getView().findViewById(R.id.coupon_code_editText);

        if (!couponCodeEditText.getText().toString().trim().equalsIgnoreCase("")) {
            mProgressWheel.setVisibility(View.VISIBLE);
            mPlaceOrderLL.setVisibility(View.GONE);
            mShadowView.setVisibility(View.GONE);
            mCartListView.setVisibility(View.INVISIBLE);

            String url = ApiUrls.API_COUPON_APPLY;

            SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
            JSONObject head;
            HashMap<String, String> headerMap = new HashMap<String, String>();
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            try {
                head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
                Logger.v("", "Login Response: " + head.getJSONArray("Access-Token").get(0).toString());
                headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray("Access-Token").get(0).toString());
                headerMap.put(Headers.CLIENT, head.getJSONArray("Client").get(0).toString());
                headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray("Token-Type").get(0).toString());
                headerMap.put(Headers.UID, head.getJSONArray("Uid").get(0).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HashMap<String, String> body = new HashMap<String, String>();
            body.put("code", couponCodeEditText.getText().toString());

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.PUT).setHeaders(headerMap).setBody(body).build();
            mCouponAsync = new CouponAsync(this);
            mCouponAsync.executeTask(request);
        } else {
            showSnackbar("Please Enter Proper Coupon Code", Snackbar.LENGTH_LONG);

        }
    }

    @Override
    public void couponAppliedSuccessfully(Response response) {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mCartListView.setVisibility(View.VISIBLE);
        mPlaceOrderLL.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.VISIBLE);
        mCartListView.removeFooterView(mFooterView);
        loadCart();
        showSnackbar("Coupon Code Applied Successfully", Snackbar.LENGTH_LONG);

    }

    @Override
    public void couponAppliedFailed(Response response) {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mCartListView.setVisibility(View.VISIBLE);
        mPlaceOrderLL.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.VISIBLE);
        /**Response Body Format Below**/
        /*{
            "errors": {
                "coupon": [
                    "not found"
                ]
            }
        }*/
        if (response.getResponseCode() == 0) {
            showSnackbar(getString(R.string.no_internet), Snackbar.LENGTH_LONG);
        } else {
            try {
                JSONObject body = new JSONObject(response.getBody());
                Iterator<String> keys = body.getJSONObject("errors").keys();
            /*while (keys.hasNext()) {
                Object obj = keys.next();
                Logger.v("", "KEYS: " + obj.toString());
            }*/
                String error = keys.next().toString()
                        + " " +
                        body.getJSONObject("errors").getJSONArray("coupon").getString(0).toString();
                showSnackbar(error, Snackbar.LENGTH_LONG);

            } catch (Exception e) {
                e.printStackTrace();
                showSnackbar("Please Enter Proper Coupon Code", Snackbar.LENGTH_LONG);
            }
        }

    }

    private AddressArray mAddressArrayObject;

    public void placeOrder() {
        if (mCartAdapter != null) {
            if (mCartAdapter.getProducts().size() != 0) {
                String json = mAppSharedPrefs.getAddresses();
                Gson gson = new Gson();
                mAddressArrayObject = gson.fromJson(json, AddressArray.class);

                ArrayList<Address> addressArray = null;

                try {
                    addressArray = mAddressArrayObject.getAddresses();
                } catch (NullPointerException e) {
                    addressArray = null;
                }

                Gson gsonProducts = new Gson();
                String productsString = gsonProducts.toJson(mCartAdapter.getProducts());
                Intent intent;

                if (addressArray == null || addressArray.size() == 0) {
                    intent = new Intent(getActivity(), AddBillingShippingActivity.class);
                    /*if (Float.parseFloat(mCartItems.getCart().getTotal().toString().trim()) <= 0) {
                        intent.putExtra("FREE_ORDER", true);
                    } else {
                        intent.putExtra("FREE_ORDER", false);
                    }*/
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), ConfirmAddressActivity.class);
                    /*if (Float.parseFloat(mCartItems.getCart().getTotal().toString().trim()) <= 0) {
                        intent.putExtra("FREE_ORDER", true);
                    } else {
                        intent.putExtra("FREE_ORDER", false);
                    }*/
                    intent.putExtra("AddressJson", json);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(getActivity(), "No Products Found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void updateFooterData(Cart cartDetails) {

        String currencyCode = String.valueOf(Character.toChars((char) Integer.parseInt(cartDetails.getHexSymbol(), 16)));
        float itemTotalVal = 0;
        float deliveryAmtVal = 0;
        float discountAmtVal = 0;
        float totalAmtVal = 0;

       /* LineItem productDetail;
        for (int i = 0; i < productDetails.size(); i++) {
            productDetail = productDetails.get(i);
            itemTotalVal = itemTotalVal + Float.parseFloat(productDetail.getTotal().trim().split(" ")[1].trim());
            //discountAmtVal = discountAmtVal + ((productDetail.getPrice() - productDetail.getDiscountPrice()) * productDetail.getQuantity());
        }

        totalAmtVal = itemTotalVal + deliveryAmtVal - discountAmtVal;*/

        mFooterItemTotal.setText(currencyCode + cartDetails.getItemTotal());
        //mFooterDeliveryAmt.setText("Rs." + deliveryAmtVal);
        mFooterDiscountAmt.setText("- " + currencyCode + cartDetails.getDiscount());
        mFooterTotalAmt.setText(currencyCode + cartDetails.getTotal());
    }


    private int mIdRemove = -1;

    @Override
    public void removeItem(int id) {
        //initViews(getView());
        mIdRemove = id;
        Logger.v("ID REMOVE", "REMOVE ID: " + id);
        mProgressWheel.setVisibility(View.VISIBLE);
        mCartListView.setVisibility(View.INVISIBLE);
        removeItemFromCart(id);
    }

    @Override
    public void removeItemFromCart(int id) {
        String url = ApiUrls.API_DEL_CART_ITEMS + id;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            Logger.v("", "Login Response: " + head.getJSONArray("Access-Token").get(0).toString());
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());

            mPlaceOrderLL.setVisibility(View.GONE);
            mShadowView.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.DELETE).setHeaders(headerMap).build();
        mCartDeleteItemAsync = new CartDeleteItemAsync(this);
        mCartDeleteItemAsync.executeTask(request);
    }

    @Override
    public void cartItemDeletedSuccessfully(Response response) {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mCartListView.setVisibility(View.VISIBLE);
        mCartListView.removeFooterView(mFooterView);
        showSnackbar("Item deleted Successfully", Snackbar.LENGTH_LONG);
        loadCart();
    }

    @Override
    public void cartItemDeletionFailed(Response response) {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mCartListView.setVisibility(View.VISIBLE);
        mPlaceOrderLL.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.VISIBLE);

        final android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        NetworkStatusDialogFragment prev = (NetworkStatusDialogFragment) fragmentManager.findFragmentByTag(NetworkStatusDialogFragment.TAG);
        if (prev != null) {
            prev.dismissAllowingStateLoss();
            ft.remove(prev);
        }

        NetworkStatusDialogFragment newRetryFragment = new NetworkStatusDialogFragment();
        try {
            Bundle args = new Bundle();
            args.putString("ACTION", "REMOVE");
            args.putInt("ID", mIdRemove);
            newRetryFragment.setArguments(args);
            newRetryFragment.show(ft, NetworkStatusDialogFragment.TAG);
        } catch (IllegalStateException e) {
            return;
        }
    }


    private int mIdUpdate = -1;
    private int mQuantity = -1;

    @Override
    public void updateQuantity(int id, int quantity) {
        mProgressWheel.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.GONE);
        mPlaceOrderLL.setVisibility(View.GONE);
        mCartListView.setVisibility(View.INVISIBLE);
        mIdUpdate = id;
        mQuantity = quantity;
        updateCartItem(id, quantity);
    }


    @Override
    public void updateCartItem(int id, int quantity) {
        String url = ApiUrls.API_UPDATE_CART_ITEMS + id;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        headerMap.put(Headers.TOKEN, getString(R.string.token));
        headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            Logger.v("", "Login Response: " + head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, String> bodyMap = new HashMap<String, String>();
        bodyMap.put("quantity", String.valueOf(quantity));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.PUT).setHeaders(headerMap).setBody(bodyMap).build();
        mCartUpdateItemAsync = new CartUpdateItemAsync(this);
        mCartUpdateItemAsync.executeTask(request);
    }

    @Override
    public void cartItemUpdatedSuccessfully(Response response) {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mCartListView.setVisibility(View.VISIBLE);
        mPlaceOrderLL.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.VISIBLE);
        mCartListView.removeFooterView(mFooterView);
        showSnackbar("Item Updated Successfully", Snackbar.LENGTH_LONG);
        loadCart();
    }

    @Override
    public void cartItemUpdationFailed(Response response) {
        mProgressWheel.setVisibility(View.INVISIBLE);
        mCartListView.setVisibility(View.VISIBLE);
        mPlaceOrderLL.setVisibility(View.VISIBLE);
        mShadowView.setVisibility(View.VISIBLE);
        //if (response.getResponseCode() == 0) {
        final android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        NetworkStatusDialogFragment prev = (NetworkStatusDialogFragment) fragmentManager.findFragmentByTag(NetworkStatusDialogFragment.TAG);
        if (prev != null) {
            prev.dismissAllowingStateLoss();
            ft.remove(prev);
        }

        NetworkStatusDialogFragment newRetryFragment = new NetworkStatusDialogFragment();
        try {
            Bundle args = new Bundle();
            args.putString("ACTION", "UPDATE");
            args.putInt("ID", mIdUpdate);
            args.putInt("QUANTITY", mQuantity);
            newRetryFragment.setArguments(args);
            newRetryFragment.show(ft, NetworkStatusDialogFragment.TAG);
        } catch (IllegalStateException e) {
            return;
        }
        /*} else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Item Update Failed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
        }*/
        //loadCart();
    }


    @Override
    public void loadAddressList() {
        mProgressDialog.show();
        mAppSharedPrefs.clearAddresses();
        String url = ApiUrls.API_ADD_ADDRESS;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
        mAddAddressAsync = new ListAddressAsync(this, getActivity());
        mAddAddressAsync.executeTask(request);
    }

    @Override
    public void loadAddressFailed(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (response.getResponseCode() == 0) {
                showSnackBar(getString(R.string.no_internet));
            } else {
                showSnackBar(getString(R.string.problem_loading_data));
            }
        }

    }

    @Override
    public void loadAddressSuccess(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String getAddress = response.getBody();
                getData(getAddress);
                placeOrder();
            } else if (response.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
                mAppSharedPrefs.clearAddresses();
                placeOrder();
            } else {
                loadAddressFailed(response);
            }
        }

    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }

    private void getData(String addressJson) {
        Gson gson = new Gson();
        ListOfAddress mAddressArrayObject = gson.fromJson(addressJson, ListOfAddress.class);
        ArrayList<Address> addressArray = mAddressArrayObject.getAddresses();
        System.out.println("Total names" + mAddressArrayObject.getAddresses());

        for (int i = 0; i < addressArray.size(); i++) {
            /*TODO: temporary try catch block*/
            try {
                if (addressArray.get(i).getDefault() == 1) {
                    addressArray.get(i).setDefault(1);
                }
            } catch (NullPointerException defaultNull) {
                addressArray.get(i).setDefault(0);
            }
        }


        AddressArray addressArrayObject = new AddressArray();
        addressArrayObject.setAddresses(addressArray);
        String jsonAddressArray = new Gson().toJson(addressArrayObject);

        mAppSharedPrefs.setAddresses(jsonAddressArray);
        Logger.v("", "Pavi Address: CartFragment " + mAppSharedPrefs.getAddresses());
    }

    @Override
    public void onDestroy() {

        if (mAddAddressAsync != null) {
            mAddAddressAsync.cancel(true);
        }

        if (mCartAsync != null) {
            mCartAsync.cancel(true);
        }
        if (mCartDeleteItemAsync != null) {
            mCartDeleteItemAsync.cancel(true);
        }
        if (mCouponAsync != null) {
            mCouponAsync.cancel(true);
        }
        if (mCartUpdateItemAsync != null) {
            mCartUpdateItemAsync.cancel(true);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }


        super.onDestroy();
    }

    private void placeOrderClick() {

        if (!mAppSharedPrefs.getLoggedIn()) {
            mAppSharedPrefs.clearLoginFragmentShown();
            ((CartActivity) getActivity()).showLoginFragment();
        } else {
            if (CartAdapter.outOfStockItems == 0)
                loadAddressList();
            else
                showSnackbar("Please delete Out of Stock Items to Proceed", Snackbar.LENGTH_LONG);
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.rippleView:
                placeOrderClick();
                mPlaceOrderClicked = true;
                break;
            case R.id.apply_coupon_ripple_view:
                Utils.hideSoftKeyboard(getActivity(), rippleView);
                applyCoupon();
                break;
            case R.id.retry_button_ripple_container:

                loadCart();

                break;

        }

    }
}

