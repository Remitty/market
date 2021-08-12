package com.brian.market.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brian.market.R;
import com.brian.market.models.OrderDetail;
import com.brian.market.order.adapter.MyOrderAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class OrderHistory extends Fragment {
    RecyclerView recyclerHistory;
    MyOrderAdapter mAdapter;
    private List<OrderDetail> orderList = new ArrayList<>();

    public OrderHistory() {
        // Required empty public constructor
    }

    public static OrderHistory newInstance(List<OrderDetail> history) {
        OrderHistory fragment = new OrderHistory();
        fragment.orderList = history;
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
        View view = inflater.inflate(R.layout.fragment_my_order_history, container, false);


        recyclerHistory = view.findViewById(R.id.recycler_history);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        LinearLayout emptyLayout = view.findViewById(R.id.empty_view);

        if(orderList.size() > 0) {
            recyclerHistory.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
        else {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerHistory.setVisibility(View.GONE);
        }

        mAdapter = new MyOrderAdapter(getActivity(), orderList);
        recyclerHistory.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyOrderAdapter.Listener(){
            @Override
            public void onItemEdit(int position) {
            }

            @Override
            public void onItemCancel(int position) {
            }

            @Override
            public void onItemClick(int position) {
                OrderDetail item = orderList.get(position);
//                Intent intent = new Intent(getActivity(), Ad_detail_activity.class);
//                intent.putExtra("adId", item.getId());
//                startActivity(intent);
            }

            @Override
            public void onItemConfirm(int position) {

            }

        });
        mAdapter.notifyDataSetChanged();
        return view;
    }
}
