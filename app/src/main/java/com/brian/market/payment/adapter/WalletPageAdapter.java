package com.brian.market.payment.adapter;

import android.content.Context;

import com.brian.market.R;
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

import okhttp3.ResponseBody;
import retrofit2.Callback;

public class WalletPageAdapter extends FragmentPagerAdapter {
    private ArrayList<WithdrawTransaction> history = new ArrayList<>();
    private String wallet, paypal;
    private Context context;
    public WalletPageAdapter(Context context, FragmentManager fm, String wallet, String paypal, ArrayList<WithdrawTransaction> list) {
        super(fm);
        this.history = list;
        this.wallet = wallet;
        this.paypal = paypal;
        this.context = context;
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
                return this.context.getString(R.string.withdraw);
            case 1:
                return this.context.getString(R.string.withdraw_history);
        }
        return null;
    }
}
