package com.brian.market.profile;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.brian.market.modelsList.OrderDetail;
import com.google.android.material.tabs.TabLayout;
import com.brian.market.R;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.profile.adapter.MyProductPageAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

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

public class MyProductActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    SettingsMain settingsMain;
    RestService restService;

    private ArrayList<ProductDetails> myProductList = new ArrayList<>();
    private ArrayList<ProductDetails> myFeaturedProductList = new ArrayList<>();
    private ArrayList<OrderDetail> OrderList = new ArrayList<>();
    private ArrayList<OrderDetail> OrderHistoryList = new ArrayList<>();

    ViewPager mViewPager;
    MyProductPageAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_realty);

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(getMainColor()));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.parseColor(getMainColor()));
        toolbar.setTitle(getString(R.string.my_lists));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.container);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));



        loadAllData();
    }

    private void loadAllData() {
        settingsMain.showDilog(this);

        if (SettingsMain.isConnectingToInternet(this)) {

            Call<ResponseBody> myCall = restService.sellerproductlist( UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info load realty Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if(response.getBoolean("success")) {
                                myProductList.clear();
                                OrderList.clear();

                                JSONArray productlist = response.getJSONArray("productlist");
                                JSONArray orderlist = response.getJSONArray("orderlist");

                                Log.d("info load productlist", productlist.toString());
                                Log.d("info load orderlist", orderlist.toString());

                                if (productlist != null && productlist.length() > 0) {
                                    for (int i = 0; i < productlist.length(); i++) {
                                        ProductDetails item = new ProductDetails();
                                        JSONObject product = productlist.getJSONObject(i);
                                        item.setData(product);

                                        if(item.getFeaturetype())
                                            myFeaturedProductList.add(item);
                                        else myProductList.add(item);
                                    }
                                }

                                if (orderlist != null && orderlist.length() > 0) {
                                    for (int i = 0; i < orderlist.length(); i++) {
                                        OrderDetail item = new OrderDetail();
                                        JSONObject product = orderlist.getJSONObject(i);
                                        item.setData(product);

                                        if(item.getStatus().equals("Processing"))
                                            OrderList.add(item);
                                        else OrderHistoryList.add(item);
                                    }
                                }

                                mSectionsPagerAdapter = new MyProductPageAdapter(getBaseContext(), getSupportFragmentManager(), myProductList, myFeaturedProductList, OrderList, OrderHistoryList);
                                mViewPager.setAdapter(mSectionsPagerAdapter);

                                mViewPager.setCurrentItem(0);
                                mSectionsPagerAdapter.notifyDataSetChanged();

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
    }
}
