package com.mirraw.android.ui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mirraw.android.Mirraw;
import com.mirraw.android.R;
import com.mirraw.android.Utils.AddressUtil;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.CreateOrderAsync;
import com.mirraw.android.models.PaymentOptionsDetail.AvailablePaymentOptions;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.activities.ThankYouActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by pavitra on 17/8/15.
 */
public class CODFragment extends Fragment implements
        CreateOrderAsync.CreateOrderLoader {

    private SharedPreferencesManager mAppSharedPrefs;
    private AvailablePaymentOptions mAvailablePaymentOptions;
    private String mCurrencyCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppSharedPrefs = new SharedPreferencesManager(Mirraw.getAppContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.cod_fragment, container, false);

        Bundle bundle = getArguments();
        String availOptionsString = bundle.getString("AVAILABLE_PAY_OPTIONS");
        Logger.v("", "AVAILABLE OPTIONS: " + availOptionsString);
        Gson gson = new Gson();
        mAvailablePaymentOptions = gson.fromJson(availOptionsString, AvailablePaymentOptions.class);

        return initViews(view);
    }

    private View initViews(View v) {
        mCurrencyCode = String.valueOf(Character.toChars((char) Integer.parseInt(mAvailablePaymentOptions.getDetails().getHexSymbol(), 16)));
        initHeaderViews(v);
        addCharge(v);

        int totalQty = 0;
        try {
            totalQty = mAvailablePaymentOptions.getDetails().getItemCount();
        } catch (Exception e) {

        }
        TextView totalItems = (TextView) v.findViewById(R.id.quantity);
        if (totalQty == 1)
            totalItems.setText(totalQty + " Item");
        else
            totalItems.setText(totalQty + " Items");

        v.findViewById(R.id.codPay).setEnabled(true);
        v.findViewById(R.id.codPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createOrder(mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashOnDelivery().getValue());
            }
        });

        getActivity().findViewById(R.id.order_status_head).setVisibility(View.GONE);
        getActivity().findViewById(R.id.order_layout).setVisibility(View.GONE);

        return v;
    }

    private void initHeaderViews(View view) {
        RelativeLayout backBtn = (RelativeLayout) view.findViewById(R.id.backBtnLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(getActivity(), v);
                getFragmentManager().popBackStack();
            }
        });
    }

    private void addCharge(View v) {
        TextView orderAmtView = (TextView) v.findViewById(R.id.total_order_amt);
        TextView shippingAmtView = (TextView) v.findViewById(R.id.shipping_amt);
        TextView discountAmtView = (TextView) v.findViewById(R.id.discount_amt);
        TextView totalAmtView = (TextView) v.findViewById(R.id.total_amt);
        TextView youPay = (TextView) getActivity().findViewById(R.id.total_payment);

        TextView additionalCharge;
        Double addnCharge = 0.0;

        if (mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashOnDelivery().getAdditionalCharge() > 0
                && mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashOnDelivery().getAvailable()) {
            addnCharge = mAvailablePaymentOptions.getDetails().getPaymentOptions().getCashOnDelivery().getAdditionalCharge();
            additionalCharge = (TextView) v.findViewById(R.id.addn_amt);
            if (mAvailablePaymentOptions.getDetails().getHexSymbol().equalsIgnoreCase("20B9")) {
                additionalCharge.setText(mCurrencyCode + addnCharge.intValue());
            } else {
                additionalCharge.setText(mCurrencyCode + addnCharge);
            }
        }
        Double total = mAvailablePaymentOptions.getDetails().getItemTotal()
                + mAvailablePaymentOptions.getDetails().getShippingTotal()
                - mAvailablePaymentOptions.getDetails().getDiscount()
                + addnCharge;

        if (mAvailablePaymentOptions.getDetails().getHexSymbol().equalsIgnoreCase("20B9")) {
            orderAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getItemTotal().intValue());
            shippingAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getShippingTotal().intValue());
            discountAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getDiscount().intValue());
            totalAmtView.setText(mCurrencyCode + total.intValue());
            youPay.setText(mCurrencyCode + total.intValue());
        } else {
            orderAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getItemTotal());
            shippingAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getShippingTotal());
            discountAmtView.setText(mCurrencyCode + mAvailablePaymentOptions.getDetails().getDiscount());
            totalAmtView.setText(mCurrencyCode + total);
            youPay.setText(mCurrencyCode + total);
        }
    }

    CreateOrderAsync mCreateOrderAsync;
    ProgressDialog mProgressDialog;

    @Override
    public void createOrder(String pay_type) {
        mProgressDialog = ProgressDialog.show(getActivity(), getString(R.string.please_wait), getString(R.string.creating_cod), true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mCreateOrderAsync != null) {
                    mCreateOrderAsync.cancel(true);
                }
            }
        });
        int shipping_id = AddressUtil.getShipping_id();
        int billing_id = AddressUtil.getBilling_id();

        if (shipping_id == -1)
            shipping_id = billing_id;

        Logger.v("", "Shipping Id: " + shipping_id);
        Logger.v("", "Billing Id: " + billing_id);

        String url = ApiUrls.API_CREATE_ORDER;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();

        try {
            headerMap.put(Headers.TOKEN, getString(R.string.token));
            Logger.v("", "Token: " + getString(R.string.token));
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            Logger.v("", "Device-ID: " + NetworkUtil.getDeviceId(getActivity()));
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            Logger.v("", "Access-Token: " + head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            Logger.v("", "Client: " + head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            Logger.v("", "Token-Type: " + head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            Logger.v("", "Uid: " + head.getJSONArray(Headers.UID).get(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject body = null;
        try {
            body = new JSONObject();
            JSONObject order = new JSONObject();
            order.put("shipping_address", String.valueOf(shipping_id));
            order.put("billing_address", String.valueOf(billing_id));
            order.put("pay_type", pay_type);

            body.put("order", order);

            Logger.v("ORDERS", "ORDERS: " + body.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.POST).setHeaders(headerMap).setBodyJson(body).build();
        mCreateOrderAsync = new CreateOrderAsync(this);
        mCreateOrderAsync.executeTask(request);
    }


    @Override
    public void createOrderSuccessful(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            Intent thankYou = new Intent(getActivity(), ThankYouActivity.class);
            thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (response.getResponseCode() == 200) {
                thankYou.putExtra("response", response.getBody());
                Toast.makeText(getActivity(), "Order Created Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Some Error Ocurred", Toast.LENGTH_SHORT).show();
            }
            startActivity(thankYou);
            getActivity().finish();
        }

    }

    @Override
    public void createOrderFailed(Response response) {
        if (isAdded()) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (response.getResponseCode() == 0) {
                showSnackBar("No Internet Connection");
            } else {
                Toast.makeText(getActivity(), "Some Error Ocurred", Toast.LENGTH_SHORT).show();
                Intent thankYou = new Intent(getActivity(), ThankYouActivity.class);
                thankYou.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(thankYou);
                getActivity().finish();
            }
        }

    }

    private void showSnackBar(String msg) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        if (mCreateOrderAsync != null) {
            mCreateOrderAsync.cancel(true);
        }
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        super.onDestroy();
    }
}
