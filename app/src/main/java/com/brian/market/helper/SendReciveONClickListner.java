package com.brian.market.helper;

import com.brian.market.models.messageSentRecivModel;

public interface SendReciveONClickListner {
    void onItemClick(messageSentRecivModel item);
    void onItemDelete(messageSentRecivModel item, int position);
}
