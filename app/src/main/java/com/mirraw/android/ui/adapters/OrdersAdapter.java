package com.mirraw.android.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.orders.Orders;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by varun on 19/8/15.
 */
public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RippleView.OnRippleCompleteListener {


    @Override
    public void onComplete(RippleView rippleView) {
        int id = rippleView.getId();
        switch (id) {
            case R.id.retry_button_ripple_container:
                sOrdersClickListener.onRetryButtonClickedBottom();
                break;
        }
    }

    public interface OrdersClickListener {
        public void onOrdersClicked(View v, int position);

        public void onRetryButtonClickedBottom();
    }

    Orders mOrders;
    private static OrdersClickListener sOrdersClickListener;

    public OrdersAdapter(Orders orders, OrdersClickListener ordersClickListener) {
        this.mOrders = orders;
        this.sOrdersClickListener = ordersClickListener;
    }

    RippleView retryButtonRippleView;
    ProgressWheel mProgressWheelBottom;
    LinearLayout mNoInternetBottom;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPES.Normal:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_number, parent, false);
                viewHolder = new OrdersViewHolder(view);
                break;
            case VIEW_TYPES.Footer:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_no_internet, parent, false);

                /*mNoInternetBottom = (LinearLayout) view.findViewById(R.id.noInternetLL);
                mNoInternetBottom.setVisibility(View.GONE);
                mProgressWheelBottom = (ProgressWheel) view.findViewById(R.id.progressWheel);
                btnRetryBottom = (Button) view.findViewById(R.id.retryButton);
                //btnRetryBottom.setVisibility(View.GONE);
                btnRetryBottom.setOnClickListener(this);*/

                viewHolder = new ProgressViewHolder(view);
                break;
            /*default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_number, parent, false);
                viewHolder = new OrdersViewHolder(view);
                break;*/
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof OrdersViewHolder) {
                if (position <= mOrders.getOrders().size() - 1) {
                    String strOrderNumber = mOrders.getOrders().get(position).getNumber();
                    String strPaymentType = mOrders.getOrders().get(position).getPayType();
                    String strPayentState = mOrders.getOrders().get(position).getPaymentState();
                    String strCreated = mOrders.getOrders().get(position).getCreatedAt();
                    String strTotal = mOrders.getOrders().get(position).getTotal().toString();

                    String strSymbol = String.valueOf(Character.toChars((char) Integer.parseInt(mOrders.getOrders().get(position).getHexSymbol(), 16)));

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
                    Date date = format.parse(strCreated);

                    String outputDate = outputFormat.format(date);

                    ((OrdersViewHolder) holder).mOrderNumber.setText("Order number: " + strOrderNumber);
                    ((OrdersViewHolder) holder).mPaymentType.setText("Payment type: " + strPaymentType);
                    ((OrdersViewHolder) holder).mPaymentState.setText("Payment state: " + strPayentState);
                    ((OrdersViewHolder) holder).mCreated.setText("Created on: " + outputDate);
                    ((OrdersViewHolder) holder).mTotal.setText("Order total: " + strSymbol + " " + strTotal);
                }
            } else if (holder instanceof ProgressViewHolder) {
                retryButtonRippleView = ((ProgressViewHolder) holder).retryButtonRippleView;
                mNoInternetBottom = ((ProgressViewHolder) holder).mNoInterNetLL;
                mNoInternetBottom.setVisibility(View.GONE);
                mProgressWheelBottom = ((ProgressViewHolder) holder).progressWheelBottom;
                retryButtonRippleView.setOnRippleCompleteListener(this);
            }


        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            Mint.logExceptionMessage(TAG, gson.toJson(mOrders).toString(), e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position >= mOrders.getOrders().size() ? VIEW_TYPES.Footer : VIEW_TYPES.Normal;
    }


    public void hideProgress() {
        if (mProgressWheelBottom != null && mNoInternetBottom != null) {
            mProgressWheelBottom.setVisibility(View.GONE);
            mNoInternetBottom.setVisibility(View.VISIBLE);
        }
    }

    public void showProgress() {
        if (mProgressWheelBottom != null && mNoInternetBottom != null) {
            mProgressWheelBottom.setVisibility(View.VISIBLE);
            mNoInternetBottom.setVisibility(View.GONE);
        }
    }

    public void lastPage() {
        mAllPageShown = true;
        notifyDataSetChanged();
        if (mProgressWheelBottom != null && mNoInternetBottom != null) {
            mProgressWheelBottom.setVisibility(View.GONE);
            mNoInternetBottom.setVisibility(View.GONE);
        }
    }

    String TAG = OrdersAdapter.class.getSimpleName();

    Boolean mAllPageShown = false;

    @Override
    public int getItemCount() {
        if (!mAllPageShown) {
            return mOrders.getOrders().size() + 1;
        } else {
            return mOrders.getOrders().size();
        }

    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RippleView.OnRippleCompleteListener {

        public TextView mOrderNumber, mPaymentType, mPaymentState, mCreated, mTotal;
        RippleView rippleView;

        public OrdersViewHolder(View itemView) {
            super(itemView);
            mOrderNumber = (TextView) itemView.findViewById(R.id.txtOrderNumber);
            mPaymentType = (TextView) itemView.findViewById(R.id.txtPaymentType);
            mPaymentState = (TextView) itemView.findViewById(R.id.txtPaymentState);
            mCreated = (TextView) itemView.findViewById(R.id.txtCreated);
            mTotal = (TextView) itemView.findViewById(R.id.txtTotal);
            rippleView = (RippleView) itemView.findViewById(R.id.rippleView);
            rippleView.setOnRippleCompleteListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sOrdersClickListener.onOrdersClicked(v, getPosition());
        }

        @Override
        public void onComplete(RippleView rippleView) {
            sOrdersClickListener.onOrdersClicked(rippleView, getPosition());
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder{

        public ProgressWheel progressWheelBottom;
        public RippleView retryButtonRippleView;
        public LinearLayout mNoInterNetLL;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressWheelBottom = (ProgressWheel) itemView.findViewById(R.id.progressWheelBottom);
            retryButtonRippleView = (RippleView) itemView.findViewById(R.id.retry_button_ripple_container);
            mNoInterNetLL = (LinearLayout) itemView.findViewById(R.id.noInternetLL);
        }

    }

    private class VIEW_TYPES {
        public static final int Normal = 2;
        public static final int Footer = 3;
    }
}
