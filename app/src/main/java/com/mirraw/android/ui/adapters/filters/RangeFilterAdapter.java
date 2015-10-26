package com.mirraw.android.ui.adapters.filters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.models.searchResults.List;

/**
 * Created by vihaan on 20/7/15.
 */
public class RangeFilterAdapter extends RecyclerView.Adapter<RangeFilterAdapter.ViewHolder> {

    private RangeFilterListener mRangeFilterListener;
    private java.util.List<List> mList;

    public int mSelectedItem = -1;
    Context context;
    private String mHexValue;
    private String mFilterName;


    public RangeFilterAdapter(RangeFilterListener listener, java.util.List<List> list, Context context, String hexValue, String filterName) {
        mRangeFilterListener = listener;
        mList = list;
        mFilterName = filterName;
        this.context = context;
        mHexValue = hexValue;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_range_filter, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        List list = mList.get(position);
        if (mSelectedItem == -1) {
            if (list.getSelected()) {
                holder.rangeFilterRadioButton.setChecked(true);

                // holder.rangeFilterRadioButton.setButtonTintList(context.getResources().getColorStateList(R.color.green_color));
                mRangeFilterListener.onRangeFilterClicked(holder.getView(), position);
                mSelectedItem = position;
            } else {
                holder.rangeFilterRadioButton.setChecked(false);
                list.setSelected(false);
                // holder.rangeFilterRadioButton.setButtonTintList(context.getResources().getColorStateList(R.color.white));
            }
        } else {
            if (position == mSelectedItem) {
                //holder.rangeFilterRadioButton.setButtonTintList(context.getResources().getColorStateList(R.color.green_color));
                holder.rangeFilterRadioButton.setChecked(true);
                mRangeFilterListener.onRangeFilterClicked(holder.getView(), position);
                list.setSelected(true);
            } else {
                holder.rangeFilterRadioButton.setChecked(false);
                list.setSelected(false);
                //holder.rangeFilterRadioButton.setButtonTintList(context.getResources().getColorStateList(R.color.white));
            }
        }
        String currencySymbol = String.valueOf(Character.toChars((char) Integer.parseInt(mHexValue, 16)));
        String discountPercent = list.getName().replace("-", " to  ");
        String price = list.getName().replace("-", " to  " + currencySymbol);
        String count = String.valueOf(mList.get(position).getCount());
        if (mFilterName.equalsIgnoreCase("Price")) {
            holder.rangeFilterTextView.setText(currencySymbol + " " + price + " (" + count + ")");

        } else {
            holder.rangeFilterTextView.setText(discountPercent + " (" + count + ")");
        }


    }

    public void resetSelectedItem() {
        mSelectedItem = -1;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public interface RangeFilterListener {
        public void onRangeFilterClicked(View view, int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements RippleView.OnRippleCompleteListener {
        public TextView rangeFilterTextView;
        public RadioButton rangeFilterRadioButton;
        public LinearLayout rangeFilterLayout;
        public RippleView rangeFilterRipple;

        private View mView;

        public ViewHolder(View view) {
            super(view);

            rangeFilterRipple = (RippleView) view.findViewById(R.id.rangeFilterRipple);
            rangeFilterRadioButton = (RadioButton) view.findViewById(R.id.rangeFilterRadioButton);
            rangeFilterTextView = (TextView) view.findViewById(R.id.rangeFilterTextView);
            rangeFilterRipple.setOnRippleCompleteListener(this);
            mView = view;
        }

        public View getView() {
            return mView;
        }


        @Override
        public void onComplete(RippleView rippleView) {
            mSelectedItem = getPosition();
            mRangeFilterListener.onRangeFilterClicked(rippleView, mSelectedItem);
            notifyDataSetChanged();
        }
    }
}
