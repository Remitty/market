package com.brian.market.doba.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brian.market.R;
import com.brian.market.doba.models.DobaProduct;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DobaGoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    ArrayList<DobaProduct> goodList;

    Context mContext;
    Listener ItemClickListener;

    public DobaGoodAdapter(Context context, ArrayList<DobaProduct> data) {
        this.goodList = data;
        this.mContext = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_doba_good, parent, false);
            return new GoodViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof GoodViewHolder) {
            populateItemRows((GoodViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return goodList.size();
    }

    private void populateItemRows(GoodViewHolder holder, int position) {

        final DobaProduct item = goodList.get(position);
        String path ="";
        if(item.getPics().length() > 0)
            path = item.getPics().optJSONObject(0).optString("thumb");
        if(!path.isEmpty())
            Picasso.with(mContext).load(path).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemClickListener.onItemClick(position);
            }
        });
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return goodList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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

    private class GoodViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public GoodViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    private void showLoadingView(DobaGoodAdapter.LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    public void loadMore(ArrayList<DobaProduct> newList) {
        goodList.retainAll(newList);
        notifyItemRangeChanged(goodList.size()-1 , newList.size());
    }
}
