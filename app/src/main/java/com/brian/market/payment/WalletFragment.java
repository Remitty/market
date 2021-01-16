package com.brian.market.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.StripeData;
import com.brian.market.utills.UrlController;
import com.github.thiagolocatelli.stripe.StripeApp;
import com.github.thiagolocatelli.stripe.StripeButton;
import com.github.thiagolocatelli.stripe.StripeConnectListener;
import com.github.thiagolocatelli.stripe.StripeSession;
import com.google.gson.JsonObject;
import com.stripe.Stripe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

public class WalletFragment extends Fragment {

    View mView;

    SettingsMain settingsMain;
    RestService restService;

    TextView tvBalance;
    EditText editCashAmount;
    Button btnCashout, btnCashoutPaypal;
    RelativeLayout loadingLayout;
    String stripeAccount;

    private StripeApp mApp;
    private StripeButton mStripeButton;
    String wallet, paypal;

    public WalletFragment(String wallet, String paypal) {
        // Required empty public constructor
        this.wallet = wallet;
        this.paypal = paypal;
    }

    public static WalletFragment newInstance(String wallet, String paypal) {
        WalletFragment fragment = new WalletFragment(wallet, paypal);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_wallet, container, false);

        settingsMain = new SettingsMain(getActivity());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());

        initComponents();

        initStripeConnect();

        btnCashout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PlaidConnect.initPlaid(WithdrawActivity.this);
                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder.setTitle(getActivity().getResources().getString(R.string.app_name))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage("Are you sure you want to withdraw $ " + editCashAmount.getText() + " via bank?");
                    builder.setCancelable(true);
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestWithdraw("Bank");
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }
        });

        btnCashoutPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paypal.equals("") || paypal.equals("null")) {
                    Toast.makeText(getContext(), "No paypal", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    builder.setTitle(getActivity().getResources().getString(R.string.app_name))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage("Are you sure you want to withdraw $ " + editCashAmount.getText() + " to " + paypal +" ?");
                    builder.setCancelable(true);
                    builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestWithdraw("Paypal");
                        }
                    });
                    builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            }
        });

        return mView;
    }

    private boolean validate() {
        boolean validate = true;
        if(editCashAmount.getText().toString().equals("")) {
            validate = false;
            editCashAmount.setError("!");
        }
        return validate;
    }

    private void initStripeConnect() {
        mApp = new StripeApp(getActivity(), "StripeAccount", StripeData.CLIENT_ID,
                settingsMain.getKey("stripeKey"), StripeData.CALLBACK_URL);

        mStripeButton = (StripeButton) mView.findViewById(R.id.btnStripeConnect);
        mStripeButton.setStripeApp(mApp);
        mStripeButton.addStripeConnectListener(new StripeConnectListener() {

            @Override
            public void onConnected() {
//                tvSummary.setText("Connected as " + mApp.getAccessToken());

                Stripe.apiKey = mApp.getAccessToken();
                StripeSession stripeSession = mApp.getStripeSession();
                stripeAccount = stripeSession.getUserId();
                if (SettingsMain.isConnectingToInternet(getActivity())) {
                    JsonObject params = new JsonObject();
                    params.addProperty("stripe_account_id", stripeAccount);

                    Call<ResponseBody> myCall = restService.stripeconnect(params, UrlController.AddHeaders(getActivity()));
                    myCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                            Log.d("info withdraw Resp", "" + responseObj.toString());
                            if (responseObj.isSuccessful()) {
                                Toast.makeText(getActivity(), "Connected successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            if (t instanceof TimeoutException) {
                                Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            }
                            if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
                                Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                            }
                            if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                                Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                            }
                            else {
                                Toast.makeText(getActivity(), "Something error", Toast.LENGTH_SHORT).show();
                                Log.d("info Checkout err", String.valueOf(t));
                                Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDisconnected() {
                Toast.makeText(getActivity(), "Disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initComponents() {
        tvBalance = mView.findViewById(R.id.text_balance);
        tvBalance.setText("$ " + wallet);
        editCashAmount = mView.findViewById(R.id.edit_cash_amount);
        btnCashout = mView.findViewById(R.id.btn_cashout);
        btnCashoutPaypal = mView.findViewById(R.id.btn_cashout_paypal);
        loadingLayout = mView.findViewById(R.id.loadingLayout);
    }

    private void requestWithdraw(String method) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            loadingLayout.setVisibility(View.VISIBLE);
            JsonObject params = new JsonObject();
            params.addProperty("withdraw_price", editCashAmount.getText().toString());
            params.addProperty("method", method);

            Call<ResponseBody> myCall = restService.withdraw(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                        Log.d("info withdraw Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            settingsMain.showAlertDialog(getActivity(), response.get("message").toString());

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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                    }
                    else {
                        Toast.makeText(getActivity(), "Something error", Toast.LENGTH_SHORT).show();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            loadingLayout.setVisibility(View.GONE);
            Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }
}
