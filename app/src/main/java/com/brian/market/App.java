package com.brian.market;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.brian.market.databases.DB_Handler;
import com.brian.market.databases.DB_Manager;
import com.facebook.drawee.backends.pipeline.Fresco;
//import com.google.android.gms.ads.MobileAds;
import com.google.android.libraries.places.api.Places;

import com.brian.market.R;
import com.brian.market.helper.LocaleHelper;
import com.brian.market.utills.NoInternet.AppLifeCycleManager;

public class App extends Application {
    private static Context context;
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
