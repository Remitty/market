package com.brian.market.auction;

import android.content.DialogInterface;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.auction.adapter.MyAuctionAdapter;
import com.brian.market.helper.OnAuctionItemClickListener;
import com.brian.market.modelsList.Auction;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class WinAuctionFragment extends Fragment {

    SettingsMain settingsMain;
    RestService restService;

    private ArrayList<Auction> auctionList = new ArrayList<>();
    RecyclerView recyclerView;

    MyAuctionAdapter myAuctionAdapter;

    LinearLayout emptyLayout;

    public WinAuctionFragment(ArrayList<Auction> list) {
        // Required empty public constructor
        this.auctionList = list;
    }

    public static WinAuctionFragment newInstance(ArrayList<Auction> list) {
        WinAuctionFragment fragment = new WinAuctionFragment(list);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settingsMain = new SettingsMain(getActivity());
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_win_auction, container, false);

        myAuctionAdapter = new MyAuctionAdapter(getActivity(), auctionList, 3);
        myAuctionAdapter.setOnItemClickListener(new OnAuctionItemClickListener() {
            @Override
            public void onItemClick(Auction item) {

            }

            @Override
            public void onViewDetailClick(String id) {

            }

            @Override
            public void onConfirm(Auction item) {
                confrimAlertDialog(item);
            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAuctionAdapter);

        emptyLayout = view.findViewById(R.id.empty);
        if(auctionList.size() == 0)
            emptyLayout.setVisibility(View.VISIBLE);

        return view;
    }

    private void confrimAlertDialog(Auction item) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getActivity().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Are you sure you want to confirm this auction?");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendAuctionConfrim(item);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private void sendAuctionConfrim(Auction item) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {
            settingsMain.showDilog(getActivity());
            JsonObject params = new JsonObject();
            params.addProperty("id", item.getId());
            Call<ResponseBody> myCall = restService.postConfirm(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    settingsMain.hideDilog();
                    try {
                        Log.d("info invoice Responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            Log.d("shipping_addres", response.toString());
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
                    settingsMain.hideDilog();
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }
}
