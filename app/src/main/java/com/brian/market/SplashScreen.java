package com.brian.market;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brian.market.helper.LocaleHelper;
import com.brian.market.home.HomeActivity;
import com.brian.market.home.helper.Location_popupModel;
import com.brian.market.profile.ProfileActivity;
import com.brian.market.signinorup.MainActivity;
import com.brian.market.signinorup.ProfileCompleteActivity;
import com.brian.market.signinorup.WelcomeActivity;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class SplashScreen extends AppCompatActivity {

    public static JSONObject jsonObjectAppRating, jsonObjectAppShare;
    public static boolean gmap_has_countries = false, app_show_languages = false;
    public static JSONArray app_languages;
    public static String languagePopupTitle, languagePopupClose, gmap_countries;
    Activity activity;
    SettingsMain setting;
    JSONObject jsonObjectSetting;
    boolean isRTL = false;
    String gmap_lang;
    SharedPreferences prefs;
    private RestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);

        activity = this;
        setting = new SettingsMain(this);
        restService = UrlController.createService(RestService.class, setting.getAuthToken(), this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        setting.setDeviceToken(token);
                        // Log and toast
                        Log.d("FIREbaseTAG", token);
//                        Toast.makeText(getApplicationContext(), token, Toast.LENGTH_SHORT).show();
                    }
                });

        setDynamicLink();

        if (SettingsMain.isConnectingToInternet(this)) {
            setSettings();

            if (setting.getAppOpen()) {
                if(!setting.getProfileComplete()) {

//                    if(setting.getReinstall()) {
//                        requestProfileComplete();
//                    }
//                    else{
                        startActivity(new Intent(activity, ProfileCompleteActivity.class));
                        activity.finish();
//                    }
                    return;
                }

                Intent intent = new Intent(activity, HomeActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.right_enter, R.anim.left_out);
                activity.finish();
            } else {
                SplashScreen.this.finish();
                setting.setUserEmail("");
                setting.setUserImage("");
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(SplashScreen.this);
            alert.setTitle(setting.getAlertDialogTitle("error"));
            alert.setCancelable(false);
            alert.setMessage(setting.getAlertDialogMessage("internetMessage"));
            alert.setPositiveButton(setting.getAlertOkText(), (dialog, which) -> {
                dialog.dismiss();
                SplashScreen.this.recreate();
            });
            alert.show();
        }

    }

    private void requestProfileComplete() {
        if (SettingsMain.isConnectingToInternet(this)) {
                SettingsMain.showDilog(this);
                Call<ResponseBody> req = restService.getProfileDetails(UrlController.UploadImageAddHeaders(this));
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseobj) {
                        try {
                            Log.d("info UpdateProfile Resp", "" + responseobj.toString());
                            if (responseobj.isSuccessful()) {

                                JSONObject response = new JSONObject(responseobj.body().string());
                                if (response.getBoolean("success")) {
                                    JSONObject jsonObject = response.getJSONObject("data");
                                    if(jsonObject.getJSONObject("payment") != null) {
                                        setting.setProfileComplete(true);
                                        setting.setReinstall(false);
                                        startActivity(new Intent(activity, HomeActivity.class));
                                    }else {
                                        startActivity(new Intent(activity, ProfileCompleteActivity.class));
                                    }
                                    activity.finish();
                                }
                            }
                            SettingsMain.hideDilog();
                        } catch (JSONException e) {
                            SettingsMain.hideDilog();
                            e.printStackTrace();
                        } catch (IOException e) {
                            SettingsMain.hideDilog();
                            e.printStackTrace();
                        }
                        SettingsMain.hideDilog();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (t instanceof TimeoutException) {
                            Toast.makeText(getBaseContext(), setting.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            SettingsMain.hideDilog();
                        }
                        if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                            Toast.makeText(getBaseContext(), setting.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            SettingsMain.hideDilog();
                        }
                        if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                            Log.d("info UpdateProfile", "NullPointert Exception" + t.getLocalizedMessage());
                            SettingsMain.hideDilog();
                        } else {
                            SettingsMain.hideDilog();
                            Log.d("info UpdateProfile err", String.valueOf(t));
                            Log.d("info UpdateProfile err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                        }
                    }
                });
            }


    }

    private void setDynamicLink() {
        FirebaseAnalytics.getInstance(this);
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.d("firebase deeplink", deepLink.toString());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("firebase deeplink", "getDynamicLink:onFailure", e);
                    }
                });
    }

    private void setSettings() {
        Location_popupModel Location_popupModel = new Location_popupModel();
        Location_popupModel.setSlider_number(0);
        Location_popupModel.setSlider_step(10);
//        Location_popupModel.setLocation(jsonObjectLocationPopup.getString("location"));
        Location_popupModel.setText(getString(R.string.or_distance));
        Location_popupModel.setBtn_submit(getString(R.string.yes));
        Location_popupModel.setBtn_clear(getString(R.string.cancel));
        setting.setLocationPopupModel(Location_popupModel);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

}
