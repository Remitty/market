package com.brian.market.profile.adapter;

import com.brian.market.modelsList.OrderDetail;
import com.brian.market.modelsList.ProductDetails;
import com.brian.market.profile.ProductOrderHistoryPage;
import com.brian.market.profile.ProductOrderPage;
import com.brian.market.profile.ProductPage;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyProductPageAdapter extends FragmentPagerAdapter {

    private ArrayList<ProductDetails> myProductList = new ArrayList<>();
    private ArrayList<ProductDetails> myFeaturedProductList = new ArrayList<>();
    private ArrayList<OrderDetail> orderList = new ArrayList<>();
    private ArrayList<OrderDetail> OrderHistoryList = new ArrayList<>();

    public MyProductPageAdapter(FragmentManager fm, ArrayList<ProductDetails> list, ArrayList<ProductDetails> featuredlist, ArrayList<OrderDetail> orders, ArrayList<OrderDetail> orderhistory) {
        super(fm);
        this.myProductList = list;
        this.myFeaturedProductList = featuredlist;
        this.orderList = orders;
        this.OrderHistoryList = orderhistory;
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
                return "Products";
            case 1:
                return "Orders";
            case 2:
                return "Order History";
        }
        return null;
    }
}
