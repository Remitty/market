package com.brian.market.bitcoin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.bitcoin.adapter.WalletAdapter;
import com.brian.market.modelsList.CoinInfo;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitCoinActivity extends AppCompatActivity {
    private WalletAdapter mAdapter;
    private List<CoinInfo> coinList = new ArrayList<>();
    private RecyclerView coinListView;
    private int index;

    private ContentDialog mContentDialog;
    private String generatedAddress, coinName;
    private TextView totalAmount;
    private Handler handler;

    RestService restService;
    SettingsMain settingsMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bit_coin);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Coin Deposit");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        settingsMain = new SettingsMain(this);

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), this);

        totalAmount = findViewById(R.id.wallet_balance);

        coinListView = (RecyclerView) findViewById(R.id.coin_list_view);
        mAdapter = new WalletAdapter(this, coinList, true);
        coinListView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        coinListView.setAdapter(mAdapter);

        mAdapter.setListener(new WalletAdapter.Listener() {
            @Override
            public void OnDeposit(int position) {
                index = position;
                sendDeposit();
                coinName = coinList.get(index).getCoinSymbol();
            }
        });

//        loadCoinData();
        loadCoinList();
//        handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                loadCoinDataUpdate();
//                handler.postDelayed(this, 9000);
//            }
//        }, 10000);
    }

    private void sendDeposit() {
        CoinInfo coin = coinList.get(index);
        JsonObject params = new JsonObject();

        params.addProperty("coin", coin.getCoinId());
        params.addProperty("user_type", 1);
        if (SettingsMain.isConnectingToInternet(this)) {

            SettingsMain.showDilog(this);
            Call<ResponseBody> myCall = restService.deposit(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info AdPost Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                generatedAddress = response.optString("address");
                                showDepositDialog();

                            } else {
                                Toast.makeText(getBaseContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    SettingsMain.hideDilog();
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getBaseContext(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadCoinList() {
        if (SettingsMain.isConnectingToInternet(this)) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("user_type", "1");
            SettingsMain.showDilog(this);
            Call<ResponseBody> myCall = restService.getCoinsList(params, UrlController.AddHeaders(this));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info AdPost Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                Log.d("info AdPost object", response.toString());
                                totalAmount.setText("$ " + response.optString("wallet_balance"));

                                JSONArray coins = response.optJSONArray("coins");
                                for(int i = 0; i < coins.length(); i ++) {
                                    Log.d("coinitem", coins.get(i).toString());
                                    coinList.add(new CoinInfo((JSONObject) coins.get(i)));
                                }
                                mAdapter.notifyDataSetChanged();

                            } else {
                                Toast.makeText(getBaseContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    SettingsMain.hideDilog();
                    Log.d("info AdPost error", String.valueOf(t));
                    Log.d("info AdPost error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getBaseContext(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDepositDialog() {

        mContentDialog = new ContentDialog(R.layout.dialog_coin_deposit, generatedAddress, coinName);
        mContentDialog.setListener(new ContentDialog.Listener() {

            @Override
            public void onOk() {
//                mContentDialog.dismiss();
                Toast.makeText(getBaseContext(), "Copied successfully", Toast.LENGTH_SHORT);
                displayMessage("Copied successfully");
            }

            @Override
            public void onCancel() {
                mContentDialog.dismiss();
            }
        });

        mContentDialog.show(getSupportFragmentManager(), "deposit");
    }

    private void displayMessage(String toastString) {
        try{
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(getBaseContext(),""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
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
