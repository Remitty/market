package com.brian.market.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brian.market.Notification.Config;
import com.brian.market.auction.AuctionActivity;
import com.brian.market.auction.MyAuctionActivity;
import com.brian.market.bitcoin.BitCoinActivity;
import com.brian.market.cart.CartActivity;
import com.brian.market.cart.CartFragment;
import com.brian.market.messages.ChatActivity;
import com.brian.market.order.MyOrderActivity;
import com.brian.market.payment.BalanceActivity;
import com.brian.market.payment.PaymentActivity;
import com.brian.market.profile.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.strictmode.IntentReceiverLeakedViolation;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.autofill.AutofillValue;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.JsonObject;

import com.brian.market.payment.CardsActivity;
import com.brian.market.payment.WithdrawActivity;

import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.brian.market.profile.MyProductActivity;
import com.squareup.picasso.Picasso;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;

import me.riddhimanadib.library.BottomBarHolderActivity;
import me.riddhimanadib.library.BottomNavigationBar;
import me.riddhimanadib.library.NavigationPage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.Search.FragmentCatSubNSearch;
import com.brian.market.Settings.Settings;
import com.brian.market.helper.LocaleHelper;
import com.brian.market.home.helper.Location_popupModel;
import com.brian.market.payment.PackagesFragment;
import com.brian.market.profile.FragmentProfile;
import com.brian.market.signinorup.MainActivity;
import com.brian.market.utills.CircleTransform;
import com.brian.market.utills.GPSTracker;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static com.brian.market.utills.SettingsMain.getMainColor;

//import com.com.scriptsbundle.adforest.adapters.PlaceArrayAdapter;
//import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_ADDRESS;
//import static com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_REGIONS;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RuntimePermissionHelper.permissionInterface, BottomNavigationBar.BottomNavigationMenuClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, AdapterView.OnItemClickListener, MenuItem.OnMenuItemClickListener {

    public static Activity activity;
    public static Boolean checkLoading = false;
    boolean back_pressed = false;
    private MyReceiver receiver;
    MenuItem action_message;
    private List<NavigationPage> mNavigationPageList = new ArrayList<>();
    private BottomNavigationBar mBottomNav;

    @Override
    protected void onResume() {
        super.onResume();
//        if (settingsMain != null) {
//            if (!settingsMain.getUserVerified()) {
//                replaceFragment(new FragmentProfile(), "FragmentProfile");
//            }
//        }
    }


    ArrayList<String> strings = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    SettingsMain settingsMain;
    ImageView imageViewProfile;
    UpdateFragment updatfrag;
    TextView textViewUserName, title;
    RestService restService;
    AutoCompleteTextView currentLocationText;
    EditText MapBoxPlaces;
    FragmentHome fragmentHome;
    FragmentProfile fragmentProfile;
    GPSTracker gps;
    double latitude, longitude;
    RuntimePermissionHelper runtimePermissionHelper;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private PlacesClient placesClient;
    String location, locationIdHomePOpup, locationIdHomePOpupName;
    String imageView;
    double lat_by_mapbox, lon_by_mapbox;
    String address_by_mapbox;
    Toolbar toolbar;
    List<NavigationPage> navigationPages;

    public void updateApi(UpdateFragment listener) {
        updatfrag = listener;
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        System.gc();
        settingsMain = new SettingsMain(this);
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);
        runtimePermissionHelper.requestLocationPermission(3);
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("UserLogin");
        activity = this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(getMainColor())));

        fab.setOnClickListener(view -> {
            runtimePermissionHelper.requestLocationPermission(2);
        });

//        FloatingActionButton homeFloatingActionButton = (FloatingActionButton) findViewById(R.id.home);
//        homeFloatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(getMainColor())));
//        homeFloatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {

            checkLoading = true;
            adforest_swipeRefresh();

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        restService = UrlController.createService(RestService.class);// for test
        if (!settingsMain.getAppOpen()) {
//            fab.setVisibility(View.VISIBLE);
//            homeFloatingActionButton.setVisibility(View.GONE);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getApplicationContext(), settingsMain.getNoLoginMessage(), Toast.LENGTH_LONG).show();

                }
            });
        }
//        } else {
//            restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);
////for test
//        }
        if (header != null) {
            TextView textViewUserEmail = header.findViewById(R.id.textView);
            textViewUserName = header.findViewById(R.id.username);
            imageViewProfile = header.findViewById(R.id.imageView);
            TextView viewProfile = header.findViewById(R.id.view_profile);

            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }
            });

            int[] colors = {Color.parseColor(getMainColor()), Color.parseColor(getMainColor())};
            //create a new gradient color
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT, colors);
            gd.setCornerRadius(0f);

            header.setBackground(gd);

            if (!TextUtils.isEmpty(settingsMain.getUserEmail())) {
                textViewUserEmail.setText(settingsMain.getUserEmail());
            }
            if (!TextUtils.isEmpty(settingsMain.getUserName())) {
                textViewUserName.setText(settingsMain.getUserName());
            }
            if (!settingsMain.getAppOpen()) {
                if (!TextUtils.isEmpty(settingsMain.getGuestImage())) {
                    Picasso.with(this).load(settingsMain.getGuestImage())
                            .transform(new CircleTransform())
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.placeholder)
                            .into(imageViewProfile);
                }
            } else {
                if (!TextUtils.isEmpty(settingsMain.getUserImage())) {
                    Picasso.with(this).load(settingsMain.getUserImage())
                            .transform(new CircleTransform())
                            .error(R.drawable.placeholder)
                            .placeholder(R.drawable.placeholder)
                            .into(imageViewProfile);
                }
            }
        }

        locationIdHomePOpup = getIntent().getStringExtra("location_id");
        locationIdHomePOpupName = getIntent().getStringExtra("location_name");

        IntentFilter filter = new IntentFilter(Config.PUSH_NOTIFICATION);
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);

    }

    private void initBottomNavigation() {
        NavigationPage page1 = new NavigationPage("Home", ContextCompat.getDrawable(this, R.drawable.ic_home_black_24dp), FragmentHome.newInstance());
        NavigationPage page2 = new NavigationPage("Wishlist", ContextCompat.getDrawable(this, R.drawable.ic_favorite_border), FragmentWishlist.newInstance());
        NavigationPage page3 = new NavigationPage("My Cart", ContextCompat.getDrawable(this, R.drawable.ic_shopping_bag), CartFragment.newInstance());
        NavigationPage page4 = new NavigationPage("Sell", ContextCompat.getDrawable(this, R.drawable.ic_photo_camera), AddNewProductFragment.newInstance());

        navigationPages = new ArrayList<>();
        navigationPages.add(page1);
        navigationPages.add(page2);
        navigationPages.add(page3);
        navigationPages.add(page4);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void adforest_swipeRefresh() {
        String fragment = null;
        swipeRefreshLayout.setRefreshing(true);
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameContainer);
        if (currentFragment instanceof FragmentHome) {
            fragment = "FragmentHome";
        }

        if (currentFragment instanceof FragmentWishlist) {
            fragment = "FragmentWishlist";
        }
//
        if (currentFragment instanceof CartFragment) {
            fragment = "CartFragment";
        }

        if (currentFragment instanceof FragmentAllLocations) {
            fragment = "FragmentAllLocations";
        }

        if (currentFragment instanceof FragmentCatSubNSearch) {
            fragment = "FragmentCatSubNSearch";
        }

        if(fragment != null) {
            Handler handler = new Handler();
            final String finalFragment = fragment;
//            SettingsMain.showDilog(activity);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    SettingsMain.reload(HomeActivity.this, finalFragment);
                    swipeRefreshLayout.setRefreshing(false);
//                    if(!finalFragment.equals("FragmentHome"))
//                        SettingsMain.hideDilog();
                }
            }, 500);
        }
    }

    private void adforest_showNotificationDialog(String title, String message, String image) {

        final Dialog dialog;
        dialog = new Dialog(HomeActivity.this, R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_notification_layout);
        ImageView imageView = dialog.findViewById(R.id.notificationImage);
        TextView tv_title = dialog.findViewById(R.id.notificationTitle);
        TextView tV_message = dialog.findViewById(R.id.notificationMessage);
        Button button = dialog.findViewById(R.id.cancel_button);
        button.setText(settingsMain.getGenericAlertCancelText());
        button.setBackgroundColor(Color.parseColor(getMainColor()));


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));


        if (!TextUtils.isEmpty(image)) {
            Picasso.with(this).load(image)
                    .fit()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }

        tv_title.setText(title);
        tV_message.setText(message);

        button.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    public void changeImage() {
        if (!TextUtils.isEmpty(settingsMain.getUserImage())) {
            Picasso.with(this).load(settingsMain.getUserImage())
                    .transform(new CircleTransform())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(imageViewProfile);
        }
        textViewUserName.setText(settingsMain.getUserName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        action_message = menu.findItem(R.id.action_message);

        MenuItem action_home = menu.findItem(R.id.action_home);
        MenuItem action_cart = menu.findItem(R.id.action_cart);
        MenuItem action_wishlist = menu.findItem(R.id.action_wishlist);
        MenuItem action_search = menu.findItem(R.id.action_search);
        MenuItem action_location = menu.findItem(R.id.action_location);

        action_home.setOnMenuItemClickListener(this);
        action_cart.setOnMenuItemClickListener(this);
        action_wishlist.setOnMenuItemClickListener(this);
        action_message.setOnMenuItemClickListener(this);
        action_search.setOnMenuItemClickListener(this);
        action_location.setOnMenuItemClickListener(this);

//        searchViewItem.setOnMenuItemClickListener(item -> {
//            runtimePermissionHelper.requestLocationPermission(1);
//            return true;
//        });

        return super.onCreateOptionsMenu(menu);
    }

    private void manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();

//        request.setCountry("US");
        request.setTypeFilter(TypeFilter.REGIONS);
        request.setSessionToken(token)
                .setQuery(query);


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

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(HomeActivity.this, android.R.layout.simple_dropdown_item_1line, data);
            currentLocationText.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


    }

    public void setupBottomBarHolderActivity(List<NavigationPage> pages) {

        // throw error if pages does not have 4 elements
        if (pages.size() != 4) {
            throw new RuntimeException("List of NavigationPage must contain 4 members.");
        } else {
            mNavigationPageList = pages;
            mBottomNav = new BottomNavigationBar(this, pages, this);
            startFragment(mNavigationPageList.get(0).getFragment(), "FragmentHome");
        }

    }

    public void startFragment(Fragment someFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = someFragment;
            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment, tag).commit();
        }
    }

    public void replaceFragment(Fragment someFragment, String tag) {

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);
        Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

        if (fragment != fragment2) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
            transaction.replace(R.id.frameContainer, someFragment, tag);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String placeId = ids.get(position);
        List<com.google.android.libraries.places.api.model.Place.Field> placeFields = Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.LAT_LNG);

// Construct a request object, passing the place ID and fields array.
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields)
                .build();
// Add a listener to handle the response.
        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            com.google.android.libraries.places.api.model.Place place = response.getPlace();
            Log.i("Places", "Place found: " + place.getLatLng().latitude + " " + place.getLatLng().longitude);
            longitude = place.getLatLng().longitude;
            latitude = place.getLatLng().latitude;

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
// Handle error with given status code.
                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });


    }

    private void adforest_loctionSearch() {

        gps = new GPSTracker(HomeActivity.this);

        List<Address> addresses1 = null;
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Location_popupModel Location_popupModel = settingsMain.getLocationPopupModel(this);

            final Dialog dialog = new Dialog(HomeActivity.this, R.style.customDialog);

            dialog.setCanceledOnTouchOutside(true);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_location_seekbar);

            final BubbleSeekBar bubbleSeekBar = dialog.findViewById(R.id.seakBar);
            bubbleSeekBar.getConfigBuilder()
                    .max(Location_popupModel.getSlider_number())
                    .sectionCount(Location_popupModel.getSlider_step())
                    .secondTrackColor(Color.parseColor(getMainColor()))
                    .build();


            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));
            Button Send = dialog.findViewById(R.id.send_button);
            Button Cancel = dialog.findViewById(R.id.cancel_button);
            TextView locationText = dialog.findViewById(R.id.locationText);

            currentLocationText = dialog.findViewById(R.id.et_location);

            placesClient = com.google.android.libraries.places.api.Places.createClient(this);
            currentLocationText.setHint("Please input city");
            currentLocationText.setOnItemClickListener(this);
            currentLocationText.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    manageAutoComplete(s.toString());

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            Send.setText("Search");
            Cancel.setText(Location_popupModel.getBtn_clear());
            locationText.setText(Location_popupModel.getText());

            Send.setBackgroundColor(Color.parseColor(getMainColor()));
            Cancel.setBackgroundColor(Color.parseColor(getMainColor()));

            Send.setOnClickListener(v -> {
                adforest_changeNearByStatus(Double.toString(latitude), Double.toString(longitude),
                        Integer.toString(bubbleSeekBar.getProgress()));
                dialog.dismiss();
            });
            Cancel.setOnClickListener(v -> {
//                adforest_changeNearByStatus("", ""
//                        , Integer.toString(bubbleSeekBar.getProgress()));
                dialog.dismiss();
            });

            dialog.show();
        } else
            gps.showSettingsAlert();
    }

    @Override
    public void onClickedOnBottomNavigationMenu(int menuType) {
        Fragment fragment = null;
        String tag = null;
        switch (menuType) {
            case BottomNavigationBar.MENU_BAR_1:
                fragment = mNavigationPageList.get(0).getFragment();
                tag="FragmentHome";
                toolbar.setTitle(R.string.app_name);
                break;
            case BottomNavigationBar.MENU_BAR_2:
                fragment = mNavigationPageList.get(1).getFragment();
                tag="FragmentWishlist";
                toolbar.setTitle("Wishlist");
                break;
            case BottomNavigationBar.MENU_BAR_3:
                fragment = mNavigationPageList.get(2).getFragment();
                tag="CartFragment";
                toolbar.setTitle("My Cart");
                break;
            case BottomNavigationBar.MENU_BAR_4:
//                fragment = mNavigationPageList.get(3).getFragment();
                if (settingsMain.getAppOpen()) {
                    runtimePermissionHelper.requestLocationPermission(2);
                } else {
                    //Alert dialog for exit form Home screen
                    AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                    alert.setTitle(settingsMain.getAlertDialogTitle("info"));
                    alert.setCancelable(false);
                    alert.setMessage("You can't post now. please signin into app.");
                    alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
//                    FragmentProfile fragmentProfile = new FragmentProfile();
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.frameContainer, fragmentProfile).addToBackStack(null).commit();
                        dialog.dismiss();
                    });
                    alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
                    alert.show();
                }

                break;
        }

        // replacing fragment with the current one
        if (fragment != null) {
            replaceFragment(fragment, tag);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 35) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                CarmenFeature feature = PlaceAutocomplete.getPlace(data);
                Point point = feature.center();
                latitude = point.latitude();
                longitude = point.longitude();
                address_by_mapbox = feature.placeName();
                Log.d("karwao", address_by_mapbox.toString());
                MapBoxPlaces.setText(address_by_mapbox);

            }
        }
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            backPressed();
//            super.onBackPressed();
            overridePendingTransition(R.anim.left_enter, R.anim.right_out);
        }
    }

    private void backPressed() {
        if (!back_pressed) {
            Toast.makeText(HomeActivity.this, "Press Again To Exit", Toast.LENGTH_SHORT).show();
            back_pressed = true;
            android.os.Handler mHandler = new android.os.Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    back_pressed = false;
                }
            }, 2000L);
        } else {
            //Alert dialog for exit form login screen

            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle(settingsMain.getAlertDialogTitle("info"));
            alert.setCancelable(false);
            alert.setMessage("Are you sure you want to exit?");
            alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                HomeActivity.this.finishAffinity();
                dialog.dismiss();
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            });
            alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
            alert.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        Log.d("info onDestroy called", "onDestroy");
    }

    @Override
    protected void onRestart() {

        super.onRestart();

//        adforest_swipeRefresh();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setChecked(true);

        switch(id){
            case R.id.home:
                FragmentManager fm = HomeActivity.this.getSupportFragmentManager();
                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                break;

            case R.id.profile:
//                replaceFragment(new FragmentProfile(), "FragmentProfile");
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                break;
            case R.id.auctions:
                startActivity(new Intent(HomeActivity.this, AuctionActivity.class));
                break;
            case R.id.myAuction:
                startActivity(new Intent(HomeActivity.this, MyAuctionActivity.class));
                break;
            case R.id.message:
                Intent intent1 = new Intent(getApplicationContext(), ChatActivity.class);
                intent1.putExtra("receiverId", "");
                startActivity(intent1);
                break;
            case R.id.payment:
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.coin_deposit:
                startActivity(new Intent(getApplicationContext(), BitCoinActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.myOrders:
                startActivity(new Intent(getApplicationContext(), MyOrderActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.withdraw:
                startActivity(new Intent(getApplicationContext(), WithdrawActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.balance:
                startActivity(new Intent(getApplicationContext(), BalanceActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.myProducts:
//                replaceFragment(new MyAds(), "MyAds");
                startActivity(new Intent(getApplicationContext(), MyProductActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.nav_settings:
                startActivity(new Intent(getApplicationContext(), Settings.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.nav_log_in:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                break;
            case R.id.nav_log_out:
                AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
                alert.setTitle(getBaseContext().getResources().getString(R.string.app_name));
                alert.setCancelable(false);
                alert.setIcon(R.mipmap.ic_launcher);
                alert.setMessage("Do you want to logout?");
                alert.setPositiveButton(settingsMain.getAlertOkText(), (dialog, which) -> {
                    settingsMain.setUserLogin("0");
//                    settingsMain.setUserEmail("");
                    settingsMain.setUserName("");
                    settingsMain.setAuthToken("");
                    settingsMain.setFireBaseId("");
                    FirebaseAuth.getInstance().signOut();
                    HomeActivity.this.finish();
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_enter, R.anim.left_out);
//                    adforest_AddFirebaseid();
                    settingsMain.setAppOpen(false);
                    dialog.dismiss();
                });
                alert.setNegativeButton(settingsMain.getAlertCancelText(), (dialogInterface, i) -> dialogInterface.dismiss());
                alert.show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onSuccessPermission(int code) {

        switch (code){
            case 1:
                adforest_loctionSearch();
                break;
            case 2:
                Intent intent = new Intent(HomeActivity.this, AddNewProductPost.class);
                startActivity(intent);
                break;
            case 3:
                initBottomNavigation();
                setupBottomBarHolderActivity(navigationPages);
                break;
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
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                startActivity(intent);
                break;
            case R.id.action_cart:
                Intent intent2 = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent2);
                break;
            case R.id.action_wishlist:
                startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
                break;
            case R.id.action_search:
                search(item);
                break;
            case R.id.action_location:
                runtimePermissionHelper.requestLocationPermission(1);
                break;
            case R.id.action_message:
                item.setVisible(false);
                Intent intent1 = new Intent(HomeActivity.this, ChatActivity.class);
                intent1.putExtra("receiverId", "");
                startActivity(intent1);
                break;
        }
        return false;
    }

    private void search(MenuItem item) {
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Search Good...");
//        searchView.setBackgroundColor(WHITE);
        searchView.setIconified(true);
        searchView.setBackground(getResources().getDrawable(R.drawable.search_bar));
        searchView.setSubmitButtonEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            searchView.autofill(AutofillValue.forList(View.AUTOFILL_TYPE_TOGGLE));
        }
        SearchView.SearchAutoComplete mSearchSrcTextView = searchView.findViewById(R.id.search_src_text);
//        mSearchSrcTextView.setBackground(getResources().getDrawable(R.drawable.rounded_corner_white_rectangle_border_grey));
        mSearchSrcTextView.setHeight(15);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mSearchSrcTextView.autofill(AutofillValue.forList(View.AUTOFILL_TYPE_TOGGLE));
        }
        mSearchSrcTextView.setTextColor(Color.parseColor("#8B008B"));
        mSearchSrcTextView.setHintTextColor(Color.parseColor("#8B008B"));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                if (!query.equals("")) {

                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fragment = fm.findFragmentByTag("FragmentCatSubNSearch");
                    Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

                    FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", "");
                    bundle.putString("title", query);

                    fragment_search.setArguments(bundle);

                        replaceFragment(fragment_search, "FragmentCatSubNSearch");
                    if (fragment != fragment2) {
                        return true;
                    } else {
                        updatfrag.updatefrag(query);
                        return true;
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void adforest_changeNearByStatus(final String nearby_latitude,
                                             final String nearby_longitude, final String nearby_distance) {
        if(currentLocationText.getText().toString().equals("") && nearby_distance.equals("0")){
            Toast.makeText(this, "Please type city or choose distance", Toast.LENGTH_SHORT).show();
            return;
        }

        if (SettingsMain.isConnectingToInternet(this)) {
//            SettingsMain.hideDilog();
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("FragmentCatSubNSearch");
            Fragment fragment2 = fm.findFragmentById(R.id.frameContainer);

            FragmentCatSubNSearch fragment_search = new FragmentCatSubNSearch();
            Bundle bundle = new Bundle();
            bundle.putString("nearby_latitude", nearby_latitude);
            bundle.putString("nearby_longitude", nearby_longitude);
            bundle.putString("nearby_distance", nearby_distance);
            bundle.putString("city", currentLocationText.getText().toString());

            settingsMain.setLatitude(nearby_latitude);
            settingsMain.setLongitude(nearby_longitude);
            settingsMain.setDistance(nearby_distance);

            fragment_search.setArguments(bundle);

            if (fragment != fragment2) {
                replaceFragment(fragment_search, "FragmentCatSubNSearch");
            } else {
                updatfrag.updatefrag(nearby_latitude, nearby_longitude, nearby_distance, currentLocationText.getText().toString());
            }
        } else {
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    public interface UpdateFragment {
        void updatefrag(String s);

        void updatefrag(String latitude, String longitude, String distance, String city);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "Intent Detected.", Toast.LENGTH_LONG).show();
            String img = intent.getStringExtra("img");
            String text = intent.getStringExtra("text");
            String name = intent.getStringExtra("name");
            String channel = intent.getStringExtra("channel");
            String senderId = intent.getStringExtra("sender");

            action_message.setVisible(true);

//            menu.findItem(R.id.action_message).setVisible(true);
        }
    }
}