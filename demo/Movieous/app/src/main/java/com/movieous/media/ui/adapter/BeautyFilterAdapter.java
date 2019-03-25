package com.movieous.media.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.movieous.media.R;
import com.movieous.media.api.vendor.fusdk.FuSDKManager;
import com.movieous.media.api.vendor.stsdk.StSDKManager;
import com.movieous.media.mvp.contract.FilterChangedListener;
import com.movieous.media.mvp.model.entity.FilterVendor;
import com.movieous.media.mvp.model.entity.MediaParam;
import com.movieous.media.mvp.model.entity.UFilter;
import com.movieous.media.utils.SharePrefUtils;

import java.util.List;

public class BeautyFilterAdapter extends RecyclerView.Adapter<BeautyFilterAdapter.HomeRecyclerHolder> {
    private int mFilterPositionSelect = 0;
    private int mFilterTypeSelect = UFilter.Companion.getFILTER_TYPE_BEAUTY_FILTER();
    private int filterType = UFilter.Companion.getFILTER_TYPE_FILTER();

    private FilterChangedListener mFilterChangedListener;
    private List<UFilter> mBeautyFilters;
    public FilterVendor mFilterVendor = FilterVendor.FACEUNITY;

    public BeautyFilterAdapter(Context context, FilterChangedListener listener) {
        mFilterChangedListener = listener;
        MediaParam param = SharePrefUtils.getParam(context);
        mFilterVendor = param.vendor;
        mBeautyFilters = mFilterVendor == FilterVendor.FACEUNITY ?
                FuSDKManager.getFilterList() :
                StSDKManager.getFilterList(context);
    }

    @Override
    public BeautyFilterAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeautyFilterAdapter.HomeRecyclerHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_view, parent, false));
    }

    @Override
    public void onBindViewHolder(BeautyFilterAdapter.HomeRecyclerHolder holder, final int position) {
        final List<UFilter> filters = getItems(filterType);
        UFilter filter = filters.get(position);
        if (mFilterVendor == FilterVendor.FACEUNITY) {
            holder.filterImg.setImageResource(filter.getResId());
            holder.filterName.setText(filter.getDescription());
        } else {
            holder.filterImg.setImageBitmap(filter.getIcon());
            holder.filterName.setText(filter.getName());
        }
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

    public List<UFilter> getItems(int type) {
        return mBeautyFilters;
    }

    public class HomeRecyclerHolder extends RecyclerView.ViewHolder {

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
