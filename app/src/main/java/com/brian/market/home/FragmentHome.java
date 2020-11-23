package com.brian.market.home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.brian.market.ad_detail.full_screen_image.FullScreenViewActivity;
import com.brian.market.helper.WorkaroundMapFragment;
import com.brian.market.utills.GPSTracker;
import com.brian.market.utills.RuntimePermissionHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.ViewCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

import com.brian.market.Notification.Config;
import com.brian.market.R;
import com.brian.market.Search.FragmentCatSubNSearch;
import com.brian.market.Search.SearchActivity;
import com.brian.market.ad_detail.Ad_detail_activity;
import com.brian.market.adapters.ProductAdapter;
import com.brian.market.adapters.ItemSearchFeatureAdsAdapter;
import com.brian.market.helper.CatSubCatOnclicklinstener;
import com.brian.market.helper.GridSpacingItemDecoration;
import com.brian.market.helper.MyProductOnclicklinstener;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.home.adapter.ItemMainAllCatAdapter;
import com.brian.market.home.adapter.ItemMainAllLocationPoPUpHome;
import com.brian.market.home.adapter.ItemMainCAT_Related_All;
import com.brian.market.modelsList.blogModel;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.modelsList.homeCatListModel;
import com.brian.market.modelsList.homeCatRelatedList;
import com.brian.market.modelsList.myAdsModel;
import com.brian.market.modelsList.subcatDiloglist;
import com.brian.market.utills.CustomBorderDrawable;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

public class FragmentHome extends Fragment {

    static int adsCounter = 0;
    private View mView;
    public JSONObject jsonObjectSubMenu, responseData;
    static Boolean Ad_post = false;// for test
    String regId, locationIDd, locationIdHomePOpup, locationIdHomePOpupName, imageViewLocation;
    static String Verfiedmessage;
    ArrayList<homeCatListModel> listitems = new ArrayList<>();
    ArrayList<homeCatListModel> locationAdscat = new ArrayList<>();
    ArrayList<homeCatRelatedList> listitemsRelated = new ArrayList<>();
    ArrayList<ProductDetails> featureAdsList = new ArrayList<>();
    ArrayList<blogModel> blogsArrayList = new ArrayList<>();
    ItemSearchFeatureAdsAdapter itemFeatureAdsAdapter;
    int[] iconsId = {R.drawable.ic_pages, R.drawable.ic_help_outline_black_24dp, R.drawable.ic_about_black_24dp, R.drawable.ic_file};
    LinearLayout featureAboveLayoyut, featurebelowLayoyut, featuredMidLayout, latestLayout;
    Menu menu;
    View viw;
    RestService restService;
    TextView textViewTitleFeature, textViewTitleFeatureBelow, textViewTitleFeatureMid;
    CardView catCardView;
    LinearLayout HomeCustomLayout, staticSlider, emptyLayout;
    TextView tv_search_title, tv_search_subTitle;
    EditText et_search, searchViewLcoation;
    TextView et_location;
    Boolean dikhao = false;
    ImageButton img_location;
    ImageButton img_btn_search;
    RelativeLayout searchLayout;
    ImageView backgroundImage;
    Button buttonAllCat;
    static boolean title_Nav;
    private SettingsMain settingsMain;
    private ArrayList<ProductDetails> latesetAdsList = new ArrayList<>();
    private ArrayList<ProductDetails> nearByAdsList = new ArrayList<>();
    List<String> spinnerItems, spinnerID;
    ListView listView;
    private RecyclerView mRecyclerView, mRecyclerView2, featuredRecylerViewAbove, featuredRecylerViewBelow,
            featuredRecylerViewMid, latestRecyclerView;
    private NestedScrollView scrollView;
    private Context context;
    final ArrayList<subcatDiloglist> listitems12 = new ArrayList<>();
    public static View locationFragmentView;
    public static AlertDialog locationDialog;
    public String calledfrom;
    //    FirebaseDatabase database;
//    DatabaseReference myRef;
    private String btnViewAllText;
    private static LayoutInflater inflater = null;
    boolean back_pressed = false;
    ItemMainAllLocationPoPUpHome ItemMainAllLocationPoPUpHome;
    protected GoogleMap mMap;
    RuntimePermissionHelper runtimePermissionHelper;
    private String address;
    private TextView myAddress;

    BannerSlider bannerSlider;
    List<Banner> banners = new ArrayList<>();
    ArrayList<String> imageUrls = new ArrayList<>();

    public FragmentHome() {
    }

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    public void adforest_recylerview_autoScroll(final int duration, final int pixelsToMove, final int delayMillis,
                                                final RecyclerView recyclerView, final GridLayoutManager gridLayoutManager
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    public static void setData(Boolean title_nav) {
        FragmentHome.title_Nav = title_nav;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getView().setFocusableInTouchMode(true);
        this.getView().requestFocus();
        this.getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

//This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                        backPressed();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_main_home, container, false);

//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("UserLogin");
        context = getActivity();
        settingsMain = new SettingsMain(getActivity());

        NavigationView navigationView = this.getActivity().findViewById(R.id.nav_view);

        // get menu from navigationView
        menu = navigationView.getMenu();

        initComponents();

        initListeners();

        if (getArguments() != null) {
//            locationIDd = getArguments().getString("location_id");
//            imageViewLocation =getArguments().getString("location_img");
            locationIdHomePOpup = getArguments().getString("location_id");
            locationIdHomePOpupName = getArguments().getString("location_name");
            et_location.setText(locationIdHomePOpupName);
            et_location.setTextColor(Color.BLACK);
//            Toast.makeText(context, locationIdHomePOpup, Toast.LENGTH_SHORT).show();
        }

        if (settingsMain.getRTL()) {
            Drawable drawable = getResources().getDrawable(R.drawable.bg_home_search_clickrtl).mutate();
            drawable.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_ATOP);
            img_btn_search.setBackground(drawable);

        } else {

            Drawable drawable = getResources().getDrawable(R.drawable.bg_home_search_click).mutate();
            drawable.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_ATOP);

            img_btn_search.setBackground(drawable);
        }

        restService = UrlController.createService(RestService.class);

        ((HomeActivity) getActivity()).changeImage();

        SharedPreferences pref = getActivity().getSharedPreferences(Config.SHARED_PREF, 0);

        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(true);

        getActivity().setTitle(getResources().getString(R.string.app_name));


        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            Double latitude = gps.getLatitude();
            Double longitude = gps.getLongitude();

            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                address = addresses.get(0).getAddressLine(0);

                adforest_getAllData();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else gps.showSettingsAlert();

        return mView;

    }

    private void initListeners() {
        et_search.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("catId", "");
                intent.putExtra("ad_title", et_search.getText().toString());
                intent.putExtra("requestFrom", "Home");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
            return false;
        });
        et_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (locationFragmentView != null) {
                    locationDialog.dismiss();
                    locationFragmentView = inflater.inflate(R.layout.locationpopup, null);
                }
                locationFragmentView = LayoutInflater.from(context).inflate(R.layout.locationpopup, null);
                builder.setView(locationFragmentView);

                locationDialog = builder.create();
                locationDialog.show();
                FragmentAllLocationsPopUpHome allCategories = (FragmentAllLocationsPopUpHome) getFragmentManager().findFragmentById(R.id.locationSubCatFragment);
                allCategories.getArgs();
                return;
            }

        });

        img_btn_search.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SearchActivity.class);
            intent.putExtra("catId", "");
            intent.putExtra("ad_title", et_search.getText().toString());
            intent.putExtra("requestFrom", "Home");
            intent.putExtra("ad_country", locationIdHomePOpup);
            startActivity(intent);
            Log.d("info Search intent Home", intent.toString());
            getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
        });

        buttonAllCat.setOnClickListener(v -> {
            FragmentAllCategories fragmentAllCategories = new FragmentAllCategories();
            replaceFragment(fragmentAllCategories, "FragmentAllCategories");
        });

//        bannerSlider.setOnBannerClickListener(position -> {
//            if (banners.size() > 0) {
//
//                Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
//                i.putExtra("imageUrls", imageUrls);
//                i.putExtra("position", position);
//                startActivity(i);
//            }
//        });
    }

    private void initComponents() {
        mRecyclerView = mView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);

        featureAboveLayoyut = mView.findViewById(R.id.featureAboveLayoyut);
        featurebelowLayoyut = mView.findViewById(R.id.featureAboveLayoutBelow);
        featuredMidLayout = mView.findViewById(R.id.featureLayoutMid);
        latestLayout = mView.findViewById(R.id.latestLayout);
        emptyLayout = mView.findViewById(R.id.empty_view);
        textViewTitleFeature = mView.findViewById(R.id.textView6);
        textViewTitleFeatureBelow = mView.findViewById(R.id.textView7);
        textViewTitleFeatureMid = mView.findViewById(R.id.textView8);
        featuredRecylerViewAbove = mView.findViewById(R.id.recycler_view3);
        featuredRecylerViewBelow = mView.findViewById(R.id.featuredRecylerViewBelow);
        featuredRecylerViewMid = mView.findViewById(R.id.featuredRecylerViewMid);
        latestRecyclerView = mView.findViewById(R.id.latestRecyclerView);
        scrollView = mView.findViewById(R.id.scrollView);
        catCardView = mView.findViewById(R.id.card_view);
        buttonAllCat = mView.findViewById(R.id.buttonAllCat);
        buttonAllCat.setBackground(CustomBorderDrawable.customButton(6, 6, 6, 6, SettingsMain.getMainColor(), SettingsMain.getMainColor(), SettingsMain.getMainColor(), 3));

        HomeCustomLayout = mView.findViewById(R.id.HomeCustomLayout);
        staticSlider = mView.findViewById(R.id.linear1);
        mRecyclerView2 = mView.findViewById(R.id.recycler_view2);
        viw = mView.findViewById(R.id.viw);
        tv_search_title = mView.findViewById(R.id.tv_search_title);
        tv_search_subTitle = mView.findViewById(R.id.tv_search_subTitle);
        et_location = mView.findViewById(R.id.et_location);
//        img_location  =view.findViewById(R.id.img_location);
        et_search = mView.findViewById(R.id.et_search);
        img_btn_search = mView.findViewById(R.id.img_btn_search);
        searchLayout = mView.findViewById(R.id.searchLayout);
        backgroundImage = mView.findViewById(R.id.backgroundImage);

        myAddress = mView.findViewById(R.id.my_location);

        bannerSlider = mView.findViewById(R.id.banner_slider);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null) {
                Uri s = data.getData();

                et_location.setText(s.toString());

            }
        }
    }

    //    public void showLocationCategoriesDialog(){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//
//        FragmentAllCategories allCategories2 = (FragmentAllCategories) getFragmentManager().findFragmentById(R.id.locationSubCatFragment);
//        Intent intent=new Intent(getContext(),SearchLocationLatLongActivity.class);
//        startActivity(intent);
//
////        if (allCategories2!=null) {
////// FragmentTransaction trans = getFragmentManager().beginTransaction();
////// trans.remove(allCategories2);
////// trans.commit();
////// getFragmentManager().popBackStack();a
////
////        }
//
////        if (locationFragmentView!=null){
////
////            builder.setView(locationFragmentView);
////            locationCatDialog = builder.create();
////            locationCatDialog.show();
////            FragmentAllCategories allCategories = (FragmentAllCategories) getFragmentManager().findFragmentById(R.id.locationSubCatFragment);
////            allCategories.getArgs(catId);
////            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
////            return;
////        }
//        locationFragmentView = LayoutInflater.from(context).inflate(R.layout.news_articles, null);
//        builder.setView(locationFragmentView);
//
//        locationCatDialog = builder.create();
//        locationCatDialog.show();
////        FragmentAllCategories allCategories = (FragmentAllCategories) getFragmentManager().findFragmentById(R.id.locationSubCatFragment);
////        allCategories.getArgs(catId);
//        Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
//
//        return;
//    }

    private void backPressed() {
        if (!back_pressed) {
            Toast.makeText(getContext(), "Press Again To Exit", Toast.LENGTH_SHORT).show();
            back_pressed = true;
            android.os.Handler mHandler = new android.os.Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2000L);
        } else {
            ViewDialog viewDialog = new ViewDialog();
            viewDialog.showDialog(getActivity(), "Are you sure you want to exit?");
//            this.finishAffinity();
        }
    }

    //category not paid dialog
    public class ViewDialog {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.popup_delete);
//            ImageView imageView = dialog.findViewById(R.id.a);
//            Glide.with(getContext()).load(R.drawable.angry).into(imageView);
            TextView text = (TextView) dialog.findViewById(R.id.txt_delete_job);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_confirm);
            dialogButton.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finishAffinity();
                    dialog.dismiss();
                }
            });
            Button dialogButtonCancel = (Button) dialog.findViewById(R.id.btn_close);
            dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

    private void adforest_getAllData() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            JsonObject object = new JsonObject();
            object.addProperty("address", address);
            Call<ResponseBody> myCall = restService.postHomeDetails(object, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    SettingsMain.hideDilog();
                    try {
                        Log.d("info HomeGet Responce", "" + responseObj.toString());

                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                responseData = response.getJSONObject("data");
                                Log.d("home data", responseData.toString());
                                HomeActivity.checkLoading = false;

                                if (!settingsMain.getAppOpen()) {
                                    menu.findItem(R.id.message).setVisible(false);
                                    menu.findItem(R.id.profile).setVisible(false);
                                    menu.findItem(R.id.withdraw).setVisible(false);
                                    menu.findItem(R.id.myProducts).setVisible(false);
                                    menu.findItem(R.id.myOrders).setVisible(false);
                                    menu.findItem(R.id.myAuction).setVisible(false);
                                    menu.findItem(R.id.cards).setVisible(false);

                                    menu.findItem(R.id.nav_log_out).setVisible(false);
                                    menu.findItem(R.id.coin_deposit).setVisible(false);
                                    menu.findItem(R.id.nav_log_in).setVisible(true);
                                }

                                settingsMain.setKey("stripeKey", responseData.getString("stripe_key"));
                                address = responseData.getString("my_location");
                                myAddress.setText(address);
                                if (responseData.getJSONArray("cat_icons").length() == 0) {
                                    catCardView.setVisibility(View.GONE);
                                } else {
                                    adforest_setAllCatgories(responseData.getJSONArray("cat_icons"),
                                            responseData.getInt("cat_icons_column"), mRecyclerView);
                                    catCardView.setVisibility(View.VISIBLE);
                                }

                                ArrayList<String> currencies = new ArrayList<String>();
                                currencies.add("Select currency");
                                JSONArray currency_data = responseData.getJSONArray("currencies");
                                for (int i = 0; i < currency_data.length(); i++) {
                                    currencies.add(currency_data.optJSONObject(i).optString("currency"));
                                }

                                settingsMain.setCurrencies(currencies);

                                if (responseData.getJSONArray("sliders").length() > 0) {
                                    staticSlider.setVisibility(View.VISIBLE);
                                    adforest_setAllRelated(responseData.getJSONArray("sliders"), mRecyclerView2);
                                }

                                if (responseData.getBoolean("is_show_featured")) {
                                    JSONObject featuredObject = responseData.getJSONObject("featured");
                                    String featuredPosition = "2";//responseData.getString("featured_position");

                                    switch (featuredPosition) {
                                        case "1":
                                            Log.e("infoi", "asdasdasdsa" + featuredPosition);
                                            featureAboveLayoyut.setVisibility(View.VISIBLE);
                                            adforest_setAllFeaturedAds(featuredObject, featuredRecylerViewAbove, textViewTitleFeature);
                                            break;
                                        case "2":
                                            Log.e("infoi", "asdasdasdsa" + featuredPosition);

                                            featuredMidLayout.setVisibility(View.VISIBLE);
                                            adforest_setAllFeaturedAds(featuredObject, featuredRecylerViewMid, textViewTitleFeatureMid);
                                            break;
                                        case "3":
                                            Log.e("infoi", "asdasdasdsa" + featuredPosition);
                                            featurebelowLayoyut.setVisibility(View.VISIBLE);
                                            adforest_setAllFeaturedAds(featuredObject, featuredRecylerViewBelow, textViewTitleFeatureBelow);
                                            break;
                                    }
                                }

                                if (responseData.getBoolean("is_show_latest")) {
                                    latestLayout.setVisibility(View.VISIBLE);
                                    adforest_setAllLatesetAds(responseData.getJSONObject("latest_ads"), latestRecyclerView, "latest");
                                }

                                if(responseData.getBoolean("is_show_latest") || responseData.getBoolean("is_show_featured")) {
                                    scrollView.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);
                                }
                                else {
                                    emptyLayout.setVisibility(View.VISIBLE);
                                    scrollView.setVisibility(View.GONE);
                                }

                                banners.clear();
                                imageUrls.clear();
//                                if(bannerSlider != null) bannerSlider.removeAllBanners();
                                for (int i = 0; i < responseData.getJSONArray("banners").length(); i++) {
                                    String path = responseData.getJSONArray("banners").getJSONObject(i).getString("image");
                                    if(!path.startsWith("http"))
                                        path = UrlController.IP_ADDRESS + "storage/" + path;
                                    banners.add(new RemoteBanner(path));
                                    imageUrls.add(path);
                                    banners.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
                                }

                                if (banners.size() > 0)
                                    bannerSlider.setBanners(banners);

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
                    SettingsMain.hideDilog();
                    Log.d("info HomeGet error", String.valueOf(t));
//                    Timber.d(String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_setAllFeaturedAds(JSONObject featureObject, RecyclerView featuredRecylerView,
                                            TextView textViewTitleFeature) {
        featureAdsList.clear();

        GridLayoutManager MyLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);

        featuredRecylerView.setHasFixedSize(true);
        featuredRecylerView.setNestedScrollingEnabled(false);
        featuredRecylerView.setLayoutManager(MyLayoutManager2);

        ViewCompat.setNestedScrollingEnabled(featuredRecylerView, false);

        try {

//            textViewTitleFeature.setText(featureObject.getString("text"));
            if (featureObject.getJSONArray("products").length() > 0)
                for (int i = 0; i < featureObject.getJSONArray("products").length(); i++) {

                    ProductDetails item = new ProductDetails();
                    JSONObject object = featureObject.getJSONArray("products").getJSONObject(i);
                    item.setData(object);
                    featureAdsList.add(item);
                }
            itemFeatureAdsAdapter = new ItemSearchFeatureAdsAdapter(getActivity(), featureAdsList);

            featuredRecylerView.setAdapter(itemFeatureAdsAdapter);


            itemFeatureAdsAdapter.setOnItemClickListener(new OnItemClickListener2() {
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
            if (settingsMain.isFeaturedScrollEnable()) {
                adforest_recylerview_autoScroll(settingsMain.getFeaturedScroolDuration(),
                        40, settingsMain.getFeaturedScroolLoop(),
                        featuredRecylerView, MyLayoutManager2, itemFeatureAdsAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void adforest_setAllCatgories(JSONArray jsonArray, int noOfCol, RecyclerView recyclerView) {
        listitems = new ArrayList<>();
        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select category");
        for (int i = 0; i < jsonArray.length(); i++) {
            homeCatListModel item = new homeCatListModel();
            item.setTitle(jsonArray.optJSONObject(i).optString("cat_name"));
            item.setThumbnail(jsonArray.optJSONObject(i).optString("cat_img"));
            item.setId(jsonArray.optJSONObject(i).optString("id"));
            item.setData(jsonArray.optJSONObject(i));
            listitems.add(item);
            categories.add(jsonArray.optJSONObject(i).optString("cat_name"));
        }

        settingsMain.setCategories(categories);

        GridLayoutManager MyLayoutManager = new GridLayoutManager(getActivity(), noOfCol);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setLayoutManager(MyLayoutManager);
        int spacing = 0; // 50px

        recyclerView.addItemDecoration(new GridSpacingItemDecoration(noOfCol, spacing, false));
        ItemMainAllCatAdapter adapter = new ItemMainAllCatAdapter(context, listitems, noOfCol);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(item -> {
//            Toast.makeText(getContext(), item.getTitle(), Toast.LENGTH_LONG).show();
            FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
            Bundle bundle = new Bundle();
            bundle.putString("id", item.getId());
            bundle.putString("title", "");
//            bundle.putString("city", address);
            fragment_search.setArguments(bundle);
            replaceFragment(fragment_search, "FragmentCatSubNSearch");

        });
//        adapter.setOnItemClickListener(item -> {
//            Intent intent = new Intent(getContext(), SearchActivity.class);
//            intent.putExtra("catId",item.getId());
//            intent.putExtra("title","");
//            intent.putExtra("requestFrom", "Home");
//
//            startActivity(intent);
//            getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//
//        });
    }

    private void adforest_setAllRelated(JSONArray jsonArray, RecyclerView recyclerView) {

        listitemsRelated.clear();

        GridLayoutManager MyLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager2.setOrientation(RecyclerView.VERTICAL);

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerView.setLayoutManager(MyLayoutManager2);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        Log.d("data array", "" + jsonArray.length());
        for (int each = 0; each < jsonArray.length(); each++) {
            homeCatRelatedList relateItem = new homeCatRelatedList();
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(each);

                relateItem.setTitle(jsonObject.getString("name"));
                relateItem.setViewAllBtnText(btnViewAllText);
                relateItem.setCatId(jsonObject.getString("cat_id"));
                JSONArray innerList = jsonObject.getJSONArray("data");

                ArrayList<ProductDetails> list = new ArrayList<>();

                for (int i = 0; i < innerList.length(); i++) {
                    ProductDetails item = new ProductDetails();
                    item.setData(jsonObject);
                    list.add(item);
                }

                relateItem.setArrayList(list);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            listitemsRelated.add(relateItem);
        }


        ItemMainCAT_Related_All itemMainCAT_related_all = new ItemMainCAT_Related_All(context, listitemsRelated);
        recyclerView.setAdapter(itemMainCAT_related_all);

        itemMainCAT_related_all.setOnItemClickListener(new MyProductOnclicklinstener() {


            @Override
            public void onItemClick(ProductDetails item) {

            }

            @Override
            public void delViewOnClick(View v, int position) {
                FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
                Bundle bundle = new Bundle();
                bundle.putString("id", v.getTag().toString());
                bundle.putString("title", "");

                fragment_search.setArguments(bundle);
                replaceFragment(fragment_search, "FragmentCatSubNSearch");
            }

        });

    }

    private void adforest_setAllLatesetAds(JSONObject jsonObject, RecyclerView recyclerView, final String checkAdsType) {

        latesetAdsList.clear();

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        try {
            JSONObject object = jsonObject;

            JSONArray data = object.getJSONArray("products");
            Log.d("latest products", data.toString());
            for (int i = 0; i < data.length(); i++) {
                ProductDetails item = new ProductDetails();
                item.setData(data.optJSONObject(i));
                latesetAdsList.add(item);
            }

            ProductAdapter adapter = new ProductAdapter(getActivity(), latesetAdsList);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener2() {
                @Override
                public void onItemClick(ProductDetails item) {
                    Log.d("item_id", item.getId()+"");
                    Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                    intent.putExtra("adId", ""+item.getId());
                    startActivity(intent);
                }

                @Override
                public void onRemoveFav(int position) {

                }

                @Override
                public void onShare(ProductDetails item) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, item.getCardName());
                    i.putExtra(Intent.EXTRA_TEXT, "link");
                    startActivity(Intent.createChooser(i, "Share Product"));
                }

                @Override
                public void onAddCart(ProductDetails item) {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
