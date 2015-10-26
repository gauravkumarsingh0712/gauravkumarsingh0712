package com.mirraw.android.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.async.CartAsync;
import com.mirraw.android.async.CartSyncAsync;
import com.mirraw.android.async.OrderDetailsAsync;
import com.mirraw.android.models.orders.OrderDetails;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by pavitra on 24/7/15.
 */
public class ThankYouActivity extends AnimationActivity
        implements CartAsync.CartLoader, OrderDetailsAsync.OrderDetailsLoader {

    TextView id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Thank You!");

        LinearLayout order_no_layout = (LinearLayout) findViewById(R.id.order_no_layout);
        TextView woohooTextView = (TextView) findViewById(R.id.woohooTextView);
        TextView order_status = (TextView) findViewById(R.id.order_status);

        if (getIntent().getStringExtra("response") != null) {
            String response = getIntent().getStringExtra("response");
            order_status.setText("Order Placed Successfully");
            woohooTextView.setVisibility(View.VISIBLE);
            order_no_layout.setVisibility(View.VISIBLE);
            id = (TextView) findViewById(R.id.order_no);
            try {
                //order_no_layout.setVisibility(View.GONE);
                JSONObject responseJson = new JSONObject(response);
                id.setText(" "+responseJson.getString("number"));
                //loadOrderDetails();
            } catch (Exception e) {
                woohooTextView.setVisibility(View.GONE);
                order_no_layout.setVisibility(View.GONE);
                e.printStackTrace();
            }
        } else {
            order_status.setText("Order could not be placed");
            woohooTextView.setVisibility(View.GONE);
            order_no_layout.setVisibility(View.GONE);
            getSupportActionBar().setTitle("Order Failed");
            /*TextView thanku = (TextView) findViewById(R.id.thanku);
            thanku.setVisibility(View.GONE);*/
        }

        RippleView continueShoppingRippleView = (RippleView) findViewById(R.id.continue_ripple_view);
        continueShoppingRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                goHomeScreen();
            }
        });

        final RippleView rateTheApp = (RippleView) findViewById(R.id.rate_ripple_view);
        rateTheApp.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                rateTheApp();
            }
        });

        // deleteBoughtItems();
    }

    private void rateTheApp() {
        //Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.mirraw.android&ah=j34bb71YH4YjqZhZjRjH_xR-6_M");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    //Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
                    Uri.parse("https://play.google.com/store/apps/details?id=com.mirraw.android&ah=j34bb71YH4YjqZhZjRjH_xR-6_M")));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goHomeScreen();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        goHomeScreen();
    }

    private void goHomeScreen() {
        Intent intent = new Intent(ThankYouActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private CartAsync mAsync;
    private CartSyncAsync mCartSyncAsync;

    @Override
    protected void onDestroy() {
        if (mAsync != null)
            mAsync.cancel(true);
        if (mCartSyncAsync != null)
            mCartSyncAsync.cancel(true);
        if (mOrderDetailsAsync != null) {
            mOrderDetailsAsync.cancel(true);
        }
        super.onDestroy();

    }

    private void deleteBoughtItems() {
        loadCart();
    }


    @Override
    public void loadCart() {
        mAsync = new CartAsync(this);
        mAsync.executeTask();
    }

    @Override
    public void onEmptyCart(Response response) {

    }

    @Override
    public void onCartLoaded(Response response) {

    }

    OrderDetailsAsync mOrderDetailsAsync;

    @Override
    public void loadOrderDetails() {
        mOrderDetailsAsync = new OrderDetailsAsync(this);
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        String orderId = "";
        String response = "";
        try {

            response = getIntent().getStringExtra("response");
            JSONObject responseJson = new JSONObject(response);

            orderId = responseJson.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = ApiUrls.API_CREATE_ORDER + "/" + orderId;

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(this));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        JSONObject head;

        try {
            head = new JSONObject(sharedPreferencesManager.getLoginResponse()).getJSONObject("mHeaders");
            requestHeaders.put(Headers.ACCESS_TOKEN, head.getJSONArray(Headers.ACCESS_TOKEN).get(0).toString());
            requestHeaders.put(Headers.CLIENT, head.getJSONArray(Headers.CLIENT).get(0).toString());
            requestHeaders.put(Headers.TOKEN_TYPE, head.getJSONArray(Headers.TOKEN_TYPE).get(0).toString());
            requestHeaders.put(Headers.UID, head.getJSONArray(Headers.UID).get(0).toString());
        } catch (Exception e) {

        }

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        mOrderDetailsAsync.executeTask(request);
    }


    @Override
    public void onOrderDetailsLoaded(Response response) {
        OrderDetails orderDetails;
        try {
            Gson gson = new Gson();
            if (response.getResponseCode() == 200) {
                orderDetails = gson.fromJson(response.getBody(), OrderDetails.class);
                id.setText(orderDetails.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //Mint.logExceptionMessage(OrderInformationFragment.class.getSimpleName(), "OrderInformationFragment onOrderDetailsLoaded", e);
        }
    }

    @Override
    public void couldNotLoadOrderDetails(Response response) {

    }

    /*@Override
    public void onCartLoaded(ArrayList<ProductDetail> products) {
        int boughtProductId = -1;
        String productsString = getIntent().getStringExtra("PRODUCT_LIST");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProductDetail>>(){}.getType();
        ArrayList<ProductDetail> boughtProductArray = (ArrayList<ProductDetail>)gson.fromJson(productsString, type);

        //Delete Bought Items From Cart
        for(int i=0; i<boughtProductArray.size(); i++){
            for(int j=0; j<products.size(); j++){
                if(boughtProductArray.get(i).getId().equals(products.get(j).getId())){
                    Toast.makeText(this, "Products Removed: " + products.get(j).getId(), Toast.LENGTH_SHORT).show();
                    products.remove(j);
                    break;
                }
            }
        }

        mCartSyncAsync = new CartSyncAsync();
        mCartSyncAsync.executeTask(products);


    }*/
}
