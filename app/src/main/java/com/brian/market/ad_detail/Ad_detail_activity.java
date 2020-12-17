package com.brian.market.ad_detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.brian.market.R;
import com.brian.market.helper.LocaleHelper;
import com.brian.market.home.AddNewProductPost;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;

public class Ad_detail_activity extends AppCompatActivity implements RuntimePermissionHelper.permissionInterface {

    SettingsMain settingsMain;
    Intent intent;
    RuntimePermissionHelper runtimePermissionHelper;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_detail_activity);

        settingsMain = new SettingsMain(this);
        intent = getIntent();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(settingsMain.getMainColor()));
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                window.setStatusBarColor(getColor(R.color.colorRedCrayon));
//            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        runtimePermissionHelper = new RuntimePermissionHelper(this, this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(settingsMain.getMainColor())));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runtimePermissionHelper.requestLocationPermission(1);
            }
        });

        toolbar.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

//        if (settingsMain.getAppOpen()) {
//            fab.setVisibility(View.GONE);
//        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentAdDetail fragmentAdDetail = new FragmentAdDetail();

        Bundle bundle = new Bundle();
        bundle.putString("id", intent.getStringExtra("adId"));
        bundle.putString("detail_type", intent.getStringExtra("detail_type"));
        bundle.putString("book_id", intent.getStringExtra("book_id"));
        bundle.putString("is_rejected", intent.getStringExtra("is_rejected"));
//        bundle.putString("r_id", intent.getStringExtra("r_id"));
//        bundle.putString("c_id", intent.getStringExtra("c_id"));

        fragmentAdDetail.setArguments(bundle);
        startFragment(fragmentAdDetail, "FragmentAdDetail");
        updateViews(settingsMain.getAlertDialogMessage("gmap_lang"));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    private void updateViews(String languageCode) {
        LocaleHelper.setLocale(this, languageCode);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_enter, R.anim.right_out);
    }

    public void startFragment(Fragment someFragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentByTag(tag);

        if (fragment == null) {
            fragment = someFragment;
            fm.beginTransaction()
                    .add(R.id.frameContainer, fragment, tag).commit();
        }
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
    public void onSuccessPermission(int code) {
        Intent intent = new Intent(Ad_detail_activity.this, AddNewProductPost.class);
        startActivity(intent);
    }
}
