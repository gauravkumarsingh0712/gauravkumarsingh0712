package com.mirraw.android.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.sharedresources.Logger;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.models.orders.DesignerOrder;
import com.mirraw.android.models.orders.OrderDetails;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.splunk.mint.Mint;

/**
 * Created by varun on 21/8/15.
 */
public class OrderInformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OrderInformationClickListener {
        public void onOrderInformationClicked(View v, int position);
    }

    private static OrderInformationClickListener sOrderInformationClickListener;

    OrderDetails mOrderDetails;
    String TAG = OrderInformationAdapter.class.getSimpleName();

    public OrderInformationAdapter(OrderDetails orderDetails, OrderInformationClickListener orderInformationClickListener) {
        this.mOrderDetails = orderDetails;
        this.sOrderInformationClickListener = orderInformationClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPES.Header:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_information_header, parent, false);
                //setHeader(view);
                viewHolder = new OrderInformationHeaderViewHolder(view);
                break;
            case VIEW_TYPES.Normal:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_information, parent, false);
                viewHolder = new OrderInformationViewHolder(view);
                break;
            /*default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_information, parent, false);
                break;*/
        }


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (position == 0) {
                setHeader(holder);
                return;
            }

            setOrderDetails(holder, position - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setOrderDetails(final RecyclerView.ViewHolder holder, int position) {
        DesignerOrder designerOrder = mOrderDetails.getDesignerOrders().get(position);
        String strSymbol = String.valueOf(Character.toChars((char) Integer.parseInt(mOrderDetails.getHexSymbol(), 16)));
        int lineItemSize = designerOrder.getLineItems().size();

        String strDesignerName = "";
        if (designerOrder.getLineItems().get(0).getDesignerName() != null) {
            strDesignerName = designerOrder.getLineItems().get(0).getDesignerName();
        }

        ((OrderInformationViewHolder) holder).mDesignerName.setText(strDesignerName);

        String strState = "";
        if (designerOrder.getState() != null) {
            strState = designerOrder.getState();
        }

        String strCourier = "";
        if (designerOrder.getCourierCompany() != null) {
            strCourier = designerOrder.getCourierCompany();
        }

        String strTrackingNumber = "";
        if (designerOrder.getTrackingNumber() != null) {
            strTrackingNumber = designerOrder.getTrackingNumber();
        }


        if (!strState.equalsIgnoreCase("")) {
            ((OrderInformationViewHolder) holder).mStateDesigner.setText("State: " + strState);
        } else {
            ((OrderInformationViewHolder) holder).mStateDesigner.setVisibility(View.GONE);
        }

        if (!strCourier.equalsIgnoreCase("")) {
            ((OrderInformationViewHolder) holder).mCourierDesigner.setText("Courier company: " + strCourier);
        } else {
            ((OrderInformationViewHolder) holder).mCourierDesigner.setVisibility(View.GONE);
        }

        if (!strTrackingNumber.equalsIgnoreCase("")) {
            ((OrderInformationViewHolder) holder).mTrackingNumberDesigner.setText("Tracking number: " + strTrackingNumber);
        } else {
            ((OrderInformationViewHolder) holder).mTrackingNumberDesigner.setVisibility(View.GONE);
        }

        LinearLayout ll = ((OrderInformationViewHolder) holder).mImagesLL;
        ll.removeAllViews();
        View view;
        for (int i = 0; i < lineItemSize; i++) {

            view = LayoutInflater.from(ll.getContext()).inflate(R.layout.hsv_item_order_image, ll, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.productImageView);
            TextView txtProductName = (TextView) view.findViewById(R.id.txtProductName);
            TextView txtProductPrice = (TextView) view.findViewById(R.id.txtProductPrice);

            String strProductName = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getTitle();
            String strProductPrice = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getPrice().toString();
            String imageUrl = getImageUrl(position, i);//mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getSizes().getLarge();

            Context context = imageView.getContext();

            txtProductName.setText(strProductName);
            txtProductPrice.setText(strSymbol + " " + strProductPrice);

            /*Picasso.with(context).load("https://" + imageUrl)
                    .placeholder(R.mipmap.company_logo)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(imageView);*/

            ImageLoader.getInstance()
                    .displayImage(Utils.addHttpSchemeIfMissing(imageUrl), imageView, UILUtils.getImageOptionsSmall(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });

            ll.addView(view);

            /*ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sOrderInformationClickListener.onOrderInformationClicked(v, holder.getPosition());
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sOrderInformationClickListener.onOrderInformationClicked(v, holder.getPosition());
                }
            });*/

            ((OrderInformationViewHolder) holder).rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                @Override
                public void onComplete(RippleView rippleView) {
                    sOrderInformationClickListener.onOrderInformationClicked(rippleView, holder.getPosition());
                }
            });
        }
    }

    private String getImageUrl(int position, int i) {
        String strImageUrl = "";

        /*DensityUtils densityUtils = new DensityUtils();
        int density = densityUtils.getDensity();

        switch (density) {
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_HIGH:
                strImageUrl = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getSizes().getSmall();
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_XXHIGH:
                strImageUrl = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getSizes().getLarge();
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                strImageUrl = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getSizes().getOriginal();
                break;
            default:
                strImageUrl = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getSizes().getLarge();
                break;
        }*/

        String temp = "";
        try {
            strImageUrl = mOrderDetails.getDesignerOrders().get(position).getLineItems().get(i).getSizes().getSmallM();
            Logger.i(TAG, strImageUrl);
            temp = strImageUrl.replace(".jpg", "_m.jpg");
            Logger.i(TAG, temp);
        } catch (Exception e) {
            DesignerOrder designerOrder = mOrderDetails.getDesignerOrders().get(position);
            Gson gson = new Gson();
            String message = gson.toJson(designerOrder);
            Mint.logExceptionMessage(TAG, message, e);
        }


        return strImageUrl;
    }

    private void setHeader(RecyclerView.ViewHolder holder) {

        TextView mState, mCourier, mTrackingNumber, mItemTotal, mDiscount, mShipping, mCod, mTotal;

        String strSymbol = String.valueOf(Character.toChars((char) Integer.parseInt(mOrderDetails.getHexSymbol(), 16)));

        if (holder instanceof OrderInformationHeaderViewHolder) {
            mState = ((OrderInformationHeaderViewHolder) holder).mState;
            mCourier = ((OrderInformationHeaderViewHolder) holder).mCourier;
            mTrackingNumber = ((OrderInformationHeaderViewHolder) holder).mTrackingNumber;
            mItemTotal = ((OrderInformationHeaderViewHolder) holder).mItemTotal;
            mDiscount = ((OrderInformationHeaderViewHolder) holder).mDiscount;
            mShipping = ((OrderInformationHeaderViewHolder) holder).mShipping;
            mCod = ((OrderInformationHeaderViewHolder) holder).mCod;
            mTotal = ((OrderInformationHeaderViewHolder) holder).mTotal;

            String strState = "";
            if (mOrderDetails.getState() != null) {
                strState = mOrderDetails.getState();
            }

            String strCourier = "";
            if (mOrderDetails.getCourierCompany() != null) {
                strCourier = mOrderDetails.getCourierCompany();
            }

            String strTrackingNumber = "";
            if ((mOrderDetails.getTrackingNumber() != null)) {
                strTrackingNumber = mOrderDetails.getTrackingNumber();
            }

            String strItemTotal = "";
            if (String.valueOf(mOrderDetails.getItemTotal()) != null) {
                strItemTotal = String.valueOf(mOrderDetails.getItemTotal());
            }


            String strDiscount = "";
            if (mOrderDetails.getDiscounts() != null && !mOrderDetails.getDiscounts().equalsIgnoreCase("0.0")) {
                strDiscount = mOrderDetails.getDiscounts().toString();
            }

            String strShipping = "";
            if (String.valueOf(mOrderDetails.getShipping()) != null && !mOrderDetails.getShipping().equalsIgnoreCase("0.0")) {
                strShipping = mOrderDetails.getShipping().toString();
            }

            String strCod = "";
            if (String.valueOf(mOrderDetails.getCod()) != null && !mOrderDetails.getCod().equalsIgnoreCase("0.0")) {
                strCod = mOrderDetails.getCod().toString();
            }

            String strTotal = "";
            if (mOrderDetails.getTotal() != null) {
                strTotal = mOrderDetails.getTotal().toString();
            }


            if (!strState.equalsIgnoreCase("")) {
                mState.setText("State: " + strState);
            } else {
                mState.setVisibility(View.GONE);
            }

            if (!strCourier.equalsIgnoreCase("")) {
                mCourier.setText("Courier company: " + strCourier);
            } else {
                mCourier.setVisibility(View.GONE);
            }

            if (!strTrackingNumber.equalsIgnoreCase("")) {
                mTrackingNumber.setText("Tracking number: " + strTrackingNumber);
            } else {
                mTrackingNumber.setVisibility(View.GONE);
            }

            //String[] strItemTotalAmount = strItemTotal.split(" ");
            if (!strItemTotal.equalsIgnoreCase("")) { // && !strItemTotalAmount[1].equalsIgnoreCase("0")) {
                mItemTotal.setText("Item total: " + strSymbol + " " + strItemTotal);
            } else {
                mItemTotal.setVisibility(View.GONE);
            }

            //String[] strDiscountAmount = strDiscount.split(" ");
            if (!strDiscount.equalsIgnoreCase("")) {// && !strDiscountAmount[1].equalsIgnoreCase("0")) {
                mDiscount.setText("Discounts: " + strSymbol + " " + strDiscount + "");
            } else {
                mDiscount.setVisibility(View.GONE);
            }

            //String[] strShippingAmount = strShipping.split(" ");
            if (!strShipping.equalsIgnoreCase("")) { // && !strShippingAmount[1].equalsIgnoreCase("0")) {
                mShipping.setText("Shipping charges: " + strSymbol + " " + strShipping);
            } else {
                mShipping.setVisibility(View.GONE);
            }

            //String[] strCodAmount = strCod.split(" ");
            if (!strCod.equalsIgnoreCase("")) { // && !strCodAmount[1].equalsIgnoreCase("0")) {
                mCod.setText("COD charges: " + strSymbol + " " + strCod);
            } else {
                mCod.setVisibility(View.GONE);
            }

            if (!strTotal.equalsIgnoreCase("")) {
                mTotal.setText("Total: " + strSymbol + " " + strTotal);
            } else {
                mTotal.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPES.Header : VIEW_TYPES.Normal;
    }

    @Override
    public int getItemCount() {
        return mOrderDetails.getDesignerOrders().size() + 1;
    }

    public static class OrderInformationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            RippleView.OnRippleCompleteListener {

        TextView mDesignerName, mStateDesigner, mCourierDesigner, mTrackingNumberDesigner;
        HorizontalScrollView mScrlImages;
        LinearLayout mImagesLL;
        RippleView rippleView;

        public OrderInformationViewHolder(View itemView) {
            super(itemView);
            mDesignerName = (TextView) itemView.findViewById(R.id.txtDesignerName);
            mStateDesigner = (TextView) itemView.findViewById(R.id.txtStateDesigner);
            mCourierDesigner = (TextView) itemView.findViewById(R.id.txtCourierDesigner);
            mTrackingNumberDesigner = (TextView) itemView.findViewById(R.id.txtTrackingNumberDesigner);
            mScrlImages = (HorizontalScrollView) itemView.findViewById(R.id.scrlImages);
            mImagesLL = (LinearLayout) itemView.findViewById(R.id.imagesLL);
            rippleView = (RippleView) itemView.findViewById(R.id.rippleView);
            rippleView.setOnRippleCompleteListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sOrderInformationClickListener.onOrderInformationClicked(v, getPosition());
        }

        @Override
        public void onComplete(RippleView rippleView) {
            sOrderInformationClickListener.onOrderInformationClicked(rippleView, getPosition());
        }
    }

    public static class OrderInformationHeaderViewHolder extends RecyclerView.ViewHolder {

        TextView mOrderSummary, mState, mCourier, mTrackingNumber, mItemTotal, mDiscount, mShipping, mCod, mTotal;

        public OrderInformationHeaderViewHolder(View itemView) {
            super(itemView);
            mOrderSummary = (TextView) itemView.findViewById(R.id.orderSummary);
            mState = (TextView) itemView.findViewById(R.id.txtState);
            mCourier = (TextView) itemView.findViewById(R.id.txtCourier);
            mTrackingNumber = (TextView) itemView.findViewById(R.id.txtTrackingNumber);
            mItemTotal = (TextView) itemView.findViewById(R.id.txtItemTotal);
            mDiscount = (TextView) itemView.findViewById(R.id.txtDiscount);
            mShipping = (TextView) itemView.findViewById(R.id.txtShipping);
            mCod = (TextView) itemView.findViewById(R.id.txtCod);
            mTotal = (TextView) itemView.findViewById(R.id.txtTotal);
        }
    }

    private class VIEW_TYPES {
        public static final int Header = 1;
        public static final int Normal = 2;
        public static final int Footer = 3;
    }
}
