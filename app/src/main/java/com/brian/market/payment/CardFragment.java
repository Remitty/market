package com.brian.market.payment;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.modelsList.CreditCard;
import com.brian.market.payment.adapter.CardPagerAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CardFragment extends Fragment {
    private CreditCard mCard;
    private ViewPager mCardPager;
    SettingsMain settingsMain;
    RestService restService;

    public CardFragment(CreditCard card, ViewPager pager) {
        // Required empty public constructor
        mCard = card;
        mCardPager = pager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_card, container, false);

        settingsMain = new SettingsMain(getContext());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getContext());

        ImageView cardLogo = view.findViewById(R.id.card_logo);
        TextView cardId = view.findViewById(R.id.card_id);
        Button btnCard = view.findViewById(R.id.btn_card);
        Button btnCardDelete = view.findViewById(R.id.btn_card_delete);


        if(mCard.getData() != null) {
            if(mCard.getBrand().equals("Visa"))
                cardLogo.setImageDrawable(getActivity().getDrawable(R.drawable.ic_visa));
            else
                cardLogo.setImageDrawable(getActivity().getDrawable(R.drawable.ic_mastercard));

            cardId.setText("XXXX-XXXX-XXXX-" + mCard.getLastFour());

            btnCard.setText("Change");
            btnCardDelete.setVisibility(View.VISIBLE);
        }

        btnCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCardPager.setCurrentItem(1);
            }
        });

        btnCardDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getContext().getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Are you sure you want to delete this card?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendCardDeleteRequest(mCard.getCardId());
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

    private void sendCardDeleteRequest(String card_id) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            JsonObject params = new JsonObject();
            params.addProperty("card_id", card_id);

            Call<ResponseBody> myCall = restService.deleteCard(params, UrlController.AddHeaders(getContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
//                    loadingLayout.setVisibility(View.GONE);
                    try {
                        Log.d("info delete card Resp", "" + responseObj.toString());
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
