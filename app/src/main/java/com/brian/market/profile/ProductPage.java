package com.brian.market.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brian.market.R;
import com.brian.market.ad_detail.Ad_detail_activity;
import com.brian.market.adapters.ItemSearchFeatureAdsAdapter;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.profile.adapter.MyProductAdapter;
import com.brian.market.helper.MyProductOnclicklinstener;
import com.brian.market.models.ProductDetails;
import com.brian.market.utills.Network.RestService;
import com.brian.market.utills.SettingsMain;
import com.brian.market.utills.UrlController;

import java.util.ArrayList;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductPage extends Fragment {

    private ArrayList<ProductDetails> featuredList = new ArrayList<>();
    private ArrayList<ProductDetails> realtyList = new ArrayList<>();

    SettingsMain settingsMain;
    RestService restService;

    RecyclerView MyRecyclerView, recyclerViewFeatured;

    MyProductAdapter myProductAdapter;
    ItemSearchFeatureAdsAdapter itemSearchFeatureAdsAdapter;

    NestedScrollView scrollView;

    GridLayoutManager MyLayoutManager2;
    LinearLayout ll_featured;

    public ProductPage() {
        // Required empty public constructor
    }

    public static ProductPage newInstance(ArrayList<ProductDetails> list, ArrayList<ProductDetails> featured) {
        ProductPage fragment = new ProductPage();
        fragment.featuredList = featured;
        fragment.realtyList = list;
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
        View view = inflater.inflate(R.layout.fragment_seller_product_page, container, false);
        settingsMain = new SettingsMain(getActivity());
        restService = UrlController.createService(RestService.class);

        scrollView = view.findViewById(R.id.scrollView);

        MyRecyclerView = view.findViewById(R.id.recycler_view);
        MyRecyclerView.setHasFixedSize(true);
        MyRecyclerView.setNestedScrollingEnabled(false);
        MyRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        recyclerViewFeatured = view.findViewById(R.id.recycler_featured);
        recyclerViewFeatured.setHasFixedSize(true);
        MyLayoutManager2 = new GridLayoutManager(getActivity(), 1);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerViewFeatured.setLayoutManager(MyLayoutManager2);

        myProductAdapter = new MyProductAdapter(getActivity(), realtyList);
        itemSearchFeatureAdsAdapter = new ItemSearchFeatureAdsAdapter(getActivity(), featuredList);

        recyclerViewFeatured.setAdapter(itemSearchFeatureAdsAdapter);
        MyRecyclerView.setAdapter(myProductAdapter);

        LinearLayout emptyLayout = view.findViewById(R.id.empty_view);

        if(realtyList.size() == 0 && featuredList.size() == 0) {

            emptyLayout.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }
        else {
            scrollView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }

        ll_featured = view.findViewById(R.id.ll_featured);
        if(featuredList.size() > 0){
            ll_featured.setVisibility(View.VISIBLE);
        }
        else
            ll_featured.setVisibility(View.GONE);

        itemSearchFeatureAdsAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(ProductDetails item) {
                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                intent.putExtra("adId", ""+item.getId());
                intent.putExtra("detail_type", "post_edit");
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }

            @Override
            public void onRemoveFav(int position) {

            }

            @Override
            public void onShare(ProductDetails item) {

            }

            @Override
            public void onAddCart(ProductDetails item) {

            }

        });

        myProductAdapter.setOnItemClickListener(new MyProductOnclicklinstener() {
            @Override
            public void onItemClick(ProductDetails item) {
                Log.d("item_id", item.getId()+"");
                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
                intent.putExtra("adId", ""+item.getId());
                intent.putExtra("detail_type", "post_edit");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_enter, R.anim.left_out);
            }

            @Override
            public void delViewOnClick(View v, int position) {

            }
        });

        return view;
    }
}
