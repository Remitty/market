package com.brian.market.doba.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.market.R;
import com.brian.market.doba.models.DobaCategory;

import java.util.ArrayList;

public class DobaCatAdapter extends RecyclerView.Adapter<DobaCatAdapter.CatViewHolder>{

    ArrayList<DobaCategory> catList;
    Context mContext;
    Listener ItemClickListener;
    public DobaCatAdapter(Context context, ArrayList<DobaCategory> cats) {
        mContext = context;
        catList = cats;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doba_cat, parent, false);
        return new DobaCatAdapter.CatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        DobaCategory cat = catList.get(position);
        holder.tvCatName.setText(cat.getCatName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public void setOnItemClickListener(Listener onItemClickListener) {
        this.ItemClickListener = onItemClickListener;
    }

    public interface Listener {
        /**
         * @param position
         */
        void onItemClick(int position);
    }

    class CatViewHolder extends RecyclerView.ViewHolder {
        TextView tvCatName;
        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCatName = itemView.findViewById(R.id.tv_cat_name);
        }
    }
}
