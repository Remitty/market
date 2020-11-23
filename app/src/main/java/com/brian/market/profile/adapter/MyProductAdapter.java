package com.brian.market.profile.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brian.market.databases.User_Favorites_DB;
import com.brian.market.databases.User_Recents_DB;
import com.brian.market.helper.MyProductOnclicklinstener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.brian.market.R;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.utills.SettingsMain;


public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {

    SettingsMain settingsMain;
    Context context;
    private ArrayList<ProductDetails> list;
    private MyProductOnclicklinstener onItemClickListener;

    private User_Recents_DB recents_db;
    private User_Favorites_DB favorites_db;

    public MyProductAdapter(Context context, ArrayList<ProductDetails> Data) {
        this.list = Data;
        settingsMain = new SettingsMain(context);
        this.context = context;

        recents_db = new User_Recents_DB();
        favorites_db = new User_Favorites_DB();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ProductDetails feedItem = list.get(position);

        holder.titleTextView.setText(feedItem.getCardName());
        holder.dateTV.setText(feedItem.getDate());
        holder.priceTV.setText(feedItem.getPrice() + " " + feedItem.getCurrency());

        holder.locationTV.setText(feedItem.getLocation());
        holder.addressTV.setText(feedItem.getAddress());
        holder.qtyText.setText(feedItem.getQty() + " Remaining");

        if(feedItem.isShipping()){
            holder.shippingLayout.setVisibility(View.VISIBLE);
            holder.shipPriceTV.setText("$ "+feedItem.getShipPrice());
        }
        else holder.shippingLayout.setVisibility(View.GONE);

        if (feedItem.getFeaturetype()) {
            holder.featureText.setVisibility(View.VISIBLE);
//            holder.featureText.setText(feedItem.getAddTypeFeature());
            holder.featureText.setBackgroundColor(Color.parseColor("#E52D27"));
        }

        if (!TextUtils.isEmpty(feedItem.getImageResourceId())) {
            Picasso.with(context).load(feedItem.getImageResourceId())
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


        holder.linearLayout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(MyProductOnclicklinstener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTV, priceTV, shipPriceTV, locationTV, addressTV, featureText, qtyText;
        ImageView mainImage;
        RelativeLayout linearLayout;
        LinearLayout shippingLayout;

        MyViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.text_view_name);
            dateTV = v.findViewById(R.id.date);
            priceTV = v.findViewById(R.id.prices);
            shipPriceTV = v.findViewById(R.id.shipping_price);
            locationTV = v.findViewById(R.id.location);
            addressTV = v.findViewById(R.id.address);
            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));
            qtyText = v.findViewById(R.id.qty);

            mainImage = v.findViewById(R.id.image_view);

            linearLayout = v.findViewById(R.id.linear_layout_card_view);
            shippingLayout = v.findViewById(R.id.shipping_layout);
            featureText = v.findViewById(R.id.textView4);
        }
    }
}
