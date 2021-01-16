package com.brian.market.messages;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.brian.market.R;
import com.brian.market.helper.SendReciveONClickListner;
import com.brian.market.messages.adapter.ItemSendRecMesageAdapter;
import com.brian.market.modelsList.messageSentRecivModel;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecievedOffersList extends Fragment {
    RecyclerView recyclerView;
    SettingsMain settingsMain;

    ArrayList<messageSentRecivModel> listitems = new ArrayList<>();

    int currentPage = 1, nextPage = 1, totalPage = 0;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    boolean loading = true, hasNextPage = false;
    ItemSendRecMesageAdapter itemSendRecMesageAdapter;
    ProgressBar progressBar;
    RestService restService;
    LinearLayout emptyLayout;

    String adID;

    public RecievedOffersList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_offers, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            adID = bundle.getString("adId", "0");
        }

        settingsMain = new SettingsMain(getActivity());

        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        emptyLayout = view.findViewById(R.id.empty_view);

        recyclerView = view.findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(MyLayoutManager);
        restService = UrlController.createService(RestService.class, settingsMain.getAuthToken(), getActivity());

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = MyLayoutManager.getChildCount();
                    totalItemCount = MyLayoutManager.getItemCount();
                    pastVisiblesItems = MyLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            if (hasNextPage) {
                                progressBar.setVisibility(View.VISIBLE);
                                adforest_loadMore(nextPage);
                            }
                        }
                    }
                }
            }
        });

        itemSendRecMesageAdapter = new ItemSendRecMesageAdapter(getActivity(), listitems);
        recyclerView.setAdapter(itemSendRecMesageAdapter);

        itemSendRecMesageAdapter.setOnItemClickListener(new SendReciveONClickListner() {
            @Override
            public void onItemClick(messageSentRecivModel item) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("chatId", item.getId());
                intent.putExtra("senderId", item.getSender_id());
                intent.putExtra("receiverId", item.getReceiver_id());
                intent.putExtra("text", item.getMessage());
                intent.putExtra("topic", item.getTopic());

                if(item.isMine()) {
                    intent.putExtra("img", settingsMain.getUserImage());
                    intent.putExtra("sender_name", "You");
                }
                else {
                    intent.putExtra("img", item.getProfile());
                    intent.putExtra("sender_name", item.getName());
                }
                intent.putExtra("type", item.isMine());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }

            @Override
            public void onItemDelete(messageSentRecivModel item, int position) {
                deleteConfirmChatItem(item.getId(), position);
            }

        });


//        adforest_getAllData();
        return view;
    }

    private void deleteConfirmChatItem(String id, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("Are you sure you want to to remove this chat history?");
        builder.setCancelable(true);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteChatItem(id, position);
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

    private void deleteChatItem(String id, int position) {
        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("chat_id", id);

            Call<ResponseBody> myCall = restService.deleteChat(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info RecievedOffer List", "Responce" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                listitems.remove(position);
                                itemSendRecMesageAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info RecievedLis", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info RecievedList error", String.valueOf(t));
                        Log.d("info RecievedList error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void adforest_loadMore(int nextPag) {

//        if (SettingsMain.isConnectingToInternet(getActivity())) {
//
//            JsonObject params = new JsonObject();
//
//            params.addProperty("page_number", nextPag);
//            params.addProperty("ad_id", adID);
//
//            Log.d("info Send OffersList", params.toString());
//            Call<ResponseBody> myCall = restService.postGetRecievedOffersList(params, UrlController.AddHeaders(getActivity()));
//            myCall.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
//                    try {
//                        if (responseObj.isSuccessful()) {
//                            Log.d("info MoreOffer List", "Responce" + responseObj.toString());
//
//                            JSONObject response = new JSONObject(responseObj.body().string());
//                            if (response.getBoolean("success")) {
//                                Log.d("info MoreOffer object", "" + response.getJSONObject("data"));
//
//                                JSONObject jsonObjectPagination = response.getJSONObject("data").getJSONObject("pagination");
//
//                                nextPage = jsonObjectPagination.getInt("next_page");
//                                currentPage = jsonObjectPagination.getInt("current_page");
//                                totalPage = jsonObjectPagination.getInt("max_num_pages");
//                                hasNextPage = jsonObjectPagination.getBoolean("has_next_page");
//
//                                JSONArray jsonArrayMessage = response.getJSONObject("data").getJSONObject("received_offers").getJSONArray("items");
//                                for (int i = 0; i < jsonArrayMessage.length(); i++) {
//
//                                    messageSentRecivModel item = new messageSentRecivModel();
//
//
//                                    item.setId(jsonArrayMessage.getJSONObject(i).getString("ad_id"));
//                                    item.setName(jsonArrayMessage.getJSONObject(i).getString("message_author_name"));
//                                    item.setTopic(jsonArrayMessage.getJSONObject(i).getString("message_ad_title"));
//                                    item.setType("receive");
//                                    item.setMessageRead(jsonArrayMessage.getJSONObject(i).getBoolean("message_read_status"));
//                                    item.setSender_id(jsonArrayMessage.getJSONObject(i).getString("message_sender_id"));
//                                    item.setReceiver_id(jsonArrayMessage.getJSONObject(i).getString("message_receiver_id"));
//                                    item.setTumbnail(jsonArrayMessage.getJSONObject(i).getString("message_ad_img"));
//
//                                    listitems.add(item);
//                                }
//                                loading = true;
//                                //recyclerView.setAdapter(itemSendRecMesageAdapter);
//                                itemSendRecMesageAdapter.notifyDataSetChanged();
//
//
//                            } else {
//                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        SettingsMain.hideDilog();
//
//                    } catch (JSONException e) {
//                        SettingsMain.hideDilog();
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        SettingsMain.hideDilog();
//                        e.printStackTrace();
//                    }
//                    SettingsMain.hideDilog();
//                    progressBar.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                    if (t instanceof TimeoutException) {
//                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
//                        SettingsMain.hideDilog();
//                    }
//                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {
//
//                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
//                        SettingsMain.hideDilog();
//                    }
//                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
//                        Log.d("info MoreOffer List", "NullPointert Exception" + t.getLocalizedMessage());
//                        SettingsMain.hideDilog();
//                    } else {
//                        SettingsMain.hideDilog();
//                        Log.d("info MoreOffer List err", String.valueOf(t));
//                        Log.d("info MoreOffer List err", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
//                    }
//                }
//            });
//        } else {
//            SettingsMain.hideDilog();
//            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
//        }
    }


    private void adforest_getAllData() {

        if (SettingsMain.isConnectingToInternet(getActivity())) {

            SettingsMain.showDilog(getActivity());

            JsonObject params = new JsonObject();
            params.addProperty("ad_id", adID);

            Log.d("info Send RecievedOffer", params.toString());
            Call<ResponseBody> myCall = restService.postGetRecievedOffersList(params, UrlController.AddHeaders(getActivity()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                            Log.d("info RecievedOffer List", "Responce" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            getActivity().setTitle(getString(R.string.chat_list));
                            if (response.getBoolean("success")) {
                                Log.d("info RecievedList obj", "" + response.getJSONArray("data"));
                                adforest_initializeList(response.getJSONArray("data"));
                                itemSendRecMesageAdapter.notifyDataSetChanged();

                                if(listitems.size() > 0) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    emptyLayout.setVisibility(View.GONE);
                                }
                                else{
                                    recyclerView.setVisibility(View.GONE);
                                    emptyLayout.setVisibility(View.VISIBLE);
                                }

                            } else {
                                Toast.makeText(getActivity(), response.get("message").toString(), Toast.LENGTH_SHORT).show();
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
                    if (t instanceof TimeoutException) {
                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof SocketTimeoutException || t instanceof NullPointerException) {

                        Toast.makeText(getActivity(), settingsMain.getAlertDialogMessage("internetMessage"), Toast.LENGTH_SHORT).show();
                        SettingsMain.hideDilog();
                    }
                    if (t instanceof NullPointerException || t instanceof UnknownError || t instanceof NumberFormatException) {
                        Log.d("info RecievedLis", "NullPointert Exception" + t.getLocalizedMessage());
                        SettingsMain.hideDilog();
                    } else {
                        SettingsMain.hideDilog();
                        Log.d("info RecievedList error", String.valueOf(t));
                        Log.d("info RecievedList error", String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                    }
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getActivity(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }


    public void adforest_initializeList(JSONArray jsonArrayMessage) {

        try {
            listitems.clear();

//            JSONArray jsonArrayMessage = jsonObjectData.getJSONObject("received_offers").getJSONArray("items");
            for (int i = 0; i < jsonArrayMessage.length(); i++) {

                messageSentRecivModel item = new messageSentRecivModel();

                JSONObject data = jsonArrayMessage.getJSONObject(i);
                String auth_name = data.getString("auth_name");
                JSONObject post = data.getJSONObject("post");

                item.setId(data.getString("id"));
                item.setName(auth_name);
                item.setTopic(post.getString("title"));

//                item.setType("receive");
//                item.setMessageRead(jsonArrayMessage.getJSONObject(i).getBoolean("message_read_status"));
                item.setSender_id(data.getString("sender_id"));
                item.setReceiver_id(data.getString("receiver_id"));
                item.setMessage(data.getString("message"));
                item.setProfile(data.getString("profile"));
                item.setChatTime(data.getString("time"));
                item.setMine(data.getBoolean("type"));
                item.setMessageRead(data.getBoolean("read"));
                item.setTumbnail(post.getJSONArray("images").getJSONObject(0).getString("thumb"));

                listitems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adforest_getAllData();
    }
}
