package com.mirraw.android.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.Utils.DensityUtils;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.searchResults.Design;
import com.mirraw.android.sharedresources.Logger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import java.util.List;

/**
 * Created by varun on 30/7/15.
 */
public class ProductListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RippleView.OnRippleCompleteListener {


    @Override
    public void onComplete(RippleView rippleView) {
        int Id = rippleView.getId();
        switch (Id) {
            case R.id.retry_button_ripple_container:
                sProductListingClickListener.onRetryButton();
                break;
        }
    }

    public interface ProductListingClickListener {
        public void onProductListingClicked(View v, int position);

        public void onRetryButton();
    }

    String TAG = ProductListingAdapter.class.getSimpleName();
    RippleView retryButtonRippleView;
    ProgressWheel mProgressWheelBottom;
    LinearLayout mNoInternetBottom;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view;
        //Logger.d(ProductListingAdapter.class.getSimpleName(), "value of i " + i);
        switch (i) {
            case VIEW_TYPES.Normal:
                //Logger.d(TAG, "grid_item_search_result");
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_search_result, viewGroup, false);
                break;
            case VIEW_TYPES.Footer:
                //Logger.d(TAG, "grid_bottom_progress_wheel");
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.include_no_internet, viewGroup, false);

                mNoInternetBottom = (LinearLayout) view.findViewById(R.id.noInternetLL);
                mNoInternetBottom.setVisibility(View.GONE);
                mProgressWheelBottom = (ProgressWheel) view.findViewById(R.id.progressWheel);
                retryButtonRippleView = (RippleView) view.findViewById(R.id.retry_button_ripple_container);
                //btnRetryBottom.setVisibility(View.GONE);
                retryButtonRippleView.setOnRippleCompleteListener(this);

                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                lp.setFullSpan(true);
                view.setLayoutParams(lp);
                break;
            default:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_item_search_result, viewGroup, false);
        }

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //Logger.d(TAG, "Position onBindViewHolder " + position + " " + getItemCount() + " instance of holder: " + holder.getClass().getSimpleName().toString());
        if (holder instanceof ProgressViewHolder) {
            //Logger.d(TAG, "Progress wheel " + position);
            mProgressWheelBottom = ((ProgressViewHolder) holder).progressWheelBottom;
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            lp.setFullSpan(true);
            holder.itemView.setLayoutParams(lp);
            retryButtonRippleView = ((ProgressViewHolder) holder).retryButtonRippleView;
            retryButtonRippleView.setVisibility(View.GONE);
        } else if (holder instanceof ViewHolder) {  //holder instanceof ViewHolder
            //Logger.d(TAG, "Normal holder " + position + " size " + mProductListing.size());
            if (position <= mProductListing.size() - 1) {
                Design result = mProductListing.get(position);
                String photoUrl = getImageUrl(position);
                String blockName = result.getTitle();
                String blockDesignerName = result.getDesigner();
                Double strPrice = result.getPrice();
                Double strPriceDiscounted = result.getDiscountPrice();
                Context context = ((ViewHolder) holder).blockImageview.getContext();
                /*Picasso.with(context).load("https://" + photoUrl)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(((ViewHolder) holder).blockImageview);*/

                ImageLoader.getInstance()
                        .displayImage(Utils.addHttpSchemeIfMissing(photoUrl), ((ViewHolder) holder).blockImageview, UILUtils.getImageOptionsSmall(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                //Logger.d(TAG, "Loading Started " + position);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                //Logger.d(TAG, "onLoadingFailed " + position);
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                /*ObjectAnimator.ofFloat(((ViewHolder) holder).blockImageview, "alpha", 0, 0.25f, 0.50f, 0.75f, 1)
                                        .setDuration(800)
                                        .start();*/
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });

                /*BitmapDrawable bmpDrawable = (BitmapDrawable) ((ViewHolder) holder).blockImageview.getDrawable();
                Bitmap bmp = bmpDrawable.getBitmap();
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);
                int size = b.size();
                Logger.d(TAG, "onBindViewHolder image size: " + size);*/
                if (blockName == null) {
                    ((ViewHolder) holder).blockTextView.setText("");
                } else {
                    ((ViewHolder) holder).blockTextView.setText(blockName);
                }
                if (blockDesignerName == null) {
                    ((ViewHolder) holder).blockDesignerTextView.setText("");
                } else {
                    ((ViewHolder) holder).blockDesignerTextView.setText("By " + blockDesignerName);
                }
                if (String.valueOf(strPrice) == null) {
                    ((ViewHolder) holder).txtPrice.setText("");
                } else {
                    if (strCurrencyCode.equalsIgnoreCase("20B9")) {
                        ((ViewHolder) holder).txtPrice.setText(strSymbol + " " + String.valueOf(strPriceDiscounted.intValue()));
                    } else {
                        ((ViewHolder) holder).txtPrice.setText(strSymbol + " " + String.valueOf(strPriceDiscounted));
                    }
                    Double intDiscount = result.getDiscountPercent();
                    int comparisionResult = Double.compare(strPrice, strPriceDiscounted);
                    if (comparisionResult != 0 && String.valueOf(strPriceDiscounted) != null) {
                        ((ViewHolder) holder).txtPriceDiscounted.setVisibility(View.VISIBLE);
                        ((ViewHolder) holder).txtDiscount.setVisibility(View.VISIBLE);
                        if (strCurrencyCode.equalsIgnoreCase("20B9")) {
                            ((ViewHolder) holder).txtPriceDiscounted.setText(strSymbol + " " + String.valueOf(strPrice.intValue()));
                            ((ViewHolder) holder).txtDiscount.setText(String.valueOf(String.valueOf(intDiscount.intValue())) + "% Off");
                        } else {
                            ((ViewHolder) holder).txtPriceDiscounted.setText(strSymbol + " " + String.valueOf(strPrice));
                            ((ViewHolder) holder).txtDiscount.setText(String.valueOf(String.valueOf(intDiscount)) + "% Off");
                        }
                        ((ViewHolder) holder).txtPriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        ((ViewHolder) holder).txtPriceDiscounted.setVisibility(View.GONE);
                        ((ViewHolder) holder).txtDiscount.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private String getImageUrl(int position) {
        String strImageUrl = "";

        DensityUtils densityUtils = new DensityUtils();
        int density = densityUtils.getDensity();


//        try {
//            switch (density) {
//                case DisplayMetrics.DENSITY_MEDIUM:
//                case DisplayMetrics.DENSITY_HIGH:
//
//                    break;
//                case DisplayMetrics.DENSITY_XHIGH:
//                case DisplayMetrics.DENSITY_XXHIGH:
//                    strImageUrl = mProductListing.get(position).getSizes().getOriginal();
//                    break;
//                case DisplayMetrics.DENSITY_XXXHIGH:
//                    strImageUrl = mProductListing.get(position).getSizes().getOriginal();
//                    break;
//                default:
//                    strImageUrl = mProductListing.get(position).getSizes().getSmall();
//                    break;
//            }
//        } catch (Exception e) {
//            Mint.logExceptionMessage(TAG, "getImageUrl ProductListingAdapter position: " + position + " productListing JSON: " + mProductListing.get(position), e);
//        }

        String temp = "";

        //some designs give null sizes.
        try {
            strImageUrl = mProductListing.get(position).getSizes().getSmallM();
            //Logger.i(TAG, strImageUrl);
        } catch (Exception e) {
            Design design = mProductListing.get(position);
            Gson gson = new Gson();
            String message = gson.toJson(design);
            Mint.logExceptionMessage(TAG, message, e);
        }


        return strImageUrl;
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= mProductListing.size()) ? VIEW_TYPES.Footer : VIEW_TYPES.Normal;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount();
    }

    List<Design> mProductListing;
    String strCurrencyCode, strSymbol;

    private static ProductListingClickListener sProductListingClickListener;

    public ProductListingAdapter(String currencyCode, List<Design> productListing, ProductListingClickListener sProductListingClickListener) {
        this.mProductListing = productListing;
        this.sProductListingClickListener = sProductListingClickListener;
        this.strCurrencyCode = currencyCode;
        this.strSymbol = String.valueOf(Character.toChars((char) Integer.parseInt(strCurrencyCode, 16)));
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
        mAllPagesShown = true;
        if (!mLastPageNotified) {
            notifyDataSetChanged();
            mLastPageNotified = true;
        }

        if (mProgressWheelBottom != null && mNoInternetBottom != null) {
            mProgressWheelBottom.setVisibility(View.GONE);
            mNoInternetBottom.setVisibility(View.GONE);
        }
    }


    Boolean mAllPagesShown = false;
    Boolean mLastPageNotified = false;

    @Override
    public int getItemCount() {
        if (!mAllPagesShown) {
            return mProductListing.size() + 1;
        } else {
            return mProductListing.size();
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements RippleView.OnRippleCompleteListener {

        public ImageView blockImageview;
        public TextView blockTextView;
        public TextView blockDesignerTextView;
        public TextView txtPrice;
        public TextView txtPriceDiscounted;
        public TextView txtDiscount;
        public RippleView rippleView;

        public ViewHolder(View itemView) {
            super(itemView);
            blockTextView = (TextView) itemView.findViewById(R.id.searchResultTextView);
            blockImageview = (ImageView) itemView.findViewById(R.id.searchResultImageView);
            blockDesignerTextView = (TextView) itemView.findViewById(R.id.searchResultDesignerTextView);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtPriceDiscounted = (TextView) itemView.findViewById(R.id.txtPriceDiscounted);
            txtDiscount = (TextView) itemView.findViewById(R.id.txtDiscount);
            rippleView = (RippleView) itemView.findViewById(R.id.rippleView);

            //hack
            if (rippleView != null) {
                rippleView.setOnRippleCompleteListener(this);
            }
        }

        @Override
        public void onComplete(RippleView rippleView) {
            int position = getPosition();
            sProductListingClickListener.onProductListingClicked(rippleView, position);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {

        public ProgressWheel progressWheelBottom;
        public RippleView retryButtonRippleView;

        public ProgressViewHolder(View itemView) {
            super(itemView);
            progressWheelBottom = (ProgressWheel) itemView.findViewById(R.id.progressWheelBottom);
            retryButtonRippleView = (RippleView) itemView.findViewById(R.id.retry_button_ripple_container);
        }

    }

    private class VIEW_TYPES {
        public static final int Normal = 2;
        public static final int Footer = 3;
    }
}
