package com.brian.market.cart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.brian.market.R;
import com.brian.market.models.ProductDetails;
import com.brian.market.utills.SettingsMain;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private SettingsMain settingsMain;

    ArrayList<ProductDetails> cartItemsList = new ArrayList<>();
    String shipId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        settingsMain = new SettingsMain(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settingsMain.getMainColor())));
        getSupportActionBar().setTitle(getString(R.string.my_cart));

        if(getIntent() != null) {
            cartItemsList = getIntent().getParcelableArrayListExtra("carts");
            shipId = getIntent().getStringExtra("shipId");
        }

        Fragment cartFragment = CartFragment.newInstance(cartItemsList, shipId);
        startFragment(cartFragment);

    }

    public void startFragment(Fragment someFragment) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.cart_frame);

        if (fragment == null) {
            fragment = someFragment;
            fm.beginTransaction()
                    .add(R.id.cart_frame, fragment).commit();
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
