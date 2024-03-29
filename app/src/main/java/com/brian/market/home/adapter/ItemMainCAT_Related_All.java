package com.brian.market.home.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.brian.market.R;
import com.brian.market.ad_detail.Ad_detail_activity;
import com.brian.market.adapters.ProductAdapter;
import com.brian.market.helper.MyProductOnclicklinstener;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.models.ProductDetails;
import com.brian.market.models.homeCatRelatedList;
import com.brian.market.utills.CustomBorderDrawable;
import com.brian.market.utills.SettingsMain;

public class ItemMainCAT_Related_All extends RecyclerView.Adapter<ItemMainCAT_Related_All.MyViewHolder> {
    private ArrayList<homeCatRelatedList> list;
    private MyProductOnclicklinstener onItemClickListener;
    private Context mContext;
    private SettingsMain settingsMain;

    public ItemMainCAT_Related_All(Context context, ArrayList<homeCatRelatedList> Data) {
        this.list = Data;
        this.mContext = context;
        settingsMain = new SettingsMain(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_home_related_all, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final homeCatRelatedList feedItem = list.get(position);

        holder.btnViewAll.setTag(feedItem.getCatId());
        holder.btnViewAll.setText(feedItem.getViewAllBtnText());
        holder.textViewTitle.setText(feedItem.getTitle());

        holder.btnViewAll.setBackground(CustomBorderDrawable.customButton(0, 0, 0, 0, settingsMain.getMainColor(), settingsMain.getMainColor(), settingsMain.getMainColor(), 3));

        holder.recyclerView.setHasFixedSize(true);
        GridLayoutManager MyLayoutManager2 = new GridLayoutManager(mContext, 1);
        MyLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(MyLayoutManager2);
        holder.recyclerView.setNestedScrollingEnabled(false);

        ProductAdapter productAdapter = new ProductAdapter(mContext, feedItem.getArrayList());
        holder.recyclerView.setAdapter(productAdapter);

        productAdapter.setOnItemClickListener(new OnItemClickListener2() {
            @Override
            public void onItemClick(ProductDetails item) {

                Intent intent = new Intent(mContext, Ad_detail_activity.class);
                intent.putExtra("adId", ""+item.getId());
                mContext.startActivity(intent);
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

//        View.OnClickListener listener2 = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onItemClickListener.delViewOnClick(v, position);
//            }
//        };
//
//        holder.btnViewAll.setOnClickListener(listener2);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(MyProductOnclicklinstener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, btnViewAll;
        RecyclerView recyclerView;

        MyViewHolder(View v) {
            super(v);

            textViewTitle = v.findViewById(R.id.title);
            btnViewAll = v.findViewById(R.id.btnViewAll);
            recyclerView = v.findViewById(R.id.sublist);
            recyclerView.setNestedScrollingEnabled(false);
            ViewCompat.setNestedScrollingEnabled(recyclerView, false);

        }
    }
}
