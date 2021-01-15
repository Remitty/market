package com.brian.market.payment;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ShippingActivity extends AppCompatActivity {

    EditText mEditContactName, mEditStreet, mEditApartment, mEditState, mEditPostalCode, mEditMobile;
    CheckBox checkBoxDefaultShippingAddress;
    Button mBtnSkip, mBtnSaveAddress;
    AutoCompleteTextView mAddressAutoTextView;
    private PlacesClient placesClient;
    ArrayList<String> places = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    private SettingsMain settingsMain;
    private RestService restService;

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
        getSupportActionBar().setTitle("Add Shipping Address");

        placesClient = com.google.android.libraries.places.api.Places.createClient(this);

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        initComponents();
        initListeners();

        getDefaultShippingAddress();
    }

    private void getDefaultShippingAddress() {
        if (SettingsMain.isConnectingToInternet(ShippingActivity.this)) {
            User_Cart_DB user_cart_db = new User_Cart_DB();
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
//                                if(response.optBoolean("hasDefault")) {
                                    JSONObject address = response.optJSONObject("address");
                                    if(address != null) {

//                                    mEditState.setText(address.optString("state_country"));
                                        if (response.optBoolean("hasDefault")) {
                                            mAddressAutoTextView.setText(address.getString("state_country"));
                                            mEditStreet.setText(address.getString("street"));
                                            if(!address.getString("apartment").equals("null"))
                                                mEditApartment.setText(address.getString("apartment"));
                                            mEditPostalCode.setText(address.getString("postal_code"));
                                            checkBoxDefaultShippingAddress.setChecked(true);
                                        }
                                        else {
                                            mAddressAutoTextView.setText(address.getString("state"));
                                            mEditStreet.setText(address.getString("address"));
                                            if(!address.getString("address2").equals("null"))
                                                mEditApartment.setText(address.getString("address2"));
                                            mEditPostalCode.setText(address.getString("postalcode"));
                                        }

                                        mEditMobile.setText(address.getString("mobile"));
                                        mEditContactName.setText(address.getString("contact_name"));

                                    }
//                                }
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
    }

    private void initComponents() {
        mEditContactName = findViewById(R.id.shipping_contact_edit);
        mEditStreet = findViewById(R.id.shipping_street_edit);
        mEditApartment = findViewById(R.id.shipping_apartment_edit);
        mEditState = findViewById(R.id.shipping_state_edit);
        mEditMobile = findViewById(R.id.shipping_mobile_edit);
        mEditPostalCode = findViewById(R.id.shipping_postal_code_edit);

        mAddressAutoTextView = (AutoCompleteTextView) findViewById(R.id
                .address_autoCompleteTextView);

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
                startActivity(intent);
            }
        });

        mBtnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAddressValidation()) {
                    Intent intent = new Intent(ShippingActivity.this, StripePayment.class);
                    intent.putExtra("shipping", true);
                    intent.putExtra("contact_name", mEditContactName.getText().toString());
                    intent.putExtra("street", mEditStreet.getText().toString());
                    intent.putExtra("apartment", mEditApartment.getText().toString());
//                    intent.putExtra("state", mEditState.getText().toString());
                    intent.putExtra("state", mAddressAutoTextView.getText().toString());
                    intent.putExtra("postal_code", mEditPostalCode.getText().toString());
                    intent.putExtra("mobile", mEditMobile.getText().toString());
                    intent.putExtra("set_default_address", checkBoxDefaultShippingAddress.isChecked());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getBaseContext(), "Please input all fields.", Toast.LENGTH_SHORT).show();
                }
            }
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
    }

    private void manageAutoComplete(String query, String type) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest.Builder request = FindAutocompletePredictionsRequest.builder();
//        request.setCountry("US");
        request.setTypeFilter(TypeFilter.REGIONS);
        request.setSessionToken(token)
                .setQuery(query);

//        if(type.equals("address"))
//            request.setTypeFilter(TypeFilter.ADDRESS);

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

            ArrayAdapter<?> adapter = new ArrayAdapter<Object>(ShippingActivity.this, android.R.layout.simple_dropdown_item_1line, data);
            mAddressAutoTextView.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("Places", "Place not found: " + apiException.getStatusCode());
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
//        if(mEditState.getText().toString().equals("")) {
//            mEditState.setError("!");
//            valdate = false;
//        }
        if(mAddressAutoTextView.getText().toString().equals("")) {
            mAddressAutoTextView.setError("!");
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
