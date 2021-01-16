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


public class MyAuctionAdapter extends RecyclerView.Adapter<MyAuctionAdapter.MyViewHolder> {

    SettingsMain settingsMain;
    Context context;
    private ArrayList<Auction> list;
    private int auction_type = 0;

    private OnAuctionItemClickListener onItemClickListener;

    public MyAuctionAdapter(Context context, ArrayList<Auction> Data, int type) {
        this.list = Data;
        this.auction_type = type;
        settingsMain = new SettingsMain(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_auction, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Auction feedItem = list.get(position);

        holder.titleTextView.setText(feedItem.getTitle());
        holder.dateTV.setText(feedItem.getDayAgo());
        holder.catTV.setText(feedItem.getCategory());

        holder.startPriceTV.setText(feedItem.getStartPrice() +  feedItem.getCurrency());
        holder.betPriceTV.setText(feedItem.getHighPrice() +  feedItem.getCurrency());
        holder.statusTV.setText(feedItem.getStatus());
        holder.locationTV.setText(feedItem.getLocation());
        holder.bidderTV.setText(feedItem.getBidders()+" " + context.getString(R.string.bidders));
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

        holder.layout.setOnClickListener(listener);

        if(feedItem.getStatus().equals("COMPLETED")) {
            if(auction_type == 1) {
                holder.winnerLayout.setVisibility(View.VISIBLE);
                if(feedItem.isShipping())
                    holder.winnerViewDetail.setVisibility(View.VISIBLE);
            }
            if(auction_type == 2) {
                holder.winnerLayout.setVisibility(View.VISIBLE);
            }
            if(feedItem.getWinner().equals(settingsMain.getUserName()))
                holder.winnerName.setText(context.getString(R.string.you));
            else holder.winnerName.setText(feedItem.getWinner());

            holder.countDown.start(0);
        }
        else
            holder.countDown.start(Long.parseLong(feedItem.getDuration()) * 1000);

        if(feedItem.isShipping()) {
            holder.shippingLayout.setVisibility(View.VISIBLE);
            holder.shippingPrice.setText("$ " + feedItem.getShippingPrice());

            if(feedItem.getStatus().equals("FINISHED") && auction_type == 3) {
                holder.btnConfirm.setVisibility(View.VISIBLE);
            }
        }

        holder.winnerViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onViewDetailClick(feedItem.getWinnerID());
            }
        });

        holder.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onConfirm(feedItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnAuctionItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTV, startPriceTV, betPriceTV, statusTV, locationTV, bidderTV, winnerViewDetail, winnerName, shippingPrice, catTV;
        ImageView mainImage;
        LinearLayout layout, winnerLayout, shippingLayout;
        CountdownView countDown;
        Button btnConfirm;

        MyViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.auction_title);
            countDown = v.findViewById(R.id.countDown);
            dateTV = v.findViewById(R.id.auction_post_time);
            betPriceTV = v.findViewById(R.id.auction_bet_price);
            startPriceTV = v.findViewById(R.id.auction_start_price);
            statusTV = v.findViewById(R.id.auction_status);
            locationTV = v.findViewById(R.id.auction_location);
            bidderTV = v.findViewById(R.id.auction_bidders);
            catTV = v.findViewById(R.id.auction_cat);

            winnerName = v.findViewById(R.id.winner_name);
            winnerViewDetail = v.findViewById(R.id.winner_detail);
            winnerLayout = v.findViewById(R.id.winner_layout);

            mainImage = v.findViewById(R.id.auction_photo);

            layout = v.findViewById(R.id.layout_auction);

            shippingLayout = v.findViewById(R.id.shipping_layout);
            shippingPrice = v.findViewById(R.id.auction_shipping_price);

            btnConfirm = v.findViewById(R.id.btn_auction_confirm);
        }
    }
}
