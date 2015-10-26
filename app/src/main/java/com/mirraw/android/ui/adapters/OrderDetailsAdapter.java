package com.mirraw.android.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.DensityUtils;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.orders.DesignerOrder;
import com.mirraw.android.models.orders.LineItemAddon;
import com.mirraw.android.models.orders.OptionTypeValue;
import com.mirraw.android.models.orders.OrderDetails;
import com.mirraw.android.models.orders.Variant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.splunk.mint.Mint;

import java.util.List;

/**
 * Created by varun on 19/8/15.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {

    DesignerOrder mDesignerOrder;
    OrderDetails mOrderDetails;
    int intDesignerOrderPosition;

    public OrderDetailsAdapter(OrderDetails orderDetails, int position) {
        this.mOrderDetails = orderDetails;
        this.intDesignerOrderPosition = position;
    }


    @Override
    public OrderDetailsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_track_orders, parent, false);

        OrderDetailsViewHolder orderDetailsViewHolder = new OrderDetailsViewHolder(view);

        return orderDetailsViewHolder;
    }

    String TAG = OrderDetailsAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(OrderDetailsViewHolder holder, int position) {
        try {
            String strSymbol = String.valueOf(Character.toChars((char) Integer.parseInt(mOrderDetails.getHexSymbol(), 16)));
            String strTitle = "";
            if (mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getTitle() != null) {
                strTitle = mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getTitle();
            }

            String strDesigner = "";
            if (mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getDesignerName() != null) {
                strDesigner = mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getDesignerName();
            }

            String strQuantity = "";
            if (String.valueOf(mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getQuantity()) != null) {
                strQuantity = String.valueOf(mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getQuantity());
            }

            String strSubTotal = "";
            if (String.valueOf(mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getPrice()) != null) {
                strSubTotal = String.valueOf(mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getPrice());
            }

            String strTotal = "";
            if (String.valueOf(mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getTotal()) != null) {
                strTotal = String.valueOf(mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getTotal());
            }


            Context context = holder.mProductImage.getContext();

            String strProductImageUrl = mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getSizes().getSmallM();//getImageUrl(position);

            /*Picasso.with(context).load("https://" + strProductImageUrl)
                    .placeholder(R.mipmap.company_logo)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.mProductImage);*/

            ImageLoader.getInstance()
                    .displayImage(Utils.addHttpSchemeIfMissing(strProductImageUrl), holder.mProductImage, UILUtils.getImageOptionsSmall(), new ImageLoadingListener() {
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

            if (!strTitle.equalsIgnoreCase("")) {
                holder.mTitle.setText(strTitle);
            } else {
                holder.mTitle.setVisibility(View.GONE);
            }

            /*if (!strDesigner.equalsIgnoreCase("")) {
                holder.mDesigner.setText("By " + strDesigner);
            } else {*/
            holder.mDesigner.setVisibility(View.GONE);
//            }

            if (!strQuantity.equalsIgnoreCase("")) {
                holder.mQuantity.setText("Quantity: " + strQuantity);
            } else {
                holder.mQuantity.setVisibility(View.GONE);
            }
            if (!strSubTotal.equalsIgnoreCase("")) {
                holder.mSubTotal.setText("Price: " + strSymbol + " " + strSubTotal);
            } else {
                holder.mSubTotal.setVisibility(View.GONE);
            }

            setLineItemAddons(holder, position);

            if (!strTotal.equalsIgnoreCase("")) {
                holder.mTotal.setText("Amount: " + strSymbol + " " + strTotal);
            } else {
                holder.mTotal.setVisibility(View.GONE);
            }

            setLineItemVariants(holder, position);

        } catch (Exception e) {
            e.printStackTrace();
            Gson gson = new Gson();
            Mint.logExceptionMessage(TAG, gson.toJson(mDesignerOrder).toString(), e);
        }

    }

    private void setLineItemVariants(OrderDetailsViewHolder holder, int position) {
        try {
            Variant variant = mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getVariant();
            List<OptionTypeValue> optionValues = variant.getOptionTypeValues();
            int size = optionValues.size();
            if (size > 0) {
                LinearLayout variantLL = holder.mVariantLL;
                variantLL.removeAllViews();

                for (int i = 0; i < size; i++) {
                    String strName = optionValues.get(i).getPName();
                    String strOptionType = optionValues.get(i).getOptionType();

                    TextView txtVariant = new TextView(variantLL.getContext());

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, DensityUtils.getPxFromDp(5.0f), 0, 0);

                    txtVariant.setLayoutParams(lp);

                    txtVariant.setText(strOptionType + ": " + strName);

                    variantLL.addView(txtVariant);
                }

            } else {
                holder.mVariantLL.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            holder.mVariantLL.setVisibility(View.GONE);
        }

    }

    private void setLineItemAddons(OrderDetailsViewHolder holder, int position) {
        List<LineItemAddon> lineItemAddon = mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().get(position).getLineItemAddons();
        int size = lineItemAddon.size();

        if (size > 0) {
            LinearLayout ll = holder.mLineItemAddonLL;
            ll.removeAllViews();

            for (int i = 0; i < size; i++) {
                String strAddonName = lineItemAddon.get(i).getName();
                String strAddonPrice = lineItemAddon.get(i).getSnapshotPrice();

                TextView txtAddons = new TextView(ll.getContext());

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, DensityUtils.getPxFromDp(5.0f), 0, 0);
                txtAddons.setLayoutParams(lp);

                txtAddons.setText(strAddonName + ": \n" + strAddonPrice);

                ll.addView(txtAddons);
            }
        } else {
            holder.mAddons.setVisibility(View.GONE);
            holder.mLineItemAddonLL.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mOrderDetails.getDesignerOrders().get(intDesignerOrderPosition).getLineItems().size();
    }

    public static class OrderDetailsViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle, mDesigner, mQuantity, mSubTotal, mTotal, mAddons;
        ImageView mProductImage;
        LinearLayout mLineItemAddonLL, mVariantLL;

        public OrderDetailsViewHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            mDesigner = (TextView) itemView.findViewById(R.id.txtDesigner);
            mQuantity = (TextView) itemView.findViewById(R.id.txtQuantity);
            mSubTotal = (TextView) itemView.findViewById(R.id.txtPrice);
            mTotal = (TextView) itemView.findViewById(R.id.txtTotal);
            mProductImage = (ImageView) itemView.findViewById(R.id.imgProduct);
            mLineItemAddonLL = (LinearLayout) itemView.findViewById(R.id.lineItemAddonsLL);
            mAddons = (TextView) itemView.findViewById(R.id.txtAddons);
            mVariantLL = (LinearLayout) itemView.findViewById(R.id.variantsLL);
        }
    }
}
