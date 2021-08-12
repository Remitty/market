package com.brian.market.profile.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.brian.market.models.OrderProductDetail;
import java.util.List;

import com.brian.market.R;
import com.brian.market.utills.SettingsMain;

public class SellerOrderProductAdapter extends RecyclerView.Adapter<SellerOrderProductAdapter.MyViewHolder> {

    SettingsMain settingsMain;
    Context context;
    private List<OrderProductDetail> list;
//    private MyProductOnclicklinstener onItemClickListener;

    public SellerOrderProductAdapter(Context context, List<OrderProductDetail> data) {
        this.list = data;
        settingsMain = new SettingsMain(context);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_seller_order_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderProductDetail feedItem = list.get(position);

        holder.titleTextView.setText(feedItem.getOrderProduct().getCardName());
        holder.qtyTV.setText(feedItem.getOrderProductQty());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onItemClickListener.onItemClick(feedItem);
            }
        };
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    public void setOnItemClickListener(MyProductOnclicklinstener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, qtyTV;

        MyViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.product_title);
            qtyTV = v.findViewById(R.id.order_qty_right);
        }
    }
}
