package com.brian.market.order.adapter;

import android.content.Context;

import com.brian.market.R;
import com.brian.market.models.OrderDetail;
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
    private Context context;
    public MyOrderPageAdapter(Context baseContext, FragmentManager fm, List<OrderDetail> order, List<OrderDetail> history) {
        super(fm);
        this.orderList = order;
        this.orderHistoryList = history;
        this.context = baseContext;
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
                return context.getString(R.string.orders);
            case 1:
                return context.getString(R.string.order_history);
        }
        return null;
    }
}
