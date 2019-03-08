package com.movieous.media.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.movieous.media.R;
import com.movieous.media.mvp.model.entity.StickerFilterItem;

import java.io.IOException;
import java.io.InputStream;

/**
 * 滤镜列表适配器
 */
public class SdkFilterAdapter extends RecyclerView.Adapter<SdkFilterAdapter.FilterItemViewHolder> {
    private static final String TAG = "SdkFilterAdapter";

    private Context mContext;
    private StickerFilterItem[] mFilters;
    private FilterSelectedListener mFilterSelectedListener;

    public SdkFilterAdapter(StickerFilterItem[] filters) {
        this.mFilters = filters;
    }

    public interface FilterSelectedListener {
        void onFilterSelected(String path, boolean isAssetFile);
    }

    public void setFilterSelectedListener(FilterSelectedListener listener) {
        mFilterSelectedListener = listener;
    }

    @Override
    public FilterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View contactView = inflater.inflate(R.layout.item_filter_view, parent, false);
        FilterItemViewHolder viewHolder = new FilterItemViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FilterItemViewHolder holder, int position) {
        try {
            if (position == 0) {
                holder.mName.setText("None");
                Bitmap bitmap = BitmapFactory.decodeStream(mContext.getAssets().open("filters/none.png"));
                holder.mIcon.setImageBitmap(bitmap);
                holder.mIcon.setOnClickListener(v -> {
                    if (mFilterSelectedListener != null) {
                        mFilterSelectedListener.onFilterSelected(null, true);
                    }
                });
                return;
            }

            final StickerFilterItem filter = mFilters[position - 1];
            holder.mName.setText(filter.getName());
            InputStream is = mContext.getAssets().open(filter.getThumbPath());
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.mIcon.setImageBitmap(bitmap);
            holder.mIcon.setOnClickListener(v -> {
                if (mFilterSelectedListener != null) {
                    mFilterSelectedListener.onFilterSelected(filter.getFilterPath(), true);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mFilters != null ? mFilters.length + 1 : 0;
    }

    public class FilterItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mIcon;
        public TextView mName;

        public FilterItemViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.icon);
            mName = itemView.findViewById(R.id.name);
        }
    }
}
