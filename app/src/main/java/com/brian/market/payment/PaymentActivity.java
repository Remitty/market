package com.brian.market.payment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.home.HomeActivity;
import com.brian.market.modelsList.CreditCard;
import com.brian.market.payment.adapter.CardPageAdapter;
import com.brian.market.payment.adapter.CardPagerAdapter;
import com.brian.market.payment.adapter.PaypalPagerAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private ViewPager cardPager, paypalPager;
    private SettingsMain settingsMain;
    private RestService restService;

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
}
