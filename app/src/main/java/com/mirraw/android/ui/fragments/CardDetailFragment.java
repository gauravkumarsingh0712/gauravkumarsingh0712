package com.mirraw.android.ui.fragments;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.Utils.Utils;
import com.payu.sdk.Cards;
import com.payu.sdk.Constants;
import com.payu.sdk.GetResponseTask;
import com.payu.sdk.Params;
import com.payu.sdk.PayU;
import com.payu.sdk.PaymentListener;
import com.payu.sdk.fragments.ProcessPaymentFragment;

import org.apache.http.NameValuePair;
import org.json.JSONException;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pavitra on 17/7/15.
 */
public class CardDetailFragment extends ProcessPaymentFragment implements PaymentListener {

    private int expiryMonth;
    private int expiryYear;

    private String cardNumber = "";
    private String cvv = "";
    private String nameOnCard = "";
    private String cardName = "";
    private String issuer = "";

    Boolean isExpired = true;
    Boolean isCvvValid = false;
    Boolean isCardNumberValid = false;

    Drawable nameOnCardDrawable;
    Drawable cardNumberDrawable;
    Drawable calenderDrawable;
    Drawable cvvDrawable;
    Drawable cardNameDrawable;
    Drawable issuerDrawable;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        final View cardDetails = inflater.inflate(R.layout.fragment_cards, container, false);

        initHeaderViews(cardDetails);
        // initialize issuer drawable
        Cards.initializeIssuers(getResources());

        if (Constants.ENABLE_VAS && PayU.issuingBankDownBin == null) { // vas has not been called, lets fetch bank down time.
            HashMap<String, String> varList = new HashMap<String, String>();

            varList.put("var1", "default");
            varList.put("var2", "default");
            varList.put("var3", "default");

            List<NameValuePair> postParams = null;

            try {
                postParams = PayU.getInstance(getActivity()).getParams(Constants.GET_VAS, varList);
                GetResponseTask getResponse = new GetResponseTask(CardDetailFragment.this);
                getResponse.execute(postParams);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        ((EditText) cardDetails.findViewById(R.id.expiryDatePickerEditText)).setFocusable(false);

        cardDetails.findViewById(R.id.expiryDatePickerEditText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity(), R.style.ProgressDialog);
                View datePickerLayout = getActivity().getLayoutInflater().inflate(R.layout.date_picker, null);
                dialog.setContentView(datePickerLayout);
                dialog.setCancelable(false);
                Button okButton = (Button) datePickerLayout.findViewById(R.id.datePickerOkButton);
                Button cancelButton = (Button) datePickerLayout.findViewById(R.id.datePickerCancelButton);
                final DatePicker datePicker = (DatePicker) datePickerLayout.findViewById(R.id.datePicker);

                try { // lets hide the day spinner on pre lollipop devices
                    Field datePickerFields[] = datePicker.getClass().getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName()) || "mDaySpinner".equals(datePickerField.getName()) || "mDelegate".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            ((View) datePickerField.get(datePicker)).setVisibility(View.GONE);
                        }
                    }
                } catch (Exception e) {

                }
                dialog.show();
                cancelButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        expiryMonth = datePicker.getMonth() + 1;
                        expiryYear = datePicker.getYear();
                        ((EditText) cardDetails.findViewById(R.id.expiryDatePickerEditText)).setText("" + expiryMonth + " / " + expiryYear);
                        if (expiryYear > Calendar.getInstance().get(Calendar.YEAR)) {
                            isExpired = false;
                            valid(((EditText) getActivity().findViewById(R.id.expiryDatePickerEditText)), calenderDrawable);
                        } else if (expiryYear == Calendar.getInstance().get(Calendar.YEAR) && expiryMonth - 1 >= Calendar.getInstance().get(Calendar.MONTH)) {
                            isExpired = false;
                            valid(((EditText) getActivity().findViewById(R.id.expiryDatePickerEditText)), calenderDrawable);
                        } else {
                            isExpired = true;
                            invalid(((EditText) getActivity().findViewById(R.id.expiryDatePickerEditText)), calenderDrawable);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        /***TODO: Below code is commented to disable stored card, uncomment it to enable stored card feature***/
        /* store card */
        /*if (PayU.userCredentials != null) {
            cardDetails.findViewById(R.id.storeCardCheckBox).setVisibility(View.VISIBLE);
        }*/
        /***TODO: Above code is commented to disable stored card, uncomment it to enable stored card feature***/
        // this comes form stored card fragment
        if (getArguments().getString(PayU.STORE_CARD) != null) {
            TextView backBtnHead = (TextView) cardDetails.findViewById(R.id.payment_options_head);
            backBtnHead.setText("Back to Stored Card");
            cardDetails.findViewById(R.id.storeCardCheckBox).setVisibility(View.VISIBLE);
            ((CheckBox) cardDetails.findViewById(R.id.storeCardCheckBox)).setChecked(true);
            cardDetails.findViewById(R.id.cardNameEditText).setVisibility(View.VISIBLE);
        }

        cardDetails.findViewById(R.id.storeCardCheckBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    getArguments().putString(PayU.STORE_CARD, PayU.STORE_CARD);
                    cardDetails.findViewById(R.id.cardNameEditText).setVisibility(View.VISIBLE);
                } else {
                    getArguments().remove(PayU.STORE_CARD);
                    cardDetails.findViewById(R.id.cardNameEditText).setVisibility(View.GONE);
                }
            }
        });

        cardDetails.findViewById(R.id.makePayment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.hideSoftKeyboard(getActivity(), getView());
                        Params requiredParams = new Params();

                        String nameOnCard = ((TextView) cardDetails.findViewById(R.id.nameOnCardEditText)).getText().toString();
                        if (nameOnCard.length() < 3) {
                            nameOnCard = "PayU " + nameOnCard;
                        }

                        requiredParams.put(PayU.CARD_NUMBER, cardNumber);
                        requiredParams.put(PayU.EXPIRY_MONTH, String.valueOf(expiryMonth));
                        requiredParams.put(PayU.EXPIRY_YEAR, String.valueOf(expiryYear));
                        requiredParams.put(PayU.CARDHOLDER_NAME, nameOnCard);
                        requiredParams.put(PayU.CVV, cvv);

//                getActivity().getIntent().removeExtra("AVAILABLE_PAY_OPTIONS");
                        if (getArguments().getString(PayU.STORE_CARD) != null) { // this comes from the stored card fragment.
                            requiredParams.put("card_name", cardName);
                            requiredParams.put(PayU.STORE_CARD, "1");
                            startPaymentProcessActivity(PayU.PaymentMode.CC, requiredParams);
                        } else if (getArguments().getString("rewardPoint") != null) { // this comes from cash card fragment..citi reward
                            requiredParams.put(PayU.BANKCODE, getArguments().getString("rewardPoint"));
                            startPaymentProcessActivity(PayU.PaymentMode.CASH, requiredParams);
                        } else {
                            getActivity().getIntent().getExtras().remove("AVAILABLE_PAY_OPTIONS");
                            startPaymentProcessActivity(PayU.PaymentMode.CC, requiredParams);
                        }
                    }
                }, 200);

            }
        });


        return cardDetails;
    }

    private void initHeaderViews(View cardDetails) {
        RelativeLayout backBtn = (RelativeLayout) cardDetails.findViewById(R.id.backBtnLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(getActivity(), v);
                getActivity().getSupportFragmentManager().popBackStack();
                //getFragmentManager().popBackStack();
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        nameOnCardDrawable = getResources().getDrawable(com.payu.sdk.R.drawable.user);
        cardNumberDrawable = getResources().getDrawable(com.payu.sdk.R.drawable.card);
        calenderDrawable = getResources().getDrawable(com.payu.sdk.R.drawable.calendar);
        cvvDrawable = getResources().getDrawable(com.payu.sdk.R.drawable.lock);
        cardNameDrawable = getResources().getDrawable(com.payu.sdk.R.drawable.user);

        cardNumberDrawable.setAlpha(100);
        calenderDrawable.setAlpha(100);
        cvvDrawable.setAlpha(100);


        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.nameOnCardEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, nameOnCardDrawable, null);
        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, cardNumberDrawable, null);
        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.expiryDatePickerEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, calenderDrawable, null);
        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, cvvDrawable, null);
        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNameEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, cardNameDrawable, null);


        // lets get the data from bundle
        if (savedInstanceState != null) {
            expiryMonth = savedInstanceState.getInt("ccexpmon");
            expiryYear = savedInstanceState.getInt("ccexpyr");

            if (expiryYear > Calendar.getInstance().get(Calendar.YEAR)) {
                isExpired = false;
                valid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.expiryDatePickerEditText)), calenderDrawable);
            } else if (expiryYear == Calendar.getInstance().get(Calendar.YEAR) && expiryMonth - 1 >= Calendar.getInstance().get(Calendar.MONTH)) {
                isExpired = false;
                valid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.expiryDatePickerEditText)), calenderDrawable);
            } else {
                isExpired = true;
                invalid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.expiryDatePickerEditText)), calenderDrawable);
            }
        }


        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNameEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cardName = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).getText().clear();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cardNumber = charSequence.toString();

                issuer = Cards.getIssuer(cardNumber);

                if (issuer.contentEquals("AMEX")) {
                    ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                } else {
                    ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
                }
                if (issuer != null) {
                    issuerDrawable = Cards.ISSUER_DRAWABLE.get(issuer);
                }

                if (issuer.contentEquals("SMAE")) {
                    // disable cvv and expiry
                    getActivity().findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).setVisibility(View.GONE);
                    getActivity().findViewById(com.payu.sdk.R.id.haveCvvExpiryLinearLayout).setVisibility(View.VISIBLE);
                    getActivity().findViewById(com.payu.sdk.R.id.dontHaveCvvExpiryLinearLayout).setVisibility(View.GONE);
                    if (Cards.validateCardNumber(cardNumber)) {
                        isCardNumberValid = true;
                        if (PayU.issuingBankDownBin != null && PayU.issuingBankDownBin.has(cardNumber.substring(0, 6))) {// oops bank is down.
                            try {
                                ((TextView) getActivity().findViewById(com.payu.sdk.R.id.issuerDownTextView)).setText(PayU.issuingBankDownBin.getString(cardNumber.substring(0, 6)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getActivity().findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.VISIBLE);
                        } else {
                            getActivity().findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.GONE);
                        }
                        /*if (getActivity().getIntent().getExtras().getString(PayU.OFFER_KEY) != null)
                            checkOffer(cardNumber, getActivity().getIntent().getExtras().getString(PayU.OFFER_KEY));*/
                        valid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)), issuerDrawable);
                    } else {
                        isCardNumberValid = false;
                        invalid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)), cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        //resetHeader();
                    }
                } else {
                    // enable cvv and expiry
                    getActivity().findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).setVisibility(View.VISIBLE);
                    getActivity().findViewById(com.payu.sdk.R.id.haveCvvExpiryLinearLayout).setVisibility(View.GONE);
                    getActivity().findViewById(com.payu.sdk.R.id.dontHaveCvvExpiryLinearLayout).setVisibility(View.GONE);
                    if (Cards.validateCardNumber(cardNumber)) {

                        isCardNumberValid = true;

                        if (PayU.issuingBankDownBin != null && PayU.issuingBankDownBin.has(cardNumber.substring(0, 6))) {// oops bank is down.
                            try {
                                ((TextView) getActivity().findViewById(com.payu.sdk.R.id.issuerDownTextView)).setText("We are experiencing high failure rate for " + PayU.issuingBankDownBin.getString(cardNumber.substring(0, 6)) + " cards at this time. We recommend you pay using any other means of payment.");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getActivity().findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.VISIBLE);
                        } else {
                            getActivity().findViewById(com.payu.sdk.R.id.issuerDownTextView).setVisibility(View.GONE);
                        }

                        /*if (getActivity().getIntent().getExtras().getString(PayU.OFFER_KEY) != null)
                            checkOffer(cardNumber, getActivity().getIntent().getExtras().getString(PayU.OFFER_KEY));*/
                        valid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)), issuerDrawable);
                    } else {
                        isCardNumberValid = false;
                        invalid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)), cardNumberDrawable);
                        cardNumberDrawable.setAlpha(100);
                        //resetHeader();
                    }
                }

                // lets set the issuer drawable.

                if (issuer != null && issuerDrawable != null) {
                    ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, issuerDrawable, null);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                charSequence.toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                cvv = charSequence.toString();
                if (Cards.validateCvv(cardNumber, cvv)) {
                    isCvvValid = true;
                    valid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)), cvvDrawable);
                } else {
                    isCvvValid = false;
                    invalid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)), cvvDrawable);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }


        });
        getActivity().findViewById(com.payu.sdk.R.id.haveClickHereTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).setVisibility(View.VISIBLE);
                getActivity().findViewById(com.payu.sdk.R.id.haveCvvExpiryLinearLayout).setVisibility(View.GONE);
                getActivity().findViewById(com.payu.sdk.R.id.dontHaveCvvExpiryLinearLayout).setVisibility(View.VISIBLE);
                isCvvValid = false;
                isExpired = true;
                ((EditText) getActivity().findViewById(com.payu.sdk.R.id.expiryDatePickerEditText)).getText().clear();
                ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).getText().clear();
                invalid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)), cvvDrawable);

            }
        });

        getActivity().findViewById(com.payu.sdk.R.id.dontHaveClickHereTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().findViewById(com.payu.sdk.R.id.expiryCvvLinearLayout).setVisibility(View.GONE);
                getActivity().findViewById(com.payu.sdk.R.id.haveCvvExpiryLinearLayout).setVisibility(View.VISIBLE);
                getActivity().findViewById(com.payu.sdk.R.id.dontHaveCvvExpiryLinearLayout).setVisibility(View.GONE);
                valid(((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)), cvvDrawable);
            }
        });

        getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });
        getActivity().findViewById(com.payu.sdk.R.id.nameOnCardEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });

        getActivity().findViewById(com.payu.sdk.R.id.cvvEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });

        getActivity().findViewById(com.payu.sdk.R.id.expiryDatePickerEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    makeInvalid();
                }
            }
        });

    }


    private void makeInvalid() {
        if (!isCardNumberValid && cardNumber.length() > 0 && !getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText).isFocused())
            ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cardNumberEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(com.payu.sdk.R.drawable.error_icon), null);
        if (!isCvvValid && cvv.length() > 0 && !getActivity().findViewById(com.payu.sdk.R.id.cvvEditText).isFocused())
            ((EditText) getActivity().findViewById(com.payu.sdk.R.id.cvvEditText)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(com.payu.sdk.R.drawable.error_icon), null);
    }


    private void valid(EditText editText, Drawable drawable) {
        drawable.setAlpha(255);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        if (getActivity().findViewById(R.id.expiryCvvLinearLayout).getVisibility() == View.GONE) {
            isExpired = false;
            isCvvValid = true;
        }
        if (isCardNumberValid && !isExpired && isCvvValid) {
            getActivity().findViewById(R.id.makePayment).setEnabled(true);
//            getActivity().findViewById(R.id.makePayment).setBackgroundResource(R.drawable.button_enabled);
        } else {
            getActivity().findViewById(R.id.makePayment).setEnabled(false);
//            getActivity().findViewById(R.id.makePayment).setBackgroundResource(R.drawable.button);
        }
    }

    private void invalid(EditText editText, Drawable drawable) {
        drawable.setAlpha(100);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        getActivity().findViewById(R.id.makePayment).setEnabled(false);
//        getActivity().findViewById(R.id.makePayment).setBackgroundResource(R.drawable.button);
    }

    @Override
    public void onPaymentOptionSelected(PayU.PaymentMode paymentMode) {

    }

    @Override
    public void onGetResponse(String responseMessage) {

    }
}
