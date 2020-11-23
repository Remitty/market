package com.brian.market.signinorup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import de.hdodenhof.circleimageview.CircleImageView;
import io.michaelrocks.libphonenumber.android.NumberParseException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.SplashScreen;
import com.brian.market.home.HomeActivity;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.JsonObject;
import com.phonenumberui.CountryCodeActivity;
import com.phonenumberui.countrycode.Country;
import com.phonenumberui.countrycode.CountryUtils;
import com.phonenumberui.utility.Utility;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.INVISIBLE;

public class ProfileCompleteActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, RuntimePermissionHelper.permissionInterface, AdapterView.OnItemClickListener {
    private SettingsMain settingsMain;
    TextView verifyBtn, textViewUserName, textViewUserEmail,  textViewLastLogin;
    TextView textViewAdsSold, textViewTotalList, textViewInactiveAds,textViewExppiry;
    TextView textViewName, textViewLocation, textViewMainTitle, textViewImage,
            textViewAccType, textViewIntroduction;
    TextView btnCancel, btnSave, btnDeleteAccount;
    ImageView btnSeletImage;
    EditText  editTextAddress1, editTextAddress2, editTextCity,
            editTextIntroduction,editTextPostalCode;
    AutoCompleteTextView autoCountry, autoState;
    CircleImageView imageViewProfile;
    JSONObject jsonObjectforCustom, extraText;
    Spinner spinnerACCType;
    LinearLayout viewSocialIconsLayout;
    RestService restService;
    boolean checkValidation = true;
    LinearLayout publicProfileCustomIcons;
    RuntimePermissionHelper runtimePermissionHelper;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private JSONObject jsonObject;
    private JSONObject jsonObjectChnge, jsonObject_select_pic, deleteAccountJsonObject;
    private PlacesClient placesClient;
    private CardView profileCard;
    private FrameLayout frameLayout;
    private String firstName, lastName, email;
    private AppCompatEditText etCountryCode;
    private AppCompatEditText etPhoneNumber;
    private ImageView imgFlag;
    private Country mSelectedCountry;
    private static final int COUNTRYCODE_ACTION = 10001;
    private PhoneNumberUtil mPhoneUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_complete);

        settingsMain = new SettingsMain(this);

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);
//        publicProfileCustomIcons = findViewById(R.id.publicProfileCustomIcons);
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);

//        if (getSupportActionBar() != null)
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));
        getSupportActionBar().setTitle("Complete Profile");

        initComponents();
        initListeners();

        textViewUserEmail.setText(settingsMain.getUserEmail());
        textViewUserName.setText(settingsMain.getUserName());

        setPhoneUI();

    }

    private void setPhoneUI() {
        mPhoneUtil = PhoneNumberUtil.createInstance(ProfileCompleteActivity.this);
        TelephonyManager tm = (TelephonyManager) getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryISO = tm.getNetworkCountryIso();
        String countryNumber = "";
        String countryName = "";
        Utility.log(countryISO);

        if(!TextUtils.isEmpty(countryISO))
        {
            for (Country country : CountryUtils.getAllCountries(ProfileCompleteActivity.this)) {
                if (countryISO.toLowerCase().equalsIgnoreCase(country.getIso().toLowerCase())) {
                    countryNumber = country.getPhoneCode();
                    countryName = country.getName();
                    break;
                }
            }
            Country country = new Country(countryISO,
                    countryNumber,
                    countryName);
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);
        }
        else {
            Country country = new Country(getString(com.phonenumberui.R.string.country_united_states_code),
                    getString(com.phonenumberui.R.string.country_united_states_number),
                    getString(com.phonenumberui.R.string.country_united_states_name));
            this.mSelectedCountry = country;
            etCountryCode.setText("+" + country.getPhoneCode() + "");
            imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
            Utility.log(countryNumber);
        }

        setPhoneNumberHint();
        etCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.hideKeyBoardFromView(ProfileCompleteActivity.this);
                etPhoneNumber.setError(null);
                Intent intent = new Intent(ProfileCompleteActivity.this, CountryCodeActivity.class);
                intent.putExtra("TITLE", getResources().getString(com.phonenumberui.R.string.app_name));
                startActivityForResult(intent, COUNTRYCODE_ACTION);
            }
        });
    }
    private void setPhoneNumberHint() {
        if (mSelectedCountry != null) {
            Phonenumber.PhoneNumber phoneNumber =
                    mPhoneUtil.getExampleNumberForType(mSelectedCountry.getIso().toUpperCase(),
                            PhoneNumberUtil.PhoneNumberType.MOBILE);
            if (phoneNumber != null) {
                String format = mPhoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                if (format.length() > mSelectedCountry.getPhoneCode().length())
                    etPhoneNumber.setHint(
                            format.substring((mSelectedCountry.getPhoneCode().length() + 1), format.length()));
            }
        }
    }
    private void initListeners() {
        btnSeletImage.setOnClickListener(view1 -> runtimePermissionHelper.requestStorageCameraPermission(1));

        btnSave.setOnClickListener(view12 -> checkValidation());
        btnCancel.setOnClickListener(view13 -> this.onBackPressed());

        editTextIntroduction.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });

//        SwipeRefreshLayout swipeRefreshLayout = this.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setEnabled(false);
        placesClient = com.google.android.libraries.places.api.Places.createClient(getBaseContext());
        autoCountry.setOnItemClickListener(this);
        autoState.setOnItemClickListener(this);

        autoCountry.addTextChangedListener(new TextWatcher() {

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
        autoState.addTextChangedListener(new TextWatcher() {

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
    }

    private void initComponents() {
        textViewName = findViewById(R.id.textViewName);
        textViewLocation = findViewById(R.id.textViewLocation);
        textViewAccType = findViewById(R.id.textViewAccount_type);
        textViewMainTitle = findViewById(R.id.textView);
        textViewIntroduction = findViewById(R.id.textViewIntroduction);
        textViewImage = findViewById(R.id.textViewSetImage);
        btnCancel = findViewById(R.id.textViewCancel);
        btnSeletImage = findViewById(R.id.imageSelected);
        btnDeleteAccount = findViewById(R.id.deleteAccount);
        textViewLastLogin = findViewById(R.id.loginTime);
        verifyBtn = findViewById(R.id.verified);
        textViewUserName = findViewById(R.id.text_viewName);
        textViewUserEmail = findViewById(R.id.text_viewEmail);
        spinnerACCType = findViewById(R.id.spinner);

        imageViewProfile = findViewById(R.id.image_view);
        viewSocialIconsLayout = findViewById(R.id.editProfileCustomLayout);
        viewSocialIconsLayout.setVisibility(INVISIBLE);

        textViewAdsSold = findViewById(R.id.share);
        textViewTotalList = findViewById(R.id.addfav);
        textViewInactiveAds = findViewById(R.id.report);
        textViewExppiry = findViewById(R.id.expired);

        editTextPostalCode = findViewById(R.id.editTextPostalCode);
        editTextAddress1 = findViewById(R.id.editTextAddress1);
        editTextAddress2 = findViewById(R.id.editTextAddress2);
        editTextCity = findViewById(R.id.editTextCity);

        autoCountry = findViewById(R.id.editTextCountry);
        autoState = findViewById(R.id.editTextState);

        editTextIntroduction = (EditText) findViewById(R.id.textArea_information);

        profileCard = findViewById(R.id.profile_card);
        frameLayout = findViewById(R.id.frameLayout);
        btnSave = findViewById(R.id.btnSave);

        etCountryCode = findViewById(com.phonenumberui.R.id.etCountryCode);
        etPhoneNumber = findViewById(com.phonenumberui.R.id.etPhoneNumber);
        imgFlag = findViewById(com.phonenumberui.R.id.flag_imv);

    }

    private void checkValidation() {

        boolean validFlag = true;
        // Check if all strings are null or not
        if (editTextAddress1.getText().toString().equals("") || editTextAddress1.getText().toString().length() == 0) {
            editTextAddress1.setError("!");
            validFlag = false;
        }

        if (etPhoneNumber.getText().toString().equals("") || etPhoneNumber.getText().toString().length() == 0) {
            etPhoneNumber.setError("!");
            validFlag = false;
        }
        if (autoState.getText().toString().equals("") || autoState.getText().toString().length() == 0) {
            autoState.setError("!");
            validFlag = false;
        }
        if (editTextPostalCode.getText().toString().equals("") || editTextPostalCode.getText().toString().length() == 0) {
            editTextPostalCode.setError("!");
            validFlag = false;
        }

        if (validFlag && validatePhone()) {
            adforest_sendData();
        }
    }
    private void manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();

        if (SplashScreen.gmap_has_countries) {
            request.setCountry(SplashScreen.gmap_countries);
        }
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

            if(getBaseContext() != null) {
                ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, data);
                autoState.setAdapter(adapter);
                autoCountry.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
            }
        });


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
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
// Handle error with given status code.
                Log.e("Places", "Place not found: " + exception.getMessage());
            }
        });


    }

    private void adforest_sendData() {
        checkValidation = true;

        if (SettingsMain.isConnectingToInternet(this)) {

            JsonObject params = new JsonObject();

            params.addProperty("mobile", etPhoneNumber.getText().toString());
            params.addProperty("country_code", etCountryCode.getText().toString());
            params.addProperty("address1", editTextAddress1.getText().toString());
            params.addProperty("address2", editTextAddress2.getText().toString());
//            params.addProperty("city", editTextCity.getText().toString());
//            params.addProperty("country", autoCountry.getText().toString());
            params.addProperty("state", autoState.getText().toString());
            params.addProperty("postalcode", editTextPostalCode.getText().toString());
//            params.addProperty(spinnerACCType.getTag().toString(), subDiloglist.getId());
//            params.addProperty(editTextIntroduction.getTag().toString(), editTextIntroduction.getText().toString());

            Log.d("info Send UpdatePofile", "" + params.toString());
            if (!checkValidation) {
                Toast.makeText(getBaseContext(), "Invalid URL specified", Toast.LENGTH_SHORT).show();
            } else {
                SettingsMain.showDilog(this);
                Call<ResponseBody> req = restService.postUpdateProfile(params, UrlController.UploadImageAddHeaders(this));
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseobj) {
                        try {
                            Log.d("info UpdateProfile Resp", "" + responseobj.toString());
                            if (responseobj.isSuccessful()) {

                                JSONObject response = new JSONObject(responseobj.body().string());
                                if (response.getBoolean("success")) {
                                    Log.d("info UpdateProfile obj", "" + response.toString());
                                    JSONObject data = response.getJSONObject("data");
                                    settingsMain.setUserName(data.getString("first_name") + " " + data.getString("last_name"));
                                    settingsMain.setUserPhone(data.getString("country_code")+data.getString("mobile"));
                                    settingsMain.setUserImage(response.getJSONObject("data").getString("picture"));
//                                    ((HomeActivity) this).changeImage();

                                    Toast.makeText(getBaseContext(), "Completed profile successfully", Toast.LENGTH_SHORT).show();

                                    settingsMain.setProfileComplete(true);
                                    startActivity(new Intent(ProfileCompleteActivity.this, WelcomeActivity.class));
                                } else {
                                    Toast.makeText(getBaseContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(getBaseContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            SettingsMain.hideDilog();
                        }
                        if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                            Toast.makeText(getBaseContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            SettingsMain.hideDilog();
                        }
                        if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                            Log.d("info UpdateProfile", "NullPointert Exception" + t.getLocalizedMessage());
                            SettingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info UpdateProfile err", String.valueOf(t));
                            Log.d("info UpdateProfile err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            }

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validatePhone() {
        if (TextUtils.isEmpty(etPhoneNumber.getText().toString().trim())) {
            etPhoneNumber.setError("Please enter phone number");
            etPhoneNumber.requestFocus();
            return false;
        } else if (!isValid()) {
            etPhoneNumber.setError("Please enter valid phone number");
            etPhoneNumber.requestFocus();
            return false;
        }
        return true;
    }

    public boolean isValid() {
        Phonenumber.PhoneNumber phoneNumber = getPhoneNumber();
        return phoneNumber != null && mPhoneUtil.isValidNumber(phoneNumber);
    }

    public Phonenumber.PhoneNumber getPhoneNumber() {
        try {
            String iso = null;
            if (mSelectedCountry != null) {
                iso = mSelectedCountry.getIso().toUpperCase();
            }
            return mPhoneUtil.parse(etPhoneNumber.getText().toString().trim(), iso);
        } catch (NumberParseException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

    private void selectImage() {

        final CharSequence[] items;
        items = new CharSequence[]{"camera", "local" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Load Image");
        builder.setItems(items, (dialog, item) -> {
            if (item == 0) {
                {
                    userChoosenTask = "Take Photo";
                    cameraIntent();
                }
            } else if (item == 1) {
                {
                    userChoosenTask = "Choose from Library";
                    galleryIntent();
                }
            } else if (item == 3) {
                dialog.dismiss();
            }

        });
        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        Uri tempUri = SettingsMain.getImageUri(this, thumbnail);
        File finalFile = new File(SettingsMain.getRealPathFromURI(this, tempUri));
        galleryImageUpload(tempUri);
        btnSeletImage.setImageURI(tempUri);
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                Uri tempUri = SettingsMain.getImageUri(this, bm);

                galleryImageUpload(tempUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        btnSeletImage.setImageBitmap(bm);
    }

    private void galleryImageUpload(final Uri absolutePath) {

        if (SettingsMain.isConnectingToInternet(this)) {

            SettingsMain.showDilog(this);
            final File finalFile = new File(SettingsMain.getRealPathFromURI(this, absolutePath));
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getBaseContext().getContentResolver().getType(absolutePath)),
                            finalFile
                    );
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("profile_img", finalFile.getName(), requestFile);

            Call<ResponseBody> req = restService.postUploadProfileImage(body, UrlController.UploadImageAddHeaders(this));
            req.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    Log.v("info Upload Responce", response.toString());
                    if (response.isSuccessful()) {
                        JSONObject responseobj = null;
                        try {
                            responseobj = new JSONObject(response.body().string());
                            if (responseobj.getBoolean("success")) {
                                try {
                                    Toast.makeText(getBaseContext(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //noinspection ResultOfMethodCallIgnored
                                finalFile.delete();
                            } else {
                                Toast.makeText(getBaseContext(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            SettingsMain.hideDilog();
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                            SettingsMain.hideDilog();
                        }

                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getBaseContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getBaseContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Upload profile", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info Upload profile err", String.valueOf(t));
                        Log.d("info Upload profile err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(this, "Internet error", Toast.LENGTH_SHORT).show();
        }


    }

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.right_enter, R.anim.left_out, R.anim.left_enter, R.anim.right_out);
        transaction.replace(R.id.frameContainer, someFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSuccessPermission(int code) {
        selectImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }

        if (requestCode == COUNTRYCODE_ACTION) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.hasExtra("COUNTRY")) {
                        Country country = (Country) data.getSerializableExtra("COUNTRY");
                        this.mSelectedCountry = country;
                        setPhoneNumberHint();
                        etCountryCode.setText("+" + country.getPhoneCode() + "");
                        imgFlag.setImageResource(CountryUtils.getFlagDrawableResId(country.getIso()));
                    }
                }
            }
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getBaseContext(),
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
        return;
//        super.onBackPressed();
//        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return false;
//        onBackPressed();
//        return true;
    }
}
