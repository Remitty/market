package com.brian.market.helper;

import android.view.View;

import com.brian.market.models.ProductDetails;

public interface MyProductOnclicklinstener {

    void onItemClick(ProductDetails item);
    void delViewOnClick(View v, int position);
}
