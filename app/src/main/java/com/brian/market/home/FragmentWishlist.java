package com.brian.market.home;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brian.market.R;
import com.brian.market.ad_detail.Ad_detail_activity;
import com.brian.market.adapters.ProductAdapter;
import com.brian.market.databases.User_Favorites_DB;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class FragmentWishlist extends Fragment {
    private ArrayList<ProductDetails> wishList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NestedScrollView scrollView;
    private LinearLayout emptyView;
    private RestService restService;
    ArrayList<Integer> favIds;
    ProductAdapter adapter;
    private SettingsMain settingsMain;
    public FragmentWishlist() {
        // Required empty public constructor
    }

    public static FragmentWishlist newInstance() {
        FragmentWishlist fragment = new FragmentWishlist();
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
        View view =  inflater.inflate(R.layout.fragment_wishlist, container, false);

        settingsMain = new SettingsMain(getActivity());

        restService = UrlController.createService(RestService.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        emptyView = view.findViewById(R.id.empty_view);
        scrollView = view.findViewById(R.id.scrollView);

        User_Favorites_DB favorites_db = new User_Favorites_DB();
        favIds = favorites_db.getUserFavorites();

        if(favIds.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
        else {

            wishList.clear();

            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            ViewCompat.setNestedScrollingEnabled(recyclerView, false);

            adapter = new ProductAdapter(getContext(), wishList);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new OnItemClickListener2() {
                @Override
                public void onItemClick(ProductDetails item) {
                    Log.d("item_id", item.getId()+"");
                    Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                    intent.putExtra("adId", ""+item.getId());
                    startActivity(intent);
                }

                @Override
                public void onRemoveFav(int position) {
                    wishList.remove(position);
                    adapter.notifyDataSetChanged();

                    favIds = favorites_db.getUserFavorites();

                    if(favIds.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                        scrollView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onShare(ProductDetails item) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, item.getCardName());
                    i.putExtra(Intent.EXTRA_TEXT, "link");
                    startActivity(Intent.createChooser(i, "Share Product"));
                }

                @Override
                public void onAddCart(ProductDetails item) {

                }
            });

            getFavProducts();
        }
        return view;
    }

    private void getFavProducts() {
        if (SettingsMain.isConnectingToInternet(getContext())) {
            SettingsMain.showDilog(getContext());
            JsonObject params = new JsonObject();
            params.addProperty("fav_ids", TextUtils.join(",", favIds));
            Log.d("send fav_ids", params.toString());
            Call<ResponseBody> myCall = restService.postGetLoadMoreFavouriteAds(params, UrlController.AddHeaders(getContext()));
            myCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> responseObj) {
                    try {
                        Log.d("info invoice Responce", "" + responseObj.toString());
                        if (responseObj.isSuccessful()) {

                            JSONObject response = new JSONObject(responseObj.body().string());
                            if (response.getBoolean("success")) {
                                JSONArray data = response.getJSONArray("fav_products");
                                Log.d("fav products", data.toString());
                                for (int i = 0; i < data.length(); i++) {
                                    ProductDetails item = new ProductDetails();
                                    item.setData(data.optJSONObject(i));
                                    wishList.add(item);
                                }

                                adapter.notifyDataSetChanged();
                            }
                            else
                                Toast.makeText(getContext(), response.optString("message"), Toast.LENGTH_SHORT).show();
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
                    Log.d("info Send offers ", "error" + String.valueOf(t));
                    Log.d("info Send offers ", "error" + String.valueOf(t.getMessage() + t.getCause() + t.fillInStackTrace()));
                }
            });
        } else {
            SettingsMain.hideDilog();
            Toast.makeText(getContext(), getString(R.string.internet_error), Toast.LENGTH_SHORT).show();
        }
    }
}
