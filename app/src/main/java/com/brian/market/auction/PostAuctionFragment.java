package com.brian.market.auction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.auction.adapter.AuctionAdapter;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.helper.OnAuctionItemClickListener;
import com.brian.market.modelsList.Auction;
import com.brian.market.payment.ShippingActivity;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class PostAuctionFragment extends Fragment implements RuntimePermissionHelper.permissionInterface {

    SettingsMain settingsMain;
    RestService restService;
    RuntimePermissionHelper runtimePermissionHelper;

    private ArrayList<Auction> auctionList = new ArrayList<>();
    RecyclerView recyclerView;

    AuctionAdapter auctionAdapter;

    LinearLayout emptyLayout;

    private String maskedPhoneNumber, phone;

    public PostAuctionFragment(ArrayList<Auction> list) {
        // Required empty public constructor
        this.auctionList = list;
    }
    public static PostAuctionFragment newInstance(ArrayList<Auction> list) {
        PostAuctionFragment fragment = new PostAuctionFragment(list);
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
        View view = inflater.inflate(R.layout.fragment_post_auction, container, false);

        settingsMain = new SettingsMain(getActivity());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);

        auctionAdapter = new AuctionAdapter(getActivity(), auctionList, 1);
        auctionAdapter.setOnItemClickListener(new OnAuctionItemClickListener() {
            @Override
            public void onItemClick(Auction item) {
                Intent intent = new Intent(getActivity(), AuctionDetailActivity.class);
                intent.putExtra("post_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onViewDetailClick(String id) {
                getDefaultShippingAddress();
            }

            @Override
            public void onConfirm(Auction item) {

            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(auctionAdapter);

        emptyLayout = view.findViewById(R.id.empty);
        if(auctionList.size() == 0)
            emptyLayout.setVisibility(View.VISIBLE);

        return view;
    }



    private void getDefaultShippingAddress() {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            Call<ResponseBody> myCall = restService.getDefaultShippingAddress(UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info invoice Responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("shipping_addres", response.toString());
                            if (response.getBoolean("success")) {
                                JSONObject address = response.optJSONObject("address");
                                boolean isDefault = response.getBoolean("hasDefault");
                                displayAddressDialog(address, isDefault);
                            }
                            else
                                Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayAddressDialog(JSONObject address, boolean isDefault) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setCancelable(true);

        View alertview = getActivity().getLayoutInflater().inflate(R.layout.dialog_shipping_address, null);
        TextView contact = alertview.findViewById(R.id.shipping_contact_edit);
        TextView street = alertview.findViewById(R.id.shipping_street_edit);
        TextView apartment = alertview.findViewById(R.id.shipping_apartment_edit);
        TextView state = alertview.findViewById(R.id.address_autoCompleteTextView);
        TextView postalcode = alertview.findViewById(R.id.shipping_postal_code_edit);
        ImageButton ringBtn = alertview.findViewById(R.id.btn_phone);
        ImageButton btnExitDialog = alertview.findViewById(R.id.dialog_exit_btn);
        if(address != null ) {
            if (isDefault) {
                state.setText(address.optString("state_country"));
                street.setText(address.optString("street"));
                apartment.setText(address.optString("apartment"));
                postalcode.setText(address.optString("postal_code"));
            } else {
                state.setText(address.optString("state"));
                street.setText(address.optString("address"));
                apartment.setText(address.optString("address2"));
                postalcode.setText(address.optString("postalcode"));
            }

            phone = address.optString("mobile");
            contact.setText(address.optString("contact_name"));
        }
        ringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runtimePermissionHelper.requestCallPermission(1);
            }
        });


        builder.setView(alertview);
        AlertDialog alert = builder.create();
        btnExitDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    private void generateMaskCall(String shippingMobile) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            JsonObject params = new JsonObject();
            params.addProperty("mobile", shippingMobile);

            Log.d("info send AdDetails", "" + params.toString());

            Call<ResponseBody> myCall = restService.maskedNumber(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info masked mobile ", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());

                            if (response.getBoolean("success")) {
                                maskedPhoneNumber = response.getString("phone");
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + maskedPhoneNumber));
                                startActivity(intent);

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
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
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    SettingsMain.hideDilog();
                    Log.d("info AdDetails error", String.valueOf(t));
                    Log.d("info AdDetails error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });

        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), "Internet error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public void onSuccessPermission(int code) {
        generateMaskCall(phone);
    }
}
