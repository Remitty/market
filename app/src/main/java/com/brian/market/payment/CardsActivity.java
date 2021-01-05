package com.brian.market.payment;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.brian.market.modelsList.CreditCard;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.UrlController;
import com.google.android.material.tabs.TabLayout;
import com.brian.market.R;
import com.brian.market.payment.adapter.CardPageAdapter;
import com.brian.market.utills.SettingsMain;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brian.market.utills.SettingsMain.getMainColor;

public class CardsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    SettingsMain settingsMain;
    RestService restService;
    List<CreditCard> cardList = new ArrayList<>();
    CardPageAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(getMainColor()));
        toolbar.setTitle("Cards");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mViewPager = (ViewPager) findViewById(R.id.container);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));


        loadCardList();

    }

    private void loadCardList() {
        if (SettingsMain.isConnectingToInternet(this)) {
            settingsMain.showDilog(this);
            JsonObject params = new JsonObject();

            Call<ResponseBody> myCall = restService.cardlist( UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info load cards Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONArray response = new JSONArray(responseObj.body().string());

                            Log.d("card list", response.toString());

                            if (response != null && response.length() > 0) {
                                for(int i = 0; i < response.length(); i ++) {
                                    try {
                                        JSONObject cardObject = response.getJSONObject(i);

                                        CreditCard card = new CreditCard();
                                        card.setData(cardObject);

                                        cardList.add(card);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }

                            mSectionsPagerAdapter = new CardPageAdapter(getSupportFragmentManager(), cardList);
                            mViewPager.setAdapter(mSectionsPagerAdapter);
                            mViewPager.setCurrentItem(0);
                            mSectionsPagerAdapter.notifyDataSetChanged();

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
