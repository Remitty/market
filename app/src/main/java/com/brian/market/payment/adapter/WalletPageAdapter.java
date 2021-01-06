package com.brian.market.payment.adapter;

import com.brian.market.auction.BidAuctionFragment;
import com.brian.market.auction.PostAuctionFragment;
import com.brian.market.auction.WinAuctionFragment;
import com.brian.market.modelsList.Auction;
import com.brian.market.modelsList.WithdrawTransaction;
import com.brian.market.payment.WalletFragment;
import com.brian.market.payment.WithdrawHistoryFragment;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class WalletPageAdapter extends FragmentPagerAdapter {
    private ArrayList<WithdrawTransaction> history = new ArrayList<>();
    private String wallet, paypal;
    public WalletPageAdapter(FragmentManager fm, String wallet, String paypal, ArrayList<WithdrawTransaction> list) {
        super(fm);
        this.history = list;
        this.wallet = wallet;
        this.paypal = paypal;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return WalletFragment.newInstance(wallet, paypal);
            case 1:
                return WithdrawHistoryFragment.newInstance(history);
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
                return "Wallet";
            case 1:
                return "Withdraw History";
        }
        return null;
    }
}
