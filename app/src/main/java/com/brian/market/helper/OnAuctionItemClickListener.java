package com.brian.market.helper;

import com.brian.market.modelsList.Auction;

public interface OnAuctionItemClickListener {
    void onItemClick(Auction item);
    void onViewDetailClick(String id);
    void onConfirm(Auction item);
}
