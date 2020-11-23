package com.brian.market.messages.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import com.brian.market.R;
import com.brian.market.helper.SendReciveONClickListner;
import com.brian.market.modelsList.messageSentRecivModel;

public class ItemSendRecMesageAdapter extends RecyclerView.Adapter<ItemSendRecMesageAdapter.CustomViewHolder> {
    private List<messageSentRecivModel> feedItemList;
    private Context mContext;
    private SendReciveONClickListner oNItemClickListener;


    public ItemSendRecMesageAdapter(Context context, List<messageSentRecivModel> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sent_message, null);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        final messageSentRecivModel feedItem = feedItemList.get(i);

        customViewHolder.name.setText(feedItemList.get(i).getName());
        customViewHolder.is_block.setText(feedItemList.get(i).getTopic());
        customViewHolder.topic.setText(feedItemList.get(i).getMessage());
        customViewHolder.chatTime.setText(feedItemList.get(i).getChatTime());
        if (!feedItem.isMessageRead()) {
            customViewHolder.card_view.setCardBackgroundColor(Color.parseColor("#fffcf5"));
            customViewHolder.notification_icon.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(feedItem.getTumbnail())) {
            Picasso.with(mContext).load(feedItem.getTumbnail())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(customViewHolder.imageView);
        }

        View.OnClickListener listener = v -> oNItemClickListener.onItemClick(feedItem);

        customViewHolder.linearLayout.setOnClickListener(listener);
        customViewHolder.delete_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oNItemClickListener.onItemDelete(feedItem, i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public SendReciveONClickListner getOnItemClickListener() {
        return oNItemClickListener;
    }

    public void setOnItemClickListener(SendReciveONClickListner onItemClickListener) {
        this.oNItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, topic,is_block, chatTime;
        LinearLayout linearLayout;
        CardView card_view;
        ImageView notification_icon, delete_icon;

        CustomViewHolder(View view) {
            super(view);
//            this.is_block=view.findViewById(R.id.Blocktext_view);
            this.linearLayout = view.findViewById(R.id.linear_layout_card_view);
            this.imageView = view.findViewById(R.id.image_view);
            this.name = view.findViewById(R.id.text_viewName);
            this.notification_icon = view.findViewById(R.id.notification_icon);
            this.delete_icon = view.findViewById(R.id.delete_icon);
            this.topic = view.findViewById(R.id.loginTime);
            this.card_view = view.findViewById(R.id.card_view);
            this.is_block = view.findViewById(R.id.Blocktext_view);
            this.chatTime = view.findViewById(R.id.chat_time);
        }
    }
}