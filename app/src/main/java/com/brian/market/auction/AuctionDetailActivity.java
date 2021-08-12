package com.brian.market.auction;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import cn.iwgang.countdownview.CountdownView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.ad_detail.full_screen_image.FullScreenViewActivity;
import com.brian.market.models.Auction;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.brian.market.utills.SettingsMain.getMainColor;

public class AuctionDetailActivity extends AppCompatActivity {
    TextView titleTextView, dateTV, startPriceTV, betPriceTV, statusTV, locationTV, descriptionTV, winnerTV, biddersTV, shippingPrice, catTV;
    Button btnBet, btnCancel;
    EditText editBetPrice;

    CardView cardBet;

    BannerSlider bannerSlider;
    List<Banner> banners = new ArrayList<>();
    ArrayList<String> imageUrls = new ArrayList<>();
    CountdownView countdownView;
    LinearLayout loadingLayout;
    private String myId, mType="";

    LinearLayout shippingLayout;

    RestService restService;
    SettingsMain settingsMain;

    Auction auction = new Auction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction_detail);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.auction_detail));
        }

        myId = getIntent().getStringExtra("post_id");
        if(getIntent().hasExtra("type"))
            mType = getIntent().getStringExtra("type");

        restService = UrlController.createService(RestService.class);
        settingsMain = new SettingsMain(this);

        initComponents();
        initListeners();

        getPostDetail();
    }

    private void initListeners() {
        bannerSlider.setOnBannerClickListener(position -> {
            if (banners.size() > 0) {

                Intent i = new Intent(this, FullScreenViewActivity.class);
                i.putExtra("imageUrls", imageUrls);
                i.putExtra("position", position);
                startActivity(i);
            }
        });

        btnBet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editBetPrice.getText().toString().equals(""))
                    editBetPrice.setError("!");
                else {
                    if(settingsMain.getAppOpen())
                        bet();
                    else
                        Toast.makeText(getBaseContext(), getString(R.string.message_login_to_bid_offer), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AuctionDetailActivity.this);
                alert.setTitle(getString(R.string.message_confirm_cancel_auction_title))
                        .setIcon(R.mipmap.ic_launcher_round)
                        .setMessage(getString(R.string.message_cancel_auction))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendCancel();
                            }
                        })
                        .show();
            }
        });
    }

    private void initComponents() {
        titleTextView = findViewById(R.id.auction_title);
        dateTV = findViewById(R.id.auction_post_time);
        catTV = findViewById(R.id.auction_cat);
        betPriceTV = findViewById(R.id.auction_bet_price);
        startPriceTV = findViewById(R.id.auction_start_price);
        statusTV = findViewById(R.id.auction_status);
        locationTV = findViewById(R.id.auction_location);
        descriptionTV = findViewById(R.id.auction_description);
        winnerTV = findViewById(R.id.auction_winner);
        biddersTV = findViewById(R.id.auction_bidders);

        shippingLayout = findViewById(R.id.shipping_layout);
        shippingPrice = findViewById(R.id.auction_shipping_price);

        bannerSlider = findViewById(R.id.banner_slider);
        countdownView = findViewById(R.id.countDown);
        loadingLayout = findViewById(R.id.loadingLayout);

        btnBet = findViewById(R.id.btn_bet);
        editBetPrice = findViewById(R.id.edit_bet_price);

        cardBet = findViewById(R.id.card_bet);
        btnCancel = findViewById(R.id.btn_cancel_auction);
        if(mType.equals("post")) {
            cardBet.setVisibility(View.GONE);
            btnCancel.setVisibility(View.VISIBLE);
        }
    }

    private void getPostDetail() {

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", myId);

        if (SettingsMain.isConnectingToInternet(this)) {

            SettingsMain.showDilog(this);
            Log.d("info adPost Data", "" + params.toString());
            Call<ResponseBody> myCall = restService.getAuctionDetail(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info AdPost Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                Log.d("info AdPost Resp", "" + response.toString());
                                banners.clear();
                                imageUrls.clear();
                                bannerSlider.removeAllBanners();

                                JSONObject data = response.getJSONObject("data");


                                auction.setData(data);

                                titleTextView.setText(auction.getTitle());
                                catTV.setText(auction.getCategory());
                                descriptionTV.setText(auction.getDescription());
                                startPriceTV.setText(auction.getStartPrice()  +  auction.getCurrency());
                                locationTV.setText(auction.getLocation());
                                dateTV.setText(auction.getDayAgo());
                                statusTV.setText(auction.getStatus());
                                betPriceTV.setText(auction.getHighPrice() +  auction.getCurrency());
                                if(auction.isShipping()) {
                                    shippingLayout.setVisibility(View.VISIBLE);
                                    shippingPrice.setText("$ " + auction.getShippingPrice());
                                }
                                if(auction.getWinner().equals(settingsMain.getUserName())) {
                                    winnerTV.setText("You Winning");
                                }else
                                    winnerTV.setText(auction.getWinner() + " Winning");
                                biddersTV.setText(auction.getBidders() + " Bidders");
                                if(!auction.getStatus().equals("COMPLETED") && !auction.getStatus().equals("FINISHED"))
                                    countdownView.start(Long.parseLong(auction.getDuration()) * 1000);

                                for (int i = 0; i < auction.getImages().length(); i++) {
                                    String path = auction.getImageResourceId(i);
                                    banners.add(new RemoteBanner(path));
                                    imageUrls.add(path);
                                    banners.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
                                }

                                if (banners.size() > 0)
                                    bannerSlider.setBanners(banners);

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
                    SettingsMain.hideDilog();
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getBaseContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void bet() {
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);
        JsonObject params = new JsonObject();
        params.addProperty("id", myId);
        params.addProperty("price", editBetPrice.getText().toString());
        if (SettingsMain.isConnectingToInternet(this)) {

            loadingLayout.setVisibility(View.VISIBLE);
            btnBet.setVisibility(View.GONE);
//            SettingsMain.showDilog(this);
            Log.d("info bet Data", "" + params.toString());
            Call<ResponseBody> myCall = restService.postBet(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info bet Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Toast.makeText(getBaseContext(), response.optString("message"), Toast.LENGTH_SHORT).show();

                            if(response.optBoolean("success")) {
                                betPriceTV.setText(editBetPrice.getText().toString());
                                winnerTV.setText("You Winning");
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
//                    SettingsMain.hideDilog();
                    loadingLayout.setVisibility(View.GONE);
                    btnBet.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    SettingsMain.hideDilog();
                    loadingLayout.setVisibility(View.GONE);
                    btnBet.setVisibility(View.VISIBLE);
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getBaseContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void sendCancel() {
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);
        JsonObject params = new JsonObject();
        params.addProperty("id", myId);

        if (SettingsMain.isConnectingToInternet(this)) {

            loadingLayout.setVisibility(View.VISIBLE);
//            SettingsMain.showDilog(this);
            Log.d("info bet Data", "" + params.toString());
            Call<ResponseBody> myCall = restService.cancelBet(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info bet Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if(response.getBoolean("success"))
                                btnCancel.setVisibility(View.GONE);

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
//                    SettingsMain.hideDilog();
                    loadingLayout.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    SettingsMain.hideDilog();
                    loadingLayout.setVisibility(View.GONE);
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getBaseContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
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
}
