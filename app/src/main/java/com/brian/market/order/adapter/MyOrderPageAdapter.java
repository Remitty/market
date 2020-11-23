package com.brian.market.order.adapter;

import com.brian.market.modelsList.OrderDetail;
import com.brian.market.order.OrderHistory;
import com.brian.market.order.MyOrder;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyOrderPageAdapter extends FragmentPagerAdapter {

    private List<OrderDetail> orderList = new ArrayList<>();
    private List<OrderDetail> orderHistoryList = new ArrayList<>();

    public MyOrderPageAdapter(FragmentManager fm, List<OrderDetail> order, List<OrderDetail> history) {
        super(fm);
        this.orderList = order;
        this.orderHistoryList = history;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MyOrder.newInstance(this.orderList);
            case 1:
                return OrderHistory.newInstance(this.orderHistoryList);
            default:

                return null;
        }
    }


    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Orders";
            case 1:
                return "Order History";
        }
        return null;
    }
}
