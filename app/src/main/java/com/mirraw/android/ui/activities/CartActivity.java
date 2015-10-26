package com.mirraw.android.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

import com.mirraw.android.R;
import com.mirraw.android.ui.fragments.CartFragment;
import com.mirraw.android.ui.fragments.LoginDialogFragment;
import com.mirraw.android.ui.fragments.filters.NetworkStatusDialogFragment;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

//import com.mirraw.android.R;

/**
 * Created by vihaan on 3/7/15.
 */
public class CartActivity extends BaseActivity implements
        NetworkStatusDialogFragment.OnRetryListener {

    public static final String TAG = CartActivity.class.getSimpleName();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onLoginSuccess() {
        if (mCartFragment != null) {
            //((CartFragment) mCartFragment).continueToPayment();
            ((CartFragment) mCartFragment).loadCart();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        initToolbar();
        setTitle(getString(R.string.cart));

        showFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setupActionBarBackButton();

        return true;
    }

    Fragment mCartFragment;

    private void showFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        mCartFragment = new CartFragment();
        ft.replace(R.id.container, mCartFragment);
        ft.commit();
    }

    @Override
    public void onUpdateRetry(int id, int quantity) {
        CartFragment cartFrag = (CartFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);

        if (cartFrag != null) {
            cartFrag.updateQuantity(id, quantity);
        } /*else {
            // Otherwise, we're in the one-pane layout and must swap frags...

            // Create fragment and give it an argument for the selected article
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }*/
    }

    @Override
    public void onRemoveRetry(int id) {
        CartFragment cartFrag = (CartFragment)
                getSupportFragmentManager().findFragmentById(R.id.container);

        if (cartFrag != null) {
            cartFrag.removeItem(id);
        }
    }


    private LoginDialogFragment mLoginDialogFragment;


}
