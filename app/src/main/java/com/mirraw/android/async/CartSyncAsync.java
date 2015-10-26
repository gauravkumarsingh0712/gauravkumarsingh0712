package com.mirraw.android.async;

import com.mirraw.android.database.CartManager;
import com.mirraw.android.models.productDetail.ProductDetail;

import java.util.ArrayList;

/**
 * Created by vihaan on 21/7/15.
 */
public class CartSyncAsync extends CoreAsync<ArrayList<ProductDetail>, Void, Void> {

    private CartManager mCartManager;
    public CartSyncAsync()
    {
        mCartManager = new CartManager();
    }

    @Override
    protected Void doInBackground(ArrayList<ProductDetail>... params) {
        ArrayList<ProductDetail> products = params[0];
        mCartManager.clearTable();
        mCartManager.saveProducts(products);
        return null;
    }


}
