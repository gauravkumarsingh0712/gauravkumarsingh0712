package com.mirraw.android.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mirraw.android.R;
import com.mirraw.android.ui.activities.HomeActivity;

/**
 * Created by Nimesh Luhana on 31-07-2015.
 */
public class PasswordResetMailFragment extends android.support.v4.app.Fragment implements View.OnClickListener {


    String mMailId;
    TextView mResetMessage;
    Button mContinueShoppingButton;

    public static PasswordResetMailFragment newInstance(String mailId) {

        Bundle bundle = new Bundle();
        bundle.putString("mailId", mailId);
        PasswordResetMailFragment passwordResetMailFragment = new PasswordResetMailFragment();
        passwordResetMailFragment.setArguments(bundle);
        return passwordResetMailFragment;


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.fragment_password_reset_mail, container, false);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMailId = getArguments().getString("mailId");
        initViews(view);


    }

    private void initViews(View view) {
        initResetInstructionText(view);
        initContinueShopping(view);
    }

    private void initResetInstructionText(View view) {
        mResetMessage = (TextView) view.findViewById(R.id.resetMessage);
        mResetMessage.setText("We have sent a mail to " + mMailId + ".It includes the password reset instructions.");

    }

    private void initContinueShopping(View view) {
        mContinueShoppingButton = (Button) view.findViewById(R.id.continueShoppingBtn);
        mContinueShoppingButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.continueShoppingBtn:


                goHomeScreen();

                break;


        }

    }

    private void goHomeScreen() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        getActivity().finish();
    }
}
