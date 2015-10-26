package com.mirraw.android.ui.fragments.filters;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.mirraw.android.R;
import com.mirraw.android.Utils.LoginManager;
import com.mirraw.android.Utils.SharedPreferencesManager;
import com.mirraw.android.ui.activities.EmailLoginActivity;
import com.mirraw.android.ui.activities.RegisterActivity;

import org.w3c.dom.Text;

import java.util.Arrays;

/**
 * Created by pavitra on 7/8/15.
 */
public class NetworkStatusDialogFragment extends DialogFragment
        implements
        View.OnClickListener {

    public static final String TAG = NetworkStatusDialogFragment.class.getSimpleName();


    OnRetryListener mCallback;

    // Container Activity must implement this interface
    public interface OnRetryListener {
        public void onUpdateRetry(int id, int quantity);

        public void onRemoveRetry(int id);
    }


    public NetworkStatusDialogFragment() {
        //Empty Constructor required for DialogFragment
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnRetryListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRetryListener");
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.BorderlessDialogAnimated);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog mDialog = super.onCreateDialog(savedInstanceState);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(mDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.windowAnimations = R.style.SlideUpDownDialog;
        mDialog.getWindow().setAttributes(layoutParams);

        return mDialog;
    }


    private int mIdUpdate = -1;
    private int mQuantity = -1;
    private int mIdRemove = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_retry, container, false);
        getDialog().setCanceledOnTouchOutside(true);

        if (getArguments().getString("ACTION").equalsIgnoreCase("REMOVE")) {
            mIdRemove = getArguments().getInt("ID");
        } else if (getArguments().getString("ACTION").equalsIgnoreCase("UPDATE")) {
            mIdUpdate = getArguments().getInt("ID");
            mQuantity = getArguments().getInt("QUANTITY");
        }

        initViews(view);

        return view;
    }

    private void initViews(View view) {
        String status = "No Internet Connection.\nDo you want to Try again?";

        TextView statusTextView = (TextView) view.findViewById(R.id.status_msg);
        statusTextView.setText(status);

        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        Button retryBtn = (Button) view.findViewById(R.id.retryBtn);

        cancelBtn.setOnClickListener(this);
        retryBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelBtn:
                dismiss();
                break;
            case R.id.retryBtn:
                if (getArguments().getString("ACTION").equalsIgnoreCase("REMOVE")) {
                    mCallback.onRemoveRetry(mIdRemove);
                } else if (getArguments().getString("ACTION").equalsIgnoreCase("UPDATE")) {
                    mCallback.onUpdateRetry(mIdUpdate, mQuantity);
                }
                dismiss();
                break;
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
