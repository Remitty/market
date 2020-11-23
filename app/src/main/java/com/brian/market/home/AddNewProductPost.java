package com.brian.market.home;

import android.Manifest;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brian.market.modelsList.ProductDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonObject;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;
import com.mapbox.mapboxsdk.location.OnLocationClickListener;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;
//import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
//import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;
import com.wonshinhyo.dragrecyclerview.DragRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.adapters.SpinnerAndListAdapter;
import com.brian.market.helper.LocaleHelper;
import com.brian.market.helper.MyProductOnclicklinstener;
import com.brian.market.helper.WorkaroundMapFragment;
import com.brian.market.home.adapter.ItemEditImageAdapter;
import com.brian.market.home.helper.AdPostImageModel;
import com.brian.market.home.helper.ProgressModel;
import com.brian.market.modelsList.myAdsModel;
import com.brian.market.modelsList.subcatDiloglist;
import com.brian.market.utills.GPSTracker;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;


public class AddNewProductPost extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, AdapterView.OnItemClickListener,  AdapterView.OnItemSelectedListener,
        RuntimePermissionHelper.permissionInterface, LocationEngineCallback<LocationEngineResult>, OnLocationClickListener, OnCameraTrackingChangedListener, PermissionsListener {

    private static final String TAG = "Sample";
    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";
    private static final int SELECT_PHOTO = 1000;

    protected GoogleMap mMap;
    AutoCompleteTextView mLocationAutoTextView, mAddressAutoTextView;
    List<View> allViewInstanceforCustom = new ArrayList<>();
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    String spinnerId;
    JSONObject jsonObject, jsonObjectforCustom;
//    Spinner spinnerLocation;
    //    HtmlTextView terms_conditionsTitleTV;
    TextView btnSelectPix, btnPostAd, Gallary, tv_done;
    EditText editPostTitle, editPostDescription, editPostPrice, postQtyET, editAddress,
            editTextUserLat, editTextuserLong;
    Activity context;
    FrameLayout frameLayout;
    SettingsMain settingsMain;
    LinearLayout linearLayoutCustom;
    String catID;
    boolean ison = false;
    List<View> allViewInstance = new ArrayList<>();
    LinearLayout  linearLayoutMapView;
    int imageLimit, per_limit;
    String stringImageLimitText = "";
    LinearLayout page1, page2, page3, linearLayoutImageSection, showHideLocation;
    ImageView imageViewNext1, imageViewNext2, imageViewBack1, imageViewBack2;
    CheckBox featureAdChkBox;
    Spinner catSpinner, currencySpinner;
    RadioGroup RdgFeatured, rdgShipping;
    RadioButton rdbFeature1, rdbFeature2, rdbFeature3;
    RadioButton rdbShipping, rdbShippingLocal;
    EditText editShippingPrice;

    List<File> allFile = new ArrayList<>();
    int addId;
    HorizontalScrollView horizontalScrollView;
    ItemEditImageAdapter itemEditImageAdapter;
    ArrayList<myAdsModel> myImages;
    DragRecyclerView recyclerView;
    RestService restService;
    SwitchDateTimeDialogFragment dateTimeFragment;
    String require_message = "";
    RuntimePermissionHelper runtimePermissionHelper;
    ProgressBar progress_bar;
    int imageRequestCount = 1;
    int totalUploadedImages = 0, currentSize = 0;
    FrameLayout loadingLayout;
    private PlacesClient placesClient;
    private NestedScrollView mScrollView;
    private Boolean spinnerTouched = false;
    private ArrayList<String> paths = new ArrayList<>();
    private ArrayList<String> imagePaths = new ArrayList<>();
    private ArrayList<Uri> imagePaths1 = new ArrayList<>();
    private Spinner spinnershow;
    private Calendar myCalendar = Calendar.getInstance();
    private int currentFileNumber = 1;
    private int totalFiles = 1;
    private ProgressModel progressModel;
    private AdPostImageModel adPostImageModel;
    private static Animation shakeAnimation;
    //MapBox Stuff
    boolean showGoogleMap = false;
    private PermissionsManager permissionsManager;
    LocationEngineRequest request;
    private LocationEngine locationEngine;
    LocationComponent locationComponent;
    MapView mapView;
    private boolean isInTrackingMode;
    Marker marker;
    EditText placesContainer;
    LinearLayout latlongLayout;
    LatLng point;
    Boolean packageLimit = false;
    BottomSheetDialog dialog;
    ListView listView;
    TextView tvAmenties;
    String checkedAmenties="";

    String myId;

    //    PackagesFragment packagesFragment;
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_add_new_ad_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Post New Product");
        setSupportActionBar(toolbar);
        displayLocationSettingsRequest(this);
        placesClient = com.google.android.libraries.places.api.Places.createClient(this);
        settingsMain = new SettingsMain(this);
        context = AddNewProductPost.this;
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }

        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        myId = getIntent().getStringExtra("post_id");

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        initComponents();

        initListerners();

        // Initialize the AutocompleteSupportFragment.

//        packagesFragment=new PackagesFragment();

        adforest_initiImagesAdapter();
        // get view from server
//        if (settingsMain.getBannerShow()) {
////            adforest_bannersAds();
////        }
        updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));

//        mLocationAutoTextView.setOnItemClickListener(this);

        ArrayList<String> categories = new ArrayList<String>();
        categories = settingsMain.getCategories();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        catSpinner.setAdapter(adapter);
        catSpinner.setOnItemSelectedListener(this);

        ArrayList<String> currencies = new ArrayList<String>();
        currencies = settingsMain.getCurrencies();

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter1);
        currencySpinner.setSelection(1);
        currencySpinner.setOnItemSelectedListener(this);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_bottom_amenties, null);
        dialog = new BottomSheetDialog(AddNewProductPost.this);
        dialog.setContentView(dialogView);

        listView = dialogView.findViewById(R.id.bottom_amenties_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        Button button = dialogView.findViewById(R.id.select_done);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSelectedItems();
                dialog.dismiss();
                List<String> selected = getSelectedItems();
                String logString = TextUtils.join(", ", selected);
                if(selected.size()>0)
                tvAmenties.setText(logString);
            }
        });

        if(myId != null){
            setTitle("Edit Product");
            getPostDetail();// for update
        }
    }

    private void getPostDetail() {
        JsonObject params = new JsonObject();

        params.addProperty("ad_id", myId);

        if (SettingsMain.isConnectingToInternet(context)) {

            SettingsMain.showDilog(context);
            Log.d("info adPost Data", "" + params.toString());
            Call<ResponseBody> myCall = restService.postDetail(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info AdPost Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info AdPost object", response.toString());
                                JSONObject data = response.getJSONObject("data");
                                JSONArray images = response.getJSONArray("images");

                                for (int i = 0; i < images.length(); i ++){
                                    adforest_updateImages(images.getJSONObject(i));
                                }

                                ProductDetails product = new ProductDetails();
                                product.setData(data);

                                editPostTitle.setText(product.getCardName());
                                editPostDescription.setText(product.getDescription());
                                editPostPrice.setText(product.getPrice());
                                postQtyET.setText("" + product.getQty());
                                mLocationAutoTextView.setText(product.getLocation());
                                placesContainer.setText(product.getAddress());
                                editAddress.setText(product.getAddress());
                                editTextUserLat.setText(product.getLat());
                                editTextuserLong.setText(product.getLong());
                                catSpinner.setSelection(data.getInt("cat_id"));
                                currencySpinner.setSelection(data.getInt("currency"));
                                switch (data.getInt("isFeatured")){
                                    case 1:
                                        rdbFeature1.setChecked(true);
                                        break;
                                    case 2:
                                        rdbFeature2.setChecked(true);
                                        break;
                                    case 3:
                                        rdbFeature3.setChecked(true);
                                        break;
                                }

                                if(product.isShipping()){
                                    rdbShipping.setChecked(true);
                                    editShippingPrice.setVisibility(View.VISIBLE);
                                    editShippingPrice.setText(product.getShipPrice());
                                }
                                else rdbShippingLocal.setChecked(true);

                            } else {
                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(context, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void initComponents() {
        mScrollView = (NestedScrollView) findViewById(R.id.scrollView);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.editorToolbar);

        WorkaroundMapFragment mapFragment = ((WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map));
        mapFragment.setListener(() -> mScrollView.requestDisallowInterceptTouchEvent(true));

        mapFragment.getMapAsync(this);

        frameLayout = (FrameLayout) findViewById(R.id.frame);

        btnSelectPix = (TextView) findViewById(R.id.selectPix);
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
//        progress_bar1 = (ProgressBar) findViewById(R.id.progress_bar1);
        Gallary = (TextView) findViewById(R.id.Gallary);
        tv_done = (TextView) findViewById(R.id.tv_done);
        loadingLayout = (FrameLayout) findViewById(R.id.loadingLayout);
        progressModel = SettingsMain.getProgressSettings(context);
        adPostImageModel = settingsMain.getAdPostImageModel(context);
        placesContainer = findViewById(R.id.placeContainer);
        latlongLayout = findViewById(R.id.latlongLayout);

        linearLayoutCustom = (LinearLayout) findViewById(R.id.customFieldLayout);
        linearLayoutMapView = (LinearLayout) findViewById(R.id.mapViewONOFF);

        editTextUserLat = (EditText) findViewById(R.id.latET);
        editTextuserLong = (EditText) findViewById(R.id.longET);

        page2 = (LinearLayout) findViewById(R.id.line2);
        page3 = (LinearLayout) findViewById(R.id.line3);

        recyclerView = (DragRecyclerView) findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        GridLayoutManager MyLayoutManager = new GridLayoutManager(context, 3);
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);

        linearLayoutImageSection = (LinearLayout) findViewById(R.id.ll11);

        btnPostAd = findViewById(R.id.postAd);
        editPostTitle = findViewById(R.id.postTitleET);
        editPostDescription = findViewById(R.id.postDescriptionET);
        editPostPrice = findViewById(R.id.postPriceET);
        postQtyET = findViewById(R.id.postQtyET);
        editAddress = findViewById(R.id.edit_address);

        featureAdChkBox = (CheckBox) findViewById(R.id.featureAdChkBox);

        catSpinner = findViewById(R.id.cat_spinner);
        currencySpinner = findViewById(R.id.spin_currency);

        RdgFeatured = findViewById(R.id.rdg_featured);
        rdbFeature1 = findViewById(R.id.rdb_featured_1);
        rdbFeature2 = findViewById(R.id.rdb_featured_2);
        rdbFeature3 = findViewById(R.id.rdb_featured_3);
        rdbFeature1.setText("$0.88/ 1day");
        rdbFeature2.setText("$2.5/ 3days");
        rdbFeature3.setText("$5/ 1week");

        rdgShipping = findViewById(R.id.rdg_shipping);
        rdbShipping = findViewById(R.id.rdb_shipping);
        rdbShippingLocal = findViewById(R.id.rdb_shipping_local);
        editShippingPrice = findViewById(R.id.shipping_price_edit);

        page2.setVisibility(View.VISIBLE);
        page3.setVisibility(View.GONE);
//        showHideLocation.setVisibility(View.GONE);

        myImages = new ArrayList<>();
        shakeAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.shake);

        imageViewBack1 = (ImageView) findViewById(R.id.back1);
        imageViewBack2 = (ImageView) findViewById(R.id.back2);
        imageViewNext1 = (ImageView) findViewById(R.id.next1);
        imageViewNext2 = (ImageView) findViewById(R.id.next2);


//        spinnerLocation = (Spinner) findViewById(R.id.spinnerLocation);

        mLocationAutoTextView = (AutoCompleteTextView) findViewById(R.id
                .location_autoCompleteTextView);
        mAddressAutoTextView = (AutoCompleteTextView) findViewById(R.id
                .address_autoCompleteTextView);
//        spinnerLocation.setFocusable(true);
        editPostTitle.setFocusable(true);
        mLocationAutoTextView.setFocusable(true);
//        mLocationAutoTextView.setFocusable(true);

        tvAmenties = findViewById(R.id.tvAmenties);

    }

    private void initListerners(){
        placesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new PlaceAutocomplete.IntentBuilder()
//                        .accessToken(getString(R.string.access_token))
//                        .placeOptions(PlaceOptions.builder().backgroundColor(Color.parseColor("#EEEEEE")).limit(10).build(PlaceOptions.MODE_CARDS))
//                        .build(AddNewAdPost.this);
//                startActivityForResult(intent, 35);

                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .setTypeFilter(TypeFilter.CITIES)
                        .build(getBaseContext());
                startActivityForResult(intent, 35);

            }
        });

        imageViewNext2.setOnClickListener(view -> {
//            if (adforest_page2Validation()) {
            page2.setVisibility(View.GONE);
            page3.setVisibility(View.VISIBLE);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.right_enter));
//                editTextUserName.requestFocus();
//            } else {
//                Toast.makeText(context, require_message, Toast.LENGTH_SHORT).show();
//            }
        });

        imageViewBack2.setOnClickListener(view -> {
            page3.setVisibility(View.GONE);
            page2.setVisibility(View.VISIBLE);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.left_enter));
        });

        btnPostAd.setOnClickListener(view -> {
            boolean b = false;
//            if (spinnerLocation.getSelectedItemPosition() == 0) {
//                setSpinnerError(spinnerLocation);
//                spinnerLocation.requestFocus();
//                b = true;
//            }

            if (editPostTitle.getText().toString().isEmpty()) {
                editPostTitle.setError("!");
                editPostTitle.requestFocus();
                b = true;
            }
            if (mLocationAutoTextView.getVisibility() == View.VISIBLE) {
                if (mLocationAutoTextView.getText().toString().isEmpty()) {
                    b = true;
                }
            }

//            if (editAddress.getText().toString().equals("")) {
//                editAddress.setError("!");
//                editAddress.requestFocus();
//                b = true;
//            }

            if (editPostPrice.getText().toString().equals("")) {
                editPostPrice.setError("!");
                editPostPrice.requestFocus();
                b = true;
            }

            if (postQtyET.getText().toString().equals("")) {
                postQtyET.setError("!");
                postQtyET.requestFocus();
                b = true;
            }

            if(catSpinner.getSelectedItemPosition() == 0) {
                b = true;
            }

            if(currencySpinner.getSelectedItemPosition() == 0) {
                b = true;
            }

            if(rdbShipping.isChecked() && editShippingPrice.getText().toString().equals("")){
                b = true;
                editShippingPrice.setError("Please input shipping price");
            }

            if (b) {

                if (mLocationAutoTextView.getVisibility() == View.VISIBLE) {
                    if (mLocationAutoTextView.getText().toString().isEmpty()) {
                        mLocationAutoTextView.requestFocus();
                        mLocationAutoTextView.setError("!");
                        Toast.makeText(context, require_message, Toast.LENGTH_SHORT).show();
                    }
                }

                if (catSpinner.getSelectedItemPosition() == 0) {
                    setSpinnerError(catSpinner);
                    catSpinner.requestFocus();
                    Toast.makeText(context, "Please select category", Toast.LENGTH_SHORT).show();
                }

                if (currencySpinner.getSelectedItemPosition() == 0) {
                    setSpinnerError(currencySpinner);
                    currencySpinner.requestFocus();
                    Toast.makeText(context, "Please select currency", Toast.LENGTH_SHORT).show();
                }

            } else {
//                adforest_submitQuery(adforest_getDataFromDynamicViews());
                adforest_submitQuery();
            }
        });

        btnSelectPix.setOnClickListener((View view) -> {
            runtimePermissionHelper.requestStorageCameraPermission(1);
        });

        mAddressAutoTextView.setOnItemClickListener(this);

        mLocationAutoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                manageAutoComplete(s.toString(), "location");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        mAddressAutoTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                manageAutoComplete(s.toString(), "address");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        tvAmenties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        rdgShipping.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("checkshipping id", checkedId+"");
                if(checkedId == group.getChildAt(1).getId()) {/// select shipping
                    editShippingPrice.setVisibility(View.VISIBLE);
                    currencySpinner.setSelection(1);
                    currencySpinner.setEnabled(false);
                }
                else {///select local
                    currencySpinner.setEnabled(true);
                    editShippingPrice.setVisibility(View.GONE);
                }
            }
        });
    }

    private void manageAutoComplete(String query, String type) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();
//        request.setCountry("US");
        request.setTypeFilter(TypeFilter.REGIONS);
        request.setSessionToken(token)
                .setQuery(query);

        if(type.equals("address"))
            request.setTypeFilter(TypeFilter.ADDRESS);

        placesClient.findAutocompletePredictions(request.build()).addOnSuccessListener((response) -> {

            ids.clear();
            places.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                places.add(prediction.getFullText(null).toString());
                ids.add(prediction.getPlaceId());
                Log.i("Places", prediction.getPlaceId());
                Log.i("Places", prediction.getFullText(null).toString());
            }
            String[] data = places.toArray(new String[places.size()]); // terms is a List<String>

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(AddNewProductPost.this, android.R.layout.simple_dropdown_item_1line, data);
            mLocationAutoTextView.setAdapter(adapter);
            mAddressAutoTextView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if(view == mAddressAutoTextView) {
            String placeId = ids.get(position);
            List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                    .build();
// Add a listener to handle the response.
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                com.google.android.libraries.places.api.model.Place place = response.getPlace();
                Log.i("Places", "Place found: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
                if (mMap != null) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));

                    editTextuserLong.setText(String.format("%s", place.getLatLng().longitude));
                    editTextUserLat.setText(String.format("%s", place.getLatLng().latitude));
                }
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    int statusCode = apiException.getStatusCode();
// Handle error with given status code.
                    Log.e("Places", "Place not found: " + exception.getMessage());
                }
            });

//        }
    }

    public void adforest_initiImagesAdapter() {
        imageLimit = 10000;
        per_limit = 4;
        stringImageLimitText = "Too Large";
        myImages = new ArrayList<>();
        itemEditImageAdapter = new ItemEditImageAdapter(context, myImages);
        recyclerView.setAdapter(itemEditImageAdapter);
        itemEditImageAdapter.setHandleDragEnabled(true);
        itemEditImageAdapter.setLongPressDragEnabled(true);
        itemEditImageAdapter.setSwipeEnabled(true);

        itemEditImageAdapter.setOnItemClickListener(new MyProductOnclicklinstener() {


            @Override
            public void onItemClick(ProductDetails item) {

            }

            @Override
            public void delViewOnClick(View v, int position) {
                myAdsModel item = myImages.get(position);
//                if(item.getImageType() == 0)
                    delImage(item.getAdId(), position, item.getImageType());
//                if(item.getImageType() == 1)
//                    delImage(position+"", item.getImageType());
            }

        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    //APi for submiting post to server
    private void adforest_submitQuery() {
        JsonObject params = new JsonObject();

//        if (jsonObjectforCustom != null && adforest_getDataFromDynamicViewsForCustom() != null) {
//            params.add("custom_fields", adforest_getDataFromDynamicViewsForCustom());
//        }

        JSONObject jsonObj;

        if (itemEditImageAdapter != null) {
            if (itemEditImageAdapter.getItemCount() > 0)
                params.addProperty("images", itemEditImageAdapter.getAllTags());
        } else
            params.addProperty("images", "");
        if(myId != null)
            params.addProperty("ad_id", myId);
//            jsonObj = jsonObject.getJSONObject("data").getJSONObject("profile");

        params.addProperty("title", editPostTitle.getText().toString());
        params.addProperty("description", editPostDescription.getText().toString());
        params.addProperty("price", editPostPrice.getText().toString());
        params.addProperty("count", postQtyET.getText().toString());
        params.addProperty("cat_id", catSpinner.getSelectedItemPosition());
        params.addProperty("currency", currencySpinner.getSelectedItemPosition());
//        params.addProperty("amenties", checkedAmenties);

//            try {
//                String phoneNumber = editTextUserPhone.getText().toString();
//                if (jsonObj.getBoolean("is_phone_verification_on")) {
//                    if (phoneNumber.contains("+"))
//                        params.addProperty(jsonObj.getJSONObject("phone").getString("field_type_name"), phoneNumber);
//                    else
//                        params.addProperty(jsonObj.getJSONObject("phone").getString("field_type_name"), "+" + phoneNumber);
//                } else
//                    params.addProperty(jsonObj.getJSONObject("phone").getString("field_type_name"), phoneNumber);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        if (!mLocationAutoTextView.getText().toString().equals("")) {
            params.addProperty("location", mLocationAutoTextView.getText().toString());
//        } else {
            params.addProperty("address", editAddress.getText().toString());
//        }

        params.addProperty("location_lat", editTextUserLat.getText().toString());
        params.addProperty("location_lng", editTextuserLong.getText().toString());
//            if (jsonObj.getBoolean("ad_country_show")) {
//                subcatDiloglist subDiloglist = (subcatDiloglist) spinnerLocation.getSelectedView().getTag();
//                params.addProperty("ad_country", subDiloglist.getId());
//            }
        if (featureAdChkBox.isChecked()) {
            params.addProperty("isFeatured", "true");
        } else
            params.addProperty("isFeatured", "false");

        int selectedId=RdgFeatured.getCheckedRadioButtonId();
        RadioButton rdbFeature=(RadioButton) findViewById(selectedId);
        switch(selectedId) {
            case R.id.rdb_featured_1:
                params.addProperty("featured", 1);
                break;
            case R.id.rdb_featured_2:
                params.addProperty("featured", 2);
                break;
            case R.id.rdb_featured_3:
                params.addProperty("featured", 3);
                break;
        }

        int selectedShipping = rdgShipping.getCheckedRadioButtonId();
        switch (selectedShipping){
            case R.id.rdb_shipping:
                params.addProperty("isShipping", 1);
                params.addProperty("shipping_price", editShippingPrice.getText().toString());
                break;
            case R.id.rdb_shipping_local:
                params.addProperty("isShipping", 0);
                break;
        }

        if (SettingsMain.isConnectingToInternet(context)) {

            SettingsMain.showDilog(context);
            Log.d("info adPost Data", "" + params.toString());
            Call<ResponseBody> myCall = restService.postAdNewPost(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info AdPost Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info AdPost object", response.toString());

                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(AddNewProductPost.this, HomeActivity.class);
//                                intent.putExtra("adId", response.getJSONObject("data").getString("ad_id"));
                                startActivity(intent);

                                AddNewProductPost.this.finish();
                            } else {
                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(context, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    // getting images selected from gallery for post and sending them to server
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
     super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                imagePaths = new ArrayList<>();
                imageRequestCount = 1;
                imagePaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                if (SettingsMain.isConnectingToInternet(context)) {
                    if (imagePaths.size() > 0) {
                        btnSelectPix.setEnabled(false);
                        AsyncImageTask asyncImageTask = new AsyncImageTask();
                        asyncImageTask.execute(imagePaths);
                    }
                }
            } else {
                btnSelectPix.setEnabled(true);
                Toast.makeText(context, settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == 35) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                placesContainer.setText(place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }

//        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
//
//            Uri uri = data.getData();
//            Bitmap bitmap = null;
//
////          imagePaths1 = new ArrayList<>();
//            imageRequestCount = 1;
//            imagePaths1.add(uri);
//            List<MultipartBody.Part> parts = null;
//            parts = new ArrayList<>();
//            parts.add(adforestst_prepareFilePart("file" + 0, uri));
//
//            adforest_uploadImages(parts);
//
//            addImage(uri);
//
//        }

    }

    private void addImage(Uri uri) {
        myAdsModel item = new myAdsModel();
        String lastTag="0";
        if(myImages.size() > 0) {
            myAdsModel last = myImages.get(myImages.size() - 1);
            lastTag = last.getAdId();
        }

        item.setAdId((Integer.parseInt(lastTag)+1)+"");
        item.setImageType(1);
        item.setImage(uri);

        myImages.add(item);

        itemEditImageAdapter.notifyDataSetChanged();
    }

    public void goToImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }

    private void adforest_uploadImages(MultipartBody.Part parts) {
        Log.d("info image parts", parts.toString());
        String ad_id = Integer.toString(addId);
        Log.d("info SendImage", myId + "");
        if(myId == null) myId = "";

        RequestBody adID =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, myId);

        final Call<ResponseBody> req = restService.postUploadImage(adID, parts);

        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Log.v("info UploadImage", response.toString());
                if (response.isSuccessful()) {

                    JSONObject responseobj = null;
                    try {
                        responseobj = new JSONObject(response.body().string());
                        Log.d("info UploadImage object", "" + responseobj.getJSONObject("data").toString());
                        if (responseobj.getBoolean("success")) {

                            adforest_updateImages(responseobj.getJSONObject("data"));

                            int selectedImageSize = imagePaths.size();
                            int totalSize = currentSize + selectedImageSize;
                            Log.d("info image2", "muImage" + totalSize + "imagePaths" + totalUploadedImages);
                                adforest_UploadSuccessImage();
                            if (totalSize == totalUploadedImages) {
                                imagePaths.clear();
                                paths.clear();
                                if (allFile.size() > 0) {
                                    for (File file : allFile) {
                                        if (file.exists()) {
                                            if (file.delete()) {
                                                Log.d("file Deleted :", file.getPath());
                                            } else {
                                                Log.d("file not Deleted :", file.getPath());
                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            adforest_UploadFailedImage();
                            Toast.makeText(context, responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        adforest_UploadFailedImage();
                        e.printStackTrace();
                    } catch (IOException e) {
                        adforest_UploadFailedImage();
                        e.printStackTrace();
                    }


                    btnSelectPix.setEnabled(true);

                } else {
                    adforest_UploadFailedImage();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("info Upload Image Err:", t.toString());

                if (t instanceof TimeoutException) {
                    adforest_UploadFailedImage();

//                    adforest_requestForImages();
                }
                if (t instanceof SocketTimeoutException) {
                    adforest_UploadFailedImage();
//                    adforest_requestForImages();
//
                } else {
                    adforest_UploadFailedImage();
//                    adforest_requestForImages();
                }
            }
        });

    }

    private void adforest_UploadFailedImage() {
        progress_bar.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
        btnSelectPix.setEnabled(true);
//        Toast.makeText(context, progressModel.getFailMessage(), Toast.LENGTH_SHORT).show();

    }

    private void adforest_UploadSuccessImage() {
        progress_bar.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.GONE);
//        Toast.makeText(context, progressModel.getSuccessMessage(), Toast.LENGTH_SHORT).show();
        btnSelectPix.setEnabled(true);
    }

    private MultipartBody.Part adforestst_prepareFilePart(String fileName, Uri fileUri) {

        File finalFile = new File(SettingsMain.getRealPathFromURI(context, fileUri));
        allFile.add(finalFile);
        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        finalFile
                );
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(fileName, finalFile.getName(), requestFile);
    }

    private File adforest_rotateImage(String path) {
        File file = null;
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Bitmap rotatedBitmap = null;
        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = rotateImage(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = rotateImage(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = rotateImage(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        file = new File(getRealPathFromURI(getImageUri(rotatedBitmap)));
        allFile.add(file);
        return file;
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    //setting spiner error for validation
    private void setSpinnerError(Spinner spinner) {
        View selectedView = spinner.getSelectedView();
        if (selectedView != null && selectedView instanceof TextView) {
            spinner.requestFocus();
            TextView selectedTextView = (TextView) selectedView;
            selectedTextView.setError("Required!");
            selectedTextView.setTextColor(Color.RED);
        }
    }

    @Override
    public void onMapReady(GoogleMap Map) {
        mMap = Map;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            return;
        }
        GPSTracker gpsTracker = new GPSTracker(AddNewProductPost.this);
        if (!gpsTracker.canGetLocation())
            gpsTracker.showSettingsAlert();
        else if (gpsTracker.canGetLocation() && gpsTracker.isCheckPermission()) {
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        LatLng currentPos = null;
        currentPos = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        // create marker
        MarkerOptions marker = new MarkerOptions()
                .position(currentPos)
//                .title(gpsTracker.get)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        mMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude())).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                2000, null);



        editTextUserLat.setText(String.format("%s", gpsTracker.getLatitude()));
        editTextuserLong.setText(String.format("%s", gpsTracker.getLongitude()));

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            editTextuserLong.setText(String.format("%s", latLng.longitude));
            editTextUserLat.setText(String.format("%s", latLng.latitude));
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        GPSTracker gpsTracker = new GPSTracker(AddNewProductPost.this);
        if (!gpsTracker.canGetLocation())
            gpsTracker.showSettingsAlert();
        else {
            Geocoder geocoder;
            List<Address> addresses1 = null;
            try {
                addresses1 = new Geocoder(this, Locale.getDefault()).getFromLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder result = new StringBuilder();
            if (addresses1.size() > 0) {
                Address address = addresses1.get(0);
                int maxIndex = address.getMaxAddressLineIndex();
                for (int x = 0; x <= maxIndex; x++) {
                    result.append(address.getAddressLine(x));
                    //result.append(",");
                }
            }
            try {
                mAddressAutoTextView.setText(result.toString());
                editTextuserLong.setText(gpsTracker.getLongitude() + "");
                editTextUserLat.setText(gpsTracker.getLatitude() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public void onSuccess(LocationEngineResult result) {
        Location location = result.getLastLocation();

        if (location == null) {

            return;
        }

        List<Address> addresses = null;
        if (location != null) {
            Geocoder geocoder = new Geocoder(AddNewProductPost.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (addresses != null) {
            StringBuilder stringBuilder = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int maxIndex = address.getMaxAddressLineIndex();
                for (int x = 0; x <= maxIndex; x++) {
                    stringBuilder.append(address.getAddressLine(x));
                    //result.append(",");
                }
            }
            placesContainer.setText(stringBuilder.toString());
            editTextUserLat.setText(String.valueOf(location.getLatitude()));
            editTextuserLong.setText(String.valueOf(location.getLongitude()));
            if (locationEngine != null)
                locationEngine.removeLocationUpdates(this);
            return;
        } else {
            Toast.makeText(AddNewProductPost.this, "Failed To Get Address. Please Try Again", Toast.LENGTH_SHORT).show();
        }

        // Create a Toast which displays the new location's coordinates
//        Toast.makeText(this, String.format(getString(R.string.new_location)+
//                        String.valueOf(result.getLastLocation().getLatitude())+
//                        String.valueOf(result.getLastLocation().getLongitude())),
//                Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null)
            locationEngine.removeLocationUpdates(this);
    }

    @Override
    public void onFailure(@NonNull Exception exception) {
        Log.d("LocationChangeActivity", exception.getLocalizedMessage());

        Toast.makeText(this, exception.getLocalizedMessage(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCameraTrackingDismissed() {
        isInTrackingMode = false;
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {

    }

    @Override
    public void onLocationComponentClick() {
        if (locationComponent.getLastKnownLocation() != null) {
            Toast.makeText(this, String.format("sdfsdfj",
                    locationComponent.getLastKnownLocation().getLatitude(),
                    locationComponent.getLastKnownLocation().getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Explanation Needed",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
//        if (granted) {
//            if (mapBoxMap.getStyle() != null) {
//                enableLocationComponent(mapBoxMap.getStyle());
//
//            }
//        } else {
//            Toast.makeText(this, "Location Permission Not Granted", Toast.LENGTH_LONG).show();
//            finish();
//        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private static class LatLngEvaluator implements TypeEvaluator<com.mapbox.mapboxsdk.geometry.LatLng> {
        // Method is used to interpolate the marker animation.

        private com.mapbox.mapboxsdk.geometry.LatLng latLng = new com.mapbox.mapboxsdk.geometry.LatLng();

        @Override
        public com.mapbox.mapboxsdk.geometry.LatLng evaluate(float fraction, com.mapbox.mapboxsdk.geometry.LatLng startValue, com.mapbox.mapboxsdk.geometry.LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    }

    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(AddNewProductPost.this, 990);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(this)
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .build();

            // Get an instance of the component
//            locationComponent = mapBoxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            // Add the location icon click listener
            locationComponent.addOnLocationClickListener(this);

            // Add the camera tracking listener. Fires if the map camera is manually moved.
            locationComponent.addOnCameraTrackingChangedListener(this);

            initLocationEngine();
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            if (gps_enabled) {
                if (!isInTrackingMode) {
                    isInTrackingMode = true;
                    locationComponent.setCameraMode(CameraMode.TRACKING);
                    locationComponent.zoomWhileTracking(16f);
//                    Location location = locationComponent.getLastKnownLocation();
//                    List<Address> addresses = null;
//                    if (location != null) {
//                        Geocoder geocoder = new Geocoder(AddNewAdPost.this, Locale.getDefault());
//                        try {
//                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//
//                    if (addresses != null) {
//                        StringBuilder result = new StringBuilder();
//                        if (addresses.size() > 0) {
//                            Address address = addresses.get(0);
//                            int maxIndex = address.getMaxAddressLineIndex();
//                            for (int x = 0; x <= maxIndex; x++) {
//                                result.append(address.getAddressLine(x));
//                                //result.append(",");
//                            }
//                        }
//                        placesContainer.setText(result.toString());
//                    } else {
//                        Toast.makeText(AddNewAdPost.this, "Failed To Get Address. Please Try Again", Toast.LENGTH_SHORT).show();
//                    }
                }
            } else {
                displayLocationSettingsRequest(AddNewProductPost.this);
            }
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        request = new LocationEngineRequest.Builder(5000L)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(10000).build();


        locationEngine.requestLocationUpdates(request, this, getMainLooper());
        locationEngine.getLastLocation(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public class ViewDialogMaterial {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialogmaterial);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            FrameLayout mDialogNo = dialog.findViewById(R.id.frmOk);
            mDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "achaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
//                    startFragment(packagesFragment, "PackagesFragment");
                }
            });

            FrameLayout mDialogOk = dialog.findViewById(R.id.frmNo);
            mDialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "INkar ha ", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            });
//
            dialog.show();
//        }
        }
    }

    //start fragment
//    public void startFragment(Fragment someFragment, String tag) {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentByTag(tag);
//        if (fragment == null) {
//            fragment = someFragment;
//            fm.beginTransaction()
//                    .add(R.id.frame, fragment, tag).commit();
//        }
//    }
    //category not paid dialog
    public class ViewDialog {

        public void showDialog(Activity activity, String msg) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.dialogforcat);
            ImageView imageView = dialog.findViewById(R.id.a);
            Glide.with(AddNewProductPost.this).load(R.drawable.angry).into(imageView);
            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

    //show bump and feature ads
    private void adforest_bumpNDFeatureAds(RelativeLayout relativeLayout, TextView textView,
                                           final CheckBox checkBox
            , JSONObject adObject, final JSONObject alertObject) {
        String alertTile = null, alertText = null, alertYesBtn = null, alertNoBtn = null;
        relativeLayout.setVisibility(View.VISIBLE);
        try {
            textView.setText(adObject.getString("title"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("info AdPost CheckBox", adObject.toString());
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        try {
            alertTile = alertObject.getString("title");
            alertText = alertObject.getString("text");
            alertYesBtn = alertObject.getString("btn_ok");
            alertNoBtn = alertObject.getString("btn_no");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String finalAlertTile = alertTile;
        final String finalAlertText = alertText;
        final String finalAlertYesBtn = alertYesBtn;
        final String finalAlertNoBtn = alertNoBtn;
        checkBox.setOnClickListener(v -> {
            alert.setTitle(finalAlertTile);
            alert.setCancelable(false);
            alert.setMessage(finalAlertText);
            alert.setPositiveButton(finalAlertYesBtn, (dialog, which) -> {
                checkBox.setChecked(true);
                dialog.dismiss();
            });
            alert.setNegativeButton(finalAlertNoBtn, (dialogInterface, i) -> {
                dialogInterface.dismiss();
                checkBox.setChecked(false);
            });
            alert.show();
        });
    }

    //showing dilog for catgory selection
    private void adforest_ShowDialog(JSONObject data, final subcatDiloglist main,
                                     final ArrayList<subcatDiloglist> spinnerOptionsout,
                                     final SpinnerAndListAdapter spinnerAndListAdapterout,
                                     final Spinner spinner1, final String field_type_name) {

        Log.d("info Show Dialog ===== ", "adforest_ShowDialog");
        try {
            Log.d("info Show Dialog ===== ", data.getJSONArray("values").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(context, R.style.PauseDialog);

        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_sub_cat);

        dialog.setTitle(main.getName());
        ListView listView = dialog.findViewById(R.id.listView);

        final ArrayList<subcatDiloglist> listitems = new ArrayList<>();
        final JSONArray listArray;
        try {
            listArray = data.getJSONArray("values");
            for (int j = 0; j < listArray.length(); j++) {
                subcatDiloglist subDiloglist = new subcatDiloglist();
                subDiloglist.setId(listArray.getJSONObject(j).getString("id"));
                subDiloglist.setName(listArray.getJSONObject(j).getString("name"));
                subDiloglist.setHasSub(listArray.getJSONObject(j).getBoolean("has_sub"));
                subDiloglist.setHasCustom(listArray.getJSONObject(j).getBoolean("has_template"));
//                subDiloglist.setBidding(listArray.getJSONObject(j).getBoolean("is_bidding"));
                listitems.add(subDiloglist);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final SpinnerAndListAdapter spinnerAndListAdapter1 = new SpinnerAndListAdapter(context, listitems, true);
        listView.setAdapter(spinnerAndListAdapter1);

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            final subcatDiloglist subcatDiloglistitem = (subcatDiloglist) view.getTag();

            //Log.d("helllo" , spinnerOptionsout.adforest_get(1).getId() + " === " + spinnerOptionsout.adforest_get(1).getName());

            if (!spinnerOptionsout.get(1).getId().equals(subcatDiloglistitem.getId())) {

                if (subcatDiloglistitem.isHasSub()) {


                    if (SettingsMain.isConnectingToInternet(context)) {

                        SettingsMain.showDilog(context);

                        //if user select subcategoreis and again select subCategoreis
                        if (field_type_name.equals("ad_cats1")) {
                            JsonObject params = new JsonObject();
                            params.addProperty("subcat", subcatDiloglistitem.getId());
                            Log.d("info SendSubCatAg", params.toString());

                            Call<ResponseBody> myCall = restService.postGetSubCategories(params, UrlController.AddHeaders(context));
                            myCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                    try {
                                        if (responseObj.isSuccessful()) {
                                            Log.d("info SubCatAg Resp", "" + responseObj.toString());

                                            JSONObject response = new JSONObject(responseObj.body().string());
                                            if (response.getBoolean("success")) {
                                                Log.d("info SubCatAg object", "" + response.getJSONObject("data"));

                                                adforest_ShowDialog(response.getJSONObject("data"), subcatDiloglistitem, spinnerOptionsout
                                                        , spinnerAndListAdapterout, spinner1, field_type_name);

                                            } else {
                                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                                    Log.d("info SubCatAg error", String.valueOf(t));
                                    Log.d("info SubCatAg error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                }
                            });

                        }

                        //if user select subLocation and again select subLocation
                        if (field_type_name.equals("ad_country")) {

                            JsonObject params1 = new JsonObject();
                            params1.addProperty("ad_country", subcatDiloglistitem.getId());

                            Log.d("info SendLocationsAg", params1.toString());

                            Call<ResponseBody> myCall = restService.postGetSubLocations(params1, UrlController.AddHeaders(context));
                            myCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                    try {
                                        if (responseObj.isSuccessful()) {
                                            Log.d("info SubLocationAg Resp", "" + responseObj.toString());

                                            JSONObject response = new JSONObject(responseObj.body().string());
                                            if (response.getBoolean("success")) {
                                                Log.d("info SubLocationAg obj", "" + response.getJSONObject("data"));

                                                adforest_ShowDialog(response.getJSONObject("data"), subcatDiloglistitem, spinnerOptionsout
                                                        , spinnerAndListAdapterout, spinner1, field_type_name);

                                            } else {
                                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                                    Log.d("info SubLocationAg err", String.valueOf(t));
                                    Log.d("info SubLocationAg err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                }
                            });

                        }
                    } else {
                        SettingsMain.hideDilog();
                        Toast.makeText(context, "Internet error", Toast.LENGTH_SHORT).show();
                    }


                } else {

                    for (int ii = 0; ii < spinnerOptionsout.size(); ii++) {
                        if (spinnerOptionsout.get(ii).getId().equals(subcatDiloglistitem.getId())) {
                            spinnerOptionsout.remove(ii);
                            Log.d("info ===== ", "else of list inner is 1st button into for loop");
                            break;
                        }
                    }
                    Log.d("info ===== ", "else of list inner is 1st button out of for loop");

                    spinnerOptionsout.add(1, subcatDiloglistitem);
                    spinner1.setSelection(1, false);
                    spinnerAndListAdapterout.notifyDataSetChanged();

                }

                if (subcatDiloglistitem.isHasCustom() && field_type_name.equals("ad_cats1")) {
                    linearLayoutCustom.removeAllViews();
                    allViewInstanceforCustom.clear();
                    catID = subcatDiloglistitem.getId();
//                    adforest_showCustom(data, subcatDiloglistitem, cardViewtBidingTimer, "null");
                    ison = true;
                    Log.d("true===== ", "inter add All");

                } else {
                    if (ison && field_type_name.equals("ad_cats1")) {
                        linearLayoutCustom.removeAllViews();
                        allViewInstanceforCustom.clear();
//                        requiredFieldsForCustom.clear();
                        ison = false;
                        Log.d("true===== ", "inter remove All");
                    }
                }
            } else {
                spinner1.setSelection(1, false);
                Log.d("info ===== ", "else of chk is 1st button out");

            }
            dialog.dismiss();
        });

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        try {
            Send.setText(jsonObject.getJSONObject("extra").getString("dialog_send"));
            Cancel.setText(jsonObject.getJSONObject("extra").getString("dialg_cancel"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Send.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        Cancel.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        Send.setOnClickListener(v -> {

            for (int i = 0; i < spinnerOptionsout.size(); i++) {
                if (spinnerOptionsout.get(i).getId().equals(main.getId())) {
                    spinnerOptionsout.remove(i);
                    Log.d("info ===== ", "send button in");
                    break;
                }
            }

            spinnerOptionsout.add(1, main);
            spinnerAndListAdapterout.notifyDataSetChanged();
            spinner1.setSelection(1, false);
            Log.d("info ===== ", "send button out");

            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();

    }

    public void hideSoftKeyboard() {
        if (context.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (page2.getVisibility() == View.VISIBLE) {
//            imageViewBack1.performClick();
            if(myId == null)
            allRemoveImages();
            super.onBackPressed();
            overridePendingTransition(R.anim.left_enter, R.anim.right_out);
        } else if (page3.getVisibility() == View.VISIBLE) {
            imageViewBack2.performClick();
        }
    }

    private void allRemoveImages() {
        myAdsModel item = null;

        if(myImages.size() == 0) return;

        for(int i = 0; i < myImages.size(); i ++){
            item = myImages.get(i);

            delImage(item.getAdId()+"",i, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                break;
        }
        return true;
    }

    private void delImage(String tag, int position, int imageType) {

//        if(imageType == 1){
//            myImages.remove(Integer.parseInt(tag));
//            itemEditImageAdapter.notifyDataSetChanged();
//            return;
//        }

        if (SettingsMain.isConnectingToInternet(context)) {
            loadingLayout.setVisibility(View.VISIBLE);
//            SettingsMain.showDilog(context);
            JsonObject params = new JsonObject();
            params.addProperty("img_id", tag);
            params.addProperty("ad_id", myId);
            Log.d("info send DeleteImage", params.toString());
            Call<ResponseBody> myCall = restService.postDeleteImages(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info DeleteImage Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info DeleteImage object", "" + response.toString());
//                                adforest_updateImages(response.getJSONObject("data"));
                                myImages.remove(position);
                                itemEditImageAdapter.notifyDataSetChanged();
//                                Gallary.setVisibility(View.VISIBLE);
//                                Gallary.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_black_24dp, 0, 0, 0);
//                                Gallary.setText(response.getJSONObject("data").getJSONArray("ad_images").length() + "");
//                                tv_done.setVisibility(View.GONE);

//                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    loadingLayout.setVisibility(View.GONE);
                    Log.d("info DeleteImage error", t.getStackTrace().toString());
                    Log.d("info DeleteImage error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(context, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    void adforest_initiImageList(JSONObject object) {
        myAdsModel item = new myAdsModel();

//        object = jsonArrayImages.getJSONObject(i);
        String image_path = "";
        try {
            totalUploadedImages ++;

//            JSONObject object = jsonObject.getJSONObject("ad_images");
            item.setAdId(object.getString("id"));

            if (object.optString("thumb").startsWith("http"))
                image_path = object.optString("thumb");
            else
                image_path = UrlController.IP_ADDRESS + "storage/" + object.optString("thumb");
            item.setImage(image_path);
            item.setImageType(0);
            myImages.add(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        myImages.clear();
//        try {
//
//
//            JSONArray jsonArrayImages = jsonObject.getJSONArray("ad_images");
//
//            totalUploadedImages = jsonArrayImages.length();
//            Log.d("info Images Data", "" + jsonArrayImages.toString());
//
//            if (jsonArrayImages.length() > 0) {
//
//                for (int i = 0; i < jsonArrayImages.length(); i++) {
//                    myAdsModel item = new myAdsModel();
//                    JSONObject object = new JSONObject();
//                    object = jsonArrayImages.getJSONObject(i);
//
//                    item.setAdId(object.getString("img_id"));
//                    item.setImage((object.getString("thumb")));
//                    item.setImageType(0);
//                    myImages.add(item);
//                }
//
//            } else {
////                recyclerView.setAdapter(null);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    void adforest_updateImages(JSONObject jsonObject) {
        adforest_initiImageList(jsonObject);
        itemEditImageAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void adforest_showDate(final EditText editText) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewProductPost.this, (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
            if (editText != null)
                editText.setText(sdf.format(myCalendar.getTime()));
        }, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private List<String> getSelectedItems() {
        List<String> result = new ArrayList<>();
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        checkedAmenties="";
        if(checkedItems.size() > 0)
        for (int i = 0; i < listView.getCount(); ++i) {
            if (checkedItems.valueAt(i)) {
                result.add((String) listView.getItemAtPosition(checkedItems.keyAt(i)));
                checkedAmenties += (checkedItems.keyAt(i)+1)+",";
            }
        }

        return result;
    }

    @Override
    public void onSuccessPermission(int code) {
//        if (imageLimit < per_limit) {
//            per_limit = imageLimit;
//        }
//        if (imageLimit > 0) {
            FilePickerBuilder.getInstance().setMaxCount(per_limit)
                    .setSelectedFiles(paths)
                    .setActivityTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar)
                    .pickPhoto(AddNewProductPost.this);
//        goToImageIntent();
//            Toast.makeText(context, stringImageLimitText, Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(context, stringImageLimitText, Toast.LENGTH_SHORT).show();
//        }

    }


    private class AsyncImageTask extends AsyncTask<ArrayList<String>, Void, List<MultipartBody.Part>> {
        ArrayList<String> imaagesLis = null;
        boolean checkDimensions = true, checkImageSize;

        @SafeVarargs
        @Override
        protected final List<MultipartBody.Part> doInBackground(ArrayList<String>... params) {
            List<MultipartBody.Part> parts = null;
            imaagesLis = params[0];
            totalFiles = imaagesLis.size();
            for (int i = 0; i < imaagesLis.size(); i++) {
                parts = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentDateandTime = sdf.format(new Date());
                Log.d("info image", currentDateandTime);
                checkDimensions = true;
                checkImageSize = true;
//                if (adPostImageModel.getDim_is_show()) {
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(imaagesLis.get(i), options);
//                    int imageWidth = options.outWidth;
//                    int imageHeight = options.outHeight;
//                    if (imageHeight > Integer.parseInt(adPostImageModel.getDim_height()) &&
//                            imageWidth > Integer.parseInt(adPostImageModel.getDim_width())) {
//                        checkDimensions = true;
//                        Log.d("treuwala", String.valueOf(checkDimensions));
//                    } else {
//                        checkDimensions = false;
//
//                        Log.d("falsewala", String.valueOf(checkDimensions));
//                        runOnUiThread(() -> Toast.makeText(context, adPostImageModel.getDim_height_message(), Toast.LENGTH_SHORT).show());
//                    }
//                }
                checkImageSize = true;
                checkDimensions = true;
                File file = new File(imaagesLis.get(i));
//                long fileSizeInBytes = file.length();
//                Integer fileSizeBytes = Math.round(fileSizeInBytes);
//                if (fileSizeBytes > Integer.parseInt(adPostImageModel.getImg_size())) {
//                    checkImageSize = false;
//                    Log.d("falsewalasize", String.valueOf(checkImageSize));
//                    runOnUiThread(() -> Toast.makeText(context, adPostImageModel.getImg_message(), Toast.LENGTH_SHORT).show());
//                } else {
//                    checkImageSize = true;
//                    Log.d("truewalasize", String.valueOf(checkImageSize));
//                }
                if (checkImageSize && checkDimensions) {
                    File finalFile1 = adforest_rotateImage(imaagesLis.get(i));
//                    File finalFile1 =new File(imaagesLis.get(i));

                    Uri tempUri = SettingsMain.decodeFile(context, finalFile1);

//                    parts.add(adforestst_prepareFilePart("file" + i, tempUri));

//                    adforest_uploadImages(parts);
                    adforest_uploadImages(adforestst_prepareFilePart("image", tempUri));
                }
            }
            return parts;
        }

        @Override
        protected void onPostExecute(List<MultipartBody.Part> result) {
        }

        @Override
        protected void onPreExecute() {
            currentSize = myImages.size();
            progress_bar.setVisibility(View.VISIBLE);

            Drawable drawable = getResources().getDrawable(R.drawable.bg_uploading).mutate();
            drawable.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_ATOP);
            loadingLayout.setBackground(drawable);

            loadingLayout.setVisibility(View.VISIBLE);
        }

    }
}
