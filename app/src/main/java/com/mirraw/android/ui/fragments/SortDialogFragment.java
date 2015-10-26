package com.mirraw.android.ui.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.searchResults.List__;
import com.mirraw.android.models.searchResults.Sorts;

import java.util.List;

public class SortDialogFragment extends DialogFragment implements RippleView.OnRippleCompleteListener {


    public interface SortOptionClickListener {
        public void onSortOptionClicked(int id);
    }

    static SortOptionClickListener sSortClickListener;
    String mSortOptions;


    LinearLayout mSortDialogContainer;
    //    View view;
    static SortDialogFragment sortFragment;

    private boolean isApplySort = false;

    private int id;

    public static SortDialogFragment newInstance(String applicableSortOptions, SortOptionClickListener sortClickListener, int position) {
        sSortClickListener = sortClickListener;

        if (sortFragment == null) {
            sortFragment = new SortDialogFragment();
        }
        System.out.println("position222 : " + position);
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("applicableSortOptions", applicableSortOptions);
        args.putInt("position", position);
        if (sortFragment.getArguments() != null) {
            sortFragment.getArguments().clear();
        }
        sortFragment.setArguments(args);

        return sortFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.BorderlessDialogAnimated);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.windowAnimations = R.style.SlideUpDownDialog;
        dialog.getWindow().setAttributes(layoutParams);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_sort, container);
        getDialog().setCanceledOnTouchOutside(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mSortDialogContainer = (LinearLayout) view.findViewById(R.id.sortDialogContainer);

        mSortOptions = getArguments().getString("applicableSortOptions");
        id = getArguments().getInt("position");
        System.out.println("position000 : " + id);
        Gson gson = new Gson();

        Sorts sorts = gson.fromJson(mSortOptions, Sorts.class);
        List<List__> sort_options = sorts.getList();
        addSortOptions(mSortDialogContainer, sort_options);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    TextView sortOptionTextView;
    RelativeLayout sortOptionRl;
    RippleView rippleView;
    int value = -1;

    private void addSortOptions(LinearLayout sortDialogContainer, List<List__> sortOptions) {


        for (int i = 0; i < sortOptions.size(); i++) {
            {
                sortOptionRl = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.list_item_sort, sortDialogContainer, false);
                rippleView = (RippleView) sortOptionRl.findViewById(R.id.rippleView);
                sortOptionTextView = (TextView) sortOptionRl.findViewById(R.id.sortOptionTextView);
                sortOptionTextView.setText(sortOptions.get(i).getName());
                value = i;
                rippleView.setOnRippleCompleteListener(this);

                rippleView.setTag(sortOptions.get(i).getValue());
                if (id - 1 == value) {

                    sortOptionTextView.setTypeface(null, Typeface.BOLD);
                    sortOptionTextView.setTextColor(getActivity().getResources().getColor(R.color.green_color));
                }


                sortDialogContainer.addView(sortOptionRl);
            }

        }
    }


    @Override
    public void onComplete(RippleView rippleView) {

        isApplySort = true;
        int position = Integer.parseInt(rippleView.getTag().toString());

        sSortClickListener.onSortOptionClicked(position);
    }
}
