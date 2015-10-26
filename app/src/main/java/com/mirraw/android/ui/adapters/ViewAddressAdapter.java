package com.mirraw.android.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.DeleteAddressAsync;
import com.mirraw.android.async.ListAddressAsync;
import com.mirraw.android.async.ModifyAddressAsync;
import com.mirraw.android.models.address.Address;
import com.mirraw.android.models.address.AddressArray;
import com.mirraw.android.models.address.ListOfAddress;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.activities.ConfirmAddressActivity;
import com.mirraw.android.ui.activities.EditAddressActivity;
import com.mirraw.android.ui.activities.ViewAddressActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by pavitra on 24/7/15.
 */
public class ViewAddressAdapter extends RecyclerView.Adapter<ViewAddressAdapter.ViewHolder> implements
        DeleteAddressAsync.AddressLoader,
        ListAddressAsync.AddressLoader,
        ModifyAddressAsync.ModifyAddress {
    private ArrayList<Address> mAdresses;
    private Context mContext;
    private Activity mActivity;
    private static int defaultPosition = -1;
    private static int shipPosition = -1;
    private SharedPreferencesManager mAppSharedPrefs;
    private Address address;
    int mPosition;
    long mId;
    //private boolean mFREE_ORDER = false;

    private ProgressDialog mProgressDialog;

    CheckBox mLastClickedCheckbox;

    public ViewAddressAdapter(Context context, Activity activity, ArrayList<Address> addresses, int defaultAddressPosition, int shipAddressPosition) {
        mContext = context;
        mActivity = activity;
        mAdresses = addresses;
        defaultPosition = defaultAddressPosition;
        shipPosition = shipAddressPosition;
        //mFREE_ORDER = freeOrder;
        mAppSharedPrefs = new SharedPreferencesManager(mContext);
        mLastClickedCheckbox = new CheckBox(mContext);
        mLastClickedCheckbox.setChecked(false);
    }

    @Override
    public ViewAddressAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_address, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewAddressAdapter.ViewHolder holder, int position) {
        address = mAdresses.get(position);
        holder.name.setText(address.getName());
        holder.city.setText(address.getCity());
        holder.address.setText(address.getStreetAddress());
        holder.state.setText(address.getState() + " - " + address.getPincode());
        holder.country.setText(address.getCountry());
        holder.mobile.setText(" : " + address.getPhone());
        //holder.editBtn.setBackgroundColor(Color.GRAY);
        if (position >= 0) {
            if (position == defaultPosition) {
                holder.checkDefault.setChecked(true);
                mAdresses.get(position).setDefault(1);
                mLastClickedCheckbox = holder.checkDefault;
            } else {
                holder.checkDefault.setChecked(false);
                mAdresses.get(position).setDefault(0);
            }

            if (position == shipPosition) {
                //holder.shipBtn.setBackgroundColor(Color.parseColor("#7700FF00"));
                mAdresses.get(position).setShipAddressStatus(true);
            } else {
                //holder.shipBtn.setBackgroundColor(Color.LTGRAY);
                mAdresses.get(position).setShipAddressStatus(false);
            }
        }
        saveAddressDataSharedPref();
    }

    @Override
    public int getItemCount() {
        return mAdresses.size();
    }

    private void saveAddressDataSharedPref() {
        AddressArray addressArrayObject = new AddressArray();
        addressArrayObject.setAddresses(mAdresses);
        String jsonAddressArray = new Gson().toJson(addressArrayObject);

        mAppSharedPrefs.setAddresses(jsonAddressArray);
    }

    ModifyAddressAsync mModifyAddressAsync;

    @Override
    public void modifyAddress() {
        mProgressDialog = ProgressDialog.show(mContext, "Please wait..", "Modifying Address.");
        mProgressDialog.setCancelable(false);

        String url = ApiUrls.API_ADD_ADDRESS + "/" + mId;
        HashMap<String, String> body = new HashMap<String, String>();
        Address address = mAdresses.get(defaultPosition);
        body.put("name", address.getName());
        body.put("street_address", address.getStreetAddress());
        body.put("landmark", address.getLandmark());
        body.put("city", address.getCity());
        body.put("state", address.getState());
        body.put("country", address.getCountry());
        body.put("pincode", address.getPincode());
        body.put("phone", address.getPhone());
        // body.put("user_id", String.valueOf(address.getUser_id()));
        body.put("default", address.getDefault().toString());

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(mContext);
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<String, String>();
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.TOKEN, mContext.getString(R.string.token));
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray("Access-Token").get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray("Client").get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray("Token-Type").get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray("Uid").get(0).toString());
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(mContext));

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.PUT).setBody(body).setHeaders(headerMap).build();
            mModifyAddressAsync = new ModifyAddressAsync(this, mActivity);
            mModifyAddressAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onModifyAddressFailed(Response response) {
        Utils.showSnackBar(mActivity.getString(R.string.modify_address_failed), mActivity, Snackbar.LENGTH_LONG);
        loadAddressList();
    }

    @Override
    public void onModifyAddressSuccess(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        saveAddressDataSharedPref();
        loadAddressList();
    }


    private DeleteAddressAsync mDeleteAddressAsync;

    @Override
    public void deletedAddress(int position) {
        mProgressDialog = ProgressDialog.show(mContext, "Please wait..", "Deleting Address.");
        mProgressDialog.setCancelable(false);

        String url = ApiUrls.API_ADD_ADDRESS + "/" + mAdresses.get(position).getId();
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(mContext);
        JSONObject head;// = new JSONObject();
        HashMap<String, String> headerMap = new HashMap<String, String>();
        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.TOKEN, mContext.getString(R.string.token));
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray("Access-Token").get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray("Client").get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray("Token-Type").get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray("Uid").get(0).toString());
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(mContext));

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.DELETE).setHeaders(headerMap).build();
            mDeleteAddressAsync = new DeleteAddressAsync(this);
            mDeleteAddressAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeleteAddressFailed(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (response.getResponseCode() == 0) {
            Utils.showSnackBar(mActivity.getString(R.string.no_internet), mActivity, Snackbar.LENGTH_LONG);
        } else {
            Utils.showSnackBar(mActivity.getString(R.string.delete_address_failed), mActivity, Snackbar.LENGTH_LONG);
        }
    }

    @Override
    public void onDeleteAddressSuccess(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        System.out.println("Respone code : " + response.getResponseCode());

        if (defaultPosition == mPosition) {
            defaultPosition = 0;
        } else if (mPosition < defaultPosition) {
            defaultPosition--;
        }
        mAdresses.get(defaultPosition).setDefault(1);
        if (shipPosition == mPosition) {
            shipPosition = 0;
        } else if (mPosition < shipPosition) {
            shipPosition--;
        }
        mAdresses.get(shipPosition).setShipAddressStatus(true);
        mAdresses.remove(mPosition);
        notifyDataSetChanged();
        if (mAdresses.size() == 0) {
            ((Activity) mContext).finish();
        }
        saveAddressDataSharedPref();

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
        alertDialogBuilder.setTitle("Confirm Delete.");
        alertDialogBuilder
                .setMessage("Click yes to Delete!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletedAddress(mPosition);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private ListAddressAsync mGetAddressAsync;

    @Override
    public void loadAddressList() {
        mProgressDialog = ProgressDialog.show(mContext, "Please Wait..", "Loading addresses");
        mProgressDialog.setCancelable(true);
        String url = ApiUrls.API_ADD_ADDRESS;
        JSONObject head;
        HashMap<String, String> headerMap = new HashMap<>();
        try {
            head = new JSONObject(mAppSharedPrefs.getLoginResponse()).getJSONObject("mHeaders");
            headerMap.put(Headers.TOKEN, mContext.getString(R.string.token));
            headerMap.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            headerMap.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            headerMap.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            headerMap.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
            headerMap.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(mContext));

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET).setHeaders(headerMap).build();
            mGetAddressAsync = new ListAddressAsync(this, mActivity);
            mGetAddressAsync.executeTask(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadAddressFailed(Response response) {
        if (mActivity instanceof ViewAddressActivity) {
            ((ViewAddressActivity) mActivity).onNoInternet();
        }
    }

    @Override
    public void loadAddressSuccess(Response response) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        /*response.setResponseCode(0);
        loadAddressFailed(response);*/

        if (response.getResponseCode() == 200) {
            Gson gson = new Gson();
            String addressJson = response.getBody();
            ListOfAddress mAddressArrayObject = gson.fromJson(addressJson, ListOfAddress.class);
            ArrayList<Address> addressArray = mAddressArrayObject.getAddresses();
            System.out.println("Total names" + mAddressArrayObject.getAddresses());

            for (int i = 0; i < addressArray.size(); i++) {
                if (addressArray.get(i).getDefault() == 1) {
                    defaultPosition = i;
                    break;
                }
            }

            if (defaultPosition == -1) {
                defaultPosition = 0;
                addressArray.get(defaultPosition).setDefault(1);
            }

            for (int i = 0; i < addressArray.size(); i++) {
                if (addressArray.get(i).getShipAddressStatus()) {
                    shipPosition = i;
                    break;
                }
            }

            if (shipPosition == -1) {
                shipPosition = defaultPosition;
            }

            mAdresses = addressArray;
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RippleView.OnRippleCompleteListener {
        TextView name, address, state, country, mobile, city;
        CheckBox checkDefault;
        RippleView editBtn, shipBtn;
        ImageButton removeBtn;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            address = (TextView) view.findViewById(R.id.address);
            city = (TextView) view.findViewById(R.id.city);
            state = (TextView) view.findViewById(R.id.state);
            country = (TextView) view.findViewById(R.id.country);
            mobile = (TextView) view.findViewById(R.id.mobile);
            checkDefault = (CheckBox) view.findViewById(R.id.markDefault);
            removeBtn = (ImageButton) view.findViewById(R.id.removeBtn);
            editBtn = (RippleView) view.findViewById(R.id.editBtn);
            shipBtn = (RippleView) view.findViewById(R.id.shipBtn);
            editBtn.setOnRippleCompleteListener(this);
            removeBtn.setOnClickListener(this);
            shipBtn.setOnRippleCompleteListener(this);
            checkDefault.setOnClickListener(this);
            int position = getPosition();
            /*if(position>=0) {
                if (position == defaultPosition) {
                    checkDefault.setChecked(true);
                    mAdresses.get(position).setDefaultAddressStatus(true);
                    *//*mAdresses.get(position).setShipAddressStatus(true);*//*
                } else {
                    checkDefault.setChecked(false);
                    mAdresses.get(position).setDefaultAddressStatus(false);
                    *//*mAdresses.get(position).setShipAddressStatus(false);*//*
                }
            }
            saveAddressDataSharedPref();*/
        }

        @Override
        public void onClick(View v) {
            mPosition = getPosition();
            switch (v.getId()) {
                case R.id.removeBtn:
                    showDeleteConfirmationDialog();
                    break;
                case R.id.editBtn:
//                    if (Utils.isNetworkAvailable(mContext)) {
//                        saveAddressDataSharedPref();
//                        Address address = mAdresses.get(mPosition);
//                        Intent editAddress = new Intent(mContext, EditAddressActivity.class);
//                        editAddress.putExtra("EDIT_ADDRESS", true);
//                        Logger.v("", "Position: " + mPosition);
//                        editAddress.putExtra("POSITION", mPosition);
//                        editAddress.putExtra("id", address.getId());
//                        mContext.startActivity(editAddress);
//                        //((Activity) mContext).finish();
//                    } else {
//                        Utils.showSnackBar(mActivity.getString(R.string.no_internet), mActivity, Snackbar.LENGTH_LONG);
//                    }
                    break;
                case R.id.shipBtn:
//                    mAdresses.get(shipPosition).setShipAddressStatus(false);
//                    shipPosition = getPosition();
//                    mAdresses.get(shipPosition).setShipAddressStatus(true);
//                    ((Activity) mContext).finish();
//                    shipPosition = position;
//                    mAdresses.get(position).setShipAddressStatus(true);
//                    notifyDataSetChanged();
//                    saveAddressDataSharedPref();
//                    ((Activity) mContext).finish();
//                    mAdresses.get(shipPosition).setShipAddressStatus(false);
//                    shipPosition = getPosition();
//                    mAdresses.get(shipPosition).setShipAddressStatus(true);
//                    saveAddressDataSharedPref();
//                    addAddress();
//                    ((Activity) mContext).finish();

                    break;
                case R.id.markDefault: {
                    /*if (checkDefault.isChecked()) {
                        if (mLastClickedCheckbox.isChecked())
                            mLastClickedCheckbox.setChecked(false);
                    }
                    mLastClickedCheckbox = checkDefault;
                    mAdresses.get(defaultPosition).setDefault(0);
                    mId = mAdresses.get(defaultPosition).getId();
                    saveAddressDataSharedPref();
                    mDefault = 0;
                    modifyAddress();*/
                    defaultPosition = mPosition;
                    mAdresses.get(defaultPosition).setDefault(1);
                    mId = mAdresses.get(defaultPosition).getId();
                    /*saveAddressDataSharedPref();
                    mDefault = 1;*/
                    modifyAddress();

//                        defaultPosition = mPosition;
//                    notifyDataSetChanged();
//                    mAdresses.get(mPosition).setDefault(1);
//                    saveAddressDataSharedPref();
//                    addAddress();
                }
                break;
            }
        }

        @Override
        public void onComplete(RippleView rippleView) {
            mPosition = getPosition();
            switch (rippleView.getId()) {
                case R.id.editBtn:
                    // pavitras new code
                    if (Utils.isNetworkAvailable(mContext)) {
                        saveAddressDataSharedPref();
                        Address address = mAdresses.get(mPosition);
                        Intent editAddress = new Intent(mContext, EditAddressActivity.class);
                        editAddress.putExtra("EDIT_ADDRESS", true);
                        Logger.v("", "Position: " + mPosition);
                        editAddress.putExtra("POSITION", mPosition);
                        editAddress.putExtra("id", address.getId());
                        mContext.startActivity(editAddress);
                        //((Activity) mContext).finish();
                    } else {
                        Utils.showSnackBar(mActivity.getString(R.string.no_internet), mActivity, Snackbar.LENGTH_LONG);
                    }
                    break;

                case R.id.shipBtn:
                    // Pavitra's new code
                    mAdresses.get(shipPosition).setShipAddressStatus(false);
                    shipPosition = getPosition();
                    mAdresses.get(shipPosition).setShipAddressStatus(true);
                    saveAddressDataSharedPref();
                    Intent confirmAddressIntent = new Intent(mContext, ConfirmAddressActivity.class);
                    //confirmAddressIntent.putExtra("FREE_ORDER", mFREE_ORDER);
                    confirmAddressIntent.putExtra("AddressJson", mAppSharedPrefs.getAddresses());
                    mContext.startActivity(confirmAddressIntent);

                    break;
            }


        }
        /*@Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getPosition();
            switch(buttonView.getId()){
                case R.id.markDefault:
                    if (isChecked) {
                        checkedPosition = position;
                    }
                    //notifyDataSetChanged();
                    //saveAddressDataSharedPref();
                    break;
            }
        }*/
    }

    public void onDestroy() {
        if (mDeleteAddressAsync != null) {
            mDeleteAddressAsync.cancel(true);
        }
        if (mGetAddressAsync != null) {
            mGetAddressAsync.cancel(true);
        }
        if (mModifyAddressAsync != null) {
            mModifyAddressAsync.cancel(true);
        }
    }
}
