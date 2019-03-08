package com.movieous.media.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.movieous.media.R;
import iknow.android.utils.thread.UiThreadExecutor;
import video.movieous.shortvideo.UMediaUtil;

import java.util.ArrayList;
import java.util.List;

public class VideoThumbListView extends FrameLayout {
    private Context mContext;
    private RecyclerView mFrameList;
    private ObservableHorizontalScrollView mScrollView;

    private long mDurationMs;
    private int mFrameWidth;
    private int mFrameHeight;
    private int mFrameCount;

    private FrameListAdapter mFrameListAdapter;

    public VideoThumbListView(@NonNull Context context) {
        super(context);
    }

    public VideoThumbListView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.thumb_list_view, this);
        mFrameList = view.findViewById(R.id.recycler_frame_list);
        mScrollView = view.findViewById(R.id.scroll_view);
    }

    private void initFrameList() {
        mFrameList.setAdapter(mFrameListAdapter = new FrameListAdapter());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mFrameList.setLayoutManager(layoutManager);
        mScrollView.setOnScrollListener(new OnViewScrollListener());
    }

    public void setVideoPath(String path, long durationMs) {
        mDurationMs = durationMs > 0 ? durationMs : UMediaUtil.getMetadata(path).duration;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mFrameCount = 10; // TODO
        mFrameWidth = mFrameHeight = wm.getDefaultDisplay().getWidth() / mFrameCount;
        initFrameList();
        getVideoThumbs(mContext, Uri.parse(path), mFrameCount, 0, durationMs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    private class OnViewScrollListener implements ObservableHorizontalScrollView.OnScrollListener {
        @Override
        public void onScrollChanged(ObservableHorizontalScrollView scrollView, final int x, int y, int oldX, int oldY, boolean dragScroll) {
        }
    }

    private void getVideoThumbs(final Context context, final Uri videoUri, int totalThumbsCount, long startPosition, long endPosition) {
        UMediaUtil.getVideoThumb(videoUri, totalThumbsCount, startPosition, endPosition, mFrameWidth, mFrameHeight,
                (bitmap, integer) -> {
                    if (bitmap != null) {
                        UiThreadExecutor.runTask("", () -> mFrameListAdapter.addBitmaps((Bitmap) bitmap), 0L);
                    }
                });
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.thumbnail);
        }
    }

    private class FrameListAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<Bitmap> mBitmaps = new ArrayList<>();

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View contactView = inflater.inflate(R.layout.item_devide_frame, parent, false);
            ItemViewHolder viewHolder = new ItemViewHolder(contactView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            LayoutParams params = new LayoutParams(mFrameWidth, mFrameHeight);
            params.width = mFrameWidth;
            holder.mImageView.setLayoutParams(params);
            if (mBitmaps.size() > position) {
                holder.mImageView.setImageBitmap(mBitmaps.get(position));
            }
        }

        @Override
        public int getItemCount() {
            return mFrameCount;
        }

        public void addBitmaps(Bitmap bitmap) {
            mBitmaps.add(bitmap);
            notifyDataSetChanged();
        }
    }

}
