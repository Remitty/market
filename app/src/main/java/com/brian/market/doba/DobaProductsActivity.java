package com.brian.market.doba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.doba.adapters.DobaGoodAdapter;
import com.brian.market.doba.helper.Doba;
import com.brian.market.doba.models.DobaCategory;
import com.brian.market.doba.models.DobaProduct;
import com.brian.market.helper.EndlessNestedScrollViewListener;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DobaProductsActivity extends AppCompatActivity {

    SettingsMain settingsMain;
    RestService restService;

    RecyclerView goodRecyclerView;
    ArrayList<DobaProduct> goodList = new ArrayList<>();
    DobaGoodAdapter goodAdapter;

    private NestedScrollView scrollView;
    TextInputEditText editSearch;
    LinearLayout emptyLayout;

    String catId, keyword="", strGoods;
    boolean isMore = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doba_products);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.shop_wholesale));
        }

        if(getIntent() != null) {
            catId = getIntent().getStringExtra("catId");
        }

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class);

        emptyLayout = findViewById(R.id.empty_layout);

        goodRecyclerView = findViewById(R.id.goods_recycler_view);
        ViewCompat.setNestedScrollingEnabled(goodRecyclerView, false);
        goodAdapter = new DobaGoodAdapter(this, goodList);
        goodAdapter.setOnItemClickListener(new DobaGoodAdapter.Listener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(DobaProductsActivity.this, DobaProductActivity.class);
                intent.putExtra("spuId", goodList.get(position).getSpuId());
                startActivity(intent);
            }
        });
        StaggeredGridLayoutManager grid = new StaggeredGridLayoutManager(3, 1);
        goodRecyclerView.setLayoutManager(grid);
        goodRecyclerView.setAdapter(goodAdapter);

        editSearch = findViewById(R.id.edit_search);

        scrollView = findViewById(R.id.scrollView);
        initNestedScrollView();
        getData(1);

        initEditSearch();
    }

    private void initEditSearch() {
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    goodList.clear();
                    keyword = v.getText().toString();
                    getData(1);
                    return true;
                }
                return false;
            }
        });
    }

    private void initNestedScrollView() {
        if (scrollView != null) {
            scrollView.setOnScrollChangeListener(new EndlessNestedScrollViewListener(goodRecyclerView.getLayoutManager()){
                @Override
                public void onLoadMore(int page) {
                    isMore = true;
                    showMoreLoading();
                    Log.d("page number", page+"");
                    getData(page+1);
                }
            });
        }
    }

    private void getData(int page) {
        if(!isMore) {
            settingsMain.showDilog(this);
            emptyLayout.setVisibility(View.VISIBLE);
        }
        strGoods = "";
        Doba doba = new Doba();
        Log.d("doba keyword", keyword);
        okhttp3.Call mycall = doba.requestGoods(catId, page, keyword);
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                isMore = false;
                settingsMain.hideDilog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideMoreLoading();
                    }
                });
                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
//                settingsMain.hideDilog();
                isMore = false;
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONObject bdata = object.getJSONObject("businessData");
                        if(bdata.getBoolean("successful")) {
                            JSONObject data = bdata.getJSONObject("data");
                            JSONArray goods = data.getJSONArray("goodsList");
                            for (int i = 0; i < goods.length(); i++) {
                                try {
                                    DobaProduct good = new DobaProduct(goods.getJSONObject(i));
                                    strGoods += good.getSpuId();
                                    if(i != goods.length()-1) {
                                        strGoods += ",";
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(goods.length() > 0)
                                getGoodsList();
                            else {

                                settingsMain.hideDilog();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideMoreLoading();
                                    Toast.makeText(getBaseContext(), bdata.optString("businessMessage"), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hideMoreLoading();
//                        goodAdapter.notifyDataSetChanged();
//                    }
//                });
            }
        });
    }

    private void getGoodsList()
    {
        Doba doba = new Doba();
        Log.d("doba keyword", keyword);
        okhttp3.Call mycall = doba.requestGood(strGoods);
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
                Log.e("doba onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONObject bdata = object.getJSONObject("businessData");
                        if(bdata.getBoolean("successful")) {
                            JSONArray data = bdata.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                try {
                                    DobaProduct good = new DobaProduct(data.getJSONObject(i));
                                    goodList.add(good);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideMoreLoading();
                                    if(goodList.size() > 0)
                                        emptyLayout.setVisibility(View.GONE);
//                                    goodAdapter.notifyDataSetChanged();
                                    goodAdapter.loadMore(goodList);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideMoreLoading();
                                    Toast.makeText(getBaseContext(), bdata.optString("businessMessage"), Toast.LENGTH_SHORT).show();
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

    private void showMoreLoading() {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideMoreLoading() {
        isMore = false;
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
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
