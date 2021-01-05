package com.brian.market.payment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.brian.market.payment.PaypalAddFragment;
import com.brian.market.payment.PaypalFragment;

public class PaypalPagerAdapter extends FragmentPagerAdapter {
    private String mPaypal;
    ViewPager mPaypalPager;

    public PaypalPagerAdapter(@NonNull FragmentManager fm, String paypal, ViewPager paypalPager) {
        super(fm);
        mPaypal = paypal;
        mPaypalPager = paypalPager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PaypalFragment(mPaypal, mPaypalPager);
            case 1:
                return new PaypalAddFragment(mPaypal);
            default:

                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
