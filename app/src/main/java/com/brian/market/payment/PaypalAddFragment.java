package com.brian.market.payment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaypalAddFragment extends Fragment {
    SettingsMain settingsMain;
    FrameLayout loadingLayout;
    RestService restService;
    private String mPaypal="";
    Button btnPaypal;
    public PaypalAddFragment(String paypal) {
        // Required empty public constructor
        mPaypal = paypal;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_paypal_add, container, false);

        settingsMain = new SettingsMain(getContext());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getContext());

        loadingLayout = (FrameLayout) view.findViewById(R.id.loadingLayout);

        EditText paypal = view.findViewById(R.id.eidt_paypal);
        btnPaypal = view.findViewById(R.id.btn_paypal);
        if(!mPaypal.equals("null") && !mPaypal.equals(""))
            paypal.setText(mPaypal);
        btnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paypal.getText().toString().equals("")) {
                    paypal.setError("!");
                    return;
                }
                Pattern p = Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b");
                Matcher m = p.matcher(paypal.getText().toString());
                if (!m.find()) {
                    paypal.setError(getString(R.string.invalid_email));
                    return;
                }
                sendPaypal(paypal.getText().toString());
            }
        });
        return view;
    }

    private void sendPaypal(String paypal) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();
            params.addProperty("paypal", paypal);

            Call<ResponseBody> myCall = restService.paypal(params, UrlController.AddHeaders(getContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    btnPaypal.setVisibility(View.VISIBLE);
                    try {
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

                            //restart activity
                            Intent intent = getActivity().getIntent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            getActivity().overridePendingTransition(0, 0);
                            getActivity().finish();

                            getActivity().overridePendingTransition(0, 0);
                            startActivity(intent);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loadingLayout.setVisibility(View.GONE);
                    btnPaypal.setVisibility(View.VISIBLE);
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
            loadingLayout.setVisibility(View.GONE);
            btnPaypal.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }
}
