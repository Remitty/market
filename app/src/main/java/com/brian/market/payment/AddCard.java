package com.brian.market.payment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.brian.market.R;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import androidx.fragment.app.Fragment;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCard extends Fragment {
    CardInputWidget stripeWidget;
    SettingsMain settingsMain;
    FrameLayout loadingLayout;
    RestService restService;
    String cvcNo, cardNo;
    String stringCardError, stringExpiryError, stringCVCError, stringInvalidCard;
    int month, year;
    private String PUBLISHABLE_KEY;

    Button btnAddCard;

    public AddCard() {
        // Required empty public constructor
    }

    public static AddCard newInstance() {
        AddCard fragment = new AddCard();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_card, container, false);

        settingsMain = new SettingsMain(getContext());
        PUBLISHABLE_KEY = settingsMain.getKey("stripeKey");

        stripeWidget = (CardInputWidget) view.findViewById(R.id.stripe_widget);
        loadingLayout = (FrameLayout) view.findViewById(R.id.loadingLayout);

        btnAddCard = view.findViewById(R.id.btn_add_card);

//        btnAddCard.setBackgroundColor(Color.parseColor(settingsMain.getMainColor()));

        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getContext());

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stripeWidget.getCard() != null && !TextUtils.isEmpty(stripeWidget.getCard().getNumber())) {
                    showLoading();
                    checkoutStripe();
                }

            }
        });

        stringCardError = "Invalidate Card";
        stringExpiryError = "Invalidate Expiry";
        stringCVCError = "Invalidate CVC";
        stringInvalidCard = "Invalidate Card";

        return view;
    }

    private void checkoutStripe() {

        cvcNo = stripeWidget.getCard().getCVC();
        cardNo = stripeWidget.getCard().getNumber();
        month = stripeWidget.getCard().getExpMonth();
        year = stripeWidget.getCard().getExpYear();

        Card card = new Card(cardNo, month, year, cvcNo);

        boolean validation = card.validateCard();
        if (validation) {
            if (SettingsMain.isConnectingToInternet(getActivity())) {

                Stripe stripe = new Stripe(getActivity(), PUBLISHABLE_KEY);
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                // Send token to your server
                                Log.e("token success", token.toString());
                                Log.e("token success", token.getId());

                                sendAddCard(token);
                            }

                            public void onError(Exception error) {
                                // Show localized error message
                                Log.e("token fail", error.getLocalizedMessage());
                                loadingLayout.setVisibility(View.GONE);
                                handleError(error.getLocalizedMessage());
                            }
                        }
                );
            } else {
                loadingLayout.setVisibility(View.GONE);
                Snackbar.make(getView().findViewById(android.R.id.content), settingsMain.getAlertDialogMessage("internetMessage"), Snackbar.LENGTH_LONG).show();
            }
        } else if (!card.validateNumber()) {
            loadingLayout.setVisibility(View.GONE);
            handleError("Invalidate Card");
        } else if (!card.validateExpiryDate()) {
            loadingLayout.setVisibility(View.GONE);
            handleError(stringExpiryError);
        } else if (!card.validateCVC()) {
            loadingLayout.setVisibility(View.GONE);
            handleError(stringCVCError);
        } else {
            loadingLayout.setVisibility(View.GONE);
            handleError(stringInvalidCard);
        }
    }

    private void sendAddCard(Token token) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();
            params.addProperty("stripe_token", token.getId());

            Call<ResponseBody> myCall = restService.postCard(params, UrlController.AddHeaders(getContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    loadingLayout.setVisibility(View.GONE);
                    try {
                            Log.d("info addcard Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            Toast.makeText(getContext(), response.get("message").toString(), Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getActivity(), settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(){
        Drawable drawable = getResources().getDrawable(R.drawable.bg_uploading).mutate();
        drawable.setColorFilter(Color.parseColor(SettingsMain.getMainColor()), PorterDuff.Mode.SRC_ATOP);
        loadingLayout.setBackground(drawable);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void handleError(String error) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle(settingsMain.getAlertDialogTitle("error"));
        alert.setMessage(error);
        alert.setPositiveButton(settingsMain.getAlertOkText(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
