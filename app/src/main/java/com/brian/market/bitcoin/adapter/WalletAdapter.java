package com.brian.market.bitcoin.adapter;


import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brian.market.R;
import com.brian.market.modelsList.CoinInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.OrderViewHolder> {
    private List<CoinInfo> arrItems;
    private Listener listener;
    private Context mContext;

    public WalletAdapter(List<CoinInfo> arrItems, boolean viewType) {
        this.arrItems = arrItems;
    }

    public WalletAdapter(Context context, List<CoinInfo> arrItems, boolean viewType) {
        this.mContext = context;
        this.arrItems = arrItems;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView tvCoinType;
        TextView tvCoinUsdc;
        ImageView coinIcon;
        Button btnDeposit;

        public OrderViewHolder(View view) {
            super(view);

            tvCoinType = view.findViewById(R.id.coin_type);
            tvCoinUsdc= view.findViewById(R.id.coin_usdc);
            coinIcon = view.findViewById(R.id.coin_icon);
            btnDeposit= view.findViewById(R.id.btn_wallet_deposit);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_item_layout, parent, false);
        OrderViewHolder vh = new OrderViewHolder(mView);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        CoinInfo item = arrItems.get(position);

        holder.tvCoinType.setText(item.getCoinType());
//        if(item.getCoinWallet() != null && item.getCoinWallet().length() > 0) {
        holder.tvCoinUsdc.setVisibility(View.VISIBLE);
        holder.tvCoinUsdc.setText("$ " + item.getCoinRate());

        Picasso.with(mContext).load(item.getCoinIcon())
                .placeholder(R.drawable.coin_bitcoin)
                .error(R.drawable.coin_bitcoin)
                .into(holder.coinIcon);

//        if(item.getCoinSymbol().equalsIgnoreCase("USDC")){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Picasso.with(mContext).load(R.drawable.usdc)
//                        .placeholder(R.drawable.coin_bitcoin)
//                        .error(R.drawable.coin_bitcoin)
//                        .into(holder.coinIcon);
//            }
//        }

//        if(item.getCoinSymbol().equalsIgnoreCase("BCH")){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                Picasso.with(mContext).load(R.drawable.bch)
//                        .placeholder(R.drawable.coin_bitcoin)
//                        .error(R.drawable.coin_bitcoin)
//                        .into(holder.coinIcon);
//            }
//        }

//        }
//        switch (item.getCoinType()) {
//            case "bitcoin":
//                holder.coinIcon.setImageResource(R.drawable.coin_bitcoin);
//                break;
//            case "ethereum":
//                holder.coinIcon.setImageResource(R.drawable.coin_eth);
//                break;
//            case "ethereumclassic":
//                holder.coinIcon.setImageResource(R.drawable.coin_ethc);
//                break;
//            case "litecoin":
//                holder.coinIcon.setImageResource(R.drawable.coin_litecoin);
//                break;
//            case "bat":
//                holder.coinIcon.setImageResource(R.drawable.coin_bat);
//                break;
//            case "xrp":
//                holder.coinIcon.setImageResource(R.drawable.coin_xrp);
//                break;
//            case "eos":
//                holder.coinIcon.setImageResource(R.drawable.coin_eos);
//                break;
//            case "usdc":
//                holder.coinIcon.setImageResource(R.drawable.coin_usd);
//                break;
//        }
//        else holder.btnDeposit.setText("Create");

        holder.btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnDeposit(position);
            }
        });

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return arrItems != null ? arrItems.size(): 0;
    }

    public Object getItem(int i) {
        return arrItems.get(i);
    }

    public interface Listener {
        /**
         * @param position
         */
        void OnDeposit(int position);
    }
}
