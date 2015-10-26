package com.mirraw.android.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.mirraw.android.R;
import com.mirraw.android.Utils.DensityUtils;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.cart.Cart;
import com.mirraw.android.models.cart.LineItem;
import com.mirraw.android.ui.activities.ProductDetailActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by vihaan on 11/7/15.
 */
public class CartAdapter extends BaseAdapter {

    private List<LineItem> mLineItems;
    private Context mContext;

    public static int outOfStockItems = 0;

    DataChanged mDataChanged;

    public interface DataChanged {
        public void updateFooterData(Cart cartDetails);

        public void removeItem(int id);

        public void updateQuantity(int id, int quantity);
    }

    public CartAdapter(Context context, List<LineItem> lineItems, DataChanged dataChanged) {
        mContext = context;
        mLineItems = lineItems;
        mDataChanged = dataChanged;
        outOfStockItems = 0;
    }

    public List<LineItem> getProducts() {
        return mLineItems;
    }

    @Override
    public int getCount() {
        return mLineItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mLineItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {

            view = LayoutInflater.from(mContext).inflate(R.layout.list_item_cart, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.productImageView = (ImageView) view.findViewById(R.id.productImageView);
            viewHolder.productTitleTextView = (TextView) view.findViewById(R.id.productDescriptionTextView);
            viewHolder.designerTextView = (TextView) view.findViewById(R.id.designerTextView);
            viewHolder.totalPriceTextView = (TextView) view.findViewById(R.id.totalTextView);
            viewHolder.subtotalPriceTextView = (TextView) view.findViewById(R.id.subTotalTextView);
            viewHolder.outOfOrderTextView = (TextView) view.findViewById(R.id.outOfStockTextView);
            viewHolder.removeImageButton = (ImageButton) view.findViewById(R.id.removeImageButton);
            viewHolder.quantitySpinner = (Button) view.findViewById(R.id.quantitySpinner);
            viewHolder.addonLayout = (LinearLayout) view.findViewById(R.id.addonlayout);
            viewHolder.variantLayout = (LinearLayout) view.findViewById(R.id.variantLayout);
            viewHolder.productImageRippleView = (RippleView) view.findViewById(R.id.productImageRippleView);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        final LineItem productDetail = mLineItems.get(position);
        String currencyCode = String.valueOf(Character.toChars((char) Integer.parseInt(productDetail.getHexSymbol(), 16)));
        String imageUrl = Utils.addHttpSchemeIfMissing(getImageUrl(productDetail));
        /*Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image).into(viewHolder.productImageView);*/

        ImageLoader.getInstance()
                .displayImage(imageUrl, viewHolder.productImageView, UILUtils.getImageOptionsSmall(), new ImageLoadingListener() {
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


        viewHolder.productImageRippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                Utils.hideSoftKeyboard(mContext, rippleView);
                navigateToProductDetailScreen(productDetail);
            }
        });

        viewHolder.productTitleTextView.setText(productDetail.getTitle());
        viewHolder.productTitleTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mContext, v);
                navigateToProductDetailScreen(productDetail);
            }
        });

        viewHolder.designerTextView.setText("by "+productDetail.getDesigner());
        viewHolder.designerTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mContext, v);
                navigateToProductDetailScreen(productDetail);
            }
        });

        viewHolder.totalPriceTextView.setText(currencyCode + productDetail.getSnapshotPrice().toString());
        viewHolder.subtotalPriceTextView.setText(currencyCode + productDetail.getTotal().toString());

        if (productDetail.getRequiredStock()) {
            viewHolder.outOfOrderTextView.setVisibility(View.GONE);
        } else {
            viewHolder.outOfOrderTextView.setVisibility(View.VISIBLE);
            outOfStockItems++;
        }


        viewHolder.quantitySpinner.setText(productDetail.getQuantity() + " ");
        viewHolder.quantitySpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mContext, v);

                CharSequence[] list = new CharSequence[productDetail.getStock()];

                for (int i = 1; i <= productDetail.getStock(); i++) {
                    list[i - 1] = String.valueOf(i);
                }

                int selectedQuantity = -1;
                for (int i = 0; i < list.length; i++) {
                    if (productDetail.getQuantity() == Integer.parseInt(list[i].toString())) {
                        selectedQuantity = i;
                        break;
                    }
                }

                AlertDialog.Builder builder2 = new AlertDialog.Builder(mContext)
                        .setTitle("Choose Quantity")
                        .setSingleChoiceItems(list, selectedQuantity, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDataChanged.updateQuantity(productDetail.getId(), (which + 1));
                                //productDetail.setQuantity(which);
                                dialog.dismiss();
                                //notifyDataSetChanged();
                            }
                        });
                builder2.create().show();
            }
        });
        final int tempPosition = position;
        viewHolder.removeImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideSoftKeyboard(mContext, v);
                mDataChanged.removeItem(mLineItems.get(position).getId());
                /*mLineItems.remove(tempPosition);
                notifyDataSetChanged();
                mDataChanged.updateFooterData(mLineItems);*/
            }
        });

        /**TODO: Remove if(productDeatil.getVariant()!=null) when server sends blank values for all the attributes thats not available.**/
        if (productDetail.getVariant() != null) {
            View variants;
            if (productDetail.getVariant().getOptionTypeValues().size() > 0) {
                viewHolder.variantLayout.setVisibility(View.VISIBLE);
                viewHolder.variantLayout.removeAllViews();
                for (int i = 0; i < productDetail.getVariant().getOptionTypeValues().size(); i++) {
                    variants = LayoutInflater.from(mContext).inflate(R.layout.addon_item, null);
                    TextView optionType = (TextView) variants.findViewById(R.id.addon_key_textview);
                    optionType.setText(productDetail.getVariant().getOptionTypeValues().get(i).getOptionType().toString() + ": ");
                    TextView pName = (TextView) variants.findViewById(R.id.addon_value_textview);
                    pName.setText(productDetail.getVariant().getOptionTypeValues().get(i).getPName().toString());

                    viewHolder.variantLayout.addView(variants);
                }
            } else {
                viewHolder.variantLayout.setVisibility(View.GONE);
            }
        } else {
            viewHolder.variantLayout.setVisibility(View.GONE);
        }

        /**TODO: Remove if(productDetail.get**/
        if (productDetail.getLineItemAddons() != null) {
            View addons;
            if (productDetail.getLineItemAddons().size() > 0) {
                viewHolder.addonLayout.setVisibility(View.VISIBLE);
                viewHolder.addonLayout.removeAllViews();
                for (int i = 0; i < productDetail.getLineItemAddons().size(); i++) {
                    addons = LayoutInflater.from(mContext).inflate(R.layout.addon_item, null);
                    TextView name = (TextView) addons.findViewById(R.id.addon_key_textview);
                    name.setText(productDetail.getLineItemAddons().get(i).getName().toString());
                    TextView price = (TextView) addons.findViewById(R.id.addon_value_textview);
                    price.setText(currencyCode + productDetail.getLineItemAddons().get(i).getPrice().toString());

                    viewHolder.addonLayout.addView(addons);
                }
            } else {
                viewHolder.addonLayout.setVisibility(View.GONE);
            }
        } else {
            viewHolder.addonLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void navigateToProductDetailScreen(LineItem productDetail) {
        Bundle bundle = new Bundle();
        bundle.putString("productId", productDetail.getDesignId().toString());
        bundle.putString("productTitle", productDetail.getTitle());
        Intent intent = new Intent(mContext, ProductDetailActivity.class);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }


    private String getImageUrl(LineItem item) {
        String strImageUrl = "";

        DensityUtils densityUtils = new DensityUtils();
        int density = densityUtils.getDensity();

        /*switch (density) {
            case DisplayMetrics.DENSITY_MEDIUM:
            case DisplayMetrics.DENSITY_HIGH:
                strImageUrl = item.getSizes().getSmall();
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_XXHIGH:
                strImageUrl = item.getSizes().getLarge();
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                strImageUrl = item.getSizes().getOriginal();
                break;
            default:
                strImageUrl = item.getSizes().getLarge();
                break;
        }*/
        strImageUrl = item.getSizes().getSmallM();
        return strImageUrl;
    }


    static class ViewHolder {
        ImageView productImageView;
        TextView productTitleTextView, designerTextView, totalPriceTextView, subtotalPriceTextView, outOfOrderTextView;
        //ImageButton minusImageButton, plusImageButton;
        //TextView quantityTextView;
        ImageButton removeImageButton;
        Button quantitySpinner;
        LinearLayout addonLayout;
        LinearLayout variantLayout;
        RippleView productImageRippleView;
    }
}
