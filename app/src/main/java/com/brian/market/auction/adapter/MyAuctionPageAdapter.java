package com.brian.market.auction.adapter;

import com.brian.market.auction.BidAuctionFragment;
import com.brian.market.auction.PostAuctionFragment;
import com.brian.market.auction.WinAuctionFragment;
import com.brian.market.modelsList.Auction;
import com.brian.market.order.MyOrder;
import com.brian.market.order.OrderHistory;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAuctionPageAdapter extends FragmentPagerAdapter {
    private ArrayList<Auction> postList = new ArrayList<>();
    private ArrayList<Auction> bidList = new ArrayList<>();
    private ArrayList<Auction> winList = new ArrayList<>();

    public MyAuctionPageAdapter(FragmentManager fm, ArrayList<Auction> post, ArrayList<Auction> bid, ArrayList<Auction> win) {
        super(fm);
        this.postList = post;
        this.bidList = bid;
        this.winList = win;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PostAuctionFragment.newInstance(this.postList);
            case 1:
                return BidAuctionFragment.newInstance(this.bidList);
            case 2:
                return WinAuctionFragment.newInstance(this.winList);
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
                return "Post";
            case 1:
                return "Bid";
            case 2:
                return "Win";
        }
        return null;
    }
}
