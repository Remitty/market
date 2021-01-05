package com.brian.market.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaypalFragment extends Fragment {
    private String mPaypal;
    private ViewPager mPaypalPager;
    SettingsMain settingsMain;
    RestService restService;

    public PaypalFragment(String paypal, ViewPager paypalPager) {
        // Required empty public constructor
        mPaypal = paypal;
        mPaypalPager = paypalPager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_paypal, container, false);

        settingsMain = new SettingsMain(getContext());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getContext());

        TextView paypal = view.findViewById(R.id.paypal_id);
        Button btnPaypal = view.findViewById(R.id.btn_paypal);
        Button btnPaypalDelete = view.findViewById(R.id.btn_paypal_delete);

        if(!mPaypal.equals("") && !mPaypal.equals("null")) {
            paypal.setText(mPaypal);
            btnPaypal.setText("Edit");
            btnPaypalDelete.setVisibility(View.VISIBLE);
        }

        btnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPaypalPager.setCurrentItem(1);
            }
        });

        btnPaypalDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getContext().getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Are you sure you want to delete this paypal?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendPaypalDeleteRequest();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    private void sendPaypalDeleteRequest() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            Call<ResponseBody> myCall = restService.paypal_delete(UrlController.AddHeaders(getContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
//                    loadingLayout.setVisibility(View.GONE);
                    try {
                        Log.d("info delete card Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), PaymentActivity.class));
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
                        Toast.makeText(getContext(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                    }
                    else {
                        Toast.makeText(getContext(), "Something error", Toast.LENGTH_SHORT).show();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
//            loadingLayout.setVisibility(View.GONE);
            Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }
}
