package com.brian.market.payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import com.brian.market.databases.User_Cart_DB;
import com.brian.market.home.HomeActivity;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.brian.market.modelsList.CreditCard;
import com.squareup.picasso.Picasso;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.stripe.android.view.CardInputWidget;

public class StripePayment extends AppCompatActivity {

    SettingsMain settingsMain;
    FrameLayout loadingLayout;
    RestService restService;

    Button chkout, cancel;
    TextView tvSubTotal, tvTax, tvShipping, tvTotal;
    TextView tvWalletBalance, tvPaypal, tvCardId;
    ImageView check1, check2, check3, imgCard;

    private CreditCard mCard = new CreditCard();
    private String mPaypal="";

    private String id = "", strSubtotal, strTax, strShipping, strTotal, strPaymentMethod="";
    private boolean shipping, defaultshipping;
    private Intent mIntent;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        settingsMain = new SettingsMain(this);

        mIntent = getIntent();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));
        getSupportActionBar().setTitle("Check Out");

        initComponents();

        initListeners();

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        getInvoiceData();



    }

    private void initListeners() {
        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("")) {
                    Toast.makeText(getBaseContext(), "Please select payment option.", Toast.LENGTH_SHORT).show();
                    return;
                }
                confirmOrder();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StripePayment.this, HomeActivity.class));
            }
        });

        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("Cash"))
                    return;
                strPaymentMethod = "Cash";
                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCard.getData() == null) {
                    Toast.makeText(getBaseContext(), "No card.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strPaymentMethod.equals("Card"))
                    return;
                strPaymentMethod = "Card";
                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPaypal.equals("null") || mPaypal.equals("")) {
                    Toast.makeText(getBaseContext(), "No paypal.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strPaymentMethod.equals("Paypal"))
                    return;
                strPaymentMethod = "Paypal";
                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
            }
        });

    }

    private void confirmOrder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StripePayment.this);

        View alertview = getLayoutInflater().inflate(R.layout.dialog_order_confirm, null);
        TextView contact = alertview.findViewById(R.id.shipping_contact_edit);
        TextView street = alertview.findViewById(R.id.shipping_street_edit);
        TextView apartment = alertview.findViewById(R.id.shipping_apartment_edit);
        TextView state = alertview.findViewById(R.id.address_autoCompleteTextView);
        TextView postalcode = alertview.findViewById(R.id.shipping_postal_code_edit);
        TextView mobile = alertview.findViewById(R.id.shipping_mobile_edit);

        contact.setText(mIntent.getStringExtra("contact_name"));
        street.setText(mIntent.getStringExtra("street"));
        apartment.setText(mIntent.getStringExtra("apartment"));
        state.setText(mIntent.getStringExtra("state"));
        postalcode.setText(mIntent.getStringExtra("postal_code"));
        mobile.setText(mIntent.getStringExtra("mobile"));

        TextView subtotal = alertview.findViewById(R.id.checkout_subtotal);
        TextView tax = alertview.findViewById(R.id.checkout_tax);
        TextView shipping = alertview.findViewById(R.id.checkout_shipping);
        TextView total = alertview.findViewById(R.id.checkout_total);

        subtotal.setText(strSubtotal);
        total.setText(strTotal);
        tax.setText(strTax);
        shipping.setText(strShipping);

        builder.setCancelable(true);
        builder.setView(alertview);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLoading();

                adforest_Checkout();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initComponents() {
        chkout = findViewById(R.id.checkout_order_btn);
        chkout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));
        cancel = findViewById(R.id.checkout_cancel_btn);
        loadingLayout = (FrameLayout) findViewById(R.id.loadingLayout);
        tvSubTotal = findViewById(R.id.checkout_subtotal);
        tvTax = findViewById(R.id.checkout_tax);
        tvShipping = findViewById(R.id.checkout_shipping);
        tvTotal = findViewById(R.id.checkout_total);
        tvWalletBalance = findViewById(R.id.wallet_balance);
        tvCardId = findViewById(R.id.card_id);
        tvPaypal = findViewById(R.id.paypal_id);
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        imgCard = findViewById(R.id.card_logo);

    }

    private void getInvoiceData() {
        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
            User_Cart_DB user_cart_db = new User_Cart_DB();
            SettingsMain.showDilog(StripePayment.this);

            JsonObject params = new JsonObject();
            params.addProperty("product_ids", user_cart_db.getCartItemsForInvoice().toString());
            boolean shipping = mIntent.getBooleanExtra("shipping", false);
            params.addProperty("shipping", shipping);
            Log.d("info Send invoice", params.toString());

            Call<ResponseBody> myCall = restService.postInvoice(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info invoice Responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {

                                strSubtotal = new DecimalFormat("$#0.00").format(response.optDouble("subtotal"));
                                strTotal = new DecimalFormat("$#0.00").format(response.optDouble("total"));
                                strTax = new DecimalFormat("$#0.00").format(response.optDouble("tax"));
                                strShipping = new DecimalFormat("$#0.00").format(response.optDouble("shipping_price"));

                                tvSubTotal.setText(strSubtotal);
                                tvTotal.setText(strTotal);
                                tvTax.setText(strTax);
                                tvShipping.setText(strShipping);

                                tvWalletBalance.setText(new DecimalFormat("$#0.00").format(response.optDouble("wallet_balance")));

                                mCard.setData(response.optJSONObject("card"));
                                mPaypal = response.optString("paypal");

                                if(mCard.getData() != null) {
                                    tvCardId.setText("XXXX-XXXX-XXXX-"+mCard.getLastFour());
                                    if(mCard.getBrand().equals("Visa"))
                                        imgCard.setImageDrawable(getDrawable(R.drawable.ic_visa));
                                    else imgCard.setImageDrawable(getDrawable(R.drawable.ic_mastercard));
                                }

                                if(!mPaypal.equals("null") && !mPaypal.equals(""))
                                    tvPaypal.setText(mPaypal);

                            } else {
                                SettingsMain.showAlertDialog(getBaseContext(), response.get("message").toString());
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
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(StripePayment.this, "Internet error", Toast.LENGTH_SHORT).show();
        }
    }


    private void adforest_Checkout() {

        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {

            JsonObject params = new JsonObject();
            params.addProperty("payment", strPaymentMethod);
            User_Cart_DB user_cart_db = new User_Cart_DB();
            params.addProperty("product_ids", user_cart_db.getCartItemsForInvoice().toString());
            boolean shipping = mIntent.getBooleanExtra("shipping", false);
            if(shipping) {
                params.addProperty("shipping_contact", mIntent.getStringExtra("contact_name"));
                params.addProperty("shipping_street", mIntent.getStringExtra("street"));
                params.addProperty("shipping_state", mIntent.getStringExtra("state"));
                params.addProperty("shipping_apartment", mIntent.getStringExtra("apartment"));
                params.addProperty("shipping_postal_code", mIntent.getStringExtra("postal_code"));
                params.addProperty("shipping_mobile", mIntent.getStringExtra("mobile"));
                params.addProperty("shipping_default", mIntent.getBooleanExtra("set_default_address", false));
            }
            params.addProperty("shipping", shipping);
            Log.d("info Send Checkout", params.toString());

            Call<ResponseBody> myCall = restService.postCheckout(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                        if (responseObj.isSuccessful()) {
                            Log.d("info Checkout Resp", "" + responseObj.toString());

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                            if (response.getBoolean("success")) {
                                User_Cart_DB user_cart_db = new User_Cart_DB();
                                user_cart_db.clearCart();
                                settingsMain.setPaymentCompletedMessage(response.get("message").toString());
                                adforest_getDataForThankYou();

                            } else
                                Toast.makeText(StripePayment.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

                        }
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
                    loadingLayout.setVisibility(View.GONE);
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                        settingsMain.hideDilog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something error", Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            loadingLayout.setVisibility(View.GONE);
            SettingsMain.hideDilog();
            Toast.makeText(StripePayment.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public void adforest_getDataForThankYou() {
        Intent intent = new Intent(StripePayment.this, Thankyou.class);
//                                intent.putExtra("data", responseData.getString("data"));
        intent.putExtra("order_thankyou_title", "Congratulation");
        intent.putExtra("order_thankyou_btn", "Home");
        startActivity(intent);
//                                SettingsMain.hideDilog();
        StripePayment.this.finish();
//        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
//            Call<ResponseBody> myCall = restService.getPaymentCompleteData(UrlController.AddHeaders(StripePayment.this));
//            myCall.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
//                    try {
//                        if (responseObj.isSuccessful()) {
//                            Log.d("info ThankYou Details", "" + responseObj.toString());
//
//                            JSONObject response = new JSONObject(responseObj.body().string());
//                            if (response.getBoolean("success")) {
//                                JSONObject responseData = response.getJSONObject("data");
//
//                                Log.d("info ThankYou object", "" + response.getJSONObject("data"));
//
//                                Intent intent = new Intent(StripePayment.this, Thankyou.class);
//                                intent.putExtra("data", responseData.getString("data"));
//                                intent.putExtra("order_thankyou_title", responseData.getString("order_thankyou_title"));
//                                intent.putExtra("order_thankyou_btn", responseData.getString("order_thankyou_btn"));
//                                startActivity(intent);
//                                SettingsMain.hideDilog();
//                                StripePayment.this.finish();
//                            } else {
//                                SettingsMain.hideDilog();
//                                Toast.makeText(StripePayment.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        SettingsMain.hideDilog();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        SettingsMain.hideDilog();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    SettingsMain.hideDilog();
//                    Log.d("info ThankYou error", String.valueOf(t));
//                    Log.d("info ThankYou error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
//                }
//            });
//        } else {
//            SettingsMain.hideDilog();
//            Toast.makeText(StripePayment.this, "Internet error", Toast.LENGTH_SHORT).show();
//        }
    }

    private void showLoading(){
        Drawable drawable = getResources().getDrawable(R.drawable.bg_uploading).mutate();
        drawable.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_ATOP);
        loadingLayout.setBackground(drawable);
        loadingLayout.setVisibility(View.VISIBLE);
    }
}

