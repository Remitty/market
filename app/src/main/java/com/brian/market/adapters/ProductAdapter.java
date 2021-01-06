package com.brian.market.adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brian.market.cart.CartFragment;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.databases.User_Favorites_DB;
import com.brian.market.databases.User_Recents_DB;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;
import com.brian.market.R;
import com.brian.market.helper.OnItemClickListener2;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.utills.SettingsMain;


public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    SettingsMain settingsMain;
    Context context;
    private ArrayList<ProductDetails> list;
    private OnItemClickListener2 onItemClickListener;

    private User_Recents_DB recents_db;
    private User_Favorites_DB favorites_db;
    private boolean flag_related = false;

    public ProductAdapter(Context context, ArrayList<ProductDetails> Data) {
        this.list = Data;
        settingsMain = new SettingsMain(context);
        this.context = context;

        recents_db = new User_Recents_DB();
        favorites_db = new User_Favorites_DB();
    }

    public ProductAdapter(Context context, ArrayList<ProductDetails> Data, boolean b) {
        this.list = Data;
        settingsMain = new SettingsMain(context);
        this.context = context;
        flag_related = b;

        recents_db = new User_Recents_DB();
        favorites_db = new User_Favorites_DB();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof MyViewHolder) {
            populateItemRows((MyViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener2 onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dateTV, priceTV, shipPriceTV, locationTV, addressTV, featureText, dayAgo;
        ImageView mainImage, shareImage, cartImage, soldImage;
        ToggleButton btnFav;
        RelativeLayout linearLayout;
        CountdownView cv_countdownView;
        LinearLayout shipping_layout, add_cart_layout;

        MyViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.text_view_name);
            dateTV = v.findViewById(R.id.date);
            priceTV = v.findViewById(R.id.prices);
            shipPriceTV = v.findViewById(R.id.shipping_price);
            locationTV = v.findViewById(R.id.location);
            addressTV = v.findViewById(R.id.address);
            dayAgo = v.findViewById(R.id.day_ago);
//            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));

            mainImage = v.findViewById(R.id.image_view);
            soldImage = v.findViewById(R.id.image_sold);
            btnFav = v.findViewById(R.id.add_fav);
            shareImage = v.findViewById(R.id.share);
            cartImage = v.findViewById(R.id.add_cart);
            cv_countdownView = v.findViewById(R.id.cv_countdownView);

            add_cart_layout = v.findViewById(R.id.ll_add_cart);

            linearLayout = v.findViewById(R.id.linear_layout_card_view);
            featureText = v.findViewById(R.id.textView4);
            shipping_layout = v.findViewById(R.id.shipping_layout);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    public void loadMore(ArrayList<ProductDetails> newList) {
        list.retainAll(newList);
        notifyItemRangeChanged(list.size()-1 , newList.size());
    }

    private void populateItemRows(MyViewHolder holder, int position) {

        final ProductDetails feedItem = list.get(position);

        holder.titleTextView.setText(feedItem.getCardName());
        holder.dateTV.setText(feedItem.getDate());
        holder.priceTV.setText(feedItem.getPrice() + " " + feedItem.getCurrency());
        holder.dayAgo.setText(feedItem.getDayAgo());
        if(feedItem.isShipping()) {
            holder.shipPriceTV.setText("$ " + feedItem.getShipPrice());
            holder.shipping_layout.setVisibility(View.VISIBLE);
        }
        else holder.shipping_layout.setVisibility(View.GONE);

        holder.locationTV.setText(feedItem.getLocation());
        holder.addressTV.setText(feedItem.getAddress());
        if (feedItem.getFeaturetype()) {
            holder.featureText.setVisibility(View.VISIBLE);
//            holder.featureText.setText(list.get(position).getAddTypeFeature());
            holder.featureText.setBackgroundColor(Color.parseColor("#E52D27"));
        }
        else{
            holder.cv_countdownView.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(feedItem.getImageResourceId())) {
            Picasso.with(context).load(feedItem.getImageResourceId())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }
        if(flag_related)
            holder.mainImage.setMaxHeight(200);

        if(feedItem.isShipping()){
            if (CartFragment.checkCartHasProduct(feedItem.getId())) {
                Picasso.with(context).load(R.drawable.ic_cart_full)
                        .error(R.drawable.ic_cart_empty)
                        .placeholder(R.drawable.ic_cart_empty)
                        .into(holder.cartImage);
                holder.add_cart_layout.setVisibility(View.GONE);
            }else{
//                holder.add_cart_layout.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.add_cart_layout.setVisibility(View.GONE);
        }

        if(feedItem.getQty() == 0) {
            holder.soldImage.setVisibility(View.VISIBLE);
            holder.add_cart_layout.setVisibility(View.GONE);
        }

        holder.btnFav.setOnCheckedChangeListener(null);

        if (favorites_db.getUserFavorites().contains(feedItem.getId())) {
            feedItem.setIsLiked("1");
            holder.btnFav.setChecked(true);
        }
        else {
            feedItem.setIsLiked("0");
            holder.btnFav.setChecked(false);
        }

        // Handle the Click event of product_like_btn ToggleButton
        holder.btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(holder.btnFav.isChecked()) {
                    feedItem.setIsLiked("1");
                    holder.btnFav.setChecked(true);

                    // Add the Product to User's Favorites
                    if (!favorites_db.getUserFavorites().contains(feedItem.getId())) {
                        favorites_db.insertFavoriteItem(feedItem.getId());
                    }
                }
                else {
                    feedItem.setIsLiked("0");
                    holder.btnFav.setChecked(false);

                    // Remove the Product from User's Favorites
                    if (favorites_db.getUserFavorites().contains(feedItem.getId())) {
                        favorites_db.deleteFavoriteItem(feedItem.getId());
                    }

                    onItemClickListener.onRemoveFav(position);
                }
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(feedItem);
            }
        };

        holder.shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onShare(feedItem);
            }
        });

        holder.add_cart_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CartFragment.checkCartHasProduct(feedItem.getId())) {
                    User_Cart_DB user_cart_db = new User_Cart_DB();
                    user_cart_db.addCartItem(feedItem);
                    feedItem.setCustomersBasketQuantity(1);
                    feedItem.setTotalPrice(feedItem.getPrice());
                    Picasso.with(context).load(R.drawable.ic_cart_full)
                            .error(R.drawable.ic_cart_empty)
                            .placeholder(R.drawable.ic_cart_empty)
                            .into(holder.cartImage);

                    v.setVisibility(View.GONE);

                    Toast.makeText(context, "Added the product to cart successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "Added the product to cart already", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.linearLayout.setOnClickListener(listener);

    }
}
