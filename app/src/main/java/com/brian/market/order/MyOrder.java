package com.brian.market.order;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.doba.helper.Doba;
import com.brian.market.models.OrderDetail;
import com.brian.market.order.adapter.MyOrderAdapter;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrder extends Fragment {
    RecyclerView recyclerHistory;
    MyOrderAdapter mAdapter;
    private Context context;
    private List<OrderDetail> orderList = new ArrayList<>();
    private SettingsMain settingsMain;
    private RestService restService;
    private OrderDetail order;
    private int index;

    public MyOrder() {
        // Required empty public constructor
    }

    public static MyOrder newInstance(List<OrderDetail> history) {
        MyOrder fragment = new MyOrder();
        fragment.orderList = history;
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
        View view = inflater.inflate(R.layout.fragment_my_order_history, container, false);
        context = getContext();
        settingsMain = new SettingsMain(context);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), context);

        recyclerHistory = view.findViewById(R.id.recycler_history);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayout emptyLayout = view.findViewById(R.id.empty_view);

        if(orderList.size() > 0) {
            recyclerHistory.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
        else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
        }

        mAdapter = new MyOrderAdapter(getActivity(), orderList, true);
        recyclerHistory.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyOrderAdapter.Listener(){
            @Override
            public void onItemEdit(int position) {

            }

            @Override
            public void onItemCancel(int position) {
                order = orderList.get(position);
                index = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.message_cancel_order));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if((order.getPayment().equals("Token") || order.getPayment().equals("DobaCard")) && !order.getTxnId().isEmpty())
                            dobaOrderCancel();
                        else orderCancel();
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

            @Override
            public void onItemClick(int position) {
                order = orderList.get(position);
                index = position;
//                OrderDetail item = orderList.get(position);
//                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
//                intent.putExtra("adId", item.getId());
//                startActivity(intent);
            }

            @Override
            public void onItemConfirm(int position) {
                order = orderList.get(position);
                index = position;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.message_confirm_order));
                builder.setCancelable(true);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if((order.getPayment().equals("Token") || order.getPayment().equals("DobaCard")) && !order.getTxnId().isEmpty())
                            dobarOrderConfirm();
                        else orderConfirm();
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

        });
        mAdapter.notifyDataSetChanged();
        return view;
    }

    private void dobarOrderConfirm() {
        settingsMain.showDilog(getActivity());

        Doba doba = new Doba();
        okhttp3.Call mycall = doba.requestConfirmOrder(order.getTxnId());
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                settingsMain.hideDilog();

                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                settingsMain.hideDilog();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba good onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONArray bdata = object.getJSONArray("businessData");
                        if(bdata.getJSONObject(0).getBoolean("successful"))
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    orderConfirm();
                                }
                            });

                        else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), bdata.getJSONObject(0).getString("businessMessage"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void dobaOrderCancel() {
        settingsMain.showDilog(getActivity());

        Doba doba = new Doba();
        okhttp3.Call mycall = doba.requestCancelOrder(order.getTxnId(), "This was test");
        mycall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.@NotNull Call call, @NotNull IOException e) {
                settingsMain.hideDilog();

                Log.d("doba here", "failed");
            }

            @Override
            public void onResponse(okhttp3.@NotNull Call call, okhttp3.@NotNull Response response) throws IOException {
                settingsMain.hideDilog();
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                String responseData = response.body().string();
                Log.e("doba good onResponse:", responseData);
                JSONObject object = null;
                try {
                    object = new JSONObject(responseData);
                    if(object.getInt("responseCode") == 0) {
                        JSONArray bdata = object.getJSONArray("businessData");
                        if(bdata.getJSONObject(0).getBoolean("successful"))
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    orderCancel();
                                }
                            });

                        else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Toast.makeText(getContext(), bdata.getJSONObject(0).getString("businessMessage"), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });
    }

    private void orderConfirm() {
        settingsMain.showDilog(context);

        if (SettingsMain.isConnectingToInternet(context)) {

            JsonObject params = new JsonObject();
            params.addProperty("order_id", order.getOrderId());

            Call<ResponseBody> myCall = restService.orderconfirm(params, UrlController.AddHeaders(context));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info orderconfirm Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if(response.getBoolean("success")) {
                                orderList.remove(index);
                                mAdapter.notifyDataSetChanged();
                            }

                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();

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
                    settingsMain.hideDilog();
                    if (t instanceof TimeoutException) {
                        Toast.makeText(context, settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
                        Toast.makeText(context, settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                    }
                    else {
                        Toast.makeText(context, "Something error", Toast.LENGTH_SHORT).show();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            settingsMain.hideDilog();
            Toast.makeText(context, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }

    private void orderCancel() {
        settingsMain.showDilog(context);

        if (SettingsMain.isConnectingToInternet(context)) {

            JsonObject params = new JsonObject();
            params.addProperty("order_id", order.getOrderId());
            Log.d("cancel order id", order.getOrderId());

            Call<ResponseBody> myCall = restService.ordercancel(params, UrlController.AddHeaders(context));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info ordercancel Resp", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if(response.getBoolean("success")) {
                                orderList.remove(index);
                                mAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
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
                    settingsMain.hideDilog();
                    if (t instanceof TimeoutException) {
                        Toast.makeText(context, settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
                        Toast.makeText(context, settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info Checkout ", "NullPointert Exception" + t.getLocalizedMessage());
                    }
                    else {
                        Toast.makeText(context, "Something error", Toast.LENGTH_SHORT).show();
                        Log.d("info Checkout err", String.valueOf(t));
                        Log.d("info Checkout err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            settingsMain.hideDilog();
            Toast.makeText(context, settingsMain.getAlertDialogTitle("error"), Toast.LENGTH_SHORT).show();
        }
    }
}
