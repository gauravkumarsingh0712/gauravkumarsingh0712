package com.mirraw.android.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mirraw.android.R;
import com.mirraw.android.Utils.Utils;
import com.payu.sdk.Constants;
import com.payu.sdk.GetResponseTask;
import com.payu.sdk.Params;
import com.payu.sdk.PayU;
import com.payu.sdk.PaymentListener;
import com.payu.sdk.adapters.NetBankingAdapter;
import com.payu.sdk.fragments.ProcessPaymentFragment;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pavitra on 22/7/15.
 */
public class NBFragment extends ProcessPaymentFragment implements PaymentListener {

    String bankCode = "";
    ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View netBankingFragment = inflater.inflate(R.layout.fragment_net_banking, container, false);

        initHeaderViews(netBankingFragment);

        mProgressDialog = new ProgressDialog(getActivity());

        netBankingFragment.findViewById(R.id.nbPayButton);
        netBankingFragment.findViewById(R.id.nbPayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Params requiredParams = new Params();
                        requiredParams.put(PayU.BANKCODE, bankCode);
//                getActivity().getIntent().removeExtra("AVAILABLE_PAY_OPTIONS");
                        startPaymentProcessActivity(PayU.PaymentMode.NB, requiredParams);
                    }
                },200);

            }
        });

        return netBankingFragment;
    }

    private void initHeaderViews(View netBankingFragment) {
        RelativeLayout backBtn = (RelativeLayout) netBankingFragment.findViewById(R.id.backBtnLayout);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(getActivity(), v);
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // fetch the data once again if last fetched at is less than 15 min
        if ((System.currentTimeMillis() - PayU.dataFetchedAt) / (60000) < 15) {
            // dont fetch
            setupAdapter();
        } else {
            // fetch
            mProgressDialog.setMessage(getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            List<NameValuePair> postParams = null;

            HashMap varList = new HashMap();

            if (PayU.userCredentials == null) {// ok we dont have a user credentials.
                varList.put(Constants.VAR1, Constants.DEFAULT);
            } else {
                varList.put(Constants.VAR1, PayU.userCredentials);
            }

            try {
                postParams = PayU.getInstance(getActivity()).getParams(Constants.PAYMENT_RELATED_DETAILS, varList);
                GetResponseTask getResponse = new GetResponseTask(NBFragment.this);
                getResponse.execute(postParams);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }


        if (Constants.ENABLE_VAS && PayU.netBankingStatus == null) {
            HashMap<String, String> varList = new HashMap<String, String>();

            varList.put("var1", "default");
            varList.put("var2", "default");
            varList.put("var3", "default");

            List<NameValuePair> postParams = null;

            try {
                postParams = PayU.getInstance(getActivity()).getParams(Constants.GET_VAS, varList);
                GetResponseTask getResponse = new GetResponseTask(NBFragment.this);
                getResponse.execute(postParams);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }


    private void setupAdapter() {
        if (getActivity() != null && !getActivity().isFinishing()) {

            NetBankingAdapter adapter = new NetBankingAdapter(getActivity(), PayU.availableBanks);

            Spinner netBankingSpinner = (Spinner) getActivity().findViewById(R.id.netBankingSpinner);
            netBankingSpinner.setAdapter(adapter);

            netBankingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    try {
                        bankCode = ((JSONObject) adapterView.getAdapter().getItem(i)).getString("code");


                        if (bankCode.contentEquals("default")) {
                            //disable the button
//                        getActivity().findViewById(R.id.nbPayButton).setBackgroundResource(R.drawable.button);
                            getActivity().findViewById(R.id.nbPayButton).setEnabled(false);
                        } else {
                            //enable the button
//                        getActivity().findViewById(R.id.nbPayButton).setBackgroundResource(R.drawable.button_enabled);
                            if (PayU.netBankingStatus != null && PayU.netBankingStatus.get(bankCode) == 0) {
                                ((TextView) getActivity().findViewById(R.id.netBankingErrorText)).setText("Oops! " + ((JSONObject) adapterView.getAdapter().getItem(i)).getString("title") + " seems to be down. We recommend you pay using any other means of payment.");
                                getActivity().findViewById(R.id.netBankingErrorText).setVisibility(View.VISIBLE);
                            } else {
                                getActivity().findViewById(R.id.netBankingErrorText).setVisibility(View.GONE);
                            }
                            getActivity().findViewById(R.id.nbPayButton).setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    }


    @Override
    public void onPaymentOptionSelected(PayU.PaymentMode paymentMode) {

    }

    @Override
    public void onGetResponse(String responseMessage) {
        if (getActivity() != null && !getActivity().isFinishing()) {
            // setup adapter
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            if (PayU.availableBanks != null)
                setupAdapter();
            if (Constants.DEBUG)
                Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
