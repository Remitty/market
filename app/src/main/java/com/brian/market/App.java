package com.brian.market;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.brian.market.databases.DB_Handler;
import com.brian.market.databases.DB_Manager;
import com.facebook.drawee.backends.pipeline.Fresco;
//import com.google.android.gms.ads.MobileAds;
import com.google.android.libraries.places.api.Places;

import com.brian.market.helper.LocaleHelper;
import com.brian.market.utills.NoInternet.AppLifeCycleManager;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class App extends Application {
    private static Context context;
    public IWXAPI iwxAPI;
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);

        context = this.getApplicationContext();

        DB_Handler db_handler = new DB_Handler();
        DB_Manager.initializeInstance(db_handler);

//        MobileAds.initialize(this, String.valueOf(R.string.Admob_app_id));
        AppLifeCycleManager.init(this);
        String apiKey = getString(R.string.places_api_key);
        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        iwxAPI = WXAPIFactory.createWXAPI(this, getString(R.string.wechat_app_id), true);
        iwxAPI.registerApp(getString(R.string.wechat_app_id));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
        MultiDex.install(this);
    }

    public static Context getContext(){
        return context;
    }
}
