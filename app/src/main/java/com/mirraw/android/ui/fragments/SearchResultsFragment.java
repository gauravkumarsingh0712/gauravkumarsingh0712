package com.mirraw.android.ui.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.mirraw.android.Utils.Utils;
import com.mirraw.android.async.GetSearchResultsAsync;
import com.mirraw.android.interfaces.DataLoader;
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
import com.mirraw.android.ui.adapters.SearchResultsAdapter;
import com.mirraw.android.ui.fragments.filters.RangeFilterFragment;
import com.mirraw.android.ui.widgets.RecyclerViewPauseOnScrollListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.splunk.mint.Mint;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nimesh Luhana on 08-07-2015.
 */

public class SearchResultsFragment extends Fragment implements GetSearchResultsAsync.SearchResultsLoader,
        DataLoader,
        SearchResultsAdapter.SearchResultsClickListener,
        SortAndFilterButtonListener,
        SortDialogFragment.SortOptionClickListener,
        ProductListingActivity.SortFilterClickListener, RippleView.OnRippleCompleteListener {


//    public interface SortAndFilterButtonListener {
//        void onSortButtonClicked(Sorts applicableSorts);
//
//        void onFilterButtonClicked(Filters filters);
//    }

    String TAG = SearchResultsFragment.class.getSimpleName();
    SortAndFilterButtonListener mSortAndFilterButtonListener;
    SortFilterChangeListener mSortFilterChangeListener;
    String mSearchTerm;
    String mTemp = null;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    TextView mEnterSearch, mNoSearchResults;


    TextView mFilter, mSort;
    private BroadcastReceiver mSearchBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mRecyclerView.isShown()) {
                mSortFilterChangeListener.hideSortFilterView();
            }
            mTemp = intent.getStringExtra("searchTerm");

            //checking if called after bieng destroyed
            Bundle b = intent.getExtras();
            if (b != null) {
                if (b.containsKey("sortKey") && b.containsKey("sortId") && b.containsKey("searchTerm")) {

                    mSortKey = b.getString("sortKey");
                    mSortId = b.getInt("sortId");
                    mSearchTerm = b.getString("searchTerm");
                    sort_results(mSortId, mSortKey);
                }


            }


            try {
                mSearchTerm = URLEncoder.encode(mTemp, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
                Logger.d(TAG, e.toString());
            }

            if (mTemp.length() >= 2) {


                showSearchProgressBar(mTemp);
                mPageCounter = 1;
                getFirstPageData();

            } else if (mTemp.length() == 1) {


                if (searchResultsAsync != null) {
                    searchResultsAsync.cancel(true);
                }
            } else if (mTemp.length() == 0) {
                showEmptySearchMessage();

            }


        }


    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSortAndFilterButtonListener = this;
        mSortFilterChangeListener = (SortFilterChangeListener) activity;
    }

    //doesnt actually show anything :P
    private void showEmptySearchMessage() {
        //mFilterSortButtonLayout.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.GONE);
        mNoInternetLL.setVisibility(View.GONE);
        mEnterSearch.setVisibility(View.VISIBLE);

    }

    private void showSearchProgressBar(String temp) {

        mEnterSearch.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.VISIBLE);
        //sProgressBarTextView.setText("Searching \"" + searchTerm +"\""); ....have not created any text view to display this
        mNoInternetLL.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        //mFilterSortButtonLayout.setVisibility(View.GONE);
        mNoSearchResults.setVisibility(View.GONE);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mSearchBroadcastReceiver, new IntentFilter("app_search"));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        beforeSearchQueryEntered();

        //loadSearchResults();
    }

    private void initViews(View view) {
        //initFilterAndSort(view);
        initRecyclerView(view);
        initProgressWheel(view);
        initNoInternetView(view);
        initEnterSearchTextView(view);
        initNoSearchResults(view);

    }


    private LinearLayout sortLinearLayout, filterLinearLayout;
    ImageView filterImageView, sortImageView;

    /*private void initFilterAndSort(View view) {
        mFilterSortButtonLayout = (RelativeLayout) view.findViewById(R.id.include_sort_filter);
        sortLinearLayout = (LinearLayout) view.findViewById(R.id.main_sort_layout);
        filterLinearLayout = (LinearLayout) view.findViewById(R.id.main_filter_layout);
        mFilter = (TextView) view.findViewById(R.id.filterButton);
        mSort = (TextView) view.findViewById(R.id.sortButton);
        filterImageView = (ImageView) view.findViewById(R.id.filter_imageview);
        sortImageView = (ImageView) view.findViewById(R.id.sort_imageview);
        sortLinearLayout.setOnClickListener(this);
        filterLinearLayout.setOnClickListener(this);
    }*/

    private void initEnterSearchTextView(View view) {
        mEnterSearch = (TextView) view.findViewById(R.id.enter_search);
    }

    private void initNoSearchResults(View view) {
        mNoSearchResults = (TextView) view.findViewById(R.id.no_search_results);
    }

    private RelativeLayout mFilterSortButtonLayout;
    private LinearLayout mNoInternetLL;


    private void initNoInternetView(View view) {
        mNoInternetLL = (LinearLayout) view.findViewById(R.id.noInternetLL);
        ((RippleView) view.findViewById(R.id.retry_button_ripple_container)).setOnRippleCompleteListener(this);
    }

    private ProgressWheel mProgressWheel;

    private void initProgressWheel(View view) {

        mProgressWheel = (ProgressWheel) view.findViewById(R.id.progressWheel);


    }

    private RecyclerView mRecyclerView;

    boolean loading = true;
    int[] pastVisiblesItems;
    int visibleItemCount;
    int totalItemCount;
    static int sThreshHold = 10;


    private void initRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        //LinearLayoutManager smanager = new LinearLayoutManager(getActivity());
        //GridLayoutManager gManager=new GridLayoutManager(getActivity(),3);
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

                if (loading) {
                    if ((pastVisiblesItems[1] + 1 >= totalItemCount - sThreshHold) || (pastVisiblesItems[0] + 1 >= totalItemCount - sThreshHold)) {
                        loading = false;

                        setNextPage();
                        if (mPageCounter <= mTotalPages) {
                            //mProductListingAdapter.showProgress();
                            //getNextPage();
                            onRetryButton();
                        } else {
                            mSearchResultsAdapter.lastPage();
                        }

                    }
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (pastVisiblesItems[0] + visibleItemCount)) {
                    loading = true;
                }


            }
        });

    }

    public void setNextPage() {
        mPageCounter = mNextPage;
    }

    @Override
    public void onSearchResultsPreLoad() {
        mProgressWheel.setVisibility(View.VISIBLE);
        mNoInternetLL.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        //mFilterSortButtonLayout.setVisibility(View.GONE);

        mEnterSearch.setVisibility(View.GONE);

    }

    private GetSearchResultsAsync searchResultsAsync;


    List<Design> mDesignResults;
    Sorts mSortsResults;
    Filters mFilters;
    int mTotalPages;

    SearchResults mSearchResults;
    SearchResultsAdapter mSearchResultsAdapter;


    @Override
    public void onSearchResultsLoaded(Response response) {

        Gson gson = new Gson();
        JSONObject jsonObject;
        String result_designs = null;
        String result_sorts = null;
        String strResponse = response.getBody();


        Type listOfSearchResults = new TypeToken<SearchResults>() {
        }.getType();

        try {
            if (response.getResponseCode() == 200) {
                mSearchResults = gson.fromJson(strResponse, listOfSearchResults);

                mSortsResults = mSearchResults.getSearch().getSorts();
                if (mFilters == null) {
                    mFilters = mSearchResults.getSearch().getFilters();
                }


                if (mPageCounter == 1) {
                    mDesignResults = mSearchResults.getSearch().getDesigns();

                    mSearchResultsAdapter = new SearchResultsAdapter(mSearchResults.getSearch().getHexSymbol(), mDesignResults, this);
                    mRecyclerView.swapAdapter(mSearchResultsAdapter, false);
                    mStaggeredGridLayoutManager.scrollToPositionWithOffset(0, 0);
                    mSearchResultsAdapter.notifyDataSetChanged();

                } else {
                    mDesignResults.addAll(mSearchResults.getSearch().getDesigns());
                    /*mSearchResultsAdapter.notifyDataSetChanged();
                    mRecyclerView.swapAdapter(mSearchResultsAdapter, false);*/
                    mSearchResultsAdapter.notifyItemRangeInserted(mSearchResultsAdapter.getItemCount(), mSearchResults.getSearch().getDesigns().size());
                }

                mTotalPages = mSearchResults.getSearch().getTotalPages();
                if (mSearchResults.getSearch().getNextPage() != null) {
                    mNextPage = mSearchResults.getSearch().getNextPage();
                } else {
                    mSearchResultsAdapter.lastPage();
                    mNextPage++;
                }

                if (mPageCounter == mTotalPages) {
                    mSearchResultsAdapter.lastPage();
                }
                onSearchResultsPostLoad();
                mSortFilterChangeListener.updateSortView(mSortId);
                mSortFilterChangeListener.updateFilterView(mFilterParams);
                //mSortFilterChangeListener.showSortFilterView();
/*
                if (mSortId != 0) {
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
                onSearchResultsLoadFailed(response);
            }
        } catch (Exception e) {
            Logger.d(TAG, "" + e.getMessage());
            onSearchResultsLoadFailed(response);
        }


    }

    @Override
    public void onSearchResultsLoadFailed(Response response) {
        if (mPageCounter == 1) {
            if (response.getResponseCode() == 0) { //not connected to the internet
                onNoInternet();
            } else if (response.getResponseCode() == 204 && (!mFilterParams.equalsIgnoreCase("") || (mSortId != 0 && !mSortKey.equalsIgnoreCase("")))) {
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
                Logger.e(TAG, getString(R.string.content_not_found));

            } else if (response.getResponseCode() == 500) {
                Logger.wtf(TAG, getString(R.string.internal_server_error));

            } else {
                couldNotLoadSearchResults();
            }
        } else {
            if (response.getResponseCode() == 0) { //not connected to the internet
                onNoInternetBottom();
            } else if (response.getResponseCode() == 204) {
                Logger.e(TAG, getString(R.string.content_not_found));

            } else if (response.getResponseCode() == 500) {
                Logger.wtf(TAG, getString(R.string.internal_server_error));
            } else {
                couldNotLoadSearchResultsBottom();
            }
        }


    }

    private void couldNotLoadSearchResultsBottom() {
        mSearchResultsAdapter.hideProgress();
        Toast.makeText(getActivity(), "Problem loading search results", Toast.LENGTH_LONG).show();
    }

    private void onNoInternetBottom() {
        mSearchResultsAdapter.hideProgress();
    }

    public void onNoInternet() {

        mRecyclerView.setVisibility(View.GONE);
        //mFilterSortButtonLayout.setVisibility(View.GONE);
        mNoInternetLL.setVisibility(View.VISIBLE);
        mProgressWheel.setVisibility(View.GONE);
        mEnterSearch.setVisibility(View.GONE);
        mNoSearchResults.setVisibility(View.GONE);
    }

    @Override
    public void couldNotLoadSearchResults() {
        Toast.makeText(getActivity(), "Problem loading search results", Toast.LENGTH_LONG).show();
    }


    int mPageCounter = 1;
    int mNextPage = 0;
    String url = "";

    @Override
    public void getFirstPageData() {

        if (mPageCounter == 1) {
            if (searchResultsAsync == null) {
                loadSearchResults();
            } else {
                searchResultsAsync.cancel(true);
                searchResultsAsync = new GetSearchResultsAsync(this, getActivity());

                url = getUrl();
                //Logger.d(TAG, url);

                HashMap<String, String> requestHeaders = new HashMap<String, String>();
                requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
                requestHeaders.put(Headers.TOKEN, getString(R.string.token));

                Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                        .setHeaders(requestHeaders)
                        .build();

                searchResultsAsync.execute(request);
            }
        } else {
            if (searchResultsAsync.getStatus() != AsyncTask.Status.RUNNING) {
                searchResultsAsync.cancel(true);
                searchResultsAsync = new GetSearchResultsAsync(this, getActivity());


                url = getUrl();
                //Logger.d(TAG, url);

                HashMap<String, String> requestHeaders = new HashMap<String, String>();
                requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
                requestHeaders.put(Headers.TOKEN, getString(R.string.token));

                Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                        .setHeaders(requestHeaders)
                        .build();

                searchResultsAsync.execute(request);
            }
        }

    }

    @Override
    public void loadSearchResults() {
        searchResultsAsync = new GetSearchResultsAsync(this, getActivity());

        String url = getUrl();

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        searchResultsAsync.execute(request);


    }

    String mSortKey = "";
    int mSortId = 0;

    public void sort_results(int sort_id, String sort_key) {

        mSortId = sort_id;
        mSortKey = sort_key;
        String url;
        mPageCounter = 1;
        if (mFilterParams != "") {
            url = ApiUrls.API_SEARCH + "?term='" + mSearchTerm + "'&" + sort_key + "=" + sort_id + "" + mFilterParams + "&page=" + mPageCounter;
        } else {
            url = ApiUrls.API_SEARCH + "?term='" + mSearchTerm + "'&" + sort_key + "=" + sort_id + "&page=" + mPageCounter;
        }

        if (searchResultsAsync != null) {
            searchResultsAsync.cancel(true);
        }

        onSearchResultsPreLoad();
        searchResultsAsync = new GetSearchResultsAsync(this, getActivity());


        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        searchResultsAsync.execute(request);
        //onSearchResultsPreLoad();


    }

    String mFilterParams = "";

    public void filter_results(String filter_params) {
        String url = "";
        mFilterParams = filter_params;
        mPageCounter = 1;
        if (mSortId != 0 && mSortKey != "") {
            url = ApiUrls.API_SEARCH + "?term=" + mSearchTerm + "" + filter_params + "&" + mSortKey + "=" + mSortId + "&page=" + mPageCounter;
        } else {
            url = ApiUrls.API_SEARCH + "?term=" + mSearchTerm + "" + filter_params + "&page=" + mPageCounter;
        }

        if (searchResultsAsync != null) {
            searchResultsAsync.cancel(true);
        }

        onSearchResultsPreLoad();
        searchResultsAsync = new GetSearchResultsAsync(this, getActivity());

        HashMap<String, String> requestHeaders = new HashMap<String, String>();
        requestHeaders.put(Headers.DEVICE_ID, NetworkUtil.getDeviceId(getActivity()));
        requestHeaders.put(Headers.TOKEN, getString(R.string.token));

        Request request = new Request.RequestBuilder(url, Request.RequestTypeEnum.GET)
                .setHeaders(requestHeaders)
                .build();

        searchResultsAsync.execute(request);
    }


    @Override
    public void setFirstPageData(String response) {

    }


    @Override
    public void onSearchResultsPostLoad() {

        mRecyclerView.setVisibility(View.VISIBLE);
        //mFilterSortButtonLayout.setVisibility(View.VISIBLE);

        mNoInternetLL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.GONE);
        mEnterSearch.setVisibility(View.GONE);
        mNoSearchResults.setVisibility(View.GONE);
        mSortFilterChangeListener.showSortFilterView();


    }

    @Override
    public void beforeSearchQueryEntered() {
        mRecyclerView.setVisibility(View.GONE);
        //mFilterSortButtonLayout.setVisibility(View.GONE);
        mNoInternetLL.setVisibility(View.GONE);
        mProgressWheel.setVisibility(View.GONE);
        mEnterSearch.setVisibility(View.VISIBLE);
        mNoSearchResults.setVisibility(View.GONE);

    }

    @Override
    public void onDestroy() {

        if (searchResultsAsync != null) {
            searchResultsAsync.cancel(true);
            searchResultsAsync = null;

        }
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mSearchBroadcastReceiver);
        super.onDestroy();
    }


    private void onRetryButtonClicked() {
        Intent i = new Intent("app_search");
        i.putExtra("searchTerm", mTemp);

        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);


    }

    @Override
    public void onSearchResultClicked(View v, int position) {

        Bundle bundle = new Bundle();
        bundle.putString("productId", mDesignResults.get(position).getId().toString());
        bundle.putString("productTitle", mDesignResults.get(position).getTitle());
        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRetryButton() {
        try {
            mSearchResultsAdapter.showProgress();
            getFirstPageData();
        } catch (Exception e) {
            Mint.logExceptionMessage(TAG, "Show Progress Search on page number: " + mPageCounter + " URL is: " + url, e);
        }

    }

    private String getUrl() {
        String url;

        if (mFilterParams != "" && mSortId != 0 && mSortKey != "") {
            url = ApiUrls.API_SEARCH + "?term=" + mSearchTerm + "&" + mSortKey + "=" + mSortId + "" + mFilterParams + "&page=" + mPageCounter;
        } else if (mSortId != 0 && mSortKey != "") {
            url = ApiUrls.API_SEARCH + "?term=" + mSearchTerm + "" + mFilterParams + "&" + mSortKey + "=" + mSortId + "&page=" + mPageCounter;
        } else if (mFilterParams != "") {
            url = ApiUrls.API_SEARCH + "?term=" + mSearchTerm + "" + mFilterParams + "&page=" + mPageCounter;
        } else {
            url = ApiUrls.API_SEARCH + "?term=" + mSearchTerm + "&page=" + mPageCounter;
        }

        return url;
    }

    DialogFragment mSortFragment;
    int position;

    @Override
    public void onSortButtonClicked(Sorts applicableSorts) {
        mSortKey = applicableSorts.getKey();


        Gson gson = new Gson();
        String options = gson.toJson(applicableSorts);


        mSortFragment = SortDialogFragment.newInstance(options, this, position);
       /* mSortFragment.show(getActivity().getFragmentManager(), null);*/
        mSortFragment.show(getActivity().getFragmentManager(), "SortDialogFragment");
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
            intent.putExtras(bundle);

            startActivityForResult(intent, FiltersActivity.FILTER_REQ_CODE);
            getActivity().overridePendingTransition(R.anim.slide_up_from_bottom, R.anim.fade_out);
        } catch (Exception e) {
            //Toast.makeText(getActivity(), "Some problem in Filter", Toast.LENGTH_SHORT).show();
            Utils.showSnackBar("Some problem in Filter", getActivity(), Snackbar.LENGTH_LONG);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FiltersActivity.FILTER_REQ_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                String filterQueryParams = data.getStringExtra("filterQueryParams");
                String filters = data.getStringExtra("filters");
                updateFilters(filters);

//                mSelectedRangeFilters = data.getStringExtra("selectedRangeFilters");
//                mSelectedIdFilters = data.getStringExtra("selectedIdFilters");
                // Toast.makeText(getActivity(), filterQueryParams, Toast.LENGTH_SHORT).show();
                SearchResultsFragment searchResultsFragment = (SearchResultsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
                searchResultsFragment.filter_results(filterQueryParams);
            } else if (resultCode == getActivity().RESULT_CANCELED) {

            }

        }
    }

    private void updateFilters(String filters) {
        Gson gson = new Gson();
        Type type = new TypeToken<Filters>() {
        }.getType();
        Filters data = gson.fromJson(filters, type);
        if (filters != null) {
            mFilters.setIdFilters(data.getIdFilters());
            mFilters.setRangeFilters(data.getRangeFilters());
        }
    }

    @Override
    public void onSortOptionClicked(int id) {
        {
            //make use of id as key of sort option as in json recieved
            position = id;
            SearchResultsFragment searchResultsFragment = (SearchResultsFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

            if (searchResultsFragment != null) {
                //use SearchResultsFragment to retrive sorted results!!
                mSortFragment.dismiss();
                searchResultsFragment.sort_results(id, mSortKey);
                // Toast.makeText(getActivity(), "" + id, Toast.LENGTH_LONG).show();


            } else {
                Toast.makeText(getActivity(), "fragment died!!", Toast.LENGTH_LONG).show();

                Intent i = new Intent("app_search");
                Bundle b = new Bundle();
                b.putString("searchTerm", mSearchTerm);
                b.putString("sortKey", mSortKey);
                b.putInt("sortId", id);

                i.putExtras(b);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(i);


            }


        }
    }

    @Override
    public void onSortClicked() {
        if (mSortsResults != null) {
            mSortAndFilterButtonListener.onSortButtonClicked(mSortsResults);
        }
    }

    @Override
    public void onFilterClicked() {
        if (mFilters != null) {
            mSortAndFilterButtonListener.onFilterButtonClicked(mFilters);
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
}