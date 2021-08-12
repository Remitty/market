package com.brian.market.payment;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.banners.RemoteBanner;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.doba.helper.Doba;
import com.brian.market.doba.models.DobaProduct;
import com.brian.market.models.ProductDetails;
import com.brian.market.models.ShippingAddressModel;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ShippingActivity extends AppCompatActivity {

    EditText mEditContactName, mEditStreet, mEditApartment, mEditState, mEditCountry, mEditCity, mEditPostalCode, mEditMobile;
    CheckBox checkBoxDefaultShippingAddress;
    Button mBtnSkip, mBtnSaveAddress;
    private SettingsMain settingsMain;
    private RestService restService;

    private boolean wholesaleflag;
    ShippingAddressModel shipAddress;
    static ArrayList<ProductDetails> cartItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);

        settingsMain = new SettingsMain(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));
        getSupportActionBar().setTitle(getString(R.string.add_shipping_address));

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        if(getIntent() != null) {
            wholesaleflag = getIntent().getBooleanExtra("wholesaleflag", false);
            if(wholesaleflag) cartItemsList = getIntent().getParcelableArrayListExtra("goods");
        }

        initComponents();
        initListeners();

        getDefaultShippingAddress();
    }

    private void getDefaultShippingAddress() {
        if (SettingsMain.isConnectingToInternet(ShippingActivity.this)) {
            SettingsMain.showDilog(ShippingActivity.this);

            Call<ResponseBody> myCall = restService.getDefaultShippingAddress(UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info invoice Responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                JSONObject address = response.optJSONObject("address");
                                shipAddress = new ShippingAddressModel(address);

                                mEditContactName.setText(shipAddress.getName());
                                mEditState.setText(shipAddress.getState());
                                mEditCity.setText(shipAddress.getCity());
                                mEditCountry.setText(shipAddress.getCountry());
                                mEditStreet.setText(shipAddress.getAddress1());
                                mEditApartment.setText(shipAddress.getAddress2());
                                mEditPostalCode.setText(shipAddress.getPostalCode());
                                mEditMobile.setText(shipAddress.getPhone());
                                checkBoxDefaultShippingAddress.setChecked(response.optBoolean("hasDefault"));

                            }
                            else
                                Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
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
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(ShippingActivity.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }

//        Doba doba = new Doba();
//        okhttp3.Call mycall = doba.requestCountryList("US");
//        mycall.enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
//                settingsMain.hideDilog();
//
//                Log.d("doba here", "failed");
//            }
//
//            @Override
//            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
//                settingsMain.hideDilog();
//                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//                String responseData = response.body().string();
//                Log.e("doba country list onResponse:", responseData);
//                JSONObject object = null;
//                try {
//                    object = new JSONObject(responseData);
//                    if(object.getInt("responseCode") == 0) {
//                        JSONObject bdata = object.getJSONObject("businessData");
//                        JSONObject data = bdata.getJSONArray("data").getJSONObject(0);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
    }

    private void initComponents() {
        mEditContactName = findViewById(R.id.shipping_contact_edit);
        mEditStreet = findViewById(R.id.shipping_street_edit);
        mEditApartment = findViewById(R.id.shipping_apartment_edit);
        mEditState = findViewById(R.id.shipping_state_edit);
        mEditCity = findViewById(R.id.shipping_city_edit);
        mEditCountry = findViewById(R.id.shipping_country_edit);
        mEditMobile = findViewById(R.id.shipping_mobile_edit);
        mEditPostalCode = findViewById(R.id.shipping_postal_code_edit);

        checkBoxDefaultShippingAddress = findViewById(R.id.default_shipping_checkbox);

        mBtnSkip = findViewById(R.id.skip_shipping_btn);
        mBtnSaveAddress = findViewById(R.id.save_address_btn);
    }

    private void initListeners() {
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShippingActivity.this, StripePayment.class);
                intent.putExtra("shipping", false);
                intent.putExtra("wholesaleflag", wholesaleflag);
                startActivity(intent);
            }
        });

        mBtnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAddressValidation()) {
                    Intent intent = new Intent(ShippingActivity.this, StripePayment.class);
                    intent.putExtra("shipping", true);
                    intent.putExtra("wholesaleflag", wholesaleflag);
                    if(wholesaleflag) intent.putExtra("goods", cartItemsList);
                    shipAddress.setAddress1(mEditStreet.getText().toString());
                    shipAddress.setAddress2(mEditApartment.getText().toString());
                    shipAddress.setCity(mEditCity.getText().toString());
                    shipAddress.setState(mEditState.getText().toString());
                    shipAddress.setCountry(mEditCountry.getText().toString());
                    shipAddress.setName(mEditContactName.getText().toString());
                    shipAddress.setPhone(mEditMobile.getText().toString());
                    shipAddress.setPostalCode(mEditPostalCode.getText().toString());
                    intent.putExtra("shipping_address", shipAddress);
                    intent.putExtra("set_default_address", checkBoxDefaultShippingAddress.isChecked());
                    intent.putExtra("shipId", getIntent().getStringExtra("shipId"));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getBaseContext(), "Please input all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean checkAddressValidation() {
        boolean valdate = true;
        if(mEditContactName.getText().toString().equals("")) {
            mEditContactName.setError("!");
            valdate = false;
        }
        if(mEditStreet.getText().toString().equals("")) {
            mEditStreet.setError("!");
            valdate = false;
        }
//        if(mEditApartment.getText().toString().equals("")) {
//            mEditApartment.setError("!");
//            valdate = false;
//        }
        if(mEditState.getText().toString().length() != 2) {
            mEditState.setError("!");
            valdate = false;
        }
        if(mEditCountry.getText().toString().length() != 2) {
            mEditCountry.setError("!");
            valdate = false;
        }
        if(mEditCity.getText().toString().equals("")) {
            mEditCity.setError("!");
            valdate = false;
        }
        if(mEditPostalCode.getText().toString().equals("")) {
            mEditPostalCode.setError("!");
            valdate = false;
        }
        if(mEditMobile.getText().toString().equals("")) {
            mEditMobile.setError("!");
            valdate = false;
        }
        return valdate;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }
}
