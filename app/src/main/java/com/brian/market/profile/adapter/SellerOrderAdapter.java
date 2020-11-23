package com.brian.market.profile.adapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.brian.market.modelsList.OrderDetail;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.brian.market.R;
import com.brian.market.modelsList.OrderProductDetail;
import com.brian.market.utills.SettingsMain;

import org.json.JSONArray;

import static android.graphics.Color.BLACK;

public class SellerOrderAdapter extends RecyclerView.Adapter<SellerOrderAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<OrderDetail> orderList;
    private List<OrderProductDetail> orderProductList;
    private Context mContext;
    private Listener ItemClickListener;
    SellerOrderProductAdapter mAdapter;

    public SellerOrderAdapter(Context context, List<OrderDetail> orderList) {
        this.orderList = orderList;
        this.mContext = context;
        settingsMain = new SettingsMain(context);
    }

    @Override
    public SellerOrderAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_seller_order, viewGroup, false);
        return new SellerOrderAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SellerOrderAdapter.CustomViewHolder customViewHolder, int i) {
        final OrderDetail feedItem = orderList.get(i);
        orderProductList = feedItem.getOrderProducts();
        mAdapter = new SellerOrderProductAdapter(mContext, orderProductList);
        customViewHolder.tvOrderId.setText(feedItem.getOrderId());
        customViewHolder.tvDate.setText(feedItem.getDate().substring(0, 10));
        customViewHolder.tvStatus.setText(feedItem.getStatus());
        if(feedItem.getShipping() == 1)
            customViewHolder.tvShipping.setText("Get shipping detail");
        else {
            customViewHolder.tvShipping.setText("Local");
            customViewHolder.tvShipping.setTextColor(BLACK);
        }
        customViewHolder.tvPrice.setText(new DecimalFormat("$#0.00").format(feedItem.getPrice()));
        customViewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        customViewHolder.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        customViewHolder.tvShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedItem.getShipping() == 1)
                    ItemClickListener.onShippingClick(feedItem);
            }
        });

//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ItemClickListener.onItemClick(i);
//            }
//        };

//        customViewHolder.itemView.setOnClickListener(listener);

    }
    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    @Override
    public int getItemCount() {
        return (null != orderList ? orderList.size() : 0);
    }

    public Listener getOnItemClickListener() {
        return ItemClickListener;
    }

    public void setOnItemClickListener(Listener onItemClickListener) {
        this.ItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvPrice, tvStatus, tvDate, tvShipping;
        RecyclerView recyclerView;

        CustomViewHolder(View view) {
            super(view);

            this.tvOrderId = view.findViewById(R.id.tv_order_id);
            this.tvPrice = view.findViewById(R.id.tv_total_price);
            this.tvStatus = view.findViewById(R.id.tv_order_status);
            this.tvDate = view.findViewById(R.id.tv_order_date);
            this.tvShipping = view.findViewById(R.id.tv_order_shipping);
            this.recyclerView = view.findViewById(R.id.order_product_recycler_view);

        }
    }

    public interface Listener {
        /**
         * @param position
         */
        void onItemClick(int position);
        void onShippingClick(OrderDetail order);
    }
}
