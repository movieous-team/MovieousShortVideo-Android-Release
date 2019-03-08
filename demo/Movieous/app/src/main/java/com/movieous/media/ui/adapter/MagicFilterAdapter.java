package com.movieous.media.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.movieous.media.R;
import com.movieous.media.mvp.contract.OnItemClickListener;
import com.movieous.media.mvp.model.entity.MagicFilterItem;

import java.util.ArrayList;

public class MagicFilterAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener {
    private Context mContext;
    private ArrayList<MagicFilterItem> mFilterList;
    private OnItemClickListener mOnItemClickListener;
    private int mSelectedPosition = -1;

    public MagicFilterAdapter(ArrayList<MagicFilterItem> list, Context context) {
        mFilterList = list;
        mContext = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public boolean setSelectedPosition(int position) {
        boolean isSelected = position != mSelectedPosition;
        if (isSelected) {
            mSelectedPosition = position;
        } else {
            mSelectedPosition = -1;
        }
        return isSelected;
    }

    public void setPosition(int position) {
        mSelectedPosition = position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_view, null);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final FilterViewHolder holder = (FilterViewHolder) viewHolder;
        boolean isSelected = mSelectedPosition == position;
        MagicFilterItem item = mFilterList.get(position);
        ImageView icon = holder.imageView;
        View itemView = holder.itemView;
        switch (item.getVendor()) {
            case FU:
                icon.setImageResource(item.getResId());
                break;
            case ST:
                icon.setImageBitmap(item.getIcon());
                break;
        }
        itemView.setTag(position);
        itemView.setSelected(isSelected);
        icon.setBackgroundResource(isSelected ? R.drawable.control_filter_select : 0);
    }

    @Override
    public int getItemCount() {
        return mFilterList.size();
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, position);
        }
        notifyDataSetChanged();
    }

    @Override
    public boolean onLongClick(View view) {
        int position = (int) view.getTag();
        return (mOnItemClickListener != null) ? mOnItemClickListener.onItemLongClick(view, position) : false;
    }

    private static class FilterViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imageView;

        public FilterViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            imageView = itemView.findViewById(R.id.icon);
        }
    }

}
