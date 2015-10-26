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
import com.mirraw.android.R;
import com.mirraw.android.Utils.DensityUtils;
import com.mirraw.android.Utils.UILUtils;
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.models.searchResults.Design;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RippleView.OnRippleCompleteListener {


    @Override
    public void onComplete(RippleView rippleView) {
        int Id = rippleView.getId();
        switch (Id) {
            case R.id.retry_button_ripple_container:
                sSearchResultsClickListener.onRetryButton();
                break;
        }
    }

    public interface SearchResultsClickListener {

        public void onSearchResultClicked(View v, int position);

        public void onRetryButton();

    }

    String TAG = SearchResultsAdapter.class.getSimpleName();
    RippleView mRetryButtonRippleView;
    ProgressWheel mProgressWheelBottom;
    LinearLayout mNoInternetBottom;

    @Override
    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPES.Normal:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_search_result, parent, false);
                break;
            case VIEW_TYPES.Footer:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_no_internet, parent, false);
                mNoInternetBottom = (LinearLayout) view.findViewById(R.id.noInternetLL);
                mNoInternetBottom.setVisibility(View.GONE);
                mProgressWheelBottom = (ProgressWheel) view.findViewById(R.id.progressWheel);
                mRetryButtonRippleView = (RippleView) view.findViewById(R.id.retry_button_ripple_container);
                //mRetryButtonRippleView.setVisibility(View.GONE);
                mRetryButtonRippleView.setOnRippleCompleteListener(this);

                StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                lp.setFullSpan(true);
                view.setLayoutParams(lp);
                break;
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_search_result, parent, false);
                break;
        }

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            if (position <= mDesignResults.size() - 1) {
                Design result = mDesignResults.get(position);
                String photoUrl = getImageUrl(position);
                String blockName = result.getTitle();
                String blockDesignerName = result.getDesigner();
                Double strPrice = result.getPrice();
                Double strPriceDiscounted = result.getDiscountPrice();
                Context context = ((ViewHolder) holder).blockImageView.getContext();
                /*Picasso.with(context).load("https://" + photoUrl).skipMemoryCache()
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.placeholder_image)
                        .into(((ViewHolder) holder).blockImageView);*/
                ImageLoader.getInstance()
                        .displayImage(Utils.addHttpSchemeIfMissing(photoUrl), ((ViewHolder) holder).blockImageView, UILUtils.getImageOptionsSmall(), new ImageLoadingListener() {
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
                                //Logger.d(TAG, "onLoadingComplete " + position);
                            }

                            @Override
                            public void onLoadingCancelled(String imageUri, View view) {

                            }
                        });

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
                    if (Double.compare(strPrice,strPriceDiscounted)!=0 && String.valueOf(strPriceDiscounted) != null) {
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
        try {
            /*switch (density) {
                case DisplayMetrics.DENSITY_MEDIUM:
                case DisplayMetrics.DENSITY_HIGH:
                    strImageUrl = mDesignResults.get(position).getSizes().getLarge();
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                case DisplayMetrics.DENSITY_XXHIGH:
                    strImageUrl = mDesignResults.get(position).getSizes().getOriginal();
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    strImageUrl = mDesignResults.get(position).getSizes().getOriginal();
                    break;
                default:
                    strImageUrl = mDesignResults.get(position).getSizes().getSmall();
                    break;
            }*/
            strImageUrl = mDesignResults.get(position).getSizes().getSmallM();
        } catch (Exception e) {
            Mint.logExceptionMessage(TAG, "getImageUrl SearchResultsAdapter position: " + position + " Search Results JSON: " + mDesignResults.get(position), e);
        }


        return strImageUrl;
    }


    List<Design> mDesignResults;
    private static SearchResultsClickListener sSearchResultsClickListener;
    String strCurrencyCode, strSymbol;


    public SearchResultsAdapter(String currencyCode, List<Design> sSearchResults, SearchResultsClickListener sSearchResultsClickListener) {
        this.mDesignResults = sSearchResults;
        this.sSearchResultsClickListener = sSearchResultsClickListener;
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
            return mDesignResults.size() + 1;
        } else {
            return mDesignResults.size();
        }

    }

    @Override
    public int getItemViewType(int position) {
        return (position >= mDesignResults.size()) ? VIEW_TYPES.Footer : VIEW_TYPES.Normal;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements RippleView.OnRippleCompleteListener {
        public ImageView blockImageView;
        public TextView blockTextView;
        public TextView blockDesignerTextView;
        public TextView txtPrice;
        public TextView txtPriceDiscounted;
        public TextView txtDiscount;
        public RippleView rippleView;

        public ViewHolder(View view) {
            super(view);
            blockImageView = (ImageView) view.findViewById(R.id.searchResultImageView);
            blockTextView = (TextView) view.findViewById(R.id.searchResultTextView);
            blockDesignerTextView = (TextView) view.findViewById(R.id.searchResultDesignerTextView);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtPriceDiscounted = (TextView) view.findViewById(R.id.txtPriceDiscounted);
            txtDiscount = (TextView) view.findViewById(R.id.txtDiscount);
            rippleView = (RippleView) view.findViewById(R.id.rippleView);
            if (rippleView != null) {
                rippleView.setOnRippleCompleteListener(this);
            }
        }

        @Override
        public void onComplete(RippleView rippleView) {
            int position = getPosition();

            sSearchResultsClickListener.onSearchResultClicked(rippleView, position);
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