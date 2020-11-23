package com.brian.market.helper;

import android.view.View;

import com.brian.market.modelsList.ProductDetails;

public interface CatSubCatOnclicklinstener {
    void onItemClick(ProductDetails item);

    void onItemTouch(ProductDetails item);

    void addToFavClick(View v, String position);

}
