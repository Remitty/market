package com.brian.market.helper;

import android.view.View;

import com.brian.market.modelsList.ProductDetails;
import com.brian.market.modelsList.myAdsModel;

public interface MyProductOnclicklinstener {

    void onItemClick(ProductDetails item);
    void delViewOnClick(View v, int position);
}
