package com.movieous.media.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.faceunity.entity.Filter;
import com.movieous.media.R;
import com.movieous.media.mvp.contract.FilterChangedListener;
import com.movieous.media.mvp.model.entity.BeautyFilterEnum;

import java.util.List;

public class BeautyFilterAdapter extends RecyclerView.Adapter<BeautyFilterAdapter.HomeRecyclerHolder> {
    private int mFilterPositionSelect = 0;
    private int mFilterTypeSelect = Filter.FILTER_TYPE_BEAUTY_FILTER;
    int filterType = Filter.FILTER_TYPE_BEAUTY_FILTER;

    private FilterChangedListener mFilterChangedListener;
    private List<Filter> mBeautyFilters;
    private List<Filter> mFilters;

    public BeautyFilterAdapter(FilterChangedListener listener) {
        mFilterChangedListener = listener;
        mBeautyFilters = BeautyFilterEnum.Companion.getFiltersByFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
        mFilters = BeautyFilterEnum.Companion.getFiltersByFilterType(Filter.FILTER_TYPE_FILTER);
    }

    @Override
    public BeautyFilterAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeautyFilterAdapter.HomeRecyclerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_view, parent, false));
    }

    @Override
    public void onBindViewHolder(BeautyFilterAdapter.HomeRecyclerHolder holder, final int position) {
        final List<Filter> filters = getItems(filterType);
        holder.filterImg.setImageResource(filters.get(position).resId());
        holder.filterName.setText(filters.get(position).description());
        if (mFilterPositionSelect == position && filterType == mFilterTypeSelect) {
            holder.filterImg.setBackgroundResource(R.drawable.control_filter_select);
        } else {
            holder.filterImg.setBackgroundResource(0);
        }
        holder.itemView.setOnClickListener(v -> {
            mFilterPositionSelect = position;
            mFilterTypeSelect = filterType;
            //setFilterProgress();
            notifyDataSetChanged();
            mFilterChangedListener.onBeautyFilterChanged(filters.get(mFilterPositionSelect));
        });
    }

    @Override
    public int getItemCount() {
        return getItems(filterType).size();
    }

    public void setFilterType(int filterType) {
        this.filterType = filterType;
        notifyDataSetChanged();
    }

    public List<Filter> getItems(int type) {
        switch (type) {
            case Filter.FILTER_TYPE_BEAUTY_FILTER:
                return mBeautyFilters;
            case Filter.FILTER_TYPE_FILTER:
                return mFilters;
        }
        return mFilters;
    }

    class HomeRecyclerHolder extends RecyclerView.ViewHolder {

        ImageView filterImg;
        TextView filterName;

        public HomeRecyclerHolder(View itemView) {
            super(itemView);
            filterImg = itemView.findViewById(R.id.icon);
            filterName = itemView.findViewById(R.id.name);
            filterName.setVisibility(View.VISIBLE);
        }
    }
}
