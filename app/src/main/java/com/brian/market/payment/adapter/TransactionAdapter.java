package com.brian.market.payment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brian.market.R;
import com.brian.market.models.WithdrawTransaction;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.OrderViewHolder> {
    private List<WithdrawTransaction> arrItems;

    public TransactionAdapter(List<WithdrawTransaction> arrItems) {
        this.arrItems = arrItems;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvPrice, tvMethod, tvStatus, tvDate;

        public OrderViewHolder(View view) {
            super(view);

            tvPrice = view.findViewById(R.id.transaction_price);
            tvMethod = view.findViewById(R.id.transaction_method);
            tvStatus = view.findViewById(R.id.transaction_status);
            tvDate = view.findViewById(R.id.transaction_date);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        WithdrawTransaction item = arrItems.get(position);

        holder.tvDate.setText(item.getDate());
        holder.tvMethod.setText(item.getMethod());
        holder.tvStatus.setText(item.getStatus());
        holder.tvPrice.setText(item.getPrice());

    }


    @Override
    public int getItemCount() {
        return arrItems.size();
    }

    public Object getItem(int i) {
        return arrItems.get(i);
    }

}
