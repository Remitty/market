package com.brian.market.order.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brian.market.models.OrderDetail;

import java.text.DecimalFormat;
import java.util.List;

import com.brian.market.R;
import com.brian.market.utills.SettingsMain;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.CustomViewHolder> {

    SettingsMain settingsMain;
    private List<OrderDetail> orderList;
    private Context mContext;
    private Listener ItemClickListener;
    private boolean show = false;

    public MyOrderAdapter(Context context, List<OrderDetail> orderList) {
        this.orderList = orderList;
        this.mContext = context;
        settingsMain = new SettingsMain(context);

    }

    public MyOrderAdapter(Context context, List<OrderDetail> orderList, boolean flag) {
        this.orderList = orderList;
        this.mContext = context;
        this.show = flag;
        settingsMain = new SettingsMain(context);

    }

    @Override
    public MyOrderAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_my_order, viewGroup, false);
        return new MyOrderAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOrderAdapter.CustomViewHolder customViewHolder, int i) {
        final OrderDetail feedItem = orderList.get(i);

        customViewHolder.tvOrderId.setText(feedItem.getOrderId());
        customViewHolder.tvDate.setText(feedItem.getDate().substring(0, 10));
        customViewHolder.tvKindProducts.setText(""+feedItem.getProductKinds());
        customViewHolder.tvNoProducts.setText(""+feedItem.getProductCounts());
        customViewHolder.tvStatus.setText(feedItem.getStatus());
        customViewHolder.tvPrice.setText(new DecimalFormat("$#0.00").format(feedItem.getPrice()));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.onItemClick(i);
            }
        };

        customViewHolder.btnBookConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.onItemConfirm(i);
            }
        });

        customViewHolder.btnBookEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.onItemEdit(i);
            }
        });
        customViewHolder.btnBookCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.onItemCancel(i);
            }
        });

        customViewHolder.itemView.setOnClickListener(listener);

        if(show) {
            customViewHolder.btnGroupLayout.setVisibility(View.VISIBLE);
        } else {
            customViewHolder.btnGroupLayout.setVisibility(View.GONE);
        }

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
        ImageView imageView;
        TextView tvOrderId, tvNoProducts, tvKindProducts, tvPrice, tvStatus, tvDate;
        Button btnBookEdit, btnBookCancel, btnBookConfirm;
        LinearLayout btnGroupLayout;

        CustomViewHolder(View view) {
            super(view);

            this.tvNoProducts = view.findViewById(R.id.tv_no_products);
            this.tvKindProducts = view.findViewById(R.id.tv_product_kind);
            this.tvOrderId = view.findViewById(R.id.tv_order_id);
            this.tvPrice = view.findViewById(R.id.tv_total_price);
            this.tvStatus = view.findViewById(R.id.tv_order_status);
            this.tvDate = view.findViewById(R.id.tv_order_date);
            this.btnBookCancel = view.findViewById(R.id.btn_book_cancel);
            this.btnBookEdit = view.findViewById(R.id.btn_book_edit);
            this.btnBookConfirm = view.findViewById(R.id.btn_book_confirm);
            this.btnGroupLayout = view.findViewById(R.id.btn_group_layout);
        }
    }

    public interface Listener {
        /**
         * @param position
         */
        void onItemEdit(int position);
        void onItemCancel(int position);
        void onItemClick(int position);
        void onItemConfirm(int position);
    }
}
