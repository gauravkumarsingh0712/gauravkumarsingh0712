package com.mirraw.android.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mirraw.android.R;
import com.mirraw.android.async.GetProductListingAsync;
import com.mirraw.android.interfaces.PaginatedDataLoader;
import com.mirraw.android.interfaces.SortAndFilterButtonListener;
import com.mirraw.android.interfaces.SortFilterChangeListener;
import com.mirraw.android.models.searchResults.Design;
import com.mirraw.android.models.searchResults.Filters;
import com.mirraw.android.models.searchResults.SearchResults;
import com.mirraw.android.models.searchResults.Sorts;
import com.mirraw.android.network.ApiUrls;
import com.mirraw.android.network.Headers;
import com.mirraw.android.network.NetworkUtil;
import com.mirraw.android.network.Request;
import com.mirraw.android.network.Response;
import com.mirraw.android.sharedresources.Logger;
import com.mirraw.android.ui.MarginDecoration;
import com.mirraw.android.ui.activities.FiltersActivity;
import com.mirraw.android.ui.activities.ProductDetailActivity;
import com.mirraw.android.ui.activities.ProductListingActivity;
import com.mirraw.android.ui.adapters.ProductListingAdapter;
import com.mirraw.android.ui.fragments.filters.RangeFilterFragment;
import com.mirraw.android.ui.widgets.RecyclerViewPauseOnScrollListener;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductListingFragment extends Fragment implements PaginatedDataLoader,
        View.OnClickListener,
        GetProductListingAsync.ProductListingLoader,
        ProductListingAdapter.ProductListingClickListener, SortAndFilterButtonListener,
        SortDialogFragment.SortOptionClickListener,
        ProductListingActivity.SortFilterClickListener, RippleView.OnRippleCompleteListener {


    public ProductListingFragment() {
        // Required empty public constructor
    }


    private String TAG = ProductListingFragment.class.getSimpleName();
    private String mKey, mValue;

    SortAndFilterButtonListener mSortAndFilterButtonListerner;

    SortFilterChangeListener mSortFilterChangeListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mSortAndFilterButtonListerner = this;
        mSortFilterChangeListener = (SortFilterChangeListener) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getActivity().getIntent().getExtras();//getArguments();
        if (bundle != null) {
            mKey = bundle.getString("key");
            mValue = bundle.getString("value");
            getActivity().setTitle(bundle.getString("title"));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*View view = (View) getView().getParent();
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) view.getLayoutParams();
        ScrollingSortFilterBehavior scrollingSortFilterBehavior = (ScrollingSortFilterBehavior) lp.getBehavior();
        scrollingSortFilterBehavior.setLayout(getView());*/
        /*CoordinatorLayout.LayoutParams lp = mCoordinatorLayoutParamsProvider.getLayoutParams();
        ScrollingSortFilterBehavior scrollingSortFilterBehavior = (ScrollingSortFilterBehavior) lp.getBehavior();
        scrollingSortFilterBehavior.setLayout(getView());*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_product_listing, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        getFirstPageData();
    }

    private void initViews(View view) {
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
        //initFilterAndSort(view);
    }

    TextView mFilter, mSort;
    private RelativeLayout mFilterSortButtonLayout;
    ImageView filterImageView, sortImageView;
    private LinearLayout sortLinearLayout, filterLinearLayout;

    /*private void initFilterAndSort(View view) {
        mFilterSortButtonLayout = (RelativeLayout) view.findViewById(R.id.include_sort_filter);
        sortLinearLayout = (LinearLayout) view.findViewById(R.id.main_sort_layout);
        filterLinearLayout = (LinearLayout) view.findViewById(R.id.main_filter_layout);
        mFilter = (TextView) view.findViewById(R.id.filterButton);
        mSort = (TextView) view.findViewById(R.id.sortButton);
        filterImageView = (ImageView) view.findViewById(R.id.filter_imageview);
        sortImageView = (ImageView) view.findViewById(R.id.sort_imageview);
        //sortLinearLayout.setOnClickListener(this);
        //filterLinearLayout.setOnClickListener(this);

    }*/

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    boolean loading = true;
    int[] pastVisiblesItems;
    int visibleItemCount;
    int totalItemCount;
    ViewPropertyAnimator viewPropertyAnimator;
    static int sThreshHold = 10;

    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_list);

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(new MarginDecoration(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerViewPauseOnScrollListener(ImageLoader.getInstance(), false, true));


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mStaggeredGridLayoutManager.getChildCount();
                totalItemCount = mStaggeredGridLayoutManager.getItemCount();

                //Logger.d(TAG, "Value of loading: " + loading);
                pastVisiblesItems = mStaggeredGridLayoutManager.findLastVisibleItemPositions(null);

                if (loading && mNextPage != 0) {
                    if ((pastVisiblesItems[1] + 1 >= totalItemCount - sThreshHold) || (pastVisiblesItems[0] + 1 >= totalItemCount - sThreshHold)) {
                        loading = false;

                        setNextPage();
                        if (mPageCounter <= mTotalPages) {
                            onRetryButton();
                        } else {
                            mProductListingAdapter.lastPage();
                        }

                    }
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (pastVisiblesItems[0] + visibleItemCount)) {
                    loading = true;
                }

                /*Logger.d(TAG, "" + dy);
                int sortFilterHeight = mFilterSortButtonLayout.getHeight();
                int translationY = 0;
                if (dy < 0) {
                    translationY = -sortFilterHeight;
                } else if (dy > 0) {
                    translationY = sortFilterHeight;
                }
                viewPropertyAnimator.animate(mFilterSortButtonLayout).setDuration(300).translationY(translationY);*/
            }
        });
    }

    private ProgressWheel mProgressWheel;
    private ProgressWheel mProgressWheelBottom;

    private void initProgressWheel(View view) {
        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);
        mProgressWheelBottom = (ProgressWheel) view.findViewById(R.id.progressWheelBottom);
    }

    private RelativeLayout mConnectionContainer;
    private LinearLayout mNoInternetLL;

    private void initNoInternetView(View view) {
        mConnectionContainer = (RelativeLayout) view.findViewById(R.id.connectionContainer);
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        ((RippleView) view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
    }


    int mPageCounter = 1;
    int mNextPage = 0;
    String url = "";

    @Override
    public void getNextPage() {

        mSortFilterChangeListener.hideSortFilterView();
        if (productListingAsync.getStatus() != AsyncTask.Status.RUNNING) {
            productListingAsync.cancel(true);
            productListingAsync = new GetProductListingAsync(this, getActivity());


            if (mSortId != 0 && mSortKey != "" && mFilterParams != "") {
                url = ApiUrls.API_SEARCH + "?" + mKey + "=" + mValue + "&page=" + mPageCounter + "" + mFilterParams + "&" + mSortKey + "=" + mSortId + "";
            } else if (mFilterParams != "") {
                url = ApiUrls.API_SEARCH + "?" + mKey + "=" + mValue + "&page=" + mPageCounter + "" + mFilterParams + "";
            } else if (mSortId != 0 && mSortKey != "") {
                url = ApiUrls.API_SEARCH + "?" + mKey + "=" + mValue + "&page=" + mPageCounter + "&" + mSortKey + "=" + mSortId + "";
            } else {
                url = ApiUrls.API_SEARCH + "?" + mKey + "=" + mValue + "&page=" + mPageCounter + "";
            }


            HashMap<String, String> requestHeaders = new HashMap<String, String>();
            requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
            requestHeaders.put(Headers.TOKEN, getString(R.string.token));

            Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                    .setHeaders(requestHeaders)
                    .build();

            productListingAsync.execute(request);

            if (mPageCounter == 1) {
                mProgressWheel.setVisibility(View.VISIBLE);
                if (mSortId != 0 || mFilterParams != "") {
                    mRecyclerView.setVisibility(View.GONE);
                    //mFilterSortButtonLayout.setVisibility(View.GONE);
                    mNoInternetLL.setVisibility(View.GONE);
                    mProgressWheel.setVisibility(View.VISIBLE);
                }
            }
        } /*else {
            mProductListingAdapter.lastPage();
        }*/

    }

    @Override
    public void setNextPage() {
        mPageCounter = mNextPage;
    }


    @Override
    public void getFirstPageData() {
        try {
            mConnectionContainer.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            //mFilterSortButtonLayout.setVisibility(View.GONE);
            mNoInternetLL.setVisibility(View.GONE);
            mProgressWheel.setVisibility(View.VISIBLE);
            mKey = URLEncoder.encode("" + mKey, "UTF-8");
            //mValue = URLDecoder.decode("" + mValue, "UTF-8");
            if (mValue.contains(" ")) {
                mValue = URLEncoder.encode("" + mValue, "UTF-8");
            }
            if (productListingAsync == null) {
                loadProductListing();
            } else {
            /*productListingAsync.cancel(true);
            productListingAsync = new GetProductListingAsync(this, getActivity());*/
                getNextPage();
            }
        } catch (Exception e) {
            Mint.logExceptionMessage(TAG, "URL Dead", e);
        }

    }

    @Override
    public void setFirstPageData(String response) {

    }

    private GetProductListingAsync productListingAsync;

    @Override
    public void loadProductListing() {
        productListingAsync = new GetProductListingAsync(this, getActivity());
        getNextPage();
    }

    List<Design> mDesignResults;
    Sorts mSortsResults;
    Filters mFilters;
    int mTotalPages;

    SearchResults mSearchResults;
    ProductListingAdapter mProductListingAdapter;

    @Override
    public void onProductListingLoaded(Response response) {

        Gson gson = new Gson();
        JSONObject jsonObject = null;
        String result_designs = null;
        String result_sorts = null;
        String strResponse = response.getBody();

        Type listOfProducts = new TypeToken<SearchResults>() {
        }.getType();
        try {
            if (response.getResponseCode() == 200) {
                mSearchResults = gson.fromJson(strResponse, listOfProducts);
                mSortsResults = mSearchResults.getSearch().getSorts();
                if (mFilters == null) {
                    mFilters = mSearchResults.getSearch().getFilters();

                }
                if (mPageCounter == 1) {
                    mDesignResults = mSearchResults.getSearch().getDesigns();
                    mProductListingAdapter = new ProductListingAdapter(mSearchResults.getSearch().getHexSymbol(), mDesignResults, this);
                    //mRecyclerView.setAdapter(mProductListingAdapter);
                    mRecyclerView.swapAdapter(mProductListingAdapter, false);
                    //mProductListingAdapter.notifyDataSetChanged();
                    mStaggeredGridLayoutManager.scrollToPositionWithOffset(0, 0);
                } else {
                    //loading = true;
                    mDesignResults.addAll(mSearchResults.getSearch().getDesigns());

                    mProductListingAdapter.notifyItemRangeInserted(mProductListingAdapter.getItemCount(), mSearchResults.getSearch().getDesigns().size());
                    //mProductListingAdapter.notifyDataSetChanged();
                    //mRecyclerView.swapAdapter(mProductListingAdapter, false);

                }

                mTotalPages = mSearchResults.getSearch().getTotalPages();
                if (mSearchResults.getSearch().getNextPage() != null) {
                    mNextPage = mSearchResults.getSearch().getNextPage();
                } else {
                    mProductListingAdapter.lastPage();
                    mNextPage++;
                }

                if (mPageCounter == mTotalPages) {
                    mProductListingAdapter.lastPage();
                }

                onProductListingPostLoad();
                mSortFilterChangeListener.updateSortView(mSortId);
                mSortFilterChangeListener.updateFilterView(mFilterParams);
                mSortFilterChangeListener.showSortFilterView();
                /*if (mSortId != 0) {
                    sortImageView.getDrawable().setColorFilter(getResources().getColor(R.color.green_color), PorterDuff.Mode.MULTIPLY);
                    mSort.setTextColor(getResources().getColor(R.color.green_color));
                } else {
                    sortImageView.getDrawable().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
                    mSort.setTextColor(getResources().getColor(android.R.color.white));
                }

                if (!mFilterParams.equalsIgnoreCase("")) {
                    filterImageView.getDrawable().setColorFilter(getResources().getColor(R.color.green_color), PorterDuff.Mode.MULTIPLY);
                    mFilter.setTextColor(getResources().getColor(R.color.green_color));
                } else {
                    filterImageView.getDrawable().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY);
                    mFilter.setTextColor(getResources().getColor(android.R.color.white));
                }*/
            } else {
                onProductListingLoadFailed(response);
            }
        } catch (Exception e) {
            Logger.d(TAG, "" + e.getMessage());
            //Toast.makeText(getActivity(), strResponse, Toast.LENGTH_LONG).show();
            onProductListingLoadFailed(response);
        }

    }


    @Override
    public void onProductListingLoadFailed(Response response) {
        if (mPageCounter == 1) {
            if (response.getResponseCode() == 0) {
                onNoInternet();
            } else if (response.getResponseCode() == 204 && (!mFilterParams.equalsIgnoreCase("") || (mSortId != 0 && !mSortKey.equalsIgnoreCase("")))) {
                //Toast.makeText(getActivity(), "No content found for the applied Sort/Filter", Toast.LENGTH_LONG).show();
                Mint.logEvent(TAG, MintLogLevel.Info, "204", url);
                final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "No content found for the applied Sort/Filter", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
                snackbar.show();
                mProgressWheel.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mSortFilterChangeListener.showSortFilterView();
                //mFilterSortButtonLayout.setVisibility(View.VISIBLE);
            } else if (response.getResponseCode() == 204) {
                //Toast.makeText(getActivity(), "Content not available", Toast.LENGTH_LONG).show();
                Mint.logEvent(TAG, MintLogLevel.Info, "204", url);
                final Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), "No content found for the applied Sort/Filter", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                        getActivity().finish();
                    }
                });
                snackbar.getView().setBackgroundColor(getResources().getColor(R.color.dark_grey));
                snackbar.show();
                mProgressWheel.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                //mFilterSortButtonLayout.setVisibility(View.GONE);
            } else if (response.getResponseCode() == 500) {
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
                onNoInternet();
            } else {
                couldNotLoadProductListing();
            }
        } else {
            if (response.getResponseCode() == 0) {
                onNoInternetBottom();
            } else if (response.getResponseCode() == 204) {
                //Toast.makeText(getActivity(), "Content not available", Toast.LENGTH_LONG).show();
                //onNoInternetBottom();
                Mint.logEvent(TAG, MintLogLevel.Info, "204", url);
                mProductListingAdapter.lastPage();
            } else if (response.getResponseCode() == 500) {
                Toast.makeText(getActivity(), "Server error", Toast.LENGTH_LONG).show();
                onNoInternetBottom();
            } else {
                couldNotLoadProductListingBottom();
            }
        }
    }

    public void onNoInternetBottom() {
        mProductListingAdapter.hideProgress();
    }

    public void couldNotLoadProductListingBottom() {
        mProductListingAdapter.hideProgress();
        Toast.makeText(getActivity(), "Problem loading products", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNoInternet() {
        mConnectionContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        //mFilterSortButtonLayout.setVisibility(View.GONE);
        mNoInternetLL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
    }

    @Override
    public void couldNotLoadProductListing() {
        Toast.makeText(getActivity(), "Could not load products", Toast.LENGTH_LONG).show();
        mConnectionContainer.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        //mFilterSortButtonLayout.setVisibility(View.GONE);
        mNoInternetLL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
    }

    @Override
    public void onProductListingPostLoad() {
        mRecyclerView.setVisibility(View.VISIBLE);
        //mFilterSortButtonLayout.setVisibility(View.VISIBLE);
        mNoInternetLL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.GONE);
    }

    @Override
    public void onProductListingClicked(View v, int position) {
        if (position < mDesignResults.size()) {
            Bundle bundle = new Bundle();
            bundle.putString("productId", mDesignResults.get(position).getId().toString());
            bundle.putString("productTitle", mDesignResults.get(position).getTitle());
            Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onRetryButton() {
        try {
            mProductListingAdapter.showProgress();
            getNextPage();
        } catch (Exception e) {
            Mint.logExceptionMessage(TAG, "ShowProgress on page number: " + mPageCounter + " and url: " + url, e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retryButton:
                onRetryButtonClicked();
                break;
            case R.id.main_filter_layout:
                mSortAndFilterButtonListerner.onFilterButtonClicked(mFilters);
                break;
            case R.id.main_sort_layout:
                mSortAndFilterButtonListerner.onSortButtonClicked(mSortsResults);
                break;

        }
    }

    private void onRetryButtonClicked() {
        getFirstPageData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        productListingAsync.cancel(true);
    }


    String mSortKey = "";
    int mSortId = 0;

    public void sort_results(int sort_id, String sort_key) {
        mSortId = sort_id;
        mSortKey = sort_key;
        String url;
        if (mFilterParams != "") {
            url = ApiUrls.API_SEARCH + "?" + sort_key + "=" + sort_id + "" + mFilterParams;
        } else {
            url = ApiUrls.API_SEARCH + "?" + sort_key + "=" + sort_id + "";
        }


        if (productListingAsync != null) {
            productListingAsync.cancel(true);
        }
        mPageCounter = 1;
        mNextPage = 0;
        getNextPage();

    }

    String mFilterParams = "";

    public void filter_results(String filter_params) {
        String url = "";
        mFilterParams = filter_params;
        if (mSortId != 0 && mSortKey != "") {
            url = ApiUrls.API_SEARCH + "?term=" + filter_params + "&" + mSortKey + "=" + mSortId + "";
        } else {
            url = ApiUrls.API_SEARCH + "?term=" + filter_params;
        }

        if (productListingAsync != null) {
            productListingAsync.cancel(true);
        }
        mPageCounter = 1;
        mNextPage = 0;
        getNextPage();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FiltersActivity.FILTER_REQ_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                String filterQueryParams = data.getStringExtra("filterQueryParams");
                String filtersString = data.getStringExtra("filters");
                boolean isApplyFilter = data.getBooleanExtra("apply", false);
                if (isApplyFilter) {
                    //filterImageView.setImageAlpha(3);
                }

                updateFilters(filtersString);


//                mSelectedRangeFilters = data.getStringExtra("selectedRangeFilters");
//                mSelectedIdFilters = data.getStringExtra("selectedIdFilters");
                // Toast.makeText(getActivity(), filterQueryParams, Toast.LENGTH_SHORT).show();
                ProductListingFragment productListingFragment = (ProductListingFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                productListingFragment.filter_results(filterQueryParams);
            } else if (resultCode == getActivity().RESULT_CANCELED) {

            }

        }
    }

    Filters data;

    private void updateFilters(String filters) {
        Gson gson = new Gson();
        Type type = new TypeToken<Filters>() {
        }.getType();
        Filters data = gson.fromJson(filters, type);
        if (filters != null) {
            mFilters.setIdFilters(data.getIdFilters());
            mFilters.setRangeFilters(data.getRangeFilters());
            System.out.println("getSELECTED111 : " + mFilters.getRangeFilters().get(0).getList().get(0).getSelected());
        }
    }

    android.app.DialogFragment mSortFragment;
    int position;

    @Override
    public void onSortButtonClicked(Sorts applicableSorts) {
        mSortKey = applicableSorts.getKey();

        Gson gson = new Gson();
        String options = gson.toJson(applicableSorts);

        mSortFragment = SortDialogFragment.newInstance(options, this, position);
        /*mSortFragment.show(getActivity().getFragmentManager(), null);*/
        mSortFragment.show(getActivity().getFragmentManager(), "SortDialogFragment");
//        sortImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFilterButtonClicked(Filters filters) {
        try {
            Intent intent = new Intent(getActivity(), FiltersActivity.class);
            Gson gson = new Gson();

            mFilters = filters;

            String data = gson.toJson(mFilters);

            Bundle bundle = new Bundle();
            bundle.putString("filters", data);
            bundle.putString(RangeFilterFragment.KEY_HEX_VALUE, mSearchResults.getSearch().getHexSymbol());
            // bundle.putString("selectedRangeFilters", mSelectedRangeFilters);
            // bundle.putString("selectedIdFilters", mSelectedIdFilters);

            intent.putExtras(bundle);

            startActivityForResult(intent, FiltersActivity.FILTER_REQ_CODE);
            getActivity().overridePendingTransition(R.anim.slide_up_from_bottom, R.anim.fade_out);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Some problem in Filter", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSortOptionClicked(int id) {
        position = id;
        ProductListingFragment productListingFragment = (ProductListingFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
        if (productListingFragment != null) {
            mSortFragment.dismiss();
            productListingFragment.sort_results(id, mSortKey);
            // Toast.makeText(getActivity(), "" + id, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onSortClicked() {
        if (mSortsResults != null) {
            mSortAndFilterButtonListerner.onSortButtonClicked(mSortsResults);
        }
    }

    @Override
    public void onFilterClicked() {
        if (mFilters != null) {
            mSortAndFilterButtonListerner.onFilterButtonClicked(mFilters);
        }
    }

    @Override
    public void onComplete(RippleView rippleView) {
        switch (rippleView.getId()) {
            case R.id.retry_button_ripple_container:
                onRetryButtonClicked();
                break;


        }

    }

    private class SCROLL_DIRECTION {
        public static final int ScrollUp = 0;
        public static final int ScrollDown = 1;
    }
}
