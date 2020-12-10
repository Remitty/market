package com.brian.market.auction.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brian.market.helper.OnAuctionItemClickListener;
import com.brian.market.modelsList.Auction;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;
import com.brian.market.R;
import com.brian.market.utills.SettingsMain;


public class AuctionAdapter extends RecyclerView.Adapter<AuctionAdapter.MyViewHolder> {

    SettingsMain settingsMain;
    Context context;
    private ArrayList<Auction> list;

    private OnAuctionItemClickListener onItemClickListener;

    public AuctionAdapter(Context context, ArrayList<Auction> Data, int type) {
        this.list = Data;
        settingsMain = new SettingsMain(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_auction_origin, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Auction feedItem = list.get(position);

        if (!TextUtils.isEmpty(feedItem.getImageResourceId(0))) {
            Picasso.with(context).load(feedItem.getImageResourceId(0))
                    .resize(250, 250).centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(feedItem);
            }
        };

        holder.itemView.setOnClickListener(listener);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnAuctionItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mainImage;

        MyViewHolder(View v) {
            super(v);

            mainImage = v.findViewById(R.id.auction_photo);

        }
    }
}
