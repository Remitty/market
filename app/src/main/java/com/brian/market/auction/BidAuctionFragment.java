package com.brian.market.auction;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brian.market.R;
import com.brian.market.auction.adapter.MyAuctionAdapter;
import com.brian.market.helper.OnAuctionItemClickListener;
import com.brian.market.modelsList.Auction;

import java.util.ArrayList;

public class BidAuctionFragment extends Fragment {

    private ArrayList<Auction> auctionList = new ArrayList<>();
    RecyclerView recyclerView;

    MyAuctionAdapter myAuctionAdapter;

    LinearLayout emptyLayout;

    public BidAuctionFragment(ArrayList<Auction> list) {
        // Required empty public constructor
        this.auctionList = list;
    }

    public static BidAuctionFragment newInstance(ArrayList<Auction> list) {
        BidAuctionFragment fragment = new BidAuctionFragment(list);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bid_auction, container, false);

        myAuctionAdapter = new MyAuctionAdapter(getActivity(), auctionList, 0);
        myAuctionAdapter.setOnItemClickListener(new OnAuctionItemClickListener() {
            @Override
            public void onItemClick(Auction item) {
                Intent intent = new Intent(getActivity(), AuctionDetailActivity.class);
                intent.putExtra("post_id", item.getId());
                startActivity(intent);
            }

            @Override
            public void onViewDetailClick(String id) {

            }

            @Override
            public void onConfirm(Auction item) {

            }
        });
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(myAuctionAdapter);

        emptyLayout = view.findViewById(R.id.empty);
        if(auctionList.size() == 0)
            emptyLayout.setVisibility(View.VISIBLE);

        return  view;
    }
}
