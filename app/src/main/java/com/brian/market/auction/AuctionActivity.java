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

import com.brian.market.auction.adapter.AuctionAdapter;
import com.brian.market.helper.GridSpacingItemDecoration;
import com.brian.market.helper.OnAuctionItemClickListener;
import com.brian.market.home.adapter.ItemMainAllCatAdapter;
import com.brian.market.modelsList.Auction;
import com.brian.market.R;
import com.brian.market.modelsList.homeCatListModel;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.brian.market.utills.SettingsMain.getMainColor;

public class AuctionActivity extends AppCompatActivity {
    SettingsMain settingsMain;
    RestService restService;

    private ArrayList<Auction> auctionList = new ArrayList<>();
    private ArrayList<Auction> auctionListTemp = new ArrayList<>();
    private ArrayList<homeCatListModel> catList = new ArrayList<>();
    RecyclerView recyclerView, catRecycler;

    ViewPager mViewPager;
    AuctionAdapter auctionAdapter;
    LinearLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auction);

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.auction));
        }

        auctionAdapter = new AuctionAdapter(AuctionActivity.this, auctionList, 2);
        auctionAdapter.setOnItemClickListener(new OnAuctionItemClickListener() {
            @Override
            public void onItemClick(Auction item) {
                Intent intent = new Intent(AuctionActivity.this, AuctionDetailActivity.class);
                intent.putExtra("post_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onViewDetailClick(String id) {

            }

            @Override
            public void onConfirm(Auction item) {

            }
        });
        recyclerView = findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(grid);
        recyclerView.setAdapter(auctionAdapter);

        catRecycler = findViewById(R.id.recycler_cat);

        emptyLayout = findViewById(R.id.empty);
        
        loadAllData();
    }

    private void loadAllData() {
        settingsMain.showDilog(this);

        if (SettingsMain.isConnectingToInternet(this)) {

            Call<ResponseBody> myCall = restService.getAuctionList( UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info load auction Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if(response.getBoolean("success")) {
                                auctionList.clear();
                                catList.clear();

                                JSONArray list = response.getJSONArray("data");
                                Log.d("info load auction", list.toString());

                                if (list != null && list.length() > 0) {
                                    for (int i = 0; i < list.length(); i++) {
                                        Auction item = new Auction();
                                        JSONObject product = list.getJSONObject(i);
                                        item.setData(product);

                                         auctionList.add(item);
                                        auctionListTemp.add(item);
                                    }
                                }

                                auctionAdapter.notifyDataSetChanged();

                                JSONArray cats = response.getJSONArray("cats");

                                if (cats != null && cats.length() > 0) {
                                    for (int i = 0; i < cats.length(); i++) {
                                        homeCatListModel item = new homeCatListModel();
                                        item.setTitle(cats.optJSONObject(i).optString("cat_name"));
                                        item.setThumbnail(cats.optJSONObject(i).optString("cat_img"));
                                        item.setId(cats.optJSONObject(i).optString("id"));
                                        item.setData(cats.optJSONObject(i));

                                        catList.add(item);
                                    }
                                }
                                int noOfCol = cats.length();
                                GridLayoutManager MyLayoutManager = new GridLayoutManager(AuctionActivity.this, cats.length());
                                MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
                                catRecycler.setLayoutManager(MyLayoutManager);

                                catRecycler.addItemDecoration(new GridSpacingItemDecoration(noOfCol, 0, false));
                                ItemMainAllCatAdapter adapter = new ItemMainAllCatAdapter(getBaseContext(), catList, noOfCol);
                                catRecycler.setAdapter(adapter);
                                adapter.setOnItemClickListener(item -> {
                                    auctionList.clear();
                                    for (Auction auction: auctionListTemp) {
                                        if(auction.getCategoryId() == Integer.parseInt(item.getId())){
                                            auctionList.add(auction);
                                        }
                                    }
                                    auctionAdapter.notifyDataSetChanged();
                                });

                                if(auctionList.size() == 0)
                                    emptyLayout.setVisibility(View.VISIBLE);

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
//        loadAllData();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // The activity is about to be restarted.
        loadAllData();
    }
}
