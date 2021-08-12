package com.brian.market.doba;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brian.market.R;
import com.brian.market.cart.CartActivity;
import com.brian.market.doba.helper.Doba;
import com.brian.market.doba.models.DobaProduct;
import com.brian.market.models.ProductDetails;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;

public class DobaProductActivity extends AppCompatActivity {

    SettingsMain settingsMain;
    RestService restService;

    TextView tvTitle, tvShip, tvPrice, tvShipMethod, tvQty;
    HtmlTextView htmlTextView;
    LinearLayout buyNow;

    BannerSlider bannerSlider;
    List<Banner> banners = new ArrayList();
    ArrayList<String> imageUrls = new ArrayList();

    String spuId;
    DobaProduct product;
    ArrayList<ProductDetails> carts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doba_product);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.shop_wholesale));
        }

        if(getIntent() != null) {
            spuId = getIntent().getStringExtra("spuId");
        }

        settingsMain = new SettingsMain(this);
        restService = UrlController.createService(RestService.class);

        initComponents();
        initListeners();

        getData();
    }


    private void initComponents() {
        tvTitle = findViewById(R.id.text_view_name);
        tvPrice = findViewById(R.id.prices);
        tvShip = findViewById(R.id.shipping);
        tvShipMethod = findViewById(R.id.shipping_method);
        tvQty = findViewById(R.id.qty);
        htmlTextView = findViewById(R.id.html_text);
        buyNow = findViewById(R.id.ll_buy);
        bannerSlider = findViewById(R.id.banner_slider1);
    }

    private void initListeners() {
        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DobaProductActivity.this, CartActivity.class);
                carts.clear();
                JSONObject object = new JSONObject();
                try {
                    object.put("type", "");
                    object.put("path", "");
                    object.put("unit_price", product.getMarketPrice());
                    object.put("updated_at", "2020-12-12");
                    object.put("location", "");
                    object.put("isFav", false);
                    object.put("id", product.getItemNo());
                    object.put("qty", product.getQty());
                    object.put("title", product.getTitle());
                    object.put("images", product.getPics());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ProductDetails detail = new ProductDetails();
                detail.setData(object);
                detail.setCustomersBasketQuantity(1);
                carts.add(detail);
                intent.putExtra("shipId", product.getShipId());
                intent.putParcelableArrayListExtra("carts", carts);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        settingsMain.showDilog(this);

        Doba doba = new Doba();
        okhttp3.Call mycall = doba.requestGood(spuId);
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
                Log.e("doba good onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONObject bdata = object.getJSONObject("businessData");
                        JSONObject data = bdata.getJSONArray("data").getJSONObject(0);
                        product = new DobaProduct(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        banners.clear();
                        imageUrls.clear();
                        bannerSlider.removeAllBanners();

                        for (int i = 0; i < product.getPics().length(); i++) {
                            String path = null;
                            try {
                                path = product.getPics().getJSONObject(i).getString("thumb");
                                banners.add(new RemoteBanner(path));
                                imageUrls.add(path);
                                banners.get(i).setScaleType(ImageView.ScaleType.FIT_XY);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (banners.size() > 0)
                            bannerSlider.setBanners(banners);
                        if(banners.size() > 1)
                            bannerSlider.setInterval(7000);

                        tvTitle.setText(product.getTitle());
                        htmlTextView.setHtml(product.getDescription());
                        tvPrice.setText(product.getMarketPrice() + " " + product.getCurrency());
                        tvShip.setText(product.getShipTo());
                        tvQty.setText(product.getQty());
                        tvShipMethod.setText(product.getShipMethod());
                    }
                });
            }
        });
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
