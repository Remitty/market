package com.brian.market.payment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.brian.market.R;
import com.brian.market.modelsList.CreditCard;
import com.brian.market.payment.adapter.CardPagerAdapter;
import com.brian.market.payment.adapter.PaypalPagerAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.firebase.auth.AuthResult;
import com.google.gson.JsonObject;
import com.stripe.android.Stripe;
import com.stripe.android.exception.APIConnectionException;
import com.stripe.android.exception.APIException;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.exception.CardException;
import com.stripe.android.exception.InvalidRequestException;
import com.stripe.android.model.SourceParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private static final int SDK_PAY_FLAG = 2;
    private static final int SDK_AUTH_FLAG = 3;
    private ViewPager cardPager, paypalPager;
    private SettingsMain settingsMain;
    private RestService restService;
    private String PUBLISHABLE_KEY="";
    private int START_ALIPAY_REQUEST = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Payment");
        }

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        cardPager = (ViewPager) findViewById(R.id.card_pager);
        cardPager.setCurrentItem(0);
        paypalPager = (ViewPager) findViewById(R.id.paypal_pager);
        paypalPager.setCurrentItem(0);

        PUBLISHABLE_KEY = settingsMain.getKey("stripeKey");



        getPayment();
    }


    private void getPayment() {
        if (SettingsMain.isConnectingToInternet(this)) {
            settingsMain.showDilog(this);
            JsonObject params = new JsonObject();

            Call<ResponseBody> myCall = restService.payment( UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info load cards Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            CreditCard card = new CreditCard();
                            card.setData(response.optJSONObject("card"));
                            cardPager.setAdapter(new CardPagerAdapter(getSupportFragmentManager(), card, cardPager));

                            String paypal = response.optString("paypal");
                            paypalPager.setAdapter(new PaypalPagerAdapter(getSupportFragmentManager(), paypal, paypalPager));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    settingsMain.hideDilog();
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getBaseContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
                        Toast.makeText(getBaseContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Something error", Toast.LENGTH_SHORT).show();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            Toast.makeText(this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//                    /**
//                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//                     */
//                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//                    String resultStatus = payResult.getResultStatus();
//                    // 判断resultStatus 为9000则代表支付成功
//                    if (TextUtils.equals(resultStatus, "9000")) {
//                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        showAlert(PaymentActivity.this, "success" + payResult);
//                    } else {
//                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        showAlert(PaymentActivity.this, "faild" + payResult);
//                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
//                    @SuppressWarnings("unchecked")
//                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//                    String resultStatus = authResult.getResultStatus();
//
//                    // 判断resultStatus 为“9000”且result_code
//                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//                        // 获取alipay_open_id，调支付时作为参数extern_token 的value
//                        // 传入，则支付账户为该授权账户
//                        showAlert(PaymentActivity.this, getString(R.string.auth_success) + authResult);
//                    } else {
//                        // 其他状态值则为授权失败
//                        showAlert(PaymentActivity.this, getString(R.string.auth_failed) + authResult);
//                    }
                    break;
                }
                default:
                    break;
            }
        };
    };


    private void handleAlipay() {
        SourceParams alipayParams = SourceParams.createAlipayReusableParams("USD", "Sample", "sample@sample.com", "mycompany://alipay");
        Stripe stripe = new Stripe(this, PUBLISHABLE_KEY);
        try {
            stripe.createSourceSynchronous(alipayParams);

            final String orderInfo = "";   // order information
            Runnable payRunnable = new Runnable() {
                @Override
                public void run() {
                    PayTask alipay = new PayTask(PaymentActivity.this);
                    String result = alipay.pay(orderInfo,true);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            // must call asynchronously
            Thread payThread = new Thread(payRunnable);
            payThread.start();

        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (InvalidRequestException e) {
            e.printStackTrace();
        } catch (APIConnectionException e) {
            e.printStackTrace();
        } catch (APIException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_ALIPAY_REQUEST) {
            if (resultCode == Activity.RESULT_CANCELED) {
                // Do not use the source
            } else {
                // The source was approved.
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
//        startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("Ok", null)
                .setOnDismissListener(onDismiss)
                .show();
    }

}
