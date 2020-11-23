package com.brian.market.ad_detail;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.cart.CartFragment;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.helper.WorkaroundMapFragment;
import com.brian.market.messages.ChatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;


import com.brian.market.order.MyOrderActivity;
import com.brian.market.home.AddNewProductPost;
import com.brian.market.profile.MyProductActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import cn.iwgang.countdownview.CountdownView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.brian.market.R;
import com.brian.market.ad_detail.full_screen_image.FullScreenViewActivity;
import com.brian.market.adapters.ProductAdapter;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.home.EditAdPost;
import com.brian.market.home.HomeActivity;
import com.brian.market.modelsList.blogCommentsModel;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.modelsList.myAdsModel;
import com.brian.market.public_profile.FragmentPublic_Profile;
import com.brian.market.utills.CustomBorderDrawable;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.squareup.picasso.Picasso;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class FragmentAdDetail extends Fragment implements Serializable, RuntimePermissionHelper.permissionInterface, OnMapReadyCallback {
    public static String myId, myBookId;

    Dialog dialog;
    RelativeLayout relativeLayoutFeature;
    SettingsMain settingsMain;
    myAdsModel item;
    TextView textViewAdName, textViewLocation, textViewAddress, textViewSeen, textViewDate, textViewPrice, textViewLastLogin, textViewDescrition, tvShippingPrice;
    TextView shareBtn, addToFavBtn, reportBtn, verifyBtn, textViewRateNo, textViewUserName, textViewRelated, textViewDescript;
    TextView messageBtn, callBtn, bookBtn, textViewNotify, getDirectionBtn, textViewFeatured, makeFeatureBtn, featuredText, bidStatisticsBtn;
    HtmlTextView htmlTextView;
    LinearLayout linearLayout2, linearLayoutOuter;
    RatingBar ratingBar;
    ImageView imageViewProfile, soldImage;
    BannerSlider bannerSlider;
    List<Banner> banners;
    ArrayList<String> imageUrls;
    JSONObject  jsonObjectReport,
            jsonObjectShareInfo, jsonObjectRatingInfo, blockUserObject, JsonObjectData;
    RecyclerView mRecyclerView, ratingRecylerView;
    View temphide;
    LinearLayout linearLayout, linearLayoutSubmitRating, ratingLoadLayout;
    int noOfCol = 2;
    CardView cardViewBidSec, cardViewRating;
    TextView textViewTotBid, textViewHighBid, textViewLowBid, textViewTotBidtext, textViewHighBidtext, textViewLowBidtext, textViewRatingTitle,
            textViewRatingNotEdit, textViewRatingButton, textViewNoCurrentRating, textViewRatingTitleTop, ratingLoadMoreButton,
            tv_adType, tv_blockUser, editAdd;
    RestService restService;
    NestedScrollView nestedScroll;
    EditText editTextRattingComment;
    SimpleRatingBar simpleRatingBar;
    boolean LoadMoreDialogOpen = false;
    Dialog loadMoreRating;
    CountdownView countDown;
    //    FrameLayout youtube_view;
    FrameLayout youtube_view;
    RuntimePermissionHelper runtimePermissionHelper;
    Dialog callDialog;
    private ArrayList<ProductDetails> list = new ArrayList<>();
    private ArrayList<blogCommentsModel> listitems = new ArrayList<>();
    private String phoneNumber, maskedPhoneNumber, maskedService;
    private String adAuthorId;
    private String unitPrice;
    private JSONObject jsonObjectStaticText;
    private double latitude = 0.0;
    private double longitude = 0.0;
    String detailType;
    Boolean ratinghideshow;
    RelativeLayout reactions;
    LinearLayout bookBtnGroupLayout, add_cart_layout;
    View mView;
    TextView btnBookEdit, btnBookCancel;
    CardView cardBookContent;
    TextView tvBookTerm, tvBookTotalPrice, tvBookCancelPolicy, tvAmenties;
    String bookFrom, bookTo, bookCustomer;
    
    LinearLayout postBtnGroupLayout, shipping_layout;
    TextView btnPostEdit, btnPostDelete;
    private String topic;
    protected GoogleMap mMap;
    ProductDetails product;

    public FragmentAdDetail() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_ad_detail, container, false);
        settingsMain = new SettingsMain(getActivity());

        final Bundle bundle = this.getArguments();
        if (bundle != null) {
            myId = bundle.getString("id", "0");
            myBookId = bundle.getString("book_id");
            detailType = bundle.getString("detail_type");
        }

        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);

        initComponents();

        initListeners();

        checkDetailType();
        mRecyclerView = mView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager MyLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(MyLayoutManager2);

//        SwipeRefreshLayout swipeRefreshLayout = mView.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(() -> adforest_recreateAdDetail());

        adforest_getAllData(myId,true);

//        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getFragmentManager()
////                .findFragmentById(R.id.map));
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        NestedScrollView mScrollView = (NestedScrollView) mView.findViewById(R.id.scrollViewUp);
        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.setListener(() -> mScrollView.requestDisallowInterceptTouchEvent(true));
        mapFragment.getMapAsync(this);

        return mView;
    }

    private void checkDetailType() {
        if(detailType != null) {
            restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
            switch (detailType) {
                case "booking_upcoming":
                    bookBtn.setVisibility(View.GONE);
                    bookBtnGroupLayout.setVisibility(View.VISIBLE);
                    cardBookContent.setVisibility(View.VISIBLE);
                    break;
                case "post_edit":
                    bookBtn.setVisibility(View.GONE);
                    postBtnGroupLayout.setVisibility(View.VISIBLE);
                    add_cart_layout.setVisibility(View.GONE);
                    callBtn.setVisibility(View.GONE);
                    messageBtn.setVisibility(View.GONE);
                    break;
            }
        }
        else{
            restService = UrlController.createService(RestService.class);
        }
    }

    private void initListeners() {
        editAdd.setOnClickListener(view1 -> {

            Intent intent = new Intent(getContext(), EditAdPost.class);
            intent.putExtra("id", myId);
            startActivity(intent);

        });

        textViewRatingButton.setOnClickListener(v -> {
            if (!editTextRattingComment.getText().toString().isEmpty()) {
                JsonObject params = new JsonObject();
                params.addProperty("ad_id", myId);
                params.addProperty("rating", simpleRatingBar.getRating());
                params.addProperty("rating_comments", editTextRattingComment.getText().toString());
                adforest_postRating(params);
            }
            if (editTextRattingComment.getText().toString().isEmpty()) {
                editTextRattingComment.setError("");
            }
        });



        if (messageBtn != null)
            messageBtn.setOnClickListener(view12 -> {
                if (!settingsMain.getAppOpen()) {
                    Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();

                } else {
//                        if (jsonObjectStaticText.getString("send_msg_btn_type").equals("receive")) {
//                    Intent intent = new Intent(getActivity(), Message.class);
//                    intent.putExtra("receive", true);
//                    startActivity(intent);
//                    getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                        } else
                            adforest_showDilogMessage();
//                    Intent intent = new Intent(getActivity(), ChatActivity.class);
//                    intent.putExtra("adId", myId);
//                    intent.putExtra("senderId", "1");
//                    intent.putExtra("recieverId", "2");
////                    intent.putExtra("type", item.getType());
//                    startActivity(intent);
                }
            });
        if (callBtn != null)

            callBtn.setOnClickListener(view13 -> {
                if (!settingsMain.getAppOpen()) {
                    Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    if (SettingsMain.isConnectingToInternet(getActivity())) {

                        JsonObject params = new JsonObject();
                        params.addProperty("mobile", phoneNumber);

                        Log.d("info send AdDetails", "" + params.toString());

                        Call<ResponseBody> myCall = restService.maskedNumber(params, UrlController.AddHeaders(getActivity()));
                        myCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                try {
                                    Log.d("info masked mobile ", "" + responseObj.toString());
                                    if (responseObj.isSuccessful()) {

                                        JSONObject response = new JSONObject(responseObj.body().string());

                                        if (response.getBoolean("success")) {
                                            maskedPhoneNumber = response.getString("phone");
                                            maskedService = response.getString("service");

                                            adforest_showDilogCall();
                                        } else {
                                            Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                    }
                                    SettingsMain.hideDilog();
                                } catch (JSONException e) {
                                    SettingsMain.hideDilog();
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    SettingsMain.hideDilog();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                SettingsMain.hideDilog();
                                Log.d("info AdDetails error", String.valueOf(t));
                                Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                            }
                        });

                    } else {
                        SettingsMain.hideDilog();
                        Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                }
            });

        bookBtn.setOnClickListener(view14 -> {

//            Biding_ViewPagerFragment fragment = new Biding_ViewPagerFragment();
//            buttonPress = "bidButton";
//            replaceFragment(fragment, "Biding_ViewPagerFragment");
            if(settingsMain.getAppOpen()) {
//                Intent intent = new Intent(getActivity(), OrderActivity.class);
//                intent.putExtra("post_id", myId);
//                intent.putExtra("post_title", textViewAdName.getText());
//                intent.putExtra("post_price", unitPrice);
//                startActivity(intent);
            }
            else {
                Toast.makeText(getContext(), "Please login...", Toast.LENGTH_SHORT).show();
            }
        });

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentAdDetail.this.goToPublicProfile();
            }
        });

        reportBtn.setOnClickListener(view15 -> {
            try {
                if (jsonObjectStaticText.getString("send_msg_btn_type").equals("receive")) {
                    Toast.makeText(getActivity(), JsonObjectData.getString("cant_report_txt"), Toast.LENGTH_SHORT).show();
                } else {
                    if (settingsMain.getAppOpen()) {
                        Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();

                    } else {
                        adforest_showDilogReport();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        textViewUserName.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                goToPublicProfile();
            }
            return true;
        });



        shareBtn.setOnClickListener(view16 -> {
            try {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, jsonObjectShareInfo.getString("title"));
                i.putExtra(Intent.EXTRA_TEXT, jsonObjectShareInfo.getString("link"));
                startActivity(Intent.createChooser(i, jsonObjectShareInfo.getString("text")));
            } catch (Exception e) {
                //e.toString();
            }
        });

        getDirectionBtn.setOnClickListener(view17 -> {

            String strUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + textViewLocation.getText().toString() + ")";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));
            getActivity().startActivity(intent);

            Log.d("info data object", longitude + "  ===   " + latitude);


        });

        makeFeatureBtn.setOnClickListener(view18 -> {
            if (settingsMain.getAppOpen()) {
                Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();

            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(settingsMain.getAlertDialogTitle("info"));
                alert.setCancelable(false);
                alert.setMessage(settingsMain.getAlertDialogMessage("confirmMessage"));
                alert.setPositiveButton(settingsMain.getAlertOkText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        adforest_makeFeature();

                        dialog.dismiss();
                    }
                });
                alert.setNegativeButton(settingsMain.getAlertCancelText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
            }
        });

        addToFavBtn.setOnClickListener(view19 -> {
            if (settingsMain.getAppOpen()) {
                Toast.makeText(getActivity(), settingsMain.getNoLoginMessage(), Toast.LENGTH_SHORT).show();
            } else {
                adforest_addToFavourite();
            }
        });
        bannerSlider.setOnBannerClickListener(position -> {
            if (banners.size() > 0) {

                Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
                i.putExtra("imageUrls", imageUrls);
                i.putExtra("position", position);
                startActivity(i);
            }
        });
        tv_blockUser.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                adforest_blockUserDialog();
            }
            return true;
        });

        btnBookEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), OrderActivity.class);
//                intent.putExtra("post_id", myId);
//                intent.putExtra("post_title", textViewAdName.getText());
//                intent.putExtra("post_price", unitPrice);
//                if(myBookId != null) {
//                    intent.putExtra("book_id", myBookId);
//                    intent.putExtra("book_from", bookFrom);
//                    intent.putExtra("book_to", bookTo);
//                    intent.putExtra("book_customer", bookCustomer);
//                }
//                startActivity(intent);
            }
        });

        btnBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getContext().getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Are you sure you want to to cancel this book?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestCancelBook();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        btnPostEdit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewProductPost.class);
                intent.putExtra("post_id", myId);
                startActivity(intent);
            }
        });

        btnPostDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getContext().getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Are you sure you want to to delete this product?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestDeletePost();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        add_cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CartFragment.checkCartHasProduct(product.getId())) {
                    User_Cart_DB user_cart_db = new User_Cart_DB();
                    user_cart_db.addCartItem(product);
                    product.setCustomersBasketQuantity(1);
                    product.setTotalPrice(product.getPrice());
                    v.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Added the product to cart successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(), "Added the product to cart already", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initComponents() {
        linearLayout = getActivity().findViewById(R.id.ll11);
        linearLayout.setVisibility(View.VISIBLE);

//        linearLayout.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        temphide = getActivity().findViewById(R.id.temphide);
        nestedScroll = mView.findViewById(R.id.scrollViewUp);
        getDirectionBtn = mView.findViewById(R.id.textView20);
        relativeLayoutFeature = mView.findViewById(R.id.relMakeFeature);
        textViewFeatured = mView.findViewById(R.id.relMakeFeatureTV);
        makeFeatureBtn = mView.findViewById(R.id.btnMakeFeat);
        featuredText = mView.findViewById(R.id.featuredText);

        textViewNotify = mView.findViewById(R.id.textView19);
        bannerSlider = mView.findViewById(R.id.banner_slider1);
        banners = new ArrayList<>();
        imageUrls = new ArrayList<>();

        messageBtn = getActivity().findViewById(R.id.message);
        callBtn = getActivity().findViewById(R.id.call);
        bookBtn = mView.findViewById(R.id.bidBtn);
        youtube_view = mView.findViewById(R.id.youtube_view);
        bidStatisticsBtn = mView.findViewById(R.id.bidStatisticsBtn);
        editAdd = mView.findViewById(R.id.editAdd);
        //book_upcoming
        bookBtnGroupLayout = mView.findViewById(R.id.book_btngroup_layout);
        btnBookEdit = mView.findViewById(R.id.btnBookEdit);
        btnBookCancel = mView.findViewById(R.id.btnBookCancel);
        cardBookContent = mView.findViewById(R.id.cardBookContent);
        tvBookCancelPolicy = mView.findViewById(R.id.book_cancel_policy);
        tvBookTerm = mView.findViewById(R.id.book_terms);
        tvBookTotalPrice = mView.findViewById(R.id.book_total_prices);
        ///////
        ///post_edit
        postBtnGroupLayout = mView.findViewById(R.id.post_btngroup_layout);
        btnPostDelete = mView.findViewById(R.id.btnPostCancel);
        btnPostEdit = mView.findViewById(R.id.btnPostEdit);
        ///////////

//        if (messageBtn != null)
//            messageBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
//        if (callBtn != null)
//            callBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        makeFeatureBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        bookBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        bidStatisticsBtn.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        editAdd.setBackground(CustomBorderDrawable.customButton(6, 6, 6, 6, SettingsMain.getMainColor(), SettingsMain.getMainColor(), SettingsMain.getMainColor(), 3));

        cardViewBidSec = mView.findViewById(R.id.card_view4);

        textViewTotBid = mView.findViewById(R.id.textView8);
        textViewTotBidtext = mView.findViewById(R.id.textView9);
        textViewHighBid = mView.findViewById(R.id.textView10);
        textViewHighBidtext = mView.findViewById(R.id.textView11);
        textViewLowBid = mView.findViewById(R.id.textView12);
        textViewLowBidtext = mView.findViewById(R.id.textView13);

        textViewAdName = mView.findViewById(R.id.text_view_name);
        textViewLocation = mView.findViewById(R.id.location);
        textViewAddress = mView.findViewById(R.id.address);
        textViewSeen = mView.findViewById(R.id.views);
        textViewDate = mView.findViewById(R.id.date);
        textViewPrice = mView.findViewById(R.id.prices);
        textViewLastLogin = mView.findViewById(R.id.loginTime);
        textViewDescrition = mView.findViewById(R.id.ad_description);
        shareBtn = mView.findViewById(R.id.share);
        addToFavBtn = mView.findViewById(R.id.addfav);
        reportBtn = mView.findViewById(R.id.report);
        verifyBtn = mView.findViewById(R.id.verified);
        textViewRateNo = mView.findViewById(R.id.numberOfRate);
        textViewUserName = mView.findViewById(R.id.text_viewName);
        textViewRelated = mView.findViewById(R.id.relatedText);
        htmlTextView = mView.findViewById(R.id.html_text);
        ratingBar = mView.findViewById(R.id.ratingBar);
        imageViewProfile = mView.findViewById(R.id.image_view);
        soldImage = mView.findViewById(R.id.image_sold);
        tv_adType = mView.findViewById(R.id.tv_adType);
        tv_blockUser = mView.findViewById(R.id.tv_block_user);

//        textViewPrice.setTextColor(Color.parseColor(SettingsMain.getMainColor()));

        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        linearLayoutOuter = mView.findViewById(R.id.ll1inner_location);
//        linearLayout1 = mView.findViewById(R.id.linearLayout1);
        linearLayout2 = mView.findViewById(R.id.customLayout1);
        textViewDescript = mView.findViewById(R.id.text_view_title);

        //Ratting Initialization
        cardViewRating = mView.findViewById(R.id.card_viewRating);
        textViewRatingTitle = mView.findViewById(R.id.ratingTitle);
        textViewRatingNotEdit = mView.findViewById(R.id.ratingNotEdit);
        editTextRattingComment = mView.findViewById(R.id.ratingEditText);
        textViewNoCurrentRating = mView.findViewById(R.id.noCurrentRatingText);
        linearLayoutSubmitRating = mView.findViewById(R.id.linearLayoutSubmitRating);
        textViewRatingTitleTop = mView.findViewById(R.id.sectionTitleRating);
        ratingRecylerView = mView.findViewById(R.id.ratingRecylerView);
        simpleRatingBar = mView.findViewById(R.id.ratingbarAds);
        ratingLoadLayout = mView.findViewById(R.id.ratingLoadLayout);
        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        ratingRecylerView.setLayoutManager(MyLayoutManager);
        ViewCompat.setNestedScrollingEnabled(ratingRecylerView, false);

        textViewRatingButton = mView.findViewById(R.id.rating_button);
        textViewRatingButton.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        ratingLoadMoreButton = mView.findViewById(R.id.ratingLoadMoreButton);
        ratingLoadMoreButton.setBackgroundColor(Color.parseColor(SettingsMain.getMainColor()));
        countDown = mView.findViewById(R.id.countDown);

        tvShippingPrice = mView.findViewById(R.id.shipping);
        shipping_layout = mView.findViewById(R.id.shipping_layout);
        add_cart_layout = mView.findViewById(R.id.ll_add_cart);

        tvAmenties = mView.findViewById(R.id.tv_ad_amenties);
    }

    private void adforest_getAllData(final String myId,Boolean isRejected) {
        this.myId = myId;
        nestedScroll.scrollTo(0, 0);
        youtube_view.setVisibility(View.GONE);
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("product_id", myId);
            if(myBookId != null){
                params.addProperty("book_id", myBookId);
            }
            params.addProperty("is_rejected",isRejected );

            Log.d("info send AdDetails", "" + params.toString());

            Call<ResponseBody> myCall = restService.getAdsDetail(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info AdDetails Respon", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info AdDetails object", "" + response.getJSONObject("data"));

                                JsonObjectData = response.getJSONObject("data");

                                adforest_setAllViewsText(JsonObjectData,
                                        JsonObjectData.getJSONObject("owner"));

                                ProductAdapter adapter = new ProductAdapter(getActivity(), list);
                                mRecyclerView.setAdapter(adapter);
                                adapter.setOnItemClickListener(new OnItemClickListener2() {
                                    @Override
                                    public void onItemClick(ProductDetails item) {
                                        Log.d("item_id", item.getId()+"");
                                        FragmentAdDetail.myId = item.getId()+"";
                                        adforest_recreateAdDetail();
                                        nestedScroll.scrollTo(0, 0);
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

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info AdDetails error", String.valueOf(t));
                    Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void setBookContent(JSONObject book) {
        try {
            tvBookTotalPrice.setText("$ " + book.getString("total"));

            tvBookCancelPolicy.setText(book.getString("cancel_policy"));
            bookFrom = book.getString("book_from");
            bookTo = book.getString("book_to");
            tvBookTerm.setText(bookFrom + " - " + bookTo);
            bookCustomer = book.getString("customer_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void requestCancelBook() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("book_id", myBookId);

            Log.d("info cancel book", "" + params.toString());

            Call<ResponseBody> myCall = restService.ordercancel(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info cancel book", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if(response.getBoolean("success")) {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), MyOrderActivity.class));
                            }
                            else {
//                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.d("cancel error", response.getString("message"));
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info AdDetails error", String.valueOf(t));
                    Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void requestDeletePost() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);

            Log.d("info remove ads", "" + params.toString());

            Call<ResponseBody> myCall = restService.deleteads(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info remove ads", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if(response.getBoolean("success")) {
                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getActivity(), MyProductActivity.class));
                            }
                            else {
//                                Toast.makeText(getContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                Log.d("cancel error", response.getString("message"));
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
                    SettingsMain.hideDilog();
                    Log.d("info AdDetails error", String.valueOf(t));
                    Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void adforest_setAllViewsText(final JSONObject data, JSONObject owner) {


        try {
            product = new ProductDetails();
            product.setData(data);

            if(product.isShipping()){
                add_cart_layout.setVisibility(View.VISIBLE);
            }
            else {
                add_cart_layout.setVisibility(View.GONE);
            }

            if(product.getQty() == 0) {
                add_cart_layout.setVisibility(View.GONE);
                soldImage.setVisibility(View.VISIBLE);
            }

            if (CartFragment.checkCartHasProduct(product.getId())) {
                add_cart_layout.setVisibility(View.GONE);
            }

            if(detailType !=null && detailType.equals("post_edit"))
                add_cart_layout.setVisibility(View.GONE);

            phoneNumber = owner.getString("country_code")+owner.getString("mobile");
            adAuthorId = owner.getString("id");

            textViewAdName.setText(product.getCardName());
            topic = product.getCardName();
            textViewLocation.setText(product.getLocation());
            textViewAddress.setText(product.getAddress());

            textViewDescrition.setText(product.getDescription());
            if(product.isShipping()) {
                tvShippingPrice.setText("$ "+data.getString("shipping_price"));
                shipping_layout.setVisibility(View.VISIBLE);
            }
            else shipping_layout.setVisibility(View.GONE);

//            if (data.getBoolean("is_feature")) {
//                featuredText.setVisibility(View.VISIBLE);
//                featuredText.setText(data.getString("is_feature_text"));
//                featuredText.setBackgroundColor(Color.parseColor("#E52D27"));
//            } else {
//                featuredText.setVisibility(View.GONE);
//            }
            if (!product.getLat().equals("") || !product.getLong().equals("")) {
                latitude = Double.parseDouble(product.getLat());
                longitude = Double.parseDouble(product.getLong());
                setMap();
                getDirectionBtn.setVisibility(View.VISIBLE);
            } else {
                getDirectionBtn.setVisibility(View.GONE);
            }

            unitPrice = product.getPrice();
            textViewPrice.setText(unitPrice + " " + product.getCurrency());
//            textViewDate.setText(data.getString("ad_date"));

            if (unitPrice.equals("")) {
                textViewPrice.setVisibility(View.GONE);
            } else {
                textViewPrice.setVisibility(View.VISIBLE);
            }

            banners.clear();
            imageUrls.clear();
            bannerSlider.removeAllBanners();

            for (int i = 0; i < data.getJSONArray("images").length(); i++) {
                String path = data.getJSONArray("images").getJSONObject(i).getString("thumb");
                if(!path.startsWith("http"))
                    path = UrlController.IP_ADDRESS + "storage/" + path;
                banners.add(new RemoteBanner(path));
                imageUrls.add(path);
                banners.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
            }

            if (banners.size() > 0)
                bannerSlider.setBanners(banners);
            linearLayoutOuter.removeAllViews();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        list.clear();
    }

    private void setMap() {
        if (mMap != null) {
            mMap.clear();
            LatLng location = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(location).title(product.getAddress()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
            MarkerOptions marker = new MarkerOptions()
                    .position(location)
                    .title(product.getAddress())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude)).zoom(16).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    2000, null);

        }
    }

    void showHideButtons(TextView btnShowHide) {
        if (btnShowHide.getParent() != null) {
            ((ViewGroup) btnShowHide.getParent()).removeView(btnShowHide);
            linearLayout.removeAllViewsInLayout();
        }
        linearLayout.addView(btnShowHide);
    }

    void adforest_showDilogMessage() {
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        final EditText message = dialog.findViewById(R.id.editText3);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        Send.setText("Send");
        message.setHint("Please type");
        Cancel.setText("Cancel");

        Send.setOnClickListener(v -> {

            if (!message.getText().toString().isEmpty()) {
                adforest_sendMessage(message.getText().toString());

                dialog.dismiss();
            } else
                message.setError("");
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("ResourceAsColor")
    void adforest_showDilogCall() {
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_call);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

//        Log.d("info call object", jsonObjectCallNow.toString() + phoneNumber);
        final TextView textViewCallNo = dialog.findViewById(R.id.textView2);
        textViewCallNo.setText(maskedPhoneNumber);
        final TextView verifiedOrNotText = dialog.findViewById(R.id.verifiedOrNotText);

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        Send.setText("Dial");
        Cancel.setText("Cancel");
        Send.setOnClickListener(v -> runtimePermissionHelper.requestCallPermission(1));

        Cancel.setOnClickListener(v -> dialog.dismiss());
        callDialog = dialog;
        dialog.show();
    }

    void adforest_showDilogReport() {
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_report);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        final Spinner spinner = dialog.findViewById(R.id.spinner);
        final EditText editText = dialog.findViewById(R.id.editText3);

        item = new myAdsModel();

        try {
            Send.setText(jsonObjectReport.getString("btn_send"));
            editText.setHint(jsonObjectReport.getString("input_textarea"));
            Cancel.setText(jsonObjectReport.getString("btn_cancel"));

            item.setSpinerValue(jsonObjectReport.getJSONObject("select").getJSONArray("name"));
            item.setSpinerData(jsonObjectReport.getJSONObject("select").getJSONArray("value"));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, item.getSpinerData());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Send.setOnClickListener(v -> {
            if (!editText.getText().toString().isEmpty()) {
                adforest_sendReport(item.getSpinerValue().get(spinner.getSelectedItemPosition()), editText.getText().toString());
                dialog.dismiss();
            } else {
                editText.setError("");
            }

        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    void adforest_sendReport(String type, String message) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            params.addProperty("option", type);
            params.addProperty("comments", message);
            Log.d("info sendReport Status", params.toString());

            Call<ResponseBody> myCall = restService.postSendReport(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info SendReport Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info SendReport Respon", "" + response.toString());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info SendReport error", String.valueOf(t));
                    Log.d("info SendReport error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_addToFavourite() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            Log.d("info sendFavourite", myId);
            Call<ResponseBody> myCall = restService.postAddToFavourite(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info AdToFav Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }

                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info AdToFav error", String.valueOf(t));
                    Log.d("info AdToFav error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_makeFeature() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            Log.d("info makeFeature", myId);
            Call<ResponseBody> myCall = restService.postMakeFeatured(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info makeFeature Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                SettingsMain.hideDilog();
                                adforest_recreateAdDetail();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            SettingsMain.hideDilog();
                        }
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info makeFeature error", String.valueOf(t));
                    Log.d("info makeFeature error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_sendMessage(String msg) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {
            restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", myId);
            params.addProperty("message", msg);
            Log.d("info sendMeassage", myId);

            Call<ResponseBody> myCall = restService.postSendMessageFromAd(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info sendMeassage Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                intent.putExtra("chatId", response.getString("channel"));
                                intent.putExtra("text", msg);
                                intent.putExtra("sender_name", "You");
                                intent.putExtra("img", settingsMain.getUserImage());
                                intent.putExtra("receiverId", response.getString("receiver"));
                                intent.putExtra("type", true);
                                intent.putExtra("topic", topic);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            SettingsMain.hideDilog();
                        }
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info sendMeassage error", String.valueOf(t));
                    Log.d("info sendMeassage error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_ratingReplyDialog(final String comntId) {
        String text = null, sendBtn = null, cancelBtn = null;
        try {
            JSONObject dialogObject = jsonObjectRatingInfo.getJSONObject("rply_dialog");
            text = dialogObject.getString("text");
            sendBtn = dialogObject.getString("send_btn");
            cancelBtn = dialogObject.getString("cancel_btn");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Dialog dialog;
        dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_message);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        final EditText message = dialog.findViewById(R.id.editText3);
        message.setHint(text);
        Cancel.setText(cancelBtn);
        Send.setText(sendBtn);

        Send.setOnClickListener(v -> {
            if (!message.getText().toString().isEmpty()) {
                JsonObject params = new JsonObject();
                params.addProperty("ad_id", myId);
                params.addProperty("comment_id", comntId);
                params.addProperty("rating_comments", message.getText().toString());
                adforest_postRating(params);
                dialog.dismiss();
            }
            if (message.getText().toString().isEmpty())
                message.setError("");
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    void adforest_postRating(JsonObject params) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());


            Log.d("info send PostRating", params.toString());
            Call<ResponseBody> myCall = restService.postRating(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info PostRating Respon", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                                SettingsMain.hideDilog();

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                            SettingsMain.hideDilog();
                            adforest_recreateAdDetail();
                            simpleRatingBar.setRating(0);
                            editTextRattingComment.setText("");
                        }
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info makeFeature error", String.valueOf(t));
                    Log.d("info makeFeature error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    public void adforest_blockUserDialog() {
        try {

            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(getContext());
            alert.setTitle(blockUserObject.getString("popup_title"));
            alert.setCancelable(false);
            alert.setMessage(blockUserObject.getString("popup_text"));
            alert.setPositiveButton(blockUserObject.getString("popup_confirm"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    adforest_blockUser();
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton(blockUserObject.getString("popup_cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alert.show();


        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    public void adforest_blockUser() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("user_id", adAuthorId);
            Log.d("info Send terms id =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postBlockUser(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info terms responce ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                                HomeActivity.activity.recreate();

                            } else {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        SettingsMain.hideDilog();
                    } catch (JSONException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    } catch (IOException e) {
                        SettingsMain.hideDilog();
                        e.printStackTrace();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info CustomPages ", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info CustomPages err", String.valueOf(t));
                        Log.d("info CustomPages err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //go to public profile fragment when click userProfile image or name
    public void goToPublicProfile() {
        FragmentPublic_Profile fragment = new FragmentPublic_Profile();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", adAuthorId);
        bundle.putString("requestFrom", "");
        fragment.setArguments(bundle);
        replaceFragment(fragment, "FragmentPublic_Profile");
    }

    public void adforest_recreateAdDetail() {
        FragmentAdDetail fragmentAdDetail = new FragmentAdDetail();
        Bundle bundle = new Bundle();
        bundle.putString("id", myId);
        fragmentAdDetail.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, fragmentAdDetail);
        transaction.commit();
    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void adforestCall() {
        if (callDialog != null) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + maskedPhoneNumber));
            startActivity(intent);
            callDialog.dismiss();
        }
    }

    @Override
    public void onSuccessPermission(int code) {

        adforestCall();
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        mMap = Map;
//        setMap();
    }
}
