package com.brian.market.doba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.doba.adapters.DobaCatAdapter;
import com.brian.market.doba.helper.Doba;
import com.brian.market.doba.models.DobaCategory;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DobaActivity extends AppCompatActivity{
    SettingsMain settingsMain;
    RestService restService;

    RecyclerView catRecyclerView;
    ArrayList<DobaCategory> catList = new ArrayList<>();
    DobaCatAdapter catAdapter;

    LinearLayout emptyLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doba);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.shop_wholesale));
        }

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class);

        emptyLayout = findViewById(R.id.empty_layout);

        catRecyclerView = findViewById(R.id.cat_recycler_view);

        catAdapter = new DobaCatAdapter(this, catList);
        catAdapter.setOnItemClickListener(new DobaCatAdapter.Listener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(DobaActivity.this, DobaProductsActivity.class);
                intent.putExtra("catId", catList.get(position).getCatId());
                startActivity(intent);
            }
        });
        catRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        catRecyclerView.setAdapter(catAdapter);

//        getCategory();
        getCategoryOther();

    }

    private void getCategoryOther() {
        settingsMain.showDilog(DobaActivity.this);
        Doba doba = new Doba();
        okhttp3.Call mycall = doba.requestCategories();
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
                                            DobaCategory category = new DobaCategory(data.getJSONObject(i));
                                            if (category.getLevel() == 1)
                                                catList.add(category);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            catAdapter.notifyDataSetChanged();
                                            emptyLayout.setVisibility(View.GONE);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
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


    private void getCategory() {
        settingsMain.showDilog(this);
        if (SettingsMain.isConnectingToInternet(this)) {

            Call<ResponseBody> myCall = restService.getDobaCategories();
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("info doba categories ", "" + response.toString());
                        } else {
                            Log.d("info doba error body ", "" + responseObj.errorBody().string());
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
                    Log.d("info doba error", String.valueOf(t));
                    Log.d("info doba error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(this, getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
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
