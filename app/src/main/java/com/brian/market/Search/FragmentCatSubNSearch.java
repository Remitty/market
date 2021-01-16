package com.brian.market.Search;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.adapters.ProductAdapter;
import com.brian.market.helper.EndlessNestedScrollViewListener;
import com.brian.market.helper.GridSpacingItemDecoration;
import com.brian.market.helper.MyProductOnclicklinstener;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.home.adapter.ItemMainAllCatAdapter;
import com.brian.market.modelsList.homeCatListModel;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.ad_detail.Ad_detail_activity;
import com.brian.market.adapters.ItemSearchFeatureAdsAdapter;
import com.brian.market.helper.CatSubCatOnclicklinstener;
import com.brian.market.home.HomeActivity;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.utills.NestedScroll;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

public class FragmentCatSubNSearch extends Fragment {
    View mView;
    public static String requestFrom = "";
    ArrayList<ProductDetails> searchedAdList = new ArrayList<>();
    ArrayList<ProductDetails> featureAdsList = new ArrayList<>();
    RecyclerView recyclerViewProduct, recyclerViewFeatured, recyclerViewCat;
    //EditText editTextSearch;
    SettingsMain settingsMain;
    TextView textViewTitleFeature, textViewFilterText;
    ProductAdapter productAdapter;
    ItemSearchFeatureAdsAdapter itemSearchFeatureAdsAdapter;
    LinearLayout viewProductLayout;
    JSONObject jsonObjectPagination;
    JsonObject lastSentParamas;
    LinearLayout linearLayoutCollapse;
    Spinner spinnerFilter;
//    Button spinnerFilterText;
    LinearLayout linearLayoutFilter, emptyLayout, featuredLayout, searchLayout;
    boolean isSort = false;
    RelativeLayout relativeLayoutSpiner;
    NestedScrollView scrollView;
    boolean loading = true, hasNextPage = false;
    ProgressBar progressBar;
    int currentPage = 1, nextPage = 1, totalPage = 0;
    String myId, title, ad_country, nearby_latitude, nearby_longitude, nearby_distance, ad_cats1, city, sort;
    RestService restService;
    LinearLayoutManager MyLayoutManager2;
    private JSONObject jsonObjectFilterSpinner;
    private boolean spinnerTouched2 = false;

    public FragmentCatSubNSearch() {
        // Required empty public constructor
    }

    public void adforest_recylerview_autoScroll(final int duration, final int pixelsToMove, final int delayMillis,
                                                final RecyclerView recyclerView, final LinearLayoutManager gridLayoutManager
            , final ItemSearchFeatureAdsAdapter adapter) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            int count = 0;
            boolean flag = true;

            @Override
            public void run() {
                try {
                    if (count < adapter.getItemCount()) {
                        if (count == adapter.getItemCount() - 1) {
                            flag = false;
                        } else if (count == 0) {
                            flag = true;
                        }
                        if (flag) count++;
                        else count--;

                        recyclerView.smoothScrollToPosition(count);
                        handler.postDelayed(this, duration);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
        };

        handler.postDelayed(runnable, delayMillis);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_cat_subcatlist, container, false);

        restService = UrlController.createService(RestService.class);
        settingsMain = new SettingsMain(getActivity());
//        if (!settingsMain.getAppOpen()) {
//        } else
//            restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());

        getActivity().setTitle(getString(R.string.search));

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            myId = bundle.getString("id", "");
            title = bundle.getString("title", "");
            ad_country = bundle.getString("ad_country", "");
            nearby_latitude = bundle.getString("nearby_latitude", "");
            nearby_longitude = bundle.getString("nearby_longitude", "");
            nearby_distance = bundle.getString("nearby_distance", "");
            ad_cats1 = bundle.getString("ad_cats1", "");
            city = bundle.getString("city", "");
        }


        linearLayoutCollapse = mView.findViewById(R.id.linearLayout);
        linearLayoutFilter = mView.findViewById(R.id.filter_layout);
        textViewFilterText = mView.findViewById(R.id.textViewFilter);

        relativeLayoutSpiner = mView.findViewById(R.id.rel1);

        emptyLayout = mView.findViewById(R.id.empty_view);

        recyclerViewCat = mView.findViewById(R.id.recycler_view_category);
        recyclerViewCat.setHasFixedSize(true);
        recyclerViewCat.setNestedScrollingEnabled(true);
        ViewCompat.setNestedScrollingEnabled(recyclerViewCat, true);

        initFeaturedView();
        initSearchView();
        initNestedListener();
        initSpinner();

        adforest_submitQuery("");

        if (requestFrom.equals("")) {
            ((HomeActivity) getActivity()).updateApi(new HomeActivity.UpdateFragment() {
                @Override
                public void updatefrag(String s) {
                    title = s;
                    adforest_submitQuery("");
                    scrollView.scrollTo(0, 0);

                }

                @Override
                public void updatefrag(String latitude, String longitude, String distance, String mcity) {
                    nearby_latitude = latitude;
                    nearby_longitude = longitude;
                    nearby_distance = distance;
                    city = mcity;
                    adforest_submitQuery("");
                }
            });
        }
        if (requestFrom.equals("")) {
            SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
            swipeRefreshLayout.setEnabled(true);
        }
        return mView;
    }

    private void initSpinner() {
        spinnerFilter = mView.findViewById(R.id.spinner);
//        spinnerFilterText=view.findViewById(R.id.spinnerafter);
//        spinnerFilterText.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        spinnerFilter.setOnTouchListener((v, event) -> {
            System.out.println("Real touch felt.");
            spinnerTouched2 = true;
            return false;
        });

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinnerTouched2) {
                    try {
                        //Toast.makeText(getActivity(), "" + dropDownJSONOpt.getJSONObject(i).getString("key"), Toast.LENGTH_SHORT).show();
                        isSort = true;
                        adforest_submitQuery(jsonObjectFilterSpinner.getJSONArray("sort_arr").getJSONObject(i).getString("key"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                spinnerTouched2 = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initNestedListener() {
        scrollView = mView.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener(new EndlessNestedScrollViewListener(recyclerViewProduct.getLayoutManager()){
            @Override
            public void onLoadMore(int page) {
                showMoreLoading();
                loadMoreLatestProducts(page);
            }
        });
    }

    private void initFeaturedView() {
        textViewTitleFeature = mView.findViewById(R.id.textView6);
        featuredLayout = mView.findViewById(R.id.ll_featured);
        recyclerViewFeatured = mView.findViewById(R.id.feature_recycler_view);
        recyclerViewFeatured.setHasFixedSize(true);
        MyLayoutManager2 = new LinearLayoutManager(getActivity());
        MyLayoutManager2.setInitialPrefetchItemCount(3);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewFeatured.setLayoutManager(MyLayoutManager2);
        itemSearchFeatureAdsAdapter = new ItemSearchFeatureAdsAdapter(getActivity(), featureAdsList);
        itemSearchFeatureAdsAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(ProductDetails item) {
                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                intent.putExtra("adId", ""+item.getId());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }

            @Override
            public void onRemoveFav(int position) {

            }

            @Override
            public void onShare(ProductDetails item) {

            }

            @Override
            public void onAddCart(ProductDetails item) {

            }

        });
        recyclerViewFeatured.setAdapter(itemSearchFeatureAdsAdapter);
    }

    private void initSearchView() {

        searchLayout = mView.findViewById(R.id.search_realty);
        recyclerViewProduct = mView.findViewById(R.id.product_recycler_view);
        recyclerViewProduct.setHasFixedSize(true);
        recyclerViewProduct.setNestedScrollingEnabled(false);
        StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(3, 1);
        recyclerViewProduct.setLayoutManager(grid);
        productAdapter = new ProductAdapter(getActivity(), searchedAdList);
        productAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(ProductDetails item) {

                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                intent.putExtra("adId", ""+item.getId());
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }

            @Override
            public void onRemoveFav(int position) {

            }

            @Override
            public void onShare(ProductDetails item) {

            }

            @Override
            public void onAddCart(ProductDetails item) {

            }

        });
        recyclerViewProduct.setAdapter(productAdapter);


    }

    private void initListener() {
    }

    private void adforest_submitQuery(String s) {

        JsonObject params = new JsonObject();
        if (!myId.equals("")) {
            params.addProperty("catId", myId);
        }
        if (!title.equals("")) {
            params.addProperty("title", title);
        }
        if (isSort) {
            sort = s;
            params.addProperty("sort", s);
        }
        if (!ad_country.equals("")) {
            params.addProperty("ad_country", ad_country);
        }
        if (!nearby_latitude.equals("") && !nearby_longitude.equals("") && !nearby_distance.equals("0") && !nearby_distance.equals("")) {
            params.addProperty("nearby_latitude", nearby_latitude);
            params.addProperty("nearby_longitude", nearby_longitude);
            params.addProperty("nearby_distance", nearby_distance);
        }
        if(!city.equals("")){
            params.addProperty("city", city);
        }
        if (!ad_cats1.equals("")) {
            params.addProperty("ad_cats1", ad_cats1);
        }
        lastSentParamas = params;

        params.addProperty("page_number", 1);


        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            Log.d("info Send MenuSearch =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postGetMenuSearchData(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info MenuSearch Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info MenuSearch object", "" + response.getJSONObject("data"));
                                HomeActivity.checkLoading = false;

//                                getActivity().setTitle(response.getJSONObject("extra").getString("title"));

//                                if (response.getJSONObject("extra").getBoolean("is_show_featured")) {
//                                    textViewTitleFeature.setVisibility(View.VISIBLE);
//                                } else {
//                                    textViewTitleFeature.setVisibility(View.GONE);
//                                }//for test
//                                spinnerFilterText.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Intent i = new Intent(getActivity(), SearchActivity.class);
////                                        view.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.zoomin));
//                                        startActivity(i);
//
//                                    }
//                                });
                                
                                setCategories(response.getJSONArray("categories"));
//

                                try {

                                    jsonObjectFilterSpinner = response.getJSONObject("topbar");
                                    final JSONArray dropDownJSONOpt = jsonObjectFilterSpinner.getJSONArray("sort_arr");
                                    final ArrayList<String> SpinnerOptions;
                                    SpinnerOptions = new ArrayList<>();
                                    for (int j = 0; j < dropDownJSONOpt.length(); j++) {
                                        SpinnerOptions.add(dropDownJSONOpt.getJSONObject(j).getString("value"));
                                    }

                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item_medium, SpinnerOptions);
                                    spinnerFilter.setAdapter(adapter);

                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                loading = true;

                                adforest_loadList(response.getJSONObject("data").getJSONObject("featured"),
                                        response.getJSONObject("data").getJSONArray("products"),
                                        response.getJSONObject("topbar"));

                                if (settingsMain.isFeaturedScrollEnable()) {

                                    adforest_recylerview_autoScroll(settingsMain.getFeaturedScroolDuration(),
                                            40, settingsMain.getFeaturedScroolLoop(),
                                            recyclerViewFeatured, MyLayoutManager2, itemSearchFeatureAdsAdapter);
                                }



                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info MenuSearch ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info MenuSearch err", String.valueOf(t));
                        Log.d("info MenuSearch err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
        isSort = false;
    }

    private void setCategories(JSONArray data) {
        ArrayList<homeCatListModel> listitems = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select category");
        for (int i = 0; i < data.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            item.setTitle(data.optJSONObject(i).optString("cat_name"));
            item.setThumbnail(data.optJSONObject(i).optString("cat_img"));
            item.setId(data.optJSONObject(i).optString("id"));
            item.setData(data.optJSONObject(i));
            listitems.add(item);
            categories.add(data.optJSONObject(i).optString("cat_name"));
        }

        settingsMain.setCategories(categories);

        int noOfCol = data.length();

        GridLayoutManager MyLayoutManager = new GridLayoutManager(getActivity(), noOfCol);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerViewCat.setLayoutManager(MyLayoutManager);
        int spacing = 0; // 50px

        recyclerViewCat.addItemDecoration(new GridSpacingItemDecoration(noOfCol, spacing, false));
        ItemMainAllCatAdapter adapter = new ItemMainAllCatAdapter(getContext(), listitems, noOfCol, false);
        recyclerViewCat.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
            myId = item.getId();
            adforest_submitQuery("");

        });
    }

    void adforest_loadList(JSONObject featureObject, JSONArray searchAds, JSONObject filtertext) {
        searchedAdList.clear();
        featureAdsList.clear();
        try {
            Log.d("search jsonaarry is = ", searchAds.toString());
            for (int i = 0; i < searchAds.length(); i++) {

                ProductDetails item = new ProductDetails();
                JSONObject object = searchAds.getJSONObject(i);
                item.setData(object);

                searchedAdList.add(item);
            }
            productAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.d("feature Object is = ", featureObject.getJSONArray("products").toString());
//            textViewTitleFeature.setText(featureObject.getString("text"));
            if (featureObject.getJSONArray("products").length() > 0)
                for (int i = 0; i < featureObject.getJSONArray("products").length(); i++) {

                    ProductDetails item = new ProductDetails();
                    JSONObject object = featureObject.getJSONArray("products").getJSONObject(i);
                    item.setData(object);
                    featureAdsList.add(item);
                }
            itemSearchFeatureAdsAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            textViewFilterText.setText(filtertext.getString("search_query"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(featureAdsList.size() > 0 || searchedAdList.size() > 0) {
            emptyLayout.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
        else{
            emptyLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        if(featureAdsList.size() > 0) {
            featuredLayout.setVisibility(View.VISIBLE);
        }
        else featuredLayout.setVisibility(View.GONE);
    }

    private void loadMoreLatestProducts(int page) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();
            if (!myId.equals("")) {
                params.addProperty("catId", myId);
            }
            if (!title.equals("")) {
                params.addProperty("title", title);
            }
            if (isSort) {
                params.addProperty("sort", sort);
            }
            if (!ad_country.equals("")) {
                params.addProperty("ad_country", ad_country);
            }
            if (!nearby_latitude.equals("") && !nearby_longitude.equals("") && !nearby_distance.equals("0") && !nearby_distance.equals("")) {
                params.addProperty("nearby_latitude", nearby_latitude);
                params.addProperty("nearby_longitude", nearby_longitude);
                params.addProperty("nearby_distance", nearby_distance);
            }
            if(!city.equals("")){
                params.addProperty("city", city);
            }
            if (!ad_cats1.equals("")) {
                params.addProperty("ad_cats1", ad_cats1);
            }
            lastSentParamas = params;
            params.addProperty("page_number", page);

            Call<ResponseBody> myCall = restService.postMoreProductsBySearch(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        hideMoreLoading();
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {

                                    JSONArray data = response.getJSONArray("products");
                                    Log.d("latest products", data.toString());
                                    for (int i = 0; i < data.length(); i++) {
                                        ProductDetails item = new ProductDetails();
                                        item.setData(data.optJSONObject(i));
                                        searchedAdList.add(item);
                                    }

                                    productAdapter.loadMore(searchedAdList);


                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideMoreLoading();
                    Log.d("info HomeGet error", String.valueOf(t));
//                    Timber.d(String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            hideMoreLoading();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();

        featureAdsList.clear();
        searchedAdList.clear();

        nextPage = 1;
        currentPage = 1;
        totalPage = 0;
    }

    private void showMoreLoading() {
        progressBar = mView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideMoreLoading() {
        progressBar = mView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}