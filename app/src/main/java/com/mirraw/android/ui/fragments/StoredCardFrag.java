package com.mirraw.android.ui.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mirraw.android.R;
import com.mirraw.android.adapters.StoredCardAdapter;
import com.payu.sdk.Constants;
import com.payu.sdk.GetResponseTask;
import com.payu.sdk.Params;
import com.payu.sdk.PayU;
import com.payu.sdk.PaymentListener;
import com.payu.sdk.fragments.ProcessPaymentFragment;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pavitra on 20/7/15.
 */
public class StoredCardFrag extends ProcessPaymentFragment implements PaymentListener {

    ProgressDialog mProgressDialog;

    long mLastClickTime = 0;

    public StoredCardFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View storedCardFragment = inflater.inflate(R.layout.fragment_stored_card, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        RelativeLayout backBtn = (RelativeLayout) storedCardFragment.findViewById(R.id.backBtnLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        return storedCardFragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().findViewById(R.id.useNewCardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(PayU.STORE_CARD, "store_card");

                CardDetailFragment cardsFragment = new CardDetailFragment();
                cardsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_left, R.anim.slide_in_from_right, R.anim.slide_out_to_right).replace(R.id.payment_options_layout, cardsFragment, "CardDetailFragment").addToBackStack(null).commit();
            }
        });

        if (PayU.storedCards == null) {
            fetchStoredCards();
        } else {
            setupAdapter();
        }
    }

    private void fetchStoredCards() {

        if (getActivity() != null && !getActivity().isFinishing()) {
            if (mProgressDialog == null)
                mProgressDialog = new ProgressDialog(getActivity());

            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        List<NameValuePair> postParams = null;

        HashMap<String, String> varList = new HashMap<String, String>();
        varList.put(Constants.VAR1, PayU.userCredentials);

        try {
            postParams = PayU.getInstance(getActivity()).getParams(Constants.GET_USER_CARDS, varList);
            GetResponseTask getStoredCards = new GetResponseTask(StoredCardFrag.this);
            getStoredCards.execute(postParams);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void setupAdapter() {

        StoredCardAdapter adapter = new StoredCardAdapter(getActivity(), PayU.storedCards);
        //TextView t = (TextView) getActivity().findViewById(R.id.savedCardTextView);
        //t.setText(""+PayU.storedCards.toString());
        if (getActivity() != null) {
            if (PayU.storedCards.length() < 1) {
                getActivity().findViewById(R.id.noCardFoundTextView).setVisibility(View.VISIBLE);
            } else {
                LinearLayout layout = (LinearLayout) getView().findViewById(R.id.storedListLayout);

                mProgressDialog.dismiss();

                try {
                    for (int i = 0; i < PayU.storedCards.length(); i++) {
                        View child = getActivity().getLayoutInflater().inflate(R.layout.store_card_item, null);
                        final JSONObject jsonObject = (JSONObject) PayU.storedCards.get(i);
                        final TextView t = (TextView) child.findViewById(R.id.cardLabelTextView);
                        t.setText(jsonObject.getString("card_name"));
                        final TextView t2 = (TextView) child.findViewById(R.id.cardNumberTextView);
                        t2.setText(jsonObject.getString("card_no"));
                        t2.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(com.payu.sdk.R.drawable.arrow), null);
                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cardClicked(jsonObject);
                            }
                        });

                        child.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                return cardLongClicked(jsonObject);
                            }
                        });
                        layout.addView(child);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean cardClicked(final JSONObject selectedCard) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) { // to prevent quick double click.
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        final EditText input = new EditText(getActivity());
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(50, 0, 50, 0);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        int cvvLength;
        try {
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(selectedCard.getString("card_no").matches("^3[47]+[0-9|X]*") ? 4 : 3)});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //input.setBackgroundResource(R.drawable.rectangle_box);
        //input.setTextColor(getResources().getColor(android.R.color.black));
        input.setLines(1);
        input.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.lock), null);
        linearLayout.addView(input, layoutParams);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(linearLayout);
        builder.setTitle(Constants.CVV_TITLE);
        builder.setMessage(Constants.CVV_MESSAGE);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                makePayment(selectedCard, input.getText().toString());
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog dialog = builder.create();

        if (!dialog.isShowing())
            dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false); // initially ok button is disabled

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if ((selectedCard.getString("card_no").matches("^3[47]+[0-9|X]*") && input.getText().length() == 4) || (!selectedCard.getString("card_no").matches("^3[47]+[0-9|X]*")) && input.getText().length() == 3) { // ok allow the user to make payment
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    } else {// no dont allow the user to make payment
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return false;
    }

    private boolean cardLongClicked(JSONObject selectedCard) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) { // to prevent double click
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        confirmDelete(selectedCard);

        return false;
    }

    private void makePayment(JSONObject selectedCard, String cvv) {
        Params requiredParams = new Params();
        try {

            requiredParams.put(PayU.CVV, cvv);

            requiredParams.put("store_card_token", selectedCard.getString("card_token"));

            startPaymentProcessActivity(PayU.PaymentMode.valueOf(selectedCard.getString("card_mode")), requiredParams);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void confirmDelete(final JSONObject selectedCard) {
        try {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Delete card")
                    .setCancelable(false)
                    .setMessage("Do you want to delete " + selectedCard.getString("card_no") + " ?")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            deleteCard(selectedCard);
                        }
                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Do nothing.
                }
            }).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteCard(JSONObject selectedCard) {

        mProgressDialog = new ProgressDialog(getActivity());

        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        try {
            List<NameValuePair> postParams = null;
            HashMap<String, String> varList = new HashMap<String, String>();
            // user credentials
            varList.put(Constants.VAR1, PayU.userCredentials);
            //card token
            varList.put(Constants.VAR2, selectedCard.getString("card_token"));
            postParams = PayU.getInstance(getActivity()).getParams(Constants.DELETE_USER_CARD, varList);
            GetResponseTask getStoredCards = new GetResponseTask(StoredCardFrag.this);
            getStoredCards.execute(postParams);
            //fetchStoredCards();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onPaymentOptionSelected(PayU.PaymentMode paymentMode) {

    }

    @Override
    public void onGetResponse(String responseMessage) {
        if (getActivity() != null && getActivity().getBaseContext() != null && !getActivity().isFinishing() && !isRemoving()) {
            if (Constants.DEBUG) {
                Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();
            }

            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (responseMessage.contains("deleted successfully")) {
                Snackbar snackbar=Snackbar.make(getView(), "Card Deleted Successfully", Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
                snackbar.show();

                PayU.storedCards = null;
                //fetchStoredCards();
                getActivity().finish();
            } else if (PayU.storedCards != null) {
                setupAdapter();
            }
        }
    }
}
