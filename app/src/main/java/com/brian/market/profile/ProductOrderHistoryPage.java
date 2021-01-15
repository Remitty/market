package com.brian.market.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.modelsList.OrderDetail;
import com.brian.market.order.adapter.MyOrderAdapter;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.payment.StripePayment;
import com.brian.market.profile.adapter.SellerOrderAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.RuntimePermissionHelper;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductOrderHistoryPage extends Fragment implements Serializable, RuntimePermissionHelper.permissionInterface{
    private ArrayList<OrderDetail> orderList = new ArrayList<>();
    OrderDetail selectedOrder;
    RecyclerView recyclerOrder;
    SellerOrderAdapter mAdapter;
    private RestService restService;
    private SettingsMain settingsMain;
    private String maskedPhoneNumber;
    RuntimePermissionHelper runtimePermissionHelper;

    public ProductOrderHistoryPage() {
        // Required empty public constructor
    }

    public static ProductOrderHistoryPage newInstance(ArrayList<OrderDetail> orders) {
        ProductOrderHistoryPage fragment = new ProductOrderHistoryPage();
        fragment.orderList = orders;
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
        View view = inflater.inflate(R.layout.fragment_seller_order_page, container, false);

        settingsMain = new SettingsMain(getContext());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
        runtimePermissionHelper = new RuntimePermissionHelper(getActivity(), this);

        recyclerOrder = view.findViewById(R.id.recycler_rental);
        recyclerOrder.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayout emptyLayout = view.findViewById(R.id.empty_view);

        if(orderList.size() > 0) {
            recyclerOrder.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
        else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerOrder.setVisibility(View.GONE);
        }

        mAdapter = new SellerOrderAdapter(getActivity(), orderList);
        mAdapter.setOnItemClickListener(new SellerOrderAdapter.Listener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onShippingClick(OrderDetail order) {
                selectedOrder = order;
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

                contact.setText(order.getShippingContactName());
                street.setText(order.getShippingStreet());
                apartment.setText(order.getShippingApartment());
                state.setText(order.getShippingStateCountry());
                postalcode.setText(order.getShippingPostalCode());
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
        });
        recyclerOrder.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

        return view;
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
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    @Override
    public void onSuccessPermission(int code) {
        generateMaskCall(selectedOrder.getShippingMobile());
    }
}
