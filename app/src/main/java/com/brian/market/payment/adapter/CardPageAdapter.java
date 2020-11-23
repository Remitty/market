package com.brian.market.payment.adapter;

import com.brian.market.modelsList.CreditCard;
import com.brian.market.payment.AddCard;
import com.brian.market.payment.CardListPage;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CardPageAdapter extends FragmentPagerAdapter {
    List<CreditCard> cardList = new ArrayList<>();
    public CardPageAdapter(FragmentManager fm, List<CreditCard> cardList) {
        super(fm);
        this.cardList = cardList;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AddCard();
            case 1:
                return CardListPage.newInstance(this.cardList);
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
                return "Add Card";
            case 1:
                return "Card List";
        }
        return null;
    }
}
