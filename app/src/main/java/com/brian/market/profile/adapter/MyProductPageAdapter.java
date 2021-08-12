package com.brian.market.profile.adapter;

import android.content.Context;

import com.brian.market.R;
import com.brian.market.models.OrderDetail;
import com.brian.market.models.ProductDetails;
import com.brian.market.profile.ProductOrderHistoryPage;
import com.brian.market.profile.ProductOrderPage;
import com.brian.market.profile.ProductPage;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyProductPageAdapter extends FragmentPagerAdapter {

    private ArrayList<ProductDetails> myProductList = new ArrayList<>();
    private ArrayList<ProductDetails> myFeaturedProductList = new ArrayList<>();
    private ArrayList<OrderDetail> orderList = new ArrayList<>();
    private ArrayList<OrderDetail> OrderHistoryList = new ArrayList<>();
    private Context context;
    public MyProductPageAdapter(Context baseContext, FragmentManager fm, ArrayList<ProductDetails> list, ArrayList<ProductDetails> featuredlist, ArrayList<OrderDetail> orders, ArrayList<OrderDetail> orderhistory) {
        super(fm);
        this.myProductList = list;
        this.myFeaturedProductList = featuredlist;
        this.orderList = orders;
        this.OrderHistoryList = orderhistory;
        this.context = baseContext;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ProductPage.newInstance(this.myProductList, this.myFeaturedProductList);
            case 1:
                return ProductOrderPage.newInstance(this.orderList);
            case 2:
                return ProductOrderHistoryPage.newInstance(this.OrderHistoryList);
            default:

                return null;
        }
    }


    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.products);
            case 1:
                return context.getString(R.string.orders);
            case 2:
                return context.getString(R.string.order_history);
        }
        return null;
    }
}
