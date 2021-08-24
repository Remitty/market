package com.brian.market.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.alipay.sdk.app.EnvUtils;
import com.alipay.sdk.app.PayTask;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.doba.helper.Doba;
import com.brian.market.home.HomeActivity;
import com.brian.market.models.ProductDetails;
import com.brian.market.models.ShippingAddressModel;
import com.brian.market.wxapi.WXPayEntryActivity;

import androidx.annotation.NonNull;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.brian.market.models.CreditCard;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.SocketTimeoutException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.stripe.android.model.WeChat;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class StripePayment extends WXPayEntryActivity {

    SettingsMain settingsMain;
    FrameLayout loadingLayout;
    RestService restService;

    Button chkout, cancel;
    TextView tvSubTotal, tvTax, tvShipping, tvTotal;
    TextView tvWalletBalance, tvPaypal, tvCardId;
    ImageView check1, check2, check3, check4, check5, imgCard, check6, check7;

    EditText mEditContactName, mEditStreet, mEditApartment, mEditState, mEditCountry, mEditCity, mEditPostalCode, mEditMobile;

    LinearLayout llPayment, llWhole;

    private CreditCard mCard = new CreditCard();
    private String mPaypal="";
    private JSONObject paypal;

    private String id = "", strSubtotal, strTax="0", strShipping, strTotal, strPaymentMethod="";
    private boolean shipping, defaultshipping;
    private Intent mIntent;

    public static final int PAYPAL_REQUEST_CODE = 123;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_NO_NETWORK;
    String paymentId="";

    private boolean wholesaleflag = false;
    static ArrayList<ProductDetails> cartItemsList = new ArrayList<>();
    ShippingAddressModel shipAddress;

    private JSONObject billingAddress;

    private static final int SDK_PAY_FLAG = 2;
    private static final int SDK_AUTH_FLAG = 3;
    private static final int START_DOBAPAY_REQUEST = 100;
    private String PUBLISHABLE_KEY="";
    private int START_ALIPAY_REQUEST = 0;
    private String shipId="";
    private String orderId;
    private String shipMethodId = "";

    String orderPayUrl = "";
    String dobaorderId = "";
    String orderBatchId = "";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        settingsMain = new SettingsMain(this);
        PUBLISHABLE_KEY = settingsMain.getKey("stripeKey");

        mIntent = getIntent();

        if(mIntent != null) {
//            Checkout();
            wholesaleflag = mIntent.getBooleanExtra("wholesaleflag", false);
            if(wholesaleflag) {
                cartItemsList = mIntent.getParcelableArrayListExtra("goods");
                shipId = mIntent.getStringExtra("shipId");
            }
            shipAddress = mIntent.getParcelableExtra("shipping_address");
        }

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));
        getSupportActionBar().setTitle(getString(R.string.checkout));

        initComponents();

        initListeners();

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);
        if(wholesaleflag) {
            String price = cartItemsList.get(0).getPrice();
            Integer qty = cartItemsList.get(0).getCustomersBasketQuantity();
            strSubtotal = new DecimalFormat("#0.00").format(Double.parseDouble(price) * qty);
            strTotal = strSubtotal;
            tvSubTotal.setText("$"+strSubtotal);
            tvTotal.setText("$"+strTotal);
            getShipEstimateFeeForWholeSale();
            llWhole.setVisibility(View.VISIBLE);
            llPayment.setVisibility(View.GONE);
        }
        else {
            getInvoiceData();
            llPayment.setVisibility(View.VISIBLE);
            llWhole.setVisibility(View.GONE);
        }


    }

    private void initListeners() {
        chkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("")) {
                    Toast.makeText(getBaseContext(), getString(R.string.message_select_payment_option), Toast.LENGTH_SHORT).show();
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
                check4.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check5.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCard.getData() == null) {
                    Toast.makeText(getBaseContext(), getString(R.string.no_card), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strPaymentMethod.equals("Card"))
                    return;
                strPaymentMethod = "Card";
                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check4.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check5.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(mPaypal.equals("null") || mPaypal.equals("")) {
//                    Toast.makeText(getBaseContext(), "No paypal.", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if(strPaymentMethod.equals("Paypal"))
                    return;
                strPaymentMethod = "Paypal";
                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check4.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check5.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("Alipay"))
                    return;
                strPaymentMethod = "Alipay";
                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
                check4.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check5.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("wechatpay"))
                    return;
                Toast.makeText(getBaseContext(), "Wechat pay doesn't support now.", Toast.LENGTH_SHORT).show();
                return;
//                strPaymentMethod = "wechatpay";
//                check1.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
//                check2.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
//                check3.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
//                check4.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
//                check5.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
            }
        });

        check6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("DobaCard"))
                    return;
                strPaymentMethod = "DobaCard";
                chkout.setEnabled(true);
                check6.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check7.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

        check7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strPaymentMethod.equals("Token"))
                    return;
                strPaymentMethod = "Token";
                check7.setImageDrawable(getDrawable(R.drawable.ic_check_circle_green_24dp));
                check6.setImageDrawable(getDrawable(R.drawable.ic_check_circle_black_24dp));
            }
        });

    }

    private void getShipEstimateFeeForWholeSale() {
        settingsMain.showDilog(this);
        Doba doba = new Doba();
        JSONObject good = new JSONObject();
        JSONArray goods = new JSONArray();
        try {
            good.put("itemNo", cartItemsList.get(0).getId());
            good.put("quantity", cartItemsList.get(0).getCustomersBasketQuantity());
            goods.put(good);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        okhttp3.Call mycall = doba.requestShipFee(shipAddress, shipId,  goods);
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                settingsMain.hideDilog();

                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                settingsMain.hideDilog();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba good onResponse:", responseData);
                JSONObject responseobject = null;
                try {
                    responseobject = new JSONObject(responseData);
                    if(responseobject.getInt("responseCode") == 0) {
                        JSONObject bdata = responseobject.getJSONArray("businessData").getJSONObject(0);
                        if(bdata.getInt("businessStatus") == 0) {
                            JSONObject data = bdata.getJSONObject("data");
                            JSONArray costs = data.getJSONArray("costs");
                            Double shipFee = costs.getJSONObject(0).getDouble("shipFee");
                            shipMethodId = costs.getJSONObject(0).getString("shippingMethodId");
                            strShipping = String.valueOf(shipFee);

                            strTotal = String.valueOf(Double.parseDouble(strSubtotal) + shipFee);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvTotal.setText("$"+ strTotal);
                                    tvShipping.setText("$" + strShipping);
                                    chkout.setEnabled(true);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), bdata.optString("businessMessage"), Toast.LENGTH_SHORT).show();
                                    chkout.setEnabled(false);
                                }
                            });
                        }

                    } else {
                        JSONObject finalResponseobject = responseobject;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), finalResponseobject.optString("responseMessage"), Toast.LENGTH_SHORT).show();
                                chkout.setEnabled(false);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

        contact.setText(shipAddress.getName());
        street.setText(shipAddress.getAddress1());
        apartment.setText(shipAddress.getAddress2());
        state.setText(shipAddress.getCity() + ", " + shipAddress.getState() + ", " + shipAddress.getCountry());
        postalcode.setText(shipAddress.getPostalCode());
        mobile.setText(shipAddress.getPhone());

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
        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (strPaymentMethod.equals("Paypal")) handlePaypal();
                else if (strPaymentMethod.equals("Alipay")) {
                    dialog.dismiss();
                    showLoading();
                    handleAlipay();
                } else if (strPaymentMethod.equals("wechatpay")) {
                    dialog.dismiss();
                    handleWechatPay();
                } else {
                    if(wholesaleflag) {
                        if(shipMethodId.isEmpty()) {
                            Toast.makeText(getBaseContext(), "No found a ship to the province", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        billingAddressDialog();
                    }
                    else Checkout();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void billingAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(StripePayment.this);


        View alertview = getLayoutInflater().inflate(R.layout.dialog_billing_address, null);
        mEditContactName = alertview.findViewById(R.id.shipping_contact_edit);
        mEditStreet = alertview.findViewById(R.id.shipping_street_edit);
        mEditApartment = alertview.findViewById(R.id.shipping_apartment_edit);
        mEditState = alertview.findViewById(R.id.shipping_state_edit);
        mEditCity = alertview.findViewById(R.id.shipping_city_edit);
        mEditCountry = alertview.findViewById(R.id.shipping_country_edit);
        mEditMobile = alertview.findViewById(R.id.shipping_mobile_edit);
        mEditPostalCode = alertview.findViewById(R.id.shipping_postal_code_edit);

        Button mBtnSkip = alertview.findViewById(R.id.cancel_btn);
        Button mBtnSaveAddress = alertview.findViewById(R.id.confirm_btn);

        builder.setCancelable(true);
        builder.setView(alertview);
//        mBtnSkip.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alert.dismiss();
//            }
//        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

//        mBtnSaveAddress.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checkAddressValidation()) {
                    billingAddress = new JSONObject();
                    try {
                        billingAddress.put("addr1", mEditStreet.getText().toString());
                        billingAddress.put("addr2", mEditApartment.getText().toString());
                        billingAddress.put("city", mEditCity.getText().toString());
                        billingAddress.put("provinceCode", mEditState.getText().toString());
                        billingAddress.put("countryCode", mEditCountry.getText().toString());
                        billingAddress.put("name", mEditContactName.getText().toString());
                        billingAddress.put("telephone", mEditMobile.getText().toString());
                        billingAddress.put("zip", mEditPostalCode.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Checkout();
                }
                else{
                    Toast.makeText(getBaseContext(), "Please input all fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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
        check4 = findViewById(R.id.check4);
        check5 = findViewById(R.id.check5);
        imgCard = findViewById(R.id.card_logo);

        check6 = findViewById(R.id.check6);
        check7 = findViewById(R.id.check7);

        llPayment = findViewById(R.id.ll_payment);
        llWhole = findViewById(R.id.ll_whole);

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
                                strTotal = new DecimalFormat("#0.00").format(response.optDouble("total"));
                                strTax = new DecimalFormat("$#0.00").format(response.optDouble("tax"));
                                strShipping = new DecimalFormat("$#0.00").format(response.optDouble("shipping_price"));

                                tvSubTotal.setText(strSubtotal);
                                tvTotal.setText("$"+strTotal);
                                tvTax.setText(strTax);
                                tvShipping.setText(strShipping);

                                tvWalletBalance.setText(new DecimalFormat("$#0.00").format(response.optDouble("wallet_balance")));

                                mCard.setData(response.optJSONObject("card"));
                                paypal = response.optJSONObject("paypal");
                                mPaypal = paypal.optString("paypal");

                                if(mCard.getData() != null) {
                                    tvCardId.setText("XXXX-XXXX-XXXX-"+mCard.getLastFour());
                                    if(mCard.getBrand().equals("Visa"))
                                        imgCard.setImageDrawable(getDrawable(R.drawable.ic_visa));
                                    else imgCard.setImageDrawable(getDrawable(R.drawable.ic_mastercard));
                                }

//                                if(!mPaypal.equals("null") && !mPaypal.equals(""))
//                                    tvPaypal.setText(mPaypal);

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
            Toast.makeText(StripePayment.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void Checkout() {

        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
            showLoading();
            JsonObject params = new JsonObject();
            params.addProperty("payment", strPaymentMethod);
            params.addProperty("shop_type", wholesaleflag ? "Supplier" : "Individual");
            if(strPaymentMethod.equals("Paypal") || strPaymentMethod.equals("Alipay")) {
                params.addProperty("payment_id", paymentId);
                params.addProperty("total_price", strTotal);
            }
            if(!wholesaleflag) {
                User_Cart_DB user_cart_db = new User_Cart_DB();
                params.addProperty("product_ids", user_cart_db.getCartItemsForInvoice().toString());
            } else {
                params.addProperty("qty", cartItemsList.get(0).getCustomersBasketQuantity());
                params.addProperty("subTotal", strSubtotal);
                params.addProperty("shipping_price", strShipping);

            }
            boolean shipping = mIntent.getBooleanExtra("shipping", false);
            if(shipping) {
                params.addProperty("shipping_contact", shipAddress.getName());
                params.addProperty("shipping_street", shipAddress.getAddress1());
                params.addProperty("shipping_apartment", shipAddress.getAddress2());
                params.addProperty("shipping_country", shipAddress.getCountry());
                params.addProperty("shipping_state", shipAddress.getState());
                params.addProperty("shipping_city", shipAddress.getCity());
                params.addProperty("shipping_postal_code", shipAddress.getPostalCode());
                params.addProperty("shipping_mobile", shipAddress.getPhone());
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
                            Log.d("info Checkout Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                            if (response.getBoolean("success")) {
                                String orderId = response.getString("orderId");
                                if(!wholesaleflag) {
                                    User_Cart_DB user_cart_db = new User_Cart_DB();
                                    user_cart_db.clearCart();
                                    settingsMain.setPaymentCompletedMessage(response.get("message").toString());
                                    getDataForThankYou();
                                } else {
                                    makeDobaPayment(orderId);
                                }

                            } else
                                Toast.makeText(StripePayment.this, response.get("message").toString(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getBaseContext(), responseObj.errorBody().string(), Toast.LENGTH_SHORT).show();
                            Log.e("checkout issue: ", responseObj.errorBody().string());
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
                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(StripePayment.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void makeDobaPayment(String orderId) {
        this.orderId = orderId;
        settingsMain.showDilog(this);

        Doba doba = new Doba();
        JSONArray list = new JSONArray();
        for(int i =0; i < cartItemsList.size(); i ++) {
            JSONObject object = new JSONObject();
            try {
                object.put("itemNo", cartItemsList.get(i).getId());
                object.put("quantityOrdered", String.valueOf(cartItemsList.get(i).getCustomersBasketQuantity()));
                object.put("shippingMethodId", shipMethodId);
                list.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject detail = new JSONObject();
        JSONArray orders = new JSONArray();
        try {
            detail.put("goodsDetailDTOList", list);
            detail.put("shippingAddress", shipAddress.getDobaObject());
            detail.put("orderNumber", orderId);
            orders.put(detail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        okhttp3.Call mycall = doba.requestImportOrder(billingAddress, orders);
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                settingsMain.hideDilog();

                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                settingsMain.hideDilog();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba good onResponse:", responseData);
                JSONObject object = null;

                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONObject bdata = object.getJSONObject("businessData");
                        if(bdata.getBoolean("successful")) {
                            JSONObject data = bdata.getJSONObject("data");
                            JSONArray resList = data.getJSONArray("orderSuccessResList");
                            orderPayUrl = resList.getJSONObject(0).getString("orderPayURL");
                            dobaorderId = resList.getJSONObject(0).getString("orderId");
                            orderBatchId = resList.getJSONObject(0).getString("ordBatchId");

                            if(strPaymentMethod.equals("Token")) getDobaSystemCardId(orderBatchId);
                            else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendDobaOrderToServer(dobaorderId, orderPayUrl);
                                    }
                                });

                                makeDobaCardPayment(orderPayUrl);
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getBaseContext(), bdata.optString("businessMessage"), Toast.LENGTH_SHORT).show();
                                    deleteOrder();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    private void makeDobaCardPayment(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivityForResult(intent, START_DOBAPAY_REQUEST);
    }

    private void getDobaSystemCardId(String orderBatchId) {
        settingsMain.showDilog(this);

        Doba doba = new Doba();
        okhttp3.Call mycall = doba.requestSystemCardId();
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                settingsMain.hideDilog();

                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                settingsMain.hideDilog();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba good onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONObject bdata = object.getJSONObject("businessData");
                        JSONObject data = bdata.getJSONArray("data").getJSONObject(0);
                        String cardId = data.getString("cardId");
                        makeDobaSystemPayment(orderBatchId, cardId);
                    } else {
                        deleteOrder();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void makeDobaSystemPayment(String orderBatchId, String cardId) {
        settingsMain.showDilog(this);

        Doba doba = new Doba();
        okhttp3.Call mycall = doba.requestSystemPayment(orderBatchId, cardId);
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                settingsMain.hideDilog();

                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                settingsMain.hideDilog();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba good onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONObject bdata = object.getJSONObject("businessData");
                        JSONObject data = bdata.getJSONArray("data").getJSONObject(0);
                        String totalPay = data.getString("totalPay");
                        confirmDobaSystemPaymentToServer(orderBatchId, totalPay); // to server
                    } else {
                      deleteOrder();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void confirmDobaSystemPaymentToServer(String orderBatchId, String amount) {
        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
            showLoading();
            JsonObject params = new JsonObject();
            params.addProperty("total", amount);
            params.addProperty("orderId", orderId);
            params.addProperty("txnId", orderBatchId);
            Log.d("confirm doba param", params.toString());

            Call<ResponseBody> myCall = restService.postDobaSystemPayment(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                            Log.d("info Checkout Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                            if (response.getBoolean("success")) {
                                getDataForThankYou();

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
                        Log.d("confirm doba err", String.valueOf(t));
                        Log.d("confirm doba err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(StripePayment.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDobaOrderToServer(String dobaOrderId, String payUrl) {
        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
//            showLoading();
            JsonObject params = new JsonObject();
            params.addProperty("payUrl", payUrl);  // if payUrl is empty, it is success for payment.
            params.addProperty("txnId", dobaOrderId);
            params.addProperty("orderId", this.orderId);
            Log.d("send doba order param", params.toString());

            Call<ResponseBody> myCall = restService.sendDobaOrderToServer(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
//                    loadingLayout.setVisibility(View.GONE);
                    try {
                        Log.d("info Checkout Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());
                            if (response.getBoolean("success")) {
//                                getDataForThankYou();

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
//                    loadingLayout.setVisibility(View.GONE);
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
//                        settingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getApplicationContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
//                        settingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
//                        settingsMain.hideDilog();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something error", Toast.LENGTH_SHORT).show();
//                        SettingsMain.hideDilog();
                        Log.d("confirm doba err", String.valueOf(t));
                        Log.d("confirm doba err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(StripePayment.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOrder() {
        if (SettingsMain.isConnectingToInternet(StripePayment.this)) {
            showLoading();
            JsonObject params = new JsonObject();
            params.addProperty("orderId", orderId);
            Log.d("delete doba order", params.toString());

            Call<ResponseBody> myCall = restService.deleteOrder(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                            Log.d("info Checkout Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info Checkout object", "" + response.toString());


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
                        Log.d("confirm doba err", String.valueOf(t));
                        Log.d("confirm doba err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(StripePayment.this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void handlePaypal() {
        PayPalConfiguration
                config = null;
        try {
            config = new PayPalConfiguration()
                    // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                    // or live (ENVIRONMENT_PRODUCTION)
//                    .environment(CONFIG_ENVIRONMENT)
                    .environment(paypal.getString("mode"))
                    .clientId(paypal.getString("client_id"))
                    .merchantName(paypal.getString("merchant_name"));
//                    .merchantPrivacyPolicyUri(Uri.parse(jsonObject.getString("privecy_url")))
//                    .merchantUserAgreementUri(Uri.parse(jsonObject.getString("agreement_url")));
            Intent intent = new Intent(StripePayment.this, PayPalService.class);

            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            this.startService(intent);

            //Creating a paypalpayment
            PayPalPayment payment = new PayPalPayment(new BigDecimal(strTotal), paypal.getString("currency"), "Mackirel",
                    PayPalPayment.PAYMENT_INTENT_SALE);
//
//            //Creating Paypal Payment activity intent
            Intent intent1 = new Intent(StripePayment.this, com.paypal.android.sdk.payments.PaymentActivity.class);
//
//            //putting the paypal configuration to the intent
            intent1.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
//
//            //Puting paypal payment to the intent
            intent1.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
//
//            //Starting the intent activity for result
//            //the request code will be used on the method onActivityResult
            startActivityForResult(intent1, PAYPAL_REQUEST_CODE);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(android.os.Message msg) {
            loadingLayout.setVisibility(View.GONE);
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    Map<String, String> answer = (Map<String, String>) msg.obj;
                    // The result info contains other information about the transaction
                    String resultInfo = answer.get("result");
                    String resultStatus = answer.get("resultStatus");
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
                    } else {
                        String memo = answer.get("memo");
                        Toast.makeText(getBaseContext(), memo, Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    Map<String, String> answer = (Map<String, String>) msg.obj;
//                    // The result info contains other information about the transaction
//                    String resultInfo = answer.get("result");
//                    String resultStatus = answer.get("resultStatus");
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getBaseContext(), "failed", Toast.LENGTH_SHORT).show();
//                    }
                    break;
                }
                default:
                    break;
            }
        };
    };

    @SuppressLint("WrongConstant")
    private void handleAlipay() {
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        EnvUtils.setEnv(EnvUtils.EnvEnum.SANDBOX);
        int total =  (int)(Double.parseDouble(strTotal)*100);
        SourceParams alipayParams = SourceParams.createAlipaySingleUseParams(Long.parseLong(String.format("%d", total)), "USD", "Sample", "sample@sample.com", "remitty://alipay");
//        SourceParams alipayParams = SourceParams.createAlipayReusableParams("USD", "Sample", "sample@sample.com", "remitty://alipay");
        Stripe stripe = new Stripe(StripePayment.this, PUBLISHABLE_KEY);
        //            Source source = stripe.createSourceSynchronous(alipayParams);
//                    invokeAlipayNative(source);
//            invokeAlipayWeb(source);
//            invokeAlipayNativeReusable(source);
        stripe.createSource(alipayParams, new ApiResultCallback<Source>() {
            @Override
            public void onError(Exception error) {
                loadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Source source) {
                loadingLayout.setVisibility(View.GONE);
//                invokeAlipayNative(source);
                invokeAlipayWeb(source);
//              invokeAlipayNativeReusable(source);
            }
        });


    }

    private void invokeAlipayNative(Source source) {
        Map<String, Object> alipayParams = source.getSourceTypeData();
        final String dataString = (String) alipayParams.get("data_string");

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // The PayTask class is from the Alipay SDK. Do not run this function
                // on the main thread.
                PayTask alipay = new PayTask(StripePayment.this);
                // Invoking this function immediately takes the user to the Alipay
                // app, if in stalled. If not, the user is sent to the browser.
                Map<String, String> result = alipay.payV2(dataString, true);
//                Map<String, String> result = alipay.payV2(dataString.toString(), true);

                // Once you get the result, communicate it back to the main thread
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void invokeAlipayNativeReusable(Source source) {
        Map<String, Object> alipayParams = source.getSourceTypeData();
        String dataString = (String) alipayParams.get("native_url");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(dataString));
        // Start the activity with your choice of integer request code,
        // here denoted as START_ALIPAY_REQUEST
        startActivityForResult(intent, START_ALIPAY_REQUEST);
    }

    private void invokeAlipayWeb(Source source) {
        String redirectUrl = source.getRedirect().getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(redirectUrl));
        startActivity(intent);
    }

    private void handleWechatPay() {
        SourceParams wechatParam = SourceParams.createWeChatPayParams((long)(Double.parseDouble(strTotal)*100), "USD", getString(R.string.wechat_app_id), "");
        Stripe stripe = new Stripe(StripePayment.this, PUBLISHABLE_KEY);
        stripe.createSource(wechatParam, new ApiResultCallback<Source>() {
            @Override
            public void onSuccess(@NonNull Source result) {
                WeChat weChat = result.getWeChat();
                IWXAPI weChatApi = WXAPIFactory.createWXAPI(StripePayment.this, getString(R.string.wechat_app_id), true);
                PayReq payReq = new PayReq();
                payReq.appId = weChat.getAppId();
                payReq.partnerId = weChat.getPartnerId();
                payReq.packageValue = weChat.getPackageValue();
                payReq.nonceStr = weChat.getNonce();
                payReq.timeStamp = weChat.getTimestamp();
                payReq.sign = weChat.getSign();
                weChatApi.sendReq(payReq);
//                loadingLayout.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull Exception e) {
                loadingLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result is from paypal
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PAYPAL_REQUEST_CODE) {

            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Log.i("paymentExample", paymentDetails);
                        paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");
                        Checkout();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("paymentExample", "The user canceled.");
                loadingLayout.setVisibility(View.GONE);
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                loadingLayout.setVisibility(View.GONE);
            }
        }

        if (requestCode == START_ALIPAY_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // Do not use the source
            } else {
                // The source was approved.
                Checkout();
            }
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

    @Override
    public void wechatLoginSuccess() {

    }

    @Override
    public void wechatPaySuccess() {
        loadingLayout.setVisibility(View.GONE);
    }

    public void getDataForThankYou() {
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
//            Toast.makeText(StripePayment.this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
//        }
    }

    private void showLoading(){
        Drawable drawable = getResources().getDrawable(R.drawable.bg_uploading).mutate();
        drawable.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_ATOP);
        loadingLayout.setBackground(drawable);
        loadingLayout.setVisibility(View.VISIBLE);
    }

}

