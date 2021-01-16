package com.brian.market.auction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brian.market.auction.adapter.MyAuctionPageAdapter;
import com.brian.market.helper.OnAuctionItemClickListener;
import com.brian.market.home.HomeActivity;
import com.brian.market.modelsList.Auction;
import com.brian.market.R;
import com.brian.market.order.adapter.MyOrderPageAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brian.market.App.getContext;
import static com.brian.market.utills.SettingsMain.getMainColor;

public class MyAuctionActivity extends AppCompatActivity {

    private ArrayList<Auction> postList = new ArrayList<>();
    private ArrayList<Auction> bidList = new ArrayList<>();
    private ArrayList<Auction> winList = new ArrayList<>();

    SettingsMain settingsMain;
    RestService restService;

    FloatingActionButton fabPlus;

    ViewPager mViewPager;
    TabLayout tab;
    private MyAuctionPageAdapter mAuctionPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_auction);

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.my_auction));
        }

        fabPlus = findViewById(R.id.fab_plus_auction);
        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAuctionActivity.this, AddNewAuctionPost.class));
            }
        });

        tab = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.view_pager);
        tab.setupWithViewPager(mViewPager);

        loadAllData();
    }

    private void loadAllData() {
        settingsMain.showDilog(this);

        if (SettingsMain.isConnectingToInternet(this)) {

            Call<ResponseBody> myCall = restService.getMyAuctionList( UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info load my auction", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if(response.getBoolean("success")) {
                                Log.d("info load post", response.toString());
                                postList.clear();
                                bidList.clear();
                                winList.clear();

                                JSONArray list = response.getJSONArray("post_auction");
                                Log.d("info load post", list.toString());

                                if (list != null && list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        Auction item = new Auction();
                                        JSONObject product = list.getJSONObject(i);
                                        item.setData(product);
                                        postList.add(item);
                                    }
                                }

                                JSONArray list1 = response.getJSONArray("bid_auction");
                                Log.d("info load bidlist", list1.toString());

                                if (list1 != null && list1.length() > 0) {
                                    for (int i = 0; i < list1.length(); i++) {
                                        Auction item = new Auction();
                                        JSONObject product = list1.getJSONObject(i);
                                        item.setData(product);
                                        bidList.add(item);
                                    }
                                }

                                JSONArray list2 = response.getJSONArray("wining_auction");
                                Log.d("info load wining", list2.toString());

                                if (list2 != null && list2.length() > 0) {
                                    for (int i = 0; i < list2.length(); i++) {
                                        Auction item = new Auction();
                                        JSONObject product = list2.getJSONObject(i);
                                        item.setData(product);
                                        winList.add(item);
                                    }
                                }

                                mAuctionPagerAdapter = new MyAuctionPageAdapter(getSupportFragmentManager(), postList, bidList, winList);
                                mViewPager.setAdapter(mAuctionPagerAdapter);
                                mViewPager.setCurrentItem(0);
                                mAuctionPagerAdapter.notifyDataSetChanged();
                            }
                            else{
                                Toast.makeText(getBaseContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    loadingLayout.setVisibility(View.GONE);
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
        startActivity(new Intent(this, HomeActivity.class));
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

    @Override
    protected void onRestart() {
        super.onRestart();
        // The activity is about to be restarted.
        loadAllData();
    }
}
