package com.brian.market.adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.brian.market.cart.CartFragment;
import com.brian.market.databases.User_Cart_DB;
import com.brian.market.databases.User_Favorites_DB;
import com.brian.market.databases.User_Recents_DB;
import com.brian.market.helper.OnItemClickListener2;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;
import com.brian.market.R;
import com.brian.market.helper.CatSubCatOnclicklinstener;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.utills.SettingsMain;

public class ItemSearchFeatureAdsAdapter extends RecyclerView.Adapter<ItemSearchFeatureAdsAdapter.CustomViewHolder> {

    private SettingsMain settingsMain;
    private ArrayList<ProductDetails> list;
    private OnItemClickListener2 onItemClickListener;
    private Context mContext;

    private User_Recents_DB recents_db;
    private User_Favorites_DB favorites_db;

    public ItemSearchFeatureAdsAdapter(Context context, ArrayList<ProductDetails> feedItemList) {
        this.list = feedItemList;
        this.mContext = context;
        recents_db = new User_Recents_DB();
        favorites_db = new User_Favorites_DB();

    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.itemof_feature_search, null);
        settingsMain = new SettingsMain(mContext);
        return new CustomViewHolder(view);
    }
//    @Override
////    public int getItemViewType(int position) {
////        return position;
////    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, final int position) {

        final ProductDetails feedItem = list.get(position);

        holder.titleTextView.setText(feedItem.getCardName());
//        if(feedItem.isShipping())
//        holder.priceTV.setText("$ "+feedItem.getPrice());
//        else
        holder.priceTV.setText(feedItem.getPrice() + " " + feedItem.getCurrency());
        holder.locationTV.setText(feedItem.getLocation());
        holder.addressTV.setText(feedItem.getAddress());
        holder.dayAgo.setText(feedItem.getDayAgo());
//        holder.featureText.setText(list.get(position).getAddTypeFeature());
//        holder.featureText.setBackgroundColor(Color.parseColor("#E52D27"));
//        setScaleAnimation(holder.itemView);


        if (!TextUtils.isEmpty(feedItem.getImageResourceId())) {
            Picasso.with(mContext).load(feedItem.getImageResourceId())
                    .resize(250, 250).centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.mainImage);
        }


        if(feedItem.isShipping()){
            holder.shipPriceTV.setText("$ " + feedItem.getShipPrice());
            holder.shipping_layout.setVisibility(View.VISIBLE);
            if (CartFragment.checkCartHasProduct(feedItem.getId())) {
                Picasso.with(mContext).load(R.drawable.ic_cart_full)
                        .error(R.drawable.ic_cart_empty)
                        .placeholder(R.drawable.ic_cart_empty)
                        .into(holder.cartImage);
                holder.add_cart_layout.setVisibility(View.GONE);
            }
        }
        else {
            holder.add_cart_layout.setVisibility(View.GONE);
            holder.shipping_layout.setVisibility(View.GONE);
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
                    Picasso.with(mContext).load(R.drawable.ic_cart_full)
                            .error(R.drawable.ic_cart_empty)
                            .placeholder(R.drawable.ic_cart_empty)
                            .into(holder.cartImage);
                    v.setVisibility(View.GONE);
                    Toast.makeText(mContext, mContext.getString(R.string.message_added_product_into_cart), Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(mContext, mContext.getString(R.string.message_added_product_into_cart_already), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.linearLayoutMain.setOnClickListener(listener);
    }
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    public OnItemClickListener2 getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener2 onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        CountdownView cv_countdownView;
        private TextView titleTextView, priceTV, locationTV, addressTV, featureText, shipPriceTV, dayAgo;
        private RelativeLayout linearLayoutMain;
        LinearLayout shipping_layout, add_cart_layout;
        ImageView mainImage, shareImage, cartImage, soldImage;
        ToggleButton btnFav;

        CustomViewHolder(View v) {
            super(v);

            titleTextView = v.findViewById(R.id.text_view_name);
            featureText = v.findViewById(R.id.textView4);
            priceTV = v.findViewById(R.id.prices);
            shipPriceTV = v.findViewById(R.id.shipping_price);
            locationTV = v.findViewById(R.id.location);
            addressTV = v.findViewById(R.id.address);
            dayAgo = v.findViewById(R.id.day_ago);

            mainImage = v.findViewById(R.id.image_view);
            soldImage = v.findViewById(R.id.image_sold);
            btnFav = v.findViewById(R.id.add_fav);
            shareImage = v.findViewById(R.id.share);
            cartImage = v.findViewById(R.id.add_cart);

            cv_countdownView = v.findViewById(R.id.cv_countdownView);

//            priceTV.setTextColor(Color.parseColor(settingsMain.getMainColor()));
            add_cart_layout = v.findViewById(R.id.ll_add_cart);
            linearLayoutMain = v.findViewById(R.id.linear_layout_card_view);
            shipping_layout = v.findViewById(R.id.shipping_layout);
        }
    }
}