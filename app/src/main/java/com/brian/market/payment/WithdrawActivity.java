package com.brian.market.payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.auction.adapter.MyAuctionPageAdapter;
import com.brian.market.modelsList.Auction;
import com.brian.market.modelsList.WithdrawTransaction;
import com.brian.market.payment.adapter.WalletPageAdapter;
import com.brian.market.utills.PlaidConnect;
import com.github.thiagolocatelli.stripe.StripeApp;
import com.github.thiagolocatelli.stripe.StripeButton;
import com.github.thiagolocatelli.stripe.StripeConnectListener;
import com.github.thiagolocatelli.stripe.StripeSession;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.brian.market.R;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.StripeData;
import com.brian.market.utills.UrlController;
import com.stripe.Stripe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brian.market.utills.SettingsMain.getMainColor;

public class WithdrawActivity extends AppCompatActivity {
    SettingsMain settingsMain;
    RestService restService;

    ViewPager mViewPager;
    TabLayout tab;
    private WalletPageAdapter mPageAdapter;

    private ArrayList<WithdrawTransaction> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(getMainColor()));
        toolbar.setTitle("Wallet");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tab = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.view_pager);
        tab.setupWithViewPager(mViewPager);

        loadAllData();

    }

    private void loadAllData() {
        if (SettingsMain.isConnectingToInternet(this)) {
            settingsMain.showDilog(this);

            Call<ResponseBody> myCall = restService.balance(UrlController.AddHeaders(getBaseContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info delete card Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            String wallet = response.getString("balance");
                            String paypal = response.getString("paypal");

                            JSONArray list2 = response.getJSONArray("withdraw_history");
                            Log.d("info load wining", list2.toString());

                            if (list2 != null && list2.length() > 0) {
                                for (int i = 0; i < list2.length(); i++) {
                                    WithdrawTransaction item = new WithdrawTransaction();
                                    JSONObject product = list2.getJSONObject(i);
                                    item.setData(product);
                                    list.add(item);
                                }
                            }

                            mPageAdapter = new WalletPageAdapter(getSupportFragmentManager(), wallet, paypal, list);
                            mViewPager.setAdapter(mPageAdapter);
                            mViewPager.setCurrentItem(0);
                            mPageAdapter.notifyDataSetChanged();

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
            settingsMain.hideDilog();
            Toast.makeText(this, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
