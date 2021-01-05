package com.brian.market.payment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.brian.market.modelsList.CreditCard;
import com.brian.market.payment.CardAddFragment;
import com.brian.market.payment.CardFragment;

public class CardPagerAdapter extends FragmentPagerAdapter {
    private CreditCard mCard;
    private ViewPager mCardPager;

    public CardPagerAdapter(@NonNull FragmentManager fm, CreditCard card, ViewPager cardPager) {
        super(fm);
        mCard = card;
        mCardPager = cardPager;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CardFragment(mCard, mCardPager);
            case 1:
                return new CardAddFragment();
            default:

                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
