package com.brian.market.profile;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.brian.market.models.ProfileModel;
import com.facebook.login.LoginManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.cardview.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
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
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

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

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.SplashScreen;
import com.brian.market.home.HomeActivity;
import com.brian.market.signinorup.MainActivity;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class EditProfile extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, RuntimePermissionHelper.permissionInterface, AdapterView.OnItemClickListener {

    SettingsMain settingsMain;
    TextView verifyBtn, textViewRateNo, textViewUserName, textViewLastLogin;
    TextView textViewAdsSold, textViewTotalList, textViewInactiveAds,textViewExppiry;
    TextView textViewName, textViewPhoneNumber, textViewLocation, textViewMainTitle, textViewImage,
            textViewAccType, textViewIntroduction;
    TextView btnCancel, btnSend, btnChangePwd, btnDeleteAccount;
    ImageView btnSeletImage;
    EditText editTextName, editTextLastName, editTextEmail, editTextPhone, editTextAddress1, editTextAddress2, editTextCity,
            editTextIntroduction,editTextPostalCode, editTextAccount, editTextHolder, editTextRouting, editTextBank;
    List<View> allViewInstanceforCustom = new ArrayList<>();
    List<View> fieldsValidationforCustom = new ArrayList<>();
    AutoCompleteTextView autoCountry, autoState;
    RatingBar ratingBar;
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
    private CardView bankCard, profileCard;
    private FrameLayout frameLayout;
    private Button btnSave;
    private boolean verify = false;

    public EditProfile() {
        // Required empty public constructor
    }

    public EditProfile(boolean verify) {
        this.verify = verify;
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        settingsMain = new SettingsMain(getActivity());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
//        publicProfileCustomIcons = view.findViewById(R.id.publicProfileCustomIcons);
        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);
//        publicProfileCustomIcons.setVisibility(View.GONE);
//        publicProfileCustomIcons.setBackgroundResource(R.drawable.socialicons);

        textViewName = view.findViewById(R.id.textViewName);
        textViewPhoneNumber = view.findViewById(R.id.textViewPhone);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        textViewAccType = view.findViewById(R.id.textViewAccount_type);
        textViewMainTitle = view.findViewById(R.id.textView);
        textViewIntroduction = view.findViewById(R.id.textViewIntroduction);
        textViewImage = view.findViewById(R.id.textViewSetImage);
        btnCancel = view.findViewById(R.id.textViewCancel);
        btnSeletImage = view.findViewById(R.id.imageSelected);
        btnSend = view.findViewById(R.id.textViewSend);
        btnChangePwd = view.findViewById(R.id.textViewChangePwd);
        btnDeleteAccount = view.findViewById(R.id.deleteAccount);
        textViewLastLogin = view.findViewById(R.id.loginTime);
        verifyBtn = view.findViewById(R.id.verified);
        textViewRateNo = view.findViewById(R.id.numberOfRate);
        textViewUserName = view.findViewById(R.id.text_viewName);
        spinnerACCType = view.findViewById(R.id.spinner);

        imageViewProfile = view.findViewById(R.id.image_view);
        ratingBar = view.findViewById(R.id.ratingBar);
        viewSocialIconsLayout = view.findViewById(R.id.editProfileCustomLayout);
        viewSocialIconsLayout.setVisibility(View.INVISIBLE);

//        LayerDrawable stars = (LayerDrawable) this.ratingBar.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(Color.parseColor("#ffcc00"), PorterDuff.Mode.SRC_ATOP);

        textViewAdsSold = view.findViewById(R.id.share);
        textViewTotalList = view.findViewById(R.id.addfav);
        textViewInactiveAds = view.findViewById(R.id.report);
        textViewExppiry = view.findViewById(R.id.expired);

        editTextName = view.findViewById(R.id.editTextName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextPostalCode = view.findViewById(R.id.editTextPostalCode);
        editTextAccount = view.findViewById(R.id.editTextAccount);
        editTextBank = view.findViewById(R.id.editTextBank);
        editTextHolder = view.findViewById(R.id.editTextHolder);
        editTextRouting = view.findViewById(R.id.editTextRoute);
        editTextAddress1 = view.findViewById(R.id.editTextAddress1);
        editTextAddress2 = view.findViewById(R.id.editTextAddress2);
        editTextCity = view.findViewById(R.id.editTextCity);

        autoCountry = view.findViewById(R.id.editTextCountry);
        autoState = view.findViewById(R.id.editTextState);

        profileCard = view.findViewById(R.id.profile_card);
        bankCard = view.findViewById(R.id.bank_card);
        bankCard.setVisibility(View.GONE);
        frameLayout = view.findViewById(R.id.frameLayout);
        btnSave = view.findViewById(R.id.btnSave);

        btnSeletImage.setOnClickListener(view1 -> runtimePermissionHelper.requestStorageCameraPermission(1));
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()) {
                    bankCard.setVisibility(View.VISIBLE);
                    profileCard.setVisibility(View.GONE);
                    frameLayout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_enter));
                }
            }
        });
        btnSave.setOnClickListener(view12 -> adforest_sendData());
        btnCancel.setOnClickListener(view13 -> getActivity().onBackPressed());
        adforest_setAllViewsText();

//        ((HomeActivity) getActivity()).changeImage();

        btnChangePwd.setTextColor(Color.parseColor(settingsMain.getMainColor()));
        btnChangePwd.setOnClickListener(view14 -> adforest_showDilogChangePassword());

        editTextIntroduction = (EditText) view.findViewById(R.id.textArea_information);

        editTextIntroduction.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });
        btnDeleteAccount.setOnClickListener(v -> adforest_showDeteleDialog());

//        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setEnabled(false);
        placesClient = com.google.android.libraries.places.api.Places.createClient(getContext());
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
        return view;
    }

    private boolean checkValidation() {

        boolean validFlag = true;
        // Check if all strings are null or not
        Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
        Matcher m = p.matcher(editTextEmail.getText().toString());

        if (editTextName.getText().toString().equals("") || editTextName.getText().toString().length() == 0) {
            editTextName.setError("!");
            validFlag = false;
        }
        if (editTextLastName.getText().toString().equals("") || editTextLastName.getText().toString().length() == 0) {
            editTextLastName.setError("!");
            validFlag = false;
        }
        if (editTextEmail.getText().toString().equals("") || editTextEmail.getText().toString().length() == 0) {
            editTextEmail.setError("!");
            validFlag = false;
        }
        if (editTextAddress1.getText().toString().equals("") || editTextAddress1.getText().toString().length() == 0) {
            editTextAddress1.setError("!");
            validFlag = false;
        }
        if (editTextAddress1.getText().toString().equals("") || editTextAddress1.getText().toString().length() == 0) {
            editTextAddress1.setError("!");
            validFlag = false;
        }

        if (editTextCity.getText().toString().equals("") || editTextCity.getText().toString().length() == 0) {
            editTextCity.setError("!");
            validFlag = false;
        }
        if (editTextPhone.getText().toString().equals("") || editTextPhone.getText().toString().length() == 0) {
            editTextPhone.setError("!");
            validFlag = false;
        }
        if (editTextPostalCode.getText().toString().equals("") || editTextPostalCode.getText().toString().length() == 0) {
            editTextPostalCode.setError("!");
            validFlag = false;
        }

        return validFlag;
    }

    private void manageAutoComplete(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();

        if (SplashScreen.gmap_has_countries) {
            request.setCountry(SplashScreen.gmap_countries);
        }
//        if (settingsMain.getAlertDialogMessage("location_type").equals("regions")) {
//            request.setTypeFilter(TypeFilter.ADDRESS);
//        } else {
//            request
//                    .setTypeFilter(TypeFilter.REGIONS);
//        }
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

            if(getContext() != null) {
                ArrayAdapter<?> adapter = new ArrayAdapter<Object>(getContext(), android.R.layout.simple_dropdown_item_1line, data);
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

        if (SettingsMain.isConnectingToInternet(getActivity())) {


//            subcatDiloglist subDiloglist = (subcatDiloglist) spinnerACCType.getSelectedView().getTag();
            JsonObject params = new JsonObject();
            params.addProperty("first_name", editTextName.getText().toString());
            params.addProperty("last_name", editTextLastName.getText().toString());
            params.addProperty("email", editTextEmail.getText().toString());
            String phoneNumber = editTextPhone.getText().toString();
            params.addProperty("mobile", phoneNumber);

            if(!editTextAccount.getText().toString().equals("")) {
                params.addProperty("account_number", editTextAccount.getText().toString());
                params.addProperty("account_holder_name", editTextHolder.getText().toString());
                params.addProperty("account_routing_number", editTextRouting.getText().toString());
                params.addProperty("bank_name", editTextBank.getText().toString());
            }

            params.addProperty("address1", editTextAddress1.getText().toString());
            params.addProperty("address2", editTextAddress2.getText().toString());
            params.addProperty("city", editTextCity.getText().toString());
            params.addProperty("country", autoCountry.getText().toString());
            params.addProperty("state", autoState.getText().toString());
            params.addProperty("postalcode", editTextPostalCode.getText().toString());
//            params.addProperty(spinnerACCType.getTag().toString(), subDiloglist.getId());
//            params.addProperty(editTextIntroduction.getTag().toString(), editTextIntroduction.getText().toString());

            Log.d("info Send UpdatePofile", "" + params.toString());
            if (!checkValidation) {
                Toast.makeText(getContext(), "Invalid URL specified", Toast.LENGTH_SHORT).show();
            } else {
                SettingsMain.showDilog(getActivity());
                Call<ResponseBody> req = restService.postUpdateProfile(params, UrlController.UploadImageAddHeaders(getActivity()));
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
//                                    ((HomeActivity) getActivity()).changeImage();

                                    Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                    if(!verify)
                                        SettingsMain.reload(getActivity(), "EditProfile");
                                    else{
                                        startActivity(new Intent(getActivity(), HomeActivity.class));
                                    }
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
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectImage() {

        final CharSequence[] items;
        items = new CharSequence[]{"camera", "local" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {

        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        Uri tempUri = SettingsMain.getImageUri(getActivity(), thumbnail);
        File finalFile = new File(SettingsMain.getRealPathFromURI(getActivity(), tempUri));
        galleryImageUpload(tempUri);
        btnSeletImage.setImageURI(tempUri);
    }

    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Uri tempUri = SettingsMain.getImageUri(getActivity(), bm);

                galleryImageUpload(tempUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        btnSeletImage.setImageBitmap(bm);
    }

    private void galleryImageUpload(final Uri absolutePath) {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());
            final File finalFile = new File(SettingsMain.getRealPathFromURI(getActivity(), absolutePath));
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(getContext().getContentResolver().getType(absolutePath)),
                            finalFile
                    );
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("profile_img", finalFile.getName(), requestFile);

            Call<ResponseBody> req = restService.postUploadProfileImage(body, UrlController.UploadImageAddHeaders(getActivity()));
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
                                    Toast.makeText(getActivity(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                //noinspection ResultOfMethodCallIgnored
                                finalFile.delete();
                            } else {
                                Toast.makeText(getActivity(), responseobj.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }


    }

    private void adforest_setAllViewsText() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            Call<ResponseBody> myCall = restService.getEditProfileDetails(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Edit Profile ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info Edit ProfileGet", "" + response.getJSONObject("data"));

                                jsonObject = response.getJSONObject("data");

                                ProfileModel profile = new ProfileModel();
                                profile.setData(jsonObject);

                                textViewUserName.setText(profile.getFirstName() + " " + profile.getLastName());
                                getActivity().setTitle(getString(R.string.edit_profile));

                                Picasso.with(getContext()).load(settingsMain.getUserImage())
                                        .error(R.drawable.placeholder)
                                        .placeholder(R.drawable.placeholder)
                                        .into(imageViewProfile);

                                Picasso.with(getContext()).load(settingsMain.getUserImage())
                                        .error(R.drawable.placeholder)
                                        .placeholder(R.drawable.placeholder)
                                        .into(btnSeletImage);

                                editTextName.setText(profile.getFirstName());
                                editTextLastName.setText(profile.getLastName());
                                editTextEmail.setText(profile.getEmail());
                                editTextPhone.setText(profile.getMobile());
                                editTextPostalCode.setText(profile.getPostalCode());
                                editTextAddress1.setText(profile.getFirstAddress());
                                editTextAddress2.setText(profile.getSecondAddress());
                                autoCountry.setText(profile.getCountry());
                                autoState.setText(profile.getState());
                                editTextCity.setText(profile.getCity());
                                editTextAccount.setText(profile.getAccountNumber());
                                editTextBank.setText(profile.getBankName());
                                editTextHolder.setText(profile.getAcoountHolderName());
                                editTextRouting.setText(profile.getAccountRoutingNumber());

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    } catch (IOException e) {
                        e.printStackTrace();
                        SettingsMain.hideDilog();
                    }
                    SettingsMain.hideDilog();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info Edit Profile error", String.valueOf(t));
                    Log.d("info Edit Profile error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }


    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getActivity(),
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

    void adforest_showDilogChangePassword() {

        final Dialog dialog = new Dialog(getActivity(), R.style.customDialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_change_password);
        //noinspection ConstantConditions
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#00000000")));

        Button Send = dialog.findViewById(R.id.send_button);
        Button Cancel = dialog.findViewById(R.id.cancel_button);

        final EditText editTextOld = dialog.findViewById(R.id.editText);
        final EditText editTextNew = dialog.findViewById(R.id.editText2);
        final EditText editTextConfirm = dialog.findViewById(R.id.editText3);

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(editTextOld.getText().toString()) &&
                        !TextUtils.isEmpty(editTextNew.getText().toString()) &&
                        !TextUtils.isEmpty(editTextConfirm.getText().toString()))
                    if (editTextNew.getText().toString().equals(editTextConfirm.getText().toString())) {

                        if (SettingsMain.isConnectingToInternet(getActivity())) {

                            SettingsMain.showDilog(getActivity());

                            JsonObject params = new JsonObject();
                            params.addProperty("old_password", editTextOld.getText().toString());
                            params.addProperty("password", editTextNew.getText().toString());
                            params.addProperty("password_confirmation", editTextConfirm.getText().toString());

                            Log.d("info sendChange Passwrd", params.toString());
                            Call<ResponseBody> myCall = restService.postChangePasswordEditProfile(params, UrlController.AddHeaders(getActivity()));
                            myCall.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                                    try {
                                            Log.d("info ChangePassword Res", "" + responseObj.toString());
                                        if (responseObj.isSuccessful()) {

                                            JSONObject response = new JSONObject(responseObj.body().string());
                                            if (response.getBoolean("success")) {
                                                dialog.dismiss();
                                                settingsMain.setUserPassword(editTextNew.getText().toString());
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
                                    if (t instanceof TimeoutException) {
                                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                                        SettingsMain.hideDilog();
                                    }
                                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                                        SettingsMain.hideDilog();
                                    }
                                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                                        Log.d("info ChangePassword ", "NullPointert Exception" + t.getLocalizedMessage());
                                        SettingsMain.hideDilog();
                                    } else {
                                        SettingsMain.hideDilog();
                                        Log.d("info ChangePassword err", String.valueOf(t));
                                        Log.d("info ChangePassword err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                                    }
                                }
                            });
                        } else {
                            SettingsMain.hideDilog();
                            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        try {
                            Toast.makeText(getActivity(), "" + jsonObjectChnge.getString("err_pass"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void adforest_showDeteleDialog() {
        try {

            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
            alert.setTitle(deleteAccountJsonObject.getString("text"));
            alert.setCancelable(false);
            alert.setMessage(deleteAccountJsonObject.getString("popuptext"));
            alert.setPositiveButton(deleteAccountJsonObject.getString("btn_submit"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog,
                                    int which) {
                    adforest_deleteAccount();
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton(deleteAccountJsonObject.getString("btn_cancel"), new DialogInterface.OnClickListener() {
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

    private void adforest_deleteAccount() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("user_id", settingsMain.getUserLogin());
            Log.d("info Send terms id =", "" + params.toString());

            Call<ResponseBody> myCall = restService.postDeleteAccount(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info terms responce ", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                                settingsMain.setUserLogin("0");
                                settingsMain.setFireBaseId("");
                                SharedPreferences.Editor editor = getActivity().getSharedPreferences("com.adforest", MODE_PRIVATE).edit();
                                editor.putString("isSocial", "false");
                                editor.apply();
                                LoginManager.getInstance().logOut();
                                getActivity().finish();
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);


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

    public void replaceFragment(Fragment someFragment, String tag) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
}
